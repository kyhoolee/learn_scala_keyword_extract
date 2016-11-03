package id.co.babe.entityextractor.http

import java.nio.ByteBuffer

import com.google.common.net.{MediaType, HttpHeaders => CommonHttpHeaders}
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.{PortUtils, Ports}
import com.twitter.io.Buf

/**
  * Created by amura on 9/10/16.
  */
class AdvancedEmbeddedHttpServer(twitterServer: Ports)
  extends EmbeddedHttpServer(twitterServer) {

    def httpBinPost(
      path: String,
      postBody: ByteBuffer,
      accept: MediaType = null,
      suppress: Boolean = false,
      contentType: String = MediaType.PROTOBUF.toString,
      headers: Map[String, String] = Map(),
      andExpect: Status = null,
      withLocation: String = null,
      routeToAdminServer: Boolean = false,
      secure: Option[Boolean] = None): Response = {

      val request = createApiRequest(path, Method.Post)
      request.content = Buf.ByteBuffer.Owned(postBody)
      request.headerMap.set(CommonHttpHeaders.CONTENT_LENGTH, request.content.length.toString)
      request.headerMap.set(CommonHttpHeaders.CONTENT_TYPE, contentType)

      binaryHttpExecute(request, addAcceptHeader(accept, headers), suppress, andExpect, withLocation, routeToAdminServer, secure = secure.getOrElse(false))
    }

    private def binaryHttpExecute(
      request: Request,
      headers: Map[String, String] = Map(),
      suppress: Boolean = false,
      andExpect: Status = Status.Ok,
      withLocation: String = null,
      routeToAdminServer: Boolean = false,
      secure: Boolean): Response = {

      val (client, port) = chooseHttpClient(request.path, routeToAdminServer, secure)
      request.headerMap.set("Host", PortUtils.loopbackAddressForPort(port))

      httpExecute(client, request, headers, suppress, andExpect, withLocation, null)
    }

    private def chooseHttpClient(path: String, forceAdmin: Boolean, secure: Boolean) = {
      if (path.startsWith("/admin") || forceAdmin)
        (httpAdminClient, httpAdminPort)
      else if (secure)
        (httpsClient, twitterServer.httpsExternalPort.get)
      else
        (httpClient, twitterServer.httpExternalPort.get)
    }

    private def addAcceptHeader(
       accept: MediaType,
       headers: Map[String, String]): Map[String, String] = {
      if (accept != null)
        headers + (CommonHttpHeaders.ACCEPT -> accept.toString)
      else
        headers
    }
  }
