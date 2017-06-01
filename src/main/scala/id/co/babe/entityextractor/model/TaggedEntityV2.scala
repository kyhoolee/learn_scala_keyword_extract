package id.co.babe.entityextractor.model

/**
  * Created by tungpd on 5/8/17.
  */
case class TaggedEntityV2(name: String, occFreq: Int, score: Double, entityType: Byte) {

  def this(name: String) {
    this(name, 0, 0.0, 0)

  }

  def this(name:String, freq:Double, entityType: Byte) {
    this(name, 0, freq, entityType)
  }

  def this(name: String, freq: Double) {
    this(name, 0, freq, 0)
  }

  def this(name: String, count:Int, freq: Double) {
    this(name, count, freq, 0)
  }

 /* def addAlias(word: String, freq: Double) = {
    aliases += word
    occFreq += freq
    val wordParts: Array[String] = word.split("\\s+")
    var i: Int = 0
    while (i < wordParts.length) {
      {
        aliasCombination +=wordParts(i).trim.toLowerCase()
        var j: Int = i + 1
        while (j < wordParts.length) {
          {
            aliasCombination += wordParts(i).trim().toLowerCase + " " + wordParts(j).trim.toLowerCase
          }
          {
            j += 1; j - 1
          }
        }
      }
      {
        i += 1; i - 1
      }

    }

  }

  def isAnAlias(word: String): Boolean = aliases.contains(word.trim)
  def isACombinationOfAliases(word: String): Boolean = aliasCombination.contains(word.trim.toLowerCase())
*/
}
