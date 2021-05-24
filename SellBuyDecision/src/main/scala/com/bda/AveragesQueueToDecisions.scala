package com.bda

import com.bda.cosmoConnector.traits.Writer

import java.text.SimpleDateFormat
import java.util.{Date, Locale}
import scala.collection.mutable
import scala.concurrent.forkjoin.LinkedTransferQueue

class AveragesQueueToDecisions(
    val slowAvgQueue: LinkedTransferQueue[Seq[Any]],
    val fastAvgQueue: LinkedTransferQueue[Seq[Any]],
    val cosmoAvgWriter: Writer,
    val cosmoBuySellWriter: Writer
) {
  val defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

  val lastSlowProcessed: mutable.Map[String, Date] = mutable.Map[String, Date]()
  val lastFastProcessed: mutable.Map[String, Date] = mutable.Map[String, Date]()

  val lastCompareDate: mutable.Map[String, Date] = mutable.Map[String, Date]()
  val averagesMap: mutable.Map[(Date, String), (String, String)] =
    mutable.Map[(Date, String), (String, String)]()
  val keys = Seq("Instrument", "start", "end", "ask_avg")
  var previousBigger: mutable.Map[String, String] = mutable.Map[String, String]()

  def initialize(): Unit = {
    while (true) {
      Thread.sleep(1000)
      takeElementFromQueue(slowAvgQueue, "slow", lastSlowProcessed)
      takeElementFromQueue(fastAvgQueue, "fast", lastFastProcessed)
    }
  }

  def takeElementFromQueue(
      queue: LinkedTransferQueue[Seq[Any]],
      queueType: String,
      processedMap: mutable.Map[String, Date]
  ): Unit = {
    if (!queue.isEmpty) {
      val currMap = (keys zip queue.take()).toMap
      val currDate = defaultDateFormat.parse(currMap("end").toString)
      if (
        !processedMap.contains(currMap("Instrument").toString) ||
        currDate.after(processedMap(currMap("Instrument").toString))
      ) {
        // if data for new minute appear, add it to the averagesMap
        processedMap(currMap("Instrument").toString) = currDate
        addToMap(currDate, currMap("Instrument").toString, (queueType, currMap("ask_avg").toString))
      }
    }
  }

  def addToMap(date: Date, instrument: String, avg: (String, String)) {
    val mapKey = (date, instrument)
    if (averagesMap contains mapKey) {
      if (!averagesMap(mapKey)._1.equals(avg._1)) {
        // second average already stored this date so we can compare them
        compare(date, averagesMap(mapKey), avg, instrument)
        averagesMap -= mapKey
      }
    } else {
      averagesMap += (mapKey -> avg)
    }
  }

  def compare(date: Date, v1: (String, String), v2: (String, String), instrument: String): Unit = {
    cosmoAvgWriter.save(Seq(date, instrument, v1, v2)) // save moving avg to cosmoDB

    if (lastCompareDate.contains(instrument) && !date.after(lastCompareDate(instrument))) return
    lastCompareDate(instrument) = date

    val biggerAvg = if (v1._2.toDouble > v2._2.toDouble) v1 else v2

    if (!previousBigger.contains(instrument)) {
      previousBigger(instrument) = biggerAvg._1
    } else if (!biggerAvg._1.equals(previousBigger(instrument))) {
      previousBigger(instrument) match {
        case "slow" =>
          // fast average surpass slow average -> BUY
          cosmoBuySellWriter.save(Seq(date, instrument, v1, v2, "BUY"))
        case "fast" =>
          // slow average surpass fast average -> SELL
          cosmoBuySellWriter.save(Seq(date, instrument, v1, v2, "SELL"))
      }
      previousBigger(instrument) = biggerAvg._1
    }
  }
}
