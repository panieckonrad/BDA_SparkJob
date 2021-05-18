# Module Description
This module is responsible for moving data from eventhub to cosmosdb. Configurations are stored in 
application.conf file.
#Configuration
This jar uses command line arguments ('args' array) to choose a configuration for testing or 
production deployment. If no arguments are provided the configuration defaults to test.

The arguments can be:

`test`   ---> for deployment to test environment
`prod`   ---> for deployment to production environment
#Deployment
If you want to submit the jar to the databricks cluster, you need to use the `forexToCosmo-{version}-shaded-jar-with-dependencies.jar`.
To provide the arguments to jar on cluster use a parameter field ex. `["prod"]`

The Spark version 3.x is not supported. 

