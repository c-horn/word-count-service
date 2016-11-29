package sample

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext

class WordCounterFixture extends FlatSpec with Matchers with ScalaFutures {

  val sut = new WordCounter {
    override implicit val system: ActorSystem = ActorSystem()
    override implicit val materializer: ActorMaterializer = ActorMaterializer()
    override implicit val ec: ExecutionContext = system.dispatcher
  }

  def source(seq: Seq[String]): Source[ByteString, _] =
    Source.fromIterator(() => seq.map(ByteString.apply).toIterator)

  "WordCounter" should "count words" in {
    val sample = Seq("multiple words here", "more words here", "words and words")

    whenReady(sut.countWords(source(sample))) {
      _ shouldBe Map(
        "multiple" -> 1,
        "words" -> 4,
        "here" -> 2,
        "more" -> 1,
        "and" -> 1
      )
    }
  }

  "WordCouter" should "not fail to re-assemble words that span two chunks of the stream" in {
    val sample = Seq("a word can pote", "ntially span multiple chunks")

    whenReady(sut.countWords(source(sample))) {
      _ shouldBe Map(
        "a" -> 1,
        "word" -> 1,
        "could" -> 1,
        "potentially" -> 1,
        "span" -> 1,
        "multiple" -> 1,
        "chunks" -> 1
      )
    }
  }

}
