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
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "YamlLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class YamlLayout extends AbstractJacksonLayout {
   private static final String DEFAULT_FOOTER = "";
   private static final String DEFAULT_HEADER = "";
   static final String CONTENT_TYPE = "application/yaml";

   protected YamlLayout(Configuration var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, String var7, String var8, Charset var9, boolean var10) {
      super(var1, (new JacksonFactory.YAML(var10)).newWriter(var2, var3, var5), var9, var5, var4, var6, PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var7).setDefaultPattern("").build(), PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var8).setDefaultPattern("").build());
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
      return "application/yaml; charset=" + this.getCharset();
   }

   @PluginFactory
   public static AbstractJacksonLayout createLayout(@PluginConfiguration Configuration var0, @PluginAttribute("locationInfo") boolean var1, @PluginAttribute("properties") boolean var2, @PluginAttribute(value = "header",defaultString = "") String var3, @PluginAttribute(value = "footer",defaultString = "") String var4, @PluginAttribute(value = "charset",defaultString = "UTF-8") Charset var5, @PluginAttribute(value = "includeStacktrace",defaultBoolean = true) boolean var6) {
      return new YamlLayout(var0, var1, var2, false, false, true, var3, var4, var5, var6);
   }

   public static AbstractJacksonLayout createDefaultLayout() {
      return new YamlLayout(new DefaultConfiguration(), false, false, false, false, false, "", "", StandardCharsets.UTF_8, true);
   }

   public void toSerializable(LogEvent var1, Writer var2) throws IOException {
      if (this.complete && this.eventCount > 0L) {
         var2.append(", ");
      }

      super.toSerializable(var1, var2);
   }
}
