# Introduction 
Project where we store our all spark jobs.  

# Getting Started
For now this simple repo don't require any external dependencies aprt from sbt.version=1.4.3
Java 8 is required due to some Hadoop initialization problem with tests. (Tests on 16 were failing)


# Build and Test
To run unit tests it's enough to run sbt test. No local spark installation is required.

To deploy build the package by sbt package and manually upload to the cluster (for now), 
while creating new job. After providing the HelloWorldSpark name it should be runnable now!


# Code guidelines

This Spark project, follows the [Databricks Spark Scale Guide](https://github.com/databricks/scala-style-guide)

The embedded .scalafmt.conf files triggers turns on Scalafmt in IntelliJ and provides reasonably close formatting.
