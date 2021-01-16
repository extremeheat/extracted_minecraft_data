package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicLong;

public class ThresholdCircuitBreaker extends AbstractCircuitBreaker<Long> {
   private static final long INITIAL_COUNT = 0L;
   private final long threshold;
   private final AtomicLong used = new AtomicLong(0L);

   public ThresholdCircuitBreaker(long var1) {
      super();
      this.threshold = var1;
   }

   public long getThreshold() {
      return this.threshold;
   }

   public boolean checkState() throws CircuitBreakingException {
      return this.isOpen();
   }

   public void close() {
      super.close();
      this.used.set(0L);
   }

   public boolean incrementAndCheckState(Long var1) throws CircuitBreakingException {
      if (this.threshold == 0L) {
         this.open();
      }

      long var2 = this.used.addAndGet(var1);
      if (var2 > this.threshold) {
         this.open();
      }

      return this.checkState();
   }
}
