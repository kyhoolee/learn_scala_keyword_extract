package id.co.babe.entityextractor.controller

import com.twitter.finatra.http.Controller
import com.wix.accord.Descriptions.Explicit
import com.wix.accord.{Descriptions, Violation}

/**
  * Created by mainspring on 09/06/17.
  */
class BaseController extends Controller {
  def handleValidationError(violations: Set[Violation]) = {
    val vio = violations.head
    vio.description match {
      case Explicit(desc) => response.badRequest(s"${desc}")
      case desc => response.badRequest(s"${Descriptions.render(desc)} ${vio.constraint}")
    }
  }

}
