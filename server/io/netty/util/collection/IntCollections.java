package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class IntCollections {
   private static final IntObjectMap<Object> EMPTY_MAP = new IntCollections.EmptyMap();

   private IntCollections() {
      super();
   }

   public static <V> IntObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> IntObjectMap<V> unmodifiableMap(IntObjectMap<V> var0) {
      return new IntCollections.UnmodifiableMap(var0);
   }

   private static final class UnmodifiableMap<V> implements IntObjectMap<V> {
      private final IntObjectMap<V> map;
      private Set<Integer> keySet;
      private Set<Entry<Integer, V>> entrySet;
      private Collection<V> values;
      private Iterable<IntObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(IntObjectMap<V> var1) {
         super();
         this.map = var1;
      }

      public V get(int var1) {
         return this.map.get(var1);
      }

      public V put(int var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(int var1) {
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

      public boolean containsKey(int var1) {
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

      public V put(Integer var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(Object var1) {
         throw new UnsupportedOperationException("remove");
      }

      public void putAll(Map<? extends Integer, ? extends V> var1) {
         throw new UnsupportedOperationException("putAll");
      }

      public Iterable<IntObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<IntObjectMap.PrimitiveEntry<V>>() {
               public Iterator<IntObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      public Set<Integer> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      public Set<Entry<Integer, V>> entrySet() {
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

      private class EntryImpl implements IntObjectMap.PrimitiveEntry<V> {
         private final IntObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(IntObjectMap.PrimitiveEntry<V> var2) {
            super();
            this.entry = var2;
         }

         public int key() {
            return this.entry.key();
         }

         public V value() {
            return this.entry.value();
         }

         public void setValue(V var1) {
            throw new UnsupportedOperationException("setValue");
         }
      }

      private class IteratorImpl implements Iterator<IntObjectMap.PrimitiveEntry<V>> {
         final Iterator<IntObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<IntObjectMap.PrimitiveEntry<V>> var2) {
            super();
            this.iter = var2;
         }

         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public IntObjectMap.PrimitiveEntry<V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return UnmodifiableMap.this.new EntryImpl((IntObjectMap.PrimitiveEntry)this.iter.next());
            }
         }

         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      }
   }

   private static final class EmptyMap implements IntObjectMap<Object> {
      private EmptyMap() {
         super();
      }

      public Object get(int var1) {
         return null;
      }

      public Object put(int var1, Object var2) {
         throw new UnsupportedOperationException("put");
      }

      public Object remove(int var1) {
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

      public Set<Integer> keySet() {
         return Collections.emptySet();
      }

      public boolean containsKey(int var1) {
         return false;
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public Iterable<IntObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      public Object get(Object var1) {
         return null;
      }

      public Object put(Integer var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public Object remove(Object var1) {
         return null;
      }

      public void putAll(Map<? extends Integer, ?> var1) {
         throw new UnsupportedOperationException();
      }

      public Collection<Object> values() {
         return Collections.emptyList();
      }

      public Set<Entry<Integer, Object>> entrySet() {
         return Collections.emptySet();
      }

      // $FF: synthetic method
      EmptyMap(Object var1) {
         this();
      }
   }
}
