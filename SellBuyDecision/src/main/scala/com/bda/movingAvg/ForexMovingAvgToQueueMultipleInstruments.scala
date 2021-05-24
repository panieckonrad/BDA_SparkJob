package com.bda.movingAvg

import com.typesafe.config.Config
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.{DataFrame, Row}

import scala.concurrent.forkjoin.LinkedTransferQueue

class ForexMovingAvgToQueueMultipleInstruments(
    configuration: Config,
    configAvgType: String,
    inputDF: DataFrame,
    val queue: LinkedTransferQueue[Seq[Any]]
) extends ForexMovingAvgToQueue(configuration, configAvgType, inputDF) {

  override val movingAvgDF: DataStreamWriter[Row] = inputDF
    .groupBy(col("Instrument"), window(col("AskTime"), windowSpanMinutes, "1 minute"))
    .agg(avg("Ask") as "ask_avg")
    .select("Instrument", "window.start", "window.end", "ask_avg")
    .writeStream
    .foreachBatch { (batchDF: DataFrame, _: Long) =>
      val instrumentsAsRows = batchDF.select(col("Instrument")).distinct().collect.toList
      for (row <- instrumentsAsRows) {
        val singleInstrumentDF = batchDF.filter(col("Instrument") === row.toSeq.head.toString)
        if (singleInstrumentDF.count > windowSpan.toInt) {
          queue.add(singleInstrumentDF.sort(desc("start")).take(windowSpan.toInt).last.toSeq)
        }
      }
    }
    .outputMode("complete")
}
