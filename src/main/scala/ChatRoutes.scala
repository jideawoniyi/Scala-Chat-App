import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Flow, Sink, Source, Keep}
import akka.stream.OverflowStrategy
import scala.concurrent.Future
import scala.io.StdIn

object ChatRoutes {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val executionContext = system.dispatcher

    val route =
      path("chat") {
        handleWebSocketMessages(chatFlow)
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  def chatFlow: Flow[Message, Message, Any] = {
    val incomingMessages: Sink[Message, Future[akka.Done]] = Flow[Message].map {
      case TextMessage.Strict(text) =>
        println(s"Received message: $text")
      case _ =>
    }.toMat(Sink.ignore)(Keep.right)

    val outgoingMessages: Source[Message, Any] = Source
      .repeat(TextMessage("Server says hello!"))
      .throttle(1, scala.concurrent.duration.DurationInt(1).second)

    Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
  }
}

