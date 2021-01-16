package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMap<K, V> extends ForwardingObject implements Map<K, V> {
   protected ForwardingMap() {
      super();
   }

   protected abstract Map<K, V> delegate();

   public int size() {
      return this.delegate().size();
   }

   public boolean isEmpty() {
      return this.delegate().isEmpty();
   }

   @CanIgnoreReturnValue
   public V remove(Object var1) {
      return this.delegate().remove(var1);
   }

   public void clear() {
      this.delegate().clear();
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.delegate().containsKey(var1);
   }

   public boolean containsValue(@Nullable Object var1) {
      return this.delegate().containsValue(var1);
   }

   public V get(@Nullable Object var1) {
      return this.delegate().get(var1);
   }

   @CanIgnoreReturnValue
   public V put(K var1, V var2) {
      return this.delegate().put(var1, var2);
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      this.delegate().putAll(var1);
   }

   public Set<K> keySet() {
      return this.delegate().keySet();
   }

   public Collection<V> values() {
      return this.delegate().values();
   }

   public Set<Entry<K, V>> entrySet() {
      return this.delegate().entrySet();
   }

   public boolean equals(@Nullable Object var1) {
      return var1 == this || this.delegate().equals(var1);
   }

   public int hashCode() {
      return this.delegate().hashCode();
   }

   protected void standardPutAll(Map<? extends K, ? extends V> var1) {
      Maps.putAllImpl(this, var1);
   }

   @Beta
   protected V standardRemove(@Nullable Object var1) {
      Iterator var2 = this.entrySet().iterator();

      Entry var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Entry)var2.next();
      } while(!Objects.equal(var3.getKey(), var1));

      Object var4 = var3.getValue();
      var2.remove();
      return var4;
   }

   protected void standardClear() {
      Iterators.clear(this.entrySet().iterator());
   }

   @Beta
   protected boolean standardContainsKey(@Nullable Object var1) {
      return Maps.containsKeyImpl(this, var1);
   }

   protected boolean standardContainsValue(@Nullable Object var1) {
      return Maps.containsValueImpl(this, var1);
   }

   protected boolean standardIsEmpty() {
      return !this.entrySet().iterator().hasNext();
   }

   protected boolean standardEquals(@Nullable Object var1) {
      return Maps.equalsImpl(this, var1);
   }

   protected int standardHashCode() {
      return Sets.hashCodeImpl(this.entrySet());
   }

   protected String standardToString() {
      return Maps.toStringImpl(this);
   }

   @Beta
   protected abstract class StandardEntrySet extends Maps.EntrySet<K, V> {
      public StandardEntrySet() {
         super();
      }

      Map<K, V> map() {
         return ForwardingMap.this;
      }
   }

   @Beta
   protected class StandardValues extends Maps.Values<K, V> {
      public StandardValues() {
         super(ForwardingMap.this);
      }
   }

   @Beta
   protected class StandardKeySet extends Maps.KeySet<K, V> {
      public StandardKeySet() {
         super(ForwardingMap.this);
      }
   }
}
