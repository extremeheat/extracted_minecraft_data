package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
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
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public class Int2BooleanOpenCustomHashMap extends AbstractInt2BooleanMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient int[] key;
   protected transient boolean[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected IntHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Int2BooleanMap.FastEntrySet entries;
   protected transient IntSet keys;
   protected transient BooleanCollection values;

   public Int2BooleanOpenCustomHashMap(int var1, float var2, IntHash.Strategy var3) {
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
            this.key = new int[this.n + 1];
            this.value = new boolean[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Int2BooleanOpenCustomHashMap(int var1, IntHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Int2BooleanOpenCustomHashMap(IntHash.Strategy var1) {
      this(16, 0.75F, var1);
   }

   public Int2BooleanOpenCustomHashMap(Map<? extends Integer, ? extends Boolean> var1, float var2, IntHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Int2BooleanOpenCustomHashMap(Map<? extends Integer, ? extends Boolean> var1, IntHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Int2BooleanOpenCustomHashMap(Int2BooleanMap var1, float var2, IntHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Int2BooleanOpenCustomHashMap(Int2BooleanMap var1, IntHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public Int2BooleanOpenCustomHashMap(int[] var1, boolean[] var2, float var3, IntHash.Strategy var4) {
      this(var1.length, var3, var4);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            this.put(var1[var5], var2[var5]);
         }

      }
   }

   public Int2BooleanOpenCustomHashMap(int[] var1, boolean[] var2, IntHash.Strategy var3) {
      this(var1, var2, 0.75F, var3);
   }

   public IntHash.Strategy strategy() {
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

   private boolean removeEntry(int var1) {
      boolean var2 = this.value[var1];
      --this.size;
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private boolean removeNullEntry() {
      this.containsNullKey = false;
      boolean var1 = this.value[this.n];
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Integer, ? extends Boolean> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(int var1) {
      if (this.strategy.equals(var1, 0)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         int[] var3 = this.key;
         int var2;
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

   private void insert(int var1, int var2, boolean var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public boolean put(int var1, boolean var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         boolean var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   protected final void shiftKeys(int var1) {
      int[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         int var4;
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

   public boolean remove(int var1) {
      if (this.strategy.equals(var1, 0)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         int[] var3 = this.key;
         int var2;
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

   public boolean get(int var1) {
      if (this.strategy.equals(var1, 0)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         int[] var3 = this.key;
         int var2;
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

   public boolean containsKey(int var1) {
      if (this.strategy.equals(var1, 0)) {
         return this.containsNullKey;
      } else {
         int[] var3 = this.key;
         int var2;
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

   public boolean containsValue(boolean var1) {
      boolean[] var2 = this.value;
      int[] var3 = this.key;
      if (this.containsNullKey && var2[this.n] == var1) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(var3[var4] == 0 || var2[var4] != var1);

         return true;
      }
   }

   public boolean getOrDefault(int var1, boolean var2) {
      if (this.strategy.equals(var1, 0)) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         int[] var4 = this.key;
         int var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return var2;
         } else if (this.strategy.equals(var1, var3)) {
            return this.value[var5];
         } else {
            while((var3 = var4[var5 = var5 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var3)) {
                  return this.value[var5];
               }
            }

            return var2;
         }
      }
   }

   public boolean putIfAbsent(int var1, boolean var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(int var1, boolean var2) {
      if (this.strategy.equals(var1, 0)) {
         if (this.containsNullKey && var2 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         int[] var4 = this.key;
         int var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(var1, var3) && var2 == this.value[var5]) {
            this.removeEntry(var5);
            return true;
         } else {
            do {
               if ((var3 = var4[var5 = var5 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(!this.strategy.equals(var1, var3) || var2 != this.value[var5]);

            this.removeEntry(var5);
            return true;
         }
      }
   }

   public boolean replace(int var1, boolean var2, boolean var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public boolean replace(int var1, boolean var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         boolean var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public boolean computeIfAbsent(int var1, IntPredicate var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         boolean var4 = var2.test(var1);
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public boolean computeIfAbsentNullable(int var1, IntFunction<? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Boolean var4 = (Boolean)var2.apply(var1);
         if (var4 == null) {
            return this.defRetValue;
         } else {
            boolean var5 = var4;
            this.insert(-var3 - 1, var1, var5);
            return var5;
         }
      }
   }

   public boolean computeIfPresent(int var1, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Boolean var4 = (Boolean)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (this.strategy.equals(var1, 0)) {
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

   public boolean compute(int var1, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Boolean var4 = (Boolean)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (this.strategy.equals(var1, 0)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }
         }

         return this.defRetValue;
      } else {
         boolean var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public boolean merge(int var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      } else {
         Boolean var5 = (Boolean)var3.apply(this.value[var4], var2);
         if (var5 == null) {
            if (this.strategy.equals(var1, 0)) {
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

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, 0);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Int2BooleanMap.FastEntrySet int2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2BooleanOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public IntSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2BooleanOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new AbstractBooleanCollection() {
            public BooleanIterator iterator() {
               return Int2BooleanOpenCustomHashMap.this.new ValueIterator();
            }

            public int size() {
               return Int2BooleanOpenCustomHashMap.this.size;
            }

            public boolean contains(boolean var1) {
               return Int2BooleanOpenCustomHashMap.this.containsValue(var1);
            }

            public void clear() {
               Int2BooleanOpenCustomHashMap.this.clear();
            }

            public void forEach(BooleanConsumer var1) {
               if (Int2BooleanOpenCustomHashMap.this.containsNullKey) {
                  var1.accept(Int2BooleanOpenCustomHashMap.this.value[Int2BooleanOpenCustomHashMap.this.n]);
               }

               int var2 = Int2BooleanOpenCustomHashMap.this.n;

               while(var2-- != 0) {
                  if (Int2BooleanOpenCustomHashMap.this.key[var2] != 0) {
                     var1.accept(Int2BooleanOpenCustomHashMap.this.value[var2]);
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
      int[] var2 = this.key;
      boolean[] var3 = this.value;
      int var4 = var1 - 1;
      int[] var5 = new int[var1 + 1];
      boolean[] var6 = new boolean[var1 + 1];
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

   public Int2BooleanOpenCustomHashMap clone() {
      Int2BooleanOpenCustomHashMap var1;
      try {
         var1 = (Int2BooleanOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (int[])this.key.clone();
      var1.value = (boolean[])this.value.clone();
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
         var5 ^= this.value[var3] ? 1231 : 1237;
         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] ? 1231 : 1237;
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      int[] var2 = this.key;
      boolean[] var3 = this.value;
      Int2BooleanOpenCustomHashMap.MapIterator var4 = new Int2BooleanOpenCustomHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeInt(var2[var6]);
         var1.writeBoolean(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      int[] var2 = this.key = new int[this.n + 1];
      boolean[] var3 = this.value = new boolean[this.n + 1];

      boolean var5;
      int var7;
      for(int var6 = this.size; var6-- != 0; var3[var7] = var5) {
         int var4 = var1.readInt();
         var5 = var1.readBoolean();
         if (this.strategy.equals(var4, 0)) {
            var7 = this.n;
            this.containsNullKey = true;
         } else {
            for(var7 = HashCommon.mix(this.strategy.hashCode(var4)) & this.mask; var2[var7] != 0; var7 = var7 + 1 & this.mask) {
            }
         }

         var2[var7] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Int2BooleanOpenCustomHashMap.MapIterator implements BooleanIterator {
      public ValueIterator() {
         super(null);
      }

      public boolean nextBoolean() {
         return Int2BooleanOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractIntSet {
      private KeySet() {
         super();
      }

      public IntIterator iterator() {
         return Int2BooleanOpenCustomHashMap.this.new KeyIterator();
      }

      public void forEach(java.util.function.IntConsumer var1) {
         if (Int2BooleanOpenCustomHashMap.this.containsNullKey) {
            var1.accept(Int2BooleanOpenCustomHashMap.this.key[Int2BooleanOpenCustomHashMap.this.n]);
         }

         int var2 = Int2BooleanOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            int var3 = Int2BooleanOpenCustomHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Int2BooleanOpenCustomHashMap.this.size;
      }

      public boolean contains(int var1) {
         return Int2BooleanOpenCustomHashMap.this.containsKey(var1);
      }

      public boolean remove(int var1) {
         int var2 = Int2BooleanOpenCustomHashMap.this.size;
         Int2BooleanOpenCustomHashMap.this.remove(var1);
         return Int2BooleanOpenCustomHashMap.this.size != var2;
      }

      public void clear() {
         Int2BooleanOpenCustomHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Int2BooleanOpenCustomHashMap.MapIterator implements IntIterator {
      public KeyIterator() {
         super(null);
      }

      public int nextInt() {
         return Int2BooleanOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Int2BooleanMap.Entry> implements Int2BooleanMap.FastEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Int2BooleanMap.Entry> iterator() {
         return Int2BooleanOpenCustomHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Int2BooleanMap.Entry> fastIterator() {
         return Int2BooleanOpenCustomHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Integer) {
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  int var3 = (Integer)var2.getKey();
                  boolean var4 = (Boolean)var2.getValue();
                  if (Int2BooleanOpenCustomHashMap.this.strategy.equals(var3, 0)) {
                     return Int2BooleanOpenCustomHashMap.this.containsNullKey && Int2BooleanOpenCustomHashMap.this.value[Int2BooleanOpenCustomHashMap.this.n] == var4;
                  } else {
                     int[] var6 = Int2BooleanOpenCustomHashMap.this.key;
                     int var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(Int2BooleanOpenCustomHashMap.this.strategy.hashCode(var3)) & Int2BooleanOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Int2BooleanOpenCustomHashMap.this.strategy.equals(var3, var5)) {
                        return Int2BooleanOpenCustomHashMap.this.value[var7] == var4;
                     } else {
                        while((var5 = var6[var7 = var7 + 1 & Int2BooleanOpenCustomHashMap.this.mask]) != 0) {
                           if (Int2BooleanOpenCustomHashMap.this.strategy.equals(var3, var5)) {
                              return Int2BooleanOpenCustomHashMap.this.value[var7] == var4;
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
            if (var2.getKey() != null && var2.getKey() instanceof Integer) {
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  int var3 = (Integer)var2.getKey();
                  boolean var4 = (Boolean)var2.getValue();
                  if (Int2BooleanOpenCustomHashMap.this.strategy.equals(var3, 0)) {
                     if (Int2BooleanOpenCustomHashMap.this.containsNullKey && Int2BooleanOpenCustomHashMap.this.value[Int2BooleanOpenCustomHashMap.this.n] == var4) {
                        Int2BooleanOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     int[] var6 = Int2BooleanOpenCustomHashMap.this.key;
                     int var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(Int2BooleanOpenCustomHashMap.this.strategy.hashCode(var3)) & Int2BooleanOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Int2BooleanOpenCustomHashMap.this.strategy.equals(var5, var3)) {
                        if (Int2BooleanOpenCustomHashMap.this.value[var7] == var4) {
                           Int2BooleanOpenCustomHashMap.this.removeEntry(var7);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var5 = var6[var7 = var7 + 1 & Int2BooleanOpenCustomHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(!Int2BooleanOpenCustomHashMap.this.strategy.equals(var5, var3) || Int2BooleanOpenCustomHashMap.this.value[var7] != var4);

                        Int2BooleanOpenCustomHashMap.this.removeEntry(var7);
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
         return Int2BooleanOpenCustomHashMap.this.size;
      }

      public void clear() {
         Int2BooleanOpenCustomHashMap.this.clear();
      }

      public void forEach(Consumer<? super Int2BooleanMap.Entry> var1) {
         if (Int2BooleanOpenCustomHashMap.this.containsNullKey) {
            var1.accept(new AbstractInt2BooleanMap.BasicEntry(Int2BooleanOpenCustomHashMap.this.key[Int2BooleanOpenCustomHashMap.this.n], Int2BooleanOpenCustomHashMap.this.value[Int2BooleanOpenCustomHashMap.this.n]));
         }

         int var2 = Int2BooleanOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            if (Int2BooleanOpenCustomHashMap.this.key[var2] != 0) {
               var1.accept(new AbstractInt2BooleanMap.BasicEntry(Int2BooleanOpenCustomHashMap.this.key[var2], Int2BooleanOpenCustomHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Int2BooleanMap.Entry> var1) {
         AbstractInt2BooleanMap.BasicEntry var2 = new AbstractInt2BooleanMap.BasicEntry();
         if (Int2BooleanOpenCustomHashMap.this.containsNullKey) {
            var2.key = Int2BooleanOpenCustomHashMap.this.key[Int2BooleanOpenCustomHashMap.this.n];
            var2.value = Int2BooleanOpenCustomHashMap.this.value[Int2BooleanOpenCustomHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Int2BooleanOpenCustomHashMap.this.n;

         while(var3-- != 0) {
            if (Int2BooleanOpenCustomHashMap.this.key[var3] != 0) {
               var2.key = Int2BooleanOpenCustomHashMap.this.key[var3];
               var2.value = Int2BooleanOpenCustomHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Int2BooleanOpenCustomHashMap.MapIterator implements ObjectIterator<Int2BooleanMap.Entry> {
      private final Int2BooleanOpenCustomHashMap.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Int2BooleanOpenCustomHashMap.this.new MapEntry();
      }

      public Int2BooleanOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Int2BooleanOpenCustomHashMap.MapIterator implements ObjectIterator<Int2BooleanMap.Entry> {
      private Int2BooleanOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Int2BooleanOpenCustomHashMap.MapEntry next() {
         return this.entry = Int2BooleanOpenCustomHashMap.this.new MapEntry(this.nextEntry());
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
      IntArrayList wrapped;

      private MapIterator() {
         super();
         this.pos = Int2BooleanOpenCustomHashMap.this.n;
         this.last = -1;
         this.c = Int2BooleanOpenCustomHashMap.this.size;
         this.mustReturnNullKey = Int2BooleanOpenCustomHashMap.this.containsNullKey;
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
               return this.last = Int2BooleanOpenCustomHashMap.this.n;
            } else {
               int[] var1 = Int2BooleanOpenCustomHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               int var2 = this.wrapped.getInt(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(Int2BooleanOpenCustomHashMap.this.strategy.hashCode(var2)) & Int2BooleanOpenCustomHashMap.this.mask; !Int2BooleanOpenCustomHashMap.this.strategy.equals(var2, var1[var3]); var3 = var3 + 1 & Int2BooleanOpenCustomHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         int[] var5 = Int2BooleanOpenCustomHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Int2BooleanOpenCustomHashMap.this.mask;

            int var4;
            while(true) {
               if ((var4 = var5[var1]) == 0) {
                  var5[var2] = 0;
                  return;
               }

               int var3 = HashCommon.mix(Int2BooleanOpenCustomHashMap.this.strategy.hashCode(var4)) & Int2BooleanOpenCustomHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Int2BooleanOpenCustomHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new IntArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Int2BooleanOpenCustomHashMap.this.value[var2] = Int2BooleanOpenCustomHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Int2BooleanOpenCustomHashMap.this.n) {
               Int2BooleanOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Int2BooleanOpenCustomHashMap.this.remove(this.wrapped.getInt(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Int2BooleanOpenCustomHashMap.this.size;
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

   final class MapEntry implements Int2BooleanMap.Entry, java.util.Map.Entry<Integer, Boolean> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public int getIntKey() {
         return Int2BooleanOpenCustomHashMap.this.key[this.index];
      }

      public boolean getBooleanValue() {
         return Int2BooleanOpenCustomHashMap.this.value[this.index];
      }

      public boolean setValue(boolean var1) {
         boolean var2 = Int2BooleanOpenCustomHashMap.this.value[this.index];
         Int2BooleanOpenCustomHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Integer getKey() {
         return Int2BooleanOpenCustomHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Boolean getValue() {
         return Int2BooleanOpenCustomHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Boolean setValue(Boolean var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Int2BooleanOpenCustomHashMap.this.strategy.equals(Int2BooleanOpenCustomHashMap.this.key[this.index], (Integer)var2.getKey()) && Int2BooleanOpenCustomHashMap.this.value[this.index] == (Boolean)var2.getValue();
         }
      }

      public int hashCode() {
         return Int2BooleanOpenCustomHashMap.this.strategy.hashCode(Int2BooleanOpenCustomHashMap.this.key[this.index]) ^ (Int2BooleanOpenCustomHashMap.this.value[this.index] ? 1231 : 1237);
      }

      public String toString() {
         return Int2BooleanOpenCustomHashMap.this.key[this.index] + "=>" + Int2BooleanOpenCustomHashMap.this.value[this.index];
      }
   }
}
