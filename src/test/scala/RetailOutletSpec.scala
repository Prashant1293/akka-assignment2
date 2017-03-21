/**
  * Created by prashant on 21/3/17.
  */


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
      "Validate The User Request " in {
        val ref = system.actorOf(Props(classOf[PurchaseRequestHandler], testActor))
        ref tell((2,Customer("Anmol", "Delhi", "1234567890", "9999950386")), testActor)

        expectMsgPF() {
          case errorMsg: String =>
            errorMsg must be("Sry you cannot book more than one...")
        }


      }
    }
}


//class EchoActorSpec() extends TestKit(ActorSystem("EchoActor")) with ImplicitSender
//  with WordSpecLike with Matchers with BeforeAndAfterAll {
//
//  override def afterAll {
//    TestKit.shutdownActorSystem(system)
//  }
//
//  "An Echo actor" must {
//
//    "send back messages unchanged" in {
//      val echo = system.actorOf(TestActors.echoActorProps)
//      echo ! "hello world"
//      expectMsg("hello world")
//    }
//
//  }
//}

