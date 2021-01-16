package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
abstract class CollectionFuture<V, C> extends AggregateFuture<V, C> {
   CollectionFuture() {
      super();
   }

   static final class ListFuture<V> extends CollectionFuture<V, List<V>> {
      ListFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> var1, boolean var2) {
         super();
         this.init(new CollectionFuture.ListFuture.ListFutureRunningState(var1, var2));
      }

      private final class ListFutureRunningState extends CollectionFuture<V, List<V>>.CollectionFutureRunningState {
         ListFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> var2, boolean var3) {
            super(var2, var3);
         }

         public List<V> combine(List<Optional<V>> var1) {
            ArrayList var2 = Lists.newArrayListWithCapacity(var1.size());
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
               Optional var4 = (Optional)var3.next();
               var2.add(var4 != null ? var4.orNull() : null);
            }

            return Collections.unmodifiableList(var2);
         }
      }
   }

   abstract class CollectionFutureRunningState extends AggregateFuture<V, C>.RunningState {
      private List<Optional<V>> values;

      CollectionFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> var2, boolean var3) {
         super(var2, var3, true);
         this.values = (List)(var2.isEmpty() ? ImmutableList.of() : Lists.newArrayListWithCapacity(var2.size()));

         for(int var4 = 0; var4 < var2.size(); ++var4) {
            this.values.add((Object)null);
         }

      }

      final void collectOneValue(boolean var1, int var2, @Nullable V var3) {
         List var4 = this.values;
         if (var4 != null) {
            var4.set(var2, Optional.fromNullable(var3));
         } else {
            Preconditions.checkState(var1 || CollectionFuture.this.isCancelled(), "Future was done before all dependencies completed");
         }

      }

      final void handleAllCompleted() {
         List var1 = this.values;
         if (var1 != null) {
            CollectionFuture.this.set(this.combine(var1));
         } else {
            Preconditions.checkState(CollectionFuture.this.isDone());
         }

      }

      void releaseResourcesAfterFailure() {
         super.releaseResourcesAfterFailure();
         this.values = null;
      }

      abstract C combine(List<Optional<V>> var1);
   }
}
