package com.bda

import com.bda.traits.{ReadDataFrameProvider, StreamingQueryProvider}
import org.apache.spark.sql.execution.streaming.{LongOffset, MemoryStream}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.scalatest._

class EventHubToCosmosStreamTest extends FunSuite with BeforeAndAfterEach {

  var spark: SparkSession = _
  override def beforeEach() {
    spark = SparkSession
      .builder()
      .appName("testing basics")
      .master("local")
      .config("", "")
      .getOrCreate()
  }

  test("Code properly copy the input data stream to output") {
    val spark = this.spark
    import spark.implicits._
    implicit val sqlCtx: SQLContext = spark.sqlContext

    val messages = MemoryStream[String]
    val dataFrame = messages.toDF()

    assert(dataFrame.isStreaming, "we expect streaming data set")

    val inputProvider = new ReadDataFrameProvider {
      override def provideDataFrame(spark: SparkSession): DataFrame = dataFrame.select($"value".alias("body"))
    }

    val sinkProvider = new StreamingQueryProvider {
      override def provideStreamingQuery(dataFrame: DataFrame): StreamingQuery = {
        dataFrame.writeStream
          .format("memory")
          .queryName("testing")
          .outputMode("append")
          .start()
      }
    }

    val input: Seq[String] = Seq(
      "{\"BidTime\": \"2021-05-12 10:32:19\", \"Last\": \"0.78337\", \"Type\": \"Q\", \"Bid\": \"0.78337\", \"TotalVolume\": \"190804\", \"High\": \"0.78566\", \"Instrument\": \"AUDUSD.FXCM\", \"Spread\": \"0\", \"TradeTime\": \"2021-05-12 10:32:19\", \"Low\": \"0.78200\", \"PercentChange\": \"0.000638676\", \"NumberofTradesToday\": \"190805\", \"Ask\": \"0.78337\", \"Close\": \"0.78287\", \"Tick\": \"183\", \"Open\": \"0.78291\", \"Change\": \"0.0005\", \"AskTime\": \"2021-05-12 10:32:19\"}",
      "{\"BidTime\": \"2021-05-12 10:32:19\", \"Last\": \"1.21682\", \"Type\": \"Q\", \"Bid\": \"1.21682\", \"TotalVolume\": \"177407\", \"High\": \"1.21817\", \"Instrument\": \"EURUSD.FXCM\", \"Spread\": \"0.00002\", \"TradeTime\": \"2021-05-12 10:32:19\", \"Low\": \"1.21231\", \"PercentChange\": \"0.003256738\", \"NumberofTradesToday\": \"177408\", \"Ask\": \"1.21684\", \"Close\": \"1.21287\", \"Tick\": \"175\", \"Open\": \"1.21280\", \"Change\": \"0.00395\", \"AskTime\": \"2021-05-12 10:32:19\"}",
      "{\"BidTime\": \"2021-05-12 10:32:20\", \"Last\": \"0.78336\", \"Type\": \"Q\", \"Bid\": \"0.78336\", \"TotalVolume\": \"190805\", \"High\": \"0.78566\", \"Instrument\": \"AUDUSD.FXCM\", \"Spread\": \"0.00001\", \"TradeTime\": \"2021-05-12 10:32:20\", \"Low\": \"0.78200\", \"PercentChange\": \"0.000625902\", \"NumberofTradesToday\": \"190806\", \"Ask\": \"0.78337\", \"Close\": \"0.78287\", \"Tick\": \"175\", \"Open\": \"0.78291\", \"Change\": \"0.00049\", \"AskTime\": \"2021-05-12 10:32:20\"}"
    )

    val offset = messages.addData(input)

    val streamingQuery = new DataFrameIntoStreamingQueryCopier(spark, inputProvider, sinkProvider).initiateCopy()
    streamingQuery.processAllAvailable()
    messages.commit(offset.asInstanceOf[LongOffset])

    val output = spark.sql("select * from testing").collect()

    assert(output.length == 3)
    assert(output(2).getAs[String]("BidTime") == "2021-05-12 10:32:20")
  }

}
