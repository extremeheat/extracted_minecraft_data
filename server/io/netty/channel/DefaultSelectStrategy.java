package io.netty.channel;

import io.netty.util.IntSupplier;

final class DefaultSelectStrategy implements SelectStrategy {
   static final SelectStrategy INSTANCE = new DefaultSelectStrategy();

   private DefaultSelectStrategy() {
      super();
   }

   public int calculateStrategy(IntSupplier var1, boolean var2) throws Exception {
      return var2 ? var1.get() : -1;
   }
}
