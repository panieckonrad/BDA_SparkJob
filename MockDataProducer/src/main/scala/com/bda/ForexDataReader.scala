package com.bda

import ujson.Value

import java.io.InputStream

object ForexDataReader {
  def readJson(path: String): List[Value] = {
    val stream: InputStream = getClass.getClassLoader.getResourceAsStream(path)
    val fileContent: Iterator[String] = scala.io.Source.fromInputStream(stream).getLines
    val json = ujson.read(fileContent.mkString)
    stream.close()
    json.arr.toList
  }
}
