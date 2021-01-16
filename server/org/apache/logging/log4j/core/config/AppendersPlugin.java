package org.apache.logging.log4j.core.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "appenders",
   category = "Core"
)
public final class AppendersPlugin {
   private AppendersPlugin() {
      super();
   }

   @PluginFactory
   public static ConcurrentMap<String, Appender> createAppenders(@PluginElement("Appenders") Appender[] var0) {
      ConcurrentHashMap var1 = new ConcurrentHashMap(var0.length);
      Appender[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Appender var5 = var2[var4];
         var1.put(var5.getName(), var5);
      }

      return var1;
   }
}
