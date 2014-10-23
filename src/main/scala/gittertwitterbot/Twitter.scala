package gittertwitterbot

import com.typesafe.scalalogging.LazyLogging

object Twitter {

  /**
   * Check a gitter message is allowed to tweet.
   *
   * {{{
   * >>> import Twitter._
   * >>> val user = GitterUser("user")
   * >>> isAllowedToTweet(GitterMessage(user, "Hello, world!"))
   * true
   *
   * >>> isAllowedToTweet(GitterMessage(user, "Hello, world! > /dev/null"))
   * false
   *
   * >>> isAllowedToTweet(GitterMessage(user, "Hello,\n world! > /dev/null"))
   * false
   * }}}
   */
  def isAllowedToTweet(gitterMessage: GitterMessage): Boolean = {
    val regex = """(?m)^.*?(>\s*/dev/null\s*)$""".r
    regex.findFirstIn(gitterMessage.text).isEmpty
  }

}

class Twitter(
    consumerKey: String,
    consumerSecret: String,
    accessToken: String,
    accessTokenSecret: String) extends LazyLogging {

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

  private def trim(n: Int)(text: String): String =
    if (text.size <= n) text else text.slice(0, n - 3) + "..."

  private def escapeAtmark(text: String): String =
    text.replace("@", ">")

  def tweet(gitterMessage: GitterMessage, gist: Gist): Unit = {
    val message = escapeAtmark(gitterMessage.fromUser.username + ":\n" + gitterMessage.text)
    logger.info(message)
    try {
      if (message.size > 140) {
        val trimmedMessage = trim(70)(message)
        val url = gist.post(message)
        twitter.updateStatus(trimmedMessage + " " + url)
      } else {
        twitter.updateStatus(message)
      }
    } catch {
      case e: TwitterException => logger.error(e.getMessage)
    }

  }
}
