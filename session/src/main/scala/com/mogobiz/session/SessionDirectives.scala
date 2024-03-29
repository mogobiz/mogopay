/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.session

import java.io._
import java.util.{Calendar, Date}

import com.mogobiz.es.EsClient
import com.mogobiz.json.BinaryConverter
import com.mogobiz.session.config.Settings
import com.typesafe.scalalogging.StrictLogging
import shapeless._
import spray.http.HttpHeaders.Cookie
import spray.http.{DateTime, HttpCookie}
import spray.routing._

import scala.collection.mutable.Map
import scala.util.control.NonFatal

case object MissingSessionCookieRejection extends Rejection

trait SessionDirectives extends StrictLogging { backend: Backend =>

  import spray.routing.directives.BasicDirectives._
  import spray.routing.directives.CookieDirectives._
  import spray.routing.directives.HeaderDirectives._

  protected def cookieSession(): Directive1[Session] = headerValue {
    case Cookie(cookies) =>
      val xx = cookies
        .find(_.name == Settings.Session.CookieName)
        .map { cookie =>
          logger.debug(cookie.name + "=" + cookie.content)
          s"$Settings.Session.CookieName Found"
        }
        .getOrElse(s"$Settings.Session.CookieName NotFound")
      logger.debug(xx)

      cookies.find(_.name == Settings.Session.CookieName) map { cookie =>
        sessionFromCookie(cookie)
      }
    case _ => None
  }

  def session: Directive[Session :: HNil] = {
    cookieSession() | provide {
      val session = Session()
      backend.store(session)
      session
    }
  }

  def optionalSession: Directive[Option[Session] :: HNil] = {
    cookieSession().hmap(_.map(shapeless.option)) | provide(None)
  }

  def setSession(session: Session): Directive0 = {
    setCookie(Session(session.data))
  }

  def killSession(session: Session, domain: String = "", path: String = ""): Directive0 = {
    backend.delete(session.id)
    deleteCookie(Settings.Session.CookieName, domain, path)
  }

  implicit def sessionFromCookie(cookie: HttpCookie): Session =
    Session(backend.load(cookie.content).map(_.data).getOrElse(Map.empty[String, Any]),
            cookie.expires,
            cookie.maxAge,
            cookie.domain,
            cookie.path,
            cookie.secure,
            cookie.httpOnly,
            cookie.extension)

  implicit def sessionToCookie(session: Session): HttpCookie = {
    val res = HttpCookie(Settings.Session.CookieName,
                         backend.store(session),
                         session.expires,
                         session.maxAge,
                         session.domain,
                         session.path,
                         session.secure,
                         session.httpOnly,
                         session.extension)
    res
  }
}

trait Backend {
  def store(data: Session): String

  def delete(data: String): Unit

  def load(data: String): Option[Session]
}

trait CookieBackend extends Backend {
  def store(session: Session): String = {
    val encoded = java.net.URLEncoder
      .encode(session.data.filterNot(_._1.contains(":")).map(d => d._1 + ":" + d._2).mkString("\u0000"), "UTF-8")
    Crypto.sign(encoded, Settings.Session.CookieSecret) + "-" + encoded
  }

  def delete(uuid: String): Unit = {
    // do nothing
  }

  def load(data: String): Option[Session] = {
    def urldecode(data: String) =
      Map[String, Any](
          java.net.URLDecoder
            .decode(data, "UTF-8")
            .split("\u0000")
            .map(_.split(":"))
            .map(p => p(0) -> p.drop(1).mkString(":")): _*)
    // Do not change this unless you understand the security issues behind timing attacks.
    // This method intentionally runs in constant time if the two strings have the same length.
    // If it didn't, it would be vulnerable to a timing attack.
    def safeEquals(a: String, b: String) = {
      if (a.length != b.length) false
      else {
        var equal = 0
        for (i <- Array.range(0, a.length)) {
          equal |= a(i) ^ b(i)
        }
        equal == 0
      }
    }

    try {
      val splitted = data.split("-")
      val message  = splitted.tail.mkString("-")
      if (safeEquals(splitted(0), Crypto.sign(message, Settings.Session.CookieSecret)))
        Some(Session(data = urldecode(message)))
      else
        None
    } catch {
      // fail gracefully is the session cookie is corrupted
      case scala.util.control.NonFatal(_) => None
    }
  }

}

trait FileBackend extends Backend {
  private def filename(bucket: String, key: String) = s"$bucket-$key"

  private val converter = new BinaryConverter[Session.Data] {}

  def store(session: Session): String = {
    val uuid        = session(Settings.Session.CookieName).asInstanceOf[String]
    val raw         = converter.fromDomain(session)
    val sessionFile = new File(Settings.Session.Folder, filename("session", uuid))
    val out         = new FileOutputStream(sessionFile)
    val buffer      = new BufferedOutputStream(out)
    val output      = new ObjectOutputStream(buffer)
    try {
      output.writeObject(raw)
    } finally {
      output.close()
    }
    uuid
  }

  def delete(uuid: String): Unit = {
    val sessionFile = new File(Settings.Session.Folder, filename("session", uuid))
    sessionFile.delete()
  }

  def load(uuid: String): Option[Session] = {
    val sessionFile = new FileInputStream(new File(Settings.Session.Folder, filename("session", uuid)))
    try {
      val buffer = new BufferedInputStream(sessionFile)
      val input  = new ObjectInputStream(buffer)
      val raw    = input.readObject().asInstanceOf[Array[Byte]]
      Some(converter.toDomain(raw))
    } catch {
      case NonFatal(e) =>
        e.printStackTrace()
        None
    } finally {
      sessionFile.close()
    }
  }
}

case class ESSession(uuid: String,
                     data: Array[Byte],
                     expires: Option[DateTime],
                     maxAge: Option[Long],
                     domain: Option[String],
                     path: Option[String],
                     secure: Boolean,
                     httpOnly: Boolean,
                     extension: Option[String],
                     _ttl: String,
                     var dateCreated: Date = Calendar.getInstance().getTime,
                     var lastUpdated: Date = Calendar.getInstance().getTime)

trait ESBackend extends Backend {

  import org.json4s._
  import org.json4s.jackson.Serialization

  implicit val formats = Serialization.formats(NoTypeHints)

  // execution context for futures

  //  def queryRoot(): Future[HttpResponse] = pipeline(Get(route("/")))
  //  override def receive: Actor.Receive = execute

  private val ES_URL        = "http://localhost"
  private val ES_HTTP_PORT  = 9200
  private val SESSION_INDEX = "sessions"

  private val ES_FULL_URL = ES_URL + ":" + ES_HTTP_PORT

  private def route(url: String): String = ES_FULL_URL + url

  private def filename(bucket: String, key: String) = s"$bucket-$key"

  private val converter = new BinaryConverter[Session] {}

  def store(session: Session): String = {
    val raw = converter.fromDomain(session)
    val esSession = ESSession(session(Settings.Session.CookieName).asInstanceOf[String],
                              data = raw,
                              expires = session.expires,
                              maxAge = session.maxAge,
                              domain = session.domain,
                              path = session.path,
                              secure = session.secure,
                              httpOnly = session.httpOnly,
                              extension = session.extension,
                              _ttl = s"${Settings.Session.MaxAge}s")
    EsClient.index(Settings.Session.EsIndex, esSession, false)
  }

  def delete(uuid: String): Unit = {
    EsClient.delete[ESSession](Settings.Session.EsIndex, uuid, false)
  }

  def load(uuid: String): Option[Session] = {
    EsClient
      .load[ESSession](Settings.Session.EsIndex, uuid)
      .map(esSession => converter.toDomain[Session](esSession.data))
  }
}

object SessionCookieDirectives extends SessionDirectives with CookieBackend

object SessionFileDirectives extends SessionDirectives with FileBackend

object SessionESDirectives extends SessionDirectives with ESBackend
