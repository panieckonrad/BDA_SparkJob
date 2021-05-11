package com.bda

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}

import java.io.InputStream
import java.util.Properties

object ProducerCreator {
  def getProducer: KafkaProducer[String, String] = {
    val props = buildProperties("config")
    new KafkaProducer[String, String](props)
  }

  def buildProperties(configFileName: String): Properties = {
    val properties: Properties = new Properties
    val configFileStream: InputStream = getClass.getClassLoader.getResourceAsStream(configFileName)
    try {
      properties.load(configFileStream)
    } finally {
      configFileStream.close()
    }

    val bootstrapServer = scala.util.Properties.envOrNone("BOOTSTRAP_SERVER")
    val connectionString = scala.util.Properties.envOrNone("CONNECTION_STRING")
    val connPrefix = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$ConnectionString\""

    if (bootstrapServer.isDefined && connectionString.isDefined) {
      val securityConfig = s"""$connPrefix password="${connectionString.get}";"""
      properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer.get)
      properties.put("sasl.jaas.config", securityConfig)
    }

    properties
  }
}
