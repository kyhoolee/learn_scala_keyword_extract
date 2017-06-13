package id.co.babe.entityextractor.data

import id.co.babe.analysis.data.SolrClient
import id.co.babe.analysis.nlp.DictUtils
import id.co.babe.spelling.service.RedisPool

/**
  * Created by mainspring on 12/06/17.
  */
object SolrClientScala {
  def test(args:Array[String]): Unit = {
    if (args.length < 3) {
      println("Error arguments")
      return
    }
    val redis_host = args(0)
    val redis_port = Integer.parseInt(args(1))
    val redis_index = Integer.parseInt(args(2))

    DictUtils.storage = DictUtils.storage_redis//storage_redis
    RedisPool.initRedis(redis_host, redis_port, redis_index)


    SolrClient.allEntity()


  }

}
