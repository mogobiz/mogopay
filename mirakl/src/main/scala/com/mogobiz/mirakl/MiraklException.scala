package com.mogobiz.mirakl

import com.mogobiz.mirakl.CommonModel.MiraklError

class MiraklCreateOrderException(val error: MiraklError) extends Exception(error.message)

