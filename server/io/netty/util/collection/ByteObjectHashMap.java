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

public class ByteObjectHashMap<V> implements ByteObjectMap<V> {
   public static final int DEFAULT_CAPACITY = 8;
   public static final float DEFAULT_LOAD_FACTOR = 0.5F;
   private static final Object NULL_VALUE = new Object();
   private int maxSize;
   private final float loadFactor;
   private byte[] keys;
   private V[] values;
   private int size;
   private int mask;
   private final Set<Byte> keySet;
   private final Set<Entry<Byte, V>> entrySet;
   private final Iterable<ByteObjectMap.PrimitiveEntry<V>> entries;

   public ByteObjectHashMap() {
      this(8, 0.5F);
   }

   public ByteObjectHashMap(int var1) {
      this(var1, 0.5F);
   }

   public ByteObjectHashMap(int var1, float var2) {
      super();
      this.keySet = new ByteObjectHashMap.KeySet();
      this.entrySet = new ByteObjectHashMap.EntrySet();
      this.entries = new Iterable<ByteObjectMap.PrimitiveEntry<V>>() {
         public Iterator<ByteObjectMap.PrimitiveEntry<V>> iterator() {
            return ByteObjectHashMap.this.new PrimitiveIterator();
         }
      };
      if (var2 > 0.0F && var2 <= 1.0F) {
         this.loadFactor = var2;
         int var3 = MathUtil.safeFindNextPositivePowerOfTwo(var1);
         this.mask = var3 - 1;
         this.keys = new byte[var3];
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

   public V get(byte var1) {
      int var2 = this.indexOf(var1);
      return var2 == -1 ? null : toExternal(this.values[var2]);
   }

   public V put(byte var1, V var2) {
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

   public void putAll(Map<? extends Byte, ? extends V> var1) {
      if (var1 instanceof ByteObjectHashMap) {
         ByteObjectHashMap var5 = (ByteObjectHashMap)var1;

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
            this.put((Byte)var3.getKey(), var3.getValue());
         }

      }
   }

   public V remove(byte var1) {
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
      Arrays.fill(this.keys, (byte)0);
      Arrays.fill(this.values, (Object)null);
      this.size = 0;
   }

   public boolean containsKey(byte var1) {
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

   public Iterable<ByteObjectMap.PrimitiveEntry<V>> entries() {
      return this.entries;
   }

   public Collection<V> values() {
      return new AbstractCollection<V>() {
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               final ByteObjectHashMap<V>.PrimitiveIterator iter = ByteObjectHashMap.this.new PrimitiveIterator();

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
            return ByteObjectHashMap.this.size;
         }
      };
   }

   public int hashCode() {
      int var1 = this.size;
      byte[] var2 = this.keys;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte var5 = var2[var4];
         var1 ^= hashCode(var5);
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ByteObjectMap)) {
         return false;
      } else {
         ByteObjectMap var2 = (ByteObjectMap)var1;
         if (this.size != var2.size()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.values.length; ++var3) {
               Object var4 = this.values[var3];
               if (var4 != null) {
                  byte var5 = this.keys[var3];
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

   public V put(Byte var1, V var2) {
      return this.put(this.objectToKey(var1), var2);
   }

   public V remove(Object var1) {
      return this.remove(this.objectToKey(var1));
   }

   public Set<Byte> keySet() {
      return this.keySet;
   }

   public Set<Entry<Byte, V>> entrySet() {
      return this.entrySet;
   }

   private byte objectToKey(Object var1) {
      return (Byte)var1;
   }

   private int indexOf(byte var1) {
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

   private int hashIndex(byte var1) {
      return hashCode(var1) & this.mask;
   }

   private static int hashCode(byte var0) {
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
         byte var5 = this.keys[var3];
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
      byte[] var2 = this.keys;
      Object[] var3 = this.values;
      this.keys = new byte[var1];
      Object[] var4 = (Object[])(new Object[var1]);
      this.values = var4;
      this.maxSize = this.calcMaxSize(var1);
      this.mask = var1 - 1;

      for(int var5 = 0; var5 < var3.length; ++var5) {
         Object var6 = var3[var5];
         if (var6 != null) {
            byte var7 = var2[var5];

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

   protected String keyToString(byte var1) {
      return Byte.toString(var1);
   }

   final class MapEntry implements Entry<Byte, V> {
      private final int entryIndex;

      MapEntry(int var2) {
         super();
         this.entryIndex = var2;
      }

      public Byte getKey() {
         this.verifyExists();
         return ByteObjectHashMap.this.keys[this.entryIndex];
      }

      public V getValue() {
         this.verifyExists();
         return ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
      }

      public V setValue(V var1) {
         this.verifyExists();
         Object var2 = ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
         ByteObjectHashMap.this.values[this.entryIndex] = ByteObjectHashMap.toInternal(var1);
         return var2;
      }

      private void verifyExists() {
         if (ByteObjectHashMap.this.values[this.entryIndex] == null) {
            throw new IllegalStateException("The map entry has been removed");
         }
      }
   }

   private final class MapIterator implements Iterator<Entry<Byte, V>> {
      private final ByteObjectHashMap<V>.PrimitiveIterator iter;

      private MapIterator() {
         super();
         this.iter = ByteObjectHashMap.this.new PrimitiveIterator();
      }

      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public Entry<Byte, V> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.iter.next();
            return ByteObjectHashMap.this.new MapEntry(this.iter.entryIndex);
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

   private final class PrimitiveIterator implements Iterator<ByteObjectMap.PrimitiveEntry<V>>, ByteObjectMap.PrimitiveEntry<V> {
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
         while(++this.nextIndex != ByteObjectHashMap.this.values.length && ByteObjectHashMap.this.values[this.nextIndex] == null) {
         }

      }

      public boolean hasNext() {
         if (this.nextIndex == -1) {
            this.scanNext();
         }

         return this.nextIndex != ByteObjectHashMap.this.values.length;
      }

      public ByteObjectMap.PrimitiveEntry<V> next() {
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
            if (ByteObjectHashMap.this.removeAt(this.prevIndex)) {
               this.nextIndex = this.prevIndex;
            }

            this.prevIndex = -1;
         }
      }

      public byte key() {
         return ByteObjectHashMap.this.keys[this.entryIndex];
      }

      public V value() {
         return ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
      }

      public void setValue(V var1) {
         ByteObjectHashMap.this.values[this.entryIndex] = ByteObjectHashMap.toInternal(var1);
      }

      // $FF: synthetic method
      PrimitiveIterator(Object var2) {
         this();
      }
   }

   private final class KeySet extends AbstractSet<Byte> {
      private KeySet() {
         super();
      }

      public int size() {
         return ByteObjectHashMap.this.size();
      }

      public boolean contains(Object var1) {
         return ByteObjectHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return ByteObjectHashMap.this.remove(var1) != null;
      }

      public boolean retainAll(Collection<?> var1) {
         boolean var2 = false;
         Iterator var3 = ByteObjectHashMap.this.entries().iterator();

         while(var3.hasNext()) {
            ByteObjectMap.PrimitiveEntry var4 = (ByteObjectMap.PrimitiveEntry)var3.next();
            if (!var1.contains(var4.key())) {
               var2 = true;
               var3.remove();
            }
         }

         return var2;
      }

      public void clear() {
         ByteObjectHashMap.this.clear();
      }

      public Iterator<Byte> iterator() {
         return new Iterator<Byte>() {
            private final Iterator<Entry<Byte, V>> iter;

            {
               this.iter = ByteObjectHashMap.this.entrySet.iterator();
            }

            public boolean hasNext() {
               return this.iter.hasNext();
            }

            public Byte next() {
               return (Byte)((Entry)this.iter.next()).getKey();
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

   private final class EntrySet extends AbstractSet<Entry<Byte, V>> {
      private EntrySet() {
         super();
      }

      public Iterator<Entry<Byte, V>> iterator() {
         return ByteObjectHashMap.this.new MapIterator();
      }

      public int size() {
         return ByteObjectHashMap.this.size();
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }
}
