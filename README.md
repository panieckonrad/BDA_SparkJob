# Introduction 
Project where we store our all spark jobs.  

# Getting Started
Java 8 is required due to some Hadoop initialization problem with tests. (Tests on 16 were failing)


# Build and Test
To run unit tests it's enough to run mvn test. No local spark installation is required.

To build all the modules use command 'mvn package' and manually upload the chosen jar-with-dependencies from 
the chosen {submodule}/target folder to the cluster while creating new job. 
After providing the main class name (ex. com.bda.HelloWorldSpark) it should be runnable now!

To build a submodule individually just run the command 'mvn package' inside a submodule directory.


# Code guidelines

This Spark project, follows the [Databricks Spark Scale Guide](https://github.com/databricks/scala-style-guide)

The embedded .scalafmt.conf files triggers turns on Scalafmt in IntelliJ and provides reasonably close formatting.
