package com.bda

import com.bda.utils.SparkUtils.getSecret
import com.bda.traits.StreamingQueryProvider
import com.typesafe.config.Config
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}

class CosmosDBSWritingStreamingQueryProvider(configuration: Config) extends StreamingQueryProvider {
  val config: Config = configuration.getConfig("cosmosdb")
  val cosmosEndpoint: String = config.getString("cosmosEndpoint")
  val cosmosMasterKey: String = getSecret(config.getString("cosmosMasterKeySecretKey"))
  val cosmosDatabaseName: String = config.getString("cosmosDatabaseName")
  val cosmosContainerName: String = config.getString("cosmosContainerName")
  val checkpointLocation: String = config.getString("checkpointLocation")

  val readConfig = Map(
    "Endpoint" -> cosmosEndpoint,
    "Masterkey" -> cosmosMasterKey,
    "Database" -> cosmosDatabaseName,
    "Collection" -> cosmosContainerName,
    "checkpointLocation" -> checkpointLocation
  )

  override def provideStreamingQuery(dataFrame: DataFrame): StreamingQuery = {
    dataFrame.writeStream
      .trigger(Trigger.ProcessingTime("5 seconds"))
      .format("com.microsoft.azure.cosmosdb.spark.streaming.CosmosDBSinkProvider")
      .options(readConfig)
      .outputMode("append")
      .start()
  }
}
