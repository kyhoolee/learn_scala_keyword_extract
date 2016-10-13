package id.co.babe.entityextractor.repository

import javax.inject.Singleton

import com.google.inject.Inject
import com.twitter.util.Future
import id.co.babe.entityextractor.annotation.BabeDbContext
import id.co.babe.entityextractor.dao.Article
import id.co.babe.entityextractor.module.ContextModule.DbContext

/**
  * Created by aditya on 30/09/16.
  */
@Singleton
class ArticleRepository @Inject() (@BabeDbContext ctx: DbContext) {

	import ctx._

	private val articles = quote {
		query[Article].schema(
			_.entity("sasha_article"))
	}

	def findById(id: Long): Future[Option[Article]] = {
		val q = quote {
			articles.filter(_.id == lift(id)).take(1)
		}

		ctx.run(q).map(_.headOption)
	}
}
