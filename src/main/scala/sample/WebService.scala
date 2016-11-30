package sample

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContext, Future}

trait WebService extends WordCounterRoutes {

  override implicit val system: ActorSystem = ActorSystem()
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override implicit val ec: ExecutionContext = system.dispatcher

  val handler: Future[ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

}

object RunnableWebService extends App with WebService
