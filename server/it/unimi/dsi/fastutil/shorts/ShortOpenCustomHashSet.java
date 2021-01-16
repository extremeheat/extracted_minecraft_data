package it.unimi.dsi.fastutil.shorts;

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

public class ShortOpenCustomHashSet extends AbstractShortSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient short[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected ShortHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public ShortOpenCustomHashSet(int var1, float var2, ShortHash.Strategy var3) {
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
            this.key = new short[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public ShortOpenCustomHashSet(int var1, ShortHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public ShortOpenCustomHashSet(ShortHash.Strategy var1) {
      this(16, 0.75F, var1);
   }

   public ShortOpenCustomHashSet(Collection<? extends Short> var1, float var2, ShortHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.addAll(var1);
   }

   public ShortOpenCustomHashSet(Collection<? extends Short> var1, ShortHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public ShortOpenCustomHashSet(ShortCollection var1, float var2, ShortHash.Strategy var3) {
      this(var1.size(), var2, var3);
      this.addAll(var1);
   }

   public ShortOpenCustomHashSet(ShortCollection var1, ShortHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public ShortOpenCustomHashSet(ShortIterator var1, float var2, ShortHash.Strategy var3) {
      this(16, var2, var3);

      while(var1.hasNext()) {
         this.add(var1.nextShort());
      }

   }

   public ShortOpenCustomHashSet(ShortIterator var1, ShortHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public ShortOpenCustomHashSet(Iterator<?> var1, float var2, ShortHash.Strategy var3) {
      this(ShortIterators.asShortIterator(var1), var2, var3);
   }

   public ShortOpenCustomHashSet(Iterator<?> var1, ShortHash.Strategy var2) {
      this(ShortIterators.asShortIterator(var1), var2);
   }

   public ShortOpenCustomHashSet(short[] var1, int var2, int var3, float var4, ShortHash.Strategy var5) {
      this(var3 < 0 ? 0 : var3, var4, var5);
      ShortArrays.ensureOffsetLength(var1, var2, var3);

      for(int var6 = 0; var6 < var3; ++var6) {
         this.add(var1[var2 + var6]);
      }

   }

   public ShortOpenCustomHashSet(short[] var1, int var2, int var3, ShortHash.Strategy var4) {
      this(var1, var2, var3, 0.75F, var4);
   }

   public ShortOpenCustomHashSet(short[] var1, float var2, ShortHash.Strategy var3) {
      this(var1, 0, var1.length, var2, var3);
   }

   public ShortOpenCustomHashSet(short[] var1, ShortHash.Strategy var2) {
      this(var1, 0.75F, var2);
   }

   public ShortHash.Strategy strategy() {
      return this.strategy;
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

   public boolean addAll(ShortCollection var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      return super.addAll(var1);
   }

   public boolean addAll(Collection<? extends Short> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      return super.addAll(var1);
   }

   public boolean add(short var1) {
      if (this.strategy.equals(var1, (short)0)) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
         this.key[this.n] = var1;
      } else {
         short[] var4 = this.key;
         int var2;
         short var3;
         if ((var3 = var4[var2 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) != 0) {
            if (this.strategy.equals(var3, var1)) {
               return false;
            }

            while((var3 = var4[var2 = var2 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var3, var1)) {
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
      short[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         short var4;
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
      this.key[this.n] = 0;
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   public boolean remove(short var1) {
      if (this.strategy.equals(var1, (short)0)) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         short[] var3 = this.key;
         short var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(var1, var2)) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (this.strategy.equals(var1, var2)) {
                  return this.removeEntry(var4);
               }
            }

            return false;
         }
      }
   }

   public boolean contains(short var1) {
      if (this.strategy.equals(var1, (short)0)) {
         return this.containsNull;
      } else {
         short[] var3 = this.key;
         short var2;
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

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNull = false;
         Arrays.fill(this.key, (short)0);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public ShortIterator iterator() {
      return new ShortOpenCustomHashSet.SetIterator();
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
      short[] var2 = this.key;
      int var3 = var1 - 1;
      short[] var4 = new short[var1 + 1];
      int var5 = this.n;

      int var6;
      for(int var7 = this.realSize(); var7-- != 0; var4[var6] = var2[var5]) {
         do {
            --var5;
         } while(var2[var5] == 0);

         if (var4[var6 = HashCommon.mix(this.strategy.hashCode(var2[var5])) & var3] != 0) {
            while(var4[var6 = var6 + 1 & var3] != 0) {
            }
         }
      }

      this.n = var1;
      this.mask = var3;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var4;
   }

   public ShortOpenCustomHashSet clone() {
      ShortOpenCustomHashSet var1;
      try {
         var1 = (ShortOpenCustomHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (short[])this.key.clone();
      var1.containsNull = this.containsNull;
      var1.strategy = this.strategy;
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();

      for(int var3 = 0; var2-- != 0; ++var3) {
         while(this.key[var3] == 0) {
            ++var3;
         }

         var1 += this.strategy.hashCode(this.key[var3]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ShortIterator var2 = this.iterator();
      var1.defaultWriteObject();
      int var3 = this.size;

      while(var3-- != 0) {
         var1.writeShort(var2.nextShort());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      short[] var2 = this.key = new short[this.n + 1];

      short var3;
      int var5;
      for(int var4 = this.size; var4-- != 0; var2[var5] = var3) {
         var3 = var1.readShort();
         if (this.strategy.equals(var3, (short)0)) {
            var5 = this.n;
            this.containsNull = true;
         } else if (var2[var5 = HashCommon.mix(this.strategy.hashCode(var3)) & this.mask] != 0) {
            while(var2[var5 = var5 + 1 & this.mask] != 0) {
            }
         }
      }

   }

   private void checkTable() {
   }

   private class SetIterator implements ShortIterator {
      int pos;
      int last;
      int c;
      boolean mustReturnNull;
      ShortArrayList wrapped;

      private SetIterator() {
         super();
         this.pos = ShortOpenCustomHashSet.this.n;
         this.last = -1;
         this.c = ShortOpenCustomHashSet.this.size;
         this.mustReturnNull = ShortOpenCustomHashSet.this.containsNull;
      }

      public boolean hasNext() {
         return this.c != 0;
      }

      public short nextShort() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = ShortOpenCustomHashSet.this.n;
               return ShortOpenCustomHashSet.this.key[ShortOpenCustomHashSet.this.n];
            } else {
               short[] var1 = ShortOpenCustomHashSet.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0) {
                     return var1[this.last = this.pos];
                  }
               }

               this.last = -2147483648;
               return this.wrapped.getShort(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int var1) {
         short[] var5 = ShortOpenCustomHashSet.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & ShortOpenCustomHashSet.this.mask;

            short var4;
            while(true) {
               if ((var4 = var5[var1]) == 0) {
                  var5[var2] = 0;
                  return;
               }

               int var3 = HashCommon.mix(ShortOpenCustomHashSet.this.strategy.hashCode(var4)) & ShortOpenCustomHashSet.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & ShortOpenCustomHashSet.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new ShortArrayList(2);
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
            if (this.last == ShortOpenCustomHashSet.this.n) {
               ShortOpenCustomHashSet.this.containsNull = false;
               ShortOpenCustomHashSet.this.key[ShortOpenCustomHashSet.this.n] = 0;
            } else {
               if (this.pos < 0) {
                  ShortOpenCustomHashSet.this.remove(this.wrapped.getShort(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --ShortOpenCustomHashSet.this.size;
            this.last = -1;
         }
      }

      // $FF: synthetic method
      SetIterator(Object var2) {
         this();
      }
   }
}
