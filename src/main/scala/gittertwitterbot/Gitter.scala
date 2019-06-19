package gittertwitterbot

import com.typesafe.scalalogging.StrictLogging

final class Gitter(gitterRoomId: String, gitterToken: String) extends StrictLogging {

  def postMessage(text: String): Unit = {
    import org.json4s._
    import org.json4s.native.JsonMethods._
    import scalaj.http.{ Http, HttpOptions }
    implicit val formats = DefaultFormats

    logger.info(text)

    val data = Extraction.decompose(Map("text" -> text))

    val response = Http(s"https://api.gitter.im/v1/rooms/$gitterRoomId/chatMessages")
      .postData(compact(render(data)).getBytes)
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .header("Authorization", s"Bearer $gitterToken")
      .option(HttpOptions.connTimeout(10 * 1000))
      .asString

    val responseCode = response.code
    val headersMap = response.headers
    val resultString = response.body

    if (responseCode != 200) {
      headersMap.foreach { header =>
        logger.info(header.toString())
      }
      logger.error(resultString)
    }
  }

}
