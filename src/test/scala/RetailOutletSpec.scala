
import akka.actor.{Actor, ActorSystem, Props}
import org.scalatest.{BeforeAndAfterAll, Matchers, MustMatchers, WordSpecLike}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestActors, TestKit}


class RetailOutletSpec extends TestKit(ActorSystem("Book")) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers {

  override protected def afterAll(): Unit = {
    system.terminate()
  }


    "A Purchase Request Handler Actor " must {
      "Not Allow User To Purchase More Than 1 Item At A Time " in {
        val ref = system.actorOf(Props(classOf[PurchaseRequestHandler], testActor))
        ref tell((2,Customer("Anmol", "Delhi", "1234567890", "9999950386")), testActor)

        expectMsgPF() {
          case errorMsg: String =>
            errorMsg must be("Sry you cannot book more than one...")
        }


      }

      "Allow User To Go Through Validation If 1 Item Is Required By The User" in {
        val ref = system.actorOf(Props(classOf[PurchaseRequestHandler], testActor))
        ref tell((1,Customer("Anmol", "Delhi", "1234567890", "9999950386")), testActor)

        expectMsgPF() {
          case (itemCount: Int,person: Customer) =>
            (person: Customer, itemCount: Int) must be(1,Customer("Anmol", "Delhi", "1234567890", "9999950386"))
        }
      }
    }

//  "A ValidationActor" must {
//    "Deny Booking if there is no item left in store " in {
//      val ref = system.actorOf(Props(classOf[ValidationActor], testActor))
//      ref tell((10,Customer("Anmol", "Delhi", "1234567890", "9999950386l")), testActor)
//
//      expectMsgPF() {
//        case errorMsg: String =>
//          errorMsg must be("Sorry Out of stock....!!")
//      }
//    }

//    "Respond when there is enough item to sell in store " in {
//      val ref = system.actorOf(Props(classOf[ValidationActor], testActor))
//      ref tell((1,Customer("Anmol", "Delhi", "1234567890", "9999950386")), testActor)
//
//      expectMsgPF() {
//        case person: Customer =>
//          person must be(Customer("Anmol", "Delhi", "1234567890", "9999950386"))
//      }
//    }
//  }

}


