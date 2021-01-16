package org.apache.logging.log4j.core.appender;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "CountingNoOp",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public class CountingNoOpAppender extends AbstractAppender {
   private final AtomicLong total = new AtomicLong();

   public CountingNoOpAppender(String var1, Layout<?> var2) {
      super(var1, (Filter)null, var2);
   }

   public long getCount() {
      return this.total.get();
   }

   public void append(LogEvent var1) {
      this.total.incrementAndGet();
   }

   @PluginFactory
   public static CountingNoOpAppender createAppender(@PluginAttribute("name") String var0) {
      return new CountingNoOpAppender((String)Objects.requireNonNull(var0), (Layout)null);
   }
}
