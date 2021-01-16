package org.apache.logging.log4j.core.async;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public class DiscardingAsyncQueueFullPolicy extends DefaultAsyncQueueFullPolicy {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Level thresholdLevel;
   private final AtomicLong discardCount = new AtomicLong();

   public DiscardingAsyncQueueFullPolicy(Level var1) {
      super();
      this.thresholdLevel = (Level)Objects.requireNonNull(var1, "thresholdLevel");
   }

   public EventRoute getRoute(long var1, Level var3) {
      if (var3.isLessSpecificThan(this.thresholdLevel)) {
         if (this.discardCount.getAndIncrement() == 0L) {
            LOGGER.warn((String)"Async queue is full, discarding event with level {}. This message will only appear once; future events from {} are silently discarded until queue capacity becomes available.", (Object)var3, (Object)this.thresholdLevel);
         }

         return EventRoute.DISCARD;
      } else {
         return super.getRoute(var1, var3);
      }
   }

   public static long getDiscardCount(AsyncQueueFullPolicy var0) {
      return var0 instanceof DiscardingAsyncQueueFullPolicy ? ((DiscardingAsyncQueueFullPolicy)var0).discardCount.get() : 0L;
   }

   public Level getThresholdLevel() {
      return this.thresholdLevel;
   }
}
