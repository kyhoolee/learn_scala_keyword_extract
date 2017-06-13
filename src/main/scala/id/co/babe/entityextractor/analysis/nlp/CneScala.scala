package id.co.babe.entityextractor.analysis.nlp

import com.twitter.util.Future
import id.co.babe.analysis.nlp._
import id.co.babe.analysis.model.{Entity => JEntity}

/**
  * Created by MG-PC033 on 6/12/2017.
  */
object CneScala extends CneScala

class CneScala {
	val connected  = Seq("of", "de", "des", "the", ":", "'s").distinct
	val web_suffix = Seq(".com", ".co", ".id", ".net", ".co.id").distinct

	def genGroupCan(text: String) = for {
		capitalized <- processCapitalized(text)
		combination <- processCombination(capitalized, text)
		longonly <- filterShort(combination)
		countMap <- countCan(longonly, text)
		groupMap <- groupCan(longonly, countMap)
		matched <- redirectCandidate(groupMap)
	} yield buildResult(matched, combination, groupMap)

	private def processCapitalized(text: String) = Future {
		val sents = TextParser.sentenize(text)

		sents.flatten{sent =>
			val filteredSent = FilterUtils.setenceFilter(sent)
			val words = TextParser.tokenize(filteredSent)
			var next = 0

			words.zipWithIndex.map{word =>
				if(next == word._2) {
					if(checkCapital(word._1)){
						var candidate = word._1
						val start = word._1
						var count = 1

						while (next < words.length && checkCapitalorNumeric(words(next))) {
							candidate += " " + words(next)
							next += 1
							count += 1
						}

						var check = CneRefactor.candidateFilter(candidate);
						if (word._2 == 0)
							check = check && ((count > 1) || (count == 1 && !DictUtils.checkNormal(start)));

						if (check) {
							Some(postProcess(candidate))
						} else None
					} else None
				} else None
			}
		}.filter(f => f.isDefined).map(_.get).distinct
	}

	private def checkCapital(word: String) = {
		if (word != null && word.length() > 0) {
			word.exists(f => Character.isUpperCase(f))
		} else false
	}

	private def checkCapitalorNumeric(word: String) = {
		if(connected.contains(word)) true
		else {
			if (word != null && word.length() > 0) Character.isUpperCase(word.charAt(0)) || Character.isDigit(word.charAt(0));
			else false
		}
	}

	private def postProcess(can: String) = {
		can.split(" ").filter(w => !checkWeb(w)).mkString(" ")
	}

	private def checkWeb(w: String) = {
		val word = w.toLowerCase()
		word.endsWith(web_suffix)
	}

	private def processCombination(ws: Seq[String], text: String) = Future {
		val filterText = textReplace(text)
		val combination = ws.flatten{w =>
			import scala.collection.JavaConversions._
			CneRefactor.genComb(w).toIndexedSeq
		}

		combination.map{c => (c, countComb(c,filterText))}
	}

	private def textReplace(text: String) = {
		text.replace(":", " :").replace("'s", " 's")
	}

	private def countComb(com: String, text: String) = {
		var lastIndex: Int = 0
		var count: Int = 0
		while (lastIndex != -1) {
			lastIndex = text.indexOf(com, lastIndex)
			if (lastIndex != -1) {
				count += 1
				lastIndex += com.length
			}
		}
		count
	}

	private def filterShort(candidates: Seq[(String, Int)]) = Future {
		val cans = candidates
			.map{c => (c._1, c._2, DictUtils.checkEntity(c._1))}
		    .filter(f => f._3 && (f._1.length > 2 || f._2 > 1))

		val filtered = cans.map{c =>
			val l = cans.filterNot(f => f._1.toLowerCase.contains(c._1.toLowerCase) && f._1.length > c._1.length)
			if(l.size > 0) Some(c._1)
			else None
		}
		filtered.filter(_.isDefined).map(_.get).distinct
	}

	private def countCan(longCandidates: Seq[String], text: String) = Future {
		longCandidates.map{lc =>
			(lc, countDecay(lc, text))
		}
	}

	private def countDecay(can: String, text: String) = {
		var lastIndex: Int = 0
		var count: Double = 0
		while (lastIndex != -1) {
			lastIndex = text.indexOf(can, lastIndex)
			if (lastIndex != -1) {
				count += Math.exp((text.length - lastIndex) * 0.1 / text.length)
				lastIndex += can.length
			}
		}
		count
	}

	private def groupCan(candidates: Seq[String], countSet: Seq[(String, Double)]) = Future {
		candidates.map{can =>
			val cont = countSet.filter(f => can.contains(f._1))
			val wordCount = can.split("\\s+").length
			val aSum = cont.filter(f => f._1.length == can.length).map(m => m._2 * 0.8 * wordCount).sum
			val bSum = cont.filter(f => f._1.length != can.length).map(m => m._2 * 0.2 * wordCount).sum

			(can, (aSum + bSum))
		}
	}

	private def redirectCandidate(input: Seq[(String, Double)]) = Future {
		val grouped = input.map{i => (DictUtils.checkRedirect(i._1), i)}
			.groupBy(_._1)
			.map(m => (m._1, m._2.map(_._2)))
		val redirectScore = grouped.map{g =>
			val sum = g._2.map(_._2).sum
			(g._1, sum)
		}
		val rootCandidate = grouped.map{g =>
			val opt = g._2.map(_._1).sortBy(s => s.length).headOption
			(g._1, opt.getOrElse(""))
		}
		input.map(m => (rootCandidate.getOrElse(m._1,""), redirectScore.getOrElse(m._1, 0D)))
	}

	private def buildResult(matched: Seq[(String, Double)], combCan: Seq[(String, Int)], groupMap: Seq[(String, Double)]) = {
		val sortedMatched = matched.sortBy(_._2)
		val sortedCombination = combCan.sortBy(_._2)

		val matchedEntity = getMatchedEntity(sortedMatched, sortedCombination)
		val unmatchedEntity = getUnmatchedEntity(sortedCombination, groupMap.map(_._1));

		import scala.collection.JavaConversions._
		val unmatchedEntityFiltered = unmatchedEntity.diff(,matchedEntity)

		Map(("matched", matchedEntity), ("unmatched", unmatchedEntityFiltered))
	}

	private def getMatchedEntity(cans: Seq[(String, Double)], counts: Seq[(String, Int)]) = {
		val cMap = counts.toMap
		cans.map{c => new JEntity(c._1, cMap.getOrElse(c._1, 0), c._2, 0)}
	}

	private def getUnmatchedEntity(counts: Seq[(String, Int)], matched: Seq[String]) = {
		counts.filter(f => !matched.contains(f._1)).filter(f => f._2 > 0).map{m =>
			new JEntity(m._1, m._2, 0.0, 0)
		}
	}
}
