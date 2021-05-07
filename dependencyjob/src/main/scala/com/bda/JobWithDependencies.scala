package com.bda

import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import org.apache.spark.eventhubs.{ConnectionStringBuilder, EventHubsConf, EventPosition}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, SparkSession}

object JobWithDependencies extends App {
  val spark = SparkSession.builder().getOrCreate()
  val endpoint = dbutils.secrets.get(scope = "databricks", key = "tweetEventhubEndpoint")
  val eventHub = "konradtweettopic"
  val consumerGroup = "group1"
  val connectionString = ConnectionStringBuilder(endpoint)
    .setEventHubName(eventHub)
    .build

  // Eventhub configuration
  val ehConf = EventHubsConf(connectionString)
    .setStartingPosition(EventPosition.fromStartOfStream)
    .setConsumerGroup(consumerGroup)
    .setMaxEventsPerTrigger(500)

  val ehStreamDF = spark.readStream
    .format("eventhubs")
    .options(ehConf.toMap)
    .load
    .select(col("body") cast "string", col("partition"), col("enqueuedTime"))

  import org.apache.spark.sql.functions._
  import org.apache.spark.sql.types._

  val dataSchema = new StructType()
    .add("created_at", StringType)
    .add("id", StringType)
    .add("text", StringType)
    .add("author_id", StringType)

  val includesSchema = new StructType()
    .add(
      "users",
      new ArrayType(
        new StructType()
          .add("username", StringType)
          .add("id", StringType)
          .add("name", StringType)
          .add("created_at", StringType),
        true
      )
    )

  val matchingRuleSchema = new ArrayType(
    new StructType()
      .add("id", StringType)
      .add("tag", StringType),
    true
  )

  val changedStreamDF = ehStreamDF
    .select(
      from_json(get_json_object(col("body").cast(StringType), "$.data"), dataSchema) as "data",
      from_json(get_json_object(col("body").cast(StringType), "$.includes"), includesSchema) as "includes",
      from_json(
        get_json_object(col("body").cast(StringType), "$.matching_rules"),
        matchingRuleSchema
      ) as "matching_rules",
      col("enqueuedTime")
    )
    .withColumn("processedTime", current_timestamp())
    .withColumn("id", col("data.id"))

  val userDF =
    changedStreamDF
      .select(explode(col("includes.users")) as "user")
      .select(col("user.id"), col("user.username"), col("user.name"), col("user.created_at"))

  val printUserQuery =
    userDF.writeStream
      .format("console")
      .queryName("insertUser")
      //.option("checkpointLocation", "/mnt/tweeter/checkpointInsertUser")
      .foreachBatch((df: DataFrame, batchId: Long) => {
        print("We are inside the BATCH: " + batchId)
        df.show()
      })
      .start()

  printUserQuery.awaitTermination()
}
