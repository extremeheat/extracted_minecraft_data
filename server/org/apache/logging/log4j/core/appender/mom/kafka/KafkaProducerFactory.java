package org.apache.logging.log4j.core.appender.mom.kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.Producer;

public interface KafkaProducerFactory {
   Producer<byte[], byte[]> newKafkaProducer(Properties var1);
}
