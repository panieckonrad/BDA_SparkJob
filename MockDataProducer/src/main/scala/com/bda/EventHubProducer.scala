package com.bda

import org.apache.kafka.clients.producer._
import ujson.Value

class EventHubProducer(val topicName: String) {
  var failed: Boolean = false

  val callback: Callback = new Callback {
    override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
      Option(exception) match {
        case Some(_) => failed = true
        case None => ()
      }
    }
  }

  def publishMessages(getProducer: () => Producer[String, String], messagesToSend: List[Value], verbose: Boolean = false): Unit = {
    val producer = getProducer()

    for (message <- messagesToSend) {
      if (verbose) {
        println(message)
      }
      val record = new ProducerRecord[String, String](topicName, message.toString)
      producer.send(record, callback)
    }
    producer.flush()
    producer.close()
  }
}
