package io.netty.util.concurrent;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/** @deprecated */
@Deprecated
public class PromiseAggregator<V, F extends Future<V>> implements GenericFutureListener<F> {
   private final Promise<?> aggregatePromise;
   private final boolean failPending;
   private Set<Promise<V>> pendingPromises;

   public PromiseAggregator(Promise<Void> var1, boolean var2) {
      super();
      if (var1 == null) {
         throw new NullPointerException("aggregatePromise");
      } else {
         this.aggregatePromise = var1;
         this.failPending = var2;
      }
   }

   public PromiseAggregator(Promise<Void> var1) {
      this(var1, true);
   }

   @SafeVarargs
   public final PromiseAggregator<V, F> add(Promise<V>... var1) {
      if (var1 == null) {
         throw new NullPointerException("promises");
      } else if (var1.length == 0) {
         return this;
      } else {
         synchronized(this) {
            if (this.pendingPromises == null) {
               int var3;
               if (var1.length > 1) {
                  var3 = var1.length;
               } else {
                  var3 = 2;
               }

               this.pendingPromises = new LinkedHashSet(var3);
            }

            Promise[] var9 = var1;
            int var4 = var1.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Promise var6 = var9[var5];
               if (var6 != null) {
                  this.pendingPromises.add(var6);
                  var6.addListener(this);
               }
            }

            return this;
         }
      }
   }

   public synchronized void operationComplete(F var1) throws Exception {
      if (this.pendingPromises == null) {
         this.aggregatePromise.setSuccess((Object)null);
      } else {
         this.pendingPromises.remove(var1);
         if (!var1.isSuccess()) {
            Throwable var2 = var1.cause();
            this.aggregatePromise.setFailure(var2);
            if (this.failPending) {
               Iterator var3 = this.pendingPromises.iterator();

               while(var3.hasNext()) {
                  Promise var4 = (Promise)var3.next();
                  var4.setFailure(var2);
               }
            }
         } else if (this.pendingPromises.isEmpty()) {
            this.aggregatePromise.setSuccess((Object)null);
         }
      }

   }
}
