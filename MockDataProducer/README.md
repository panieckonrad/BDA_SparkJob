# Module Description 
This module is responsible for triggering and producing test Forex data. After running, 1000 rows of json are 
produced to `forextesting` Event Hub.

# Run and Test
To run unit tests go to MockDataProducer directory and type `mvn test` in terminal.

To run main class and produce messages you need to set proper environment variables. 
Replace dummynamespace and run `az eventhubs namespace authorization-rule keys list --resource-group 
BigDataAcademyMay2021 --namespace-name dummynamespace --name RootManageSharedAccessKey` to get connection string.

Copy primaryConnectionString value (with quotes) and export it to environment variable with `export 
CONNECTION_STRING=primaryConnectionStringValue`. In addition, you need to extract endpoint part of connection string. 
With this value set new environment variable `BOOTSTRAP_SERVER`. Your command should look similar to `export 
BOOTSTRAP_SERVER=dummynamespace.servicebus.windows.net:9093`

After that ensure you are in MockDataProducer directory and type `mvn exec:java -Dexec.args="x test_data.json"` in terminal 
to produce `x` messages from test_data.json file.
