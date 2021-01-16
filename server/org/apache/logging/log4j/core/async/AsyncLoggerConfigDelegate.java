package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.jmx.RingBufferAdmin;

public interface AsyncLoggerConfigDelegate {
   RingBufferAdmin createRingBufferAdmin(String var1, String var2);

   EventRoute getEventRoute(Level var1);

   void enqueueEvent(LogEvent var1, AsyncLoggerConfig var2);

   boolean tryEnqueue(LogEvent var1, AsyncLoggerConfig var2);

   void setLogEventFactory(LogEventFactory var1);
}
