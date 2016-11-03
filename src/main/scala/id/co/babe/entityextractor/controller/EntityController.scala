package id.co.babe.entityextractor.controller

import com.github.xiaodongw.swagger.finatra.SwaggerSupport
import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.util.Future
import com.wix.accord.Descriptions.Explicit
import com.wix.accord._
import com.wix.accord.dsl._
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageRequest
import id.co.babe.entityextractor.domain.swagger.EntityExtractorRequest
import id.co.babe.entityextractor.service.EntityExtractorService
import io.swagger.models.Swagger

/**
  * Created by aditya on 30/09/16.
  */
object EntitySwagger extends Swagger
class EntityController @Inject() (extractorService: EntityExtractorService) extends Controller with SwaggerSupport{
	override protected implicit val swagger: Swagger = EntitySwagger

	postWithDoc("/v1/entity/extract"){doc =>
		doc.summary("Extract Entity by Body")
		    .tag("Entity Extractor")
		    .bodyParam[EntityExtractorRequest]("body", "article content", Option(new EntityExtractorRequest(body = "Article Content")))
			.produces("application/json")
	}{ request: EntityMessageRequest =>
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

	postWithDoc("/v1/entity/extract/:aid") {doc =>
		doc.summary("Extract Entity by Article ID")
			.tag("Entity Extractor")
			.routeParam[Long]("aid", "article id")
			.produces("application/json")
	}{ request: Request =>
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
