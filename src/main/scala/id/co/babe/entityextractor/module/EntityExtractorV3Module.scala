package id.co.babe.entityextractor.module

import com.twitter.inject.{Injector, Logging, TwitterModule}
import com.typesafe.config.Config
import id.co.babe.analysis.CneAPI
import id.co.babe.analysis.nlp.DictUtils

/**
  * Created by mainspring on 09/06/17.
  */
object EntityExtractorV3Module extends TwitterModule  with Logging {

  override def singletonPostWarmupComplete(injector: Injector): Unit = {
    val config = injector.instance[Config]

    val redis_host = config.getString("extractor.redis_host")
    val redis_port = config.getInt("extractor.redis_port")
    val redis_index = config.getInt("extractor.redis_index")
    val service_url = config.getString("extractor.service_url")
    val dict_type = config.getString("extractor.dict_type")

    val sentParser = config.getString("extractor.sentparser")
    val tokenParser = config.getString("extractor.tokenparser")

    if(dict_type.equalsIgnoreCase("service")) {
      DictUtils.initService(service_url)
    } else {
      DictUtils.initRedis(redis_host, redis_port, redis_index)
    }
    CneAPI.initDict(sentParser, tokenParser);

  }
}
