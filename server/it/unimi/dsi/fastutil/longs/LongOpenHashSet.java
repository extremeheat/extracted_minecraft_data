package it.unimi.dsi.fastutil.longs;

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

public class LongOpenHashSet extends AbstractLongSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient long[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public LongOpenHashSet(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public LongOpenHashSet(int var1) {
      this(var1, 0.75F);
   }

   public LongOpenHashSet() {
      this(16, 0.75F);
   }

   public LongOpenHashSet(Collection<? extends Long> var1, float var2) {
      this(var1.size(), var2);
      this.addAll(var1);
   }

   public LongOpenHashSet(Collection<? extends Long> var1) {
      this(var1, 0.75F);
   }

   public LongOpenHashSet(LongCollection var1, float var2) {
      this(var1.size(), var2);
      this.addAll(var1);
   }

   public LongOpenHashSet(LongCollection var1) {
      this(var1, 0.75F);
   }

   public LongOpenHashSet(LongIterator var1, float var2) {
      this(16, var2);

      while(var1.hasNext()) {
         this.add(var1.nextLong());
      }

   }

   public LongOpenHashSet(LongIterator var1) {
      this(var1, 0.75F);
   }

   public LongOpenHashSet(Iterator<?> var1, float var2) {
      this(LongIterators.asLongIterator(var1), var2);
   }

   public LongOpenHashSet(Iterator<?> var1) {
      this(LongIterators.asLongIterator(var1));
   }

   public LongOpenHashSet(long[] var1, int var2, int var3, float var4) {
      this(var3 < 0 ? 0 : var3, var4);
      LongArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public LongOpenHashSet(long[] var1, int var2, int var3) {
      this(var1, var2, var3, 0.75F);
   }

   public LongOpenHashSet(long[] var1, float var2) {
      this(var1, 0, var1.length, var2);
   }

   public LongOpenHashSet(long[] var1) {
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

   public boolean addAll(LongCollection var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      return super.addAll(var1);
   }

   public boolean addAll(Collection<? extends Long> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      return super.addAll(var1);
   }

   public boolean add(long var1) {
      if (var1 == 0L) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         long[] var6 = this.key;
         int var3;
         long var4;
         if ((var4 = var6[var3 = (int)HashCommon.mix(var1) & this.mask]) != 0L) {
            if (var4 == var1) {
               return false;
            }

            while((var4 = var6[var3 = var3 + 1 & this.mask]) != 0L) {
               if (var4 == var1) {
                  return false;
               }
            }
         }

         var6[var3] = var1;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return true;
   }

   protected final void shiftKeys(int var1) {
      long[] var6 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         long var4;
         while(true) {
            if ((var4 = var6[var1]) == 0L) {
               var6[var2] = 0L;
               return;
            }

            int var3 = (int)HashCommon.mix(var4) & this.mask;
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
      this.key[this.n] = 0L;
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   public boolean remove(long var1) {
      if (var1 == 0L) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return false;
         } else if (var1 == var3) {
            return this.removeEntry(var6);
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  return this.removeEntry(var6);
               }
            }

            return false;
         }
      }
   }

   public boolean contains(long var1) {
      if (var1 == 0L) {
         return this.containsNull;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return false;
         } else if (var1 == var3) {
            return true;
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
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
         Arrays.fill(this.key, 0L);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public LongIterator iterator() {
      return new LongOpenHashSet.SetIterator();
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
      long[] var2 = this.key;
      int var3 = var1 - 1;
      long[] var4 = new long[var1 + 1];
      int var5 = this.n;

      int var6;
      for(int var7 = this.realSize(); var7-- != 0; var4[var6] = var2[var5]) {
         do {
            --var5;
         } while(var2[var5] == 0L);

         if (var4[var6 = (int)HashCommon.mix(var2[var5]) & var3] != 0L) {
            while(var4[var6 = var6 + 1 & var3] != 0L) {
            }
         }
      }

      this.n = var1;
      this.mask = var3;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var4;
   }

   public LongOpenHashSet clone() {
      LongOpenHashSet var1;
      try {
         var1 = (LongOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (long[])this.key.clone();
      var1.containsNull = this.containsNull;
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();

      for(int var3 = 0; var2-- != 0; ++var3) {
         while(this.key[var3] == 0L) {
            ++var3;
         }

         var1 += HashCommon.long2int(this.key[var3]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      LongIterator var2 = this.iterator();
      var1.defaultWriteObject();
      int var3 = this.size;

      while(var3-- != 0) {
         var1.writeLong(var2.nextLong());
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      long[] var2 = this.key = new long[this.n + 1];

      long var3;
      int var6;
      for(int var5 = this.size; var5-- != 0; var2[var6] = var3) {
         var3 = var1.readLong();
         if (var3 == 0L) {
            var6 = this.n;
            this.containsNull = true;
         } else if (var2[var6 = (int)HashCommon.mix(var3) & this.mask] != 0L) {
            while(var2[var6 = var6 + 1 & this.mask] != 0L) {
            }
         }
      }

   }

   private void checkTable() {
   }

   private class SetIterator implements LongIterator {
      int pos;
      int last;
      int c;
      boolean mustReturnNull;
      LongArrayList wrapped;

      private SetIterator() {
         super();
         this.pos = LongOpenHashSet.this.n;
         this.last = -1;
         this.c = LongOpenHashSet.this.size;
         this.mustReturnNull = LongOpenHashSet.this.containsNull;
      }

      public boolean hasNext() {
         return this.c != 0;
      }

      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = LongOpenHashSet.this.n;
               return LongOpenHashSet.this.key[LongOpenHashSet.this.n];
            } else {
               long[] var1 = LongOpenHashSet.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0L) {
                     return var1[this.last = this.pos];
                  }
               }

               this.last = -2147483648;
               return this.wrapped.getLong(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int var1) {
         long[] var6 = LongOpenHashSet.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & LongOpenHashSet.this.mask;

            long var4;
            while(true) {
               if ((var4 = var6[var1]) == 0L) {
                  var6[var2] = 0L;
                  return;
               }

               int var3 = (int)HashCommon.mix(var4) & LongOpenHashSet.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & LongOpenHashSet.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new LongArrayList(2);
               }

               this.wrapped.add(var6[var1]);
            }

            var6[var2] = var4;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == LongOpenHashSet.this.n) {
               LongOpenHashSet.this.containsNull = false;
               LongOpenHashSet.this.key[LongOpenHashSet.this.n] = 0L;
            } else {
               if (this.pos < 0) {
                  LongOpenHashSet.this.remove(this.wrapped.getLong(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --LongOpenHashSet.this.size;
            this.last = -1;
         }
      }

      // $FF: synthetic method
      SetIterator(Object var2) {
         this();
      }
   }
}
