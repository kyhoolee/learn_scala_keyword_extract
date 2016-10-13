package id.co.babe.entityextractor.service

import com.google.inject.Inject
import com.twitter.util.{Await, Future}
import id.co.babe.entityextractor.dao.Article
import id.co.babe.entityextractor.repository.ArticleRepository

/**
  * Created by aditya on 04/10/16.
  */
class ArticleService @Inject() (articleRepository: ArticleRepository) {

	def findById(id: Long): Option[Article] = {
		/*val article =*/ Await.result{ articleRepository.findById(id) }
		/*article*/
	}
}
