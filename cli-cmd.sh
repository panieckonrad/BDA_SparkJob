#COSMOSDB
# create test CosmosDB account
az cosmosdb create --name forextestdb --resource-group BigDataAcademyMay2021
# create database for stream analytics inside CosmoDB account
az cosmosdb sql database create --name asa --account-name forextestdb --resource-group BigDataAcademyMay2021
# create container for stream analytics inside CosmoDB database
az cosmosdb sql container create --account-name forextestdb --database-name asa --name output --partition-key-path /Instrument --resource-group BigDataAcademyMay2021
# create container for stream analytics aggregated data inside CosmoDB database
az cosmosdb sql container create --account-name forextestdb --database-name asa --name currencyExchangeRateOutput --partition-key-path /Instrument --resource-group BigDataAcademyMay2021

#AZURE STORAGE TABLE
# create test Azure Storage account
az storage account create --name forexteststoragebda --resource-group BigDataAcademyMay2021 --location UK South
# create test Azure Storage Table
az storage table create --name asaoutput --account-name forexstoragebda
# create read policy on Azure Storage Table
az storage table policy create --name readpolicy --table-name asaoutput --account-name forexstoragebda --permissions r
# create read and write policy on Azure Storage Table
az storage table policy create --name managepolicy --table-name asaoutput --account-name forexstoragebda --permissions raud

#AZURE STORAGE CONTAINER
# create container used for writing data from spark
az storage container create --name spark-checkpoint --account-name forexspark

#KEYVAULT AND SECRETS
# create key vault to store secrets
az keyvault create --name BigDataVault --resource-group BigDataAcademyMay2021 --location EastUS
# create secret storing key for CosmoDB 'forextestdb' account
az keyvault secret set --vault-name BigDataVault --name forexTestCosmoKey --value SECRET_VALUE
# create secret storing key for 'forex' Event Hub
az keyvault secret set --vault-name BigDataVault --name forexEventHubEndpoint --value SECRET_VALUE
# create secret storing key for 'forex' Event Hub
az keyvault secret set --vault-name BigDataVault --name forextestingEventHubEndpoint --value SECRET_VALUE
# create secret storing key for 'forexspark' storage account
az keyvault secret set --vault-name BigDataVault --name forexSparkCheckpointContainerKey --value SECRET_VALUE

#CONSUMER GROUPS IN EVENTHUB
# create 'spark_processing_consumer_group' consumer group in the production eventhub
az eventhubs eventhub consumer-group create --resource-group BigDataAcademyMay2021 --namespace-name bda2021 --eventhub-name forex --name spark_processing_consumer_group
# create consumer group in the testing eventhub
az eventhubs eventhub consumer-group create --resource-group BigDataAcademyMay2021 --namespace-name bda2021 --eventhub-name forextesting --name spark_forextesting_consumer_group