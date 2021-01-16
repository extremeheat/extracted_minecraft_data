package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatLinkedOpenHashSet extends AbstractFloatSortedSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected transient int first;
   protected transient int last;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public FloatLinkedOpenHashSet(int var1, float var2) {
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
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public FloatLinkedOpenHashSet(int var1) {
      this(var1, 0.75F);
   }

   public FloatLinkedOpenHashSet() {
      this(16, 0.75F);
   }

   public FloatLinkedOpenHashSet(Collection<? extends Float> var1, float var2) {
      this(var1.size(), var2);
      this.addAll(var1);
   }

   public FloatLinkedOpenHashSet(Collection<? extends Float> var1) {
      this(var1, 0.75F);
   }

   public FloatLinkedOpenHashSet(FloatCollection var1, float var2) {
      this(var1.size(), var2);
      this.addAll(var1);
   }

   public FloatLinkedOpenHashSet(FloatCollection var1) {
      this(var1, 0.75F);
   }

   public FloatLinkedOpenHashSet(FloatIterator var1, float var2) {
      this(16, var2);

      while(var1.hasNext()) {
         this.add(var1.nextFloat());
      }

   }

   public FloatLinkedOpenHashSet(FloatIterator var1) {
      this(var1, 0.75F);
   }

   public FloatLinkedOpenHashSet(Iterator<?> var1, float var2) {
      this(FloatIterators.asFloatIterator(var1), var2);
   }

   public FloatLinkedOpenHashSet(Iterator<?> var1) {
      this(FloatIterators.asFloatIterator(var1));
   }

   public FloatLinkedOpenHashSet(float[] var1, int var2, int var3, float var4) {
      this(var3 < 0 ? 0 : var3, var4);
      FloatArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public FloatLinkedOpenHashSet(float[] var1, int var2, int var3) {
      this(var1, var2, var3, 0.75F);
   }

   public FloatLinkedOpenHashSet(float[] var1, float var2) {
      this(var1, 0, var1.length, var2);
   }

   public FloatLinkedOpenHashSet(float[] var1) {
      this(var1, 0.75F);
   }

   private int realSize() {
      return this.containsNull ? this.size - 1 : this.size;
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

   public boolean addAll(FloatCollection var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      return super.addAll(var1);
   }

   public boolean addAll(Collection<? extends Float> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      return super.addAll(var1);
   }

   public boolean add(float var1) {
      int var2;
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNull) {
            return false;
         }

         var2 = this.n;
         this.containsNull = true;
      } else {
         float[] var4 = this.key;
         float var3;
         if (Float.floatToIntBits(var3 = var4[var2 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) != 0) {
            if (Float.floatToIntBits(var3) == Float.floatToIntBits(var1)) {
               return false;
            }

            while(Float.floatToIntBits(var3 = var4[var2 = var2 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var3) == Float.floatToIntBits(var1)) {
                  return false;
               }
            }
         }

         var4[var2] = var1;
      }

      if (this.size == 0) {
         this.first = this.last = var2;
         this.link[var2] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var2 & 4294967295L) & 4294967295L;
         this.link[var2] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var2;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return true;
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
         this.fixPointers(var1, var2);
      }
   }

   private boolean removeEntry(int var1) {
      --this.size;
      this.fixPointers(var1);
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   private boolean removeNullEntry() {
      this.containsNull = false;
      this.key[this.n] = 0.0F;
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   public boolean remove(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         float[] var3 = this.key;
         float var2;
         int var4;
         if (Float.floatToIntBits(var2 = var3[var4 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask]) == 0) {
            return false;
         } else if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
            return this.removeEntry(var4);
         } else {
            while(Float.floatToIntBits(var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(var1) == Float.floatToIntBits(var2)) {
                  return this.removeEntry(var4);
               }
            }

            return false;
         }
      }
   }

   public boolean contains(float var1) {
      if (Float.floatToIntBits(var1) == 0) {
         return this.containsNull;
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

         float var2 = this.key[var1];
         --this.size;
         if (Float.floatToIntBits(var2) == 0) {
            this.containsNull = false;
            this.key[this.n] = 0.0F;
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

         float var2 = this.key[var1];
         --this.size;
         if (Float.floatToIntBits(var2) == 0) {
            this.containsNull = false;
            this.key[this.n] = 0.0F;
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

   public boolean addAndMoveToFirst(float var1) {
      int var2;
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNull) {
            this.moveIndexToFirst(this.n);
            return false;
         }

         this.containsNull = true;
         var2 = this.n;
      } else {
         float[] var3 = this.key;

         for(var2 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask; Float.floatToIntBits(var3[var2]) != 0; var2 = var2 + 1 & this.mask) {
            if (Float.floatToIntBits(var1) == Float.floatToIntBits(var3[var2])) {
               this.moveIndexToFirst(var2);
               return false;
            }
         }
      }

      this.key[var2] = var1;
      if (this.size == 0) {
         this.first = this.last = var2;
         this.link[var2] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.first;
         var10000[var10001] ^= (this.link[this.first] ^ ((long)var2 & 4294967295L) << 32) & -4294967296L;
         this.link[var2] = -4294967296L | (long)this.first & 4294967295L;
         this.first = var2;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return true;
   }

   public boolean addAndMoveToLast(float var1) {
      int var2;
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNull) {
            this.moveIndexToLast(this.n);
            return false;
         }

         this.containsNull = true;
         var2 = this.n;
      } else {
         float[] var3 = this.key;

         for(var2 = HashCommon.mix(HashCommon.float2int(var1)) & this.mask; Float.floatToIntBits(var3[var2]) != 0; var2 = var2 + 1 & this.mask) {
            if (Float.floatToIntBits(var1) == Float.floatToIntBits(var3[var2])) {
               this.moveIndexToLast(var2);
               return false;
            }
         }
      }

      this.key[var2] = var1;
      if (this.size == 0) {
         this.first = this.last = var2;
         this.link[var2] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var2 & 4294967295L) & 4294967295L;
         this.link[var2] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var2;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return true;
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNull = false;
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

   public float firstFloat() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public float lastFloat() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
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

   public FloatComparator comparator() {
      return null;
   }

   public FloatListIterator iterator(float var1) {
      return new FloatLinkedOpenHashSet.SetIterator(var1);
   }

   public FloatListIterator iterator() {
      return new FloatLinkedOpenHashSet.SetIterator();
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
      int var3 = var1 - 1;
      float[] var4 = new float[var1 + 1];
      int var5 = this.first;
      int var6 = -1;
      int var7 = -1;
      long[] var10 = this.link;
      long[] var11 = new long[var1 + 1];
      this.first = -1;

      int var8;
      for(int var12 = this.size; var12-- != 0; var6 = var8) {
         int var9;
         if (Float.floatToIntBits(var2[var5]) == 0) {
            var9 = var1;
         } else {
            for(var9 = HashCommon.mix(HashCommon.float2int(var2[var5])) & var3; Float.floatToIntBits(var4[var9]) != 0; var9 = var9 + 1 & var3) {
            }
         }

         var4[var9] = var2[var5];
         if (var6 != -1) {
            var11[var7] ^= (var11[var7] ^ (long)var9 & 4294967295L) & 4294967295L;
            var11[var9] ^= (var11[var9] ^ ((long)var7 & 4294967295L) << 32) & -4294967296L;
            var7 = var9;
         } else {
            var7 = this.first = var9;
            var11[var9] = -1L;
         }

         var8 = var5;
         var5 = (int)var10[var5];
      }

      this.link = var11;
      this.last = var7;
      if (var7 != -1) {
         var11[var7] |= 4294967295L;
      }

      this.n = var1;
      this.mask = var3;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var4;
   }

   public FloatLinkedOpenHashSet clone() {
      FloatLinkedOpenHashSet var1;
      try {
         var1 = (FloatLinkedOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (float[])this.key.clone();
      var1.containsNull = this.containsNull;
      var1.link = (long[])this.link.clone();
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();

      for(int var3 = 0; var2-- != 0; ++var3) {
         while(Float.floatToIntBits(this.key[var3]) == 0) {
            ++var3;
         }

         var1 += HashCommon.float2int(this.key[var3]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      FloatListIterator var2 = this.iterator();
      var1.defaultWriteObject();
      int var3 = this.size;

      while(var3-- != 0) {
         var1.writeFloat(var2.nextFloat());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] var2 = this.key = new float[this.n + 1];
      long[] var3 = this.link = new long[this.n + 1];
      int var4 = -1;
      this.first = this.last = -1;
      int var6 = this.size;

      while(var6-- != 0) {
         float var5 = var1.readFloat();
         int var7;
         if (Float.floatToIntBits(var5) == 0) {
            var7 = this.n;
            this.containsNull = true;
         } else if (Float.floatToIntBits(var2[var7 = HashCommon.mix(HashCommon.float2int(var5)) & this.mask]) != 0) {
            while(Float.floatToIntBits(var2[var7 = var7 + 1 & this.mask]) != 0) {
            }
         }

         var2[var7] = var5;
         if (this.first != -1) {
            var3[var4] ^= (var3[var4] ^ (long)var7 & 4294967295L) & 4294967295L;
            var3[var7] ^= (var3[var7] ^ ((long)var4 & 4294967295L) << 32) & -4294967296L;
            var4 = var7;
         } else {
            var4 = this.first = var7;
            var3[var7] |= -4294967296L;
         }
      }

      this.last = var4;
      if (var4 != -1) {
         var3[var4] |= 4294967295L;
      }

   }

   private void checkTable() {
   }

   private class SetIterator implements FloatListIterator {
      int prev = -1;
      int next = -1;
      int curr = -1;
      int index = -1;

      SetIterator() {
         super();
         this.next = FloatLinkedOpenHashSet.this.first;
         this.index = 0;
      }

      SetIterator(float var2) {
         super();
         if (Float.floatToIntBits(var2) == 0) {
            if (FloatLinkedOpenHashSet.this.containsNull) {
               this.next = (int)FloatLinkedOpenHashSet.this.link[FloatLinkedOpenHashSet.this.n];
               this.prev = FloatLinkedOpenHashSet.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this set.");
            }
         } else if (Float.floatToIntBits(FloatLinkedOpenHashSet.this.key[FloatLinkedOpenHashSet.this.last]) == Float.floatToIntBits(var2)) {
            this.prev = FloatLinkedOpenHashSet.this.last;
            this.index = FloatLinkedOpenHashSet.this.size;
         } else {
            float[] var3 = FloatLinkedOpenHashSet.this.key;

            for(int var4 = HashCommon.mix(HashCommon.float2int(var2)) & FloatLinkedOpenHashSet.this.mask; Float.floatToIntBits(var3[var4]) != 0; var4 = var4 + 1 & FloatLinkedOpenHashSet.this.mask) {
               if (Float.floatToIntBits(var3[var4]) == Float.floatToIntBits(var2)) {
                  this.next = (int)FloatLinkedOpenHashSet.this.link[var4];
                  this.prev = var4;
                  return;
               }
            }

            throw new NoSuchElementException("The key " + var2 + " does not belong to this set.");
         }
      }

      public boolean hasNext() {
         return this.next != -1;
      }

      public boolean hasPrevious() {
         return this.prev != -1;
      }

      public float nextFloat() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next;
            this.next = (int)FloatLinkedOpenHashSet.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               ++this.index;
            }

            return FloatLinkedOpenHashSet.this.key[this.curr];
         }
      }

      public float previousFloat() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev;
            this.prev = (int)(FloatLinkedOpenHashSet.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
               --this.index;
            }

            return FloatLinkedOpenHashSet.this.key[this.curr];
         }
      }

      private final void ensureIndexKnown() {
         if (this.index < 0) {
            if (this.prev == -1) {
               this.index = 0;
            } else if (this.next == -1) {
               this.index = FloatLinkedOpenHashSet.this.size;
            } else {
               int var1 = FloatLinkedOpenHashSet.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)FloatLinkedOpenHashSet.this.link[var1];
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

      public void remove() {
         this.ensureIndexKnown();
         if (this.curr == -1) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               --this.index;
               this.prev = (int)(FloatLinkedOpenHashSet.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)FloatLinkedOpenHashSet.this.link[this.curr];
            }

            --FloatLinkedOpenHashSet.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               FloatLinkedOpenHashSet.this.first = this.next;
            } else {
               var6 = FloatLinkedOpenHashSet.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (FloatLinkedOpenHashSet.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               FloatLinkedOpenHashSet.this.last = this.prev;
            } else {
               var6 = FloatLinkedOpenHashSet.this.link;
               var10001 = this.next;
               var6[var10001] ^= (FloatLinkedOpenHashSet.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == FloatLinkedOpenHashSet.this.n) {
               FloatLinkedOpenHashSet.this.containsNull = false;
               FloatLinkedOpenHashSet.this.key[FloatLinkedOpenHashSet.this.n] = 0.0F;
            } else {
               float[] var5 = FloatLinkedOpenHashSet.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & FloatLinkedOpenHashSet.this.mask;

                  float var4;
                  while(true) {
                     if (Float.floatToIntBits(var4 = var5[var3]) == 0) {
                        var5[var1] = 0.0F;
                        return;
                     }

                     int var2 = HashCommon.mix(HashCommon.float2int(var4)) & FloatLinkedOpenHashSet.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & FloatLinkedOpenHashSet.this.mask;
                  }

                  var5[var1] = var4;
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  FloatLinkedOpenHashSet.this.fixPointers(var3, var1);
               }
            }
         }
      }
   }
}
