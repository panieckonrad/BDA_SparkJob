# create test CosmosDB account
az cosmosdb create --name forextestdb --resource-group BigDataAcademyMay2021

# create database for stream analytics inside CosmoDB account
az cosmosdb sql database create --name asa --account-name forextestdb --resource-group BigDataAcademyMay2021
# create container for stream analytics inside CosmoDB database
az cosmosdb sql container create --account-name forextestdb --database-name asa --name output --partition-key-path /Instrument --resource-group BigDataAcademyMay2021

# create test Azure Storage Table
az storage table create --name asaoutput --account-name forexstoragebda
# create read policy on Azure Storage Table
az storage table policy create --name readpolicy --table-name asaoutput --account-name forexstoragebda --permissions r
# create read and write policy on Azure Storage Table
az storage table policy create --name managepolicy --table-name asaoutput --account-name forexstoragebda --permissions raud
