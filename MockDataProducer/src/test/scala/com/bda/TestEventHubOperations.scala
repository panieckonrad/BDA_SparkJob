package com.bda

import org.apache.kafka.clients.producer.MockProducer
import org.scalatest.FunSuite

class TestEventHubOperations extends FunSuite {

  test("Verify amount of produced messages") {
    val mockProducer = new MockProducer[String, String]()
    val eventHubProducer = new EventHubProducer("forextesting")
    eventHubProducer.publishMessages(() => mockProducer)
    assert(mockProducer.history().size() == 1000)
  }

  test("Verify if exception wasn't thrown") {
    val mockProducer = new MockProducer[String, String]()
    val eventHubProducer = new EventHubProducer("forextesting")
    eventHubProducer.publishMessages(() => mockProducer)
    assert(!eventHubProducer.failed)
  }
}
