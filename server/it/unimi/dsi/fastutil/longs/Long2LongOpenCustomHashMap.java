package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
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
import java.util.function.LongUnaryOperator;

public class Long2LongOpenCustomHashMap extends AbstractLong2LongMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient long[] key;
   protected transient long[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected LongHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Long2LongMap.FastEntrySet entries;
   protected transient LongSet keys;
   protected transient LongCollection values;

   public Long2LongOpenCustomHashMap(int var1, float var2, LongHash.Strategy var3) {
      super();
      this.strategy = var3;
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new long[this.n + 1];
            this.value = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Long2LongOpenCustomHashMap(int var1, LongHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Long2LongOpenCustomHashMap(LongHash.Strategy var1) {
      this(16, 0.75F, var1);
   }

   public Long2LongOpenCustomHashMap(Map<? extends Long, ? extends Long> var1, float var2, LongHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Long2LongOpenCustomHashMap(Map<? extends Long, ? extends Long> var1, LongHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Long2LongOpenCustomHashMap(Long2LongMap var1, float var2, LongHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Long2LongOpenCustomHashMap(Long2LongMap var1, LongHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Long2LongOpenCustomHashMap(long[] var1, long[] var2, float var3, LongHash.Strategy var4) {
      this(var1.length, var3, var4);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            this.put(var1[var5], var2[var5]);
         }

      }
   }

   public Long2LongOpenCustomHashMap(long[] var1, long[] var2, LongHash.Strategy var3) {
      this(var1, var2, 0.75F, var3);
   }

   public LongHash.Strategy strategy() {
      return this.strategy;
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

   private long removeEntry(int var1) {
      long var2 = this.value[var1];
      --this.size;
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private long removeNullEntry() {
      this.containsNullKey = false;
      long var1 = this.value[this.n];
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Long, ? extends Long> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(long var1) {
      if (this.strategy.equals(var1, 0L)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0L) {
            return -(var6 + 1);
         } else if (this.strategy.equals(var1, var3)) {
            return var6;
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(var1, var3)) {
                  return var6;
               }
            }

            return -(var6 + 1);
         }
      }
   }

   private void insert(int var1, long var2, long var4) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var4;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public long put(long var1, long var3) {
      int var5 = this.find(var1);
      if (var5 < 0) {
         this.insert(-var5 - 1, var1, var3);
         return this.defRetValue;
      } else {
         long var6 = this.value[var5];
         this.value[var5] = var3;
         return var6;
      }
   }

   private long addToValue(int var1, long var2) {
      long var4 = this.value[var1];
      this.value[var1] = var4 + var2;
      return var4;
   }

   public long addTo(long var1, long var3) {
      int var5;
      if (this.strategy.equals(var1, 0L)) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var3);
         }

         var5 = this.n;
         this.containsNullKey = true;
      } else {
         long[] var8 = this.key;
         long var6;
         if ((var6 = var8[var5 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) != 0L) {
            if (this.strategy.equals(var6, var1)) {
               return this.addToValue(var5, var3);
            }

            while((var6 = var8[var5 = var5 + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(var6, var1)) {
                  return this.addToValue(var5, var3);
               }
            }
         }
      }

      this.key[var5] = var1;
      this.value[var5] = this.defRetValue + var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return this.defRetValue;
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
               return;
            }

            int var3 = HashCommon.mix(this.strategy.hashCode(var4)) & this.mask;
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

   public long remove(long var1) {
      if (this.strategy.equals(var1, 0L)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var3)) {
            return this.removeEntry(var6);
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(var1, var3)) {
                  return this.removeEntry(var6);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public long get(long var1) {
      if (this.strategy.equals(var1, 0L)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var3)) {
            return this.value[var6];
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(var1, var3)) {
                  return this.value[var6];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(long var1) {
      if (this.strategy.equals(var1, 0L)) {
         return this.containsNullKey;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0L) {
            return false;
         } else if (this.strategy.equals(var1, var3)) {
            return true;
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(var1, var3)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(long var1) {
      long[] var3 = this.value;
      long[] var4 = this.key;
      if (this.containsNullKey && var3[this.n] == var1) {
         return true;
      } else {
         int var5 = this.n;

         do {
            if (var5-- == 0) {
               return false;
            }
         } while(var4[var5] == 0L || var3[var5] != var1);

         return true;
      }
   }

   public long getOrDefault(long var1, long var3) {
      if (this.strategy.equals(var1, 0L)) {
         return this.containsNullKey ? this.value[this.n] : var3;
      } else {
         long[] var7 = this.key;
         long var5;
         int var8;
         if ((var5 = var7[var8 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0L) {
            return var3;
         } else if (this.strategy.equals(var1, var5)) {
            return this.value[var8];
         } else {
            while((var5 = var7[var8 = var8 + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(var1, var5)) {
                  return this.value[var8];
               }
            }

            return var3;
         }
      }
   }

   public long putIfAbsent(long var1, long var3) {
      int var5 = this.find(var1);
      if (var5 >= 0) {
         return this.value[var5];
      } else {
         this.insert(-var5 - 1, var1, var3);
         return this.defRetValue;
      }
   }

   public boolean remove(long var1, long var3) {
      if (this.strategy.equals(var1, 0L)) {
         if (this.containsNullKey && var3 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         long[] var7 = this.key;
         long var5;
         int var8;
         if ((var5 = var7[var8 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0L) {
            return false;
         } else if (this.strategy.equals(var1, var5) && var3 == this.value[var8]) {
            this.removeEntry(var8);
            return true;
         } else {
            do {
               if ((var5 = var7[var8 = var8 + 1 & this.mask]) == 0L) {
                  return false;
               }
            } while(!this.strategy.equals(var1, var5) || var3 != this.value[var8]);

            this.removeEntry(var8);
            return true;
         }
      }
   }

   public boolean replace(long var1, long var3, long var5) {
      int var7 = this.find(var1);
      if (var7 >= 0 && var3 == this.value[var7]) {
         this.value[var7] = var5;
         return true;
      } else {
         return false;
      }
   }

   public long replace(long var1, long var3) {
      int var5 = this.find(var1);
      if (var5 < 0) {
         return this.defRetValue;
      } else {
         long var6 = this.value[var5];
         this.value[var5] = var3;
         return var6;
      }
   }

   public long computeIfAbsent(long var1, LongUnaryOperator var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         long var5 = var3.applyAsLong(var1);
         this.insert(-var4 - 1, var1, var5);
         return var5;
      }
   }

   public long computeIfAbsentNullable(long var1, LongFunction<? extends Long> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         Long var5 = (Long)var3.apply(var1);
         if (var5 == null) {
            return this.defRetValue;
         } else {
            long var6 = var5;
            this.insert(-var4 - 1, var1, var6);
            return var6;
         }
      }
   }

   public long computeIfPresent(long var1, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         Long var5 = (Long)var3.apply(var1, this.value[var4]);
         if (var5 == null) {
            if (this.strategy.equals(var1, 0L)) {
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

   public long compute(long var1, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      Long var5 = (Long)var3.apply(var1, var4 >= 0 ? this.value[var4] : null);
      if (var5 == null) {
         if (var4 >= 0) {
            if (this.strategy.equals(var1, 0L)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var4);
            }
         }

         return this.defRetValue;
      } else {
         long var6 = var5;
         if (var4 < 0) {
            this.insert(-var4 - 1, var1, var6);
            return var6;
         } else {
            return this.value[var4] = var6;
         }
      }
   }

   public long merge(long var1, long var3, BiFunction<? super Long, ? super Long, ? extends Long> var5) {
      Objects.requireNonNull(var5);
      int var6 = this.find(var1);
      if (var6 < 0) {
         this.insert(-var6 - 1, var1, var3);
         return var3;
      } else {
         Long var7 = (Long)var5.apply(this.value[var6], var3);
         if (var7 == null) {
            if (this.strategy.equals(var1, 0L)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var6);
            }

            return this.defRetValue;
         } else {
            return this.value[var6] = var7;
         }
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, 0L);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Long2LongMap.FastEntrySet long2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2LongOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public LongSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2LongOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            public LongIterator iterator() {
               return Long2LongOpenCustomHashMap.this.new ValueIterator();
            }

            public int size() {
               return Long2LongOpenCustomHashMap.this.size;
            }

            public boolean contains(long var1) {
               return Long2LongOpenCustomHashMap.this.containsValue(var1);
            }

            public void clear() {
               Long2LongOpenCustomHashMap.this.clear();
            }

            public void forEach(java.util.function.LongConsumer var1) {
               if (Long2LongOpenCustomHashMap.this.containsNullKey) {
                  var1.accept(Long2LongOpenCustomHashMap.this.value[Long2LongOpenCustomHashMap.this.n]);
               }

               int var2 = Long2LongOpenCustomHashMap.this.n;

               while(var2-- != 0) {
                  if (Long2LongOpenCustomHashMap.this.key[var2] != 0L) {
                     var1.accept(Long2LongOpenCustomHashMap.this.value[var2]);
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
      long[] var3 = this.value;
      int var4 = var1 - 1;
      long[] var5 = new long[var1 + 1];
      long[] var6 = new long[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == 0L);

         if (var5[var8 = HashCommon.mix(this.strategy.hashCode(var2[var7])) & var4] != 0L) {
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

   public Long2LongOpenCustomHashMap clone() {
      Long2LongOpenCustomHashMap var1;
      try {
         var1 = (Long2LongOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (long[])this.key.clone();
      var1.value = (long[])this.value.clone();
      var1.strategy = this.strategy;
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

         int var5 = this.strategy.hashCode(this.key[var3]);
         var5 ^= HashCommon.long2int(this.value[var3]);
         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += HashCommon.long2int(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      long[] var2 = this.key;
      long[] var3 = this.value;
      Long2LongOpenCustomHashMap.MapIterator var4 = new Long2LongOpenCustomHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeLong(var2[var6]);
         var1.writeLong(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      long[] var2 = this.key = new long[this.n + 1];
      long[] var3 = this.value = new long[this.n + 1];

      long var6;
      int var9;
      for(int var8 = this.size; var8-- != 0; var3[var9] = var6) {
         long var4 = var1.readLong();
         var6 = var1.readLong();
         if (this.strategy.equals(var4, 0L)) {
            var9 = this.n;
            this.containsNullKey = true;
         } else {
            for(var9 = HashCommon.mix(this.strategy.hashCode(var4)) & this.mask; var2[var9] != 0L; var9 = var9 + 1 & this.mask) {
            }
         }

         var2[var9] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Long2LongOpenCustomHashMap.MapIterator implements LongIterator {
      public ValueIterator() {
         super(null);
      }

      public long nextLong() {
         return Long2LongOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractLongSet {
      private KeySet() {
         super();
      }

      public LongIterator iterator() {
         return Long2LongOpenCustomHashMap.this.new KeyIterator();
      }

      public void forEach(java.util.function.LongConsumer var1) {
         if (Long2LongOpenCustomHashMap.this.containsNullKey) {
            var1.accept(Long2LongOpenCustomHashMap.this.key[Long2LongOpenCustomHashMap.this.n]);
         }

         int var2 = Long2LongOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            long var3 = Long2LongOpenCustomHashMap.this.key[var2];
            if (var3 != 0L) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Long2LongOpenCustomHashMap.this.size;
      }

      public boolean contains(long var1) {
         return Long2LongOpenCustomHashMap.this.containsKey(var1);
      }

      public boolean remove(long var1) {
         int var3 = Long2LongOpenCustomHashMap.this.size;
         Long2LongOpenCustomHashMap.this.remove(var1);
         return Long2LongOpenCustomHashMap.this.size != var3;
      }

      public void clear() {
         Long2LongOpenCustomHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Long2LongOpenCustomHashMap.MapIterator implements LongIterator {
      public KeyIterator() {
         super(null);
      }

      public long nextLong() {
         return Long2LongOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Long2LongMap.Entry> implements Long2LongMap.FastEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Long2LongMap.Entry> iterator() {
         return Long2LongOpenCustomHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Long2LongMap.Entry> fastIterator() {
         return Long2LongOpenCustomHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Long) {
                  long var3 = (Long)var2.getKey();
                  long var5 = (Long)var2.getValue();
                  if (Long2LongOpenCustomHashMap.this.strategy.equals(var3, 0L)) {
                     return Long2LongOpenCustomHashMap.this.containsNullKey && Long2LongOpenCustomHashMap.this.value[Long2LongOpenCustomHashMap.this.n] == var5;
                  } else {
                     long[] var9 = Long2LongOpenCustomHashMap.this.key;
                     long var7;
                     int var10;
                     if ((var7 = var9[var10 = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(var3)) & Long2LongOpenCustomHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (Long2LongOpenCustomHashMap.this.strategy.equals(var3, var7)) {
                        return Long2LongOpenCustomHashMap.this.value[var10] == var5;
                     } else {
                        while((var7 = var9[var10 = var10 + 1 & Long2LongOpenCustomHashMap.this.mask]) != 0L) {
                           if (Long2LongOpenCustomHashMap.this.strategy.equals(var3, var7)) {
                              return Long2LongOpenCustomHashMap.this.value[var10] == var5;
                           }
                        }

                        return false;
                     }
                  }
               } else {
                  return false;
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
               if (var2.getValue() != null && var2.getValue() instanceof Long) {
                  long var3 = (Long)var2.getKey();
                  long var5 = (Long)var2.getValue();
                  if (Long2LongOpenCustomHashMap.this.strategy.equals(var3, 0L)) {
                     if (Long2LongOpenCustomHashMap.this.containsNullKey && Long2LongOpenCustomHashMap.this.value[Long2LongOpenCustomHashMap.this.n] == var5) {
                        Long2LongOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     long[] var9 = Long2LongOpenCustomHashMap.this.key;
                     long var7;
                     int var10;
                     if ((var7 = var9[var10 = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(var3)) & Long2LongOpenCustomHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (Long2LongOpenCustomHashMap.this.strategy.equals(var7, var3)) {
                        if (Long2LongOpenCustomHashMap.this.value[var10] == var5) {
                           Long2LongOpenCustomHashMap.this.removeEntry(var10);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var7 = var9[var10 = var10 + 1 & Long2LongOpenCustomHashMap.this.mask]) == 0L) {
                              return false;
                           }
                        } while(!Long2LongOpenCustomHashMap.this.strategy.equals(var7, var3) || Long2LongOpenCustomHashMap.this.value[var10] != var5);

                        Long2LongOpenCustomHashMap.this.removeEntry(var10);
                        return true;
                     }
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Long2LongOpenCustomHashMap.this.size;
      }

      public void clear() {
         Long2LongOpenCustomHashMap.this.clear();
      }

      public void forEach(Consumer<? super Long2LongMap.Entry> var1) {
         if (Long2LongOpenCustomHashMap.this.containsNullKey) {
            var1.accept(new AbstractLong2LongMap.BasicEntry(Long2LongOpenCustomHashMap.this.key[Long2LongOpenCustomHashMap.this.n], Long2LongOpenCustomHashMap.this.value[Long2LongOpenCustomHashMap.this.n]));
         }

         int var2 = Long2LongOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            if (Long2LongOpenCustomHashMap.this.key[var2] != 0L) {
               var1.accept(new AbstractLong2LongMap.BasicEntry(Long2LongOpenCustomHashMap.this.key[var2], Long2LongOpenCustomHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Long2LongMap.Entry> var1) {
         AbstractLong2LongMap.BasicEntry var2 = new AbstractLong2LongMap.BasicEntry();
         if (Long2LongOpenCustomHashMap.this.containsNullKey) {
            var2.key = Long2LongOpenCustomHashMap.this.key[Long2LongOpenCustomHashMap.this.n];
            var2.value = Long2LongOpenCustomHashMap.this.value[Long2LongOpenCustomHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Long2LongOpenCustomHashMap.this.n;

         while(var3-- != 0) {
            if (Long2LongOpenCustomHashMap.this.key[var3] != 0L) {
               var2.key = Long2LongOpenCustomHashMap.this.key[var3];
               var2.value = Long2LongOpenCustomHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Long2LongOpenCustomHashMap.MapIterator implements ObjectIterator<Long2LongMap.Entry> {
      private final Long2LongOpenCustomHashMap.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Long2LongOpenCustomHashMap.this.new MapEntry();
      }

      public Long2LongOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Long2LongOpenCustomHashMap.MapIterator implements ObjectIterator<Long2LongMap.Entry> {
      private Long2LongOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Long2LongOpenCustomHashMap.MapEntry next() {
         return this.entry = Long2LongOpenCustomHashMap.this.new MapEntry(this.nextEntry());
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
         this.pos = Long2LongOpenCustomHashMap.this.n;
         this.last = -1;
         this.c = Long2LongOpenCustomHashMap.this.size;
         this.mustReturnNullKey = Long2LongOpenCustomHashMap.this.containsNullKey;
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
               return this.last = Long2LongOpenCustomHashMap.this.n;
            } else {
               long[] var1 = Long2LongOpenCustomHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0L) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               long var2 = this.wrapped.getLong(-this.pos - 1);

               int var4;
               for(var4 = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(var2)) & Long2LongOpenCustomHashMap.this.mask; !Long2LongOpenCustomHashMap.this.strategy.equals(var2, var1[var4]); var4 = var4 + 1 & Long2LongOpenCustomHashMap.this.mask) {
               }

               return var4;
            }
         }
      }

      private void shiftKeys(int var1) {
         long[] var6 = Long2LongOpenCustomHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Long2LongOpenCustomHashMap.this.mask;

            long var4;
            while(true) {
               if ((var4 = var6[var1]) == 0L) {
                  var6[var2] = 0L;
                  return;
               }

               int var3 = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(var4)) & Long2LongOpenCustomHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Long2LongOpenCustomHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new LongArrayList(2);
               }

               this.wrapped.add(var6[var1]);
            }

            var6[var2] = var4;
            Long2LongOpenCustomHashMap.this.value[var2] = Long2LongOpenCustomHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Long2LongOpenCustomHashMap.this.n) {
               Long2LongOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Long2LongOpenCustomHashMap.this.remove(this.wrapped.getLong(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Long2LongOpenCustomHashMap.this.size;
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

   final class MapEntry implements Long2LongMap.Entry, java.util.Map.Entry<Long, Long> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public long getLongKey() {
         return Long2LongOpenCustomHashMap.this.key[this.index];
      }

      public long getLongValue() {
         return Long2LongOpenCustomHashMap.this.value[this.index];
      }

      public long setValue(long var1) {
         long var3 = Long2LongOpenCustomHashMap.this.value[this.index];
         Long2LongOpenCustomHashMap.this.value[this.index] = var1;
         return var3;
      }

      /** @deprecated */
      @Deprecated
      public Long getKey() {
         return Long2LongOpenCustomHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Long getValue() {
         return Long2LongOpenCustomHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Long setValue(Long var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Long2LongOpenCustomHashMap.this.strategy.equals(Long2LongOpenCustomHashMap.this.key[this.index], (Long)var2.getKey()) && Long2LongOpenCustomHashMap.this.value[this.index] == (Long)var2.getValue();
         }
      }

      public int hashCode() {
         return Long2LongOpenCustomHashMap.this.strategy.hashCode(Long2LongOpenCustomHashMap.this.key[this.index]) ^ HashCommon.long2int(Long2LongOpenCustomHashMap.this.value[this.index]);
      }

      public String toString() {
         return Long2LongOpenCustomHashMap.this.key[this.index] + "=>" + Long2LongOpenCustomHashMap.this.value[this.index];
      }
   }
}
