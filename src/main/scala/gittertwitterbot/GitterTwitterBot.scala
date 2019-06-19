package gittertwitterbot

import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.{ ExecutionContext, Future }

object GitterTwitterBot extends LazyLogging with Using {

  import configs.syntax._
  import com.typesafe.config.ConfigFactory

  private val config = ConfigFactory.load()

  private val gitterRoomId = config.get[String]("gitter.roomId").value
  private val scalaBotId = "@" + config.get[String]("scalaBotId").value
  private val gitterToken = config.get[String]("gitter.token").value
  private val gistToken = config.get[String]("github.token").value
  private val twitterConsumerKey = config.get[String]("twitter.consumerKey").value
  private val twitterConsumerSecret = config.get[String]("twitter.consumerSecret").value
  private val twitterAccessToken = config.get[String]("twitter.accessToken").value
  private val twitterAccessTokenSecret = config.get[String]("twitter.accessTokenSecret").value

  private val twitter = new Twitter(
    twitterConsumerKey,
    twitterConsumerSecret,
    twitterAccessToken,
    twitterAccessTokenSecret
  )

  private val gitter = new Gitter(gitterRoomId, gitterToken)

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

    val gist = new Gist(gistToken)

    logger.info("Start gitter-twitter-bot")

    Http(url)
      .header("Accept", "application/json")
      .header("Authorization", s"Bearer $gitterToken")
      .option(HttpOptions.connTimeout(connectionTimeout))
      .option(HttpOptions.readTimeout(readTimeout)).execute { inputStream =>

        using(new InputStreamReader(inputStream)) { ir =>
          using(new BufferedReader(ir)) { br =>
            for {
              line <- Iterator.continually(br.readLine()).takeWhile(_ != null)
              gitterMessage <- parseStreamLine(line)
            } {
              if (Twitter.isAllowedToTweet(gitterMessage)) {
                twitter.tweet(gitterMessage)
              }
              if (gitterMessage.text.startsWith(scalaBotId)) {
                Future {
                  ScalaEval(gitterMessage.text.drop(scalaBotId.length)).foreach { result =>
                    gitter.postMessage("```\n" + result + "\n```")
                  }
                }(ExecutionContext.global)
              }
            }
          }
        }
      }
  }

}
