package id.co.babe.entityextractor.marshalling

import com.google.common.net.MediaType._
import com.trueaccord.scalapb.json.JsonFormat
import com.trueaccord.scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}
import com.twitter.finagle.http.{MediaType, Request}
import com.twitter.finatra.http.exceptions.BadRequestException
import com.twitter.finatra.http.marshalling.MessageBodyReader
import com.twitter.finatra.http.request.MediaRange
import id.co.babe.entityextractor.domain.http.SProtoRequestWrapper
import id.co.babe.entityextractor.util.ReflectionSugars._

import scala.reflect._

/**
  * Created by amura on 10/2/16.
  */
class SProtobufMessageBodyReader[PM <: GeneratedMessage with Message[PM]] extends MessageBodyReader[SProtoRequestWrapper[PM]]{

	def parse[M <: SProtoRequestWrapper[PM] : Manifest](request: Request): SProtoRequestWrapper[PM] = {
		val contentType = request.contentType.getOrElse(PROTOBUF.toString)
		val mediaRanges = MediaRange.parseAndSort(contentType)

		val companion = innerTypeCompanionOf[M, GeneratedMessageCompanion[PM]]

		mediaRanges.collectFirst({
			case mr if mr.accepts(PROTOBUF.toString) =>
				val msg = companion.parseFrom(request.getInputStream())

				SProtoRequestWrapper[PM](msg, request)

			case mr if mr.accepts(MediaType.Json) =>
				val msg = JsonFormat.fromJsonString[PM](request.getContentString())(companion)

				SProtoRequestWrapper[PM](msg, request)
		}).getOrElse(
			throw new BadRequestException("Invalid Content-Type")
		)
	}
}
