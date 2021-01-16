package org.apache.logging.log4j.core.layout;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.StringEncoder;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PropertiesUtil;

public abstract class AbstractStringLayout extends AbstractLayout<String> implements StringLayout {
   protected static final int DEFAULT_STRING_BUILDER_SIZE = 1024;
   protected static final int MAX_STRING_BUILDER_SIZE = Math.max(1024, size("log4j.layoutStringBuilder.maxSize", 2048));
   private static final ThreadLocal<StringBuilder> threadLocal = new ThreadLocal();
   private Encoder<StringBuilder> textEncoder;
   private transient Charset charset;
   private final String charsetName;
   private final AbstractStringLayout.Serializer footerSerializer;
   private final AbstractStringLayout.Serializer headerSerializer;
   private final boolean useCustomEncoding;

   protected static StringBuilder getStringBuilder() {
      StringBuilder var0 = (StringBuilder)threadLocal.get();
      if (var0 == null) {
         var0 = new StringBuilder(1024);
         threadLocal.set(var0);
      }

      trimToMaxSize(var0);
      var0.setLength(0);
      return var0;
   }

   private static boolean isPreJava8() {
      String var0 = System.getProperty("java.version");
      String[] var1 = var0.split("\\.");

      try {
         int var2 = Integer.parseInt(var1[1]);
         return var2 < 8;
      } catch (Exception var3) {
         return true;
      }
   }

   private static int size(String var0, int var1) {
      return PropertiesUtil.getProperties().getIntegerProperty(var0, var1);
   }

   protected static void trimToMaxSize(StringBuilder var0) {
      if (var0.length() > MAX_STRING_BUILDER_SIZE) {
         var0.setLength(MAX_STRING_BUILDER_SIZE);
         var0.trimToSize();
      }

   }

   protected AbstractStringLayout(Charset var1) {
      this(var1, (byte[])null, (byte[])null);
   }

   protected AbstractStringLayout(Charset var1, byte[] var2, byte[] var3) {
      super((Configuration)null, var2, var3);
      this.headerSerializer = null;
      this.footerSerializer = null;
      this.charset = var1 == null ? StandardCharsets.UTF_8 : var1;
      this.charsetName = this.charset.name();
      this.useCustomEncoding = isPreJava8() && (StandardCharsets.ISO_8859_1.equals(var1) || StandardCharsets.US_ASCII.equals(var1));
      this.textEncoder = Constants.ENABLE_DIRECT_ENCODERS ? new StringBuilderEncoder(this.charset) : null;
   }

   protected AbstractStringLayout(Configuration var1, Charset var2, AbstractStringLayout.Serializer var3, AbstractStringLayout.Serializer var4) {
      super(var1, (byte[])null, (byte[])null);
      this.headerSerializer = var3;
      this.footerSerializer = var4;
      this.charset = var2 == null ? StandardCharsets.UTF_8 : var2;
      this.charsetName = this.charset.name();
      this.useCustomEncoding = isPreJava8() && (StandardCharsets.ISO_8859_1.equals(var2) || StandardCharsets.US_ASCII.equals(var2));
      this.textEncoder = Constants.ENABLE_DIRECT_ENCODERS ? new StringBuilderEncoder(this.charset) : null;
   }

   protected byte[] getBytes(String var1) {
      if (this.useCustomEncoding) {
         return StringEncoder.encodeSingleByteChars(var1);
      } else {
         try {
            return var1.getBytes(this.charsetName);
         } catch (UnsupportedEncodingException var3) {
            return var1.getBytes(this.charset);
         }
      }
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getContentType() {
      return "text/plain";
   }

   public byte[] getFooter() {
      return this.serializeToBytes(this.footerSerializer, super.getFooter());
   }

   public AbstractStringLayout.Serializer getFooterSerializer() {
      return this.footerSerializer;
   }

   public byte[] getHeader() {
      return this.serializeToBytes(this.headerSerializer, super.getHeader());
   }

   public AbstractStringLayout.Serializer getHeaderSerializer() {
      return this.headerSerializer;
   }

   private DefaultLogEventFactory getLogEventFactory() {
      return DefaultLogEventFactory.getInstance();
   }

   protected Encoder<StringBuilder> getStringBuilderEncoder() {
      if (this.textEncoder == null) {
         this.textEncoder = new StringBuilderEncoder(this.getCharset());
      }

      return this.textEncoder;
   }

   protected byte[] serializeToBytes(AbstractStringLayout.Serializer var1, byte[] var2) {
      String var3 = this.serializeToString(var1);
      return var1 == null ? var2 : StringEncoder.toBytes(var3, this.getCharset());
   }

   protected String serializeToString(AbstractStringLayout.Serializer var1) {
      if (var1 == null) {
         return null;
      } else {
         LoggerConfig var2 = this.getConfiguration().getRootLogger();
         LogEvent var3 = this.getLogEventFactory().createEvent(var2.getName(), (Marker)null, "", var2.getLevel(), (Message)null, (List)null, (Throwable)null);
         return var1.toSerializable(var3);
      }
   }

   public byte[] toByteArray(LogEvent var1) {
      return this.getBytes((String)this.toSerializable(var1));
   }

   public interface Serializer2 {
      StringBuilder toSerializable(LogEvent var1, StringBuilder var2);
   }

   public interface Serializer {
      String toSerializable(LogEvent var1);
   }

   public abstract static class Builder<B extends AbstractStringLayout.Builder<B>> extends AbstractLayout.Builder<B> {
      @PluginBuilderAttribute("charset")
      private Charset charset;
      @PluginElement("footerSerializer")
      private AbstractStringLayout.Serializer footerSerializer;
      @PluginElement("headerSerializer")
      private AbstractStringLayout.Serializer headerSerializer;

      public Builder() {
         super();
      }

      public Charset getCharset() {
         return this.charset;
      }

      public AbstractStringLayout.Serializer getFooterSerializer() {
         return this.footerSerializer;
      }

      public AbstractStringLayout.Serializer getHeaderSerializer() {
         return this.headerSerializer;
      }

      public B setCharset(Charset var1) {
         this.charset = var1;
         return (AbstractStringLayout.Builder)this.asBuilder();
      }

      public B setFooterSerializer(AbstractStringLayout.Serializer var1) {
         this.footerSerializer = var1;
         return (AbstractStringLayout.Builder)this.asBuilder();
      }

      public B setHeaderSerializer(AbstractStringLayout.Serializer var1) {
         this.headerSerializer = var1;
         return (AbstractStringLayout.Builder)this.asBuilder();
      }
   }
}
