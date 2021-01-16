package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "Null",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public class NullAppender extends AbstractAppender {
   public static final String PLUGIN_NAME = "Null";

   @PluginFactory
   public static NullAppender createAppender(@PluginAttribute("name") String var0) {
      return new NullAppender(var0);
   }

   private NullAppender(String var1) {
      super(var1, (Filter)null, (Layout)null);
   }

   public void append(LogEvent var1) {
   }
}
