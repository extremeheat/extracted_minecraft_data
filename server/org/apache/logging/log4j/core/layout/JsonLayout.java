package org.apache.logging.log4j.core.layout;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;

@Plugin(
   name = "JsonLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class JsonLayout extends AbstractJacksonLayout {
   private static final String DEFAULT_FOOTER = "]";
   private static final String DEFAULT_HEADER = "[";
   static final String CONTENT_TYPE = "application/json";

   protected JsonLayout(Configuration var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7, String var8, String var9, Charset var10, boolean var11) {
      super(var1, (new JacksonFactory.JSON(var4, var11)).newWriter(var2, var3, var6), var10, var6, var5, var7, PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var8).setDefaultPattern("[").build(), PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var9).setDefaultPattern("]").build());
   }

   public byte[] getHeader() {
      if (!this.complete) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();
         String var2 = this.serializeToString(this.getHeaderSerializer());
         if (var2 != null) {
            var1.append(var2);
         }

         var1.append(this.eol);
         return this.getBytes(var1.toString());
      }
   }

   public byte[] getFooter() {
      if (!this.complete) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.eol);
         String var2 = this.serializeToString(this.getFooterSerializer());
         if (var2 != null) {
            var1.append(var2);
         }

         var1.append(this.eol);
         return this.getBytes(var1.toString());
      }
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap();
      var1.put("version", "2.0");
      return var1;
   }

   public String getContentType() {
      return "application/json; charset=" + this.getCharset();
   }

   /** @deprecated */
   @Deprecated
   public static JsonLayout createLayout(@PluginConfiguration Configuration var0, @PluginAttribute("locationInfo") boolean var1, @PluginAttribute("properties") boolean var2, @PluginAttribute("propertiesAsList") boolean var3, @PluginAttribute("complete") boolean var4, @PluginAttribute("compact") boolean var5, @PluginAttribute("eventEol") boolean var6, @PluginAttribute(value = "header",defaultString = "[") String var7, @PluginAttribute(value = "footer",defaultString = "]") String var8, @PluginAttribute(value = "charset",defaultString = "UTF-8") Charset var9, @PluginAttribute(value = "includeStacktrace",defaultBoolean = true) boolean var10) {
      boolean var11 = var2 && var3;
      return new JsonLayout(var0, var1, var2, var11, var4, var5, var6, var7, var8, var9, var10);
   }

   @PluginBuilderFactory
   public static <B extends JsonLayout.Builder<B>> B newBuilder() {
      return (JsonLayout.Builder)(new JsonLayout.Builder()).asBuilder();
   }

   public static JsonLayout createDefaultLayout() {
      return new JsonLayout(new DefaultConfiguration(), false, false, false, false, false, false, "[", "]", StandardCharsets.UTF_8, true);
   }

   public void toSerializable(LogEvent var1, Writer var2) throws IOException {
      if (this.complete && this.eventCount > 0L) {
         var2.append(", ");
      }

      super.toSerializable(var1, var2);
   }

   public static class Builder<B extends JsonLayout.Builder<B>> extends AbstractJacksonLayout.Builder<B> implements org.apache.logging.log4j.core.util.Builder<JsonLayout> {
      @PluginBuilderAttribute
      private boolean locationInfo;
      @PluginBuilderAttribute
      private boolean properties;
      @PluginBuilderAttribute
      private boolean propertiesAsList;
      @PluginBuilderAttribute
      private boolean includeStacktrace = true;

      public Builder() {
         super();
         this.setCharset(StandardCharsets.UTF_8);
      }

      public JsonLayout build() {
         boolean var1 = this.properties && this.propertiesAsList;
         String var2 = this.toStringOrNull(this.getHeader());
         String var3 = this.toStringOrNull(this.getFooter());
         return new JsonLayout(this.getConfiguration(), this.locationInfo, this.properties, var1, this.isComplete(), this.isCompact(), this.getEventEol(), var2, var3, this.getCharset(), this.includeStacktrace);
      }

      private String toStringOrNull(byte[] var1) {
         return var1 == null ? null : new String(var1, Charset.defaultCharset());
      }

      public boolean isLocationInfo() {
         return this.locationInfo;
      }

      public boolean isProperties() {
         return this.properties;
      }

      public boolean isPropertiesAsList() {
         return this.propertiesAsList;
      }

      public boolean isIncludeStacktrace() {
         return this.includeStacktrace;
      }

      public B setLocationInfo(boolean var1) {
         this.locationInfo = var1;
         return (JsonLayout.Builder)this.asBuilder();
      }

      public B setProperties(boolean var1) {
         this.properties = var1;
         return (JsonLayout.Builder)this.asBuilder();
      }

      public B setPropertiesAsList(boolean var1) {
         this.propertiesAsList = var1;
         return (JsonLayout.Builder)this.asBuilder();
      }

      public B setIncludeStacktrace(boolean var1) {
         this.includeStacktrace = var1;
         return (JsonLayout.Builder)this.asBuilder();
      }
   }
}
