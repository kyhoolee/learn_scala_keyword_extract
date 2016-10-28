package id.co.babe.entityextractor.marshalling

import com.twitter.finagle.http.Request

/**
  * Created by amura on 10/26/16.
  */
trait RequestAware {
	var request: Request = _
}
