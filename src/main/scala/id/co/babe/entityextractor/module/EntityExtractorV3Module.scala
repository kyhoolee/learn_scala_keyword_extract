package id.co.babe.entityextractor.module

import com.twitter.inject.{Injector, Logging, TwitterModule}
import com.typesafe.config.Config
import id.co.babe.analysis.CneAPI

/**
  * Created by mainspring on 09/06/17.
  */
object EntityExtractorV3Module extends TwitterModule  with Logging {

  override def singletonPostWarmupComplete(injector: Injector): Unit = {
    val config = injector.instance[Config]
    val idWord = config.getString("extractor.idword")
    val stopWord = config.getString("extractor.stopword")
    val tagWord = config.getString("extractor.tagword")
    val tagWord2 = config.getString("extractor.tagword2")
    val redirectWord = config.getString("extractor.redirectword")
    val sentParser = config.getString("extractor.sentparser")
    val tokenParser = config.getString("extractor.tokenparser")

    var tag = Array[String](tagWord, tagWord2)

    CneAPI.initDict(idWord, stopWord, tag,
      redirectWord, sentParser, tokenParser);

  }
}
