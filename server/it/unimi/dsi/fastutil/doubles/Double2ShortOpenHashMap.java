package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.IntConsumer;

public class Double2ShortOpenHashMap extends AbstractDouble2ShortMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient double[] key;
   protected transient short[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Double2ShortMap.FastEntrySet entries;
   protected transient DoubleSet keys;
   protected transient ShortCollection values;

   public Double2ShortOpenHashMap(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new double[this.n + 1];
            this.value = new short[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Double2ShortOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Double2ShortOpenHashMap() {
      this(16, 0.75F);
   }

   public Double2ShortOpenHashMap(Map<? extends Double, ? extends Short> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Double2ShortOpenHashMap(Map<? extends Double, ? extends Short> var1) {
      this(var1, 0.75F);
   }

   public Double2ShortOpenHashMap(Double2ShortMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Double2ShortOpenHashMap(Double2ShortMap var1) {
      this(var1, 0.75F);
   }

   public Double2ShortOpenHashMap(double[] var1, short[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Double2ShortOpenHashMap(double[] var1, short[] var2) {
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

   private short removeEntry(int var1) {
      short var2 = this.value[var1];
      --this.size;
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private short removeNullEntry() {
      this.containsNullKey = false;
      short var1 = this.value[this.n];
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Double, ? extends Short> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         double[] var5 = this.key;
         double var3;
         int var6;
         if (Double.doubleToLongBits(var3 = var5[var6 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return -(var6 + 1);
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
            return var6;
         } else {
            while(Double.doubleToLongBits(var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
                  return var6;
               }
            }

            return -(var6 + 1);
         }
      }
   }

   private void insert(int var1, double var2, short var4) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var4;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public short put(double var1, short var3) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var3);
         return this.defRetValue;
      } else {
         short var5 = this.value[var4];
         this.value[var4] = var3;
         return var5;
      }
   }

   private short addToValue(int var1, short var2) {
      short var3 = this.value[var1];
      this.value[var1] = (short)(var3 + var2);
      return var3;
   }

   public short addTo(double var1, short var3) {
      int var4;
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var3);
         }

         var4 = this.n;
         this.containsNullKey = true;
      } else {
         double[] var7 = this.key;
         double var5;
         if (Double.doubleToLongBits(var5 = var7[var4 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(var5) == Double.doubleToLongBits(var1)) {
               return this.addToValue(var4, var3);
            }

            while(Double.doubleToLongBits(var5 = var7[var4 = var4 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var5) == Double.doubleToLongBits(var1)) {
                  return this.addToValue(var4, var3);
               }
            }
         }
      }

      this.key[var4] = var1;
      this.value[var4] = (short)(this.defRetValue + var3);
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return this.defRetValue;
   }

   protected final void shiftKeys(int var1) {
      double[] var6 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         double var4;
         while(true) {
            if (Double.doubleToLongBits(var4 = var6[var1]) == 0L) {
               var6[var2] = 0.0D;
               return;
            }

            int var3 = (int)HashCommon.mix(Double.doubleToRawLongBits(var4)) & this.mask;
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

   public short remove(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         double[] var5 = this.key;
         double var3;
         int var6;
         if (Double.doubleToLongBits(var3 = var5[var6 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
            return this.removeEntry(var6);
         } else {
            while(Double.doubleToLongBits(var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
                  return this.removeEntry(var6);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public short get(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         double[] var5 = this.key;
         double var3;
         int var6;
         if (Double.doubleToLongBits(var3 = var5[var6 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
            return this.value[var6];
         } else {
            while(Double.doubleToLongBits(var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
                  return this.value[var6];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         return this.containsNullKey;
      } else {
         double[] var5 = this.key;
         double var3;
         int var6;
         if (Double.doubleToLongBits(var3 = var5[var6 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
            return true;
         } else {
            while(Double.doubleToLongBits(var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(short var1) {
      short[] var2 = this.value;
      double[] var3 = this.key;
      if (this.containsNullKey && var2[this.n] == var1) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(Double.doubleToLongBits(var3[var4]) == 0L || var2[var4] != var1);

         return true;
      }
   }

   public short getOrDefault(double var1, short var3) {
      if (Double.doubleToLongBits(var1) == 0L) {
         return this.containsNullKey ? this.value[this.n] : var3;
      } else {
         double[] var6 = this.key;
         double var4;
         int var7;
         if (Double.doubleToLongBits(var4 = var6[var7 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return var3;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var4)) {
            return this.value[var7];
         } else {
            while(Double.doubleToLongBits(var4 = var6[var7 = var7 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var4)) {
                  return this.value[var7];
               }
            }

            return var3;
         }
      }
   }

   public short putIfAbsent(double var1, short var3) {
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         this.insert(-var4 - 1, var1, var3);
         return this.defRetValue;
      }
   }

   public boolean remove(double var1, short var3) {
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNullKey && var3 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         double[] var6 = this.key;
         double var4;
         int var7;
         if (Double.doubleToLongBits(var4 = var6[var7 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var4) && var3 == this.value[var7]) {
            this.removeEntry(var7);
            return true;
         } else {
            do {
               if (Double.doubleToLongBits(var4 = var6[var7 = var7 + 1 & this.mask]) == 0L) {
                  return false;
               }
            } while(Double.doubleToLongBits(var1) != Double.doubleToLongBits(var4) || var3 != this.value[var7]);

            this.removeEntry(var7);
            return true;
         }
      }
   }

   public boolean replace(double var1, short var3, short var4) {
      int var5 = this.find(var1);
      if (var5 >= 0 && var3 == this.value[var5]) {
         this.value[var5] = var4;
         return true;
      } else {
         return false;
      }
   }

   public short replace(double var1, short var3) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         short var5 = this.value[var4];
         this.value[var4] = var3;
         return var5;
      }
   }

   public short computeIfAbsent(double var1, DoubleToIntFunction var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         short var5 = SafeMath.safeIntToShort(var3.applyAsInt(var1));
         this.insert(-var4 - 1, var1, var5);
         return var5;
      }
   }

   public short computeIfAbsentNullable(double var1, DoubleFunction<? extends Short> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         Short var5 = (Short)var3.apply(var1);
         if (var5 == null) {
            return this.defRetValue;
         } else {
            short var6 = var5;
            this.insert(-var4 - 1, var1, var6);
            return var6;
         }
      }
   }

   public short computeIfPresent(double var1, BiFunction<? super Double, ? super Short, ? extends Short> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         Short var5 = (Short)var3.apply(var1, this.value[var4]);
         if (var5 == null) {
            if (Double.doubleToLongBits(var1) == 0L) {
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

   public short compute(double var1, BiFunction<? super Double, ? super Short, ? extends Short> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      Short var5 = (Short)var3.apply(var1, var4 >= 0 ? this.value[var4] : null);
      if (var5 == null) {
         if (var4 >= 0) {
            if (Double.doubleToLongBits(var1) == 0L) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var4);
            }
         }

         return this.defRetValue;
      } else {
         short var6 = var5;
         if (var4 < 0) {
            this.insert(-var4 - 1, var1, var6);
            return var6;
         } else {
            return this.value[var4] = var6;
         }
      }
   }

   public short merge(double var1, short var3, BiFunction<? super Short, ? super Short, ? extends Short> var4) {
      Objects.requireNonNull(var4);
      int var5 = this.find(var1);
      if (var5 < 0) {
         this.insert(-var5 - 1, var1, var3);
         return var3;
      } else {
         Short var6 = (Short)var4.apply(this.value[var5], var3);
         if (var6 == null) {
            if (Double.doubleToLongBits(var1) == 0L) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var5);
            }

            return this.defRetValue;
         } else {
            return this.value[var5] = var6;
         }
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, 0.0D);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Double2ShortMap.FastEntrySet double2ShortEntrySet() {
      if (this.entries == null) {
         this.entries = new Double2ShortOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public DoubleSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2ShortOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public ShortCollection values() {
      if (this.values == null) {
         this.values = new AbstractShortCollection() {
            public ShortIterator iterator() {
               return Double2ShortOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Double2ShortOpenHashMap.this.size;
            }

            public boolean contains(short var1) {
               return Double2ShortOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Double2ShortOpenHashMap.this.clear();
            }

            public void forEach(IntConsumer var1) {
               if (Double2ShortOpenHashMap.this.containsNullKey) {
                  var1.accept(Double2ShortOpenHashMap.this.value[Double2ShortOpenHashMap.this.n]);
               }

               int var2 = Double2ShortOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Double.doubleToLongBits(Double2ShortOpenHashMap.this.key[var2]) != 0L) {
                     var1.accept(Double2ShortOpenHashMap.this.value[var2]);
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
      double[] var2 = this.key;
      short[] var3 = this.value;
      int var4 = var1 - 1;
      double[] var5 = new double[var1 + 1];
      short[] var6 = new short[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(Double.doubleToLongBits(var2[var7]) == 0L);

         if (Double.doubleToLongBits(var5[var8 = (int)HashCommon.mix(Double.doubleToRawLongBits(var2[var7])) & var4]) != 0L) {
            while(Double.doubleToLongBits(var5[var8 = var8 + 1 & var4]) != 0L) {
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

   public Double2ShortOpenHashMap clone() {
      Double2ShortOpenHashMap var1;
      try {
         var1 = (Double2ShortOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (double[])this.key.clone();
      var1.value = (short[])this.value.clone();
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(boolean var4 = false; var2-- != 0; ++var3) {
         while(Double.doubleToLongBits(this.key[var3]) == 0L) {
            ++var3;
         }

         int var5 = HashCommon.double2int(this.key[var3]);
         var5 ^= this.value[var3];
         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n];
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      double[] var2 = this.key;
      short[] var3 = this.value;
      Double2ShortOpenHashMap.MapIterator var4 = new Double2ShortOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeDouble(var2[var6]);
         var1.writeShort(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      double[] var2 = this.key = new double[this.n + 1];
      short[] var3 = this.value = new short[this.n + 1];

      short var6;
      int var8;
      for(int var7 = this.size; var7-- != 0; var3[var8] = var6) {
         double var4 = var1.readDouble();
         var6 = var1.readShort();
         if (Double.doubleToLongBits(var4) == 0L) {
            var8 = this.n;
            this.containsNullKey = true;
         } else {
            for(var8 = (int)HashCommon.mix(Double.doubleToRawLongBits(var4)) & this.mask; Double.doubleToLongBits(var2[var8]) != 0L; var8 = var8 + 1 & this.mask) {
            }
         }

         var2[var8] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Double2ShortOpenHashMap.MapIterator implements ShortIterator {
      public ValueIterator() {
         super(null);
      }

      public short nextShort() {
         return Double2ShortOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractDoubleSet {
      private KeySet() {
         super();
      }

      public DoubleIterator iterator() {
         return Double2ShortOpenHashMap.this.new KeyIterator();
      }

      public void forEach(java.util.function.DoubleConsumer var1) {
         if (Double2ShortOpenHashMap.this.containsNullKey) {
            var1.accept(Double2ShortOpenHashMap.this.key[Double2ShortOpenHashMap.this.n]);
         }

         int var2 = Double2ShortOpenHashMap.this.n;

         while(var2-- != 0) {
            double var3 = Double2ShortOpenHashMap.this.key[var2];
            if (Double.doubleToLongBits(var3) != 0L) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Double2ShortOpenHashMap.this.size;
      }

      public boolean contains(double var1) {
         return Double2ShortOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(double var1) {
         int var3 = Double2ShortOpenHashMap.this.size;
         Double2ShortOpenHashMap.this.remove(var1);
         return Double2ShortOpenHashMap.this.size != var3;
      }

      public void clear() {
         Double2ShortOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Double2ShortOpenHashMap.MapIterator implements DoubleIterator {
      public KeyIterator() {
         super(null);
      }

      public double nextDouble() {
         return Double2ShortOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Double2ShortMap.Entry> implements Double2ShortMap.FastEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Double2ShortMap.Entry> iterator() {
         return Double2ShortOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Double2ShortMap.Entry> fastIterator() {
         return Double2ShortOpenHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Double) {
               if (var2.getValue() != null && var2.getValue() instanceof Short) {
                  double var3 = (Double)var2.getKey();
                  short var5 = (Short)var2.getValue();
                  if (Double.doubleToLongBits(var3) == 0L) {
                     return Double2ShortOpenHashMap.this.containsNullKey && Double2ShortOpenHashMap.this.value[Double2ShortOpenHashMap.this.n] == var5;
                  } else {
                     double[] var8 = Double2ShortOpenHashMap.this.key;
                     double var6;
                     int var9;
                     if (Double.doubleToLongBits(var6 = var8[var9 = (int)HashCommon.mix(Double.doubleToRawLongBits(var3)) & Double2ShortOpenHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (Double.doubleToLongBits(var3) == Double.doubleToLongBits(var6)) {
                        return Double2ShortOpenHashMap.this.value[var9] == var5;
                     } else {
                        while(Double.doubleToLongBits(var6 = var8[var9 = var9 + 1 & Double2ShortOpenHashMap.this.mask]) != 0L) {
                           if (Double.doubleToLongBits(var3) == Double.doubleToLongBits(var6)) {
                              return Double2ShortOpenHashMap.this.value[var9] == var5;
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
            if (var2.getKey() != null && var2.getKey() instanceof Double) {
               if (var2.getValue() != null && var2.getValue() instanceof Short) {
                  double var3 = (Double)var2.getKey();
                  short var5 = (Short)var2.getValue();
                  if (Double.doubleToLongBits(var3) == 0L) {
                     if (Double2ShortOpenHashMap.this.containsNullKey && Double2ShortOpenHashMap.this.value[Double2ShortOpenHashMap.this.n] == var5) {
                        Double2ShortOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     double[] var8 = Double2ShortOpenHashMap.this.key;
                     double var6;
                     int var9;
                     if (Double.doubleToLongBits(var6 = var8[var9 = (int)HashCommon.mix(Double.doubleToRawLongBits(var3)) & Double2ShortOpenHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (Double.doubleToLongBits(var6) == Double.doubleToLongBits(var3)) {
                        if (Double2ShortOpenHashMap.this.value[var9] == var5) {
                           Double2ShortOpenHashMap.this.removeEntry(var9);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if (Double.doubleToLongBits(var6 = var8[var9 = var9 + 1 & Double2ShortOpenHashMap.this.mask]) == 0L) {
                              return false;
                           }
                        } while(Double.doubleToLongBits(var6) != Double.doubleToLongBits(var3) || Double2ShortOpenHashMap.this.value[var9] != var5);

                        Double2ShortOpenHashMap.this.removeEntry(var9);
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
         return Double2ShortOpenHashMap.this.size;
      }

      public void clear() {
         Double2ShortOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Double2ShortMap.Entry> var1) {
         if (Double2ShortOpenHashMap.this.containsNullKey) {
            var1.accept(new AbstractDouble2ShortMap.BasicEntry(Double2ShortOpenHashMap.this.key[Double2ShortOpenHashMap.this.n], Double2ShortOpenHashMap.this.value[Double2ShortOpenHashMap.this.n]));
         }

         int var2 = Double2ShortOpenHashMap.this.n;

         while(var2-- != 0) {
            if (Double.doubleToLongBits(Double2ShortOpenHashMap.this.key[var2]) != 0L) {
               var1.accept(new AbstractDouble2ShortMap.BasicEntry(Double2ShortOpenHashMap.this.key[var2], Double2ShortOpenHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Double2ShortMap.Entry> var1) {
         AbstractDouble2ShortMap.BasicEntry var2 = new AbstractDouble2ShortMap.BasicEntry();
         if (Double2ShortOpenHashMap.this.containsNullKey) {
            var2.key = Double2ShortOpenHashMap.this.key[Double2ShortOpenHashMap.this.n];
            var2.value = Double2ShortOpenHashMap.this.value[Double2ShortOpenHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Double2ShortOpenHashMap.this.n;

         while(var3-- != 0) {
            if (Double.doubleToLongBits(Double2ShortOpenHashMap.this.key[var3]) != 0L) {
               var2.key = Double2ShortOpenHashMap.this.key[var3];
               var2.value = Double2ShortOpenHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Double2ShortOpenHashMap.MapIterator implements ObjectIterator<Double2ShortMap.Entry> {
      private final Double2ShortOpenHashMap.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Double2ShortOpenHashMap.this.new MapEntry();
      }

      public Double2ShortOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Double2ShortOpenHashMap.MapIterator implements ObjectIterator<Double2ShortMap.Entry> {
      private Double2ShortOpenHashMap.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Double2ShortOpenHashMap.MapEntry next() {
         return this.entry = Double2ShortOpenHashMap.this.new MapEntry(this.nextEntry());
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
      DoubleArrayList wrapped;

      private MapIterator() {
         super();
         this.pos = Double2ShortOpenHashMap.this.n;
         this.last = -1;
         this.c = Double2ShortOpenHashMap.this.size;
         this.mustReturnNullKey = Double2ShortOpenHashMap.this.containsNullKey;
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
               return this.last = Double2ShortOpenHashMap.this.n;
            } else {
               double[] var1 = Double2ShortOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (Double.doubleToLongBits(var1[this.pos]) != 0L) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               double var2 = this.wrapped.getDouble(-this.pos - 1);

               int var4;
               for(var4 = (int)HashCommon.mix(Double.doubleToRawLongBits(var2)) & Double2ShortOpenHashMap.this.mask; Double.doubleToLongBits(var2) != Double.doubleToLongBits(var1[var4]); var4 = var4 + 1 & Double2ShortOpenHashMap.this.mask) {
               }

               return var4;
            }
         }
      }

      private void shiftKeys(int var1) {
         double[] var6 = Double2ShortOpenHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Double2ShortOpenHashMap.this.mask;

            double var4;
            while(true) {
               if (Double.doubleToLongBits(var4 = var6[var1]) == 0L) {
                  var6[var2] = 0.0D;
                  return;
               }

               int var3 = (int)HashCommon.mix(Double.doubleToRawLongBits(var4)) & Double2ShortOpenHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Double2ShortOpenHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new DoubleArrayList(2);
               }

               this.wrapped.add(var6[var1]);
            }

            var6[var2] = var4;
            Double2ShortOpenHashMap.this.value[var2] = Double2ShortOpenHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Double2ShortOpenHashMap.this.n) {
               Double2ShortOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Double2ShortOpenHashMap.this.remove(this.wrapped.getDouble(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Double2ShortOpenHashMap.this.size;
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

   final class MapEntry implements Double2ShortMap.Entry, java.util.Map.Entry<Double, Short> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public double getDoubleKey() {
         return Double2ShortOpenHashMap.this.key[this.index];
      }

      public short getShortValue() {
         return Double2ShortOpenHashMap.this.value[this.index];
      }

      public short setValue(short var1) {
         short var2 = Double2ShortOpenHashMap.this.value[this.index];
         Double2ShortOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Double getKey() {
         return Double2ShortOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Short getValue() {
         return Double2ShortOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Short setValue(Short var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Double.doubleToLongBits(Double2ShortOpenHashMap.this.key[this.index]) == Double.doubleToLongBits((Double)var2.getKey()) && Double2ShortOpenHashMap.this.value[this.index] == (Short)var2.getValue();
         }
      }

      public int hashCode() {
         return HashCommon.double2int(Double2ShortOpenHashMap.this.key[this.index]) ^ Double2ShortOpenHashMap.this.value[this.index];
      }

      public String toString() {
         return Double2ShortOpenHashMap.this.key[this.index] + "=>" + Double2ShortOpenHashMap.this.value[this.index];
      }
   }
}
