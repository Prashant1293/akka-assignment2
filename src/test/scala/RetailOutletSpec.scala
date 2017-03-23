import akka.actor.{ActorSystem, Props}
import akka.testkit._
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.Success


object RetailOutletSpec {

  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        |akka.loggers = [akka.testkit.TestEventListener]
      """.stripMargin
    )
    ActorSystem("test-system", config)
  }
}

import RetailOutletSpec._

class RetailOutletSpec extends TestKit(testSystem) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers with ImplicitSender{

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  "PurchaseRequestActor" must {
    "Invalidate the request" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestActor].withDispatcher(dispatcherId)
      val ref = system.actorOf(props)
      EventFilter.info(message = "Unknown Request", occurrences = 1).intercept {
        ref ! "Unknown obj asked"
      }
    }

    "Successfully initiate Request" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      EventFilter.info(message = "Request Initiated", occurrences = 1).intercept {
        ref ! (1,Customer("Prashant", "Delhi", "1800237976832547", "8457033478"))
      }
    }

    "Deny when user is asking for more than one item" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      EventFilter.info(message = "Sry you cannot book more than one...", occurrences = 1).intercept {
        ref ! (2,Customer("Prashant", "Delhi", "1800237976832547", "8457033478"))
      }
    }
  }

  "ValidationActor" must {

    "handle if item is out of stock" in {

      val ref = TestActorRef[ValidationActor]
      ref.underlyingActor.count = 0
      EventFilter.info(message = "Sorry Out of stock....!!", occurrences = 1).intercept {
        ref ! (Customer("Prashant", "Delhi", "1800237976832547", "8457033478"))
      }
    }

    "Validate the request" in {
      val ref = TestActorRef[ValidationActor]
      val ref2 = TestActorRef[PurchaseActor]
      ref.underlyingActor.count = 8
      implicit val timeout = Timeout(1000 seconds)
      val future=ref2 ? Customer("Prashant", "Delhi", "1800237976832547", "8457033478")
      val Success(result:String) = future.value.get
      result must be ("Ok")
    }

    "Respond to user providing Invalid Details" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[ValidationActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      EventFilter.info(message = "Invalid UserDetails", occurrences = 1).intercept {
        ref ! ""
      }
    }

  }

  "PurchaseActor" must {
    "provide with the Booking Details For User " in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      EventFilter.info(message = "Thanks for booking !!, your details are...", occurrences = 1).intercept {
        ref ! (Customer("Prashant", "Delhi", "1800237976832547", "8457033478"))
      }
    }

    "Respond to User's Invalid Details" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      EventFilter.info(message = "Wrong User Details", occurrences = 1).intercept {
        ref ! ""
      }
    }
  }

}