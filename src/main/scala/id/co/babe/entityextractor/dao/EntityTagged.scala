package id.co.babe.entityextractor.dao

/**
  * Created by aditya on 04/10/16.
  */
case class EntityTagged (
	id: Long,
	name: String,
	aliases: String,
	status: Byte,
	entityType: Int
)