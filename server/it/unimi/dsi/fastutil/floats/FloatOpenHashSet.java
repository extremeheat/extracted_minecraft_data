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

public class FloatOpenHashSet extends AbstractFloatSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public FloatOpenHashSet(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new float[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public FloatOpenHashSet(int var1) {
      this(var1, 0.75F);
   }

   public FloatOpenHashSet() {
      this(16, 0.75F);
   }

   public FloatOpenHashSet(Collection<? extends Float> var1, float var2) {
      this(var1.size(), var2);
      this.addAll(var1);
   }

   public FloatOpenHashSet(Collection<? extends Float> var1) {
      this(var1, 0.75F);
   }

   public FloatOpenHashSet(FloatCollection var1, float var2) {
      this(var1.size(), var2);
      this.addAll(var1);
   }

   public FloatOpenHashSet(FloatCollection var1) {
      this(var1, 0.75F);
   }

   public FloatOpenHashSet(FloatIterator var1, float var2) {
      this(16, var2);

      while(var1.hasNext()) {
         this.add(var1.nextFloat());
      }

   }

   public FloatOpenHashSet(FloatIterator var1) {
      this(var1, 0.75F);
   }

   public FloatOpenHashSet(Iterator<?> var1, float var2) {
      this(FloatIterators.asFloatIterator(var1), var2);
   }

   public FloatOpenHashSet(Iterator<?> var1) {
      this(FloatIterators.asFloatIterator(var1));
   }

   public FloatOpenHashSet(float[] var1, int var2, int var3, float var4) {
      this(var3 < 0 ? 0 : var3, var4);
      FloatArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public FloatOpenHashSet(float[] var1, int var2, int var3) {
      this(var1, var2, var3, 0.75F);
   }

   public FloatOpenHashSet(float[] var1, float var2) {
      this(var1, 0, var1.length, var2);
   }

   public FloatOpenHashSet(float[] var1) {
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
      if (Float.floatToIntBits(var1) == 0) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         float[] var4 = this.key;
         int var2;
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
      }
   }

   private boolean removeEntry(int var1) {
      --this.size;
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

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNull = false;
         Arrays.fill(this.key, 0.0F);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public FloatIterator iterator() {
      return new FloatOpenHashSet.SetIterator();
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
      int var5 = this.n;

      int var6;
      for(int var7 = this.realSize(); var7-- != 0; var4[var6] = var2[var5]) {
         do {
            --var5;
         } while(Float.floatToIntBits(var2[var5]) == 0);

         if (Float.floatToIntBits(var4[var6 = HashCommon.mix(HashCommon.float2int(var2[var5])) & var3]) != 0) {
            while(Float.floatToIntBits(var4[var6 = var6 + 1 & var3]) != 0) {
            }
         }
      }

      this.n = var1;
      this.mask = var3;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var4;
   }

   public FloatOpenHashSet clone() {
      FloatOpenHashSet var1;
      try {
         var1 = (FloatOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (float[])this.key.clone();
      var1.containsNull = this.containsNull;
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
      FloatIterator var2 = this.iterator();
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

      float var3;
      int var5;
      for(int var4 = this.size; var4-- != 0; var2[var5] = var3) {
         var3 = var1.readFloat();
         if (Float.floatToIntBits(var3) == 0) {
            var5 = this.n;
            this.containsNull = true;
         } else if (Float.floatToIntBits(var2[var5 = HashCommon.mix(HashCommon.float2int(var3)) & this.mask]) != 0) {
            while(Float.floatToIntBits(var2[var5 = var5 + 1 & this.mask]) != 0) {
            }
         }
      }

   }

   private void checkTable() {
   }

   private class SetIterator implements FloatIterator {
      int pos;
      int last;
      int c;
      boolean mustReturnNull;
      FloatArrayList wrapped;

      private SetIterator() {
         super();
         this.pos = FloatOpenHashSet.this.n;
         this.last = -1;
         this.c = FloatOpenHashSet.this.size;
         this.mustReturnNull = FloatOpenHashSet.this.containsNull;
      }

      public boolean hasNext() {
         return this.c != 0;
      }

      public float nextFloat() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = FloatOpenHashSet.this.n;
               return FloatOpenHashSet.this.key[FloatOpenHashSet.this.n];
            } else {
               float[] var1 = FloatOpenHashSet.this.key;

               while(--this.pos >= 0) {
                  if (Float.floatToIntBits(var1[this.pos]) != 0) {
                     return var1[this.last = this.pos];
                  }
               }

               this.last = -2147483648;
               return this.wrapped.getFloat(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int var1) {
         float[] var5 = FloatOpenHashSet.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & FloatOpenHashSet.this.mask;

            float var4;
            while(true) {
               if (Float.floatToIntBits(var4 = var5[var1]) == 0) {
                  var5[var2] = 0.0F;
                  return;
               }

               int var3 = HashCommon.mix(HashCommon.float2int(var4)) & FloatOpenHashSet.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & FloatOpenHashSet.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new FloatArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == FloatOpenHashSet.this.n) {
               FloatOpenHashSet.this.containsNull = false;
               FloatOpenHashSet.this.key[FloatOpenHashSet.this.n] = 0.0F;
            } else {
               if (this.pos < 0) {
                  FloatOpenHashSet.this.remove(this.wrapped.getFloat(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --FloatOpenHashSet.this.size;
            this.last = -1;
         }
      }

      // $FF: synthetic method
      SetIterator(Object var2) {
         this();
      }
   }
}
