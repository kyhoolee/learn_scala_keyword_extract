package id.co.babe.entityextractor.controller

import com.google.common.net.MediaType
import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.exceptions.NotFoundException
import id.co.babe.entityextractor.domain.http.SProtoRequestWrapper
import id.co.babe.entityextractor.service.{ArticleService, EntityExtractorService}

/**
  * Created by aditya on 30/09/16.
  */
class EntityController @Inject() (articleService: ArticleService, extractorService: EntityExtractorService) extends Controller {

	post("/v1/entity/extract") { request: /*SProtoRequestWrapper[EntityMessageRequest]*/ Request =>
		// TODO
		response.noContent
	}

	post("/v1/entity/extract/:aid") { request: Request =>
		// TODO: get article using aid
		val art = articleService.findById(request.getLongParam("aid"))
//		art

		if (art == None)
			throw new NotFoundException(MediaType.PLAIN_TEXT_UTF_8, Seq.empty[String])

		// TODO: extract entities using extractor service
		val entities = extractorService.extractEntities(art.get.body)
//		response.noContent
		entities
	}
}
