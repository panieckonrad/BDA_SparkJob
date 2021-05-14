package com.bda.traits

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.StreamingQuery

trait StreamingQueryProvider {
  def provideStreamingQuery(dataFrame: DataFrame): StreamingQuery
}
