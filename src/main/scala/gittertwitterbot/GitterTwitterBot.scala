package gittertwitterbot

import com.typesafe.scalalogging.LazyLogging

object GitterTwitterBot extends LazyLogging {

  import com.github.kxbmap.configs._
  import com.typesafe.config.ConfigFactory

  private val config = ConfigFactory.load()

  private val gitterRoomId = config.get[String]("gitter.roomId")
  private val gitterToken = config.get[String]("gitter.token")
  private val twitterConsumerKey = config.get[String]("twitter.consumerKey")
  private val twitterConsumerSecret = config.get[String]("twitter.consumerSecret")
  private val twitterAccessToken = config.get[String]("twitter.accessToken")
  private val twitterAccessTokenSecret = config.get[String]("twitter.accessTokenSecret")

  private val twitter = new Twitter(
    twitterConsumerKey,
    twitterConsumerSecret,
    twitterAccessToken,
    twitterAccessTokenSecret
  )

  private def using[A, R <: { def close() }](r : R)(f : R => A) : A =
    try { f(r) } finally { r.close() }

  private def parseStreamLine(line: String): Option[GitterMessage] = {
    import org.json4s._
    import org.json4s.native.JsonMethods._
    implicit val formats = DefaultFormats
    if (line.trim.isEmpty) None else Some(parse(line).extract[GitterMessage])
  }

  def main(args: Array[String]): Unit = {
    import scalaj.http.Http
    import scalaj.http.HttpOptions
    import java.io.{ InputStreamReader, BufferedReader }

    val url = s"https://stream.gitter.im/v1/rooms/${gitterRoomId}/chatMessages"
    val connectionTimeout = 60 * 1000
    val readTimeout = 60 * 1000

    Http(url)
      .header("Accept", "application/json")
      .header("Authorization", s"Bearer $gitterToken")
      .option(HttpOptions.connTimeout(connectionTimeout))
      .option(HttpOptions.readTimeout(readTimeout)) { inputStream =>

      using(new InputStreamReader(inputStream)) { ir =>
        using (new BufferedReader(ir)) { br =>
          for {
            line <- Iterator.continually(br.readLine()).takeWhile(_ != null)
            gitterMessage <- parseStreamLine(line)
          } {
            twitter.tweet(gitterMessage)
          }
        }
      }
    }
  }

}
