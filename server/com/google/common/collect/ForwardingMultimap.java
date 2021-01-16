package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMultimap<K, V> extends ForwardingObject implements Multimap<K, V> {
   protected ForwardingMultimap() {
      super();
   }

   protected abstract Multimap<K, V> delegate();

   public Map<K, Collection<V>> asMap() {
      return this.delegate().asMap();
   }

   public void clear() {
      this.delegate().clear();
   }

   public boolean containsEntry(@Nullable Object var1, @Nullable Object var2) {
      return this.delegate().containsEntry(var1, var2);
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.delegate().containsKey(var1);
   }

   public boolean containsValue(@Nullable Object var1) {
      return this.delegate().containsValue(var1);
   }

   public Collection<Entry<K, V>> entries() {
      return this.delegate().entries();
   }

   public Collection<V> get(@Nullable K var1) {
      return this.delegate().get(var1);
   }

   public boolean isEmpty() {
      return this.delegate().isEmpty();
   }

   public Multiset<K> keys() {
      return this.delegate().keys();
   }

   public Set<K> keySet() {
      return this.delegate().keySet();
   }

   @CanIgnoreReturnValue
   public boolean put(K var1, V var2) {
      return this.delegate().put(var1, var2);
   }

   @CanIgnoreReturnValue
   public boolean putAll(K var1, Iterable<? extends V> var2) {
      return this.delegate().putAll(var1, var2);
   }

   @CanIgnoreReturnValue
   public boolean putAll(Multimap<? extends K, ? extends V> var1) {
      return this.delegate().putAll(var1);
   }

   @CanIgnoreReturnValue
   public boolean remove(@Nullable Object var1, @Nullable Object var2) {
      return this.delegate().remove(var1, var2);
   }

   @CanIgnoreReturnValue
   public Collection<V> removeAll(@Nullable Object var1) {
      return this.delegate().removeAll(var1);
   }

   @CanIgnoreReturnValue
   public Collection<V> replaceValues(K var1, Iterable<? extends V> var2) {
      return this.delegate().replaceValues(var1, var2);
   }

   public int size() {
      return this.delegate().size();
   }

   public Collection<V> values() {
      return this.delegate().values();
   }

   public boolean equals(@Nullable Object var1) {
      return var1 == this || this.delegate().equals(var1);
   }

   public int hashCode() {
      return this.delegate().hashCode();
   }
}
