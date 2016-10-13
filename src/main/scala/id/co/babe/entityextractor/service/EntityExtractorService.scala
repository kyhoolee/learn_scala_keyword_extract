package id.co.babe.entityextractor.service

import com.google.inject.Inject
import com.twitter.util.{Await, Future}
import id.co.babe.entityextractor.dao.Synonym
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponse
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponse.Entity
import id.co.babe.entityextractor.model.TaggedEntity
import id.co.babe.entityextractor.repository._

import scala.collection.mutable
import scala.util.control.Breaks

/**
  * Created by aditya on 04/10/16.
  */
class EntityExtractorService @Inject() (stopwordRepository: StopwordRepository,
										entityTaggedRepository: EntityTaggedRepository,
										synonymRepository: SynonymRepository,
										dbpediaResourceCategoryRepository: DbpediaResourceCategoryRepository,
										entityGeonameRepository: EntityGeonameRepository) {

	private val tagPattern = "<[^>]+>".r
	private val bodyPattern = "[A-Z]+[a-zA-Z0-9]*( [A-Z]+[a-zA-Z0-9]*)*".r
	private val titlePattern = "\\b([a-z]+[a-z0-9]*)\\b".r
	private val redundantPattern = "( )+".r

	private val stopwordPattern = Await.result{ stopwordRepository.findAllActive().map(_.map(s => s.keyword.toLowerCase)) }
		    .distinct.mkString("(?i)\\b(", "|", ")\\b").r

	private val synonymMap = Await.result{ synonymRepository.findAll().map(_.map(s => (s.keyword.toLowerCase, s))) }.toMap[String, Synonym]

	private val geonameList = Await.result { entityGeonameRepository.findAllActive().map(_.map(g => g.keyword.toLowerCase)) }.toList

	private def getEntityCandidatesWithCapital(content: String) = {
		bodyPattern.findAllMatchIn(content).map{ entityCandidate =>
			val sanitized = redundantPattern.replaceAllIn(entityCandidate.group(0), " ")
			val cleaned = stopwordPattern.replaceAllIn(sanitized, "")

			cleaned
		}.filter{ entity =>
			entity.length > 2
		}.toList.distinct
	}

	private def getMatchesWithTaggedEntity(entityCandidates: Seq[String]) = {
		entityTaggedRepository.findByNames(entityCandidates).map{ list =>
			var retMap = mutable.HashMap.empty[String, Int]

			list.foreach { entityTagged =>
				if (!retMap.contains(entityTagged.name) || (entityTagged.entityType != 7 && retMap(entityTagged.name) == 7))
					retMap += (entityTagged.name -> entityTagged.entityType)
			}

			retMap
		}
	}

	private def breakLongPhrases(longWord: String) = {
		val ret = mutable.ListBuffer.empty[TaggedEntity]
		val wordParts = longWord.split("\\s+")

		val loop = new Breaks
		// proceed for phrases with every possible length
		var len = wordParts.length

		loop.breakable {
			while (len >= 1) {
				{
					var entitiesToCheck = mutable.HashMap.empty[String, Int]

					var i = 0
					while (i < wordParts.length && (i + len - 1) < wordParts.length) {
						{
							val entity = new StringBuilder

							var j = 0
							while (j < len && (i + j) < wordParts.length) {
								{
									if (entity.nonEmpty) entity += ' '
									entity ++= wordParts(i + j)
								}
								{
									j += 1; j - 1
								}
							}

							entitiesToCheck += (entity.toString -> i)
						}
						{
							i += 1; i - 1
						}
					}

					// check whether possible phrases exist in database
					var poses = mutable.ListBuffer.empty[Int]
					var breakaway = false
					// possible phrases exist in database,
					// determining position of matched phrases and put into poses
					getMatchesWithTaggedEntity(entitiesToCheck.keySet.toSeq).foreach { matches =>
						matches.foreach {
							entity =>
								var curPos = 0
								entitiesToCheck.foreach { eKey =>
									if (eKey._1.toLowerCase.contentEquals(entity._1.toLowerCase))
										curPos = eKey._2
									else
										curPos += 1
								}
								poses += curPos

								val taggedEntity: TaggedEntity = new TaggedEntity(entity._1)
								taggedEntity.entityType = entity._2.toByte
								ret += taggedEntity

								breakaway = true
						}
					}

					// sort poses
					poses.sorted

					// add phrases/word which is not in the database result
					var idx, lastPosesIdx, curPos = 0
					while (idx < wordParts.length && lastPosesIdx < poses.size) {
						{
							curPos = poses(lastPosesIdx)
							val buffer = StringBuilder.newBuilder
							while (idx < curPos) {
								{
									if (buffer.nonEmpty) buffer += ' '
									buffer ++= wordParts(idx)
								}
								{
									idx += 1; idx - 1
								}
							}
							if (buffer.nonEmpty) {
								val taggedEntity = new TaggedEntity(buffer.toString)
								ret += taggedEntity
							}
							idx = curPos + len
						}
						{
							lastPosesIdx += 1; lastPosesIdx - 1
						}
					}

					if (idx < wordParts.length) {
						val lastEntity = StringBuilder.newBuilder
						var i = idx
						while (i < wordParts.length) {
							{
								if (lastEntity.nonEmpty) lastEntity += ' '
								lastEntity ++= wordParts(i)

							}
							{
								i += 1; i - 1
							}
						}
						if (lastEntity.nonEmpty) {
							val taggedEntity = new TaggedEntity(lastEntity.toString)
							ret += taggedEntity
						}
					}
					if (breakaway) loop.break
				}
				{
					len -= 1; len + 1
				}
			}
		}

		Future(ret)
	}

	private def mergeSynonym(entityCandidates: mutable.Map[String, TaggedEntity]) = Future {
		var finalEntityCandidates = mutable.Map.empty[String, TaggedEntity]
		var synonymEntityCandidates = mutable.Map.empty[Long, TaggedEntity]

		entityCandidates.foreach { entityCandidate =>
			var primaryEntityId = -1l
			var isFinalEntity, isPrimaryEntity = false

			if (synonymMap.contains(entityCandidate._1.toLowerCase)) {
				val synonym = synonymMap(entityCandidate._1.toLowerCase)
				if (synonym.alternateId.exists(altId => altId > 0)) {
					primaryEntityId = synonym.alternateId.get
				} else {
					primaryEntityId = synonym.id
					isPrimaryEntity = true
				}

			} else {
				isFinalEntity = true
			}

			if (isFinalEntity) {
				finalEntityCandidates += entityCandidate._1.toLowerCase -> entityCandidate._2
			} else {
				if (!synonymEntityCandidates.contains(primaryEntityId))
					synonymEntityCandidates += primaryEntityId -> entityCandidate._2
				else {
					val prevEntity = synonymEntityCandidates(primaryEntityId)
					if (isPrimaryEntity)  {
						entityCandidate._2.addAlias(prevEntity.name, prevEntity.occFreq)
						synonymEntityCandidates += primaryEntityId -> entityCandidate._2
					} else {
						prevEntity.addAlias(entityCandidate._2.name, entityCandidate._2.occFreq)
						synonymEntityCandidates += primaryEntityId -> prevEntity
					}
				}
			}
		}

		synonymEntityCandidates.values.foreach { taggedEntity =>
			finalEntityCandidates += taggedEntity.name -> taggedEntity
		}

		finalEntityCandidates
	}

	private def getMatchesWithDbpedia(entityCandidates: List[String]) = {
		dbpediaResourceCategoryRepository.findByResources(entityCandidates).map(_.map(d => (d.resource, d.categoryType)).toMap)
	}

	private def updateEntityType(matchEntities: Map[String, Int], entityCandidates: Map[String, TaggedEntity]) = Future {
		val _temp = entityCandidates.map(m => (m._1.toLowerCase, m._2))

		matchEntities.foreach { matchEntity =>
			_temp.get(matchEntity._1.toLowerCase).foreach { taggedEntity =>
				taggedEntity.entityType = matchEntity._2.toByte
			}
		}

		entityCandidates
	}

	def extractEntities(rawContent: String) =  {
		val cleanedContent = tagPattern.replaceAllIn(rawContent, "\n")

		var entityCandidates = mutable.Map.empty[String, TaggedEntity]

		entityCandidates ++= getEntityCandidatesWithCapital(cleanedContent).map { entityCandidate =>
			val count = entityCandidate.mkString("(?i)\\b", "", "\\b").r.findAllIn(cleanedContent).length

			val taggedEntity: TaggedEntity = new TaggedEntity(entityCandidate)
			taggedEntity.occFreq = count

			(entityCandidate, taggedEntity)
		}

		entityCandidates.filter { d =>
			d._1.split("\\s+").length > 2
		}.foreach { entityCandidate =>
			breakLongPhrases(entityCandidate._1).foreach { smallerPhrases =>
				if (smallerPhrases.size > 1) {
					// exist smaller phrases, then
					smallerPhrases.foreach { taggedEntity =>
						if (entityCandidates.contains(taggedEntity.name)) {
							// if it is already exists as candidate, then increase its frequency
							entityCandidates(taggedEntity.name).addAlias(taggedEntity.name, taggedEntity.occFreq)
						} else {
							// if it does not exists yet, then add new entry as candidate
							entityCandidates += (taggedEntity.name -> taggedEntity)
						}
					}
					entityCandidates -= entityCandidate._1
				}
			}

		}

		val newEntityCandidates = for {
			//Merge synonym
			entityCandidates <- mergeSynonym(entityCandidates)
			// Step #3 get matches with dbpedia entities -> matchEntities
			matchEntities <- getMatchesWithDbpedia(entityCandidates.keySet.toList)
			// Step #4 get matches with tagged entities
			matchTaggedEntities <- getMatchesWithTaggedEntity(entityCandidates.keySet.toSeq)

			entityCandidates <- updateEntityType(matchEntities, entityCandidates.toMap)

			entityCandidates <- updateEntityType(matchTaggedEntities.toMap, entityCandidates)
		} yield entityCandidates

		newEntityCandidates.flatMap { entityCandidates =>
			// Step #7 weighten entity using its alias(es)
			val keyOfEntityCandidates = entityCandidates.keySet
			for (entity1Name <- keyOfEntityCandidates) {
				// process entity with 1 word only
				val entity1 = entityCandidates.get(entity1Name)
				if (entity1.isDefined && entity1.get.aliasCombination.size == 1) {
					// check whether there exists other entities having this alias
					// process when the entity is different
					keyOfEntityCandidates.filterNot( entity2Name => entity2Name.equalsIgnoreCase(entity1Name)).foreach { entity2Name =>
						val entity2 = entityCandidates.get(entity2Name)
						// process when entity2 more than 1 word AND entity2 is superset of entity1
						// entity2 should not be undefined entity (not exists in tagged nor dbpedia entities)
						if (entity2.isDefined && entity2.get.aliasCombination.size > 1
							&& entity2.get.entityType != 0 && entity2.get.isACombinationOfAliases(entity1Name)) {
							// update frequency of entity2
							entity2.get.occFreq = entity2.get.occFreq + entity1.get.occFreq
							// remove entity1
							entityCandidates - entity1Name
						}
					}
				}
			}

			var matches = mutable.ListBuffer.empty[Entity]
			var unmatches = mutable.ListBuffer.empty[Entity]

			// Step #8 save remaining candidates having more than 1 occurence
			entityCandidates.filterNot(ent => geonameList.contains(ent._1.toLowerCase)).foreach { entityCandidate =>
				if (entityCandidate._2.occFreq > 1)
					matches += Entity(entityCandidate._2.name, entityCandidate._2.occFreq, entityCandidate._2.entityType)
//					matches += Entity(entityCandidate._2.name, entityCandidate._2.occFreq, Option(entityCandidate._2.entityType.toLong))
				else
					unmatches += Entity(entityCandidate._2.name, entityCandidate._2.occFreq, 0)
//					unmatches += Entity(entityCandidate._2.name, entityCandidate._2.occFreq, Option(0))
			}

			Future {
				EntityMessageResponse().update(_.matches := matches, _.unmatches := unmatches)
			}
		}
	}
}

