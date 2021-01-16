package org.apache.logging.log4j.core.layout;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractLayout<T extends Serializable> implements Layout<T> {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected final Configuration configuration;
   protected long eventCount;
   protected final byte[] footer;
   protected final byte[] header;

   /** @deprecated */
   @Deprecated
   public AbstractLayout(byte[] var1, byte[] var2) {
      this((Configuration)null, var1, var2);
   }

   public AbstractLayout(Configuration var1, byte[] var2, byte[] var3) {
      super();
      this.configuration = var1;
      this.header = var2;
      this.footer = var3;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public Map<String, String> getContentFormat() {
      return new HashMap();
   }

   public byte[] getFooter() {
      return this.footer;
   }

   public byte[] getHeader() {
      return this.header;
   }

   protected void markEvent() {
      ++this.eventCount;
   }

   public void encode(LogEvent var1, ByteBufferDestination var2) {
      byte[] var3 = this.toByteArray(var1);
      writeTo(var3, 0, var3.length, var2);
   }

   public static void writeTo(byte[] var0, int var1, int var2, ByteBufferDestination var3) {
      boolean var4 = false;
      synchronized(var3) {
         ByteBuffer var6 = var3.getByteBuffer();

         do {
            if (var2 > var6.remaining()) {
               var6 = var3.drain(var6);
            }

            int var9 = Math.min(var2, var6.remaining());
            var6.put(var0, var1, var9);
            var1 += var9;
            var2 -= var9;
         } while(var2 > 0);

      }
   }

   public abstract static class Builder<B extends AbstractLayout.Builder<B>> {
      @PluginConfiguration
      private Configuration configuration;
      @PluginBuilderAttribute
      private byte[] footer;
      @PluginBuilderAttribute
      private byte[] header;

      public Builder() {
         super();
      }

      public B asBuilder() {
         return this;
      }

      public Configuration getConfiguration() {
         return this.configuration;
      }

      public byte[] getFooter() {
         return this.footer;
      }

      public byte[] getHeader() {
         return this.header;
      }

      public B setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this.asBuilder();
      }

      public B setFooter(byte[] var1) {
         this.footer = var1;
         return this.asBuilder();
      }

      public B setHeader(byte[] var1) {
         this.header = var1;
         return this.asBuilder();
      }
   }
}
