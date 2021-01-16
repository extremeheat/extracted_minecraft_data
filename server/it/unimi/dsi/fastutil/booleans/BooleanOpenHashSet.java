package it.unimi.dsi.fastutil.booleans;

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

public class BooleanOpenHashSet extends AbstractBooleanSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient boolean[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public BooleanOpenHashSet(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new boolean[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public BooleanOpenHashSet(int var1) {
      this(var1, 0.75F);
   }

   public BooleanOpenHashSet() {
      this(16, 0.75F);
   }

   public BooleanOpenHashSet(Collection<? extends Boolean> var1, float var2) {
      this(var1.size(), var2);
      this.addAll(var1);
   }

   public BooleanOpenHashSet(Collection<? extends Boolean> var1) {
      this(var1, 0.75F);
   }

   public BooleanOpenHashSet(BooleanCollection var1, float var2) {
      this(var1.size(), var2);
      this.addAll(var1);
   }

   public BooleanOpenHashSet(BooleanCollection var1) {
      this(var1, 0.75F);
   }

   public BooleanOpenHashSet(BooleanIterator var1, float var2) {
      this(16, var2);

      while(var1.hasNext()) {
         this.add(var1.nextBoolean());
      }

   }

   public BooleanOpenHashSet(BooleanIterator var1) {
      this(var1, 0.75F);
   }

   public BooleanOpenHashSet(Iterator<?> var1, float var2) {
      this(BooleanIterators.asBooleanIterator(var1), var2);
   }

   public BooleanOpenHashSet(Iterator<?> var1) {
      this(BooleanIterators.asBooleanIterator(var1));
   }

   public BooleanOpenHashSet(boolean[] var1, int var2, int var3, float var4) {
      this(var3 < 0 ? 0 : var3, var4);
      BooleanArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public BooleanOpenHashSet(boolean[] var1, int var2, int var3) {
      this(var1, var2, var3, 0.75F);
   }

   public BooleanOpenHashSet(boolean[] var1, float var2) {
      this(var1, 0, var1.length, var2);
   }

   public BooleanOpenHashSet(boolean[] var1) {
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

   public boolean addAll(BooleanCollection var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      return super.addAll(var1);
   }

   public boolean addAll(Collection<? extends Boolean> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      return super.addAll(var1);
   }

   public boolean add(boolean var1) {
      if (!var1) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         boolean[] var4 = this.key;
         int var2;
         boolean var3;
         if (var3 = var4[var2 = (var1 ? 262886248 : -878682501) & this.mask]) {
            if (var3 == var1) {
               return false;
            }

            while(var3 = var4[var2 = var2 + 1 & this.mask]) {
               if (var3 == var1) {
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
      boolean[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         boolean var4;
         while(true) {
            if (!(var4 = var5[var1])) {
               var5[var2] = false;
               return;
            }

            int var3 = (var4 ? 262886248 : -878682501) & this.mask;
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
      this.key[this.n] = false;
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   public boolean remove(boolean var1) {
      if (!var1) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         boolean[] var3 = this.key;
         boolean var2;
         int var4;
         if (!(var2 = var3[var4 = (var1 ? 262886248 : -878682501) & this.mask])) {
            return false;
         } else if (var1 == var2) {
            return this.removeEntry(var4);
         } else {
            while(var2 = var3[var4 = var4 + 1 & this.mask]) {
               if (var1 == var2) {
                  return this.removeEntry(var4);
               }
            }

            return false;
         }
      }
   }

   public boolean contains(boolean var1) {
      if (!var1) {
         return this.containsNull;
      } else {
         boolean[] var3 = this.key;
         boolean var2;
         int var4;
         if (!(var2 = var3[var4 = (var1 ? 262886248 : -878682501) & this.mask])) {
            return false;
         } else if (var1 == var2) {
            return true;
         } else {
            while(var2 = var3[var4 = var4 + 1 & this.mask]) {
               if (var1 == var2) {
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
         Arrays.fill(this.key, false);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public BooleanIterator iterator() {
      return new BooleanOpenHashSet.SetIterator();
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
      boolean[] var2 = this.key;
      int var3 = var1 - 1;
      boolean[] var4 = new boolean[var1 + 1];
      int var5 = this.n;

      int var6;
      for(int var7 = this.realSize(); var7-- != 0; var4[var6] = var2[var5]) {
         do {
            --var5;
         } while(!var2[var5]);

         if (var4[var6 = (var2[var5] ? 262886248 : -878682501) & var3]) {
            while(var4[var6 = var6 + 1 & var3]) {
            }
         }
      }

      this.n = var1;
      this.mask = var3;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var4;
   }

   public BooleanOpenHashSet clone() {
      BooleanOpenHashSet var1;
      try {
         var1 = (BooleanOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (boolean[])this.key.clone();
      var1.containsNull = this.containsNull;
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();

      for(int var3 = 0; var2-- != 0; ++var3) {
         while(!this.key[var3]) {
            ++var3;
         }

         var1 += this.key[var3] ? 1231 : 1237;
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      BooleanIterator var2 = this.iterator();
      var1.defaultWriteObject();
      int var3 = this.size;

      while(var3-- != 0) {
         var1.writeBoolean(var2.nextBoolean());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      boolean[] var2 = this.key = new boolean[this.n + 1];

      boolean var3;
      int var5;
      for(int var4 = this.size; var4-- != 0; var2[var5] = var3) {
         var3 = var1.readBoolean();
         if (!var3) {
            var5 = this.n;
            this.containsNull = true;
         } else if (var2[var5 = (var3 ? 262886248 : -878682501) & this.mask]) {
            while(var2[var5 = var5 + 1 & this.mask]) {
            }
         }
      }

   }

   private void checkTable() {
   }

   private class SetIterator implements BooleanIterator {
      int pos;
      int last;
      int c;
      boolean mustReturnNull;
      BooleanArrayList wrapped;

      private SetIterator() {
         super();
         this.pos = BooleanOpenHashSet.this.n;
         this.last = -1;
         this.c = BooleanOpenHashSet.this.size;
         this.mustReturnNull = BooleanOpenHashSet.this.containsNull;
      }

      public boolean hasNext() {
         return this.c != 0;
      }

      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = BooleanOpenHashSet.this.n;
               return BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n];
            } else {
               boolean[] var1 = BooleanOpenHashSet.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos]) {
                     return var1[this.last = this.pos];
                  }
               }

               this.last = -2147483648;
               return this.wrapped.getBoolean(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int var1) {
         boolean[] var5 = BooleanOpenHashSet.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & BooleanOpenHashSet.this.mask;

            boolean var4;
            while(true) {
               if (!(var4 = var5[var1])) {
                  var5[var2] = false;
                  return;
               }

               int var3 = (var4 ? 262886248 : -878682501) & BooleanOpenHashSet.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & BooleanOpenHashSet.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new BooleanArrayList(2);
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
            if (this.last == BooleanOpenHashSet.this.n) {
               BooleanOpenHashSet.this.containsNull = false;
               BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n] = false;
            } else {
               if (this.pos < 0) {
                  BooleanOpenHashSet.this.remove(this.wrapped.getBoolean(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --BooleanOpenHashSet.this.size;
            this.last = -1;
         }
      }

      // $FF: synthetic method
      SetIterator(Object var2) {
         this();
      }
   }
}
