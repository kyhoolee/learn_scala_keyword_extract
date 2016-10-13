package id.co.babe.entityextractor.dao

/**
  * Created by aditya on 11/10/16.
  */
case class Synonym (
	id: Long,
	keyword: String,
	alternateId: Option[Long]
)
