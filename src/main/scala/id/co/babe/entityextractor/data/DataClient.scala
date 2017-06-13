package id.co.babe.entityextractor.data

import id.co.babe.spelling.service.HttpSpellApp

/**
  * Created by mainspring on 12/06/17.
  */
object DataClient {
  def test(args:Array[String]): Unit = {
    if (args.length < 2) {
      println("Error arguments")
      return
    }
    val address = args(0)
    val redirect = args(1)
    HttpSpellApp.getInstance().redirectInit(address, redirect)
  }

}
