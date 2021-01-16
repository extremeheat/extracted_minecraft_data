package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class CharCollections {
   private static final CharObjectMap<Object> EMPTY_MAP = new CharCollections.EmptyMap();

   private CharCollections() {
      super();
   }

   public static <V> CharObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> CharObjectMap<V> unmodifiableMap(CharObjectMap<V> var0) {
      return new CharCollections.UnmodifiableMap(var0);
   }

   private static final class UnmodifiableMap<V> implements CharObjectMap<V> {
      private final CharObjectMap<V> map;
      private Set<Character> keySet;
      private Set<Entry<Character, V>> entrySet;
      private Collection<V> values;
      private Iterable<CharObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(CharObjectMap<V> var1) {
         super();
         this.map = var1;
      }

      public V get(char var1) {
         return this.map.get(var1);
      }

      public V put(char var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(char var1) {
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

      public boolean containsKey(char var1) {
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

      public V put(Character var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      public V remove(Object var1) {
         throw new UnsupportedOperationException("remove");
      }

      public void putAll(Map<? extends Character, ? extends V> var1) {
         throw new UnsupportedOperationException("putAll");
      }

      public Iterable<CharObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<CharObjectMap.PrimitiveEntry<V>>() {
               public Iterator<CharObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      public Set<Character> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      public Set<Entry<Character, V>> entrySet() {
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

      private class EntryImpl implements CharObjectMap.PrimitiveEntry<V> {
         private final CharObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(CharObjectMap.PrimitiveEntry<V> var2) {
            super();
            this.entry = var2;
         }

         public char key() {
            return this.entry.key();
         }

         public V value() {
            return this.entry.value();
         }

         public void setValue(V var1) {
            throw new UnsupportedOperationException("setValue");
         }
      }

      private class IteratorImpl implements Iterator<CharObjectMap.PrimitiveEntry<V>> {
         final Iterator<CharObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<CharObjectMap.PrimitiveEntry<V>> var2) {
            super();
            this.iter = var2;
         }

         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public CharObjectMap.PrimitiveEntry<V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return UnmodifiableMap.this.new EntryImpl((CharObjectMap.PrimitiveEntry)this.iter.next());
            }
         }

         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      }
   }

   private static final class EmptyMap implements CharObjectMap<Object> {
      private EmptyMap() {
         super();
      }

      public Object get(char var1) {
         return null;
      }

      public Object put(char var1, Object var2) {
         throw new UnsupportedOperationException("put");
      }

      public Object remove(char var1) {
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

      public Set<Character> keySet() {
         return Collections.emptySet();
      }

      public boolean containsKey(char var1) {
         return false;
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public Iterable<CharObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      public Object get(Object var1) {
         return null;
      }

      public Object put(Character var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public Object remove(Object var1) {
         return null;
      }

      public void putAll(Map<? extends Character, ?> var1) {
         throw new UnsupportedOperationException();
      }

      public Collection<Object> values() {
         return Collections.emptyList();
      }

      public Set<Entry<Character, Object>> entrySet() {
         return Collections.emptySet();
      }

      // $FF: synthetic method
      EmptyMap(Object var1) {
         this();
      }
   }
}
