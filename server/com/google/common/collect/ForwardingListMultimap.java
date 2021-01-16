package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingListMultimap<K, V> extends ForwardingMultimap<K, V> implements ListMultimap<K, V> {
   protected ForwardingListMultimap() {
      super();
   }

   protected abstract ListMultimap<K, V> delegate();

   public List<V> get(@Nullable K var1) {
      return this.delegate().get(var1);
   }

   @CanIgnoreReturnValue
   public List<V> removeAll(@Nullable Object var1) {
      return this.delegate().removeAll(var1);
   }

   @CanIgnoreReturnValue
   public List<V> replaceValues(K var1, Iterable<? extends V> var2) {
      return this.delegate().replaceValues(var1, var2);
   }
}
