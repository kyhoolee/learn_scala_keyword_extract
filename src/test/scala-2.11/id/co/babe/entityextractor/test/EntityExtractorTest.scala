package id.co.babe.entityextractor.test

import com.google.common.net.MediaType
import com.trueaccord.scalapb.json.JsonFormat
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.HttpHeaders
import com.twitter.inject.server.FeatureTest
import id.co.babe.entityextractor.domain.message.EntityMessage.{EntityMessageRequest, EntityMessageResponse, EntityMessageResponseV2}
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponse.Entity
import id.co.babe.entityextractor.domain.message.EntityMessage.EntityMessageResponseV2.EntityV2
import id.co.babe.entityextractor.http.AdvancedEmbeddedHttpServer
import id.co.babe.entityextractor.server.ApiServer

import scala.io.Source

/**
  * Created by MG-PC033 on 11/3/2016.
  */
class EntityExtractorTest extends FeatureTest{
	val serviceVersion = "v2"

	override val server = new AdvancedEmbeddedHttpServer(new ApiServer())

	val parsedJsonString = "<img src=\"[BASEURL]/26a777b9173cdc738848c86800e012cb.jpeg\" alt=\"Jadi Plt Gubernur DKI, Rekam Jejak Sumarsono Dianggap Baik\" data:w=\"635\" data:h=\"407\">  Soni Sumarsono. Foto: MTVN\\/Dheri Agriesta    \n<p><strong>Metrotvnews.com, Jakarta:</strong> Menteri Dalam Negeri Tjahjo Kumolo melantik Direktur Jenderal bidang Otonomi Daerah Kemendagri Soni Sumarsono sebagai Pelaksana Tugas (Plt) Gubernur DKI Jakarta. Tjahjo menyebut Sumarsono merupakan sosok berpengalaman dan rekam jejaknya baik.</p> \n<p>\"Eselon I, pangkat tertinggi IV E, sudah punya pengalaman di bawah. Kemudian track record (rekam jejak) baik selama ini,\" kata Tjahjo usai pelantikan Sumarsono di kantor Kementerian Dalam Negeri, Jalan Medan Merdeka Utara, Jakarta Pusat, Rabu (26/10/2016).</p> Tjahjo meminta Sumarsono menjaga kekompakan seluruh elemen pimpinan di Pemerintah Provinsi DKI Jakarta. Hal yang sama juga disampaikan kepada Plt Gubernur Banten Nata Irawan. Kata dia, Jakarta menjadi barometer terhadap pelaksanaan pilkada 2017. \n<p>Bahkan, proses pencalonan kepala daerah di Jakarta sudah berjalan sejak tahun lalu. Tjahjo pun meminta Sumarsono memfasilitasi pelaksanaan pilkada. \"Saya pesankan khusus, jaga kekompakan,\" kata Tjahjo.</p> \n<p>Tjahjo juga meminta Sumarsono melibatkan Kepolisian, TNI, unsur intelijen, dan tokoh masyarakat saat mengunjungi masyarakat.</p> \n<p>Sumarsono dan Nata menjadi plt gubernur karena Basuki Tjahaja Purnama dan Rano Karno cuti dari jabatan Gubernur DKI dan Gubernur Banten untuk kampanye Pilkada 2017. Pelaksana tugas gubernur akan menjabat<br>hingga 11 Februari 2017.</p> \n<p><strong>Klik: <a href=\"http://news.metrotvnews.com/metro/wkBq0qDb-pesan-ahok-kepada-plt-gubernur-dki\" rel=\"nofollow\">Pesan Ahok kepada Plt Gubernur DKI</a></strong></p> \n<p>Sumarsono punya cara sendiri untuk menyukseskan Pilkada DKI. Ia akan membuat program forum pilkada damai. Forum itu akan menjadi wadah pimpinan daerah, tim sukses tiga pasangan calon, KPU, dan Bawaslu.</p> \n<p>\"Setiap ada perbedaan pendapat bisa selesai di forum itu,\" kata dia.</p> \n<p>Sumarsono mengharapkan dukungan dari Kapolda Metro Jaya Irjen M. Iriawan dalam menjalankan tugas memimpin Ibu Kota. Ia juga akan menemui tiga pasangan calon yang bertarung di Pilkada DKI, Agus Harimurti-Sylviana Murni, Basuki Tjahaja Purnama-Djarot Saiful Hidayat, dan Anies Baswedan-Sandiaga Uno.</p> \n<p>(TRK)</p>"
	val parsedJsonString1 = "test"
	val errorMessageEmpty = "body must not be empty"
	val errorAidExpected = "aid param is expected!"
	val errorJsonArticleIdNotFound = "{\"errors\":[\"Article ID #1 not found!\"]}"
	val errorUnexpectedCharacter = "{\"errors\":[\"Unexpected character ('[' (code 91)): was expecting comma to separate Object entries\"]}"
	//val errorUnexpectedCharacter = "{\"errors\":[\"Unexpected character ('[' (code 91)): was expecting comma to separate OBJECT entries\"]}"
	val entityMatch = EntityV2("Soni Sumarsono", 6.864773242635835, Option(0))
	val entityUnmatch = EntityV2("Ko Naing", -1.0, Option(0))
	val unparsedJsonString = {
		val fullpath = getClass.getResourceAsStream("/unparsed-content.txt")
		val source = Source.fromInputStream(fullpath)
		val lines = try source.mkString finally source.close()

		lines
	}

	"Entity Extractor Test" should {

		s"return ${errorMessageEmpty}" in {
			val res = server.httpPost(
				path = "/%s/entity/extract".format(serviceVersion),
				postBody = "{}",
				headers = Map(
					HttpHeaders.ContentType -> MediaType.JSON_UTF_8.toString,
					HttpHeaders.Accept -> MediaType.JSON_UTF_8.toString
				),
				andExpect = Status.BadRequest
			)

			val message = res.getContentString()
			message should equal(errorMessageEmpty)
		}

		s"return 404 not found" in {
			val res = server.httpPost(
				path = "/%s/entity/extract/".format(serviceVersion),
				postBody = "{}",
				headers = Map(
					HttpHeaders.ContentType -> MediaType.JSON_UTF_8.toString,
					HttpHeaders.Accept -> MediaType.JSON_UTF_8.toString
				),
				andExpect = Status.NotFound
			)
		}

		s"return ${errorAidExpected}" in {
			val res = server.httpPost(
				path = "/%s/entity/extract/abcde".format(serviceVersion),
				postBody = "{}",
				headers = Map(
					HttpHeaders.ContentType -> MediaType.JSON_UTF_8.toString,
					HttpHeaders.Accept -> MediaType.JSON_UTF_8.toString
				),
				andExpect = Status.BadRequest
			)

			val message = res.getContentString()
			message should equal(errorAidExpected)
		}

		s"return article not found" in {
			val res = server.httpPost(
				path = "/%s/entity/extract/1".format(serviceVersion),
				postBody = "{}",
				headers = Map(
					HttpHeaders.ContentType -> MediaType.JSON_UTF_8.toString,
					HttpHeaders.Accept -> MediaType.JSON_UTF_8.toString
				),
				andExpect = Status.NotFound
			)

			val message = res.getContentString()

			message should equal(errorJsonArticleIdNotFound)
		}

		s"return match entity" in {
			val res = server.httpPost(
				path = "/%s/entity/extract/9000594".format(serviceVersion),
				postBody = "{}",
				headers = Map(
					HttpHeaders.ContentType -> MediaType.JSON_UTF_8.toString,
					HttpHeaders.Accept -> MediaType.JSON_UTF_8.toString
				),
				andExpect = Status.Ok
			)

			val message = res.getContentString()
			val inMsg = JsonFormat.fromJsonString[EntityMessageResponseV2](message)

			inMsg.matches(0).name should equal(entityMatch.name)
			(scala.math.abs(inMsg.matches(0).occFreq - entityMatch.occFreq) <= 0.01) should equal(true)
			inMsg.matches(0).entityType.get should equal(entityMatch.entityType.get)
		}

		s"return unmatch entity" in {
			val res = server.httpPost(
				path = "/%s/entity/extract/8934045".format(serviceVersion),
				postBody = "{}",
				headers = Map(
					HttpHeaders.ContentType -> MediaType.JSON_UTF_8.toString,
					HttpHeaders.Accept -> MediaType.JSON_UTF_8.toString
				),
				andExpect = Status.Ok
			)

			val message = res.getContentString()
			val inMsg = JsonFormat.fromJsonString[EntityMessageResponseV2](message)

			inMsg.unmatches.size should equal(22)
			inMsg.matches.size should equal(3)
			inMsg.unmatches(0).name should equal(entityUnmatch.name)
			(scala.math.abs(inMsg.unmatches(0).occFreq - entityUnmatch.occFreq) <= 0.01) should equal(true)
			inMsg.unmatches(0).entityType.get should equal(entityUnmatch.entityType.get)
		}


		s"return ${errorUnexpectedCharacter}" in {
			val res = server.httpPost(
				path = "/%s/entity/extract".format(serviceVersion),
				postBody = unparsedJsonString,
				headers = Map(
					HttpHeaders.ContentType -> MediaType.JSON_UTF_8.toString,
					HttpHeaders.Accept -> MediaType.JSON_UTF_8.toString
				),
				andExpect = Status.BadRequest
			)

			val message = res.getContentString()

			message should equal(errorUnexpectedCharacter)
		}


		s"return matches entity by body" in {
			val body = EntityMessageRequest(parsedJsonString)

			val res = server.httpPost(
				path = "/%s/entity/extract".format(serviceVersion),
				postBody = JsonFormat.toJsonString(body),
				headers = Map(
					HttpHeaders.ContentType -> MediaType.JSON_UTF_8.toString,
					HttpHeaders.Accept -> MediaType.JSON_UTF_8.toString
				),
				andExpect = Status.Ok
			)

			val message = res.getContentString()
			val inMsg = JsonFormat.fromJsonString[EntityMessageResponseV2](message)

			inMsg.matches(0).name should equal(entityMatch.name)

			(scala.math.abs(inMsg.matches(0).occFreq - entityMatch.occFreq) <= 0.01) should equal(true)
			inMsg.matches(0).entityType.get should equal(entityMatch.entityType.get)

		}

	}

}
