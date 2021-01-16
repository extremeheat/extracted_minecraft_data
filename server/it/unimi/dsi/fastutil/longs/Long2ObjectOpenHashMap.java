package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.LongFunction;

public class Long2ObjectOpenHashMap<V> extends AbstractLong2ObjectMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient long[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Long2ObjectMap.FastEntrySet<V> entries;
   protected transient LongSet keys;
   protected transient ObjectCollection<V> values;

   public Long2ObjectOpenHashMap(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new long[this.n + 1];
            this.value = new Object[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Long2ObjectOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Long2ObjectOpenHashMap() {
      this(16, 0.75F);
   }

   public Long2ObjectOpenHashMap(Map<? extends Long, ? extends V> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Long2ObjectOpenHashMap(Map<? extends Long, ? extends V> var1) {
      this(var1, 0.75F);
   }

   public Long2ObjectOpenHashMap(Long2ObjectMap<V> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Long2ObjectOpenHashMap(Long2ObjectMap<V> var1) {
      this(var1, 0.75F);
   }

   public Long2ObjectOpenHashMap(long[] var1, V[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Long2ObjectOpenHashMap(long[] var1, V[] var2) {
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
      Object var1 = this.value[this.n];
      this.value[this.n] = null;
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Long, ? extends V> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(long var1) {
      if (var1 == 0L) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return -(var6 + 1);
         } else if (var1 == var3) {
            return var6;
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  return var6;
               }
            }

            return -(var6 + 1);
         }
      }
   }

   private void insert(int var1, long var2, V var4) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var4;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public V put(long var1, V var3) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var3);
         return this.defRetValue;
      } else {
         Object var5 = this.value[var4];
         this.value[var4] = var3;
         return var5;
      }
   }

   protected final void shiftKeys(int var1) {
      long[] var6 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         long var4;
         while(true) {
            if ((var4 = var6[var1]) == 0L) {
               var6[var2] = 0L;
               this.value[var2] = null;
               return;
            }

            int var3 = (int)HashCommon.mix(var4) & this.mask;
            if (var2 <= var1) {
               if (var2 >= var3 || var3 > var1) {
                  break;
               }
            } else if (var2 >= var3 && var3 > var1) {
               break;
            }

            var1 = var1 + 1 & this.mask;
         }

         var6[var2] = var4;
         this.value[var2] = this.value[var1];
      }
   }

   public V remove(long var1) {
      if (var1 == 0L) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (var1 == var3) {
            return this.removeEntry(var6);
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  return this.removeEntry(var6);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V get(long var1) {
      if (var1 == 0L) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (var1 == var3) {
            return this.value[var6];
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  return this.value[var6];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(long var1) {
      if (var1 == 0L) {
         return this.containsNullKey;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return false;
         } else if (var1 == var3) {
            return true;
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(Object var1) {
      Object[] var2 = this.value;
      long[] var3 = this.key;
      if (this.containsNullKey && Objects.equals(var2[this.n], var1)) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(var3[var4] == 0L || !Objects.equals(var2[var4], var1));

         return true;
      }
   }

   public V getOrDefault(long var1, V var3) {
      if (var1 == 0L) {
         return this.containsNullKey ? this.value[this.n] : var3;
      } else {
         long[] var6 = this.key;
         long var4;
         int var7;
         if ((var4 = var6[var7 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return var3;
         } else if (var1 == var4) {
            return this.value[var7];
         } else {
            while((var4 = var6[var7 = var7 + 1 & this.mask]) != 0L) {
               if (var1 == var4) {
                  return this.value[var7];
               }
            }

            return var3;
         }
      }
   }

   public V putIfAbsent(long var1, V var3) {
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         this.insert(-var4 - 1, var1, var3);
         return this.defRetValue;
      }
   }

   public boolean remove(long var1, Object var3) {
      if (var1 == 0L) {
         if (this.containsNullKey && Objects.equals(var3, this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         long[] var6 = this.key;
         long var4;
         int var7;
         if ((var4 = var6[var7 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return false;
         } else if (var1 == var4 && Objects.equals(var3, this.value[var7])) {
            this.removeEntry(var7);
            return true;
         } else {
            do {
               if ((var4 = var6[var7 = var7 + 1 & this.mask]) == 0L) {
                  return false;
               }
            } while(var1 != var4 || !Objects.equals(var3, this.value[var7]));

            this.removeEntry(var7);
            return true;
         }
      }
   }

   public boolean replace(long var1, V var3, V var4) {
      int var5 = this.find(var1);
      if (var5 >= 0 && Objects.equals(var3, this.value[var5])) {
         this.value[var5] = var4;
         return true;
      } else {
         return false;
      }
   }

   public V replace(long var1, V var3) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         Object var5 = this.value[var4];
         this.value[var4] = var3;
         return var5;
      }
   }

   public V computeIfAbsent(long var1, LongFunction<? extends V> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         Object var5 = var3.apply(var1);
         this.insert(-var4 - 1, var1, var5);
         return var5;
      }
   }

   public V computeIfPresent(long var1, BiFunction<? super Long, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         Object var5 = var3.apply(var1, this.value[var4]);
         if (var5 == null) {
            if (var1 == 0L) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var4);
            }

            return this.defRetValue;
         } else {
            return this.value[var4] = var5;
         }
      }
   }

   public V compute(long var1, BiFunction<? super Long, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      Object var5 = var3.apply(var1, var4 >= 0 ? this.value[var4] : null);
      if (var5 == null) {
         if (var4 >= 0) {
            if (var1 == 0L) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var4);
            }
         }

         return this.defRetValue;
      } else if (var4 < 0) {
         this.insert(-var4 - 1, var1, var5);
         return var5;
      } else {
         return this.value[var4] = var5;
      }
   }

   public V merge(long var1, V var3, BiFunction<? super V, ? super V, ? extends V> var4) {
      Objects.requireNonNull(var4);
      int var5 = this.find(var1);
      if (var5 >= 0 && this.value[var5] != null) {
         Object var6 = var4.apply(this.value[var5], var3);
         if (var6 == null) {
            if (var1 == 0L) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var5);
            }

            return this.defRetValue;
         } else {
            return this.value[var5] = var6;
         }
      } else if (var3 == null) {
         return this.defRetValue;
      } else {
         this.insert(-var5 - 1, var1, var3);
         return var3;
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, 0L);
         Arrays.fill(this.value, (Object)null);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Long2ObjectMap.FastEntrySet<V> long2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2ObjectOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public LongSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2ObjectOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractObjectCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Long2ObjectOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Long2ObjectOpenHashMap.this.size;
            }

            public boolean contains(Object var1) {
               return Long2ObjectOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Long2ObjectOpenHashMap.this.clear();
            }

            public void forEach(Consumer<? super V> var1) {
               if (Long2ObjectOpenHashMap.this.containsNullKey) {
                  var1.accept(Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n]);
               }

               int var2 = Long2ObjectOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Long2ObjectOpenHashMap.this.key[var2] != 0L) {
                     var1.accept(Long2ObjectOpenHashMap.this.value[var2]);
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
      long[] var2 = this.key;
      Object[] var3 = this.value;
      int var4 = var1 - 1;
      long[] var5 = new long[var1 + 1];
      Object[] var6 = new Object[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == 0L);

         if (var5[var8 = (int)HashCommon.mix(var2[var7]) & var4] != 0L) {
            while(var5[var8 = var8 + 1 & var4] != 0L) {
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

   public Long2ObjectOpenHashMap<V> clone() {
      Long2ObjectOpenHashMap var1;
      try {
         var1 = (Long2ObjectOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (long[])this.key.clone();
      var1.value = (Object[])this.value.clone();
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(boolean var4 = false; var2-- != 0; ++var3) {
         while(this.key[var3] == 0L) {
            ++var3;
         }

         int var5 = HashCommon.long2int(this.key[var3]);
         if (this != this.value[var3]) {
            var5 ^= this.value[var3] == null ? 0 : this.value[var3].hashCode();
         }

         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      long[] var2 = this.key;
      Object[] var3 = this.value;
      Long2ObjectOpenHashMap.MapIterator var4 = new Long2ObjectOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeLong(var2[var6]);
         var1.writeObject(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      long[] var2 = this.key = new long[this.n + 1];
      Object[] var3 = this.value = new Object[this.n + 1];

      Object var6;
      int var8;
      for(int var7 = this.size; var7-- != 0; var3[var8] = var6) {
         long var4 = var1.readLong();
         var6 = var1.readObject();
         if (var4 == 0L) {
            var8 = this.n;
            this.containsNullKey = true;
         } else {
            for(var8 = (int)HashCommon.mix(var4) & this.mask; var2[var8] != 0L; var8 = var8 + 1 & this.mask) {
            }
         }

         var2[var8] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Long2ObjectOpenHashMap<V>.MapIterator implements ObjectIterator<V> {
      public ValueIterator() {
         super(null);
      }

      public V next() {
         return Long2ObjectOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractLongSet {
      private KeySet() {
         super();
      }

      public LongIterator iterator() {
         return Long2ObjectOpenHashMap.this.new KeyIterator();
      }

      public void forEach(java.util.function.LongConsumer var1) {
         if (Long2ObjectOpenHashMap.this.containsNullKey) {
            var1.accept(Long2ObjectOpenHashMap.this.key[Long2ObjectOpenHashMap.this.n]);
         }

         int var2 = Long2ObjectOpenHashMap.this.n;

         while(var2-- != 0) {
            long var3 = Long2ObjectOpenHashMap.this.key[var2];
            if (var3 != 0L) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Long2ObjectOpenHashMap.this.size;
      }

      public boolean contains(long var1) {
         return Long2ObjectOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(long var1) {
         int var3 = Long2ObjectOpenHashMap.this.size;
         Long2ObjectOpenHashMap.this.remove(var1);
         return Long2ObjectOpenHashMap.this.size != var3;
      }

      public void clear() {
         Long2ObjectOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Long2ObjectOpenHashMap<V>.MapIterator implements LongIterator {
      public KeyIterator() {
         super(null);
      }

      public long nextLong() {
         return Long2ObjectOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Long2ObjectMap.Entry<V>> implements Long2ObjectMap.FastEntrySet<V> {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Long2ObjectMap.Entry<V>> iterator() {
         return Long2ObjectOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Long2ObjectMap.Entry<V>> fastIterator() {
         return Long2ObjectOpenHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               long var3 = (Long)var2.getKey();
               Object var5 = var2.getValue();
               if (var3 == 0L) {
                  return Long2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n], var5);
               } else {
                  long[] var8 = Long2ObjectOpenHashMap.this.key;
                  long var6;
                  int var9;
                  if ((var6 = var8[var9 = (int)HashCommon.mix(var3) & Long2ObjectOpenHashMap.this.mask]) == 0L) {
                     return false;
                  } else if (var3 == var6) {
                     return Objects.equals(Long2ObjectOpenHashMap.this.value[var9], var5);
                  } else {
                     while((var6 = var8[var9 = var9 + 1 & Long2ObjectOpenHashMap.this.mask]) != 0L) {
                        if (var3 == var6) {
                           return Objects.equals(Long2ObjectOpenHashMap.this.value[var9], var5);
                        }
                     }

                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               long var3 = (Long)var2.getKey();
               Object var5 = var2.getValue();
               if (var3 == 0L) {
                  if (Long2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n], var5)) {
                     Long2ObjectOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  long[] var8 = Long2ObjectOpenHashMap.this.key;
                  long var6;
                  int var9;
                  if ((var6 = var8[var9 = (int)HashCommon.mix(var3) & Long2ObjectOpenHashMap.this.mask]) == 0L) {
                     return false;
                  } else if (var6 == var3) {
                     if (Objects.equals(Long2ObjectOpenHashMap.this.value[var9], var5)) {
                        Long2ObjectOpenHashMap.this.removeEntry(var9);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((var6 = var8[var9 = var9 + 1 & Long2ObjectOpenHashMap.this.mask]) == 0L) {
                           return false;
                        }
                     } while(var6 != var3 || !Objects.equals(Long2ObjectOpenHashMap.this.value[var9], var5));

                     Long2ObjectOpenHashMap.this.removeEntry(var9);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Long2ObjectOpenHashMap.this.size;
      }

      public void clear() {
         Long2ObjectOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Long2ObjectMap.Entry<V>> var1) {
         if (Long2ObjectOpenHashMap.this.containsNullKey) {
            var1.accept(new AbstractLong2ObjectMap.BasicEntry(Long2ObjectOpenHashMap.this.key[Long2ObjectOpenHashMap.this.n], Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n]));
         }

         int var2 = Long2ObjectOpenHashMap.this.n;

         while(var2-- != 0) {
            if (Long2ObjectOpenHashMap.this.key[var2] != 0L) {
               var1.accept(new AbstractLong2ObjectMap.BasicEntry(Long2ObjectOpenHashMap.this.key[var2], Long2ObjectOpenHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Long2ObjectMap.Entry<V>> var1) {
         AbstractLong2ObjectMap.BasicEntry var2 = new AbstractLong2ObjectMap.BasicEntry();
         if (Long2ObjectOpenHashMap.this.containsNullKey) {
            var2.key = Long2ObjectOpenHashMap.this.key[Long2ObjectOpenHashMap.this.n];
            var2.value = Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Long2ObjectOpenHashMap.this.n;

         while(var3-- != 0) {
            if (Long2ObjectOpenHashMap.this.key[var3] != 0L) {
               var2.key = Long2ObjectOpenHashMap.this.key[var3];
               var2.value = Long2ObjectOpenHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Long2ObjectOpenHashMap<V>.MapIterator implements ObjectIterator<Long2ObjectMap.Entry<V>> {
      private final Long2ObjectOpenHashMap<V>.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Long2ObjectOpenHashMap.this.new MapEntry();
      }

      public Long2ObjectOpenHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Long2ObjectOpenHashMap<V>.MapIterator implements ObjectIterator<Long2ObjectMap.Entry<V>> {
      private Long2ObjectOpenHashMap<V>.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Long2ObjectOpenHashMap<V>.MapEntry next() {
         return this.entry = Long2ObjectOpenHashMap.this.new MapEntry(this.nextEntry());
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
      LongArrayList wrapped;

      private MapIterator() {
         super();
         this.pos = Long2ObjectOpenHashMap.this.n;
         this.last = -1;
         this.c = Long2ObjectOpenHashMap.this.size;
         this.mustReturnNullKey = Long2ObjectOpenHashMap.this.containsNullKey;
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
               return this.last = Long2ObjectOpenHashMap.this.n;
            } else {
               long[] var1 = Long2ObjectOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0L) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               long var2 = this.wrapped.getLong(-this.pos - 1);

               int var4;
               for(var4 = (int)HashCommon.mix(var2) & Long2ObjectOpenHashMap.this.mask; var2 != var1[var4]; var4 = var4 + 1 & Long2ObjectOpenHashMap.this.mask) {
               }

               return var4;
            }
         }
      }

      private void shiftKeys(int var1) {
         long[] var6 = Long2ObjectOpenHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Long2ObjectOpenHashMap.this.mask;

            long var4;
            while(true) {
               if ((var4 = var6[var1]) == 0L) {
                  var6[var2] = 0L;
                  Long2ObjectOpenHashMap.this.value[var2] = null;
                  return;
               }

               int var3 = (int)HashCommon.mix(var4) & Long2ObjectOpenHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Long2ObjectOpenHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new LongArrayList(2);
               }

               this.wrapped.add(var6[var1]);
            }

            var6[var2] = var4;
            Long2ObjectOpenHashMap.this.value[var2] = Long2ObjectOpenHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Long2ObjectOpenHashMap.this.n) {
               Long2ObjectOpenHashMap.this.containsNullKey = false;
               Long2ObjectOpenHashMap.this.value[Long2ObjectOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Long2ObjectOpenHashMap.this.remove(this.wrapped.getLong(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Long2ObjectOpenHashMap.this.size;
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

   final class MapEntry implements Long2ObjectMap.Entry<V>, java.util.Map.Entry<Long, V> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public long getLongKey() {
         return Long2ObjectOpenHashMap.this.key[this.index];
      }

      public V getValue() {
         return Long2ObjectOpenHashMap.this.value[this.index];
      }

      public V setValue(V var1) {
         Object var2 = Long2ObjectOpenHashMap.this.value[this.index];
         Long2ObjectOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Long getKey() {
         return Long2ObjectOpenHashMap.this.key[this.index];
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Long2ObjectOpenHashMap.this.key[this.index] == (Long)var2.getKey() && Objects.equals(Long2ObjectOpenHashMap.this.value[this.index], var2.getValue());
         }
      }

      public int hashCode() {
         return HashCommon.long2int(Long2ObjectOpenHashMap.this.key[this.index]) ^ (Long2ObjectOpenHashMap.this.value[this.index] == null ? 0 : Long2ObjectOpenHashMap.this.value[this.index].hashCode());
      }

      public String toString() {
         return Long2ObjectOpenHashMap.this.key[this.index] + "=>" + Long2ObjectOpenHashMap.this.value[this.index];
      }
   }
}
