import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import org.apache.log4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

object RetailOutlet extends App {

  val system = ActorSystem("Book")
  val props = Props[PurchaseRequestHandler]
  val router = system.actorOf(props)
  val no_of_request = 1
  for (i <- 1 to 10)
    router ! (no_of_request, Customer("Akhil", "Delhi", "1900234576876594", "8877033455"))

}

case class Customer(cus_name: String, address: String, credit_card_no: String, mobile_no: String)

class PurchaseRequestHandler(ref: ActorRef) extends Actor with ActorLogging {

  val validate = context.actorOf(Props[ValidationActor])
  //val str="Sry you cannot book more than one..."
  override def receive = {
    case (no_of_request, user: Customer) => {
      log.info("Inside PurchaseRequestHandler Actor\n")
     // println("Request Initialized")
      if (no_of_request == 1) {

        validate forward  user
      }
      else sender() ! "Sry you cannot book more than one..."
    }
  }
}

class ValidationActor extends Actor with ActorLogging {

  var count = 8
  val config = ConfigFactory.parseString(
    """
      |akka.actor.deployment {
      | /poolRouter {
      |   router = round-robin-pool
      |   nr-of-instances = 500
      | }
      |}
    """.stripMargin
  )

  val system = ActorSystem("RouterSystem", config)
  val purchase = system.actorOf(FromConfig.props(Props[PurchaseActor]), "poolRouter")

  override def receive = {
    case user: Customer => {
      if (count > 0) {
        println("Validate")
        count -= 1
        purchase ! user
      }
      else {
        println(s"Sorry Out of stock....!!")
      }
    }
  }
}

class PurchaseActor extends Actor with ActorLogging  {

  val purchase = context.actorOf(Props[Purchase])

  override def receive = {
    case user: Customer => {
      implicit val timeout = Timeout(5000 seconds)
      val f = purchase ask user
      Await.result(f, timeout.duration)
    }
  }
}

class Purchase extends Actor with ActorLogging  {

  override def receive = {
    case user: Customer => {
      println("Thanks for booking !!, your details are...")
      println("Name=", user.cus_name)
      println("Address=", user.address)
      println("Mobile=", user.mobile_no)
    }
  }
}
