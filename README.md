# kafka-compression-benchmark
This project will bench mark all the compression attributes of kafka with commpression.type of ( none , gzip , lz4,snappy) . SimpleStockPriceConsumer is the consumer which will listen to the topic stock-prices and StockPriceKafkaProducer is the producer with compression . 

Follow the below steps before start looking into the metrics for compression 

This is tested with kafka_2.11-1.0.0 , so we can use higher version , but not below to this 
1) Run zookepr 
2) Run three broker with server-0.properties  server-1.properties  server-2.properties from src/main/resources 
./kafka-server-start.sh src/main/resources/server-0.properties
./kafka-server-start.sh src/main/resources/server-1.properties
./kafka-server-start.sh src/main/resources/server-2.properties

3) Create topic 
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 3 --topic stock-prices --config min.insync.replicas=2

Follow this medium.com page to understand more how its working 

