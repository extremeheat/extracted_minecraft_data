package org.apache.logging.log4j.core.layout;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "CsvParameterLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public class CsvParameterLayout extends AbstractCsvLayout {
   public static AbstractCsvLayout createDefaultLayout() {
      return new CsvParameterLayout((Configuration)null, Charset.forName("UTF-8"), CSVFormat.valueOf("Default"), (String)null, (String)null);
   }

   public static AbstractCsvLayout createLayout(CSVFormat var0) {
      return new CsvParameterLayout((Configuration)null, Charset.forName("UTF-8"), var0, (String)null, (String)null);
   }

   @PluginFactory
   public static AbstractCsvLayout createLayout(@PluginConfiguration Configuration var0, @PluginAttribute(value = "format",defaultString = "Default") String var1, @PluginAttribute("delimiter") Character var2, @PluginAttribute("escape") Character var3, @PluginAttribute("quote") Character var4, @PluginAttribute("quoteMode") QuoteMode var5, @PluginAttribute("nullString") String var6, @PluginAttribute("recordSeparator") String var7, @PluginAttribute(value = "charset",defaultString = "UTF-8") Charset var8, @PluginAttribute("header") String var9, @PluginAttribute("footer") String var10) {
      CSVFormat var11 = createFormat(var1, var2, var3, var4, var5, var6, var7);
      return new CsvParameterLayout(var0, var8, var11, var9, var10);
   }

   public CsvParameterLayout(Configuration var1, Charset var2, CSVFormat var3, String var4, String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public String toSerializable(LogEvent var1) {
      Message var2 = var1.getMessage();
      Object[] var3 = var2.getParameters();
      StringBuilder var4 = getStringBuilder();

      try {
         this.getFormat().printRecord(var4, var3);
         return var4.toString();
      } catch (IOException var6) {
         StatusLogger.getLogger().error(var2, var6);
         return this.getFormat().getCommentMarker() + " " + var6;
      }
   }
}
