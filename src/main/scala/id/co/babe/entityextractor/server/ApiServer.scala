package id.co.babe.entityextractor.server

import com.github.xiaodongw.swagger.finatra.{SwaggerController, WebjarsController}
import com.google.inject.Module
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, ExceptionMappingFilter, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import id.co.babe.entityextractor.controller.{EntityController, EntityControllerV2, EntitySwagger}
import id.co.babe.entityextractor.domain.message.EntityMessage.{EntityMessageRequest, EntityMessageResponse, EntityMessageResponseV2}
import id.co.babe.entityextractor.marshalling.{SProtobufMessageBodyReader, SProtobufMessageBodyWriter}
import id.co.babe.entityextractor.module.{ContextModule, EntityExtractorV2Module, GraphiteMetricsModule, TypesafeConfigModule}
import io.swagger.models.Info

/**
  * Created by aditya on 30/09/16.
  */
object ApiServerMain extends ApiServer

class ApiServer extends HttpServer {

	override protected def defaultFinatraHttpPort: String = ":9000"

	override protected def failfastOnFlagsNotParsed: Boolean = true

	override protected def modules: Seq[Module] = Seq(
		TypesafeConfigModule,
		ContextModule,
		GraphiteMetricsModule,
		EntityExtractorV2Module
	)

	override protected def configureHttp(router: HttpRouter): Unit = {
		router
			.register[SProtobufMessageBodyReader[EntityMessageRequest]]

			.register[SProtobufMessageBodyWriter, EntityMessageResponseV2]
			.register[SProtobufMessageBodyWriter, EntityMessageResponse]

		    .filter[LoggingMDCFilter[Request, Response]]
			.filter[TraceIdMDCFilter[Request, Response]]
			.filter[CommonFilters]
			.filter[ExceptionMappingFilter[Request]]

		    .add[EntityControllerV2]
			.add[EntityController]

			.add[WebjarsController]
			.add(new SwaggerController(swagger = EntitySwagger))
	}

	val info = new Info()
		.description("Entity Extractor API")
		.version("2.1")
		.title("Entity Extractor API")

	EntitySwagger.info(info)
}
