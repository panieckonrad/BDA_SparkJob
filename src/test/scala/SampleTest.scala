import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class HelloWordTests extends FunSuite with BeforeAndAfterEach {

  var sparkSession : SparkSession = _
  override def beforeEach() {
    sparkSession = SparkSession.builder().appName("testing basics")
      .master("local")
      .config("", "")
      .getOrCreate()
  }

  test("Verify length of sequence"){
    //your unit test assert here like below
    val count = HelloWorldSpark.countStringsLongerThan3Chars(sparkSession, Seq("test", "abc", "another long one"))
    assert(count.head.getLong(0) == 2)
  }

  test("Verify length of empty sequence"){
    //your unit test assert here like below
    val count = HelloWorldSpark.countStringsLongerThan3Chars(sparkSession, Seq())
    assert(count.head.getLong(0) == 0)
  }

  override def afterEach() {
    sparkSession.stop()
  }
}
