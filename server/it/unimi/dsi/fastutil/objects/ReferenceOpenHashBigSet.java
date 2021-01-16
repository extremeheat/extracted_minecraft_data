package it.unimi.dsi.fastutil.objects;

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

public class ReferenceOpenHashBigSet<K> extends AbstractReferenceSet<K> implements Serializable, Cloneable, Hash, Size64 {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[][] key;
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

   public ReferenceOpenHashBigSet(long var1, float var3) {
      super();
      if (var3 > 0.0F && var3 <= 1.0F) {
         if (this.n < 0L) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var3;
            this.minN = this.n = HashCommon.bigArraySize(var1, var3);
            this.maxFill = HashCommon.maxFill(this.n, var3);
            this.key = ObjectBigArrays.newBigArray(this.n);
            this.initMasks();
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public ReferenceOpenHashBigSet(long var1) {
      this(var1, 0.75F);
   }

   public ReferenceOpenHashBigSet() {
      this(16L, 0.75F);
   }

   public ReferenceOpenHashBigSet(Collection<? extends K> var1, float var2) {
      this((long)var1.size(), var2);
      this.addAll(var1);
   }

   public ReferenceOpenHashBigSet(Collection<? extends K> var1) {
      this(var1, 0.75F);
   }

   public ReferenceOpenHashBigSet(ReferenceCollection<? extends K> var1, float var2) {
      this((long)var1.size(), var2);
      this.addAll(var1);
   }

   public ReferenceOpenHashBigSet(ReferenceCollection<? extends K> var1) {
      this(var1, 0.75F);
   }

   public ReferenceOpenHashBigSet(Iterator<? extends K> var1, float var2) {
      this(16L, var2);

      while(var1.hasNext()) {
         this.add(var1.next());
      }

   }

   public ReferenceOpenHashBigSet(Iterator<? extends K> var1) {
      this(var1, 0.75F);
   }

   public ReferenceOpenHashBigSet(K[] var1, int var2, int var3, float var4) {
      this(var3 < 0 ? 0L : (long)var3, var4);
      ObjectArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public ReferenceOpenHashBigSet(K[] var1, int var2, int var3) {
      this(var1, var2, var3, 0.75F);
   }

   public ReferenceOpenHashBigSet(K[] var1, float var2) {
      this(var1, 0, var1.length, var2);
   }

   public ReferenceOpenHashBigSet(K[] var1) {
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

   public boolean addAll(Collection<? extends K> var1) {
      long var2 = var1 instanceof Size64 ? ((Size64)var1).size64() : (long)var1.size();
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var2);
      } else {
         this.ensureCapacity(this.size64() + var2);
      }

      return super.addAll(var1);
   }

   public boolean add(K var1) {
      if (var1 == null) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         Object[][] var5 = this.key;
         long var6 = HashCommon.mix((long)System.identityHashCode(var1));
         int var2;
         int var3;
         Object var4;
         if ((var4 = var5[var3 = (int)((var6 & this.mask) >>> 27)][var2 = (int)(var6 & (long)this.segmentMask)]) != null) {
            if (var4 == var1) {
               return false;
            }

            while((var4 = var5[var3 = var3 + ((var2 = var2 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var2]) != null) {
               if (var4 == var1) {
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
      Object[][] var7 = this.key;

      while(true) {
         long var3 = var1;
         var1 = var1 + 1L & this.mask;

         while(true) {
            if (ObjectBigArrays.get(var7, var1) == null) {
               ObjectBigArrays.set(var7, var3, (Object)null);
               return;
            }

            long var5 = HashCommon.mix((long)System.identityHashCode(ObjectBigArrays.get(var7, var1))) & this.mask;
            if (var3 <= var1) {
               if (var3 >= var5 || var5 > var1) {
                  break;
               }
            } else if (var3 >= var5 && var5 > var1) {
               break;
            }

            var1 = var1 + 1L & this.mask;
         }

         ObjectBigArrays.set(var7, var3, ObjectBigArrays.get(var7, var1));
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

   public boolean remove(Object var1) {
      if (var1 == null) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         Object[][] var3 = this.key;
         long var4 = HashCommon.mix((long)System.identityHashCode(var1));
         Object var2;
         int var6;
         int var7;
         if ((var2 = var3[var7 = (int)((var4 & this.mask) >>> 27)][var6 = (int)(var4 & (long)this.segmentMask)]) == null) {
            return false;
         } else if (var2 == var1) {
            return this.removeEntry(var7, var6);
         } else {
            do {
               if ((var2 = var3[var7 = var7 + ((var6 = var6 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var6]) == null) {
                  return false;
               }
            } while(var2 != var1);

            return this.removeEntry(var7, var6);
         }
      }
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return this.containsNull;
      } else {
         Object[][] var3 = this.key;
         long var4 = HashCommon.mix((long)System.identityHashCode(var1));
         Object var2;
         int var6;
         int var7;
         if ((var2 = var3[var7 = (int)((var4 & this.mask) >>> 27)][var6 = (int)(var4 & (long)this.segmentMask)]) == null) {
            return false;
         } else if (var2 == var1) {
            return true;
         } else {
            do {
               if ((var2 = var3[var7 = var7 + ((var6 = var6 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var6]) == null) {
                  return false;
               }
            } while(var2 != var1);

            return true;
         }
      }
   }

   public void clear() {
      if (this.size != 0L) {
         this.size = 0L;
         this.containsNull = false;
         ObjectBigArrays.fill(this.key, (Object)null);
      }
   }

   public ObjectIterator<K> iterator() {
      return new ReferenceOpenHashBigSet.SetIterator();
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
      Object[][] var3 = this.key;
      Object[][] var4 = ObjectBigArrays.newBigArray(var1);
      long var5 = var1 - 1L;
      int var7 = var4[0].length - 1;
      int var8 = var4.length - 1;
      int var9 = 0;
      int var10 = 0;

      for(long var16 = this.realSize(); var16-- != 0L; var9 += (var10 = var10 + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while(var3[var9][var10] == null) {
            var9 += (var10 = var10 + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         Object var15 = var3[var9][var10];
         long var13 = HashCommon.mix((long)System.identityHashCode(var15));
         int var11;
         int var12;
         if (var4[var11 = (int)((var13 & var5) >>> 27)][var12 = (int)(var13 & (long)var7)] != null) {
            while(var4[var11 = var11 + ((var12 = var12 + 1 & var7) == 0 ? 1 : 0) & var8][var12] != null) {
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

   public ReferenceOpenHashBigSet<K> clone() {
      ReferenceOpenHashBigSet var1;
      try {
         var1 = (ReferenceOpenHashBigSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = ObjectBigArrays.copy(this.key);
      var1.containsNull = this.containsNull;
      return var1;
   }

   public int hashCode() {
      Object[][] var1 = this.key;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for(long var5 = this.realSize(); var5-- != 0L; var3 += (var4 = var4 + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while(var1[var3][var4] == null) {
            var3 += (var4 = var4 + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         if (this != var1[var3][var4]) {
            var2 += System.identityHashCode(var1[var3][var4]);
         }
      }

      return var2;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectIterator var2 = this.iterator();
      var1.defaultWriteObject();
      long var3 = this.size;

      while(var3-- != 0L) {
         var1.writeObject(var2.next());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.bigArraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      Object[][] var2 = this.key = ObjectBigArrays.newBigArray(this.n);
      this.initMasks();
      long var8 = this.size;

      while(true) {
         while(var8-- != 0L) {
            Object var5 = var1.readObject();
            if (var5 == null) {
               this.containsNull = true;
            } else {
               long var3 = HashCommon.mix((long)System.identityHashCode(var5));
               int var6;
               int var7;
               if (var2[var6 = (int)((var3 & this.mask) >>> 27)][var7 = (int)(var3 & (long)this.segmentMask)] != null) {
                  while(var2[var6 = var6 + ((var7 = var7 + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][var7] != null) {
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

   private class SetIterator implements ObjectIterator<K> {
      int base;
      int displ;
      long last;
      long c;
      boolean mustReturnNull;
      ReferenceArrayList<K> wrapped;

      private SetIterator() {
         super();
         this.base = ReferenceOpenHashBigSet.this.key.length;
         this.last = -1L;
         this.c = ReferenceOpenHashBigSet.this.size;
         this.mustReturnNull = ReferenceOpenHashBigSet.this.containsNull;
      }

      public boolean hasNext() {
         return this.c != 0L;
      }

      public K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = ReferenceOpenHashBigSet.this.n;
               return null;
            } else {
               Object[][] var1 = ReferenceOpenHashBigSet.this.key;

               Object var2;
               do {
                  if (this.displ == 0 && this.base <= 0) {
                     this.last = -9223372036854775808L;
                     return this.wrapped.get(-(--this.base) - 1);
                  }

                  if (this.displ-- == 0) {
                     this.displ = var1[--this.base].length - 1;
                  }

                  var2 = var1[this.base][this.displ];
               } while(var2 == null);

               this.last = (long)this.base * 134217728L + (long)this.displ;
               return var2;
            }
         }
      }

      private final void shiftKeys(long var1) {
         Object[][] var8 = ReferenceOpenHashBigSet.this.key;

         while(true) {
            long var3 = var1;
            var1 = var1 + 1L & ReferenceOpenHashBigSet.this.mask;

            Object var7;
            while(true) {
               if ((var7 = ObjectBigArrays.get(var8, var1)) == null) {
                  ObjectBigArrays.set(var8, var3, (Object)null);
                  return;
               }

               long var5 = HashCommon.mix((long)System.identityHashCode(var7)) & ReferenceOpenHashBigSet.this.mask;
               if (var3 <= var1) {
                  if (var3 >= var5 || var5 > var1) {
                     break;
                  }
               } else if (var3 >= var5 && var5 > var1) {
                  break;
               }

               var1 = var1 + 1L & ReferenceOpenHashBigSet.this.mask;
            }

            if (var1 < var3) {
               if (this.wrapped == null) {
                  this.wrapped = new ReferenceArrayList();
               }

               this.wrapped.add(ObjectBigArrays.get(var8, var1));
            }

            ObjectBigArrays.set(var8, var3, var7);
         }
      }

      public void remove() {
         if (this.last == -1L) {
            throw new IllegalStateException();
         } else {
            if (this.last == ReferenceOpenHashBigSet.this.n) {
               ReferenceOpenHashBigSet.this.containsNull = false;
            } else {
               if (this.base < 0) {
                  ReferenceOpenHashBigSet.this.remove(this.wrapped.set(-this.base - 1, (Object)null));
                  this.last = -1L;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --ReferenceOpenHashBigSet.this.size;
            this.last = -1L;
         }
      }

      // $FF: synthetic method
      SetIterator(Object var2) {
         this();
      }
   }
}
