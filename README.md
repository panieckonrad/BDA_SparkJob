# Introduction 
Project where we store our all spark jobs.  

# Getting Started
For now this simple repo don't require any external dependencies aprt from sbt.version=1.4.3
Java 8 is required due to some Hadoop initialization problem with tests. (Tests on 16 were failing)


# Build and Test
To run unit tests it's enough to run sbt test. No local spark installation is required.

To deploy build the package by sbt package and manually upload to the cluster (for now), 
while creating new job. After providing the HelloWorldSpark name it should be runnable now!


# Contribute
TODO: Explain how other users and developers can contribute to make your code better. Provide code guidelines 

If you want to learn more about creating good readme files then refer the following [guidelines](https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops). You can also seek inspiration from the below readme files:
- [ASP.NET Core](https://github.com/aspnet/Home)
- [Visual Studio Code](https://github.com/Microsoft/vscode)
- [Chakra Core](https://github.com/Microsoft/ChakraCore)
