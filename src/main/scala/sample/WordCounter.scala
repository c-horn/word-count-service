package sample

import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString

import scala.concurrent.Future

trait WordCounter extends BasicAkkaIngredients {

  private val splitter = """[\s\p{Punct}]+""".r

  private val emptyWithDefaults = Map[String, Int]().withDefaultValue(0)

  private def tokenize(raw: ByteString): Array[String] =
    splitter.split(raw.utf8String)

  private def increment(counts: Map[String, Int], token: String): Map[String, Int]=
    counts.updated(token, counts(token) + 1)

  /**
    * Count the words in a ByteString source stream
    * @param source source of words
    * @return future of word counts
    */
  def countWords(source: Source[ByteString, _]): Future[Map[String, Int]] = {
    // I used runFold over fold here because I dislike exposing Source[T, _]
    //  when we specifically know the result is not a sequence, like in the case of an aggregate
    //  the only drawback being that this trait is now aware of the stream materializer
    source.via {
      // re-frame the incoming stream around whitespace boundaries because the source may cut a word in half
      Framing.delimiter(ByteString(" "), maximumFrameLength = 1024, allowTruncation = true)
    } .runFold(emptyWithDefaults) {
      case (segmentCounts, segment) => tokenize(segment).foldLeft(segmentCounts)(increment)
    }
  }

}
