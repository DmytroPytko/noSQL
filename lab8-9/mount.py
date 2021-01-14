configs = {"fs.azure.account.auth.type": "OAuth",
           "fs.azure.account.oauth.provider.type": "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider",
           "fs.azure.account.oauth2.client.id": "5f5da4fc-1b9e-4f94-9773-0425a4f724a5",
           "fs.azure.account.oauth2.client.secret": ".0~~_s6RfbWRQN04jz7e1~3fhDfIhX~nNn",
           "fs.azure.account.oauth2.client.endpoint": "https://login.microsoftonline.com/7631cd62-5187-4e15-8b8e-ef653e366e7a/oauth2/token",
           "fs.azure.createRemoteFileSystemDuringInitialization": "true"}

# Optionally, you can add <directory-name> to the source URI of your mount point.
dbutils.fs.mount(
  source = "abfss://pytko9@pytko9.dfs.core.windows.net/",
  mount_point = "/mnt/labs",
  extra_configs = configs)

display(dbutils.fs.ls('/mnt/labs'))
