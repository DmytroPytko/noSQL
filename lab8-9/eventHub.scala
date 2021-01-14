import org.apache.spark.eventhubs.{ ConnectionStringBuilder, EventHubsConf, EventPosition }
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

// To connect to an Event Hub, EntityPath is required as part of the connection string.
// Here, we assume that the connection string from the Azure portal does not have the EntityPath part.
val appID = "5f5da4fc-1b9e-4f94-9773-0425a4f724a5"
val password = ".0~~_s6RfbWRQN04jz7e1~3fhDfIhX~nNn"
val tenantID = "7631cd62-5187-4e15-8b8e-ef653e366e7a"
val fileSystemName = "pytko9"
val storageAccountName = "pytko9"
val connectionString = ConnectionStringBuilder("Endpoint=sb://pytko9.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=yJl+ggUr6PYmJl7lEk+agBNGSfU9p7eA65G+ZV/sgAw=")
  .setEventHubName("pytko9")
  .build
val eventHubsConf = EventHubsConf(connectionString)
  .setStartingPosition(EventPosition.fromEndOfStream)

var streamingInputDF =
  spark.readStream
    .format("eventhubs")
    .options(eventHubsConf.toMap)
    .load()

val filtered = streamingInputDF.select (
  from_unixtime(col("enqueuedTime").cast(LongType)).alias("enqueuedTime")
     , get_json_object(col("body").cast(StringType), "$.year").alias("year")
     , get_json_object(col("body").cast(StringType), "$._1st_max_datetime").alias("_1st_max_datetime")
     , get_json_object(col("body").cast(StringType), "$._2nd_max_value").alias("_2nd_max_value")
     , get_json_object(col("body").cast(StringType), "$._2nd_max_datetime").alias("_2nd_max_datetime")
)

filtered.writeStream
  .format("com.databricks.spark.csv")
  .outputMode("append")
  .option("checkpointLocation", "/mnt/labs/lab9dir")
  .start("/mnt/labs/lab9dir")