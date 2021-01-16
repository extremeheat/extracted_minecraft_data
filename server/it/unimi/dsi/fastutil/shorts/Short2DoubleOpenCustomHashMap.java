package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
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
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public class Short2DoubleOpenCustomHashMap extends AbstractShort2DoubleMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient short[] key;
   protected transient double[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected ShortHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Short2DoubleMap.FastEntrySet entries;
   protected transient ShortSet keys;
   protected transient DoubleCollection values;

   public Short2DoubleOpenCustomHashMap(int var1, float var2, ShortHash.Strategy var3) {
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
            this.key = new short[this.n + 1];
            this.value = new double[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Short2DoubleOpenCustomHashMap(int var1, ShortHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Short2DoubleOpenCustomHashMap(ShortHash.Strategy var1) {
      this(16, 0.75F, var1);
   }

   public Short2DoubleOpenCustomHashMap(Map<? extends Short, ? extends Double> var1, float var2, ShortHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Short2DoubleOpenCustomHashMap(Map<? extends Short, ? extends Double> var1, ShortHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Short2DoubleOpenCustomHashMap(Short2DoubleMap var1, float var2, ShortHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Short2DoubleOpenCustomHashMap(Short2DoubleMap var1, ShortHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Short2DoubleOpenCustomHashMap(short[] var1, double[] var2, float var3, ShortHash.Strategy var4) {
      this(var1.length, var3, var4);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            this.put(var1[var5], var2[var5]);
         }

      }
   }

   public Short2DoubleOpenCustomHashMap(short[] var1, double[] var2, ShortHash.Strategy var3) {
      this(var1, var2, 0.75F, var3);
   }

   public ShortHash.Strategy strategy() {
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

   private double removeEntry(int var1) {
      double var2 = this.value[var1];
      --this.size;
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private double removeNullEntry() {
      this.containsNullKey = false;
      double var1 = this.value[this.n];
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Short, ? extends Double> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(short var1) {
      if (this.strategy.equals(var1, (short)0)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         short[] var3 = this.key;
         short var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return -(var4 + 1);
         } else if (this.strategy.equals(var1, var2)) {
            return var4;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, short var2, double var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public double put(short var1, double var2) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return this.defRetValue;
      } else {
         double var5 = this.value[var4];
         this.value[var4] = var2;
         return var5;
      }
   }

   private double addToValue(int var1, double var2) {
      double var4 = this.value[var1];
      this.value[var1] = var4 + var2;
      return var4;
   }

   public double addTo(short var1, double var2) {
      int var4;
      if (this.strategy.equals(var1, (short)0)) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var4 = this.n;
         this.containsNullKey = true;
      } else {
         short[] var6 = this.key;
         short var5;
         if ((var5 = var6[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) != 0) {
            if (this.strategy.equals(var5, var1)) {
               return this.addToValue(var4, var2);
            }

            while((var5 = var6[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var5, var1)) {
                  return this.addToValue(var4, var2);
               }
            }
         }
      }

      this.key[var4] = var1;
      this.value[var4] = this.defRetValue + var2;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return this.defRetValue;
   }

   protected final void shiftKeys(int var1) {
      short[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         short var4;
         while(true) {
            if ((var4 = var5[var1]) == 0) {
               var5[var2] = 0;
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

         var5[var2] = var4;
         this.value[var2] = this.value[var1];
      }
   }

   public double remove(short var1) {
      if (this.strategy.equals(var1, (short)0)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         short[] var3 = this.key;
         short var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return this.removeEntry(var4);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public double get(short var1) {
      if (this.strategy.equals(var1, (short)0)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         short[] var3 = this.key;
         short var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(short var1) {
      if (this.strategy.equals(var1, (short)0)) {
         return this.containsNullKey;
      } else {
         short[] var3 = this.key;
         short var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(var1, var2)) {
            return true;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(double var1) {
      double[] var3 = this.value;
      short[] var4 = this.key;
      if (this.containsNullKey && Double.doubleToLongBits(var3[this.n]) == Double.doubleToLongBits(var1)) {
         return true;
      } else {
         int var5 = this.n;

         do {
            if (var5-- == 0) {
               return false;
            }
         } while(var4[var5] == 0 || Double.doubleToLongBits(var3[var5]) != Double.doubleToLongBits(var1));

         return true;
      }
   }

   public double getOrDefault(short var1, double var2) {
      if (this.strategy.equals(var1, (short)0)) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         short[] var5 = this.key;
         short var4;
         int var6;
         if ((var4 = var5[var6 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return var2;
         } else if (this.strategy.equals(var1, var4)) {
            return this.value[var6];
         } else {
            while((var4 = var5[var6 = var6 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var4)) {
                  return this.value[var6];
               }
            }

            return var2;
         }
      }
   }

   public double putIfAbsent(short var1, double var2) {
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         this.insert(-var4 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(short var1, double var2) {
      if (this.strategy.equals(var1, (short)0)) {
         if (this.containsNullKey && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         short[] var5 = this.key;
         short var4;
         int var6;
         if ((var4 = var5[var6 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(var1, var4) && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[var6])) {
            this.removeEntry(var6);
            return true;
         } else {
            do {
               if ((var4 = var5[var6 = var6 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(!this.strategy.equals(var1, var4) || Double.doubleToLongBits(var2) != Double.doubleToLongBits(this.value[var6]));

            this.removeEntry(var6);
            return true;
         }
      }
   }

   public boolean replace(short var1, double var2, double var4) {
      int var6 = this.find(var1);
      if (var6 >= 0 && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[var6])) {
         this.value[var6] = var4;
         return true;
      } else {
         return false;
      }
   }

   public double replace(short var1, double var2) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         double var5 = this.value[var4];
         this.value[var4] = var2;
         return var5;
      }
   }

   public double computeIfAbsent(short var1, IntToDoubleFunction var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         double var4 = var2.applyAsDouble(var1);
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public double computeIfAbsentNullable(short var1, IntFunction<? extends Double> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Double var4 = (Double)var2.apply(var1);
         if (var4 == null) {
            return this.defRetValue;
         } else {
            double var5 = var4;
            this.insert(-var3 - 1, var1, var5);
            return var5;
         }
      }
   }

   public double computeIfPresent(short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Double var4 = (Double)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (this.strategy.equals(var1, (short)0)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }

            return this.defRetValue;
         } else {
            return this.value[var3] = var4;
         }
      }
   }

   public double compute(short var1, BiFunction<? super Short, ? super Double, ? extends Double> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Double var4 = (Double)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (this.strategy.equals(var1, (short)0)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }
         }

         return this.defRetValue;
      } else {
         double var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public double merge(short var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
      Objects.requireNonNull(var4);
      int var5 = this.find(var1);
      if (var5 < 0) {
         this.insert(-var5 - 1, var1, var2);
         return var2;
      } else {
         Double var6 = (Double)var4.apply(this.value[var5], var2);
         if (var6 == null) {
            if (this.strategy.equals(var1, (short)0)) {
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
         Arrays.fill(this.key, (short)0);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Short2DoubleMap.FastEntrySet short2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Short2DoubleOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ShortSet keySet() {
      if (this.keys == null) {
         this.keys = new Short2DoubleOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            public DoubleIterator iterator() {
               return Short2DoubleOpenCustomHashMap.this.new ValueIterator();
            }

            public int size() {
               return Short2DoubleOpenCustomHashMap.this.size;
            }

            public boolean contains(double var1) {
               return Short2DoubleOpenCustomHashMap.this.containsValue(var1);
            }

            public void clear() {
               Short2DoubleOpenCustomHashMap.this.clear();
            }

            public void forEach(DoubleConsumer var1) {
               if (Short2DoubleOpenCustomHashMap.this.containsNullKey) {
                  var1.accept(Short2DoubleOpenCustomHashMap.this.value[Short2DoubleOpenCustomHashMap.this.n]);
               }

               int var2 = Short2DoubleOpenCustomHashMap.this.n;

               while(var2-- != 0) {
                  if (Short2DoubleOpenCustomHashMap.this.key[var2] != 0) {
                     var1.accept(Short2DoubleOpenCustomHashMap.this.value[var2]);
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
      short[] var2 = this.key;
      double[] var3 = this.value;
      int var4 = var1 - 1;
      short[] var5 = new short[var1 + 1];
      double[] var6 = new double[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == 0);

         if (var5[var8 = HashCommon.mix(this.strategy.hashCode(var2[var7])) & var4] != 0) {
            while(var5[var8 = var8 + 1 & var4] != 0) {
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

   public Short2DoubleOpenCustomHashMap clone() {
      Short2DoubleOpenCustomHashMap var1;
      try {
         var1 = (Short2DoubleOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (short[])this.key.clone();
      var1.value = (double[])this.value.clone();
      var1.strategy = this.strategy;
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(boolean var4 = false; var2-- != 0; ++var3) {
         while(this.key[var3] == 0) {
            ++var3;
         }

         int var5 = this.strategy.hashCode(this.key[var3]);
         var5 ^= HashCommon.double2int(this.value[var3]);
         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += HashCommon.double2int(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      short[] var2 = this.key;
      double[] var3 = this.value;
      Short2DoubleOpenCustomHashMap.MapIterator var4 = new Short2DoubleOpenCustomHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeShort(var2[var6]);
         var1.writeDouble(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      short[] var2 = this.key = new short[this.n + 1];
      double[] var3 = this.value = new double[this.n + 1];

      double var5;
      int var8;
      for(int var7 = this.size; var7-- != 0; var3[var8] = var5) {
         short var4 = var1.readShort();
         var5 = var1.readDouble();
         if (this.strategy.equals(var4, (short)0)) {
            var8 = this.n;
            this.containsNullKey = true;
         } else {
            for(var8 = HashCommon.mix(this.strategy.hashCode(var4)) & this.mask; var2[var8] != 0; var8 = var8 + 1 & this.mask) {
            }
         }

         var2[var8] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Short2DoubleOpenCustomHashMap.MapIterator implements DoubleIterator {
      public ValueIterator() {
         super(null);
      }

      public double nextDouble() {
         return Short2DoubleOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractShortSet {
      private KeySet() {
         super();
      }

      public ShortIterator iterator() {
         return Short2DoubleOpenCustomHashMap.this.new KeyIterator();
      }

      public void forEach(IntConsumer var1) {
         if (Short2DoubleOpenCustomHashMap.this.containsNullKey) {
            var1.accept(Short2DoubleOpenCustomHashMap.this.key[Short2DoubleOpenCustomHashMap.this.n]);
         }

         int var2 = Short2DoubleOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            short var3 = Short2DoubleOpenCustomHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Short2DoubleOpenCustomHashMap.this.size;
      }

      public boolean contains(short var1) {
         return Short2DoubleOpenCustomHashMap.this.containsKey(var1);
      }

      public boolean remove(short var1) {
         int var2 = Short2DoubleOpenCustomHashMap.this.size;
         Short2DoubleOpenCustomHashMap.this.remove(var1);
         return Short2DoubleOpenCustomHashMap.this.size != var2;
      }

      public void clear() {
         Short2DoubleOpenCustomHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Short2DoubleOpenCustomHashMap.MapIterator implements ShortIterator {
      public KeyIterator() {
         super(null);
      }

      public short nextShort() {
         return Short2DoubleOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Short2DoubleMap.Entry> implements Short2DoubleMap.FastEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Short2DoubleMap.Entry> iterator() {
         return Short2DoubleOpenCustomHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Short2DoubleMap.Entry> fastIterator() {
         return Short2DoubleOpenCustomHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Short) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  short var3 = (Short)var2.getKey();
                  double var4 = (Double)var2.getValue();
                  if (Short2DoubleOpenCustomHashMap.this.strategy.equals(var3, (short)0)) {
                     return Short2DoubleOpenCustomHashMap.this.containsNullKey && Double.doubleToLongBits(Short2DoubleOpenCustomHashMap.this.value[Short2DoubleOpenCustomHashMap.this.n]) == Double.doubleToLongBits(var4);
                  } else {
                     short[] var7 = Short2DoubleOpenCustomHashMap.this.key;
                     short var6;
                     int var8;
                     if ((var6 = var7[var8 = HashCommon.mix(Short2DoubleOpenCustomHashMap.this.strategy.hashCode(var3)) & Short2DoubleOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Short2DoubleOpenCustomHashMap.this.strategy.equals(var3, var6)) {
                        return Double.doubleToLongBits(Short2DoubleOpenCustomHashMap.this.value[var8]) == Double.doubleToLongBits(var4);
                     } else {
                        while((var6 = var7[var8 = var8 + 1 & Short2DoubleOpenCustomHashMap.this.mask]) != 0) {
                           if (Short2DoubleOpenCustomHashMap.this.strategy.equals(var3, var6)) {
                              return Double.doubleToLongBits(Short2DoubleOpenCustomHashMap.this.value[var8]) == Double.doubleToLongBits(var4);
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
            if (var2.getKey() != null && var2.getKey() instanceof Short) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  short var3 = (Short)var2.getKey();
                  double var4 = (Double)var2.getValue();
                  if (Short2DoubleOpenCustomHashMap.this.strategy.equals(var3, (short)0)) {
                     if (Short2DoubleOpenCustomHashMap.this.containsNullKey && Double.doubleToLongBits(Short2DoubleOpenCustomHashMap.this.value[Short2DoubleOpenCustomHashMap.this.n]) == Double.doubleToLongBits(var4)) {
                        Short2DoubleOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     short[] var7 = Short2DoubleOpenCustomHashMap.this.key;
                     short var6;
                     int var8;
                     if ((var6 = var7[var8 = HashCommon.mix(Short2DoubleOpenCustomHashMap.this.strategy.hashCode(var3)) & Short2DoubleOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Short2DoubleOpenCustomHashMap.this.strategy.equals(var6, var3)) {
                        if (Double.doubleToLongBits(Short2DoubleOpenCustomHashMap.this.value[var8]) == Double.doubleToLongBits(var4)) {
                           Short2DoubleOpenCustomHashMap.this.removeEntry(var8);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var6 = var7[var8 = var8 + 1 & Short2DoubleOpenCustomHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(!Short2DoubleOpenCustomHashMap.this.strategy.equals(var6, var3) || Double.doubleToLongBits(Short2DoubleOpenCustomHashMap.this.value[var8]) != Double.doubleToLongBits(var4));

                        Short2DoubleOpenCustomHashMap.this.removeEntry(var8);
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
         return Short2DoubleOpenCustomHashMap.this.size;
      }

      public void clear() {
         Short2DoubleOpenCustomHashMap.this.clear();
      }

      public void forEach(Consumer<? super Short2DoubleMap.Entry> var1) {
         if (Short2DoubleOpenCustomHashMap.this.containsNullKey) {
            var1.accept(new AbstractShort2DoubleMap.BasicEntry(Short2DoubleOpenCustomHashMap.this.key[Short2DoubleOpenCustomHashMap.this.n], Short2DoubleOpenCustomHashMap.this.value[Short2DoubleOpenCustomHashMap.this.n]));
         }

         int var2 = Short2DoubleOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            if (Short2DoubleOpenCustomHashMap.this.key[var2] != 0) {
               var1.accept(new AbstractShort2DoubleMap.BasicEntry(Short2DoubleOpenCustomHashMap.this.key[var2], Short2DoubleOpenCustomHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Short2DoubleMap.Entry> var1) {
         AbstractShort2DoubleMap.BasicEntry var2 = new AbstractShort2DoubleMap.BasicEntry();
         if (Short2DoubleOpenCustomHashMap.this.containsNullKey) {
            var2.key = Short2DoubleOpenCustomHashMap.this.key[Short2DoubleOpenCustomHashMap.this.n];
            var2.value = Short2DoubleOpenCustomHashMap.this.value[Short2DoubleOpenCustomHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Short2DoubleOpenCustomHashMap.this.n;

         while(var3-- != 0) {
            if (Short2DoubleOpenCustomHashMap.this.key[var3] != 0) {
               var2.key = Short2DoubleOpenCustomHashMap.this.key[var3];
               var2.value = Short2DoubleOpenCustomHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Short2DoubleOpenCustomHashMap.MapIterator implements ObjectIterator<Short2DoubleMap.Entry> {
      private final Short2DoubleOpenCustomHashMap.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Short2DoubleOpenCustomHashMap.this.new MapEntry();
      }

      public Short2DoubleOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Short2DoubleOpenCustomHashMap.MapIterator implements ObjectIterator<Short2DoubleMap.Entry> {
      private Short2DoubleOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Short2DoubleOpenCustomHashMap.MapEntry next() {
         return this.entry = Short2DoubleOpenCustomHashMap.this.new MapEntry(this.nextEntry());
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
      ShortArrayList wrapped;

      private MapIterator() {
         super();
         this.pos = Short2DoubleOpenCustomHashMap.this.n;
         this.last = -1;
         this.c = Short2DoubleOpenCustomHashMap.this.size;
         this.mustReturnNullKey = Short2DoubleOpenCustomHashMap.this.containsNullKey;
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
               return this.last = Short2DoubleOpenCustomHashMap.this.n;
            } else {
               short[] var1 = Short2DoubleOpenCustomHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               short var2 = this.wrapped.getShort(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(Short2DoubleOpenCustomHashMap.this.strategy.hashCode(var2)) & Short2DoubleOpenCustomHashMap.this.mask; !Short2DoubleOpenCustomHashMap.this.strategy.equals(var2, var1[var3]); var3 = var3 + 1 & Short2DoubleOpenCustomHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         short[] var5 = Short2DoubleOpenCustomHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Short2DoubleOpenCustomHashMap.this.mask;

            short var4;
            while(true) {
               if ((var4 = var5[var1]) == 0) {
                  var5[var2] = 0;
                  return;
               }

               int var3 = HashCommon.mix(Short2DoubleOpenCustomHashMap.this.strategy.hashCode(var4)) & Short2DoubleOpenCustomHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Short2DoubleOpenCustomHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new ShortArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Short2DoubleOpenCustomHashMap.this.value[var2] = Short2DoubleOpenCustomHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Short2DoubleOpenCustomHashMap.this.n) {
               Short2DoubleOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Short2DoubleOpenCustomHashMap.this.remove(this.wrapped.getShort(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Short2DoubleOpenCustomHashMap.this.size;
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

   final class MapEntry implements Short2DoubleMap.Entry, java.util.Map.Entry<Short, Double> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public short getShortKey() {
         return Short2DoubleOpenCustomHashMap.this.key[this.index];
      }

      public double getDoubleValue() {
         return Short2DoubleOpenCustomHashMap.this.value[this.index];
      }

      public double setValue(double var1) {
         double var3 = Short2DoubleOpenCustomHashMap.this.value[this.index];
         Short2DoubleOpenCustomHashMap.this.value[this.index] = var1;
         return var3;
      }

      /** @deprecated */
      @Deprecated
      public Short getKey() {
         return Short2DoubleOpenCustomHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Double getValue() {
         return Short2DoubleOpenCustomHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Double setValue(Double var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Short2DoubleOpenCustomHashMap.this.strategy.equals(Short2DoubleOpenCustomHashMap.this.key[this.index], (Short)var2.getKey()) && Double.doubleToLongBits(Short2DoubleOpenCustomHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)var2.getValue());
         }
      }

      public int hashCode() {
         return Short2DoubleOpenCustomHashMap.this.strategy.hashCode(Short2DoubleOpenCustomHashMap.this.key[this.index]) ^ HashCommon.double2int(Short2DoubleOpenCustomHashMap.this.value[this.index]);
      }

      public String toString() {
         return Short2DoubleOpenCustomHashMap.this.key[this.index] + "=>" + Short2DoubleOpenCustomHashMap.this.value[this.index];
      }
   }
}
