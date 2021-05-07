package com.bda

import org.apache.spark.sql.{Row, SparkSession}

object HelloWorldSpark {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().getOrCreate()

    val count = countStringsLongerThan3Chars(spark, Seq("word", "another_word")).head.getLong(0)

    println(count) // this is visible on the driver!
  }

  def countStringsLongerThan3Chars(spark: SparkSession, input: Seq[String]): List[Row] = {
    import org.apache.spark.sql.functions._
    import spark.implicits._
    input
      .toDF("word")
      .select("word")
      .map(_.getString(0))
      .map(_.length)
      .filter($"value" > 3)
      .agg(count($"value"))
      .collect()
      .toList
  }

}
