package sample

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success}

object ThroughputTest {

  def main(args: Array[String]): Unit = {
    println("Press any key to start..")
    StdIn.readLine()

    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext = system.dispatcher

    val requestCount = 100000
    val server = new WebService {}
    val connectionPool = Http().cachedHostConnectionPool[(Int, Long)]("localhost", 8080)
    val sample = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/words.txt")).getLines.toSeq
    val source = Source(Stream.continually(sample).flatten).take(requestCount)
    val requestNumbers = Stream.iterate(0)(_ + 1).iterator
    val requests = source.map {
      document => HttpRequest(HttpMethods.POST, "/count-words", entity = HttpEntity(document)) -> (requestNumbers.next -> System.nanoTime)
    } via {
      connectionPool
    }
    val startTime = System.nanoTime
    val operation = requests.map {
      case (Success(response), (requestNumber, requestTime)) =>
        if (requestNumber % 10000 == 0) println(s"completed request number $requestNumber")
        System.nanoTime - requestTime
      case (Failure(e), (requestNumber, requestTime)) =>
        println(s"failure of request $requestNumber, $e")
        System.nanoTime - requestTime
    } runWith {
      Sink.seq
    } map {
      _.sum / 1e6 / requestCount
    }

    val averageRuntimeMs = Await.result(operation, Duration.Inf)
    val stopTime = System.nanoTime
    val runTimeInMs = (stopTime - startTime) / 1e6

    println(s"$requestCount requests in $runTimeInMs ms, or ${requestCount / runTimeInMs * 1e3} requests per second, with an average latency of $averageRuntimeMs ms")

    server.handler.flatMap(_.unbind()).andThen {
      println("Press any key to exit..")
      StdIn.readLine()
      sys.exit
    }
  }

}
