package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSetMultimap<K, V> extends ForwardingMultimap<K, V> implements SetMultimap<K, V> {
   public ForwardingSetMultimap() {
      super();
   }

   protected abstract SetMultimap<K, V> delegate();

   public Set<Entry<K, V>> entries() {
      return this.delegate().entries();
   }

   public Set<V> get(@Nullable K var1) {
      return this.delegate().get(var1);
   }

   @CanIgnoreReturnValue
   public Set<V> removeAll(@Nullable Object var1) {
      return this.delegate().removeAll(var1);
   }

   @CanIgnoreReturnValue
   public Set<V> replaceValues(K var1, Iterable<? extends V> var2) {
      return this.delegate().replaceValues(var1, var2);
   }
}
