package com.bda

object Main extends App {
  val topicName = "forextesting"
  val producer = new EventHubProducer(topicName)
  producer.publishMessages(() => ProducerCreator.getProducer)
}
