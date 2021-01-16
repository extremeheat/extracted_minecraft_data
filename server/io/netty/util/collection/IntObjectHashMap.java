package io.netty.util.collection;

import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class IntObjectHashMap<V> implements IntObjectMap<V> {
   public static final int DEFAULT_CAPACITY = 8;
   public static final float DEFAULT_LOAD_FACTOR = 0.5F;
   private static final Object NULL_VALUE = new Object();
   private int maxSize;
   private final float loadFactor;
   private int[] keys;
   private V[] values;
   private int size;
   private int mask;
   private final Set<Integer> keySet;
   private final Set<Entry<Integer, V>> entrySet;
   private final Iterable<IntObjectMap.PrimitiveEntry<V>> entries;

   public IntObjectHashMap() {
      this(8, 0.5F);
   }

   public IntObjectHashMap(int var1) {
      this(var1, 0.5F);
   }

   public IntObjectHashMap(int var1, float var2) {
      super();
      this.keySet = new IntObjectHashMap.KeySet();
      this.entrySet = new IntObjectHashMap.EntrySet();
      this.entries = new Iterable<IntObjectMap.PrimitiveEntry<V>>() {
         public Iterator<IntObjectMap.PrimitiveEntry<V>> iterator() {
            return IntObjectHashMap.this.new PrimitiveIterator();
         }
      };
      if (var2 > 0.0F && var2 <= 1.0F) {
         this.loadFactor = var2;
         int var3 = MathUtil.safeFindNextPositivePowerOfTwo(var1);
         this.mask = var3 - 1;
         this.keys = new int[var3];
         Object[] var4 = (Object[])(new Object[var3]);
         this.values = var4;
         this.maxSize = this.calcMaxSize(var3);
      } else {
         throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
      }
   }

   private static <T> T toExternal(T var0) {
      assert var0 != null : "null is not a legitimate internal value. Concurrent Modification?";

      return var0 == NULL_VALUE ? null : var0;
   }

   private static <T> T toInternal(T var0) {
      return var0 == null ? NULL_VALUE : var0;
   }

   public V get(int var1) {
      int var2 = this.indexOf(var1);
      return var2 == -1 ? null : toExternal(this.values[var2]);
   }

   public V put(int var1, V var2) {
      int var3 = this.hashIndex(var1);
      int var4 = var3;

      while(this.values[var4] != null) {
         if (this.keys[var4] == var1) {
            Object var5 = this.values[var4];
            this.values[var4] = toInternal(var2);
            return toExternal(var5);
         }

         if ((var4 = this.probeNext(var4)) == var3) {
            throw new IllegalStateException("Unable to insert");
         }
      }

      this.keys[var4] = var1;
      this.values[var4] = toInternal(var2);
      this.growSize();
      return null;
   }

   public void putAll(Map<? extends Integer, ? extends V> var1) {
      if (var1 instanceof IntObjectHashMap) {
         IntObjectHashMap var5 = (IntObjectHashMap)var1;

         for(int var6 = 0; var6 < var5.values.length; ++var6) {
            Object var4 = var5.values[var6];
            if (var4 != null) {
               this.put(var5.keys[var6], var4);
            }
         }

      } else {
         Iterator var2 = var1.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.put((Integer)var3.getKey(), var3.getValue());
         }

      }
   }

   public V remove(int var1) {
      int var2 = this.indexOf(var1);
      if (var2 == -1) {
         return null;
      } else {
         Object var3 = this.values[var2];
         this.removeAt(var2);
         return toExternal(var3);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public void clear() {
      Arrays.fill(this.keys, 0);
      Arrays.fill(this.values, (Object)null);
      this.size = 0;
   }

   public boolean containsKey(int var1) {
      return this.indexOf(var1) >= 0;
   }

   public boolean containsValue(Object var1) {
      Object var2 = toInternal(var1);
      Object[] var3 = this.values;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         if (var6 != null && var6.equals(var2)) {
            return true;
         }
      }

      return false;
   }

   public Iterable<IntObjectMap.PrimitiveEntry<V>> entries() {
      return this.entries;
   }

   public Collection<V> values() {
      return new AbstractCollection<V>() {
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               final IntObjectHashMap<V>.PrimitiveIterator iter = IntObjectHashMap.this.new PrimitiveIterator();

               public boolean hasNext() {
                  return this.iter.hasNext();
               }

               public V next() {
                  return this.iter.next().value();
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }

         public int size() {
            return IntObjectHashMap.this.size;
         }
      };
   }

   public int hashCode() {
      int var1 = this.size;
      int[] var2 = this.keys;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var1 ^= hashCode(var5);
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntObjectMap)) {
         return false;
      } else {
         IntObjectMap var2 = (IntObjectMap)var1;
         if (this.size != var2.size()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.values.length; ++var3) {
               Object var4 = this.values[var3];
               if (var4 != null) {
                  int var5 = this.keys[var3];
                  Object var6 = var2.get(var5);
                  if (var4 == NULL_VALUE) {
                     if (var6 != null) {
                        return false;
                     }
                  } else if (!var4.equals(var6)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public boolean containsKey(Object var1) {
      return this.containsKey(this.objectToKey(var1));
   }

   public V get(Object var1) {
      return this.get(this.objectToKey(var1));
   }

   public V put(Integer var1, V var2) {
      return this.put(this.objectToKey(var1), var2);
   }

   public V remove(Object var1) {
      return this.remove(this.objectToKey(var1));
   }

   public Set<Integer> keySet() {
      return this.keySet;
   }

   public Set<Entry<Integer, V>> entrySet() {
      return this.entrySet;
   }

   private int objectToKey(Object var1) {
      return (Integer)var1;
   }

   private int indexOf(int var1) {
      int var2 = this.hashIndex(var1);
      int var3 = var2;

      while(this.values[var3] != null) {
         if (var1 == this.keys[var3]) {
            return var3;
         }

         if ((var3 = this.probeNext(var3)) == var2) {
            return -1;
         }
      }

      return -1;
   }

   private int hashIndex(int var1) {
      return hashCode(var1) & this.mask;
   }

   private static int hashCode(int var0) {
      return var0;
   }

   private int probeNext(int var1) {
      return var1 + 1 & this.mask;
   }

   private void growSize() {
      ++this.size;
      if (this.size > this.maxSize) {
         if (this.keys.length == 2147483647) {
            throw new IllegalStateException("Max capacity reached at size=" + this.size);
         }

         this.rehash(this.keys.length << 1);
      }

   }

   private boolean removeAt(int var1) {
      --this.size;
      this.keys[var1] = 0;
      this.values[var1] = null;
      int var2 = var1;
      int var3 = this.probeNext(var1);

      for(Object var4 = this.values[var3]; var4 != null; var4 = this.values[var3 = this.probeNext(var3)]) {
         int var5 = this.keys[var3];
         int var6 = this.hashIndex(var5);
         if (var3 < var6 && (var6 <= var2 || var2 <= var3) || var6 <= var2 && var2 <= var3) {
            this.keys[var2] = var5;
            this.values[var2] = var4;
            this.keys[var3] = 0;
            this.values[var3] = null;
            var2 = var3;
         }
      }

      return var2 != var1;
   }

   private int calcMaxSize(int var1) {
      int var2 = var1 - 1;
      return Math.min(var2, (int)((float)var1 * this.loadFactor));
   }

   private void rehash(int var1) {
      int[] var2 = this.keys;
      Object[] var3 = this.values;
      this.keys = new int[var1];
      Object[] var4 = (Object[])(new Object[var1]);
      this.values = var4;
      this.maxSize = this.calcMaxSize(var1);
      this.mask = var1 - 1;

      for(int var5 = 0; var5 < var3.length; ++var5) {
         Object var6 = var3[var5];
         if (var6 != null) {
            int var7 = var2[var5];

            int var8;
            for(var8 = this.hashIndex(var7); this.values[var8] != null; var8 = this.probeNext(var8)) {
            }

            this.keys[var8] = var7;
            this.values[var8] = var6;
         }
      }

   }

   public String toString() {
      if (this.isEmpty()) {
         return "{}";
      } else {
         StringBuilder var1 = new StringBuilder(4 * this.size);
         var1.append('{');
         boolean var2 = true;

         for(int var3 = 0; var3 < this.values.length; ++var3) {
            Object var4 = this.values[var3];
            if (var4 != null) {
               if (!var2) {
                  var1.append(", ");
               }

               var1.append(this.keyToString(this.keys[var3])).append('=').append(var4 == this ? "(this Map)" : toExternal(var4));
               var2 = false;
            }
         }

         return var1.append('}').toString();
      }
   }

   protected String keyToString(int var1) {
      return Integer.toString(var1);
   }

   final class MapEntry implements Entry<Integer, V> {
      private final int entryIndex;

      MapEntry(int var2) {
         super();
         this.entryIndex = var2;
      }

      public Integer getKey() {
         this.verifyExists();
         return IntObjectHashMap.this.keys[this.entryIndex];
      }

      public V getValue() {
         this.verifyExists();
         return IntObjectHashMap.toExternal(IntObjectHashMap.this.values[this.entryIndex]);
      }

      public V setValue(V var1) {
         this.verifyExists();
         Object var2 = IntObjectHashMap.toExternal(IntObjectHashMap.this.values[this.entryIndex]);
         IntObjectHashMap.this.values[this.entryIndex] = IntObjectHashMap.toInternal(var1);
         return var2;
      }

      private void verifyExists() {
         if (IntObjectHashMap.this.values[this.entryIndex] == null) {
            throw new IllegalStateException("The map entry has been removed");
         }
      }
   }

   private final class MapIterator implements Iterator<Entry<Integer, V>> {
      private final IntObjectHashMap<V>.PrimitiveIterator iter;

      private MapIterator() {
         super();
         this.iter = IntObjectHashMap.this.new PrimitiveIterator();
      }

      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public Entry<Integer, V> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.iter.next();
            return IntObjectHashMap.this.new MapEntry(this.iter.entryIndex);
         }
      }

      public void remove() {
         this.iter.remove();
      }

      // $FF: synthetic method
      MapIterator(Object var2) {
         this();
      }
   }

   private final class PrimitiveIterator implements Iterator<IntObjectMap.PrimitiveEntry<V>>, IntObjectMap.PrimitiveEntry<V> {
      private int prevIndex;
      private int nextIndex;
      private int entryIndex;

      private PrimitiveIterator() {
         super();
         this.prevIndex = -1;
         this.nextIndex = -1;
         this.entryIndex = -1;
      }

      private void scanNext() {
         while(++this.nextIndex != IntObjectHashMap.this.values.length && IntObjectHashMap.this.values[this.nextIndex] == null) {
         }

      }

      public boolean hasNext() {
         if (this.nextIndex == -1) {
            this.scanNext();
         }

         return this.nextIndex != IntObjectHashMap.this.values.length;
      }

      public IntObjectMap.PrimitiveEntry<V> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.prevIndex = this.nextIndex;
            this.scanNext();
            this.entryIndex = this.prevIndex;
            return this;
         }
      }

      public void remove() {
         if (this.prevIndex == -1) {
            throw new IllegalStateException("next must be called before each remove.");
         } else {
            if (IntObjectHashMap.this.removeAt(this.prevIndex)) {
               this.nextIndex = this.prevIndex;
            }

            this.prevIndex = -1;
         }
      }

      public int key() {
         return IntObjectHashMap.this.keys[this.entryIndex];
      }

      public V value() {
         return IntObjectHashMap.toExternal(IntObjectHashMap.this.values[this.entryIndex]);
      }

      public void setValue(V var1) {
         IntObjectHashMap.this.values[this.entryIndex] = IntObjectHashMap.toInternal(var1);
      }

      // $FF: synthetic method
      PrimitiveIterator(Object var2) {
         this();
      }
   }

   private final class KeySet extends AbstractSet<Integer> {
      private KeySet() {
         super();
      }

      public int size() {
         return IntObjectHashMap.this.size();
      }

      public boolean contains(Object var1) {
         return IntObjectHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return IntObjectHashMap.this.remove(var1) != null;
      }

      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         Iterator var3 = IntObjectHashMap.this.entries().iterator();

         while(var3.hasNext()) {
            IntObjectMap.PrimitiveEntry var4 = (IntObjectMap.PrimitiveEntry)var3.next();
            if (!var1.contains(var4.key())) {
               var2 = true;
               var3.remove();
            }
         }

         return var2;
      }

      public void clear() {
         IntObjectHashMap.this.clear();
      }

      public Iterator<Integer> iterator() {
         return new Iterator<Integer>() {
            private final Iterator<Entry<Integer, V>> iter;

            {
               this.iter = IntObjectHashMap.this.entrySet.iterator();
            }

            public boolean hasNext() {
               return this.iter.hasNext();
            }

            public Integer next() {
               return (Integer)((Entry)this.iter.next()).getKey();
            }

            public void remove() {
               this.iter.remove();
            }
         };
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class EntrySet extends AbstractSet<Entry<Integer, V>> {
      private EntrySet() {
         super();
      }

      public Iterator<Entry<Integer, V>> iterator() {
         return IntObjectHashMap.this.new MapIterator();
      }

      public int size() {
         return IntObjectHashMap.this.size();
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }
}
