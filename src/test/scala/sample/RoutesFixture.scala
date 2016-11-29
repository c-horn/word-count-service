package sample

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.ActorMaterializer
import org.scalatest.{FlatSpec, Matchers}
import scala.concurrent.ExecutionContext
import spray.json._

class RoutesFixture extends FlatSpec with ScalatestRouteTest with Matchers with DefaultJsonProtocol {

  val sut = new WordCounterRoutes {
    override implicit val system: ActorSystem = ActorSystem()
    override implicit val materializer: ActorMaterializer = ActorMaterializer()
    override implicit val ec: ExecutionContext = system.dispatcher
  }

  "the route '/count-words'" should " accept a text document and return a JSON object containing the count of each unique word in the document" in {
    val document = "multiple words here more words here words and words"

    Post("/count-words", document) ~> sut.routes ~> check {
      status shouldBe StatusCodes.OK
      JsonParser(responseAs[String]).convertTo[Map[String, Int]] shouldBe Map(
        "multiple" -> 1,
        "words" -> 4,
        "here" -> 2,
        "more" -> 1,
        "and" -> 1
      )
    }
  }

}
