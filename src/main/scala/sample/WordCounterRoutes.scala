package sample

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait WordCounterRoutes extends WordCounter with SprayJsonSupport with DefaultJsonProtocol {

  val routes: Route =
    (post & path("count-words")) {
      withoutSizeLimit {
        extractDataBytes {
          byteStream => complete(countWords(byteStream))
        }
      }
    }

}
