package com.bda

import com.bda.utils.SparkUtils.getSecret
import com.bda.traits.ReadDataFrameProvider
import com.typesafe.config.Config
import org.apache.spark.eventhubs.{ConnectionStringBuilder, EventHubsConf, EventPosition}
import org.apache.spark.sql.{DataFrame, SparkSession}

class EventHubReadDataFrameProvider(configuration: Config) extends ReadDataFrameProvider {
  val config: Config = configuration.getConfig("eventhub")
  val endpoint: String = getSecret(config.getString("eventHubEndpointSecretKey"))
  val eventHub: String = config.getString("topic")
  val consumerGroup: String = config.getString("consumerGroup")
  val connectionString: String = ConnectionStringBuilder(endpoint)
    .setEventHubName(eventHub)
    .build

  val forexHubConf: EventHubsConf = EventHubsConf(connectionString)
    .setStartingPosition(EventPosition.fromEndOfStream)
    .setConsumerGroup(consumerGroup)
    .setMaxEventsPerTrigger(500)

  override def provideDataFrame(spark: SparkSession): DataFrame = {
    spark.readStream
      .format("eventhubs")
      .options(forexHubConf.toMap)
      .load
  }
}
