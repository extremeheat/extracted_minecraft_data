package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;

public final class PromiseCombiner {
   private int expectedCount;
   private int doneCount;
   private boolean doneAdding;
   private Promise<Void> aggregatePromise;
   private Throwable cause;
   private final GenericFutureListener<Future<?>> listener = new GenericFutureListener<Future<?>>() {
      public void operationComplete(Future<?> var1) throws Exception {
         ++PromiseCombiner.this.doneCount;
         if (!var1.isSuccess() && PromiseCombiner.this.cause == null) {
            PromiseCombiner.this.cause = var1.cause();
         }

         if (PromiseCombiner.this.doneCount == PromiseCombiner.this.expectedCount && PromiseCombiner.this.doneAdding) {
            PromiseCombiner.this.tryPromise();
         }

      }
   };

   public PromiseCombiner() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public void add(Promise var1) {
      this.add((Future)var1);
   }

   public void add(Future var1) {
      this.checkAddAllowed();
      ++this.expectedCount;
      var1.addListener(this.listener);
   }

   /** @deprecated */
   @Deprecated
   public void addAll(Promise... var1) {
      this.addAll((Future[])var1);
   }

   public void addAll(Future... var1) {
      Future[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Future var5 = var2[var4];
         this.add(var5);
      }

   }

   public void finish(Promise<Void> var1) {
      if (this.doneAdding) {
         throw new IllegalStateException("Already finished");
      } else {
         this.doneAdding = true;
         this.aggregatePromise = (Promise)ObjectUtil.checkNotNull(var1, "aggregatePromise");
         if (this.doneCount == this.expectedCount) {
            this.tryPromise();
         }

      }
   }

   private boolean tryPromise() {
      return this.cause == null ? this.aggregatePromise.trySuccess((Object)null) : this.aggregatePromise.tryFailure(this.cause);
   }

   private void checkAddAllowed() {
      if (this.doneAdding) {
         throw new IllegalStateException("Adding promises is not allowed after finished adding");
      }
   }
}
