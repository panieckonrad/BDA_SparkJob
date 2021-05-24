package com.bda.cosmoConnector

import com.microsoft.azure.cosmosdb.spark.CosmosDBSpark
import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession

import java.util.Date

class CosmoDBWriterSellBuy(configuration: Config, configCosmoName: String, val spark: SparkSession)
    extends CosmoDBWriter(configuration, configCosmoName, spark) {
  val schema =
    Seq("Instrument", "end_date", "slow_avg", "fast_avg", "slow_window_span", "fast_window_span", "userID", "decision")

  override def save(data: Seq[Any]): Unit = data match {
    case Seq(date: Date, instr: String, avg1: (String, String), avg2: (String, String), decision: String) =>
      import spark.implicits._
      val df = Seq(
        (
          instr,
          defaultDateFormat.format(date),
          if (avg1._1.equals("slow")) avg1._2 else avg2._2,
          if (avg1._1.equals("fast")) avg1._2 else avg2._2,
          slow_window_span,
          fast_window_span,
          userID,
          decision
        )
      )
        .toDF(schema: _*)
      CosmosDBSpark.save(df, sparkCosmoConfig)
  }
}
