package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;

public class DefaultAsyncQueueFullPolicy implements AsyncQueueFullPolicy {
   public DefaultAsyncQueueFullPolicy() {
      super();
   }

   public EventRoute getRoute(long var1, Level var3) {
      return EventRoute.SYNCHRONOUS;
   }
}
