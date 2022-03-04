package org.knoldus.openGraphs

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, FlowShape, SinkShape, SourceShape}
import akka.stream.scaladsl.{Broadcast, Concat, Flow, GraphDSL, Sink, Source}

object OpenGraphs extends App {

  implicit val system = ActorSystem("OpenGraphs")
  implicit val materializer = ActorMaterializer()


  /*
  complex source
    A composite source that concatenates 2 sources
    - emits ALL the elements from the first source
    - then ALL the elements from the second
   */

  val firstSource = Source(1 to 10)
  val secondSource = Source(42 to 1000)

  // step 1
  val sourceGraph = Source.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      // step 2: declaring components
      val concat = builder.add(Concat[Int](2))

      // step 3: tying them together
      firstSource ~> concat
      secondSource ~> concat

      // step 4
      SourceShape(concat.out)
    }
  )
  sourceGraph.to(Sink.foreach(println)).run()

  /*
  complex sink
   */

  val sink1 = Sink.foreach[Int](x => println(s"MM : $x"))
  val sink2 = Sink.foreach[Int](x => println(s"MM : $x"))

  val sinkGraph = Sink.fromGraph(
    GraphDSL.create() {
      implicit builder =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[Int](2))

        broadcast ~> sink1
        broadcast ~> sink2

        SinkShape(broadcast.in)

    }
  )
  firstSource.to(sinkGraph).run()


  /*
  complex flow
   */

  val incrementer = Flow[Int].map(_ + 1)
  val multiplier = Flow[Int].map(_ * 10)

  val flowGraph = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val incrementerShape = builder.add(incrementer)
      val mult = builder.add(multiplier)

      incrementerShape ~> mult

      FlowShape(incrementerShape.in, mult.out)

    }
  )
  firstSource.via(flowGraph).to(Sink.foreach(println)).run()
}