package com.kafka.compression.benchmark.consumer;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kafka.compression.benchmark.producer.StockAppConstants;
import com.kafka.compression.benchmark.producer.model.StockPrice;

public class SimpleStockPriceConsumer {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private static Consumer<String, StockPrice> createConsumer() {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, StockAppConstants.BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		// Custom Deserializer
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StockDeserializer.class.getName());
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
		// Create the consumer using props.
		final Consumer<String, StockPrice> consumer = new KafkaConsumer<>(props);
		// Subscribe to the topic.
		consumer.subscribe(Collections.singletonList(StockAppConstants.TOPIC));
		return consumer;
	}

	static void runConsumer() throws InterruptedException {
		final Consumer<String, StockPrice> consumer = createConsumer();
		final Map<String, StockPrice> map = new HashMap<>();
		try {
			final int giveUp = 1000;
			int noRecordsCount = 0;
			int readCount = 0;
			while (true) {
				final ConsumerRecords<String, StockPrice> consumerRecords = consumer.poll(1000);
				if (consumerRecords.count() == 0) {
					noRecordsCount++;
					if (noRecordsCount > giveUp)
						break;
					else
						continue;
				}
				readCount++;
				consumerRecords.forEach(record -> {
					map.put(record.key(), record.value());
				});
				if (readCount % 500 == 0) {
					displayRecordsStatsAndStocks(map, consumerRecords);
				}
				consumer.commitAsync();
			}
		} finally {
			consumer.close();
		}
		logger.info("DONE");
	}

	private static void displayRecordsStatsAndStocks(final Map<String, StockPrice> stockPriceMap,
			final ConsumerRecords<String, StockPrice> consumerRecords) {
		logger.info(String.format("New ConsumerRecords par count %d count %d \n", 
									consumerRecords.partitions().size(),
									consumerRecords.count()));
		stockPriceMap.forEach((s, stockPrice) -> logger.info(String.format("ticker %s price %d.%d", 
											stockPrice.getName(),
											stockPrice.getDollars(), 
											stockPrice.getCents())));
		
	}

	public static void main(String... args) throws Exception {
		runConsumer();
	}

}
