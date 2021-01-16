package org.apache.logging.log4j.core.appender.mom.kafka;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.SerializedLayout;
import org.apache.logging.log4j.core.util.StringEncoder;

@Plugin(
   name = "Kafka",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class KafkaAppender extends AbstractAppender {
   private final KafkaManager manager;

   /** @deprecated */
   @Deprecated
   public static KafkaAppender createAppender(@PluginElement("Layout") Layout<? extends Serializable> var0, @PluginElement("Filter") Filter var1, @Required(message = "No name provided for KafkaAppender") @PluginAttribute("name") String var2, @PluginAttribute(value = "ignoreExceptions",defaultBoolean = true) boolean var3, @Required(message = "No topic provided for KafkaAppender") @PluginAttribute("topic") String var4, @PluginElement("Properties") Property[] var5, @PluginConfiguration Configuration var6) {
      KafkaManager var7 = new KafkaManager(var6.getLoggerContext(), var2, var4, true, var5);
      return new KafkaAppender(var2, var0, var1, var3, var7);
   }

   @PluginBuilderFactory
   public static <B extends KafkaAppender.Builder<B>> B newBuilder() {
      return (KafkaAppender.Builder)(new KafkaAppender.Builder()).asBuilder();
   }

   private KafkaAppender(String var1, Layout<? extends Serializable> var2, Filter var3, boolean var4, KafkaManager var5) {
      super(var1, var3, var2, var4);
      this.manager = (KafkaManager)Objects.requireNonNull(var5, "manager");
   }

   public void append(LogEvent var1) {
      if (var1.getLoggerName().startsWith("org.apache.kafka")) {
         LOGGER.warn((String)"Recursive logging from [{}] for appender [{}].", (Object)var1.getLoggerName(), (Object)this.getName());
      } else {
         try {
            Layout var2 = this.getLayout();
            byte[] var3;
            if (var2 != null) {
               if (var2 instanceof SerializedLayout) {
                  byte[] var4 = var2.getHeader();
                  byte[] var5 = var2.toByteArray(var1);
                  var3 = new byte[var4.length + var5.length];
                  System.arraycopy(var4, 0, var3, 0, var4.length);
                  System.arraycopy(var5, 0, var3, var4.length, var5.length);
               } else {
                  var3 = var2.toByteArray(var1);
               }
            } else {
               var3 = StringEncoder.toBytes(var1.getMessage().getFormattedMessage(), StandardCharsets.UTF_8);
            }

            this.manager.send(var3);
         } catch (Exception var6) {
            LOGGER.error((String)"Unable to write to Kafka [{}] for appender [{}].", (Object)this.manager.getName(), this.getName(), var6);
            throw new AppenderLoggingException("Unable to write to Kafka in appender: " + var6.getMessage(), var6);
         }
      }

   }

   public void start() {
      super.start();
      this.manager.startup();
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      var4 &= this.manager.stop(var1, var3);
      this.setStopped();
      return var4;
   }

   public String toString() {
      return "KafkaAppender{name=" + this.getName() + ", state=" + this.getState() + ", topic=" + this.manager.getTopic() + '}';
   }

   // $FF: synthetic method
   KafkaAppender(String var1, Layout var2, Filter var3, boolean var4, KafkaManager var5, Object var6) {
      this(var1, var2, var3, var4, var5);
   }

   public static class Builder<B extends KafkaAppender.Builder<B>> extends AbstractAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<KafkaAppender> {
      @PluginAttribute("topic")
      private String topic;
      @PluginAttribute(
         value = "syncSend",
         defaultBoolean = true
      )
      private boolean syncSend;
      @PluginElement("Properties")
      private Property[] properties;

      public Builder() {
         super();
      }

      public KafkaAppender build() {
         KafkaManager var1 = new KafkaManager(this.getConfiguration().getLoggerContext(), this.getName(), this.topic, this.syncSend, this.properties);
         return new KafkaAppender(this.getName(), this.getLayout(), this.getFilter(), this.isIgnoreExceptions(), var1);
      }

      public String getTopic() {
         return this.topic;
      }

      public Property[] getProperties() {
         return this.properties;
      }

      public B setTopic(String var1) {
         this.topic = var1;
         return (KafkaAppender.Builder)this.asBuilder();
      }

      public B setSyncSend(boolean var1) {
         this.syncSend = var1;
         return (KafkaAppender.Builder)this.asBuilder();
      }

      public B setProperties(Property[] var1) {
         this.properties = var1;
         return (KafkaAppender.Builder)this.asBuilder();
      }
   }
}
