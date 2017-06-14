package id.co.babe.entityextractor.data

import id.co.babe.analysis.data.SolrClient
import id.co.babe.analysis.nlp.{CneRefactor, DictUtils}
import id.co.babe.spelling.service.RedisPool
import id.co.babe.analysis.model.{Entity => JEntity}
import id.co.babe.analysis.util.Utils
import id.co.babe.entityextractor.analysis.nlp.CneScala
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

  def main_test(args:Array[String]): Unit = {

    val redis_host = "localhost"
    val redis_port = 6379
    val redis_index = 0

    DictUtils.storage = DictUtils.storage_redis//storage_redis
    RedisPool.initRedis(redis_host, redis_port, redis_index)


    allEntity()


  }

  def allEntity(): Unit = {
    val as = SolrClient.getBabeArticleById(12313094)
    var start = System.currentTimeMillis
    DictUtils.init()
    var value = System.currentTimeMillis - start
    System.out.println("Processing time: " + value * 0.001)
    import scala.collection.JavaConversions._
    for (a <- as) {
      var content = SolrClient.htmlText(a.content)
      //testMatchedUnmatched(content)
      //content = "SEBAGAI SALAH SATU IDOL GROUP DI JEPANG, KONSEP APA YANG KALIAN MILIKI DAN APA YANG MEMBUAT KALIAN BERBEDA DENGAN IDOL GROUP LAINNYA?"
      //testCapitalized(content)
      testCombination(content)
    }
  }

  def testCombination(text:String): Unit = {
    var start = System.currentTimeMillis
    var capitalized:Seq[String] = Seq()
    CneScala.processCapitalized(text).onSuccess(d => capitalized = d)
    var combination:Seq[(String, Int)] = Seq()
    CneScala.processCombination(capitalized, text).onSuccess(d => combination = d)
    combination.map(pair => println(pair._1))
    var value = System.currentTimeMillis - start
    //printResult(r)
    println("Processing time: " + value * 0.001)
    println("\n\n\n\n-------------------\n\n\n")

    val jCap = CneRefactor.processCapitalized(text)
    val jCom = CneRefactor.processCombination(jCap, text)
    Utils.printCollection(jCom.keySet())
  }

  def testCapitalized(text:String): Unit = {
    var start = System.currentTimeMillis
    val r = CneScala.processCapitalized(text)
    var value = System.currentTimeMillis - start
    //printResult(r)
    r.onSuccess(data => data.map(word => println(word)))
    println("Processing time: " + value * 0.001)
    println("\n\n\n\n-------------------\n\n\n")

    val r1 = CneRefactor.processCapitalized(text)
    Utils.printCollection(r1)
  }

  def testMatchedUnmatched(text:String): Unit = {
    var start = System.currentTimeMillis
    val r = CneScala.genGroupCan(text)
    var value = System.currentTimeMillis - start
    //printResult(r)
    r.onSuccess(data => printResult(data))
    System.out.println("Processing time: " + value * 0.001)
  }

  def printResult(r: Map[String, Seq[JEntity]]) {
    import scala.collection.JavaConversions._
    for (key <- r.keySet) {
      System.out.println()
      System.out.println(key)
      System.out.println()
      printEntity(r.get(key).getOrElse(Seq()))
      System.out.println("\n\n")
    }
  }

  def printEntity(entity: Seq[JEntity]) {
    import scala.collection.JavaConversions._
    for (e <- entity) {
      printEntity(e)
    }
  }

  def printEntity(e: JEntity) {
    println(e.name + " : " + e.score + " : " + e.occFreq)
  }


}
