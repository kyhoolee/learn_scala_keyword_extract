package id.co.babe.entityextractor.repository

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import id.co.babe.entityextractor.annotation.BabeNlpDbContext
import id.co.babe.entityextractor.dao.{Stopword, Synonym}
import id.co.babe.entityextractor.module.ContextModule.DbContext

/**
  * Created by aditya on 11/10/16.
  */
@Singleton
class SynonymRepository @Inject() (@BabeNlpDbContext ctx: DbContext) {

	import ctx._

	private val synonyms = quote {
		query[Synonym].schema(
			_.entity("tbl_synonym_keyword")
		)
	}

	def findAll(): Future[Seq[Synonym]] = {
		ctx.run(synonyms)
	}
}
