package com.bda.utils

import org.apache.spark.sql.types.{StringType, StructField, StructType}

object ForexUtils {

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

  val forexDataSchema = new StructType(forexColumns.map(key => StructField(key, StringType)).toArray)
}
