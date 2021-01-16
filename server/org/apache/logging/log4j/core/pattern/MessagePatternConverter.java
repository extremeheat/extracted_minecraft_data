package org.apache.logging.log4j.core.pattern;

import java.util.Locale;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.ArrayUtils;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MultiformatMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@Plugin(
   name = "MessagePatternConverter",
   category = "Converter"
)
@ConverterKeys({"m", "msg", "message"})
@PerformanceSensitive({"allocation"})
public final class MessagePatternConverter extends LogEventPatternConverter {
   private static final String NOLOOKUPS = "nolookups";
   private final String[] formats;
   private final Configuration config;
   private final TextRenderer textRenderer;
   private final boolean noLookups;

   private MessagePatternConverter(Configuration var1, String[] var2) {
      super("Message", "message");
      this.formats = var2;
      this.config = var1;
      int var3 = this.loadNoLookups(var2);
      this.noLookups = var3 >= 0;
      this.textRenderer = this.loadMessageRenderer(var3 >= 0 ? (String[])ArrayUtils.remove((Object[])var2, var3) : var2);
   }

   private int loadNoLookups(String[] var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            String var3 = var1[var2];
            if ("nolookups".equalsIgnoreCase(var3)) {
               return var2;
            }
         }
      }

      return -1;
   }

   private TextRenderer loadMessageRenderer(String[] var1) {
      if (var1 != null) {
         String[] var2 = var1;
         int var3 = var1.length;
         int var4 = 0;

         while(var4 < var3) {
            String var5 = var2[var4];
            String var6 = var5.toUpperCase(Locale.ROOT);
            byte var7 = -1;
            switch(var6.hashCode()) {
            case 2014019:
               if (var6.equals("ANSI")) {
                  var7 = 0;
               }
               break;
            case 2228139:
               if (var6.equals("HTML")) {
                  var7 = 1;
               }
            }

            switch(var7) {
            case 0:
               if (Loader.isJansiAvailable()) {
                  return new JAnsiTextRenderer(var1, JAnsiTextRenderer.DefaultMessageStyleMap);
               }

               StatusLogger.getLogger().warn("You requested ANSI message rendering but JANSI is not on the classpath.");
               return null;
            case 1:
               return new HtmlTextRenderer(var1);
            default:
               ++var4;
            }
         }
      }

      return null;
   }

   public static MessagePatternConverter newInstance(Configuration var0, String[] var1) {
      return new MessagePatternConverter(var0, var1);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      Message var3 = var1.getMessage();
      if (var3 instanceof StringBuilderFormattable) {
         boolean var10 = this.textRenderer != null;
         StringBuilder var5 = var10 ? new StringBuilder(80) : var2;
         StringBuilderFormattable var6 = (StringBuilderFormattable)var3;
         int var7 = var5.length();
         var6.formatTo(var5);
         if (this.config != null && !this.noLookups) {
            for(int var8 = var7; var8 < var5.length() - 1; ++var8) {
               if (var5.charAt(var8) == '$' && var5.charAt(var8 + 1) == '{') {
                  String var9 = var5.substring(var7, var5.length());
                  var5.setLength(var7);
                  var5.append(this.config.getStrSubstitutor().replace(var1, var9));
               }
            }
         }

         if (var10) {
            this.textRenderer.render(var5, var2);
         }

      } else {
         if (var3 != null) {
            String var4;
            if (var3 instanceof MultiformatMessage) {
               var4 = ((MultiformatMessage)var3).getFormattedMessage(this.formats);
            } else {
               var4 = var3.getFormattedMessage();
            }

            if (var4 != null) {
               var2.append(this.config != null && var4.contains("${") ? this.config.getStrSubstitutor().replace(var1, var4) : var4);
            } else {
               var2.append("null");
            }
         }

      }
   }
}
