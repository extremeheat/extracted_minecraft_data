package org.apache.logging.log4j;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;

public final class EventLogger {
   public static final Marker EVENT_MARKER = MarkerManager.getMarker("EVENT");
   private static final String NAME = "EventLogger";
   private static final String FQCN = EventLogger.class.getName();
   private static final ExtendedLogger LOGGER = LogManager.getContext(false).getLogger("EventLogger");

   private EventLogger() {
      super();
   }

   public static void logEvent(StructuredDataMessage var0) {
      LOGGER.logIfEnabled(FQCN, Level.OFF, EVENT_MARKER, (Message)var0, (Throwable)null);
   }

   public static void logEvent(StructuredDataMessage var0, Level var1) {
      LOGGER.logIfEnabled(FQCN, var1, EVENT_MARKER, (Message)var0, (Throwable)null);
   }
}
