package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

public class Double2DoubleLinkedOpenHashMap extends AbstractDouble2DoubleSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient double[] key;
   protected transient double[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int first;
   protected transient int last;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Double2DoubleSortedMap.FastSortedEntrySet entries;
   protected transient DoubleSortedSet keys;
   protected transient DoubleCollection values;

   public Double2DoubleLinkedOpenHashMap(int var1, float var2) {
      super();
      this.first = -1;
      this.last = -1;
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new double[this.n + 1];
            this.value = new double[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Double2DoubleLinkedOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Double2DoubleLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Double2DoubleLinkedOpenHashMap(Map<? extends Double, ? extends Double> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Double2DoubleLinkedOpenHashMap(Map<? extends Double, ? extends Double> var1) {
      this(var1, 0.75F);
   }

   public Double2DoubleLinkedOpenHashMap(Double2DoubleMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Double2DoubleLinkedOpenHashMap(Double2DoubleMap var1) {
      this(var1, 0.75F);
   }

   public Double2DoubleLinkedOpenHashMap(double[] var1, double[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Double2DoubleLinkedOpenHashMap(double[] var1, double[] var2) {
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
      this.fixPointers(var1);
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
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Double, ? extends Double> var1) {
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

   private void insert(int var1, double var2, double var4) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var4;
      if (this.size == 0) {
         this.first = this.last = var1;
         this.link[var1] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var1 & 4294967295L) & 4294967295L;
         this.link[var1] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var1;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public double put(double var1, double var3) {
      int var5 = this.find(var1);
      if (var5 < 0) {
         this.insert(-var5 - 1, var1, var3);
         return this.defRetValue;
      } else {
         double var6 = this.value[var5];
         this.value[var5] = var3;
         return var6;
      }
   }

   private double addToValue(int var1, double var2) {
      double var4 = this.value[var1];
      this.value[var1] = var4 + var2;
      return var4;
   }

   public double addTo(double var1, double var3) {
      int var5;
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var3);
         }

         var5 = this.n;
         this.containsNullKey = true;
      } else {
         double[] var8 = this.key;
         double var6;
         if (Double.doubleToLongBits(var6 = var8[var5 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(var6) == Double.doubleToLongBits(var1)) {
               return this.addToValue(var5, var3);
            }

            while(Double.doubleToLongBits(var6 = var8[var5 = var5 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var6) == Double.doubleToLongBits(var1)) {
                  return this.addToValue(var5, var3);
               }
            }
         }
      }

      this.key[var5] = var1;
      this.value[var5] = this.defRetValue + var3;
      if (this.size == 0) {
         this.first = this.last = var5;
         this.link[var5] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var5 & 4294967295L) & 4294967295L;
         this.link[var5] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var5;
      }

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
         this.fixPointers(var1, var2);
      }
   }

   public double remove(double var1) {
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

   private double setValue(int var1, double var2) {
      double var4 = this.value[var1];
      this.value[var1] = var2;
      return var4;
   }

   public double removeFirstDouble() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int var1 = this.first;
         this.first = (int)this.link[var1];
         if (0 <= this.first) {
            long[] var10000 = this.link;
            int var10001 = this.first;
            var10000[var10001] |= -4294967296L;
         }

         --this.size;
         double var2 = this.value[var1];
         if (var1 == this.n) {
            this.containsNullKey = false;
         } else {
            this.shiftKeys(var1);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return var2;
      }
   }

   public double removeLastDouble() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int var1 = this.last;
         this.last = (int)(this.link[var1] >>> 32);
         if (0 <= this.last) {
            long[] var10000 = this.link;
            int var10001 = this.last;
            var10000[var10001] |= 4294967295L;
         }

         --this.size;
         double var2 = this.value[var1];
         if (var1 == this.n) {
            this.containsNullKey = false;
         } else {
            this.shiftKeys(var1);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return var2;
      }
   }

   private void moveIndexToFirst(int var1) {
      if (this.size != 1 && this.first != var1) {
         long[] var10000;
         int var10001;
         if (this.last == var1) {
            this.last = (int)(this.link[var1] >>> 32);
            var10000 = this.link;
            var10001 = this.last;
            var10000[var10001] |= 4294967295L;
         } else {
            long var2 = this.link[var1];
            int var4 = (int)(var2 >>> 32);
            int var5 = (int)var2;
            var10000 = this.link;
            var10000[var4] ^= (this.link[var4] ^ var2 & 4294967295L) & 4294967295L;
            var10000 = this.link;
            var10000[var5] ^= (this.link[var5] ^ var2 & -4294967296L) & -4294967296L;
         }

         var10000 = this.link;
         var10001 = this.first;
         var10000[var10001] ^= (this.link[this.first] ^ ((long)var1 & 4294967295L) << 32) & -4294967296L;
         this.link[var1] = -4294967296L | (long)this.first & 4294967295L;
         this.first = var1;
      }
   }

   private void moveIndexToLast(int var1) {
      if (this.size != 1 && this.last != var1) {
         long[] var10000;
         int var10001;
         if (this.first == var1) {
            this.first = (int)this.link[var1];
            var10000 = this.link;
            var10001 = this.first;
            var10000[var10001] |= -4294967296L;
         } else {
            long var2 = this.link[var1];
            int var4 = (int)(var2 >>> 32);
            int var5 = (int)var2;
            var10000 = this.link;
            var10000[var4] ^= (this.link[var4] ^ var2 & 4294967295L) & 4294967295L;
            var10000 = this.link;
            var10000[var5] ^= (this.link[var5] ^ var2 & -4294967296L) & -4294967296L;
         }

         var10000 = this.link;
         var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var1 & 4294967295L) & 4294967295L;
         this.link[var1] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var1;
      }
   }

   public double getAndMoveToFirst(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         double[] var5 = this.key;
         double var3;
         int var6;
         if (Double.doubleToLongBits(var3 = var5[var6 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
            this.moveIndexToFirst(var6);
            return this.value[var6];
         } else {
            while(Double.doubleToLongBits(var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
                  this.moveIndexToFirst(var6);
                  return this.value[var6];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public double getAndMoveToLast(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         double[] var5 = this.key;
         double var3;
         int var6;
         if (Double.doubleToLongBits(var3 = var5[var6 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
            this.moveIndexToLast(var6);
            return this.value[var6];
         } else {
            while(Double.doubleToLongBits(var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var3)) {
                  this.moveIndexToLast(var6);
                  return this.value[var6];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public double putAndMoveToFirst(double var1, double var3) {
      int var5;
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var3);
         }

         this.containsNullKey = true;
         var5 = this.n;
      } else {
         double[] var8 = this.key;
         double var6;
         if (Double.doubleToLongBits(var6 = var8[var5 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(var6) == Double.doubleToLongBits(var1)) {
               this.moveIndexToFirst(var5);
               return this.setValue(var5, var3);
            }

            while(Double.doubleToLongBits(var6 = var8[var5 = var5 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var6) == Double.doubleToLongBits(var1)) {
                  this.moveIndexToFirst(var5);
                  return this.setValue(var5, var3);
               }
            }
         }
      }

      this.key[var5] = var1;
      this.value[var5] = var3;
      if (this.size == 0) {
         this.first = this.last = var5;
         this.link[var5] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.first;
         var10000[var10001] ^= (this.link[this.first] ^ ((long)var5 & 4294967295L) << 32) & -4294967296L;
         this.link[var5] = -4294967296L | (long)this.first & 4294967295L;
         this.first = var5;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public double putAndMoveToLast(double var1, double var3) {
      int var5;
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var3);
         }

         this.containsNullKey = true;
         var5 = this.n;
      } else {
         double[] var8 = this.key;
         double var6;
         if (Double.doubleToLongBits(var6 = var8[var5 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(var6) == Double.doubleToLongBits(var1)) {
               this.moveIndexToLast(var5);
               return this.setValue(var5, var3);
            }

            while(Double.doubleToLongBits(var6 = var8[var5 = var5 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var6) == Double.doubleToLongBits(var1)) {
                  this.moveIndexToLast(var5);
                  return this.setValue(var5, var3);
               }
            }
         }
      }

      this.key[var5] = var1;
      this.value[var5] = var3;
      if (this.size == 0) {
         this.first = this.last = var5;
         this.link[var5] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var5 & 4294967295L) & 4294967295L;
         this.link[var5] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var5;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public double get(double var1) {
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

   public boolean containsValue(double var1) {
      double[] var3 = this.value;
      double[] var4 = this.key;
      if (this.containsNullKey && Double.doubleToLongBits(var3[this.n]) == Double.doubleToLongBits(var1)) {
         return true;
      } else {
         int var5 = this.n;

         do {
            if (var5-- == 0) {
               return false;
            }
         } while(Double.doubleToLongBits(var4[var5]) == 0L || Double.doubleToLongBits(var3[var5]) != Double.doubleToLongBits(var1));

         return true;
      }
   }

   public double getOrDefault(double var1, double var3) {
      if (Double.doubleToLongBits(var1) == 0L) {
         return this.containsNullKey ? this.value[this.n] : var3;
      } else {
         double[] var7 = this.key;
         double var5;
         int var8;
         if (Double.doubleToLongBits(var5 = var7[var8 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return var3;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var5)) {
            return this.value[var8];
         } else {
            while(Double.doubleToLongBits(var5 = var7[var8 = var8 + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var5)) {
                  return this.value[var8];
               }
            }

            return var3;
         }
      }
   }

   public double putIfAbsent(double var1, double var3) {
      int var5 = this.find(var1);
      if (var5 >= 0) {
         return this.value[var5];
      } else {
         this.insert(-var5 - 1, var1, var3);
         return this.defRetValue;
      }
   }

   public boolean remove(double var1, double var3) {
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNullKey && Double.doubleToLongBits(var3) == Double.doubleToLongBits(this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         double[] var7 = this.key;
         double var5;
         int var8;
         if (Double.doubleToLongBits(var5 = var7[var8 = (int)HashCommon.mix(Double.doubleToRawLongBits(var1)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(var1) == Double.doubleToLongBits(var5) && Double.doubleToLongBits(var3) == Double.doubleToLongBits(this.value[var8])) {
            this.removeEntry(var8);
            return true;
         } else {
            do {
               if (Double.doubleToLongBits(var5 = var7[var8 = var8 + 1 & this.mask]) == 0L) {
                  return false;
               }
            } while(Double.doubleToLongBits(var1) != Double.doubleToLongBits(var5) || Double.doubleToLongBits(var3) != Double.doubleToLongBits(this.value[var8]));

            this.removeEntry(var8);
            return true;
         }
      }
   }

   public boolean replace(double var1, double var3, double var5) {
      int var7 = this.find(var1);
      if (var7 >= 0 && Double.doubleToLongBits(var3) == Double.doubleToLongBits(this.value[var7])) {
         this.value[var7] = var5;
         return true;
      } else {
         return false;
      }
   }

   public double replace(double var1, double var3) {
      int var5 = this.find(var1);
      if (var5 < 0) {
         return this.defRetValue;
      } else {
         double var6 = this.value[var5];
         this.value[var5] = var3;
         return var6;
      }
   }

   public double computeIfAbsent(double var1, DoubleUnaryOperator var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         double var5 = var3.applyAsDouble(var1);
         this.insert(-var4 - 1, var1, var5);
         return var5;
      }
   }

   public double computeIfAbsentNullable(double var1, DoubleFunction<? extends Double> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         Double var5 = (Double)var3.apply(var1);
         if (var5 == null) {
            return this.defRetValue;
         } else {
            double var6 = var5;
            this.insert(-var4 - 1, var1, var6);
            return var6;
         }
      }
   }

   public double computeIfPresent(double var1, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         Double var5 = (Double)var3.apply(var1, this.value[var4]);
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

   public double compute(double var1, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      Double var5 = (Double)var3.apply(var1, var4 >= 0 ? this.value[var4] : null);
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
         double var6 = var5;
         if (var4 < 0) {
            this.insert(-var4 - 1, var1, var6);
            return var6;
         } else {
            return this.value[var4] = var6;
         }
      }
   }

   public double merge(double var1, double var3, BiFunction<? super Double, ? super Double, ? extends Double> var5) {
      Objects.requireNonNull(var5);
      int var6 = this.find(var1);
      if (var6 < 0) {
         this.insert(-var6 - 1, var1, var3);
         return var3;
      } else {
         Double var7 = (Double)var5.apply(this.value[var6], var3);
         if (var7 == null) {
            if (Double.doubleToLongBits(var1) == 0L) {
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
         Arrays.fill(this.key, 0.0D);
         this.first = this.last = -1;
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   protected void fixPointers(int var1) {
      if (this.size == 0) {
         this.first = this.last = -1;
      } else {
         long[] var10000;
         int var10001;
         if (this.first == var1) {
            this.first = (int)this.link[var1];
            if (0 <= this.first) {
               var10000 = this.link;
               var10001 = this.first;
               var10000[var10001] |= -4294967296L;
            }

         } else if (this.last == var1) {
            this.last = (int)(this.link[var1] >>> 32);
            if (0 <= this.last) {
               var10000 = this.link;
               var10001 = this.last;
               var10000[var10001] |= 4294967295L;
            }

         } else {
            long var2 = this.link[var1];
            int var4 = (int)(var2 >>> 32);
            int var5 = (int)var2;
            var10000 = this.link;
            var10000[var4] ^= (this.link[var4] ^ var2 & 4294967295L) & 4294967295L;
            var10000 = this.link;
            var10000[var5] ^= (this.link[var5] ^ var2 & -4294967296L) & -4294967296L;
         }
      }
   }

   protected void fixPointers(int var1, int var2) {
      if (this.size == 1) {
         this.first = this.last = var2;
         this.link[var2] = -1L;
      } else {
         long[] var10000;
         int var10001;
         if (this.first == var1) {
            this.first = var2;
            var10000 = this.link;
            var10001 = (int)this.link[var1];
            var10000[var10001] ^= (this.link[(int)this.link[var1]] ^ ((long)var2 & 4294967295L) << 32) & -4294967296L;
            this.link[var2] = this.link[var1];
         } else if (this.last == var1) {
            this.last = var2;
            var10000 = this.link;
            var10001 = (int)(this.link[var1] >>> 32);
            var10000[var10001] ^= (this.link[(int)(this.link[var1] >>> 32)] ^ (long)var2 & 4294967295L) & 4294967295L;
            this.link[var2] = this.link[var1];
         } else {
            long var3 = this.link[var1];
            int var5 = (int)(var3 >>> 32);
            int var6 = (int)var3;
            var10000 = this.link;
            var10000[var5] ^= (this.link[var5] ^ (long)var2 & 4294967295L) & 4294967295L;
            var10000 = this.link;
            var10000[var6] ^= (this.link[var6] ^ ((long)var2 & 4294967295L) << 32) & -4294967296L;
            this.link[var2] = var3;
         }
      }
   }

   public double firstDoubleKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public double lastDoubleKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Double2DoubleSortedMap tailMap(double var1) {
      throw new UnsupportedOperationException();
   }

   public Double2DoubleSortedMap headMap(double var1) {
      throw new UnsupportedOperationException();
   }

   public Double2DoubleSortedMap subMap(double var1, double var3) {
      throw new UnsupportedOperationException();
   }

   public DoubleComparator comparator() {
      return null;
   }

   public Double2DoubleSortedMap.FastSortedEntrySet double2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Double2DoubleLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public DoubleSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2DoubleLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            public DoubleIterator iterator() {
               return Double2DoubleLinkedOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Double2DoubleLinkedOpenHashMap.this.size;
            }

            public boolean contains(double var1) {
               return Double2DoubleLinkedOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Double2DoubleLinkedOpenHashMap.this.clear();
            }

            public void forEach(java.util.function.DoubleConsumer var1) {
               if (Double2DoubleLinkedOpenHashMap.this.containsNullKey) {
                  var1.accept(Double2DoubleLinkedOpenHashMap.this.value[Double2DoubleLinkedOpenHashMap.this.n]);
               }

               int var2 = Double2DoubleLinkedOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.key[var2]) != 0L) {
                     var1.accept(Double2DoubleLinkedOpenHashMap.this.value[var2]);
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
      double[] var3 = this.value;
      int var4 = var1 - 1;
      double[] var5 = new double[var1 + 1];
      double[] var6 = new double[var1 + 1];
      int var7 = this.first;
      int var8 = -1;
      int var9 = -1;
      long[] var12 = this.link;
      long[] var13 = new long[var1 + 1];
      this.first = -1;

      int var10;
      for(int var14 = this.size; var14-- != 0; var8 = var10) {
         int var11;
         if (Double.doubleToLongBits(var2[var7]) == 0L) {
            var11 = var1;
         } else {
            for(var11 = (int)HashCommon.mix(Double.doubleToRawLongBits(var2[var7])) & var4; Double.doubleToLongBits(var5[var11]) != 0L; var11 = var11 + 1 & var4) {
            }
         }

         var5[var11] = var2[var7];
         var6[var11] = var3[var7];
         if (var8 != -1) {
            var13[var9] ^= (var13[var9] ^ (long)var11 & 4294967295L) & 4294967295L;
            var13[var11] ^= (var13[var11] ^ ((long)var9 & 4294967295L) << 32) & -4294967296L;
            var9 = var11;
         } else {
            var9 = this.first = var11;
            var13[var11] = -1L;
         }

         var10 = var7;
         var7 = (int)var12[var7];
      }

      this.link = var13;
      this.last = var9;
      if (var9 != -1) {
         var13[var9] |= 4294967295L;
      }

      this.n = var1;
      this.mask = var4;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var5;
      this.value = var6;
   }

   public Double2DoubleLinkedOpenHashMap clone() {
      Double2DoubleLinkedOpenHashMap var1;
      try {
         var1 = (Double2DoubleLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (double[])this.key.clone();
      var1.value = (double[])this.value.clone();
      var1.link = (long[])this.link.clone();
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
         var5 ^= HashCommon.double2int(this.value[var3]);
         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += HashCommon.double2int(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      double[] var2 = this.key;
      double[] var3 = this.value;
      Double2DoubleLinkedOpenHashMap.MapIterator var4 = new Double2DoubleLinkedOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeDouble(var2[var6]);
         var1.writeDouble(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      double[] var2 = this.key = new double[this.n + 1];
      double[] var3 = this.value = new double[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var10 = this.size;

      while(var10-- != 0) {
         double var6 = var1.readDouble();
         double var8 = var1.readDouble();
         int var11;
         if (Double.doubleToLongBits(var6) == 0L) {
            var11 = this.n;
            this.containsNullKey = true;
         } else {
            for(var11 = (int)HashCommon.mix(Double.doubleToRawLongBits(var6)) & this.mask; Double.doubleToLongBits(var2[var11]) != 0L; var11 = var11 + 1 & this.mask) {
            }
         }

         var2[var11] = var6;
         var3[var11] = var8;
         if (this.first != -1) {
            var4[var5] ^= (var4[var5] ^ (long)var11 & 4294967295L) & 4294967295L;
            var4[var11] ^= (var4[var11] ^ ((long)var5 & 4294967295L) << 32) & -4294967296L;
            var5 = var11;
         } else {
            var5 = this.first = var11;
            var4[var11] |= -4294967296L;
         }
      }

      this.last = var5;
      if (var5 != -1) {
         var4[var5] |= 4294967295L;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Double2DoubleLinkedOpenHashMap.MapIterator implements DoubleListIterator {
      public double previousDouble() {
         return Double2DoubleLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public double nextDouble() {
         return Double2DoubleLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractDoubleSortedSet {
      private KeySet() {
         super();
      }

      public DoubleListIterator iterator(double var1) {
         return Double2DoubleLinkedOpenHashMap.this.new KeyIterator(var1);
      }

      public DoubleListIterator iterator() {
         return Double2DoubleLinkedOpenHashMap.this.new KeyIterator();
      }

      public void forEach(java.util.function.DoubleConsumer var1) {
         if (Double2DoubleLinkedOpenHashMap.this.containsNullKey) {
            var1.accept(Double2DoubleLinkedOpenHashMap.this.key[Double2DoubleLinkedOpenHashMap.this.n]);
         }

         int var2 = Double2DoubleLinkedOpenHashMap.this.n;

         while(var2-- != 0) {
            double var3 = Double2DoubleLinkedOpenHashMap.this.key[var2];
            if (Double.doubleToLongBits(var3) != 0L) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Double2DoubleLinkedOpenHashMap.this.size;
      }

      public boolean contains(double var1) {
         return Double2DoubleLinkedOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(double var1) {
         int var3 = Double2DoubleLinkedOpenHashMap.this.size;
         Double2DoubleLinkedOpenHashMap.this.remove(var1);
         return Double2DoubleLinkedOpenHashMap.this.size != var3;
      }

      public void clear() {
         Double2DoubleLinkedOpenHashMap.this.clear();
      }

      public double firstDouble() {
         if (Double2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Double2DoubleLinkedOpenHashMap.this.key[Double2DoubleLinkedOpenHashMap.this.first];
         }
      }

      public double lastDouble() {
         if (Double2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Double2DoubleLinkedOpenHashMap.this.key[Double2DoubleLinkedOpenHashMap.this.last];
         }
      }

      public DoubleComparator comparator() {
         return null;
      }

      public DoubleSortedSet tailSet(double var1) {
         throw new UnsupportedOperationException();
      }

      public DoubleSortedSet headSet(double var1) {
         throw new UnsupportedOperationException();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Double2DoubleLinkedOpenHashMap.MapIterator implements DoubleListIterator {
      public KeyIterator(double var2) {
         super(var2, null);
      }

      public double previousDouble() {
         return Double2DoubleLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public double nextDouble() {
         return Double2DoubleLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Double2DoubleMap.Entry> implements Double2DoubleSortedMap.FastSortedEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Double2DoubleMap.Entry> iterator() {
         return Double2DoubleLinkedOpenHashMap.this.new EntryIterator();
      }

      public Comparator<? super Double2DoubleMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Double2DoubleMap.Entry> subSet(Double2DoubleMap.Entry var1, Double2DoubleMap.Entry var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Double2DoubleMap.Entry> headSet(Double2DoubleMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Double2DoubleMap.Entry> tailSet(Double2DoubleMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public Double2DoubleMap.Entry first() {
         if (Double2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Double2DoubleLinkedOpenHashMap.this.new MapEntry(Double2DoubleLinkedOpenHashMap.this.first);
         }
      }

      public Double2DoubleMap.Entry last() {
         if (Double2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Double2DoubleLinkedOpenHashMap.this.new MapEntry(Double2DoubleLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Double) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  double var3 = (Double)var2.getKey();
                  double var5 = (Double)var2.getValue();
                  if (Double.doubleToLongBits(var3) == 0L) {
                     return Double2DoubleLinkedOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.value[Double2DoubleLinkedOpenHashMap.this.n]) == Double.doubleToLongBits(var5);
                  } else {
                     double[] var9 = Double2DoubleLinkedOpenHashMap.this.key;
                     double var7;
                     int var10;
                     if (Double.doubleToLongBits(var7 = var9[var10 = (int)HashCommon.mix(Double.doubleToRawLongBits(var3)) & Double2DoubleLinkedOpenHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (Double.doubleToLongBits(var3) == Double.doubleToLongBits(var7)) {
                        return Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.value[var10]) == Double.doubleToLongBits(var5);
                     } else {
                        while(Double.doubleToLongBits(var7 = var9[var10 = var10 + 1 & Double2DoubleLinkedOpenHashMap.this.mask]) != 0L) {
                           if (Double.doubleToLongBits(var3) == Double.doubleToLongBits(var7)) {
                              return Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.value[var10]) == Double.doubleToLongBits(var5);
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
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  double var3 = (Double)var2.getKey();
                  double var5 = (Double)var2.getValue();
                  if (Double.doubleToLongBits(var3) == 0L) {
                     if (Double2DoubleLinkedOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.value[Double2DoubleLinkedOpenHashMap.this.n]) == Double.doubleToLongBits(var5)) {
                        Double2DoubleLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     double[] var9 = Double2DoubleLinkedOpenHashMap.this.key;
                     double var7;
                     int var10;
                     if (Double.doubleToLongBits(var7 = var9[var10 = (int)HashCommon.mix(Double.doubleToRawLongBits(var3)) & Double2DoubleLinkedOpenHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (Double.doubleToLongBits(var7) == Double.doubleToLongBits(var3)) {
                        if (Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.value[var10]) == Double.doubleToLongBits(var5)) {
                           Double2DoubleLinkedOpenHashMap.this.removeEntry(var10);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if (Double.doubleToLongBits(var7 = var9[var10 = var10 + 1 & Double2DoubleLinkedOpenHashMap.this.mask]) == 0L) {
                              return false;
                           }
                        } while(Double.doubleToLongBits(var7) != Double.doubleToLongBits(var3) || Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.value[var10]) != Double.doubleToLongBits(var5));

                        Double2DoubleLinkedOpenHashMap.this.removeEntry(var10);
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
         return Double2DoubleLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Double2DoubleLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Double2DoubleMap.Entry> iterator(Double2DoubleMap.Entry var1) {
         return Double2DoubleLinkedOpenHashMap.this.new EntryIterator(var1.getDoubleKey());
      }

      public ObjectListIterator<Double2DoubleMap.Entry> fastIterator() {
         return Double2DoubleLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Double2DoubleMap.Entry> fastIterator(Double2DoubleMap.Entry var1) {
         return Double2DoubleLinkedOpenHashMap.this.new FastEntryIterator(var1.getDoubleKey());
      }

      public void forEach(Consumer<? super Double2DoubleMap.Entry> var1) {
         int var2 = Double2DoubleLinkedOpenHashMap.this.size;
         int var4 = Double2DoubleLinkedOpenHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Double2DoubleLinkedOpenHashMap.this.link[var4];
            var1.accept(new AbstractDouble2DoubleMap.BasicEntry(Double2DoubleLinkedOpenHashMap.this.key[var3], Double2DoubleLinkedOpenHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Double2DoubleMap.Entry> var1) {
         AbstractDouble2DoubleMap.BasicEntry var2 = new AbstractDouble2DoubleMap.BasicEntry();
         int var3 = Double2DoubleLinkedOpenHashMap.this.size;
         int var5 = Double2DoubleLinkedOpenHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Double2DoubleLinkedOpenHashMap.this.link[var5];
            var2.key = Double2DoubleLinkedOpenHashMap.this.key[var4];
            var2.value = Double2DoubleLinkedOpenHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Double2DoubleLinkedOpenHashMap.MapIterator implements ObjectListIterator<Double2DoubleMap.Entry> {
      final Double2DoubleLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Double2DoubleLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(double var2) {
         super(var2, null);
         this.entry = Double2DoubleLinkedOpenHashMap.this.new MapEntry();
      }

      public Double2DoubleLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Double2DoubleLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Double2DoubleLinkedOpenHashMap.MapIterator implements ObjectListIterator<Double2DoubleMap.Entry> {
      private Double2DoubleLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(double var2) {
         super(var2, null);
      }

      public Double2DoubleLinkedOpenHashMap.MapEntry next() {
         return this.entry = Double2DoubleLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Double2DoubleLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Double2DoubleLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private class MapIterator {
      int prev;
      int next;
      int curr;
      int index;

      protected MapIterator() {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         this.next = Double2DoubleLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(double var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (Double.doubleToLongBits(var2) == 0L) {
            if (Double2DoubleLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Double2DoubleLinkedOpenHashMap.this.link[Double2DoubleLinkedOpenHashMap.this.n];
               this.prev = Double2DoubleLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.key[Double2DoubleLinkedOpenHashMap.this.last]) == Double.doubleToLongBits(var2)) {
            this.prev = Double2DoubleLinkedOpenHashMap.this.last;
            this.index = Double2DoubleLinkedOpenHashMap.this.size;
         } else {
            for(int var4 = (int)HashCommon.mix(Double.doubleToRawLongBits(var2)) & Double2DoubleLinkedOpenHashMap.this.mask; Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.key[var4]) != 0L; var4 = var4 + 1 & Double2DoubleLinkedOpenHashMap.this.mask) {
               if (Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.key[var4]) == Double.doubleToLongBits(var2)) {
                  this.next = (int)Double2DoubleLinkedOpenHashMap.this.link[var4];
                  this.prev = var4;
                  return;
               }
            }

            throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
         }
      }

      public boolean hasNext() {
         return this.next != -1;
      }

      public boolean hasPrevious() {
         return this.prev != -1;
      }

      private final void ensureIndexKnown() {
         if (this.index < 0) {
            if (this.prev == -1) {
               this.index = 0;
            } else if (this.next == -1) {
               this.index = Double2DoubleLinkedOpenHashMap.this.size;
            } else {
               int var1 = Double2DoubleLinkedOpenHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Double2DoubleLinkedOpenHashMap.this.link[var1];
               }

            }
         }
      }

      public int nextIndex() {
         this.ensureIndexKnown();
         return this.index;
      }

      public int previousIndex() {
         this.ensureIndexKnown();
         return this.index - 1;
      }

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next;
            this.next = (int)Double2DoubleLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               ++this.index;
            }

            return this.curr;
         }
      }

      public int previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev;
            this.prev = (int)(Double2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
               --this.index;
            }

            return this.curr;
         }
      }

      public void remove() {
         this.ensureIndexKnown();
         if (this.curr == -1) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               --this.index;
               this.prev = (int)(Double2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Double2DoubleLinkedOpenHashMap.this.link[this.curr];
            }

            --Double2DoubleLinkedOpenHashMap.this.size;
            int var10001;
            long[] var7;
            if (this.prev == -1) {
               Double2DoubleLinkedOpenHashMap.this.first = this.next;
            } else {
               var7 = Double2DoubleLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var7[var10001] ^= (Double2DoubleLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Double2DoubleLinkedOpenHashMap.this.last = this.prev;
            } else {
               var7 = Double2DoubleLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var7[var10001] ^= (Double2DoubleLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Double2DoubleLinkedOpenHashMap.this.n) {
               Double2DoubleLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               double[] var6 = Double2DoubleLinkedOpenHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Double2DoubleLinkedOpenHashMap.this.mask;

                  double var4;
                  while(true) {
                     if (Double.doubleToLongBits(var4 = var6[var3]) == 0L) {
                        var6[var1] = 0.0D;
                        return;
                     }

                     int var2 = (int)HashCommon.mix(Double.doubleToRawLongBits(var4)) & Double2DoubleLinkedOpenHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Double2DoubleLinkedOpenHashMap.this.mask;
                  }

                  var6[var1] = var4;
                  Double2DoubleLinkedOpenHashMap.this.value[var1] = Double2DoubleLinkedOpenHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Double2DoubleLinkedOpenHashMap.this.fixPointers(var3, var1);
               }
            }
         }
      }

      public int skip(int var1) {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return var1 - var2 - 1;
      }

      public int back(int var1) {
         int var2 = var1;

         while(var2-- != 0 && this.hasPrevious()) {
            this.previousEntry();
         }

         return var1 - var2 - 1;
      }

      public void set(Double2DoubleMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Double2DoubleMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(double var2, Object var4) {
         this(var2);
      }
   }

   final class MapEntry implements Double2DoubleMap.Entry, java.util.Map.Entry<Double, Double> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public double getDoubleKey() {
         return Double2DoubleLinkedOpenHashMap.this.key[this.index];
      }

      public double getDoubleValue() {
         return Double2DoubleLinkedOpenHashMap.this.value[this.index];
      }

      public double setValue(double var1) {
         double var3 = Double2DoubleLinkedOpenHashMap.this.value[this.index];
         Double2DoubleLinkedOpenHashMap.this.value[this.index] = var1;
         return var3;
      }

      /** @deprecated */
      @Deprecated
      public Double getKey() {
         return Double2DoubleLinkedOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Double getValue() {
         return Double2DoubleLinkedOpenHashMap.this.value[this.index];
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
            return Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.key[this.index]) == Double.doubleToLongBits((Double)var2.getKey()) && Double.doubleToLongBits(Double2DoubleLinkedOpenHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)var2.getValue());
         }
      }

      public int hashCode() {
         return HashCommon.double2int(Double2DoubleLinkedOpenHashMap.this.key[this.index]) ^ HashCommon.double2int(Double2DoubleLinkedOpenHashMap.this.value[this.index]);
      }

      public String toString() {
         return Double2DoubleLinkedOpenHashMap.this.key[this.index] + "=>" + Double2DoubleLinkedOpenHashMap.this.value[this.index];
      }
   }
}
