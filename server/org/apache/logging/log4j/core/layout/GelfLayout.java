package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Severity;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.TriConsumer;

@Plugin(
   name = "GelfLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class GelfLayout extends AbstractStringLayout {
   private static final char C = ',';
   private static final int COMPRESSION_THRESHOLD = 1024;
   private static final char Q = '"';
   private static final String QC = "\",";
   private static final String QU = "\"_";
   private final KeyValuePair[] additionalFields;
   private final int compressionThreshold;
   private final GelfLayout.CompressionType compressionType;
   private final String host;
   private final boolean includeStacktrace;
   private final boolean includeThreadContext;
   private static final TriConsumer<String, Object, StringBuilder> WRITE_KEY_VALUES_INTO = new TriConsumer<String, Object, StringBuilder>() {
      public void accept(String var1, Object var2, StringBuilder var3) {
         var3.append("\"_");
         JsonUtils.quoteAsString(var1, var3);
         var3.append("\":\"");
         JsonUtils.quoteAsString(GelfLayout.toNullSafeString(String.valueOf(var2)), var3);
         var3.append("\",");
      }
   };
   private static final ThreadLocal<StringBuilder> messageStringBuilder = new ThreadLocal();
   private static final ThreadLocal<StringBuilder> timestampStringBuilder = new ThreadLocal();

   /** @deprecated */
   @Deprecated
   public GelfLayout(String var1, KeyValuePair[] var2, GelfLayout.CompressionType var3, int var4, boolean var5) {
      this((Configuration)null, var1, var2, var3, var4, var5, true);
   }

   private GelfLayout(Configuration var1, String var2, KeyValuePair[] var3, GelfLayout.CompressionType var4, int var5, boolean var6, boolean var7) {
      super(var1, StandardCharsets.UTF_8, (AbstractStringLayout.Serializer)null, (AbstractStringLayout.Serializer)null);
      this.host = var2 != null ? var2 : NetUtils.getLocalHostname();
      this.additionalFields = var3 != null ? var3 : new KeyValuePair[0];
      if (var1 == null) {
         KeyValuePair[] var8 = this.additionalFields;
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            KeyValuePair var11 = var8[var10];
            if (valueNeedsLookup(var11.getValue())) {
               throw new IllegalArgumentException("configuration needs to be set when there are additional fields with variables");
            }
         }
      }

      this.compressionType = var4;
      this.compressionThreshold = var5;
      this.includeStacktrace = var6;
      this.includeThreadContext = var7;
   }

   /** @deprecated */
   @Deprecated
   public static GelfLayout createLayout(@PluginAttribute("host") String var0, @PluginElement("AdditionalField") KeyValuePair[] var1, @PluginAttribute(value = "compressionType",defaultString = "GZIP") GelfLayout.CompressionType var2, @PluginAttribute(value = "compressionThreshold",defaultInt = 1024) int var3, @PluginAttribute(value = "includeStacktrace",defaultBoolean = true) boolean var4) {
      return new GelfLayout((Configuration)null, var0, var1, var2, var3, var4, true);
   }

   @PluginBuilderFactory
   public static <B extends GelfLayout.Builder<B>> B newBuilder() {
      return (GelfLayout.Builder)(new GelfLayout.Builder()).asBuilder();
   }

   public Map<String, String> getContentFormat() {
      return Collections.emptyMap();
   }

   public String getContentType() {
      return "application/json; charset=" + this.getCharset();
   }

   public byte[] toByteArray(LogEvent var1) {
      StringBuilder var2 = this.toText(var1, getStringBuilder(), false);
      byte[] var3 = this.getBytes(var2.toString());
      return this.compressionType != GelfLayout.CompressionType.OFF && var3.length > this.compressionThreshold ? this.compress(var3) : var3;
   }

   public void encode(LogEvent var1, ByteBufferDestination var2) {
      if (this.compressionType != GelfLayout.CompressionType.OFF) {
         super.encode(var1, var2);
      } else {
         StringBuilder var3 = this.toText(var1, getStringBuilder(), true);
         Encoder var4 = this.getStringBuilderEncoder();
         var4.encode(var3, var2);
      }
   }

   private byte[] compress(byte[] var1) {
      try {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream(this.compressionThreshold / 8);
         DeflaterOutputStream var3 = this.compressionType.createDeflaterOutputStream(var2);
         Throwable var4 = null;

         byte[] var5;
         try {
            if (var3 != null) {
               var3.write(var1);
               var3.finish();
               return var2.toByteArray();
            }

            var5 = var1;
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return var5;
      } catch (IOException var18) {
         StatusLogger.getLogger().error(var18);
         return var1;
      }
   }

   public String toSerializable(LogEvent var1) {
      StringBuilder var2 = this.toText(var1, getStringBuilder(), false);
      return var2.toString();
   }

   private StringBuilder toText(LogEvent var1, StringBuilder var2, boolean var3) {
      var2.append('{');
      var2.append("\"version\":\"1.1\",");
      var2.append("\"host\":\"");
      JsonUtils.quoteAsString(toNullSafeString(this.host), var2);
      var2.append("\",");
      var2.append("\"timestamp\":").append(formatTimestamp(var1.getTimeMillis())).append(',');
      var2.append("\"level\":").append(this.formatLevel(var1.getLevel())).append(',');
      if (var1.getThreadName() != null) {
         var2.append("\"_thread\":\"");
         JsonUtils.quoteAsString(var1.getThreadName(), var2);
         var2.append("\",");
      }

      if (var1.getLoggerName() != null) {
         var2.append("\"_logger\":\"");
         JsonUtils.quoteAsString(var1.getLoggerName(), var2);
         var2.append("\",");
      }

      if (this.additionalFields.length > 0) {
         StrSubstitutor var4 = this.getConfiguration().getStrSubstitutor();
         KeyValuePair[] var5 = this.additionalFields;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            KeyValuePair var8 = var5[var7];
            var2.append("\"_");
            JsonUtils.quoteAsString(var8.getKey(), var2);
            var2.append("\":\"");
            String var9 = valueNeedsLookup(var8.getValue()) ? var4.replace(var1, var8.getValue()) : var8.getValue();
            JsonUtils.quoteAsString(toNullSafeString(var9), var2);
            var2.append("\",");
         }
      }

      if (this.includeThreadContext) {
         var1.getContextData().forEach(WRITE_KEY_VALUES_INTO, var2);
      }

      if (var1.getThrown() != null) {
         var2.append("\"full_message\":\"");
         if (this.includeStacktrace) {
            JsonUtils.quoteAsString(formatThrowable(var1.getThrown()), var2);
         } else {
            JsonUtils.quoteAsString(var1.getThrown().toString(), var2);
         }

         var2.append("\",");
      }

      var2.append("\"short_message\":\"");
      Message var13 = var1.getMessage();
      if (var13 instanceof CharSequence) {
         JsonUtils.quoteAsString((CharSequence)var13, var2);
      } else if (var3 && var13 instanceof StringBuilderFormattable) {
         StringBuilder var14 = getMessageStringBuilder();

         try {
            ((StringBuilderFormattable)var13).formatTo(var14);
            JsonUtils.quoteAsString(var14, var2);
         } finally {
            trimToMaxSize(var14);
         }
      } else {
         JsonUtils.quoteAsString(toNullSafeString(var13.getFormattedMessage()), var2);
      }

      var2.append('"');
      var2.append('}');
      return var2;
   }

   private static boolean valueNeedsLookup(String var0) {
      return var0 != null && var0.contains("${");
   }

   private static StringBuilder getMessageStringBuilder() {
      StringBuilder var0 = (StringBuilder)messageStringBuilder.get();
      if (var0 == null) {
         var0 = new StringBuilder(1024);
         messageStringBuilder.set(var0);
      }

      var0.setLength(0);
      return var0;
   }

   private static CharSequence toNullSafeString(CharSequence var0) {
      return (CharSequence)(var0 == null ? "" : var0);
   }

   static CharSequence formatTimestamp(long var0) {
      if (var0 < 1000L) {
         return "0";
      } else {
         StringBuilder var2 = getTimestampStringBuilder();
         var2.append(var0);
         var2.insert(var2.length() - 3, '.');
         return var2;
      }
   }

   private static StringBuilder getTimestampStringBuilder() {
      StringBuilder var0 = (StringBuilder)timestampStringBuilder.get();
      if (var0 == null) {
         var0 = new StringBuilder(20);
         timestampStringBuilder.set(var0);
      }

      var0.setLength(0);
      return var0;
   }

   private int formatLevel(Level var1) {
      return Severity.getSeverity(var1).getCode();
   }

   static CharSequence formatThrowable(Throwable var0) {
      StringWriter var1 = new StringWriter(2048);
      PrintWriter var2 = new PrintWriter(var1);
      var0.printStackTrace(var2);
      var2.flush();
      return var1.getBuffer();
   }

   // $FF: synthetic method
   GelfLayout(Configuration var1, String var2, KeyValuePair[] var3, GelfLayout.CompressionType var4, int var5, boolean var6, boolean var7, Object var8) {
      this(var1, var2, var3, var4, var5, var6, var7);
   }

   public static class Builder<B extends GelfLayout.Builder<B>> extends AbstractStringLayout.Builder<B> implements org.apache.logging.log4j.core.util.Builder<GelfLayout> {
      @PluginBuilderAttribute
      private String host;
      @PluginElement("AdditionalField")
      private KeyValuePair[] additionalFields;
      @PluginBuilderAttribute
      private GelfLayout.CompressionType compressionType;
      @PluginBuilderAttribute
      private int compressionThreshold;
      @PluginBuilderAttribute
      private boolean includeStacktrace;
      @PluginBuilderAttribute
      private boolean includeThreadContext;

      public Builder() {
         super();
         this.compressionType = GelfLayout.CompressionType.GZIP;
         this.compressionThreshold = 1024;
         this.includeStacktrace = true;
         this.includeThreadContext = true;
         this.setCharset(StandardCharsets.UTF_8);
      }

      public GelfLayout build() {
         return new GelfLayout(this.getConfiguration(), this.host, this.additionalFields, this.compressionType, this.compressionThreshold, this.includeStacktrace, this.includeThreadContext);
      }

      public String getHost() {
         return this.host;
      }

      public GelfLayout.CompressionType getCompressionType() {
         return this.compressionType;
      }

      public int getCompressionThreshold() {
         return this.compressionThreshold;
      }

      public boolean isIncludeStacktrace() {
         return this.includeStacktrace;
      }

      public boolean isIncludeThreadContext() {
         return this.includeThreadContext;
      }

      public KeyValuePair[] getAdditionalFields() {
         return this.additionalFields;
      }

      public B setHost(String var1) {
         this.host = var1;
         return (GelfLayout.Builder)this.asBuilder();
      }

      public B setCompressionType(GelfLayout.CompressionType var1) {
         this.compressionType = var1;
         return (GelfLayout.Builder)this.asBuilder();
      }

      public B setCompressionThreshold(int var1) {
         this.compressionThreshold = var1;
         return (GelfLayout.Builder)this.asBuilder();
      }

      public B setIncludeStacktrace(boolean var1) {
         this.includeStacktrace = var1;
         return (GelfLayout.Builder)this.asBuilder();
      }

      public B setIncludeThreadContext(boolean var1) {
         this.includeThreadContext = var1;
         return (GelfLayout.Builder)this.asBuilder();
      }

      public B setAdditionalFields(KeyValuePair[] var1) {
         this.additionalFields = var1;
         return (GelfLayout.Builder)this.asBuilder();
      }
   }

   public static enum CompressionType {
      GZIP {
         public DeflaterOutputStream createDeflaterOutputStream(OutputStream var1) throws IOException {
            return new GZIPOutputStream(var1);
         }
      },
      ZLIB {
         public DeflaterOutputStream createDeflaterOutputStream(OutputStream var1) throws IOException {
            return new DeflaterOutputStream(var1);
         }
      },
      OFF {
         public DeflaterOutputStream createDeflaterOutputStream(OutputStream var1) throws IOException {
            return null;
         }
      };

      private CompressionType() {
      }

      public abstract DeflaterOutputStream createDeflaterOutputStream(OutputStream var1) throws IOException;

      // $FF: synthetic method
      CompressionType(Object var3) {
         this();
      }
   }
}
