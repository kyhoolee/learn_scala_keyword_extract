package id.co.babe.entityextractor.repository

import javax.inject.Singleton

import com.google.inject.Inject
import com.twitter.util.Future
import id.co.babe.entityextractor.annotation.BabeNlpDbContext
import id.co.babe.entityextractor.dao.EntityGeoname
import id.co.babe.entityextractor.module.ContextModule.DbContext

/**
  * Created by aditya on 04/10/16.
  */
@Singleton
class EntityGeonameRepository @Inject()(@BabeNlpDbContext ctx: DbContext) {

	import ctx._

	private val geonames = quote {
		query[EntityGeoname].schema(
			_.entity("tbl_entity_geoname")
			    .columns(_.status -> "n_status")
		)
	}

	def findAllActive(): Future[Seq[EntityGeoname]] = {
		val q = quote {
			geonames.filter(_.status == 1)
		}

		ctx.run(q)
	}
}
