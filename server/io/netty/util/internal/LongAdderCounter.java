package io.netty.util.internal;

import java.util.concurrent.atomic.LongAdder;

final class LongAdderCounter extends LongAdder implements LongCounter {
   LongAdderCounter() {
      super();
   }

   public long value() {
      return this.longValue();
   }
}
