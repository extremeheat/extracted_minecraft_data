package org.apache.logging.log4j.core.appender.mom.kafka;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.util.Log4jThread;

public class KafkaManager extends AbstractManager {
   public static final String DEFAULT_TIMEOUT_MILLIS = "30000";
   static KafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory();
   private final Properties config = new Properties();
   private Producer<byte[], byte[]> producer;
   private final int timeoutMillis;
   private final String topic;
   private final boolean syncSend;

   public KafkaManager(LoggerContext var1, String var2, String var3, boolean var4, Property[] var5) {
      super(var1, var2);
      this.topic = (String)Objects.requireNonNull(var3, "topic");
      this.syncSend = var4;
      this.config.setProperty("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
      this.config.setProperty("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
      this.config.setProperty("batch.size", "0");
      Property[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Property var9 = var6[var8];
         this.config.setProperty(var9.getName(), var9.getValue());
      }

      this.timeoutMillis = Integer.parseInt(this.config.getProperty("timeout.ms", "30000"));
   }

   public boolean releaseSub(long var1, TimeUnit var3) {
      if (var1 > 0L) {
         this.closeProducer(var1, var3);
      } else {
         this.closeProducer((long)this.timeoutMillis, TimeUnit.MILLISECONDS);
      }

      return true;
   }

   private void closeProducer(long var1, TimeUnit var3) {
      if (this.producer != null) {
         Log4jThread var4 = new Log4jThread(new Runnable() {
            public void run() {
               if (KafkaManager.this.producer != null) {
                  KafkaManager.this.producer.close();
               }

            }
         }, "KafkaManager-CloseThread");
         var4.setDaemon(true);
         var4.start();

         try {
            var4.join(var3.toMillis(var1));
         } catch (InterruptedException var6) {
            Thread.currentThread().interrupt();
         }
      }

   }

   public void send(byte[] var1) throws ExecutionException, InterruptedException, TimeoutException {
      if (this.producer != null) {
         ProducerRecord var2 = new ProducerRecord(this.topic, var1);
         if (this.syncSend) {
            Future var3 = this.producer.send(var2);
            var3.get((long)this.timeoutMillis, TimeUnit.MILLISECONDS);
         } else {
            this.producer.send(var2, new Callback() {
               public void onCompletion(RecordMetadata var1, Exception var2) {
                  if (var2 != null) {
                     KafkaManager.LOGGER.error((String)("Unable to write to Kafka [" + KafkaManager.this.getName() + "]."), (Throwable)var2);
                  }

               }
            });
         }
      }

   }

   public void startup() {
      this.producer = producerFactory.newKafkaProducer(this.config);
   }

   public String getTopic() {
      return this.topic;
   }
}
