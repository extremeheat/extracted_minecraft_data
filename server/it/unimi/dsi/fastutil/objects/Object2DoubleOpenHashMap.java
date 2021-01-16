package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
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
import java.util.function.ToDoubleFunction;

public class Object2DoubleOpenHashMap<K> extends AbstractObject2DoubleMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient double[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Object2DoubleMap.FastEntrySet<K> entries;
   protected transient ObjectSet<K> keys;
   protected transient DoubleCollection values;

   public Object2DoubleOpenHashMap(int var1, float var2) {
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
            this.value = new double[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Object2DoubleOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Object2DoubleOpenHashMap() {
      this(16, 0.75F);
   }

   public Object2DoubleOpenHashMap(Map<? extends K, ? extends Double> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Object2DoubleOpenHashMap(Map<? extends K, ? extends Double> var1) {
      this(var1, 0.75F);
   }

   public Object2DoubleOpenHashMap(Object2DoubleMap<K> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Object2DoubleOpenHashMap(Object2DoubleMap<K> var1) {
      this(var1, 0.75F);
   }

   public Object2DoubleOpenHashMap(K[] var1, double[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Object2DoubleOpenHashMap(K[] var1, double[] var2) {
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
      this.key[this.n] = null;
      double var1 = this.value[this.n];
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends K, ? extends Double> var1) {
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
         if ((var2 = var3[var4 = HashCommon.mix(var1.hashCode()) & this.mask]) == null) {
            return -(var4 + 1);
         } else if (var1.equals(var2)) {
            return var4;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1.equals(var2)) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, K var2, double var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public double put(K var1, double var2) {
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

   public double addTo(K var1, double var2) {
      int var4;
      if (var1 == null) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var4 = this.n;
         this.containsNullKey = true;
      } else {
         Object[] var6 = this.key;
         Object var5;
         if ((var5 = var6[var4 = HashCommon.mix(var1.hashCode()) & this.mask]) != null) {
            if (var5.equals(var1)) {
               return this.addToValue(var4, var2);
            }

            while((var5 = var6[var4 = var4 + 1 & this.mask]) != null) {
               if (var5.equals(var1)) {
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
      Object[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         Object var4;
         while(true) {
            if ((var4 = var5[var1]) == null) {
               var5[var2] = null;
               return;
            }

            int var3 = HashCommon.mix(var4.hashCode()) & this.mask;
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

   public double removeDouble(Object var1) {
      if (var1 == null) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1.hashCode()) & this.mask]) == null) {
            return this.defRetValue;
         } else if (var1.equals(var2)) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1.equals(var2)) {
                  return this.removeEntry(var4);
               }
            }

            return this.defRetValue;
         }
      }
   }

   public double getDouble(Object var1) {
      if (var1 == null) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1.hashCode()) & this.mask]) == null) {
            return this.defRetValue;
         } else if (var1.equals(var2)) {
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1.equals(var2)) {
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
         if ((var2 = var3[var4 = HashCommon.mix(var1.hashCode()) & this.mask]) == null) {
            return false;
         } else if (var1.equals(var2)) {
            return true;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1.equals(var2)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(double var1) {
      double[] var3 = this.value;
      Object[] var4 = this.key;
      if (this.containsNullKey && Double.doubleToLongBits(var3[this.n]) == Double.doubleToLongBits(var1)) {
         return true;
      } else {
         int var5 = this.n;

         do {
            if (var5-- == 0) {
               return false;
            }
         } while(var4[var5] == null || Double.doubleToLongBits(var3[var5]) != Double.doubleToLongBits(var1));

         return true;
      }
   }

   public double getOrDefault(Object var1, double var2) {
      if (var1 == null) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         Object[] var5 = this.key;
         Object var4;
         int var6;
         if ((var4 = var5[var6 = HashCommon.mix(var1.hashCode()) & this.mask]) == null) {
            return var2;
         } else if (var1.equals(var4)) {
            return this.value[var6];
         } else {
            while((var4 = var5[var6 = var6 + 1 & this.mask]) != null) {
               if (var1.equals(var4)) {
                  return this.value[var6];
               }
            }

            return var2;
         }
      }
   }

   public double putIfAbsent(K var1, double var2) {
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         this.insert(-var4 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(Object var1, double var2) {
      if (var1 == null) {
         if (this.containsNullKey && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         Object[] var5 = this.key;
         Object var4;
         int var6;
         if ((var4 = var5[var6 = HashCommon.mix(var1.hashCode()) & this.mask]) == null) {
            return false;
         } else if (var1.equals(var4) && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[var6])) {
            this.removeEntry(var6);
            return true;
         } else {
            do {
               if ((var4 = var5[var6 = var6 + 1 & this.mask]) == null) {
                  return false;
               }
            } while(!var1.equals(var4) || Double.doubleToLongBits(var2) != Double.doubleToLongBits(this.value[var6]));

            this.removeEntry(var6);
            return true;
         }
      }
   }

   public boolean replace(K var1, double var2, double var4) {
      int var6 = this.find(var1);
      if (var6 >= 0 && Double.doubleToLongBits(var2) == Double.doubleToLongBits(this.value[var6])) {
         this.value[var6] = var4;
         return true;
      } else {
         return false;
      }
   }

   public double replace(K var1, double var2) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         double var5 = this.value[var4];
         this.value[var4] = var2;
         return var5;
      }
   }

   public double computeDoubleIfAbsent(K var1, ToDoubleFunction<? super K> var2) {
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

   public double computeDoubleIfPresent(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Double var4 = (Double)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (var1 == null) {
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

   public double computeDouble(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Double var4 = (Double)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (var1 == null) {
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

   public double mergeDouble(K var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
      Objects.requireNonNull(var4);
      int var5 = this.find(var1);
      if (var5 < 0) {
         this.insert(-var5 - 1, var1, var2);
         return var2;
      } else {
         Double var6 = (Double)var4.apply(this.value[var5], var2);
         if (var6 == null) {
            if (var1 == null) {
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
         Arrays.fill(this.key, (Object)null);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Object2DoubleMap.FastEntrySet<K> object2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2DoubleOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ObjectSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2DoubleOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            public DoubleIterator iterator() {
               return Object2DoubleOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Object2DoubleOpenHashMap.this.size;
            }

            public boolean contains(double var1) {
               return Object2DoubleOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Object2DoubleOpenHashMap.this.clear();
            }

            public void forEach(DoubleConsumer var1) {
               if (Object2DoubleOpenHashMap.this.containsNullKey) {
                  var1.accept(Object2DoubleOpenHashMap.this.value[Object2DoubleOpenHashMap.this.n]);
               }

               int var2 = Object2DoubleOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Object2DoubleOpenHashMap.this.key[var2] != null) {
                     var1.accept(Object2DoubleOpenHashMap.this.value[var2]);
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
      double[] var3 = this.value;
      int var4 = var1 - 1;
      Object[] var5 = new Object[var1 + 1];
      double[] var6 = new double[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == null);

         if (var5[var8 = HashCommon.mix(var2[var7].hashCode()) & var4] != null) {
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

   public Object2DoubleOpenHashMap<K> clone() {
      Object2DoubleOpenHashMap var1;
      try {
         var1 = (Object2DoubleOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (Object[])this.key.clone();
      var1.value = (double[])this.value.clone();
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
            var4 = this.key[var3].hashCode();
         }

         var4 ^= HashCommon.double2int(this.value[var3]);
         var1 += var4;
      }

      if (this.containsNullKey) {
         var1 += HashCommon.double2int(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Object[] var2 = this.key;
      double[] var3 = this.value;
      Object2DoubleOpenHashMap.MapIterator var4 = new Object2DoubleOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeObject(var2[var6]);
         var1.writeDouble(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      Object[] var2 = this.key = new Object[this.n + 1];
      double[] var3 = this.value = new double[this.n + 1];

      double var5;
      int var8;
      for(int var7 = this.size; var7-- != 0; var3[var8] = var5) {
         Object var4 = var1.readObject();
         var5 = var1.readDouble();
         if (var4 == null) {
            var8 = this.n;
            this.containsNullKey = true;
         } else {
            for(var8 = HashCommon.mix(var4.hashCode()) & this.mask; var2[var8] != null; var8 = var8 + 1 & this.mask) {
            }
         }

         var2[var8] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Object2DoubleOpenHashMap<K>.MapIterator implements DoubleIterator {
      public ValueIterator() {
         super(null);
      }

      public double nextDouble() {
         return Object2DoubleOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractObjectSet<K> {
      private KeySet() {
         super();
      }

      public ObjectIterator<K> iterator() {
         return Object2DoubleOpenHashMap.this.new KeyIterator();
      }

      public void forEach(Consumer<? super K> var1) {
         if (Object2DoubleOpenHashMap.this.containsNullKey) {
            var1.accept(Object2DoubleOpenHashMap.this.key[Object2DoubleOpenHashMap.this.n]);
         }

         int var2 = Object2DoubleOpenHashMap.this.n;

         while(var2-- != 0) {
            Object var3 = Object2DoubleOpenHashMap.this.key[var2];
            if (var3 != null) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Object2DoubleOpenHashMap.this.size;
      }

      public boolean contains(Object var1) {
         return Object2DoubleOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         int var2 = Object2DoubleOpenHashMap.this.size;
         Object2DoubleOpenHashMap.this.removeDouble(var1);
         return Object2DoubleOpenHashMap.this.size != var2;
      }

      public void clear() {
         Object2DoubleOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Object2DoubleOpenHashMap<K>.MapIterator implements ObjectIterator<K> {
      public KeyIterator() {
         super(null);
      }

      public K next() {
         return Object2DoubleOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Object2DoubleMap.Entry<K>> implements Object2DoubleMap.FastEntrySet<K> {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Object2DoubleMap.Entry<K>> iterator() {
         return Object2DoubleOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Object2DoubleMap.Entry<K>> fastIterator() {
         return Object2DoubleOpenHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getValue() != null && var2.getValue() instanceof Double) {
               Object var3 = var2.getKey();
               double var4 = (Double)var2.getValue();
               if (var3 == null) {
                  return Object2DoubleOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Object2DoubleOpenHashMap.this.value[Object2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(var4);
               } else {
                  Object[] var7 = Object2DoubleOpenHashMap.this.key;
                  Object var6;
                  int var8;
                  if ((var6 = var7[var8 = HashCommon.mix(var3.hashCode()) & Object2DoubleOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (var3.equals(var6)) {
                     return Double.doubleToLongBits(Object2DoubleOpenHashMap.this.value[var8]) == Double.doubleToLongBits(var4);
                  } else {
                     while((var6 = var7[var8 = var8 + 1 & Object2DoubleOpenHashMap.this.mask]) != null) {
                        if (var3.equals(var6)) {
                           return Double.doubleToLongBits(Object2DoubleOpenHashMap.this.value[var8]) == Double.doubleToLongBits(var4);
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
            if (var2.getValue() != null && var2.getValue() instanceof Double) {
               Object var3 = var2.getKey();
               double var4 = (Double)var2.getValue();
               if (var3 == null) {
                  if (Object2DoubleOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Object2DoubleOpenHashMap.this.value[Object2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(var4)) {
                     Object2DoubleOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  Object[] var7 = Object2DoubleOpenHashMap.this.key;
                  Object var6;
                  int var8;
                  if ((var6 = var7[var8 = HashCommon.mix(var3.hashCode()) & Object2DoubleOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (var6.equals(var3)) {
                     if (Double.doubleToLongBits(Object2DoubleOpenHashMap.this.value[var8]) == Double.doubleToLongBits(var4)) {
                        Object2DoubleOpenHashMap.this.removeEntry(var8);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((var6 = var7[var8 = var8 + 1 & Object2DoubleOpenHashMap.this.mask]) == null) {
                           return false;
                        }
                     } while(!var6.equals(var3) || Double.doubleToLongBits(Object2DoubleOpenHashMap.this.value[var8]) != Double.doubleToLongBits(var4));

                     Object2DoubleOpenHashMap.this.removeEntry(var8);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Object2DoubleOpenHashMap.this.size;
      }

      public void clear() {
         Object2DoubleOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Object2DoubleMap.Entry<K>> var1) {
         if (Object2DoubleOpenHashMap.this.containsNullKey) {
            var1.accept(new AbstractObject2DoubleMap.BasicEntry(Object2DoubleOpenHashMap.this.key[Object2DoubleOpenHashMap.this.n], Object2DoubleOpenHashMap.this.value[Object2DoubleOpenHashMap.this.n]));
         }

         int var2 = Object2DoubleOpenHashMap.this.n;

         while(var2-- != 0) {
            if (Object2DoubleOpenHashMap.this.key[var2] != null) {
               var1.accept(new AbstractObject2DoubleMap.BasicEntry(Object2DoubleOpenHashMap.this.key[var2], Object2DoubleOpenHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Object2DoubleMap.Entry<K>> var1) {
         AbstractObject2DoubleMap.BasicEntry var2 = new AbstractObject2DoubleMap.BasicEntry();
         if (Object2DoubleOpenHashMap.this.containsNullKey) {
            var2.key = Object2DoubleOpenHashMap.this.key[Object2DoubleOpenHashMap.this.n];
            var2.value = Object2DoubleOpenHashMap.this.value[Object2DoubleOpenHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Object2DoubleOpenHashMap.this.n;

         while(var3-- != 0) {
            if (Object2DoubleOpenHashMap.this.key[var3] != null) {
               var2.key = Object2DoubleOpenHashMap.this.key[var3];
               var2.value = Object2DoubleOpenHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Object2DoubleOpenHashMap<K>.MapIterator implements ObjectIterator<Object2DoubleMap.Entry<K>> {
      private final Object2DoubleOpenHashMap<K>.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Object2DoubleOpenHashMap.this.new MapEntry();
      }

      public Object2DoubleOpenHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Object2DoubleOpenHashMap<K>.MapIterator implements ObjectIterator<Object2DoubleMap.Entry<K>> {
      private Object2DoubleOpenHashMap<K>.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Object2DoubleOpenHashMap<K>.MapEntry next() {
         return this.entry = Object2DoubleOpenHashMap.this.new MapEntry(this.nextEntry());
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
      ObjectArrayList<K> wrapped;

      private MapIterator() {
         super();
         this.pos = Object2DoubleOpenHashMap.this.n;
         this.last = -1;
         this.c = Object2DoubleOpenHashMap.this.size;
         this.mustReturnNullKey = Object2DoubleOpenHashMap.this.containsNullKey;
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
               return this.last = Object2DoubleOpenHashMap.this.n;
            } else {
               Object[] var1 = Object2DoubleOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != null) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               Object var2 = this.wrapped.get(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(var2.hashCode()) & Object2DoubleOpenHashMap.this.mask; !var2.equals(var1[var3]); var3 = var3 + 1 & Object2DoubleOpenHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         Object[] var5 = Object2DoubleOpenHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Object2DoubleOpenHashMap.this.mask;

            Object var4;
            while(true) {
               if ((var4 = var5[var1]) == null) {
                  var5[var2] = null;
                  return;
               }

               int var3 = HashCommon.mix(var4.hashCode()) & Object2DoubleOpenHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Object2DoubleOpenHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new ObjectArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Object2DoubleOpenHashMap.this.value[var2] = Object2DoubleOpenHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Object2DoubleOpenHashMap.this.n) {
               Object2DoubleOpenHashMap.this.containsNullKey = false;
               Object2DoubleOpenHashMap.this.key[Object2DoubleOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Object2DoubleOpenHashMap.this.removeDouble(this.wrapped.set(-this.pos - 1, (Object)null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Object2DoubleOpenHashMap.this.size;
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

   final class MapEntry implements Object2DoubleMap.Entry<K>, java.util.Map.Entry<K, Double> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public K getKey() {
         return Object2DoubleOpenHashMap.this.key[this.index];
      }

      public double getDoubleValue() {
         return Object2DoubleOpenHashMap.this.value[this.index];
      }

      public double setValue(double var1) {
         double var3 = Object2DoubleOpenHashMap.this.value[this.index];
         Object2DoubleOpenHashMap.this.value[this.index] = var1;
         return var3;
      }

      /** @deprecated */
      @Deprecated
      public Double getValue() {
         return Object2DoubleOpenHashMap.this.value[this.index];
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
            return Objects.equals(Object2DoubleOpenHashMap.this.key[this.index], var2.getKey()) && Double.doubleToLongBits(Object2DoubleOpenHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)var2.getValue());
         }
      }

      public int hashCode() {
         return (Object2DoubleOpenHashMap.this.key[this.index] == null ? 0 : Object2DoubleOpenHashMap.this.key[this.index].hashCode()) ^ HashCommon.double2int(Object2DoubleOpenHashMap.this.value[this.index]);
      }

      public String toString() {
         return Object2DoubleOpenHashMap.this.key[this.index] + "=>" + Object2DoubleOpenHashMap.this.value[this.index];
      }
   }
}
