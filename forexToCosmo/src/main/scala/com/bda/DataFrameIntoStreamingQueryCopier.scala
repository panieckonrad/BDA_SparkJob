package com.bda

import com.bda.traits.{ReadDataFrameProvider, StreamingQueryProvider}
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}

class DataFrameIntoStreamingQueryCopier(
    val spark: SparkSession,
    val inputProvider: ReadDataFrameProvider,
    val outputProvider: StreamingQueryProvider
) {

  val forexColumns = Seq(
    "BidTime",
    "Last",
    "Type",
    "Bid",
    "TotalVolume",
    "High",
    "Instrument",
    "Spread",
    "TradeTime",
    "Low",
    "PercentChange",
    "NumberofTradesToday",
    "Ask",
    "Close",
    "Tick",
    "Open",
    "Change",
    "AskTime"
  )

  val dataSchema = new StructType(forexColumns.map(key => StructField(key, StringType)).toArray)

  val inputDF: DataFrame = inputProvider
    .provideDataFrame(spark)
    .select(col("body") cast StringType)
    .select(from_json(col("body"), dataSchema).alias("json_body"))
    .select("json_body.*")

  /**
   * @return cold StreamingQuery which will copy the data from source to the target.
   *         Terminate operation needs to be invoked on the returned Query, i.e. awaitTermination()
   *         to support Job processing
   */
  def initiateCopy(): StreamingQuery = {
    outputProvider.provideStreamingQuery(inputDF)
  }
}
