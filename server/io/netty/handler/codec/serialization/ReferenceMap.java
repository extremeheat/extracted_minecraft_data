package io.netty.handler.codec.serialization;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

abstract class ReferenceMap<K, V> implements Map<K, V> {
   private final Map<K, Reference<V>> delegate;

   protected ReferenceMap(Map<K, Reference<V>> var1) {
      super();
      this.delegate = var1;
   }

   abstract Reference<V> fold(V var1);

   private V unfold(Reference<V> var1) {
      return var1 == null ? null : var1.get();
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public boolean containsKey(Object var1) {
      return this.delegate.containsKey(var1);
   }

   public boolean containsValue(Object var1) {
      throw new UnsupportedOperationException();
   }

   public V get(Object var1) {
      return this.unfold((Reference)this.delegate.get(var1));
   }

   public V put(K var1, V var2) {
      return this.unfold((Reference)this.delegate.put(var1, this.fold(var2)));
   }

   public V remove(Object var1) {
      return this.unfold((Reference)this.delegate.remove(var1));
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.delegate.put(var3.getKey(), this.fold(var3.getValue()));
      }

   }

   public void clear() {
      this.delegate.clear();
   }

   public Set<K> keySet() {
      return this.delegate.keySet();
   }

   public Collection<V> values() {
      throw new UnsupportedOperationException();
   }

   public Set<Entry<K, V>> entrySet() {
      throw new UnsupportedOperationException();
   }
}
