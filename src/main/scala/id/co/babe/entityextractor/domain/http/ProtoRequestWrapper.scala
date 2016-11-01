package id.co.babe.entityextractor.domain.http

import com.google.protobuf.Message
import com.trueaccord.scalapb.GeneratedMessage
import com.twitter.finagle.http.Request

/**
  * Created by amura on 9/15/16.
  */
case class JProtoRequestWrapper[T <: Message](wrapper: T, request: Request)

//case class SProtoRequestWrapper[T <: GeneratedMessage](wrapper: T, request: Request)
