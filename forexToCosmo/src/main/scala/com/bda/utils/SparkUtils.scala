package com.bda.utils

import com.databricks.dbutils_v1.{DBUtilsHolder, DBUtilsV1}

object SparkUtils {
  val dbutils: DBUtilsV1 = DBUtilsHolder.dbutils

  def getSecret(key: String): String = {
    dbutils.secrets.get("databricks", key)
  }
}
