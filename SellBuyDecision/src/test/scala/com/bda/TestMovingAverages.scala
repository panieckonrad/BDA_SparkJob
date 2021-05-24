package com.bda

import com.bda.cosmoConnector.{LocalWriter, LocalWriterDecisions}
import com.bda.traits.ReadDataFrameProvider
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import org.apache.spark.sql.execution.streaming.{LongOffset, MemoryStream}
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.scalatest._

@Ignore
class TestMovingAverages extends FunSuite with BeforeAndAfterEach {
  var spark: SparkSession = _
  override def beforeEach() {
    spark = SparkSession
      .builder()
      .appName("testing basics")
      .master("local")
      .config("", "")
      .getOrCreate()
  }

  test("Verify that moving averages have been calculated correctly") {
    val json = JsonFilesReader.readJson("test_data.json")
    val messagesArray = new Array[String](20)
    var currentIndex = 0
    for (message <- json) {
      messagesArray(currentIndex) = message.toString()
      currentIndex += 1
    }

    val spark = this.spark
    import spark.implicits._
    implicit val sqlCtx: SQLContext = spark.sqlContext

    val messages = MemoryStream[String]
    val dataFrame = messages.toDF()

    assert(dataFrame.isStreaming, "we expect streaming data set")

    val inputProvider = new ReadDataFrameProvider {
      override def provideDataFrame(spark: SparkSession): DataFrame = dataFrame.select($"value".alias("body"))
    }

    val config = ConfigFactory
      .load()
      .withValue("userID", ConfigValueFactory.fromAnyRef("randomUser"))
      .withValue("slow.avgSpan", ConfigValueFactory.fromAnyRef("3"))
      .withValue("fast.avgSpan", ConfigValueFactory.fromAnyRef("2"))
      .withValue("instruments", ConfigValueFactory.fromAnyRef("EURUSD.FXCM"))

    val buySellDecision = new BuySellDecision(
      config,
      spark,
      inputProvider,
      new LocalWriter(config),
      new LocalWriterDecisions(config)
    )
    val offset = messages.addData(messagesArray)
    buySellDecision.initiate()
    messages.commit(offset.asInstanceOf[LongOffset])
  }
}
