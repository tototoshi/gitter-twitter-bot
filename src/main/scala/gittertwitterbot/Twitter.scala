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

  /**
   * @see [[https://developer.twitter.com/en/docs/developer-utilities/twitter-text]]
   */
  def lightweightChar(c: Char): Boolean = {
    ((0 <= c) && (c <= 4351)) ||
      ((8192 <= c) && (c <= 8205)) ||
      ((8208 <= c) && (c <= 8223)) ||
      ((8242 <= c) && (c <= 8247))
  }

  def resizeTweet(tweet: String): String = {
    if (tweet.length <= 140) {
      tweet
    } else {
      val original = tweet.toCharArray
      val buf = new java.lang.StringBuilder()
      @annotation.tailrec
      def loop(i: Int, size: Int): Unit = {
        original.lift.apply(i) match {
          case Some(char) =>
            val nextSize = size + {
              if (lightweightChar(original(i))) {
                1
              } else {
                2
              }
            }
            if (nextSize > 280) {
              ()
            } else {
              buf.append(char)
              loop(i + 1, nextSize)
            }
          case None =>
            ()
        }
      }
      loop(0, 0)
      buf.toString
    }
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

  private def escapeAtmark(text: String): String =
    text.replace("@", ">")

  def tweet(gitterMessage: GitterMessage): Unit = {
    val message = Emoji.replace(escapeAtmark(gitterMessage.fromUser.username + ":\n" + gitterMessage.text))
    logger.info(message)
    try {
      val trimmedMessage = gittertwitterbot.Twitter.resizeTweet(message)
      twitter.updateStatus(trimmedMessage)
    } catch {
      case e: TwitterException => logger.error(e.getMessage)
    }

  }
}
