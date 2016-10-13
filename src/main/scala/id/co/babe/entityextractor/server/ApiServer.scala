package id.co.babe.entityextractor.server

import com.google.inject.Module
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, ExceptionMappingFilter, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import id.co.babe.entityextractor.controller.EntityController
import id.co.babe.entityextractor.domain.http.SProtoRequestWrapper
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageRequest
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponse
import id.co.babe.entityextractor.marshalling.{JProtobufMessageBodyWriter, SProtobufMessageBodyReader, SProtobufMessageBodyWriter}
import id.co.babe.entityextractor.module.{ContextModule, TypesafeConfigModule}

/**
  * Created by aditya on 30/09/16.
  */
object ApiServerMain extends ApiServer

class ApiServer extends HttpServer {

	override protected def defaultFinatraHttpPort: String = ":8080"

	override protected def failfastOnFlagsNotParsed: Boolean = true

	override protected def modules: Seq[Module] = Seq(TypesafeConfigModule, ContextModule)

	override protected def configureHttp(router: HttpRouter): Unit = {
		router
			.register[SProtobufMessageBodyReader[EntityMessageRequest], SProtoRequestWrapper[EntityMessageRequest]]

			.register[SProtobufMessageBodyWriter, EntityMessageResponse]
//			.register[JProtobufMessageBodyWriter, EntityMessageResponse]

		    .filter[LoggingMDCFilter[Request, Response]]
			.filter[TraceIdMDCFilter[Request, Response]]
			.filter[CommonFilters]
			.filter[ExceptionMappingFilter[Request]]

		    .add[EntityController]
	}
}
