package gittertwitterbot

import com.typesafe.scalalogging.LazyLogging

case class GistPostResponse(html_url: String)

class Gist(token: String) extends LazyLogging {

  def post(text: String): String = {
    import scalaj.http.Http
    import scalaj.http.HttpOptions

    import org.json4s._
    import org.json4s.native.JsonMethods._

    implicit val formats = DefaultFormats

    val file: Map[String, String] = Map("content" -> text)
    val files: Map[String, Map[String, String]] = Map("1.md" -> file)
    val payload: Map[String, Map[String, Map[String, String]]] = Map("files" -> files)

    val data = compact(render(Extraction.decompose(payload)))

    val body = Http
      .postData("https://api.github.com/gists", data.getBytes)
      .header("Authorization", s"token $token")
      .option(HttpOptions.connTimeout(10 * 1000))
      .option(HttpOptions.readTimeout(10 * 1000))
      .asString

    logger.info(body)

    parse(body).extract[GistPostResponse].html_url
  }

}
