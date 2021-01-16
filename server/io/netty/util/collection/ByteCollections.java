package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class ByteCollections {
   private static final ByteObjectMap<Object> EMPTY_MAP = new ByteCollections.EmptyMap();

   private ByteCollections() {
      super();
   }

   public static <V> ByteObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> ByteObjectMap<V> unmodifiableMap(ByteObjectMap<V> var0) {
      return new ByteCollections.UnmodifiableMap(var0);
   }

   private static final class UnmodifiableMap<V> implements ByteObjectMap<V> {
      private final ByteObjectMap<V> map;
      private Set<Byte> keySet;
      private Set<Entry<Byte, V>> entrySet;
      private Collection<V> values;
      private Iterable<ByteObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(ByteObjectMap<V> var1) {
         super();
         this.map = var1;
      }

      public V get(byte var1) {
         return this.map.get(var1);
      }

      public V put(byte var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(byte var1) {
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

      public boolean containsKey(byte var1) {
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

      public V put(Byte var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(Object var1) {
         throw new UnsupportedOperationException("remove");
      }

      public void putAll(Map<? extends Byte, ? extends V> var1) {
         throw new UnsupportedOperationException("putAll");
      }

      public Iterable<ByteObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<ByteObjectMap.PrimitiveEntry<V>>() {
               public Iterator<ByteObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      public Set<Byte> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      public Set<Entry<Byte, V>> entrySet() {
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

      private class EntryImpl implements ByteObjectMap.PrimitiveEntry<V> {
         private final ByteObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(ByteObjectMap.PrimitiveEntry<V> var2) {
            super();
            this.entry = var2;
         }

         public byte key() {
            return this.entry.key();
         }

         public V value() {
            return this.entry.value();
         }

         public void setValue(V var1) {
            throw new UnsupportedOperationException("setValue");
         }
      }

      private class IteratorImpl implements Iterator<ByteObjectMap.PrimitiveEntry<V>> {
         final Iterator<ByteObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<ByteObjectMap.PrimitiveEntry<V>> var2) {
            super();
            this.iter = var2;
         }

         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public ByteObjectMap.PrimitiveEntry<V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return UnmodifiableMap.this.new EntryImpl((ByteObjectMap.PrimitiveEntry)this.iter.next());
            }
         }

         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      }
   }

   private static final class EmptyMap implements ByteObjectMap<Object> {
      private EmptyMap() {
         super();
      }

      public Object get(byte var1) {
         return null;
      }

      public Object put(byte var1, Object var2) {
         throw new UnsupportedOperationException("put");
      }

      public Object remove(byte var1) {
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

      public Set<Byte> keySet() {
         return Collections.emptySet();
      }

      public boolean containsKey(byte var1) {
         return false;
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public Iterable<ByteObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      public Object get(Object var1) {
         return null;
      }

      public Object put(Byte var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public Object remove(Object var1) {
         return null;
      }

      public void putAll(Map<? extends Byte, ?> var1) {
         throw new UnsupportedOperationException();
      }

      public Collection<Object> values() {
         return Collections.emptyList();
      }

      public Set<Entry<Byte, Object>> entrySet() {
         return Collections.emptySet();
      }

      // $FF: synthetic method
      EmptyMap(Object var1) {
         this();
      }
   }
}
