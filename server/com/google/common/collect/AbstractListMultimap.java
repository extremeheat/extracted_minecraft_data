package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractListMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements ListMultimap<K, V> {
   private static final long serialVersionUID = 6588350623831699109L;

   protected AbstractListMultimap(Map<K, Collection<V>> var1) {
      super(var1);
   }

   abstract List<V> createCollection();

   List<V> createUnmodifiableEmptyCollection() {
      return ImmutableList.of();
   }

   public List<V> get(@Nullable K var1) {
      return (List)super.get(var1);
   }

   @CanIgnoreReturnValue
   public List<V> removeAll(@Nullable Object var1) {
      return (List)super.removeAll(var1);
   }

   @CanIgnoreReturnValue
   public List<V> replaceValues(@Nullable K var1, Iterable<? extends V> var2) {
      return (List)super.replaceValues(var1, var2);
   }

   @CanIgnoreReturnValue
   public boolean put(@Nullable K var1, @Nullable V var2) {
      return super.put(var1, var2);
   }

   public Map<K, Collection<V>> asMap() {
      return super.asMap();
   }

   public boolean equals(@Nullable Object var1) {
      return super.equals(var1);
   }
}
