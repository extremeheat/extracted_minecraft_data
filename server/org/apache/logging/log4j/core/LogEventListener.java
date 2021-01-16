package org.apache.logging.log4j.core;

import java.util.EventListener;
import org.apache.logging.log4j.status.StatusLogger;

public class LogEventListener implements EventListener {
   protected static final StatusLogger LOGGER = StatusLogger.getLogger();
   private final LoggerContext context = LoggerContext.getContext(false);

   protected LogEventListener() {
      super();
   }

   public void log(LogEvent var1) {
      if (var1 != null) {
         Logger var2 = this.context.getLogger(var1.getLoggerName());
         if (var2.privateConfig.filter(var1.getLevel(), var1.getMarker(), var1.getMessage(), var1.getThrown())) {
            var2.privateConfig.logEvent(var1);
         }

      }
   }
}
