package id.co.babe.entityextractor.dao

import java.util.Date

/**
  * Created by aditya on 30/09/16.
  */
case class Stopword (
	id : Long,
	keyword: String,
	status: Byte,
	created: Date
)