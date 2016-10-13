package id.co.babe.entityextractor.repository

import javax.inject.Singleton

import com.google.inject.Inject
import com.twitter.util.Future
import id.co.babe.entityextractor.annotation.BabeNlpDbContext
import id.co.babe.entityextractor.dao.DbpediaResourceCategory
import id.co.babe.entityextractor.module.ContextModule.DbContext

/**
  * Created by aditya on 04/10/16.
  */
@Singleton
class DbpediaResourceCategoryRepository @Inject() (@BabeNlpDbContext ctx: DbContext) {

	import ctx._

	private val dbpedias = quote {
		query[DbpediaResourceCategory].schema(
			_.columns(
				_.resource -> "resource_dbp",
				_.category -> "category_dbp")
		)
	}

	def findByResources(resources: List[String]) = {
		val q = quote {
			dbpedias.filter(d => liftQuery(resources).contains(d.resource))
		}

		run(q)
	}
}
