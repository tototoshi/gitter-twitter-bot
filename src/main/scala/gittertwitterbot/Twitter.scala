package gittertwitterbot

import com.typesafe.scalalogging.LazyLogging

class Twitter (
  consumerKey: String,
  consumerSecret: String,
  accessToken: String,
  accessTokenSecret: String
) extends LazyLogging {

  import twitter4j._
  import twitter4j.conf._
  private val twitter4jConfBuilder = new ConfigurationBuilder
  private val twitter4jConf = twitter4jConfBuilder
    .setOAuthConsumerKey(consumerKey)
    .setOAuthConsumerSecret(consumerSecret)
    .setOAuthAccessToken(accessToken)
    .setOAuthAccessTokenSecret(accessTokenSecret)
    .build
  private val twitter = new TwitterFactory(twitter4jConf).getInstance()

  private def trim140(text: String): String =
    if (text.size <= 140) text else text.slice(0, 137) + "..."

  private def escapeAtmark(text: String): String =
    text.replace("@", ">")

  def tweet(gitterMessage: GitterMessage): Unit = {
    val message = gitterMessage.fromUser.username + ": " + gitterMessage.text
    logger.info(message)
    val trimmedMessage = trim140(message)
    try {
      twitter.updateStatus(escapeAtmark(trimmedMessage))
    } catch {
      case e: TwitterException => logger.error(e.getMessage)
    }
  }
}
