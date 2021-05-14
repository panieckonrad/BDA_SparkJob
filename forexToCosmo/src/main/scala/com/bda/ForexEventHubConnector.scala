package com.bda

import com.bda.utils.MountUtils
import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object ForexEventHubConnector extends App {
  val spark: SparkSession = SparkSession.builder().getOrCreate()
  val config = ConfigFactory.load().getConfig(validatedArguments(0))
  val mountUtils: MountUtils = new MountUtils(config)

  mountUtils.mountCheckpointContainer()

  new DataFrameIntoStreamingQueryCopier(
    spark,
    new EventHubReadDataFrameProvider(config),
    new CosmosDBSWritingStreamingQueryProvider(config)
  )
    .initiateCopy()
    .awaitTermination()

  def validatedArguments: Array[String] = {
    if (args.isEmpty) {
      Array("test")
    } else if (args.length == 1) {
      if (args(0).equalsIgnoreCase("prod") || args(0).equalsIgnoreCase("test")) {
        args
      } else {
        throw new IllegalArgumentException("Argument should be 'test' or 'prod'")
      }
    } else {
      throw new IllegalArgumentException("This application takes only 1 argument")
    }
  }
}
