import akka.actor.{ActorSystem, Props}
import akka.testkit._
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import com.typesafe.config.ConfigFactory


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
  with BeforeAndAfterAll with MustMatchers with ImplicitSender {

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  "PurchaseRequestActor" must {
    "Invalid request" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Unknown Request", occurrences = 1).intercept {
        ref ! "hi"
      }
    }

    "Success Request" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Request Initiated", occurrences = 1).intercept {
        ref ! (1, Customer("", "", "", ""))
      }
    }

    "Respond when user is asking for more than one item" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Sry you cannot book more than one...", occurrences = 1).intercept {
        ref ! (2, Customer("", "", "", ""))
      }
    }
  }

}