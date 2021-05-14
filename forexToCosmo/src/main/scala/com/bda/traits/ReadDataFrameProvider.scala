package com.bda.traits

import org.apache.spark.sql.{DataFrame, SparkSession}

trait ReadDataFrameProvider {
  def provideDataFrame(spark: SparkSession): DataFrame
}
