package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
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
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

public class Float2FloatLinkedOpenHashMap extends AbstractFloat2FloatSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
   protected transient float[] value;
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
   protected transient Float2FloatSortedMap.FastSortedEntrySet entries;
   protected transient FloatSortedSet keys;
   protected transient FloatCollection values;

   public Float2FloatLinkedOpenHashMap(int var1, float var2) {
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
            this.key = new float[this.n + 1];
            this.value = new float[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Float2FloatLinkedOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Float2FloatLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Float2FloatLinkedOpenHashMap(Map<? extends Float, ? extends Float> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Float2FloatLinkedOpenHashMap(Map<? extends Float, ? extends Float> var1) {
      this(var1, 0.75F);
   }

   public Float2FloatLinkedOpenHashMap(Float2FloatMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Float2FloatLinkedOpenHashMap(Float2FloatMap var1) {
      this(var1, 0.75F);
   }

   public Float2FloatLinkedOpenHashMap(float[] var1, float[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Float2FloatLinkedOpenHashMap(float[] var1, float[] var2) {
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

   private float removeEntry(int var1) {
      float var2 = this.value[var1];
      --this.size;
      this.fixPointers(var1);
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private float removeNullEntry() {
      this.containsNullKey = false;
      float var1 = this.value[this.n];
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Float, ? extends Float> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return -(var4 + 1);
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
            return var4;
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, float var2, float var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
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

   public float put(float var1, float var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         float var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   private float addToValue(int var1, float var2) {
      float var3 = this.value[var1];
      this.value[var1] = var3 + var2;
      return var3;
   }

   public float addTo(float var1, float var2) {
      int var3;
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var3 = this.n;
         this.containsNullKey = true;
      } else {
         float[] var5 = this.key;
         float var4;
         if (Float.floatToIntBits(var4 = var5[var3 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) != 0) {
            if (Float.floatToIntBits(var4) == Float.floatToIntBits(var1)) {
               return this.addToValue(var3, var2);
            }

            while(Float.floatToIntBits(var4 = var5[var3 = var3 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var4) == Float.floatToIntBits(var1)) {
                  return this.addToValue(var3, var2);
               }
            }
         }
      }

      this.key[var3] = var1;
      this.value[var3] = this.defRetValue + var2;
      if (this.size == 0) {
         this.first = this.last = var3;
         this.link[var3] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var3 & 4294967295L) & 4294967295L;
         this.link[var3] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var3;
      }

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

            int var3 = HashCommon.mix(HashCommon.float2int(var4)) & this.mask;
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
         this.fixPointers(var1, var2);
      }
   }

   public float remove(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
            return this.removeEntry(var4);
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
                  return this.removeEntry(var4);
               }
            }

            return this.defRetValue;
         }
      }
   }

   private float setValue(int var1, float var2) {
      float var3 = this.value[var1];
      this.value[var1] = var2;
      return var3;
   }

   public float removeFirstFloat() {
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
         float var2 = this.value[var1];
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

   public float removeLastFloat() {
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
         float var2 = this.value[var1];
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

   public float getAndMoveToFirst(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
            this.moveIndexToFirst(var4);
            return this.value[var4];
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
                  this.moveIndexToFirst(var4);
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public float getAndMoveToLast(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
            this.moveIndexToLast(var4);
            return this.value[var4];
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
                  this.moveIndexToLast(var4);
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public float putAndMoveToFirst(float var1, float var2) {
      int var3;
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         float[] var5 = this.key;
         float var4;
         if (Float.floatToIntBits(var4 = var5[var3 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) != 0) {
            if (Float.floatToIntBits(var4) == Float.floatToIntBits(var1)) {
               this.moveIndexToFirst(var3);
               return this.setValue(var3, var2);
            }

            while(Float.floatToIntBits(var4 = var5[var3 = var3 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var4) == Float.floatToIntBits(var1)) {
                  this.moveIndexToFirst(var3);
                  return this.setValue(var3, var2);
               }
            }
         }
      }

      this.key[var3] = var1;
      this.value[var3] = var2;
      if (this.size == 0) {
         this.first = this.last = var3;
         this.link[var3] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.first;
         var10000[var10001] ^= (this.link[this.first] ^ ((long)var3 & 4294967295L) << 32) & -4294967296L;
         this.link[var3] = -4294967296L | (long)this.first & 4294967295L;
         this.first = var3;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public float putAndMoveToLast(float var1, float var2) {
      int var3;
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         float[] var5 = this.key;
         float var4;
         if (Float.floatToIntBits(var4 = var5[var3 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) != 0) {
            if (Float.floatToIntBits(var4) == Float.floatToIntBits(var1)) {
               this.moveIndexToLast(var3);
               return this.setValue(var3, var2);
            }

            while(Float.floatToIntBits(var4 = var5[var3 = var3 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var4) == Float.floatToIntBits(var1)) {
                  this.moveIndexToLast(var3);
                  return this.setValue(var3, var2);
               }
            }
         }
      }

      this.key[var3] = var1;
      this.value[var3] = var2;
      if (this.size == 0) {
         this.first = this.last = var3;
         this.link[var3] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var3 & 4294967295L) & 4294967295L;
         this.link[var3] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var3;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public float get(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
            return this.value[var4];
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNullKey;
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return false;
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
            return true;
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(float var1) {
      float[] var2 = this.value;
      float[] var3 = this.key;
      if (this.containsNullKey && Float.floatToIntBits(var2[this.n]) == Float.floatToIntBits(var1)) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(Float.floatToIntBits(var3[var4]) == 0 || Float.floatToIntBits(var2[var4]) != Float.floatToIntBits(var1));

         return true;
      }
   }

   public float getOrDefault(float var1, float var2) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         float[] var4 = this.key;
         float var3;
         int var5;
         if (Float.floatToIntBits(var3 = var4[var5 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return var2;
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var3)) {
            return this.value[var5];
         } else {
            while(Float.floatToIntBits(var3 = var4[var5 = var5 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var1) == Float.floatToIntBits(var3)) {
                  return this.value[var5];
               }
            }

            return var2;
         }
      }
   }

   public float putIfAbsent(float var1, float var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(float var1, float var2) {
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNullKey && Float.floatToIntBits(var2) == Float.floatToIntBits(this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         float[] var4 = this.key;
         float var3;
         int var5;
         if (Float.floatToIntBits(var3 = var4[var5 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return false;
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var3) && Float.floatToIntBits(var2) == Float.floatToIntBits(this.value[var5])) {
            this.removeEntry(var5);
            return true;
         } else {
            do {
               if (Float.floatToIntBits(var3 = var4[var5 = var5 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(Float.floatToIntBits(var1) != Float.floatToIntBits(var3) || Float.floatToIntBits(var2) != Float.floatToIntBits(this.value[var5]));

            this.removeEntry(var5);
            return true;
         }
      }
   }

   public boolean replace(float var1, float var2, float var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && Float.floatToIntBits(var2) == Float.floatToIntBits(this.value[var4])) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public float replace(float var1, float var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         float var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public float computeIfAbsent(float var1, DoubleUnaryOperator var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         float var4 = SafeMath.safeDoubleToFloat(var2.applyAsDouble((double)var1));
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public float computeIfAbsentNullable(float var1, DoubleFunction<? extends Float> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Float var4 = (Float)var2.apply((double)var1);
         if (var4 == null) {
            return this.defRetValue;
         } else {
            float var5 = var4;
            this.insert(-var3 - 1, var1, var5);
            return var5;
         }
      }
   }

   public float computeIfPresent(float var1, BiFunction<? super Float, ? super Float, ? extends Float> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Float var4 = (Float)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (Float.floatToIntBits(var1) == 0) {
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

   public float compute(float var1, BiFunction<? super Float, ? super Float, ? extends Float> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Float var4 = (Float)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (Float.floatToIntBits(var1) == 0) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }
         }

         return this.defRetValue;
      } else {
         float var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public float merge(float var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      } else {
         Float var5 = (Float)var3.apply(this.value[var4], var2);
         if (var5 == null) {
            if (Float.floatToIntBits(var1) == 0) {
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
         Arrays.fill(this.key, 0.0F);
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

   public float firstFloatKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public float lastFloatKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Float2FloatSortedMap tailMap(float var1) {
      throw new UnsupportedOperationException();
   }

   public Float2FloatSortedMap headMap(float var1) {
      throw new UnsupportedOperationException();
   }

   public Float2FloatSortedMap subMap(float var1, float var2) {
      throw new UnsupportedOperationException();
   }

   public FloatComparator comparator() {
      return null;
   }

   public Float2FloatSortedMap.FastSortedEntrySet float2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2FloatLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public FloatSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2FloatLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public FloatCollection values() {
      if (this.values == null) {
         this.values = new AbstractFloatCollection() {
            public FloatIterator iterator() {
               return Float2FloatLinkedOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Float2FloatLinkedOpenHashMap.this.size;
            }

            public boolean contains(float var1) {
               return Float2FloatLinkedOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Float2FloatLinkedOpenHashMap.this.clear();
            }

            public void forEach(DoubleConsumer var1) {
               if (Float2FloatLinkedOpenHashMap.this.containsNullKey) {
                  var1.accept((double)Float2FloatLinkedOpenHashMap.this.value[Float2FloatLinkedOpenHashMap.this.n]);
               }

               int var2 = Float2FloatLinkedOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.key[var2]) != 0) {
                     var1.accept((double)Float2FloatLinkedOpenHashMap.this.value[var2]);
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
      float[] var3 = this.value;
      int var4 = var1 - 1;
      float[] var5 = new float[var1 + 1];
      float[] var6 = new float[var1 + 1];
      int var7 = this.first;
      int var8 = -1;
      int var9 = -1;
      long[] var12 = this.link;
      long[] var13 = new long[var1 + 1];
      this.first = -1;

      int var10;
      for(int var14 = this.size; var14-- != 0; var8 = var10) {
         int var11;
         if (Float.floatToIntBits(var2[var7]) == 0) {
            var11 = var1;
         } else {
            for(var11 = HashCommon.mix(HashCommon.float2int(var2[var7])) & var4; Float.floatToIntBits(var5[var11]) != 0; var11 = var11 + 1 & var4) {
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

   public Float2FloatLinkedOpenHashMap clone() {
      Float2FloatLinkedOpenHashMap var1;
      try {
         var1 = (Float2FloatLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (float[])this.key.clone();
      var1.value = (float[])this.value.clone();
      var1.link = (long[])this.link.clone();
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

         int var5 = HashCommon.float2int(this.key[var3]);
         var5 ^= HashCommon.float2int(this.value[var3]);
         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += HashCommon.float2int(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      float[] var2 = this.key;
      float[] var3 = this.value;
      Float2FloatLinkedOpenHashMap.MapIterator var4 = new Float2FloatLinkedOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeFloat(var2[var6]);
         var1.writeFloat(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] var2 = this.key = new float[this.n + 1];
      float[] var3 = this.value = new float[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var8 = this.size;

      while(var8-- != 0) {
         float var6 = var1.readFloat();
         float var7 = var1.readFloat();
         int var9;
         if (Float.floatToIntBits(var6) == 0) {
            var9 = this.n;
            this.containsNullKey = true;
         } else {
            for(var9 = HashCommon.mix(HashCommon.float2int(var6)) & this.mask; Float.floatToIntBits(var2[var9]) != 0; var9 = var9 + 1 & this.mask) {
            }
         }

         var2[var9] = var6;
         var3[var9] = var7;
         if (this.first != -1) {
            var4[var5] ^= (var4[var5] ^ (long)var9 & 4294967295L) & 4294967295L;
            var4[var9] ^= (var4[var9] ^ ((long)var5 & 4294967295L) << 32) & -4294967296L;
            var5 = var9;
         } else {
            var5 = this.first = var9;
            var4[var9] |= -4294967296L;
         }
      }

      this.last = var5;
      if (var5 != -1) {
         var4[var5] |= 4294967295L;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Float2FloatLinkedOpenHashMap.MapIterator implements FloatListIterator {
      public float previousFloat() {
         return Float2FloatLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public float nextFloat() {
         return Float2FloatLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractFloatSortedSet {
      private KeySet() {
         super();
      }

      public FloatListIterator iterator(float var1) {
         return Float2FloatLinkedOpenHashMap.this.new KeyIterator(var1);
      }

      public FloatListIterator iterator() {
         return Float2FloatLinkedOpenHashMap.this.new KeyIterator();
      }

      public void forEach(DoubleConsumer var1) {
         if (Float2FloatLinkedOpenHashMap.this.containsNullKey) {
            var1.accept((double)Float2FloatLinkedOpenHashMap.this.key[Float2FloatLinkedOpenHashMap.this.n]);
         }

         int var2 = Float2FloatLinkedOpenHashMap.this.n;

         while(var2-- != 0) {
            float var3 = Float2FloatLinkedOpenHashMap.this.key[var2];
            if (Float.floatToIntBits(var3) != 0) {
               var1.accept((double)var3);
            }
         }

      }

      public int size() {
         return Float2FloatLinkedOpenHashMap.this.size;
      }

      public boolean contains(float var1) {
         return Float2FloatLinkedOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(float var1) {
         int var2 = Float2FloatLinkedOpenHashMap.this.size;
         Float2FloatLinkedOpenHashMap.this.remove(var1);
         return Float2FloatLinkedOpenHashMap.this.size != var2;
      }

      public void clear() {
         Float2FloatLinkedOpenHashMap.this.clear();
      }

      public float firstFloat() {
         if (Float2FloatLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2FloatLinkedOpenHashMap.this.key[Float2FloatLinkedOpenHashMap.this.first];
         }
      }

      public float lastFloat() {
         if (Float2FloatLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2FloatLinkedOpenHashMap.this.key[Float2FloatLinkedOpenHashMap.this.last];
         }
      }

      public FloatComparator comparator() {
         return null;
      }

      public FloatSortedSet tailSet(float var1) {
         throw new UnsupportedOperationException();
      }

      public FloatSortedSet headSet(float var1) {
         throw new UnsupportedOperationException();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Float2FloatLinkedOpenHashMap.MapIterator implements FloatListIterator {
      public KeyIterator(float var2) {
         super(var2, null);
      }

      public float previousFloat() {
         return Float2FloatLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public float nextFloat() {
         return Float2FloatLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Float2FloatMap.Entry> implements Float2FloatSortedMap.FastSortedEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Float2FloatMap.Entry> iterator() {
         return Float2FloatLinkedOpenHashMap.this.new EntryIterator();
      }

      public Comparator<? super Float2FloatMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Float2FloatMap.Entry> subSet(Float2FloatMap.Entry var1, Float2FloatMap.Entry var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Float2FloatMap.Entry> headSet(Float2FloatMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Float2FloatMap.Entry> tailSet(Float2FloatMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public Float2FloatMap.Entry first() {
         if (Float2FloatLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2FloatLinkedOpenHashMap.this.new MapEntry(Float2FloatLinkedOpenHashMap.this.first);
         }
      }

      public Float2FloatMap.Entry last() {
         if (Float2FloatLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2FloatLinkedOpenHashMap.this.new MapEntry(Float2FloatLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Float) {
               if (var2.getValue() != null && var2.getValue() instanceof Float) {
                  float var3 = (Float)var2.getKey();
                  float var4 = (Float)var2.getValue();
                  if (Float.floatToIntBits(var3) == 0) {
                     return Float2FloatLinkedOpenHashMap.this.containsNullKey && Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.value[Float2FloatLinkedOpenHashMap.this.n]) == Float.floatToIntBits(var4);
                  } else {
                     float[] var6 = Float2FloatLinkedOpenHashMap.this.key;
                     float var5;
                     int var7;
                     if (Float.floatToIntBits(var5 = var6[var7 = HashCommon.mix(HashCommon.float2int(var3)) & Float2FloatLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Float.floatToIntBits(var3) == Float.floatToIntBits(var5)) {
                        return Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.value[var7]) == Float.floatToIntBits(var4);
                     } else {
                        while(Float.floatToIntBits(var5 = var6[var7 = var7 + 1 & Float2FloatLinkedOpenHashMap.this.mask]) != 0) {
                           if (Float.floatToIntBits(var3) == Float.floatToIntBits(var5)) {
                              return Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.value[var7]) == Float.floatToIntBits(var4);
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
               if (var2.getValue() != null && var2.getValue() instanceof Float) {
                  float var3 = (Float)var2.getKey();
                  float var4 = (Float)var2.getValue();
                  if (Float.floatToIntBits(var3) == 0) {
                     if (Float2FloatLinkedOpenHashMap.this.containsNullKey && Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.value[Float2FloatLinkedOpenHashMap.this.n]) == Float.floatToIntBits(var4)) {
                        Float2FloatLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     float[] var6 = Float2FloatLinkedOpenHashMap.this.key;
                     float var5;
                     int var7;
                     if (Float.floatToIntBits(var5 = var6[var7 = HashCommon.mix(HashCommon.float2int(var3)) & Float2FloatLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Float.floatToIntBits(var5) == Float.floatToIntBits(var3)) {
                        if (Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.value[var7]) == Float.floatToIntBits(var4)) {
                           Float2FloatLinkedOpenHashMap.this.removeEntry(var7);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if (Float.floatToIntBits(var5 = var6[var7 = var7 + 1 & Float2FloatLinkedOpenHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(Float.floatToIntBits(var5) != Float.floatToIntBits(var3) || Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.value[var7]) != Float.floatToIntBits(var4));

                        Float2FloatLinkedOpenHashMap.this.removeEntry(var7);
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
         return Float2FloatLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Float2FloatLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Float2FloatMap.Entry> iterator(Float2FloatMap.Entry var1) {
         return Float2FloatLinkedOpenHashMap.this.new EntryIterator(var1.getFloatKey());
      }

      public ObjectListIterator<Float2FloatMap.Entry> fastIterator() {
         return Float2FloatLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Float2FloatMap.Entry> fastIterator(Float2FloatMap.Entry var1) {
         return Float2FloatLinkedOpenHashMap.this.new FastEntryIterator(var1.getFloatKey());
      }

      public void forEach(Consumer<? super Float2FloatMap.Entry> var1) {
         int var2 = Float2FloatLinkedOpenHashMap.this.size;
         int var4 = Float2FloatLinkedOpenHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Float2FloatLinkedOpenHashMap.this.link[var4];
            var1.accept(new AbstractFloat2FloatMap.BasicEntry(Float2FloatLinkedOpenHashMap.this.key[var3], Float2FloatLinkedOpenHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Float2FloatMap.Entry> var1) {
         AbstractFloat2FloatMap.BasicEntry var2 = new AbstractFloat2FloatMap.BasicEntry();
         int var3 = Float2FloatLinkedOpenHashMap.this.size;
         int var5 = Float2FloatLinkedOpenHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Float2FloatLinkedOpenHashMap.this.link[var5];
            var2.key = Float2FloatLinkedOpenHashMap.this.key[var4];
            var2.value = Float2FloatLinkedOpenHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Float2FloatLinkedOpenHashMap.MapIterator implements ObjectListIterator<Float2FloatMap.Entry> {
      final Float2FloatLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Float2FloatLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(float var2) {
         super(var2, null);
         this.entry = Float2FloatLinkedOpenHashMap.this.new MapEntry();
      }

      public Float2FloatLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Float2FloatLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Float2FloatLinkedOpenHashMap.MapIterator implements ObjectListIterator<Float2FloatMap.Entry> {
      private Float2FloatLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(float var2) {
         super(var2, null);
      }

      public Float2FloatLinkedOpenHashMap.MapEntry next() {
         return this.entry = Float2FloatLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Float2FloatLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Float2FloatLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
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
         this.next = Float2FloatLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(float var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (Float.floatToIntBits(var2) == 0) {
            if (Float2FloatLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Float2FloatLinkedOpenHashMap.this.link[Float2FloatLinkedOpenHashMap.this.n];
               this.prev = Float2FloatLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.key[Float2FloatLinkedOpenHashMap.this.last]) == Float.floatToIntBits(var2)) {
            this.prev = Float2FloatLinkedOpenHashMap.this.last;
            this.index = Float2FloatLinkedOpenHashMap.this.size;
         } else {
            for(int var3 = HashCommon.mix(HashCommon.float2int(var2)) & Float2FloatLinkedOpenHashMap.this.mask; Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.key[var3]) != 0; var3 = var3 + 1 & Float2FloatLinkedOpenHashMap.this.mask) {
               if (Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.key[var3]) == Float.floatToIntBits(var2)) {
                  this.next = (int)Float2FloatLinkedOpenHashMap.this.link[var3];
                  this.prev = var3;
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
               this.index = Float2FloatLinkedOpenHashMap.this.size;
            } else {
               int var1 = Float2FloatLinkedOpenHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Float2FloatLinkedOpenHashMap.this.link[var1];
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
            this.next = (int)Float2FloatLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Float2FloatLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
               this.prev = (int)(Float2FloatLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Float2FloatLinkedOpenHashMap.this.link[this.curr];
            }

            --Float2FloatLinkedOpenHashMap.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               Float2FloatLinkedOpenHashMap.this.first = this.next;
            } else {
               var6 = Float2FloatLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (Float2FloatLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Float2FloatLinkedOpenHashMap.this.last = this.prev;
            } else {
               var6 = Float2FloatLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var6[var10001] ^= (Float2FloatLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Float2FloatLinkedOpenHashMap.this.n) {
               Float2FloatLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               float[] var5 = Float2FloatLinkedOpenHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Float2FloatLinkedOpenHashMap.this.mask;

                  float var4;
                  while(true) {
                     if (Float.floatToIntBits(var4 = var5[var3]) == 0) {
                        var5[var1] = 0.0F;
                        return;
                     }

                     int var2 = HashCommon.mix(HashCommon.float2int(var4)) & Float2FloatLinkedOpenHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Float2FloatLinkedOpenHashMap.this.mask;
                  }

                  var5[var1] = var4;
                  Float2FloatLinkedOpenHashMap.this.value[var1] = Float2FloatLinkedOpenHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Float2FloatLinkedOpenHashMap.this.fixPointers(var3, var1);
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

      public void set(Float2FloatMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Float2FloatMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(float var2, Object var3) {
         this(var2);
      }
   }

   final class MapEntry implements Float2FloatMap.Entry, java.util.Map.Entry<Float, Float> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public float getFloatKey() {
         return Float2FloatLinkedOpenHashMap.this.key[this.index];
      }

      public float getFloatValue() {
         return Float2FloatLinkedOpenHashMap.this.value[this.index];
      }

      public float setValue(float var1) {
         float var2 = Float2FloatLinkedOpenHashMap.this.value[this.index];
         Float2FloatLinkedOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Float getKey() {
         return Float2FloatLinkedOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Float getValue() {
         return Float2FloatLinkedOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Float setValue(Float var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.key[this.index]) == Float.floatToIntBits((Float)var2.getKey()) && Float.floatToIntBits(Float2FloatLinkedOpenHashMap.this.value[this.index]) == Float.floatToIntBits((Float)var2.getValue());
         }
      }

      public int hashCode() {
         return HashCommon.float2int(Float2FloatLinkedOpenHashMap.this.key[this.index]) ^ HashCommon.float2int(Float2FloatLinkedOpenHashMap.this.value[this.index]);
      }

      public String toString() {
         return Float2FloatLinkedOpenHashMap.this.key[this.index] + "=>" + Float2FloatLinkedOpenHashMap.this.value[this.index];
      }
   }
}
