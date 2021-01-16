package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class ShortCollections {
   private static final ShortObjectMap<Object> EMPTY_MAP = new ShortCollections.EmptyMap();

   private ShortCollections() {
      super();
   }

   public static <V> ShortObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> ShortObjectMap<V> unmodifiableMap(ShortObjectMap<V> var0) {
      return new ShortCollections.UnmodifiableMap(var0);
   }

   private static final class UnmodifiableMap<V> implements ShortObjectMap<V> {
      private final ShortObjectMap<V> map;
      private Set<Short> keySet;
      private Set<Entry<Short, V>> entrySet;
      private Collection<V> values;
      private Iterable<ShortObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(ShortObjectMap<V> var1) {
         super();
         this.map = var1;
      }

      public V get(short var1) {
         return this.map.get(var1);
      }

      public V put(short var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(short var1) {
         throw new UnsupportedOperationException("remove");
      }

      public int size() {
         return this.map.size();
      }

      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      public void clear() {
         throw new UnsupportedOperationException("clear");
      }

      public boolean containsKey(short var1) {
         return this.map.containsKey(var1);
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public boolean containsKey(Object var1) {
         return this.map.containsKey(var1);
      }

      public V get(Object var1) {
         return this.map.get(var1);
      }

      public V put(Short var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(Object var1) {
         throw new UnsupportedOperationException("remove");
      }

      public void putAll(Map<? extends Short, ? extends V> var1) {
         throw new UnsupportedOperationException("putAll");
      }

      public Iterable<ShortObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<ShortObjectMap.PrimitiveEntry<V>>() {
               public Iterator<ShortObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      public Set<Short> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      public Set<Entry<Short, V>> entrySet() {
         if (this.entrySet == null) {
            this.entrySet = Collections.unmodifiableSet(this.map.entrySet());
         }

         return this.entrySet;
      }

      public Collection<V> values() {
         if (this.values == null) {
            this.values = Collections.unmodifiableCollection(this.map.values());
         }

         return this.values;
      }

      private class EntryImpl implements ShortObjectMap.PrimitiveEntry<V> {
         private final ShortObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(ShortObjectMap.PrimitiveEntry<V> var2) {
            super();
            this.entry = var2;
         }

         public short key() {
            return this.entry.key();
         }

         public V value() {
            return this.entry.value();
         }

         public void setValue(V var1) {
            throw new UnsupportedOperationException("setValue");
         }
      }

      private class IteratorImpl implements Iterator<ShortObjectMap.PrimitiveEntry<V>> {
         final Iterator<ShortObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<ShortObjectMap.PrimitiveEntry<V>> var2) {
            super();
            this.iter = var2;
         }

         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public ShortObjectMap.PrimitiveEntry<V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return UnmodifiableMap.this.new EntryImpl((ShortObjectMap.PrimitiveEntry)this.iter.next());
            }
         }

         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      }
   }

   private static final class EmptyMap implements ShortObjectMap<Object> {
      private EmptyMap() {
         super();
      }

      public Object get(short var1) {
         return null;
      }

      public Object put(short var1, Object var2) {
         throw new UnsupportedOperationException("put");
      }

      public Object remove(short var1) {
         return null;
      }

      public int size() {
         return 0;
      }

      public boolean isEmpty() {
         return true;
      }

      public boolean containsKey(Object var1) {
         return false;
      }

      public void clear() {
      }

      public Set<Short> keySet() {
         return Collections.emptySet();
      }

      public boolean containsKey(short var1) {
         return false;
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public Iterable<ShortObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      public Object get(Object var1) {
         return null;
      }

      public Object put(Short var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public Object remove(Object var1) {
         return null;
      }

      public void putAll(Map<? extends Short, ?> var1) {
         throw new UnsupportedOperationException();
      }

      public Collection<Object> values() {
         return Collections.emptyList();
      }

      public Set<Entry<Short, Object>> entrySet() {
         return Collections.emptySet();
      }

      // $FF: synthetic method
      EmptyMap(Object var1) {
         this();
      }
   }
}
