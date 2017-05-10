package id.co.babe.entityextractor.model

/**
  * Created by aditya on 05/10/16.
  */
case class TaggedEntity(name: String, var aliases: Set[String],
												var aliasCombination: Set[String], var occFreq: Int,
												var entityType: Byte) {

	def this(name: String) {
		this(name, Set.empty[String], Set.empty[String], 0, 0)
		addAlias(name, 1)
	}

	def addAlias(word: String, freq: Int) = {
		aliases += word
		occFreq += freq

		val wordParts: Array[String] = word.split("\\s+")
		var i: Int = 0
		while (i < wordParts.length) {
			{
				aliasCombination += wordParts(i).trim.toLowerCase
				var j: Int = i + 1
				while (j < wordParts.length) {
					{
						aliasCombination += wordParts(i).trim.toLowerCase + " " + wordParts(j).trim.toLowerCase
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

	def isACombinationOfAliases(word: String): Boolean = aliasCombination.contains(word.trim.toLowerCase)
}




