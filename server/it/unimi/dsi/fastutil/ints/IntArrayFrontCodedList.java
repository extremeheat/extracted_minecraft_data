package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.objects.AbstractObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class IntArrayFrontCodedList extends AbstractObjectList<int[]> implements Serializable, Cloneable, RandomAccess {
   private static final long serialVersionUID = 1L;
   protected final int n;
   protected final int ratio;
   protected final int[][] array;
   protected transient long[] p;

   public IntArrayFrontCodedList(Iterator<int[]> var1, int var2) {
      super();
      if (var2 < 1) {
         throw new IllegalArgumentException("Illegal ratio (" + var2 + ")");
      } else {
         int[][] var3 = IntBigArrays.EMPTY_BIG_ARRAY;
         long[] var4 = LongArrays.EMPTY_ARRAY;
         int[][] var5 = new int[2][];
         long var6 = 0L;
         int var8 = 0;

         for(int var9 = 0; var1.hasNext(); ++var8) {
            var5[var9] = (int[])var1.next();
            int var11 = var5[var9].length;
            if (var8 % var2 == 0) {
               var4 = LongArrays.grow(var4, var8 / var2 + 1);
               var4[var8 / var2] = var6;
               var3 = IntBigArrays.grow(var3, var6 + (long)count(var11) + (long)var11, var6);
               var6 += (long)writeInt(var3, var11, var6);
               IntBigArrays.copyToBig(var5[var9], 0, var3, var6, (long)var11);
               var6 += (long)var11;
            } else {
               int var12 = var5[1 - var9].length;
               if (var11 < var12) {
                  var12 = var11;
               }

               int var10;
               for(var10 = 0; var10 < var12 && var5[0][var10] == var5[1][var10]; ++var10) {
               }

               var11 -= var10;
               var3 = IntBigArrays.grow(var3, var6 + (long)count(var11) + (long)count(var10) + (long)var11, var6);
               var6 += (long)writeInt(var3, var11, var6);
               var6 += (long)writeInt(var3, var10, var6);
               IntBigArrays.copyToBig(var5[var9], var10, var3, var6, (long)var11);
               var6 += (long)var11;
            }

            var9 = 1 - var9;
         }

         this.n = var8;
         this.ratio = var2;
         this.array = IntBigArrays.trim(var3, var6);
         this.p = LongArrays.trim(var4, (var8 + var2 - 1) / var2);
      }
   }

   public IntArrayFrontCodedList(Collection<int[]> var1, int var2) {
      this(var1.iterator(), var2);
   }

   private static int readInt(int[][] var0, long var1) {
      return IntBigArrays.get(var0, var1);
   }

   private static int count(int var0) {
      return 1;
   }

   private static int writeInt(int[][] var0, int var1, long var2) {
      IntBigArrays.set(var0, var2, var1);
      return 1;
   }

   public int ratio() {
      return this.ratio;
   }

   private int length(int var1) {
      int[][] var2 = this.array;
      int var3 = var1 % this.ratio;
      long var4 = this.p[var1 / this.ratio];
      int var6 = readInt(var2, var4);
      if (var3 == 0) {
         return var6;
      } else {
         var4 += (long)(count(var6) + var6);
         var6 = readInt(var2, var4);
         int var7 = readInt(var2, var4 + (long)count(var6));

         for(int var8 = 0; var8 < var3 - 1; ++var8) {
            var4 += (long)(count(var6) + count(var7) + var6);
            var6 = readInt(var2, var4);
            var7 = readInt(var2, var4 + (long)count(var6));
         }

         return var6 + var7;
      }
   }

   public int arrayLength(int var1) {
      this.ensureRestrictedIndex(var1);
      return this.length(var1);
   }

   private int extract(int var1, int[] var2, int var3, int var4) {
      int var5 = var1 % this.ratio;
      long var6 = this.p[var1 / this.ratio];
      long var8 = var6;
      int var12 = readInt(this.array, var6);
      int var13 = 0;
      if (var5 == 0) {
         var8 = this.p[var1 / this.ratio] + (long)count(var12);
         IntBigArrays.copyFromBig(this.array, var8, var2, var3, Math.min(var4, var12));
         return var12;
      } else {
         int var15 = 0;

         for(int var16 = 0; var16 < var5; ++var16) {
            long var10 = var8 + (long)count(var12) + (long)(var16 != 0 ? count(var15) : 0);
            var8 = var10 + (long)var12;
            var12 = readInt(this.array, var8);
            var15 = readInt(this.array, var8 + (long)count(var12));
            int var14 = Math.min(var15, var4);
            if (var14 <= var13) {
               var13 = var14;
            } else {
               IntBigArrays.copyFromBig(this.array, var10, var2, var13 + var3, var14 - var13);
               var13 = var14;
            }
         }

         if (var13 < var4) {
            IntBigArrays.copyFromBig(this.array, var8 + (long)count(var12) + (long)count(var15), var2, var13 + var3, Math.min(var12, var4 - var13));
         }

         return var12 + var15;
      }
   }

   public int[] get(int var1) {
      return this.getArray(var1);
   }

   public int[] getArray(int var1) {
      this.ensureRestrictedIndex(var1);
      int var2 = this.length(var1);
      int[] var3 = new int[var2];
      this.extract(var1, var3, 0, var2);
      return var3;
   }

   public int get(int var1, int[] var2, int var3, int var4) {
      this.ensureRestrictedIndex(var1);
      IntArrays.ensureOffsetLength(var2, var3, var4);
      int var5 = this.extract(var1, var2, var3, var4);
      return var4 >= var5 ? var5 : var4 - var5;
   }

   public int get(int var1, int[] var2) {
      return this.get(var1, var2, 0, var2.length);
   }

   public int size() {
      return this.n;
   }

   public ObjectListIterator<int[]> listIterator(final int var1) {
      this.ensureIndex(var1);
      return new ObjectListIterator<int[]>() {
         int[] s;
         int i;
         long pos;
         boolean inSync;

         {
            this.s = IntArrays.EMPTY_ARRAY;
            this.i = 0;
            this.pos = 0L;
            if (var1 != 0) {
               if (var1 == IntArrayFrontCodedList.this.n) {
                  this.i = var1;
               } else {
                  this.pos = IntArrayFrontCodedList.this.p[var1 / IntArrayFrontCodedList.this.ratio];
                  int var3 = var1 % IntArrayFrontCodedList.this.ratio;
                  this.i = var1 - var3;

                  while(var3-- != 0) {
                     this.next();
                  }
               }
            }

         }

         public boolean hasNext() {
            return this.i < IntArrayFrontCodedList.this.n;
         }

         public boolean hasPrevious() {
            return this.i > 0;
         }

         public int previousIndex() {
            return this.i - 1;
         }

         public int nextIndex() {
            return this.i;
         }

         public int[] next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               int var1x;
               if (this.i % IntArrayFrontCodedList.this.ratio == 0) {
                  this.pos = IntArrayFrontCodedList.this.p[this.i / IntArrayFrontCodedList.this.ratio];
                  var1x = IntArrayFrontCodedList.readInt(IntArrayFrontCodedList.this.array, this.pos);
                  this.s = IntArrays.ensureCapacity(this.s, var1x, 0);
                  IntBigArrays.copyFromBig(IntArrayFrontCodedList.this.array, this.pos + (long)IntArrayFrontCodedList.count(var1x), this.s, 0, var1x);
                  this.pos += (long)(var1x + IntArrayFrontCodedList.count(var1x));
                  this.inSync = true;
               } else if (this.inSync) {
                  var1x = IntArrayFrontCodedList.readInt(IntArrayFrontCodedList.this.array, this.pos);
                  int var2 = IntArrayFrontCodedList.readInt(IntArrayFrontCodedList.this.array, this.pos + (long)IntArrayFrontCodedList.count(var1x));
                  this.s = IntArrays.ensureCapacity(this.s, var1x + var2, var2);
                  IntBigArrays.copyFromBig(IntArrayFrontCodedList.this.array, this.pos + (long)IntArrayFrontCodedList.count(var1x) + (long)IntArrayFrontCodedList.count(var2), this.s, var2, var1x);
                  this.pos += (long)(IntArrayFrontCodedList.count(var1x) + IntArrayFrontCodedList.count(var2) + var1x);
                  var1x += var2;
               } else {
                  this.s = IntArrays.ensureCapacity(this.s, var1x = IntArrayFrontCodedList.this.length(this.i), 0);
                  IntArrayFrontCodedList.this.extract(this.i, this.s, 0, var1x);
               }

               ++this.i;
               return IntArrays.copy(this.s, 0, var1x);
            }
         }

         public int[] previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               this.inSync = false;
               return IntArrayFrontCodedList.this.getArray(--this.i);
            }
         }
      };
   }

   public IntArrayFrontCodedList clone() {
      return this;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("[");

      for(int var2 = 0; var2 < this.n; ++var2) {
         if (var2 != 0) {
            var1.append(", ");
         }

         var1.append(IntArrayList.wrap(this.getArray(var2)).toString());
      }

      var1.append("]");
      return var1.toString();
   }

   protected long[] rebuildPointerArray() {
      long[] var1 = new long[(this.n + this.ratio - 1) / this.ratio];
      int[][] var2 = this.array;
      long var5 = 0L;
      int var7 = 0;
      int var8 = 0;

      for(int var9 = this.ratio - 1; var7 < this.n; ++var7) {
         int var3 = readInt(var2, var5);
         int var4 = count(var3);
         ++var9;
         if (var9 == this.ratio) {
            var9 = 0;
            var1[var8++] = var5;
            var5 += (long)(var4 + var3);
         } else {
            var5 += (long)(var4 + count(readInt(var2, var5 + (long)var4)) + var3);
         }
      }

      return var1;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.p = this.rebuildPointerArray();
   }
}
