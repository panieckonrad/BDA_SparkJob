package com.bda.cosmoConnector

import com.bda.cosmoConnector.traits.Writer
import com.bda.utils.SparkUtils.getSecret
import com.microsoft.azure.cosmosdb.spark.config.{Config => SparkCosmoConfig}
import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession

import java.text.SimpleDateFormat
import java.util.Locale

abstract class CosmoDBWriter(configuration: Config, configCosmoName: String, sparkSession: SparkSession)
    extends Writer {
  val defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

  val config: Config = configuration.getConfig("cosmosdb." + configCosmoName)
  val cosmosEndpoint: String = config.getString("cosmosEndpoint")
  val cosmosMasterKey: String = getSecret(config.getString("cosmosMasterKeySecretKey"))
  val cosmosDatabaseName: String = config.getString("cosmosDatabaseName")
  val cosmosContainerName: String = config.getString("cosmosContainerName")

  val cosmoConfigMap = Map(
    "Endpoint" -> cosmosEndpoint,
    "Masterkey" -> cosmosMasterKey,
    "Database" -> cosmosDatabaseName,
    "Collection" -> cosmosContainerName
  )
  val sparkCosmoConfig: SparkCosmoConfig = SparkCosmoConfig(cosmoConfigMap)

  val slow_window_span: String = configuration.getConfig("slow").getString("avgSpan")
  val fast_window_span: String = configuration.getConfig("fast").getString("avgSpan")

  val userID: String = configuration.getString("userID")

  val schema: Seq[String]

  def save(data: Seq[Any])
}
