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

  "WordCounter" should "count words" in {
    val sample = Seq("multiple words here", "more words here", "words and words")
    val source = Source.fromIterator(() => sample.map(ByteString.apply).toIterator)

    whenReady(sut.countWords(source)) {
      _ shouldBe Map(
        "multiple" -> 1,
        "words" -> 4,
        "here" -> 2,
        "more" -> 1,
        "and" -> 1
      )
    }
  }

}
