package com.bda

import com.bda.traits.{ReadDataFrameProvider, StreamingQueryProvider}
import com.bda.utils.ForexUtils.forexDataSchema
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}

class DataFrameIntoStreamingQueryCopier(
    val spark: SparkSession,
    val inputProvider: ReadDataFrameProvider,
    val outputProvider: StreamingQueryProvider
) {

  val inputDF: DataFrame = inputProvider
    .provideDataFrame(spark)
    .select(col("body") cast StringType)
    .select(from_json(col("body"), forexDataSchema).alias("json_body"))
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
