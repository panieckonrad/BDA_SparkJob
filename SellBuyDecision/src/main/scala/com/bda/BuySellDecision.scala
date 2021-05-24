package com.bda

import com.bda.cosmoConnector.traits.Writer
import com.bda.movingAvg.{ForexMovingAvgToQueue, MovingAvgFactory}
import com.bda.traits.ReadDataFrameProvider
import com.bda.utils.ForexUtils.forexDataSchema
import com.typesafe.config.Config
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.concurrent.forkjoin.LinkedTransferQueue

class BuySellDecision(
    val configuration: Config,
    val spark: SparkSession,
    val inputProvider: ReadDataFrameProvider,
    val cosmoAvgWriter: Writer,
    val cosmoSellBuyWriter: Writer
) {

  val instruments: Array[String] = configuration.getString("instruments").split(",")

  val inputDF: DataFrame = inputProvider
    .provideDataFrame(spark)
    .select(col("body") cast StringType)
    .select(from_json(col("body"), forexDataSchema).alias("json_body"))
    .select("json_body.*")
    .select("Instrument", "AskTime", "Ask")
    .filter(col("Instrument").isin(instruments: _*))

  val slowAvgQueue: LinkedTransferQueue[Seq[Any]] = new LinkedTransferQueue[Seq[Any]]()
  val fastAvgQueue: LinkedTransferQueue[Seq[Any]] = new LinkedTransferQueue[Seq[Any]]()

  val slowStream: ForexMovingAvgToQueue =
    MovingAvgFactory(instruments.length, configuration, "slow", inputDF, slowAvgQueue)
  val fastStream: ForexMovingAvgToQueue =
    MovingAvgFactory(instruments.length, configuration, "fast", inputDF, fastAvgQueue)

  val decisionsMaker = new AveragesQueueToDecisions(slowAvgQueue, fastAvgQueue, cosmoAvgWriter, cosmoSellBuyWriter)

  def initiate(): Unit = {
    slowStream.startStream()
    fastStream.startStream()
    decisionsMaker.initialize()
  }
}
