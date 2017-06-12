package id.co.babe.entityextractor.controller

import com.github.xiaodongw.swagger.finatra.SwaggerSupport
import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.util.Future
import com.wix.accord.Descriptions.Explicit
import com.wix.accord.dsl.validator
import com.wix.accord.{Descriptions, Failure, Success, validate}
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageRequest
import id.co.babe.entityextractor.service.EntityExtractorApiV3
import io.swagger.models.Swagger
import com.wix.accord.dsl._
import id.co.babe.entityextractor.domain.message.DictMessage.DictInsertResponse
import id.co.babe.entityextractor.domain.message.DictMessage.DictInsertRequest
import id.co.babe.entityextractor.domain.message.DictMessage.RedirectInsertRequest

/**
  * Created by mainspring on 09/06/17.
  */
class EntityControllerV3 @Inject()(service: EntityExtractorApiV3) extends BaseController with SwaggerSupport{
  private val apiVersion: String = "v3"

  override protected implicit val swagger: Swagger = EntitySwagger


  post("/%s/entity/extract".format(apiVersion)) {request: EntityMessageRequest =>
    validate(request) match {
      case Success => {
        service.extractEntity(request.body)
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

  post("/%s/entity/extract/:aid".format(apiVersion)) { request: Request =>
    guardAid(request)(aid => service.extractEntityById(aid))
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

  implicit val dictInsertValidator = validator[DictInsertRequest] {
    ent => ent.word is notEmpty
  }

  implicit val redirectInsertValidator = validator[RedirectInsertRequest] {
    ent => ent.word is notEmpty
  }


  post("/%s/entity/delete/redirect".format(apiVersion)){ req : RedirectInsertRequest =>
    validate(req) match {
      case Success => deleteRedirect(req)
      case Failure(violations) => handleValidationError(violations)
    }
  }


  def deleteRedirect(req:RedirectInsertRequest): Unit = {
    service.removeDictRedirect(req.word, req.redirect)
  }


  post("/%s/entity/update/redirect".format(apiVersion)){ req : RedirectInsertRequest =>
    validate(req) match {
      case Success => saveRedirect(req)
      case Failure(violations) => handleValidationError(violations)
    }
  }


  def saveRedirect(req:RedirectInsertRequest): Unit = {
    service.insertDictRedirect(req.word, req.redirect)
  }


  post("/%s/entity/delete/dict".format(apiVersion)){ req : DictInsertRequest =>
    validate(req) match {
      case Success => deleteDict(req)
      case Failure(violations) => handleValidationError(violations)
    }
  }

  def deleteDict(req:DictInsertRequest) = {
    req.dictType match {
      case "stop" =>
        service.removeDictStop(req.word.toLowerCase)
        DictInsertResponse("ok")

      case "normal" =>
        service.removeDictNormal(req.word.toLowerCase)
        DictInsertResponse("ok")

      case "entity" =>
        service.removeDictEntity(req.word.toLowerCase)
        DictInsertResponse("ok")

      case _ => DictInsertResponse("error type")

    }
  }



  post("/%s/entity/update/dict".format(apiVersion)){ req : DictInsertRequest =>
    validate(req) match {
      case Success => saveDict(req)
      case Failure(violations) => handleValidationError(violations)
    }
  }

  def saveDict(req:DictInsertRequest) = {
    req.dictType match {
      case "stop" =>
        service.insertDictStop(req.word.toLowerCase)
        DictInsertResponse("ok")

      case "normal" =>
        service.insertDictNormal(req.word.toLowerCase)
        DictInsertResponse("ok")

      case "entity" =>
        service.insertDictEntity(req.word.toLowerCase)
        DictInsertResponse("ok")

      case _ => DictInsertResponse("error type")

    }
  }





}
