package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
final class FilteredKeyListMultimap<K, V> extends FilteredKeyMultimap<K, V> implements ListMultimap<K, V> {
   FilteredKeyListMultimap(ListMultimap<K, V> var1, Predicate<? super K> var2) {
      super(var1, var2);
   }

   public ListMultimap<K, V> unfiltered() {
      return (ListMultimap)super.unfiltered();
   }

   public List<V> get(K var1) {
      return (List)super.get(var1);
   }

   public List<V> removeAll(@Nullable Object var1) {
      return (List)super.removeAll(var1);
   }

   public List<V> replaceValues(K var1, Iterable<? extends V> var2) {
      return (List)super.replaceValues(var1, var2);
   }
}
