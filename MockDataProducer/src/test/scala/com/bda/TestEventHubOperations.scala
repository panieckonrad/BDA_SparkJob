package com.bda

import org.apache.kafka.clients.producer.MockProducer
import org.scalatest.FunSuite

class TestEventHubOperations extends FunSuite {

  test("Verify amount of produced messages") {
    val mockProducer = new MockProducer[String, String]()
    val eventHubProducer = new EventHubProducer("forextesting")
    val messagesToSend = ForexDataReader.readJson(1000, "test_data.json")
    eventHubProducer.publishMessages(() => mockProducer, messagesToSend)
    assert(mockProducer.history().size() == 1000)
  }

  test("Verify if exception wasn't thrown") {
    val mockProducer = new MockProducer[String, String]()
    val eventHubProducer = new EventHubProducer("forextesting")
    val messagesToSend = ForexDataReader.readJson(1000, "test_data.json")
    eventHubProducer.publishMessages(() => mockProducer, messagesToSend)
    assert(!eventHubProducer.failed)
  }
}
