package it.unimi.dsi.fastutil.floats;

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
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

public class Float2DoubleOpenCustomHashMap extends AbstractFloat2DoubleMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
   protected transient double[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected FloatHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Float2DoubleMap.FastEntrySet entries;
   protected transient FloatSet keys;
   protected transient DoubleCollection values;

   public Float2DoubleOpenCustomHashMap(int var1, float var2, FloatHash.Strategy var3) {
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
            this.key = new float[this.n + 1];
            this.value = new double[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Float2DoubleOpenCustomHashMap(int var1, FloatHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Float2DoubleOpenCustomHashMap(FloatHash.Strategy var1) {
      this(16, 0.75F, var1);
   }

   public Float2DoubleOpenCustomHashMap(Map<? extends Float, ? extends Double> var1, float var2, FloatHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Float2DoubleOpenCustomHashMap(Map<? extends Float, ? extends Double> var1, FloatHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Float2DoubleOpenCustomHashMap(Float2DoubleMap var1, float var2, FloatHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Float2DoubleOpenCustomHashMap(Float2DoubleMap var1, FloatHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Float2DoubleOpenCustomHashMap(float[] var1, double[] var2, float var3, FloatHash.Strategy var4) {
      this(var1.length, var3, var4);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            this.put(var1[var5], var2[var5]);
         }

      }
   }

   public Float2DoubleOpenCustomHashMap(float[] var1, double[] var2, FloatHash.Strategy var3) {
      this(var1, var2, 0.75F, var3);
   }

   public FloatHash.Strategy strategy() {
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

   public void putAll(Map<? extends Float, ? extends Double> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(float var1) {
      if (this.strategy.equals(var1, 0.0F)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return -(var4 + 1);
         } else if (this.strategy.equals(var1, var2)) {
            return var4;
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, float var2, double var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public double put(float var1, double var2) {
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

   public double addTo(float var1, double var2) {
      int var4;
      if (this.strategy.equals(var1, 0.0F)) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var4 = this.n;
         this.containsNullKey = true;
      } else {
         float[] var6 = this.key;
         float var5;
         if (Float.floatToIntBits(var5 = var6[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) != 0) {
            if (this.strategy.equals(var5, var1)) {
               return this.addToValue(var4, var2);
            }

            while(Float.floatToIntBits(var5 = var6[var4 = var4 + 1 & this.mask]) != 0) {
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
      float[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         float var4;
         while(true) {
            if (Float.floatToIntBits(var4 = var5[var1]) == 0) {
               var5[var2] = 0.0F;
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

   public double remove(float var1) {
      if (this.strategy.equals(var1, 0.0F)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            return this.removeEntry(var4);
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return this.removeEntry(var4);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public double get(float var1) {
      if (this.strategy.equals(var1, 0.0F)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            return this.value[var4];
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(float var1) {
      if (this.strategy.equals(var1, 0.0F)) {
         return this.containsNullKey;
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(var1, var2)) {
            return true;
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
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
      float[] var4 = this.key;
      if (this.containsNullKey && Double.doubleToLongBits(var3[this.n]) == Double.doubleToLongBits(var1)) {
         return true;
      } else {
         int var5 = this.n;

         do {
            if (var5-- == 0) {
               return false;
            }
         } while(Float.floatToIntBits(var4[var5]) == 0 || Double.doubleToLongBits(var3[var5]) != Double.doubleToLongBits(var1));

         return true;
      }
   }

   public double getOrDefault(float var1, double var2) {
      if (this.strategy.equals(var1, 0.0F)) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         float[] var5 = this.key;
         float var4;
         int var6;
         if (Float.floatToIntBits(var4 = var5[var6 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return var2;
         } else if (this.strategy.equals(var1, var4)) {
            return this.value[var6];
         } else {
            while(Float.floatToIntBits(var4 = var5[var6 = var6 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var4)) {
                  return this.value[var6];
               }
            }

            return var2;
         }
      }
   }

   public double putIfAbsent(float var1, double var2) {
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         this.insert(-var4 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(float var1, double var2) {
      if (this.strategy.equals(var1, 0.0F)) {
         if (this.containsNullKey && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         float[] var5 = this.key;
         float var4;
         int var6;
         if (Float.floatToIntBits(var4 = var5[var6 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(var1, var4) && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[var6])) {
            this.removeEntry(var6);
            return true;
         } else {
            do {
               if (Float.floatToIntBits(var4 = var5[var6 = var6 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(!this.strategy.equals(var1, var4) || Double.doubleToLongBits(var2) != Double.doubleToLongBits(this.value[var6]));

            this.removeEntry(var6);
            return true;
         }
      }
   }

   public boolean replace(float var1, double var2, double var4) {
      int var6 = this.find(var1);
      if (var6 >= 0 && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[var6])) {
         this.value[var6] = var4;
         return true;
      } else {
         return false;
      }
   }

   public double replace(float var1, double var2) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         double var5 = this.value[var4];
         this.value[var4] = var2;
         return var5;
      }
   }

   public double computeIfAbsent(float var1, DoubleUnaryOperator var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         double var4 = var2.applyAsDouble((double)var1);
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public double computeIfAbsentNullable(float var1, DoubleFunction<? extends Double> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Double var4 = (Double)var2.apply((double)var1);
         if (var4 == null) {
            return this.defRetValue;
         } else {
            double var5 = var4;
            this.insert(-var3 - 1, var1, var5);
            return var5;
         }
      }
   }

   public double computeIfPresent(float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Double var4 = (Double)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (this.strategy.equals(var1, 0.0F)) {
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

   public double compute(float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Double var4 = (Double)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (this.strategy.equals(var1, 0.0F)) {
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

   public double merge(float var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
      Objects.requireNonNull(var4);
      int var5 = this.find(var1);
      if (var5 < 0) {
         this.insert(-var5 - 1, var1, var2);
         return var2;
      } else {
         Double var6 = (Double)var4.apply(this.value[var5], var2);
         if (var6 == null) {
            if (this.strategy.equals(var1, 0.0F)) {
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
         Arrays.fill(this.key, 0.0F);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Float2DoubleMap.FastEntrySet float2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2DoubleOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public FloatSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2DoubleOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            public DoubleIterator iterator() {
               return Float2DoubleOpenCustomHashMap.this.new ValueIterator();
            }

            public int size() {
               return Float2DoubleOpenCustomHashMap.this.size;
            }

            public boolean contains(double var1) {
               return Float2DoubleOpenCustomHashMap.this.containsValue(var1);
            }

            public void clear() {
               Float2DoubleOpenCustomHashMap.this.clear();
            }

            public void forEach(DoubleConsumer var1) {
               if (Float2DoubleOpenCustomHashMap.this.containsNullKey) {
                  var1.accept(Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n]);
               }

               int var2 = Float2DoubleOpenCustomHashMap.this.n;

               while(var2-- != 0) {
                  if (Float.floatToIntBits(Float2DoubleOpenCustomHashMap.this.key[var2]) != 0) {
                     var1.accept(Float2DoubleOpenCustomHashMap.this.value[var2]);
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
      float[] var2 = this.key;
      double[] var3 = this.value;
      int var4 = var1 - 1;
      float[] var5 = new float[var1 + 1];
      double[] var6 = new double[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(Float.floatToIntBits(var2[var7]) == 0);

         if (Float.floatToIntBits(var5[var8 = HashCommon.mix(this.strategy.hashCode(var2[var7])) & var4]) != 0) {
            while(Float.floatToIntBits(var5[var8 = var8 + 1 & var4]) != 0) {
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

   public Float2DoubleOpenCustomHashMap clone() {
      Float2DoubleOpenCustomHashMap var1;
      try {
         var1 = (Float2DoubleOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (float[])this.key.clone();
      var1.value = (double[])this.value.clone();
      var1.strategy = this.strategy;
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(boolean var4 = false; var2-- != 0; ++var3) {
         while(Float.floatToIntBits(this.key[var3]) == 0) {
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
      float[] var2 = this.key;
      double[] var3 = this.value;
      Float2DoubleOpenCustomHashMap.MapIterator var4 = new Float2DoubleOpenCustomHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeFloat(var2[var6]);
         var1.writeDouble(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] var2 = this.key = new float[this.n + 1];
      double[] var3 = this.value = new double[this.n + 1];

      double var5;
      int var8;
      for(int var7 = this.size; var7-- != 0; var3[var8] = var5) {
         float var4 = var1.readFloat();
         var5 = var1.readDouble();
         if (this.strategy.equals(var4, 0.0F)) {
            var8 = this.n;
            this.containsNullKey = true;
         } else {
            for(var8 = HashCommon.mix(this.strategy.hashCode(var4)) & this.mask; Float.floatToIntBits(var2[var8]) != 0; var8 = var8 + 1 & this.mask) {
            }
         }

         var2[var8] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Float2DoubleOpenCustomHashMap.MapIterator implements DoubleIterator {
      public ValueIterator() {
         super(null);
      }

      public double nextDouble() {
         return Float2DoubleOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractFloatSet {
      private KeySet() {
         super();
      }

      public FloatIterator iterator() {
         return Float2DoubleOpenCustomHashMap.this.new KeyIterator();
      }

      public void forEach(DoubleConsumer var1) {
         if (Float2DoubleOpenCustomHashMap.this.containsNullKey) {
            var1.accept((double)Float2DoubleOpenCustomHashMap.this.key[Float2DoubleOpenCustomHashMap.this.n]);
         }

         int var2 = Float2DoubleOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            float var3 = Float2DoubleOpenCustomHashMap.this.key[var2];
            if (Float.floatToIntBits(var3) != 0) {
               var1.accept((double)var3);
            }
         }

      }

      public int size() {
         return Float2DoubleOpenCustomHashMap.this.size;
      }

      public boolean contains(float var1) {
         return Float2DoubleOpenCustomHashMap.this.containsKey(var1);
      }

      public boolean remove(float var1) {
         int var2 = Float2DoubleOpenCustomHashMap.this.size;
         Float2DoubleOpenCustomHashMap.this.remove(var1);
         return Float2DoubleOpenCustomHashMap.this.size != var2;
      }

      public void clear() {
         Float2DoubleOpenCustomHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Float2DoubleOpenCustomHashMap.MapIterator implements FloatIterator {
      public KeyIterator() {
         super(null);
      }

      public float nextFloat() {
         return Float2DoubleOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Float2DoubleMap.Entry> implements Float2DoubleMap.FastEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Float2DoubleMap.Entry> iterator() {
         return Float2DoubleOpenCustomHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Float2DoubleMap.Entry> fastIterator() {
         return Float2DoubleOpenCustomHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Float) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  float var3 = (Float)var2.getKey();
                  double var4 = (Double)var2.getValue();
                  if (Float2DoubleOpenCustomHashMap.this.strategy.equals(var3, 0.0F)) {
                     return Float2DoubleOpenCustomHashMap.this.containsNullKey && Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n]) == Double.doubleToLongBits(var4);
                  } else {
                     float[] var7 = Float2DoubleOpenCustomHashMap.this.key;
                     float var6;
                     int var8;
                     if (Float.floatToIntBits(var6 = var7[var8 = HashCommon.mix(Float2DoubleOpenCustomHashMap.this.strategy.hashCode(var3)) & Float2DoubleOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Float2DoubleOpenCustomHashMap.this.strategy.equals(var3, var6)) {
                        return Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[var8]) == Double.doubleToLongBits(var4);
                     } else {
                        while(Float.floatToIntBits(var6 = var7[var8 = var8 + 1 & Float2DoubleOpenCustomHashMap.this.mask]) != 0) {
                           if (Float2DoubleOpenCustomHashMap.this.strategy.equals(var3, var6)) {
                              return Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[var8]) == Double.doubleToLongBits(var4);
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
            if (var2.getKey() != null && var2.getKey() instanceof Float) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  float var3 = (Float)var2.getKey();
                  double var4 = (Double)var2.getValue();
                  if (Float2DoubleOpenCustomHashMap.this.strategy.equals(var3, 0.0F)) {
                     if (Float2DoubleOpenCustomHashMap.this.containsNullKey && Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n]) == Double.doubleToLongBits(var4)) {
                        Float2DoubleOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     float[] var7 = Float2DoubleOpenCustomHashMap.this.key;
                     float var6;
                     int var8;
                     if (Float.floatToIntBits(var6 = var7[var8 = HashCommon.mix(Float2DoubleOpenCustomHashMap.this.strategy.hashCode(var3)) & Float2DoubleOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Float2DoubleOpenCustomHashMap.this.strategy.equals(var6, var3)) {
                        if (Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[var8]) == Double.doubleToLongBits(var4)) {
                           Float2DoubleOpenCustomHashMap.this.removeEntry(var8);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if (Float.floatToIntBits(var6 = var7[var8 = var8 + 1 & Float2DoubleOpenCustomHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(!Float2DoubleOpenCustomHashMap.this.strategy.equals(var6, var3) || Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[var8]) != Double.doubleToLongBits(var4));

                        Float2DoubleOpenCustomHashMap.this.removeEntry(var8);
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
         return Float2DoubleOpenCustomHashMap.this.size;
      }

      public void clear() {
         Float2DoubleOpenCustomHashMap.this.clear();
      }

      public void forEach(Consumer<? super Float2DoubleMap.Entry> var1) {
         if (Float2DoubleOpenCustomHashMap.this.containsNullKey) {
            var1.accept(new AbstractFloat2DoubleMap.BasicEntry(Float2DoubleOpenCustomHashMap.this.key[Float2DoubleOpenCustomHashMap.this.n], Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n]));
         }

         int var2 = Float2DoubleOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            if (Float.floatToIntBits(Float2DoubleOpenCustomHashMap.this.key[var2]) != 0) {
               var1.accept(new AbstractFloat2DoubleMap.BasicEntry(Float2DoubleOpenCustomHashMap.this.key[var2], Float2DoubleOpenCustomHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Float2DoubleMap.Entry> var1) {
         AbstractFloat2DoubleMap.BasicEntry var2 = new AbstractFloat2DoubleMap.BasicEntry();
         if (Float2DoubleOpenCustomHashMap.this.containsNullKey) {
            var2.key = Float2DoubleOpenCustomHashMap.this.key[Float2DoubleOpenCustomHashMap.this.n];
            var2.value = Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Float2DoubleOpenCustomHashMap.this.n;

         while(var3-- != 0) {
            if (Float.floatToIntBits(Float2DoubleOpenCustomHashMap.this.key[var3]) != 0) {
               var2.key = Float2DoubleOpenCustomHashMap.this.key[var3];
               var2.value = Float2DoubleOpenCustomHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Float2DoubleOpenCustomHashMap.MapIterator implements ObjectIterator<Float2DoubleMap.Entry> {
      private final Float2DoubleOpenCustomHashMap.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Float2DoubleOpenCustomHashMap.this.new MapEntry();
      }

      public Float2DoubleOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Float2DoubleOpenCustomHashMap.MapIterator implements ObjectIterator<Float2DoubleMap.Entry> {
      private Float2DoubleOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Float2DoubleOpenCustomHashMap.MapEntry next() {
         return this.entry = Float2DoubleOpenCustomHashMap.this.new MapEntry(this.nextEntry());
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
      FloatArrayList wrapped;

      private MapIterator() {
         super();
         this.pos = Float2DoubleOpenCustomHashMap.this.n;
         this.last = -1;
         this.c = Float2DoubleOpenCustomHashMap.this.size;
         this.mustReturnNullKey = Float2DoubleOpenCustomHashMap.this.containsNullKey;
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
               return this.last = Float2DoubleOpenCustomHashMap.this.n;
            } else {
               float[] var1 = Float2DoubleOpenCustomHashMap.this.key;

               while(--this.pos >= 0) {
                  if (Float.floatToIntBits(var1[this.pos]) != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               float var2 = this.wrapped.getFloat(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(Float2DoubleOpenCustomHashMap.this.strategy.hashCode(var2)) & Float2DoubleOpenCustomHashMap.this.mask; !Float2DoubleOpenCustomHashMap.this.strategy.equals(var2, var1[var3]); var3 = var3 + 1 & Float2DoubleOpenCustomHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         float[] var5 = Float2DoubleOpenCustomHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Float2DoubleOpenCustomHashMap.this.mask;

            float var4;
            while(true) {
               if (Float.floatToIntBits(var4 = var5[var1]) == 0) {
                  var5[var2] = 0.0F;
                  return;
               }

               int var3 = HashCommon.mix(Float2DoubleOpenCustomHashMap.this.strategy.hashCode(var4)) & Float2DoubleOpenCustomHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Float2DoubleOpenCustomHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new FloatArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Float2DoubleOpenCustomHashMap.this.value[var2] = Float2DoubleOpenCustomHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Float2DoubleOpenCustomHashMap.this.n) {
               Float2DoubleOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Float2DoubleOpenCustomHashMap.this.remove(this.wrapped.getFloat(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Float2DoubleOpenCustomHashMap.this.size;
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

   final class MapEntry implements Float2DoubleMap.Entry, java.util.Map.Entry<Float, Double> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public float getFloatKey() {
         return Float2DoubleOpenCustomHashMap.this.key[this.index];
      }

      public double getDoubleValue() {
         return Float2DoubleOpenCustomHashMap.this.value[this.index];
      }

      public double setValue(double var1) {
         double var3 = Float2DoubleOpenCustomHashMap.this.value[this.index];
         Float2DoubleOpenCustomHashMap.this.value[this.index] = var1;
         return var3;
      }

      /** @deprecated */
      @Deprecated
      public Float getKey() {
         return Float2DoubleOpenCustomHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Double getValue() {
         return Float2DoubleOpenCustomHashMap.this.value[this.index];
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
            return Float2DoubleOpenCustomHashMap.this.strategy.equals(Float2DoubleOpenCustomHashMap.this.key[this.index], (Float)var2.getKey()) && Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)var2.getValue());
         }
      }

      public int hashCode() {
         return Float2DoubleOpenCustomHashMap.this.strategy.hashCode(Float2DoubleOpenCustomHashMap.this.key[this.index]) ^ HashCommon.double2int(Float2DoubleOpenCustomHashMap.this.value[this.index]);
      }

      public String toString() {
         return Float2DoubleOpenCustomHashMap.this.key[this.index] + "=>" + Float2DoubleOpenCustomHashMap.this.value[this.index];
      }
   }
}
