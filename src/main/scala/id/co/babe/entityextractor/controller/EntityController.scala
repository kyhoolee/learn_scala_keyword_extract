package id.co.babe.entityextractor.controller

import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.util.Future
import com.wix.accord.Descriptions.Explicit
import com.wix.accord._
import com.wix.accord.dsl._
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageRequest
import id.co.babe.entityextractor.service.EntityExtractorService

/**
  * Created by aditya on 30/09/16.
  */
class EntityController @Inject() (extractorService: EntityExtractorService) extends Controller {

	post("/v1/entity/extract") { request: EntityMessageRequest =>
		validate(request) match {
			case Success => {
				extractorService.extractEntitiesById(request.body)
			}
			case Failure(violations) => {
				val vio = violations.head
				vio.description match {
					case Explicit(desc) => response.badRequest(s"${desc}")
					case desc => response.badRequest(s"${Descriptions.render(desc)} ${vio.constraint}")
				}
			}
		}
	}

	post("/v1/entity/extract/:aid") { request: Request =>
		guardAid(request)(aid => extractorService.extractEntitiesById(aid))
	}

	private def guardAid(req: Request)(f: (Long) => Future[Any]): Future[Any] = {
		val articleId = Option(req.getLongParam("aid"))
		articleId match {
			case Some(aid) if (aid > 0) => f(aid)
			case _ => response.badRequest("aid param is expected!").toFuture
		}
	}

	implicit val extractEntityValidator = validator[EntityMessageRequest]{ ent =>
		ent.body is notEmpty
	}
}
