package id.co.babe.entityextractor.service

import com.google.inject.Inject
import com.twitter.finatra.http.exceptions.NotFoundException
import com.twitter.inject.Logging
import com.twitter.util.Future
import id.co.babe.analysis.CneAPI
import id.co.babe.analysis.data.SolrClient
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponseV2
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponseV2.EntityV2
import id.co.babe.entityextractor.model.TaggedEntityV2
import id.co.babe.entityextractor.repository.ArticleRepository
import sun.misc.BASE64Decoder

import scala.collection.JavaConverters._
import scala.collection.mutable
/**
  * Created by tungpd on 5/10/17.
  */
class EntityExtractorServiceV2 @Inject()
    (articleRepository: ArticleRepository) extends Logging {

  private val tagPattern = "<[^>]+>".r
  private val matchThres = 0.0

  def extractEntity(content: String) = {
    assert(content != null)
    getMatchUnmatchEntity(content)
  }


  def getMatchUnmatchEntity(content:String): Future[EntityMessageResponseV2] = Future {
    assert(content != null)



    val entityMap = CneAPI.extractAllEntity(content)

    val matches = for {
      e <- entityMap.get("matched").asScala
    } yield EntityV2(e.name, e.occFreq, e.score, Option(e.entityType))

    val unmatches = for {
      e <- entityMap.get("unmatched").asScala
    } yield EntityV2(e.name, e.occFreq, e.score, Option(e.entityType))


    EntityMessageResponseV2().update(
      _.matches := matches.sortWith(_.score > _.score),
      _.unmatches := unmatches.sortWith(_.occFreq > _.occFreq)
    )
  }



  private def getEntityCandidates(content: String) = Future {
    val cleanedContent = tagPattern.replaceAllIn(content, " ").replaceAll("\\s+", " ").trim
    val entities = CneAPI.getFullEntity(cleanedContent).asScala
    val entityCandidates = (for {
      e <- entities
    } yield (e.name, TaggedEntityV2(e.name, e.occFreq, e.score, e.entityType.toByte))).toMap

    entityCandidates
  }
  private def divideMatchUnmatch(entityCandidates: Map[String, TaggedEntityV2]): Future[EntityMessageResponseV2] = Future {
    val matches = mutable.ListBuffer.empty[EntityV2]
    val unmatches = mutable.ListBuffer.empty[EntityV2]

    entityCandidates.foreach(entityCandidate =>
      if (entityCandidate._2.occFreq > matchThres)
        matches += EntityV2(entityCandidate._2.name, entityCandidate._2.occFreq, entityCandidate._2.score, Option(0))

      else
        unmatches +=  EntityV2(entityCandidate._2.name, -1 * entityCandidate._2.occFreq, 0, Option(0))
          //EntityV2("hihi", 0, 0.0, Option(0))
          //EntityV2(entityCandidate._2.name, -1 * entityCandidate._2.occFreq, 0.0, Option(0))
    )

    EntityMessageResponseV2().update(
      _.matches := matches.sortWith(_.score > _.score),
      _.unmatches := unmatches.sortWith(_.occFreq > _.occFreq)
    )
  }

  def extractEntityById(articleId: Long): Future[Any] = {
    articleRepository.findById(articleId) flatMap {
      art => if(art.isDefined) {
        val arr = new BASE64Decoder().decodeBuffer(art.get.body)
        val decoded: String = new String(arr, "UTF-8")
        extractEntity(SolrClient.htmlText(decoded))
      } else {
        throw NotFoundException(s"Article ID #$articleId not found!")
      }
    }
  }
}
