package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements SetMultimap<K, V> {
   private static final long serialVersionUID = 7431625294878419160L;

   protected AbstractSetMultimap(Map<K, Collection<V>> var1) {
      super(var1);
   }

   abstract Set<V> createCollection();

   Set<V> createUnmodifiableEmptyCollection() {
      return ImmutableSet.of();
   }

   public Set<V> get(@Nullable K var1) {
      return (Set)super.get(var1);
   }

   public Set<Entry<K, V>> entries() {
      return (Set)super.entries();
   }

   @CanIgnoreReturnValue
   public Set<V> removeAll(@Nullable Object var1) {
      return (Set)super.removeAll(var1);
   }

   @CanIgnoreReturnValue
   public Set<V> replaceValues(@Nullable K var1, Iterable<? extends V> var2) {
      return (Set)super.replaceValues(var1, var2);
   }

   public Map<K, Collection<V>> asMap() {
      return super.asMap();
   }

   @CanIgnoreReturnValue
   public boolean put(@Nullable K var1, @Nullable V var2) {
      return super.put(var1, var2);
   }

   public boolean equals(@Nullable Object var1) {
      return super.equals(var1);
   }
}
