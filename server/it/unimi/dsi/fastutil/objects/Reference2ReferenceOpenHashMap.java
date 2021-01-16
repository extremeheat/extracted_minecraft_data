package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class Reference2ReferenceOpenHashMap<K, V> extends AbstractReference2ReferenceMap<K, V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Reference2ReferenceMap.FastEntrySet<K, V> entries;
   protected transient ReferenceSet<K> keys;
   protected transient ReferenceCollection<V> values;

   public Reference2ReferenceOpenHashMap(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new Object[this.n + 1];
            this.value = new Object[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Reference2ReferenceOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Reference2ReferenceOpenHashMap() {
      this(16, 0.75F);
   }

   public Reference2ReferenceOpenHashMap(Map<? extends K, ? extends V> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Reference2ReferenceOpenHashMap(Map<? extends K, ? extends V> var1) {
      this(var1, 0.75F);
   }

   public Reference2ReferenceOpenHashMap(Reference2ReferenceMap<K, V> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Reference2ReferenceOpenHashMap(Reference2ReferenceMap<K, V> var1) {
      this(var1, 0.75F);
   }

   public Reference2ReferenceOpenHashMap(K[] var1, V[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Reference2ReferenceOpenHashMap(K[] var1, V[] var2) {
      this(var1, var2, 0.75F);
   }

   private int realSize() {
      return this.containsNullKey ? this.size - 1 : this.size;
   }

   private void ensureCapacity(int var1) {
      int var2 = HashCommon.arraySize(var1, this.f);
      if (var2 > this.n) {
         this.rehash(var2);
      }

   }

   private void tryCapacity(long var1) {
      int var3 = (int)Math.min(1073741824L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((double)((float)var1 / this.f)))));
      if (var3 > this.n) {
         this.rehash(var3);
      }

   }

   private V removeEntry(int var1) {
      Object var2 = this.value[var1];
      this.value[var1] = null;
      --this.size;
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private V removeNullEntry() {
      this.containsNullKey = false;
      this.key[this.n] = null;
      Object var1 = this.value[this.n];
      this.value[this.n] = null;
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(K var1) {
      if (var1 == null) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return -(var4 + 1);
         } else if (var1 == var2) {
            return var4;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, K var2, V var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public V put(K var1, V var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         Object var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   protected final void shiftKeys(int var1) {
      Object[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         Object var4;
         while(true) {
            if ((var4 = var5[var1]) == null) {
               var5[var2] = null;
               this.value[var2] = null;
               return;
            }

            int var3 = HashCommon.mix(System.identityHashCode(var4)) & this.mask;
            if (var2 <= var1) {
               if (var2 >= var3 || var3 > var1) {
                  break;
               }
            } else if (var2 >= var3 && var3 > var1) {
               break;
            }

            var1 = var1 + 1 & this.mask;
         }

         var5[var2] = var4;
         this.value[var2] = this.value[var1];
      }
   }

   public V remove(Object var1) {
      if (var1 == null) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (var1 == var2) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  return this.removeEntry(var4);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V get(Object var1) {
      if (var1 == null) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (var1 == var2) {
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(Object var1) {
      if (var1 == null) {
         return this.containsNullKey;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return false;
         } else if (var1 == var2) {
            return true;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(Object var1) {
      Object[] var2 = this.value;
      Object[] var3 = this.key;
      if (this.containsNullKey && var2[this.n] == var1) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(var3[var4] == null || var2[var4] != var1);

         return true;
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, (Object)null);
         Arrays.fill(this.value, (Object)null);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Reference2ReferenceMap.FastEntrySet<K, V> reference2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2ReferenceOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ReferenceSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2ReferenceOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Reference2ReferenceOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Reference2ReferenceOpenHashMap.this.size;
            }

            public boolean contains(Object var1) {
               return Reference2ReferenceOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Reference2ReferenceOpenHashMap.this.clear();
            }

            public void forEach(Consumer<? super V> var1) {
               if (Reference2ReferenceOpenHashMap.this.containsNullKey) {
                  var1.accept(Reference2ReferenceOpenHashMap.this.value[Reference2ReferenceOpenHashMap.this.n]);
               }

               int var2 = Reference2ReferenceOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Reference2ReferenceOpenHashMap.this.key[var2] != null) {
                     var1.accept(Reference2ReferenceOpenHashMap.this.value[var2]);
                  }
               }

            }
         };
      }

      return this.values;
   }

   public boolean trim() {
      int var1 = HashCommon.arraySize(this.size, this.f);
      if (var1 < this.n && this.size <= HashCommon.maxFill(var1, this.f)) {
         try {
            this.rehash(var1);
            return true;
         } catch (OutOfMemoryError var3) {
            return false;
         }
      } else {
         return true;
      }
   }

   public boolean trim(int var1) {
      int var2 = HashCommon.nextPowerOfTwo((int)Math.ceil((double)((float)var1 / this.f)));
      if (var2 < var1 && this.size <= HashCommon.maxFill(var2, this.f)) {
         try {
            this.rehash(var2);
            return true;
         } catch (OutOfMemoryError var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   protected void rehash(int var1) {
      Object[] var2 = this.key;
      Object[] var3 = this.value;
      int var4 = var1 - 1;
      Object[] var5 = new Object[var1 + 1];
      Object[] var6 = new Object[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == null);

         if (var5[var8 = HashCommon.mix(System.identityHashCode(var2[var7])) & var4] != null) {
            while(var5[var8 = var8 + 1 & var4] != null) {
            }
         }

         var5[var8] = var2[var7];
      }

      var6[var1] = var3[this.n];
      this.n = var1;
      this.mask = var4;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var5;
      this.value = var6;
   }

   public Reference2ReferenceOpenHashMap<K, V> clone() {
      Reference2ReferenceOpenHashMap var1;
      try {
         var1 = (Reference2ReferenceOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (Object[])this.key.clone();
      var1.value = (Object[])this.value.clone();
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(int var4 = 0; var2-- != 0; ++var3) {
         while(this.key[var3] == null) {
            ++var3;
         }

         if (this != this.key[var3]) {
            var4 = System.identityHashCode(this.key[var3]);
         }

         if (this != this.value[var3]) {
            var4 ^= this.value[var3] == null ? 0 : System.identityHashCode(this.value[var3]);
         }

         var1 += var4;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] == null ? 0 : System.identityHashCode(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Object[] var2 = this.key;
      Object[] var3 = this.value;
      Reference2ReferenceOpenHashMap.MapIterator var4 = new Reference2ReferenceOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeObject(var2[var6]);
         var1.writeObject(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      Object[] var2 = this.key = new Object[this.n + 1];
      Object[] var3 = this.value = new Object[this.n + 1];

      Object var5;
      int var7;
      for(int var6 = this.size; var6-- != 0; var3[var7] = var5) {
         Object var4 = var1.readObject();
         var5 = var1.readObject();
         if (var4 == null) {
            var7 = this.n;
            this.containsNullKey = true;
         } else {
            for(var7 = HashCommon.mix(System.identityHashCode(var4)) & this.mask; var2[var7] != null; var7 = var7 + 1 & this.mask) {
            }
         }

         var2[var7] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Reference2ReferenceOpenHashMap<K, V>.MapIterator implements ObjectIterator<V> {
      public ValueIterator() {
         super(null);
      }

      public V next() {
         return Reference2ReferenceOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
         super();
      }

      public ObjectIterator<K> iterator() {
         return Reference2ReferenceOpenHashMap.this.new KeyIterator();
      }

      public void forEach(Consumer<? super K> var1) {
         if (Reference2ReferenceOpenHashMap.this.containsNullKey) {
            var1.accept(Reference2ReferenceOpenHashMap.this.key[Reference2ReferenceOpenHashMap.this.n]);
         }

         int var2 = Reference2ReferenceOpenHashMap.this.n;

         while(var2-- != 0) {
            Object var3 = Reference2ReferenceOpenHashMap.this.key[var2];
            if (var3 != null) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Reference2ReferenceOpenHashMap.this.size;
      }

      public boolean contains(Object var1) {
         return Reference2ReferenceOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         int var2 = Reference2ReferenceOpenHashMap.this.size;
         Reference2ReferenceOpenHashMap.this.remove(var1);
         return Reference2ReferenceOpenHashMap.this.size != var2;
      }

      public void clear() {
         Reference2ReferenceOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Reference2ReferenceOpenHashMap<K, V>.MapIterator implements ObjectIterator<K> {
      public KeyIterator() {
         super(null);
      }

      public K next() {
         return Reference2ReferenceOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Reference2ReferenceMap.Entry<K, V>> implements Reference2ReferenceMap.FastEntrySet<K, V> {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> iterator() {
         return Reference2ReferenceOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator() {
         return Reference2ReferenceOpenHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var3 == null) {
               return Reference2ReferenceOpenHashMap.this.containsNullKey && Reference2ReferenceOpenHashMap.this.value[Reference2ReferenceOpenHashMap.this.n] == var4;
            } else {
               Object[] var6 = Reference2ReferenceOpenHashMap.this.key;
               Object var5;
               int var7;
               if ((var5 = var6[var7 = HashCommon.mix(System.identityHashCode(var3)) & Reference2ReferenceOpenHashMap.this.mask]) == null) {
                  return false;
               } else if (var3 == var5) {
                  return Reference2ReferenceOpenHashMap.this.value[var7] == var4;
               } else {
                  while((var5 = var6[var7 = var7 + 1 & Reference2ReferenceOpenHashMap.this.mask]) != null) {
                     if (var3 == var5) {
                        return Reference2ReferenceOpenHashMap.this.value[var7] == var4;
                     }
                  }

                  return false;
               }
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var3 == null) {
               if (Reference2ReferenceOpenHashMap.this.containsNullKey && Reference2ReferenceOpenHashMap.this.value[Reference2ReferenceOpenHashMap.this.n] == var4) {
                  Reference2ReferenceOpenHashMap.this.removeNullEntry();
                  return true;
               } else {
                  return false;
               }
            } else {
               Object[] var6 = Reference2ReferenceOpenHashMap.this.key;
               Object var5;
               int var7;
               if ((var5 = var6[var7 = HashCommon.mix(System.identityHashCode(var3)) & Reference2ReferenceOpenHashMap.this.mask]) == null) {
                  return false;
               } else if (var5 == var3) {
                  if (Reference2ReferenceOpenHashMap.this.value[var7] == var4) {
                     Reference2ReferenceOpenHashMap.this.removeEntry(var7);
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  do {
                     if ((var5 = var6[var7 = var7 + 1 & Reference2ReferenceOpenHashMap.this.mask]) == null) {
                        return false;
                     }
                  } while(var5 != var3 || Reference2ReferenceOpenHashMap.this.value[var7] != var4);

                  Reference2ReferenceOpenHashMap.this.removeEntry(var7);
                  return true;
               }
            }
         }
      }

      public int size() {
         return Reference2ReferenceOpenHashMap.this.size;
      }

      public void clear() {
         Reference2ReferenceOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> var1) {
         if (Reference2ReferenceOpenHashMap.this.containsNullKey) {
            var1.accept(new AbstractReference2ReferenceMap.BasicEntry(Reference2ReferenceOpenHashMap.this.key[Reference2ReferenceOpenHashMap.this.n], Reference2ReferenceOpenHashMap.this.value[Reference2ReferenceOpenHashMap.this.n]));
         }

         int var2 = Reference2ReferenceOpenHashMap.this.n;

         while(var2-- != 0) {
            if (Reference2ReferenceOpenHashMap.this.key[var2] != null) {
               var1.accept(new AbstractReference2ReferenceMap.BasicEntry(Reference2ReferenceOpenHashMap.this.key[var2], Reference2ReferenceOpenHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> var1) {
         AbstractReference2ReferenceMap.BasicEntry var2 = new AbstractReference2ReferenceMap.BasicEntry();
         if (Reference2ReferenceOpenHashMap.this.containsNullKey) {
            var2.key = Reference2ReferenceOpenHashMap.this.key[Reference2ReferenceOpenHashMap.this.n];
            var2.value = Reference2ReferenceOpenHashMap.this.value[Reference2ReferenceOpenHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Reference2ReferenceOpenHashMap.this.n;

         while(var3-- != 0) {
            if (Reference2ReferenceOpenHashMap.this.key[var3] != null) {
               var2.key = Reference2ReferenceOpenHashMap.this.key[var3];
               var2.value = Reference2ReferenceOpenHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Reference2ReferenceOpenHashMap<K, V>.MapIterator implements ObjectIterator<Reference2ReferenceMap.Entry<K, V>> {
      private final Reference2ReferenceOpenHashMap<K, V>.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Reference2ReferenceOpenHashMap.this.new MapEntry();
      }

      public Reference2ReferenceOpenHashMap<K, V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Reference2ReferenceOpenHashMap<K, V>.MapIterator implements ObjectIterator<Reference2ReferenceMap.Entry<K, V>> {
      private Reference2ReferenceOpenHashMap<K, V>.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Reference2ReferenceOpenHashMap<K, V>.MapEntry next() {
         return this.entry = Reference2ReferenceOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public void remove() {
         super.remove();
         this.entry.index = -1;
      }

      // $FF: synthetic method
      EntryIterator(Object var2) {
         this();
      }
   }

   private class MapIterator {
      int pos;
      int last;
      int c;
      boolean mustReturnNullKey;
      ReferenceArrayList<K> wrapped;

      private MapIterator() {
         super();
         this.pos = Reference2ReferenceOpenHashMap.this.n;
         this.last = -1;
         this.c = Reference2ReferenceOpenHashMap.this.size;
         this.mustReturnNullKey = Reference2ReferenceOpenHashMap.this.containsNullKey;
      }

      public boolean hasNext() {
         return this.c != 0;
      }

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNullKey) {
               this.mustReturnNullKey = false;
               return this.last = Reference2ReferenceOpenHashMap.this.n;
            } else {
               Object[] var1 = Reference2ReferenceOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != null) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               Object var2 = this.wrapped.get(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(System.identityHashCode(var2)) & Reference2ReferenceOpenHashMap.this.mask; var2 != var1[var3]; var3 = var3 + 1 & Reference2ReferenceOpenHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         Object[] var5 = Reference2ReferenceOpenHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Reference2ReferenceOpenHashMap.this.mask;

            Object var4;
            while(true) {
               if ((var4 = var5[var1]) == null) {
                  var5[var2] = null;
                  Reference2ReferenceOpenHashMap.this.value[var2] = null;
                  return;
               }

               int var3 = HashCommon.mix(System.identityHashCode(var4)) & Reference2ReferenceOpenHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Reference2ReferenceOpenHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new ReferenceArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Reference2ReferenceOpenHashMap.this.value[var2] = Reference2ReferenceOpenHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Reference2ReferenceOpenHashMap.this.n) {
               Reference2ReferenceOpenHashMap.this.containsNullKey = false;
               Reference2ReferenceOpenHashMap.this.key[Reference2ReferenceOpenHashMap.this.n] = null;
               Reference2ReferenceOpenHashMap.this.value[Reference2ReferenceOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Reference2ReferenceOpenHashMap.this.remove(this.wrapped.set(-this.pos - 1, (Object)null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Reference2ReferenceOpenHashMap.this.size;
            this.last = -1;
         }
      }

      public int skip(int var1) {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return var1 - var2 - 1;
      }

      // $FF: synthetic method
      MapIterator(Object var2) {
         this();
      }
   }

   final class MapEntry implements Reference2ReferenceMap.Entry<K, V>, java.util.Map.Entry<K, V> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public K getKey() {
         return Reference2ReferenceOpenHashMap.this.key[this.index];
      }

      public V getValue() {
         return Reference2ReferenceOpenHashMap.this.value[this.index];
      }

      public V setValue(V var1) {
         Object var2 = Reference2ReferenceOpenHashMap.this.value[this.index];
         Reference2ReferenceOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Reference2ReferenceOpenHashMap.this.key[this.index] == var2.getKey() && Reference2ReferenceOpenHashMap.this.value[this.index] == var2.getValue();
         }
      }

      public int hashCode() {
         return System.identityHashCode(Reference2ReferenceOpenHashMap.this.key[this.index]) ^ (Reference2ReferenceOpenHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Reference2ReferenceOpenHashMap.this.value[this.index]));
      }

      public String toString() {
         return Reference2ReferenceOpenHashMap.this.key[this.index] + "=>" + Reference2ReferenceOpenHashMap.this.value[this.index];
      }
   }
}
