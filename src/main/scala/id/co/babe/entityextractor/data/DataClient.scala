package id.co.babe.entityextractor.data

import id.co.babe.spelling.service.HttpSpellApp

/**
  * Created by mainspring on 12/06/17.
  */
object DataClient {
  def test(args:Array[String]): Unit = {
    if (args.length < 5) {
      println("Error arguments")
      return
    }
    val address = args(0)
    val normal = args(1)
    val stop = args(2)
    val entity1 = args(3)
    val entity2 = args(4)
    HttpSpellApp.getInstance().dataInit(address, normal, stop, entity1, entity2)


  }

}
