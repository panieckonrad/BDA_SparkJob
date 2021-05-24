package com.bda.cosmoConnector

import com.bda.cosmoConnector.traits.Writer
import com.typesafe.config.Config

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

/**
 * Classes for testing purposes, WIP
 */
class LocalWriter(configuration: Config) extends Writer {
  val slow_window_span: String = configuration.getString("slow.avgSpan")
  val fast_window_span: String = configuration.getString("fast.avgSpan")
  val userID: String = configuration.getString("userID")
  val defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

  override def save(data: Seq[Any]): Unit = data match {
    case Seq(date: Date, instr: String, avg1: (String, String), avg2: (String, String)) =>
      val slowAvg = if (avg1._1.equals("slow")) avg1._2 else avg2._2
      val fastAvg = if (avg1._1.equals("fast")) avg1._2 else avg2._2
      val seq = Seq(
        (instr, defaultDateFormat.format(date), slowAvg, fastAvg, slow_window_span, fast_window_span, userID)
      )
      println(seq)
  }
}

class LocalWriterDecisions(configuration: Config) extends Writer {
  val slow_window_span: String = configuration.getString("slow.avgSpan")
  val fast_window_span: String = configuration.getString("fast.avgSpan")
  val userID: String = configuration.getString("userID")
  val defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

  override def save(data: Seq[Any]): Unit = data match {
    case Seq(date: Date, instr: String, avg1: (String, String), avg2: (String, String), decision: String) =>
      val slowAvg = if (avg1._1.equals("slow")) avg1._2 else avg2._2
      val fastAvg = if (avg1._1.equals("fast")) avg1._2 else avg2._2
      val seq = Seq(
        (
          instr,
          defaultDateFormat.format(date),
          slowAvg,
          fastAvg,
          slow_window_span,
          fast_window_span,
          userID,
          decision
        )
      )
      println(seq)
  }
}
