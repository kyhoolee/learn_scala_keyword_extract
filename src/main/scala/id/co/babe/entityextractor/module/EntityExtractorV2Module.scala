package id.co.babe.entityextractor.module

import com.twitter.inject.{Injector, Logging, TwitterModule}
import com.typesafe.config.Config
import id.co.babe.analysis.CneAPI

/**
  * Created by tungpd on 5/8/17.
  */
object EntityExtractorV2Module extends TwitterModule  with Logging {

  override def singletonPostWarmupComplete(injector: Injector): Unit = {
    val config = injector.instance[Config]
    val idWord = config.getString("extractor.idword")
    val stopWord = config.getString("extractor.stopword")
    val tagWord = config.getString("extractor.tagword")
    val redirectWord = config.getString("extractor.redirectword")
    val sentParser = config.getString("extractor.sentparser")
    val tokenParser = config.getString("extractor.tokenparser")

    CneAPI.initDict(idWord, stopWord, tagWord,
      redirectWord, sentParser, tokenParser);

  }
}
