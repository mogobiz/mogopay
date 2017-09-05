/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.utils

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.Files._
import java.nio.file.Paths.get

import com.mogobiz.utils.MimeTypeTools._
import com.mortennobel.imagescaling.{AdvancedResizeOp, ResampleOp}

object ImageUtils {

  def resizeImage(file: File, format: String): Unit = {
    val src: BufferedImage = ImageIO.read(file)
    val originalWidth      = src.getWidth
    val originalHeight     = src.getHeight
    for (imageSize <- imageSizes.values) {
      val width  = imageSize.width
      val height = imageSize.height
      val out    = new File(s"${file.getAbsolutePath}.${width}x$height.$format")
      if (!out.exists()) {
        if (width == originalWidth && height == originalHeight) {
          copy(get(file.getAbsolutePath), get(out.getAbsolutePath), REPLACE_EXISTING)
        } else {
          var imgWidth   = width
          var imgHeight  = height
          var topMargin  = 0
          var leftMargin = 0
          if (originalWidth > originalHeight) {
            imgHeight = originalHeight * width / originalWidth
            topMargin = (imgWidth - imgHeight) / 2
          } else {
            imgWidth = originalWidth * height / originalHeight
            leftMargin = (imgHeight - imgWidth) / 2
          }

          val dest  = Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, imgWidth, imgHeight)
          val dest2 = Scalr.move(dest, leftMargin, topMargin, width, height, Color.WHITE)
          ImageIO.write(dest2, format, out)
        }
      }
    }
  }

  def getFile(inputFile: File, size: Option[ImageSize], create: Boolean): File = {
    size match {
      case Some(s) =>
        val format     = toFormat(detectMimeType(inputFile)).getOrElse("jpeg")
        val path       = s"${inputFile.getAbsolutePath}.${s.width}x${s.height}.$format"
        val file: File = new File(path)
        if (!file.exists() && create) {
          resizeImage(inputFile, format)
        }
        file
      case _ => inputFile
    }
  }

  val SMALL = "SMALL"

  val ICON = "ICON"

  val imageSizes = Map[String, ImageSize](ICON -> Icon, SMALL -> Small)

  trait ImageSize {
    def width: Int

    def height: Int
  }

  case object Icon extends ImageSize {
    val width  = 32
    val height = 32
  }

  case object Small extends ImageSize {
    val width  = 240
    val height = 240
  }

}
