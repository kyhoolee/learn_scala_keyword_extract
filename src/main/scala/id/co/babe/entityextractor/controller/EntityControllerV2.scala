package id.co.babe.entityextractor.controller

import com.github.xiaodongw.swagger.finatra.SwaggerSupport
import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.util.Future
import com.wix.accord.Descriptions.Explicit
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageRequest
import id.co.babe.entityextractor.service.{EntityExtractorServiceV2}
import com.wix.accord._
import com.wix.accord.dsl._
import io.swagger.models.Swagger

/**
  * Created by tungpd on 5/8/17.
  */

//object EntitySwagger extends Swagger

class EntityControllerV2 @Inject()(entityExtractorService: EntityExtractorServiceV2)
  extends Controller with SwaggerSupport {
  private val apiVersion: String = "v2"

  override protected implicit val swagger: Swagger = EntitySwagger

  /*postWithDoc("/v2/entity/extract"){doc =>
    doc.summary("Extract Entity by body")
      .tag("Entity Extractor")
      .bodyParam[EntityExtractorRequest]("body", "article content",
        Option(new EntityExtractorRequest(body = "Article Content")))
      .produces("application/json")
  }*/
  post("/%s/entity/extract".format(apiVersion)) {request: EntityMessageRequest =>
    validate(request) match {
      case Success => {
        entityExtractorService.extractEntity(request.body)
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

  /*postWithDoc("/v2/entity/extract/:aid") {doc =>
    doc.summary("Extract Entity by Article ID")
      .tag("Entity Extractor")
      .routeParam[Long]("aid", "article id")
      .produces("application/json")
  }*/
  post("/%s/entity/extract/:aid".format(apiVersion)) { request: Request =>
    guardAid(request)(aid => entityExtractorService.extractEntityById(aid))
  }

  private def guardAid(req: Request)(f: (Long) => Future[Any]): Future[Any] = {
    val articleId = Option(req.getLongParam("aid"))

    articleId match {
      case Some(aid) if (aid > 0) => f(aid)
      case _ => response.badRequest("aid param is expected!").toFuture
    }
  }

  implicit val extractEntityValidator = validator[EntityMessageRequest] {
    ent => ent.body is notEmpty
  }

}
