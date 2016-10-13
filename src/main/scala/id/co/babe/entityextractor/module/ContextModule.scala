package id.co.babe.entityextractor.module

import javax.inject.Singleton

import com.google.inject.Provides
import com.twitter.inject.TwitterModule
import com.typesafe.config.Config
import id.co.babe.entityextractor.annotation.{BabeDbContext, BabeNlpDbContext}
import io.getquill.{FinagleMysqlContext, SnakeCase}

/**
  * Created by aditya on 30/09/16.
  */
object ContextModule extends TwitterModule {

	type DbContext = FinagleMysqlContext[SnakeCase]

	@Provides @Singleton @BabeDbContext
	def provideBabeDbContext(conf: Config): DbContext = new FinagleMysqlContext[SnakeCase](conf.getConfig("ctx.babe"))

	@Provides @Singleton @BabeNlpDbContext
	def provideBabeNlpDbContext(conf: Config): DbContext = new FinagleMysqlContext[SnakeCase](conf.getConfig("ctx.babeNlp"))
}
