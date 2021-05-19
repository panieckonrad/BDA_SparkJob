package com.bda

import scala.util.Try

object Main extends App {
  val topicName = "forextesting"
  val producer = new EventHubProducer(topicName)

  val hasValidAmountParam = args.length >= 1 && Try(args(0).toInt).isSuccess
  val amountOfMessages = if (hasValidAmountParam) args(0).toInt else 1000

  val messagesToSend = ForexDataReader.readJson(amountOfMessages, args(1))

  producer.publishMessages(() => ProducerCreator.getProducer, messagesToSend)
}
