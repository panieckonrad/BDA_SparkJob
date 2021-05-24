package com.bda.movingAvg

import com.typesafe.config.Config
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.{DataFrame, Row}

import scala.concurrent.forkjoin.LinkedTransferQueue

abstract class ForexMovingAvgToQueue(
    val configuration: Config,
    val configAvgType: String,
    val inputDF: DataFrame
) {
  val config: Config = configuration.getConfig(configAvgType)
  val windowSpan: String = config.getString("avgSpan")
  val windowSpanMinutes: String = if (windowSpan.equals("1")) "1 minute" else windowSpan + " minutes"

  val movingAvgDF: DataStreamWriter[Row] = inputDF.writeStream

  def startStream(): Unit = {
    movingAvgDF.start()
  }
}

object MovingAvgFactory {
  def apply(
      numberOfInstruments: Int,
      config: Config,
      configAvgType: String,
      inputDF: DataFrame,
      queue: LinkedTransferQueue[Seq[Any]]
  ): ForexMovingAvgToQueue = numberOfInstruments match {
    case 1 => new ForexMovingAvgToQueueSingleInstrument(config, configAvgType, inputDF, queue)
    case _ => new ForexMovingAvgToQueueMultipleInstruments(config, configAvgType, inputDF, queue)
  }
}
