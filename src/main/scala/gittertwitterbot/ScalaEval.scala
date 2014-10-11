package gittertwitterbot

import com.typesafe.scalalogging.StrictLogging
import scala.util.control.NonFatal
import scalaj.http.{ HttpOptions, Http }

object ScalaEval extends StrictLogging {

  def apply(code: String): Option[String] = {
    val request = Http
      .postData("http://scala-eval.herokuapp.com", code.getBytes("UTF-8"))
      .header("Content-type", "text/plain; charset=UTF-8")
      .option(HttpOptions.connTimeout(30 * 1000))
      .option(HttpOptions.readTimeout(30 * 1000))
    try {
      Option(request.asString)
    } catch {
      case NonFatal(e) =>
        logger.warn("scala-eval error", e)
        None
    }
  }

}
