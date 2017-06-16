package id.co.babe.entityextractor.service

import com.google.inject.Inject
import com.twitter.finatra.http.exceptions.NotFoundException
import com.twitter.inject.Logging
import com.twitter.util.Future
import id.co.babe.analysis.CneAPI
import id.co.babe.analysis.data.SolrClient
import id.co.babe.analysis.nlp.{CneRefactor, DictUtils}
import id.co.babe.entityextractor.analysis.nlp.CneScala
//import id.co.babe.entityextractor.analysis.nlp.CneScala
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponseV2
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponseV2.EntityV2
import id.co.babe.entityextractor.repository.ArticleRepository
import sun.misc.BASE64Decoder

import scala.collection.JavaConverters._
/**
  * Created by mainspring on 07/06/17.
  */
class EntityExtractorApiV3 @Inject()(articleRepository: ArticleRepository) extends Logging {

  def extractEntity(content: String) = {
    assert(content != null)
    getMatchUnmatchEntityScala(content)
  }

  def extractEntityById(articleId: Long): Future[Any] = {
    articleRepository.findById(articleId) flatMap {
      art => if(art.isDefined) {
        val arr = new BASE64Decoder().decodeBuffer(art.get.body)
        val decoded: String = new String(arr, "UTF-8")
//        println("\n\n---------FilterEntity----------")
//        println(decoded)
//        println("----------FilterEntity---------\n\n")
        extractEntity(SolrClient.htmlText(decoded))
      } else {
        throw NotFoundException(s"Article ID #$articleId not found!")
      }
    }
  }

  private def getMatchUnmatchEntityScala(content: String): Future[EntityMessageResponseV2] = {
    assert(content != null)
    for {
      entityMap <- CneScala.genGroupCan(content)
    } yield {
      val matches = entityMap.getOrElse("matched", Seq()).map { e =>
        EntityV2(e.name, e.occFreq, e.score, Option(e.entityType))
      }
      val unmatches = entityMap.getOrElse("unmatched", Seq()).map { e =>
        EntityV2(e.name, e.occFreq, e.score, Option(e.entityType))
      }

      EntityMessageResponseV2().update(
        _.matches := matches.sortWith(_.score > _.score),
        _.unmatches := unmatches.sortWith(_.occFreq > _.occFreq)
      )
    }
  }

  private def getMatchUnmatchEntity(content:String): Future[EntityMessageResponseV2] = Future {
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


  def setDictType(dictType:Int): Unit = {
    DictUtils.setDictType(dictType)
  }


  def insertDictStop(word:String): Unit = {
    DictUtils.insertStop(word)
  }
  def insertDictNormal(word:String): Unit = {
    DictUtils.insertNormal(word)
  }
  def insertDictEntity(word:String): Unit = {
    DictUtils.insertEntity(word)
  }
  def insertDictRedirect(word:String, redirect:String): Unit = {
    DictUtils.insertRedirect(word, redirect)
  }

  def removeDictStop(word:String): Unit = {
    DictUtils.removeStop(word)
  }
  def removeDictNormal(word:String): Unit = {
    DictUtils.removeNormal(word)
  }
  def removeDictEntity(word:String): Unit = {
    DictUtils.removeEntity(word)
  }
  def removeDictRedirect(word:String, redirect:String): Unit = {
    DictUtils.removeRedirect(word, redirect)
  }


  def checkDictStop(word:String) = {
    DictUtils.checkStop(word)
  }
  def checkDictNormal(word:String) = {
    DictUtils.checkNormal(word)
  }
  def checkDictEntity(word:String) = {
    DictUtils.checkEntity(word)
  }
  def checkDictRedirect(word:String) = {
    DictUtils.checkRedirect(word)
  }




}
