package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedSetMultimap<K, V> extends ForwardingSetMultimap<K, V> implements SortedSetMultimap<K, V> {
   protected ForwardingSortedSetMultimap() {
      super();
   }

   protected abstract SortedSetMultimap<K, V> delegate();

   public SortedSet<V> get(@Nullable K var1) {
      return this.delegate().get(var1);
   }

   public SortedSet<V> removeAll(@Nullable Object var1) {
      return this.delegate().removeAll(var1);
   }

   public SortedSet<V> replaceValues(K var1, Iterable<? extends V> var2) {
      return this.delegate().replaceValues(var1, var2);
   }

   public Comparator<? super V> valueComparator() {
      return this.delegate().valueComparator();
   }
}
