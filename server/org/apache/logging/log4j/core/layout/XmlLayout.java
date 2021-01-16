package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "XmlLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class XmlLayout extends AbstractJacksonLayout {
   private static final String ROOT_TAG = "Events";

   protected XmlLayout(boolean var1, boolean var2, boolean var3, boolean var4, Charset var5, boolean var6) {
      super((Configuration)null, (new JacksonFactory.XML(var6)).newWriter(var1, var2, var4), var5, var4, var3, false, (AbstractStringLayout.Serializer)null, (AbstractStringLayout.Serializer)null);
   }

   public byte[] getHeader() {
      if (!this.complete) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append("<?xml version=\"1.0\" encoding=\"");
         var1.append(this.getCharset().name());
         var1.append("\"?>");
         var1.append(this.eol);
         var1.append('<');
         var1.append("Events");
         var1.append(" xmlns=\"http://logging.apache.org/log4j/2.0/events\">");
         var1.append(this.eol);
         return var1.toString().getBytes(this.getCharset());
      }
   }

   public byte[] getFooter() {
      return !this.complete ? null : this.getBytes("</Events>" + this.eol);
   }

   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap();
      var1.put("xsd", "log4j-events.xsd");
      var1.put("version", "2.0");
      return var1;
   }

   public String getContentType() {
      return "text/xml; charset=" + this.getCharset();
   }

   @PluginFactory
   public static XmlLayout createLayout(@PluginAttribute("locationInfo") boolean var0, @PluginAttribute("properties") boolean var1, @PluginAttribute("complete") boolean var2, @PluginAttribute("compact") boolean var3, @PluginAttribute(value = "charset",defaultString = "UTF-8") Charset var4, @PluginAttribute(value = "includeStacktrace",defaultBoolean = true) boolean var5) {
      return new XmlLayout(var0, var1, var2, var3, var4, var5);
   }

   public static XmlLayout createDefaultLayout() {
      return new XmlLayout(false, false, false, false, StandardCharsets.UTF_8, true);
   }
}
