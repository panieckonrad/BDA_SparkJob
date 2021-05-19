package com.bda

import ujson.Value

import java.io.InputStream

object ForexDataReader {
  def readJson(amount: Int, fileName: String): List[Value] = {
    val stream: InputStream = getClass.getClassLoader.getResourceAsStream(fileName)
    val fileContent: Iterator[String] = scala.io.Source.fromInputStream(stream).getLines
    val json = ujson.read(fileContent.mkString)
    stream.close()
    json.arr.toList.take(amount)
  }
}
