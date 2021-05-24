package com.bda

import com.bda.cosmoConnector.{CosmoDBWriterAvgWriter, CosmoDBWriterSellBuy}
import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import org.apache.spark.sql.SparkSession

object MakingBuySellDecision extends App {
  val spark: SparkSession = SparkSession.builder().getOrCreate()
  val config = getConfig(args)

  new BuySellDecision(
    config,
    spark,
    new EventHubReadDataFrameProvider(config),
    new CosmoDBWriterAvgWriter(config, "minuteAvg", spark),
    new CosmoDBWriterSellBuy(config, "buysell", spark)
  )
    .initiate()

  def getConfig(args: Array[String]): Config = {
    validateArguments(args)
    ConfigFactory
      .load()
      .getConfig(args(0))
      .withValue("userID", ConfigValueFactory.fromAnyRef(args(1)))
      .withValue("slow.avgSpan", ConfigValueFactory.fromAnyRef(args(2)))
      .withValue("fast.avgSpan", ConfigValueFactory.fromAnyRef(args(3)))
      .withValue("instruments", ConfigValueFactory.fromAnyRef(args(4)))
  }

  def validateArguments(args: Array[String]): Unit = {
    if (args.length != 5) {
      throw new IllegalArgumentException(
        "\nThis application takes 5 arguments:" +
          "\n\t- env:'test'/'prod'" +
          "\n\t- userName" +
          "\n\t- slowMovingAvgSpan in minutes" +
          "\n\t- fastMovingAvgSpan in minutes" +
          "\n\t- instruments e.g. 'AUDUSD.FXCM,USDCHF.FXCM'"
      )
    } else {
      if (!args(0).equalsIgnoreCase("prod") && !args(0).equalsIgnoreCase("test")) {
        throw new IllegalArgumentException("args(0) should be 'test' or 'prod'")
      }
      if (!args(2).forall(_.isDigit) && !args(3).forall(_.isDigit)) {
        throw new IllegalArgumentException("args(2) and args(3) should be integer values")
      }
    }
  }
}
