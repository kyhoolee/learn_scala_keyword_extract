package id.co.babe.entityextractor.repository

import com.google.inject.{Inject, Singleton}
import com.twitter.util.Future
import id.co.babe.entityextractor.annotation.BabeNlpDbContext
import id.co.babe.entityextractor.dao.Stopword
import id.co.babe.entityextractor.module.ContextModule.DbContext

/**
  * Created by aditya on 04/10/16.
  */
@Singleton
class StopwordRepository @Inject() (@BabeNlpDbContext ctx: DbContext) {

	import ctx._

	private val stopwords = quote {
		query[Stopword].schema(
			_.entity("tbl_stopword")
			    .columns(_.status -> "n_status")
		)
	}

	def findAllActive(): Future[Seq[Stopword]] = {
		val q = quote {
			stopwords.filter(_.status == 1)
		}

		ctx.run(q)
	}
}
