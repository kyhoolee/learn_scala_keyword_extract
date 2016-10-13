package id.co.babe.entityextractor.util

import scala.reflect.runtime.universe._

/**
  * Created by amura on 10/3/16.
  */
object ReflectionSugars {

	private lazy val universeMirror = runtimeMirror(getClass.getClassLoader)

	def companionOf[RT](t: Type) = {
		val companionMirror = universeMirror.reflectModule(t.typeSymbol.companion.asModule)
		companionMirror.instance.asInstanceOf[RT]
	}

	def companionOf[T : TypeTag, RT] : RT =
		companionOf(typeOf(implicitly[TypeTag[T]]))

	def innerTypeCompanionOf[T : TypeTag, RT] : RT = {
		val t = implicitly[TypeTag[T]].tpe.typeArgs.head
		companionOf(t)
	}
}
