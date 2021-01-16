package org.apache.logging.log4j.core.appender.rewrite;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.util.KeyValuePair;

@Plugin(
   name = "LoggerNameLevelRewritePolicy",
   category = "Core",
   elementType = "rewritePolicy",
   printObject = true
)
public class LoggerNameLevelRewritePolicy implements RewritePolicy {
   private final String loggerName;
   private final Map<Level, Level> map;

   @PluginFactory
   public static LoggerNameLevelRewritePolicy createPolicy(@PluginAttribute("logger") String var0, @PluginElement("KeyValuePair") KeyValuePair[] var1) {
      HashMap var2 = new HashMap(var1.length);
      KeyValuePair[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         KeyValuePair var6 = var3[var5];
         var2.put(getLevel(var6.getKey()), getLevel(var6.getValue()));
      }

      return new LoggerNameLevelRewritePolicy(var0, var2);
   }

   private static Level getLevel(String var0) {
      return Level.getLevel(var0.toUpperCase(Locale.ROOT));
   }

   private LoggerNameLevelRewritePolicy(String var1, Map<Level, Level> var2) {
      super();
      this.loggerName = var1;
      this.map = var2;
   }

   public LogEvent rewrite(LogEvent var1) {
      if (!var1.getLoggerName().startsWith(this.loggerName)) {
         return var1;
      } else {
         Level var2 = var1.getLevel();
         Level var3 = (Level)this.map.get(var2);
         if (var3 != null && var3 != var2) {
            Log4jLogEvent var4 = (new Log4jLogEvent.Builder(var1)).setLevel(var3).build();
            return var4;
         } else {
            return var1;
         }
      }
   }
}
