package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatOpenHashBigSet extends AbstractFloatSet implements Serializable, Cloneable, Hash, Size64 {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[][] key;
   protected transient long mask;
   protected transient int segmentMask;
   protected transient int baseMask;
   protected transient boolean containsNull;
   protected transient long n;
   protected transient long maxFill;
   protected final transient long minN;
   protected final float f;
   protected long size;

   private void initMasks() {
      this.mask = this.n - 1L;
      this.segmentMask = this.key[0].length - 1;
      this.baseMask = this.key.length - 1;
   }

   public FloatOpenHashBigSet(long var1, float var3) {
      super();
      if (var3 > 0.0F && var3 <= 1.0F) {
         if (this.n < 0L) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var3;
            this.minN = this.n = HashCommon.bigArraySize(var1, var3);
            this.maxFill = HashCommon.maxFill(this.n, var3);
            this.key = FloatBigArrays.newBigArray(this.n);
            this.initMasks();
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public FloatOpenHashBigSet(long var1) {
      this(var1, 0.75F);
   }

   public FloatOpenHashBigSet() {
      this(16L, 0.75F);
   }

   public FloatOpenHashBigSet(Collection<? extends Float> var1, float var2) {
      this((long)var1.size(), var2);
      this.addAll(var1);
   }

   public FloatOpenHashBigSet(Collection<? extends Float> var1) {
      this(var1, 0.75F);
   }

   public FloatOpenHashBigSet(FloatCollection var1, float var2) {
      this((long)var1.size(), var2);
      this.addAll(var1);
   }

   public FloatOpenHashBigSet(FloatCollection var1) {
      this(var1, 0.75F);
   }

   public FloatOpenHashBigSet(FloatIterator var1, float var2) {
      this(16L, var2);

      while(var1.hasNext()) {
         this.add(var1.nextFloat());
      }

   }

   public FloatOpenHashBigSet(FloatIterator var1) {
      this(var1, 0.75F);
   }

   public FloatOpenHashBigSet(Iterator<?> var1, float var2) {
      this(FloatIterators.asFloatIterator(var1), var2);
   }

   public FloatOpenHashBigSet(Iterator<?> var1) {
      this(FloatIterators.asFloatIterator(var1));
   }

   public FloatOpenHashBigSet(float[] var1, int var2, int var3, float var4) {
      this(var3 < 0 ? 0L : (long)var3, var4);
      FloatArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public FloatOpenHashBigSet(float[] var1, int var2, int var3) {
      this(var1, var2, var3, 0.75F);
   }

   public FloatOpenHashBigSet(float[] var1, float var2) {
      this(var1, 0, var1.length, var2);
   }

   public FloatOpenHashBigSet(float[] var1) {
      this(var1, 0.75F);
   }

   private long realSize() {
      return this.containsNull ? this.size - 1L : this.size;
   }

   private void ensureCapacity(long var1) {
      long var3 = HashCommon.bigArraySize(var1, this.f);
      if (var3 > this.n) {
         this.rehash(var3);
      }

   }

   public boolean addAll(Collection<? extends Float> var1) {
      long var2 = var1 instanceof Size64 ? ((Size64)var1).size64() : (long)var1.size();
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var2);
      } else {
         this.ensureCapacity(this.size64() + var2);
      }

      return super.addAll(var1);
   }

   public boolean addAll(FloatCollection var1) {
      long var2 = var1 instanceof Size64 ? ((Size64)var1).size64() : (long)var1.size();
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var2);
      } else {
         this.ensureCapacity(this.size64() + var2);
      }

      return super.addAll(var1);
   }

   public boolean add(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         float[][] var5 = this.key;
         long var6 = HashCommon.mix((long)HashCommon.float2int(var1));
         int var2;
         int var3;
         float var4;
         if (Float.floatToIntBits(var4 = var5[var3 = (int)((var6 & this.mask) >>> 27)][var2 = (int)(var6 & (long)this.segmentMask)]) != 0) {
            if (Float.floatToIntBits(var4) == Float.floatToIntBits(var1)) {
               return false;
            }

            while(Float.floatToIntBits(var4 = var5[var3 = var3 + ((var2 = var2 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var2]) != 0) {
               if (Float.floatToIntBits(var4) == Float.floatToIntBits(var1)) {
                  return false;
               }
            }
         }

         var5[var3][var2] = var1;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(2L * this.n);
      }

      return true;
   }

   protected final void shiftKeys(long var1) {
      float[][] var7 = this.key;

      while(true) {
         long var3 = var1;
         var1 = var1 + 1L & this.mask;

         while(true) {
            if (Float.floatToIntBits(FloatBigArrays.get(var7, var1)) == 0) {
               FloatBigArrays.set(var7, var3, 0.0F);
               return;
            }

            long var5 = HashCommon.mix((long)HashCommon.float2int(FloatBigArrays.get(var7, var1))) & this.mask;
            if (var3 <= var1) {
               if (var3 >= var5 || var5 > var1) {
                  break;
               }
            } else if (var3 >= var5 && var5 > var1) {
               break;
            }

            var1 = var1 + 1L & this.mask;
         }

         FloatBigArrays.set(var7, var3, FloatBigArrays.get(var7, var1));
      }
   }

   private boolean removeEntry(int var1, int var2) {
      --this.size;
      this.shiftKeys((long)var1 * 134217728L + (long)var2);
      if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
         this.rehash(this.n / 2L);
      }

      return true;
   }

   private boolean removeNullEntry() {
      this.containsNull = false;
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
         this.rehash(this.n / 2L);
      }

      return true;
   }

   public boolean remove(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         float[][] var3 = this.key;
         long var4 = HashCommon.mix((long)HashCommon.float2int(var1));
         float var2;
         int var6;
         int var7;
         if (Float.floatToIntBits(var2 = var3[var7 = (int)((var4 & this.mask) >>> 27)][var6 = (int)(var4 & (long)this.segmentMask)]) == 0) {
            return false;
         } else if (Float.floatToIntBits(var2) == Float.floatToIntBits(var1)) {
            return this.removeEntry(var7, var6);
         } else {
            do {
               if (Float.floatToIntBits(var2 = var3[var7 = var7 + ((var6 = var6 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var6]) == 0) {
                  return false;
               }
            } while(Float.floatToIntBits(var2) != Float.floatToIntBits(var1));

            return this.removeEntry(var7, var6);
         }
      }
   }

   public boolean contains(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNull;
      } else {
         float[][] var3 = this.key;
         long var4 = HashCommon.mix((long)HashCommon.float2int(var1));
         float var2;
         int var6;
         int var7;
         if (Float.floatToIntBits(var2 = var3[var7 = (int)((var4 & this.mask) >>> 27)][var6 = (int)(var4 & (long)this.segmentMask)]) == 0) {
            return false;
         } else if (Float.floatToIntBits(var2) == Float.floatToIntBits(var1)) {
            return true;
         } else {
            do {
               if (Float.floatToIntBits(var2 = var3[var7 = var7 + ((var6 = var6 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var6]) == 0) {
                  return false;
               }
            } while(Float.floatToIntBits(var2) != Float.floatToIntBits(var1));

            return true;
         }
      }
   }

   public void clear() {
      if (this.size != 0L) {
         this.size = 0L;
         this.containsNull = false;
         FloatBigArrays.fill(this.key, 0.0F);
      }
   }

   public FloatIterator iterator() {
      return new FloatOpenHashBigSet.SetIterator();
   }

   public boolean trim() {
      long var1 = HashCommon.bigArraySize(this.size, this.f);
      if (var1 < this.n && this.size <= HashCommon.maxFill(var1, this.f)) {
         try {
            this.rehash(var1);
            return true;
         } catch (OutOfMemoryError var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   public boolean trim(long var1) {
      long var3 = HashCommon.bigArraySize(var1, this.f);
      if (this.n <= var3) {
         return true;
      } else {
         try {
            this.rehash(var3);
            return true;
         } catch (OutOfMemoryError var6) {
            return false;
         }
      }
   }

   protected void rehash(long var1) {
      float[][] var3 = this.key;
      float[][] var4 = FloatBigArrays.newBigArray(var1);
      long var5 = var1 - 1L;
      int var7 = var4[0].length - 1;
      int var8 = var4.length - 1;
      int var9 = 0;
      int var10 = 0;

      for(long var16 = this.realSize(); var16-- != 0L; var9 += (var10 = var10 + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while(Float.floatToIntBits(var3[var9][var10]) == 0) {
            var9 += (var10 = var10 + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         float var15 = var3[var9][var10];
         long var13 = HashCommon.mix((long)HashCommon.float2int(var15));
         int var11;
         int var12;
         if (Float.floatToIntBits(var4[var11 = (int)((var13 & var5) >>> 27)][var12 = (int)(var13 & (long)var7)]) != 0) {
            while(Float.floatToIntBits(var4[var11 = var11 + ((var12 = var12 + 1 & var7) == 0 ? 1 : 0) & var8][var12]) != 0) {
            }
         }

         var4[var11][var12] = var15;
      }

      this.n = var1;
      this.key = var4;
      this.initMasks();
      this.maxFill = HashCommon.maxFill(this.n, this.f);
   }

   /** @deprecated */
   @Deprecated
   public int size() {
      return (int)Math.min(2147483647L, this.size);
   }

   public long size64() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0L;
   }

   public FloatOpenHashBigSet clone() {
      FloatOpenHashBigSet var1;
      try {
         var1 = (FloatOpenHashBigSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = FloatBigArrays.copy(this.key);
      var1.containsNull = this.containsNull;
      return var1;
   }

   public int hashCode() {
      float[][] var1 = this.key;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for(long var5 = this.realSize(); var5-- != 0L; var3 += (var4 = var4 + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while(Float.floatToIntBits(var1[var3][var4]) == 0) {
            var3 += (var4 = var4 + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         var2 += HashCommon.float2int(var1[var3][var4]);
      }

      return var2;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      FloatIterator var2 = this.iterator();
      var1.defaultWriteObject();
      long var3 = this.size;

      while(var3-- != 0L) {
         var1.writeFloat(var2.nextFloat());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.bigArraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      float[][] var2 = this.key = FloatBigArrays.newBigArray(this.n);
      this.initMasks();
      long var8 = this.size;

      while(true) {
         while(var8-- != 0L) {
            float var5 = var1.readFloat();
            if (Float.floatToIntBits(var5) == 0) {
               this.containsNull = true;
            } else {
               long var3 = HashCommon.mix((long)HashCommon.float2int(var5));
               int var6;
               int var7;
               if (Float.floatToIntBits(var2[var6 = (int)((var3 & this.mask) >>> 27)][var7 = (int)(var3 & (long)this.segmentMask)]) != 0) {
                  while(Float.floatToIntBits(var2[var6 = var6 + ((var7 = var7 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var7]) != 0) {
                  }
               }

               var2[var6][var7] = var5;
            }
         }

         return;
      }
   }

   private void checkTable() {
   }

   private class SetIterator implements FloatIterator {
      int base;
      int displ;
      long last;
      long c;
      boolean mustReturnNull;
      FloatArrayList wrapped;

      private SetIterator() {
         super();
         this.base = FloatOpenHashBigSet.this.key.length;
         this.last = -1L;
         this.c = FloatOpenHashBigSet.this.size;
         this.mustReturnNull = FloatOpenHashBigSet.this.containsNull;
      }

      public boolean hasNext() {
         return this.c != 0L;
      }

      public float nextFloat() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = FloatOpenHashBigSet.this.n;
               return 0.0F;
            } else {
               float[][] var1 = FloatOpenHashBigSet.this.key;

               float var2;
               do {
                  if (this.displ == 0 && this.base <= 0) {
                     this.last = -9223372036854775808L;
                     return this.wrapped.getFloat(-(--this.base) - 1);
                  }

                  if (this.displ-- == 0) {
                     this.displ = var1[--this.base].length - 1;
                  }

                  var2 = var1[this.base][this.displ];
               } while(Float.floatToIntBits(var2) == 0);

               this.last = (long)this.base * 134217728L + (long)this.displ;
               return var2;
            }
         }
      }

      private final void shiftKeys(long var1) {
         float[][] var8 = FloatOpenHashBigSet.this.key;

         while(true) {
            long var3 = var1;
            var1 = var1 + 1L & FloatOpenHashBigSet.this.mask;

            float var7;
            while(true) {
               if (Float.floatToIntBits(var7 = FloatBigArrays.get(var8, var1)) == 0) {
                  FloatBigArrays.set(var8, var3, 0.0F);
                  return;
               }

               long var5 = HashCommon.mix((long)HashCommon.float2int(var7)) & FloatOpenHashBigSet.this.mask;
               if (var3 <= var1) {
                  if (var3 >= var5 || var5 > var1) {
                     break;
                  }
               } else if (var3 >= var5 && var5 > var1) {
                  break;
               }

               var1 = var1 + 1L & FloatOpenHashBigSet.this.mask;
            }

            if (var1 < var3) {
               if (this.wrapped == null) {
                  this.wrapped = new FloatArrayList();
               }

               this.wrapped.add(FloatBigArrays.get(var8, var1));
            }

            FloatBigArrays.set(var8, var3, var7);
         }
      }

      public void remove() {
         if (this.last == -1L) {
            throw new IllegalStateException();
         } else {
            if (this.last == FloatOpenHashBigSet.this.n) {
               FloatOpenHashBigSet.this.containsNull = false;
            } else {
               if (this.base < 0) {
                  FloatOpenHashBigSet.this.remove(this.wrapped.getFloat(-this.base - 1));
                  this.last = -1L;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --FloatOpenHashBigSet.this.size;
            this.last = -1L;
         }
      }

      // $FF: synthetic method
      SetIterator(Object var2) {
         this();
      }
   }
}
