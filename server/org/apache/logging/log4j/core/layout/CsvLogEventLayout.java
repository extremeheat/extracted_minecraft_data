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
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "CsvLogEventLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public class CsvLogEventLayout extends AbstractCsvLayout {
   public static CsvLogEventLayout createDefaultLayout() {
      return new CsvLogEventLayout((Configuration)null, Charset.forName("UTF-8"), CSVFormat.valueOf("Default"), (String)null, (String)null);
   }

   public static CsvLogEventLayout createLayout(CSVFormat var0) {
      return new CsvLogEventLayout((Configuration)null, Charset.forName("UTF-8"), var0, (String)null, (String)null);
   }

   @PluginFactory
   public static CsvLogEventLayout createLayout(@PluginConfiguration Configuration var0, @PluginAttribute(value = "format",defaultString = "Default") String var1, @PluginAttribute("delimiter") Character var2, @PluginAttribute("escape") Character var3, @PluginAttribute("quote") Character var4, @PluginAttribute("quoteMode") QuoteMode var5, @PluginAttribute("nullString") String var6, @PluginAttribute("recordSeparator") String var7, @PluginAttribute(value = "charset",defaultString = "UTF-8") Charset var8, @PluginAttribute("header") String var9, @PluginAttribute("footer") String var10) {
      CSVFormat var11 = createFormat(var1, var2, var3, var4, var5, var6, var7);
      return new CsvLogEventLayout(var0, var8, var11, var9, var10);
   }

   protected CsvLogEventLayout(Configuration var1, Charset var2, CSVFormat var3, String var4, String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public String toSerializable(LogEvent var1) {
      StringBuilder var2 = getStringBuilder();
      CSVFormat var3 = this.getFormat();

      try {
         var3.print(var1.getNanoTime(), var2, true);
         var3.print(var1.getTimeMillis(), var2, false);
         var3.print(var1.getLevel(), var2, false);
         var3.print(var1.getThreadId(), var2, false);
         var3.print(var1.getThreadName(), var2, false);
         var3.print(var1.getThreadPriority(), var2, false);
         var3.print(var1.getMessage().getFormattedMessage(), var2, false);
         var3.print(var1.getLoggerFqcn(), var2, false);
         var3.print(var1.getLoggerName(), var2, false);
         var3.print(var1.getMarker(), var2, false);
         var3.print(var1.getThrownProxy(), var2, false);
         var3.print(var1.getSource(), var2, false);
         var3.print(var1.getContextData(), var2, false);
         var3.print(var1.getContextStack(), var2, false);
         var3.println(var2);
         return var2.toString();
      } catch (IOException var5) {
         StatusLogger.getLogger().error(var1.toString(), var5);
         return var3.getCommentMarker() + " " + var5;
      }
   }
}
