package com.bda.utils

import com.bda.utils.SparkUtils.{dbutils, getSecret}
import com.typesafe.config.Config

class MountUtils(configuration: Config) {

  def mountCheckpointContainer(): Unit = {
    val config = configuration.getConfig("mountutils")
    val containerName = config.getString("containerName")
    val storageAccountName = config.getString("storageAccountName")
    val accountKey = getSecret(config.getString("accountKeySecretKey"))
    val containerConfig = "fs.azure.account.key." + storageAccountName + ".blob.core.windows.net"
    val mountDirectory = config.getString("mountPoint")

    if (!dbutils.fs.mounts.map(mnt => mnt.mountPoint).contains(mountDirectory)) {
      dbutils.fs.mount(
        source = "wasbs://" + containerName + "@" + storageAccountName + ".blob.core.windows.net/",
        mountPoint = mountDirectory,
        extraConfigs = Map(containerConfig -> accountKey)
      )
    }
  }

}
