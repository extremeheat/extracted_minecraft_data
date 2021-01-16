package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class LongCollections {
   private static final LongObjectMap<Object> EMPTY_MAP = new LongCollections.EmptyMap();

   private LongCollections() {
      super();
   }

   public static <V> LongObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> LongObjectMap<V> unmodifiableMap(LongObjectMap<V> var0) {
      return new LongCollections.UnmodifiableMap(var0);
   }

   private static final class UnmodifiableMap<V> implements LongObjectMap<V> {
      private final LongObjectMap<V> map;
      private Set<Long> keySet;
      private Set<Entry<Long, V>> entrySet;
      private Collection<V> values;
      private Iterable<LongObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(LongObjectMap<V> var1) {
         super();
         this.map = var1;
      }

      public V get(long var1) {
         return this.map.get(var1);
      }

      public V put(long var1, V var3) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(long var1) {
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

      public boolean containsKey(long var1) {
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

      public V put(Long var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(Object var1) {
         throw new UnsupportedOperationException("remove");
      }

      public void putAll(Map<? extends Long, ? extends V> var1) {
         throw new UnsupportedOperationException("putAll");
      }

      public Iterable<LongObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<LongObjectMap.PrimitiveEntry<V>>() {
               public Iterator<LongObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      public Set<Long> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      public Set<Entry<Long, V>> entrySet() {
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

      private class EntryImpl implements LongObjectMap.PrimitiveEntry<V> {
         private final LongObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(LongObjectMap.PrimitiveEntry<V> var2) {
            super();
            this.entry = var2;
         }

         public long key() {
            return this.entry.key();
         }

         public V value() {
            return this.entry.value();
         }

         public void setValue(V var1) {
            throw new UnsupportedOperationException("setValue");
         }
      }

      private class IteratorImpl implements Iterator<LongObjectMap.PrimitiveEntry<V>> {
         final Iterator<LongObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<LongObjectMap.PrimitiveEntry<V>> var2) {
            super();
            this.iter = var2;
         }

         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public LongObjectMap.PrimitiveEntry<V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return UnmodifiableMap.this.new EntryImpl((LongObjectMap.PrimitiveEntry)this.iter.next());
            }
         }

         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      }
   }

   private static final class EmptyMap implements LongObjectMap<Object> {
      private EmptyMap() {
         super();
      }

      public Object get(long var1) {
         return null;
      }

      public Object put(long var1, Object var3) {
         throw new UnsupportedOperationException("put");
      }

      public Object remove(long var1) {
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

      public Set<Long> keySet() {
         return Collections.emptySet();
      }

      public boolean containsKey(long var1) {
         return false;
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public Iterable<LongObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      public Object get(Object var1) {
         return null;
      }

      public Object put(Long var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public Object remove(Object var1) {
         return null;
      }

      public void putAll(Map<? extends Long, ?> var1) {
         throw new UnsupportedOperationException();
      }

      public Collection<Object> values() {
         return Collections.emptyList();
      }

      public Set<Entry<Long, Object>> entrySet() {
         return Collections.emptySet();
      }

      // $FF: synthetic method
      EmptyMap(Object var1) {
         this();
      }
   }
}
