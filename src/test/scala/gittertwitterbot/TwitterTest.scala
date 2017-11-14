package gittertwitterbot

import org.scalatest.FunSpec
import scala.util.Random

class TwitterTest extends FunSpec {
  describe("Twitter") {
    it("resizeTweet") {
      (0 to 2000).foreach { n =>
        val str = List.fill(n) {
          if (Random.nextBoolean()) {
            Random.nextPrintableChar()
          } else {
            Random.nextInt.toChar
          }
        }.mkString

        val result = Twitter.resizeTweet(str)
        val max = 280
        assert(result.length <= max)
        val x = result.count(Twitter.lightweightChar)
        val y = result.length - x
        assert(x + (y * 2) <= max)
        assert(str.startsWith(result))
      }
    }
  }
}
