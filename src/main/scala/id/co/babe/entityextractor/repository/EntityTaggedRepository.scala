package id.co.babe.entityextractor.repository

import javax.inject.Singleton

import com.google.inject.Inject
import com.twitter.util.Future
import id.co.babe.entityextractor.annotation.BabeNlpDbContext
import id.co.babe.entityextractor.dao.EntityTagged
import id.co.babe.entityextractor.module.ContextModule.DbContext

/**
  * Created by aditya on 04/10/16.
  */
@Singleton
class EntityTaggedRepository @Inject() (@BabeNlpDbContext ctx: DbContext) {

	import ctx._

	private val taggeds = quote {
		query[EntityTagged].schema(
			_.entity("tbl_entity_tagged")
			    .columns(
					_.name -> "entity_name",
					_.status -> "n_status"
				)
		)
	}

	def findByNames(names: Seq[String]): Future[Seq[EntityTagged]] = {
		val q = quote {
			taggeds.filter(t => liftQuery(names).contains(t.name))
		}

		ctx.run(q)
	}
}
