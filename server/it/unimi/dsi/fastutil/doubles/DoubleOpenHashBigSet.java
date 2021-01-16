package it.unimi.dsi.fastutil.doubles;

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

public class DoubleOpenHashBigSet extends AbstractDoubleSet implements Serializable, Cloneable, Hash, Size64 {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient double[][] key;
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

   public DoubleOpenHashBigSet(long var1, float var3) {
      super();
      if (var3 > 0.0F && var3 <= 1.0F) {
         if (this.n < 0L) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var3;
            this.minN = this.n = HashCommon.bigArraySize(var1, var3);
            this.maxFill = HashCommon.maxFill(this.n, var3);
            this.key = DoubleBigArrays.newBigArray(this.n);
            this.initMasks();
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public DoubleOpenHashBigSet(long var1) {
      this(var1, 0.75F);
   }

   public DoubleOpenHashBigSet() {
      this(16L, 0.75F);
   }

   public DoubleOpenHashBigSet(Collection<? extends Double> var1, float var2) {
      this((long)var1.size(), var2);
      this.addAll(var1);
   }

   public DoubleOpenHashBigSet(Collection<? extends Double> var1) {
      this(var1, 0.75F);
   }

   public DoubleOpenHashBigSet(DoubleCollection var1, float var2) {
      this((long)var1.size(), var2);
      this.addAll(var1);
   }

   public DoubleOpenHashBigSet(DoubleCollection var1) {
      this(var1, 0.75F);
   }

   public DoubleOpenHashBigSet(DoubleIterator var1, float var2) {
      this(16L, var2);

      while(var1.hasNext()) {
         this.add(var1.nextDouble());
      }

   }

   public DoubleOpenHashBigSet(DoubleIterator var1) {
      this(var1, 0.75F);
   }

   public DoubleOpenHashBigSet(Iterator<?> var1, float var2) {
      this(DoubleIterators.asDoubleIterator(var1), var2);
   }

   public DoubleOpenHashBigSet(Iterator<?> var1) {
      this(DoubleIterators.asDoubleIterator(var1));
   }

   public DoubleOpenHashBigSet(double[] var1, int var2, int var3, float var4) {
      this(var3 < 0 ? 0L : (long)var3, var4);
      DoubleArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public DoubleOpenHashBigSet(double[] var1, int var2, int var3) {
      this(var1, var2, var3, 0.75F);
   }

   public DoubleOpenHashBigSet(double[] var1, float var2) {
      this(var1, 0, var1.length, var2);
   }

   public DoubleOpenHashBigSet(double[] var1) {
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

   public boolean addAll(Collection<? extends Double> var1) {
      long var2 = var1 instanceof Size64 ? ((Size64)var1).size64() : (long)var1.size();
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var2);
      } else {
         this.ensureCapacity(this.size64() + var2);
      }

      return super.addAll(var1);
   }

   public boolean addAll(DoubleCollection var1) {
      long var2 = var1 instanceof Size64 ? ((Size64)var1).size64() : (long)var1.size();
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var2);
      } else {
         this.ensureCapacity(this.size64() + var2);
      }

      return super.addAll(var1);
   }

   public boolean add(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         double[][] var7 = this.key;
         long var8 = HashCommon.mix(Double.doubleToRawLongBits(var1));
         int var3;
         int var4;
         double var5;
         if (Double.doubleToLongBits(var5 = var7[var4 = (int)((var8 & this.mask) >>> 27)][var3 = (int)(var8 & (long)this.segmentMask)]) != 0L) {
            if (Double.doubleToLongBits(var5) == Double.doubleToLongBits(var1)) {
               return false;
            }

            while(Double.doubleToLongBits(var5 = var7[var4 = var4 + ((var3 = var3 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var3]) != 0L) {
               if (Double.doubleToLongBits(var5) == Double.doubleToLongBits(var1)) {
                  return false;
               }
            }
         }

         var7[var4][var3] = var1;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(2L * this.n);
      }

      return true;
   }

   protected final void shiftKeys(long var1) {
      double[][] var7 = this.key;

      while(true) {
         long var3 = var1;
         var1 = var1 + 1L & this.mask;

         while(true) {
            if (Double.doubleToLongBits(DoubleBigArrays.get(var7, var1)) == 0L) {
               DoubleBigArrays.set(var7, var3, 0.0D);
               return;
            }

            long var5 = HashCommon.mix(Double.doubleToRawLongBits(DoubleBigArrays.get(var7, var1))) & this.mask;
            if (var3 <= var1) {
               if (var3 >= var5 || var5 > var1) {
                  break;
               }
            } else if (var3 >= var5 && var5 > var1) {
               break;
            }

            var1 = var1 + 1L & this.mask;
         }

         DoubleBigArrays.set(var7, var3, DoubleBigArrays.get(var7, var1));
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

   public boolean remove(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         double[][] var5 = this.key;
         long var6 = HashCommon.mix(Double.doubleToRawLongBits(var1));
         double var3;
         int var8;
         int var9;
         if (Double.doubleToLongBits(var3 = var5[var9 = (int)((var6 & this.mask) >>> 27)][var8 = (int)(var6 & (long)this.segmentMask)]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(var3) == Double.doubleToLongBits(var1)) {
            return this.removeEntry(var9, var8);
         } else {
            do {
               if (Double.doubleToLongBits(var3 = var5[var9 = var9 + ((var8 = var8 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var8]) == 0L) {
                  return false;
               }
            } while(Double.doubleToLongBits(var3) != Double.doubleToLongBits(var1));

            return this.removeEntry(var9, var8);
         }
      }
   }

   public boolean contains(double var1) {
      if (Double.doubleToLongBits(var1) == 0L) {
         return this.containsNull;
      } else {
         double[][] var5 = this.key;
         long var6 = HashCommon.mix(Double.doubleToRawLongBits(var1));
         double var3;
         int var8;
         int var9;
         if (Double.doubleToLongBits(var3 = var5[var9 = (int)((var6 & this.mask) >>> 27)][var8 = (int)(var6 & (long)this.segmentMask)]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(var3) == Double.doubleToLongBits(var1)) {
            return true;
         } else {
            do {
               if (Double.doubleToLongBits(var3 = var5[var9 = var9 + ((var8 = var8 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var8]) == 0L) {
                  return false;
               }
            } while(Double.doubleToLongBits(var3) != Double.doubleToLongBits(var1));

            return true;
         }
      }
   }

   public void clear() {
      if (this.size != 0L) {
         this.size = 0L;
         this.containsNull = false;
         DoubleBigArrays.fill(this.key, 0.0D);
      }
   }

   public DoubleIterator iterator() {
      return new DoubleOpenHashBigSet.SetIterator();
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
      double[][] var3 = this.key;
      double[][] var4 = DoubleBigArrays.newBigArray(var1);
      long var5 = var1 - 1L;
      int var7 = var4[0].length - 1;
      int var8 = var4.length - 1;
      int var9 = 0;
      int var10 = 0;

      for(long var17 = this.realSize(); var17-- != 0L; var9 += (var10 = var10 + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while(Double.doubleToLongBits(var3[var9][var10]) == 0L) {
            var9 += (var10 = var10 + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         double var15 = var3[var9][var10];
         long var13 = HashCommon.mix(Double.doubleToRawLongBits(var15));
         int var11;
         int var12;
         if (Double.doubleToLongBits(var4[var11 = (int)((var13 & var5) >>> 27)][var12 = (int)(var13 & (long)var7)]) != 0L) {
            while(Double.doubleToLongBits(var4[var11 = var11 + ((var12 = var12 + 1 & var7) == 0 ? 1 : 0) & var8][var12]) != 0L) {
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

   public DoubleOpenHashBigSet clone() {
      DoubleOpenHashBigSet var1;
      try {
         var1 = (DoubleOpenHashBigSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = DoubleBigArrays.copy(this.key);
      var1.containsNull = this.containsNull;
      return var1;
   }

   public int hashCode() {
      double[][] var1 = this.key;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for(long var5 = this.realSize(); var5-- != 0L; var3 += (var4 = var4 + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while(Double.doubleToLongBits(var1[var3][var4]) == 0L) {
            var3 += (var4 = var4 + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         var2 += HashCommon.double2int(var1[var3][var4]);
      }

      return var2;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      DoubleIterator var2 = this.iterator();
      var1.defaultWriteObject();
      long var3 = this.size;

      while(var3-- != 0L) {
         var1.writeDouble(var2.nextDouble());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.bigArraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      double[][] var2 = this.key = DoubleBigArrays.newBigArray(this.n);
      this.initMasks();
      long var9 = this.size;

      while(true) {
         while(var9-- != 0L) {
            double var5 = var1.readDouble();
            if (Double.doubleToLongBits(var5) == 0L) {
               this.containsNull = true;
            } else {
               long var3 = HashCommon.mix(Double.doubleToRawLongBits(var5));
               int var7;
               int var8;
               if (Double.doubleToLongBits(var2[var7 = (int)((var3 & this.mask) >>> 27)][var8 = (int)(var3 & (long)this.segmentMask)]) != 0L) {
                  while(Double.doubleToLongBits(var2[var7 = var7 + ((var8 = var8 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var8]) != 0L) {
                  }
               }

               var2[var7][var8] = var5;
            }
         }

         return;
      }
   }

   private void checkTable() {
   }

   private class SetIterator implements DoubleIterator {
      int base;
      int displ;
      long last;
      long c;
      boolean mustReturnNull;
      DoubleArrayList wrapped;

      private SetIterator() {
         super();
         this.base = DoubleOpenHashBigSet.this.key.length;
         this.last = -1L;
         this.c = DoubleOpenHashBigSet.this.size;
         this.mustReturnNull = DoubleOpenHashBigSet.this.containsNull;
      }

      public boolean hasNext() {
         return this.c != 0L;
      }

      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = DoubleOpenHashBigSet.this.n;
               return 0.0D;
            } else {
               double[][] var1 = DoubleOpenHashBigSet.this.key;

               double var2;
               do {
                  if (this.displ == 0 && this.base <= 0) {
                     this.last = -9223372036854775808L;
                     return this.wrapped.getDouble(-(--this.base) - 1);
                  }

                  if (this.displ-- == 0) {
                     this.displ = var1[--this.base].length - 1;
                  }

                  var2 = var1[this.base][this.displ];
               } while(Double.doubleToLongBits(var2) == 0L);

               this.last = (long)this.base * 134217728L + (long)this.displ;
               return var2;
            }
         }
      }

      private final void shiftKeys(long var1) {
         double[][] var9 = DoubleOpenHashBigSet.this.key;

         while(true) {
            long var3 = var1;
            var1 = var1 + 1L & DoubleOpenHashBigSet.this.mask;

            double var7;
            while(true) {
               if (Double.doubleToLongBits(var7 = DoubleBigArrays.get(var9, var1)) == 0L) {
                  DoubleBigArrays.set(var9, var3, 0.0D);
                  return;
               }

               long var5 = HashCommon.mix(Double.doubleToRawLongBits(var7)) & DoubleOpenHashBigSet.this.mask;
               if (var3 <= var1) {
                  if (var3 >= var5 || var5 > var1) {
                     break;
                  }
               } else if (var3 >= var5 && var5 > var1) {
                  break;
               }

               var1 = var1 + 1L & DoubleOpenHashBigSet.this.mask;
            }

            if (var1 < var3) {
               if (this.wrapped == null) {
                  this.wrapped = new DoubleArrayList();
               }

               this.wrapped.add(DoubleBigArrays.get(var9, var1));
            }

            DoubleBigArrays.set(var9, var3, var7);
         }
      }

      public void remove() {
         if (this.last == -1L) {
            throw new IllegalStateException();
         } else {
            if (this.last == DoubleOpenHashBigSet.this.n) {
               DoubleOpenHashBigSet.this.containsNull = false;
            } else {
               if (this.base < 0) {
                  DoubleOpenHashBigSet.this.remove(this.wrapped.getDouble(-this.base - 1));
                  this.last = -1L;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --DoubleOpenHashBigSet.this.size;
            this.last = -1L;
         }
      }

      // $FF: synthetic method
      SetIterator(Object var2) {
         this();
      }
   }
}
