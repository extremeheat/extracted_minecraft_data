package org.apache.logging.log4j.core.appender.rewrite;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(
   name = "Rewrite",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RewriteAppender extends AbstractAppender {
   private final Configuration config;
   private final ConcurrentMap<String, AppenderControl> appenders = new ConcurrentHashMap();
   private final RewritePolicy rewritePolicy;
   private final AppenderRef[] appenderRefs;

   private RewriteAppender(String var1, Filter var2, boolean var3, AppenderRef[] var4, RewritePolicy var5, Configuration var6) {
      super(var1, var2, (Layout)null, var3);
      this.config = var6;
      this.rewritePolicy = var5;
      this.appenderRefs = var4;
   }

   public void start() {
      AppenderRef[] var1 = this.appenderRefs;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         AppenderRef var4 = var1[var3];
         String var5 = var4.getRef();
         Appender var6 = this.config.getAppender(var5);
         if (var6 != null) {
            Filter var7 = var6 instanceof AbstractAppender ? ((AbstractAppender)var6).getFilter() : null;
            this.appenders.put(var5, new AppenderControl(var6, var4.getLevel(), var7));
         } else {
            LOGGER.error("Appender " + var4 + " cannot be located. Reference ignored");
         }
      }

      super.start();
   }

   public void append(LogEvent var1) {
      if (this.rewritePolicy != null) {
         var1 = this.rewritePolicy.rewrite(var1);
      }

      Iterator var2 = this.appenders.values().iterator();

      while(var2.hasNext()) {
         AppenderControl var3 = (AppenderControl)var2.next();
         var3.callAppender(var1);
      }

   }

   @PluginFactory
   public static RewriteAppender createAppender(@PluginAttribute("name") String var0, @PluginAttribute("ignoreExceptions") String var1, @PluginElement("AppenderRef") AppenderRef[] var2, @PluginConfiguration Configuration var3, @PluginElement("RewritePolicy") RewritePolicy var4, @PluginElement("Filter") Filter var5) {
      boolean var6 = Booleans.parseBoolean(var1, true);
      if (var0 == null) {
         LOGGER.error("No name provided for RewriteAppender");
         return null;
      } else if (var2 == null) {
         LOGGER.error("No appender references defined for RewriteAppender");
         return null;
      } else {
         return new RewriteAppender(var0, var5, var6, var2, var4, var3);
      }
   }
}
