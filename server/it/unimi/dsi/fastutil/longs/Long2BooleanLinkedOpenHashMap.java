package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
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
import java.util.function.LongFunction;
import java.util.function.LongPredicate;

public class Long2BooleanLinkedOpenHashMap extends AbstractLong2BooleanSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient long[] key;
   protected transient boolean[] value;
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
   protected transient Long2BooleanSortedMap.FastSortedEntrySet entries;
   protected transient LongSortedSet keys;
   protected transient BooleanCollection values;

   public Long2BooleanLinkedOpenHashMap(int var1, float var2) {
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
            this.key = new long[this.n + 1];
            this.value = new boolean[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Long2BooleanLinkedOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Long2BooleanLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Long2BooleanLinkedOpenHashMap(Map<? extends Long, ? extends Boolean> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Long2BooleanLinkedOpenHashMap(Map<? extends Long, ? extends Boolean> var1) {
      this(var1, 0.75F);
   }

   public Long2BooleanLinkedOpenHashMap(Long2BooleanMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Long2BooleanLinkedOpenHashMap(Long2BooleanMap var1) {
      this(var1, 0.75F);
   }

   public Long2BooleanLinkedOpenHashMap(long[] var1, boolean[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Long2BooleanLinkedOpenHashMap(long[] var1, boolean[] var2) {
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

   private boolean removeEntry(int var1) {
      boolean var2 = this.value[var1];
      --this.size;
      this.fixPointers(var1);
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private boolean removeNullEntry() {
      this.containsNullKey = false;
      boolean var1 = this.value[this.n];
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Long, ? extends Boolean> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(long var1) {
      if (var1 == 0L) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return -(var6 + 1);
         } else if (var1 == var3) {
            return var6;
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  return var6;
               }
            }

            return -(var6 + 1);
         }
      }
   }

   private void insert(int var1, long var2, boolean var4) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var4;
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

   public boolean put(long var1, boolean var3) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var3);
         return this.defRetValue;
      } else {
         boolean var5 = this.value[var4];
         this.value[var4] = var3;
         return var5;
      }
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
         this.value[var2] = this.value[var1];
         this.fixPointers(var1, var2);
      }
   }

   public boolean remove(long var1) {
      if (var1 == 0L) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (var1 == var3) {
            return this.removeEntry(var6);
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  return this.removeEntry(var6);
               }
            }

            return this.defRetValue;
         }
      }
   }

   private boolean setValue(int var1, boolean var2) {
      boolean var3 = this.value[var1];
      this.value[var1] = var2;
      return var3;
   }

   public boolean removeFirstBoolean() {
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
         boolean var2 = this.value[var1];
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

   public boolean removeLastBoolean() {
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
         boolean var2 = this.value[var1];
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

   public boolean getAndMoveToFirst(long var1) {
      if (var1 == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (var1 == var3) {
            this.moveIndexToFirst(var6);
            return this.value[var6];
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  this.moveIndexToFirst(var6);
                  return this.value[var6];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean getAndMoveToLast(long var1) {
      if (var1 == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (var1 == var3) {
            this.moveIndexToLast(var6);
            return this.value[var6];
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  this.moveIndexToLast(var6);
                  return this.value[var6];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean putAndMoveToFirst(long var1, boolean var3) {
      int var4;
      if (var1 == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var3);
         }

         this.containsNullKey = true;
         var4 = this.n;
      } else {
         long[] var7 = this.key;
         long var5;
         if ((var5 = var7[var4 = (int)HashCommon.mix(var1) & this.mask]) != 0L) {
            if (var5 == var1) {
               this.moveIndexToFirst(var4);
               return this.setValue(var4, var3);
            }

            while((var5 = var7[var4 = var4 + 1 & this.mask]) != 0L) {
               if (var5 == var1) {
                  this.moveIndexToFirst(var4);
                  return this.setValue(var4, var3);
               }
            }
         }
      }

      this.key[var4] = var1;
      this.value[var4] = var3;
      if (this.size == 0) {
         this.first = this.last = var4;
         this.link[var4] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.first;
         var10000[var10001] ^= (this.link[this.first] ^ ((long)var4 & 4294967295L) << 32) & -4294967296L;
         this.link[var4] = -4294967296L | (long)this.first & 4294967295L;
         this.first = var4;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public boolean putAndMoveToLast(long var1, boolean var3) {
      int var4;
      if (var1 == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var3);
         }

         this.containsNullKey = true;
         var4 = this.n;
      } else {
         long[] var7 = this.key;
         long var5;
         if ((var5 = var7[var4 = (int)HashCommon.mix(var1) & this.mask]) != 0L) {
            if (var5 == var1) {
               this.moveIndexToLast(var4);
               return this.setValue(var4, var3);
            }

            while((var5 = var7[var4 = var4 + 1 & this.mask]) != 0L) {
               if (var5 == var1) {
                  this.moveIndexToLast(var4);
                  return this.setValue(var4, var3);
               }
            }
         }
      }

      this.key[var4] = var1;
      this.value[var4] = var3;
      if (this.size == 0) {
         this.first = this.last = var4;
         this.link[var4] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var4 & 4294967295L) & 4294967295L;
         this.link[var4] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var4;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public boolean get(long var1) {
      if (var1 == 0L) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         long[] var5 = this.key;
         long var3;
         int var6;
         if ((var3 = var5[var6 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (var1 == var3) {
            return this.value[var6];
         } else {
            while((var3 = var5[var6 = var6 + 1 & this.mask]) != 0L) {
               if (var1 == var3) {
                  return this.value[var6];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(long var1) {
      if (var1 == 0L) {
         return this.containsNullKey;
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

   public boolean containsValue(boolean var1) {
      boolean[] var2 = this.value;
      long[] var3 = this.key;
      if (this.containsNullKey && var2[this.n] == var1) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(var3[var4] == 0L || var2[var4] != var1);

         return true;
      }
   }

   public boolean getOrDefault(long var1, boolean var3) {
      if (var1 == 0L) {
         return this.containsNullKey ? this.value[this.n] : var3;
      } else {
         long[] var6 = this.key;
         long var4;
         int var7;
         if ((var4 = var6[var7 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return var3;
         } else if (var1 == var4) {
            return this.value[var7];
         } else {
            while((var4 = var6[var7 = var7 + 1 & this.mask]) != 0L) {
               if (var1 == var4) {
                  return this.value[var7];
               }
            }

            return var3;
         }
      }
   }

   public boolean putIfAbsent(long var1, boolean var3) {
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         this.insert(-var4 - 1, var1, var3);
         return this.defRetValue;
      }
   }

   public boolean remove(long var1, boolean var3) {
      if (var1 == 0L) {
         if (this.containsNullKey && var3 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         long[] var6 = this.key;
         long var4;
         int var7;
         if ((var4 = var6[var7 = (int)HashCommon.mix(var1) & this.mask]) == 0L) {
            return false;
         } else if (var1 == var4 && var3 == this.value[var7]) {
            this.removeEntry(var7);
            return true;
         } else {
            do {
               if ((var4 = var6[var7 = var7 + 1 & this.mask]) == 0L) {
                  return false;
               }
            } while(var1 != var4 || var3 != this.value[var7]);

            this.removeEntry(var7);
            return true;
         }
      }
   }

   public boolean replace(long var1, boolean var3, boolean var4) {
      int var5 = this.find(var1);
      if (var5 >= 0 && var3 == this.value[var5]) {
         this.value[var5] = var4;
         return true;
      } else {
         return false;
      }
   }

   public boolean replace(long var1, boolean var3) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         boolean var5 = this.value[var4];
         this.value[var4] = var3;
         return var5;
      }
   }

   public boolean computeIfAbsent(long var1, LongPredicate var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         boolean var5 = var3.test(var1);
         this.insert(-var4 - 1, var1, var5);
         return var5;
      }
   }

   public boolean computeIfAbsentNullable(long var1, LongFunction<? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         Boolean var5 = (Boolean)var3.apply(var1);
         if (var5 == null) {
            return this.defRetValue;
         } else {
            boolean var6 = var5;
            this.insert(-var4 - 1, var1, var6);
            return var6;
         }
      }
   }

   public boolean computeIfPresent(long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         Boolean var5 = (Boolean)var3.apply(var1, this.value[var4]);
         if (var5 == null) {
            if (var1 == 0L) {
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

   public boolean compute(long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      Boolean var5 = (Boolean)var3.apply(var1, var4 >= 0 ? this.value[var4] : null);
      if (var5 == null) {
         if (var4 >= 0) {
            if (var1 == 0L) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var4);
            }
         }

         return this.defRetValue;
      } else {
         boolean var6 = var5;
         if (var4 < 0) {
            this.insert(-var4 - 1, var1, var6);
            return var6;
         } else {
            return this.value[var4] = var6;
         }
      }
   }

   public boolean merge(long var1, boolean var3, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var4) {
      Objects.requireNonNull(var4);
      int var5 = this.find(var1);
      if (var5 < 0) {
         this.insert(-var5 - 1, var1, var3);
         return var3;
      } else {
         Boolean var6 = (Boolean)var4.apply(this.value[var5], var3);
         if (var6 == null) {
            if (var1 == 0L) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var5);
            }

            return this.defRetValue;
         } else {
            return this.value[var5] = var6;
         }
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, 0L);
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

   public long firstLongKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public long lastLongKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Long2BooleanSortedMap tailMap(long var1) {
      throw new UnsupportedOperationException();
   }

   public Long2BooleanSortedMap headMap(long var1) {
      throw new UnsupportedOperationException();
   }

   public Long2BooleanSortedMap subMap(long var1, long var3) {
      throw new UnsupportedOperationException();
   }

   public LongComparator comparator() {
      return null;
   }

   public Long2BooleanSortedMap.FastSortedEntrySet long2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2BooleanLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public LongSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2BooleanLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new AbstractBooleanCollection() {
            public BooleanIterator iterator() {
               return Long2BooleanLinkedOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Long2BooleanLinkedOpenHashMap.this.size;
            }

            public boolean contains(boolean var1) {
               return Long2BooleanLinkedOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Long2BooleanLinkedOpenHashMap.this.clear();
            }

            public void forEach(BooleanConsumer var1) {
               if (Long2BooleanLinkedOpenHashMap.this.containsNullKey) {
                  var1.accept(Long2BooleanLinkedOpenHashMap.this.value[Long2BooleanLinkedOpenHashMap.this.n]);
               }

               int var2 = Long2BooleanLinkedOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Long2BooleanLinkedOpenHashMap.this.key[var2] != 0L) {
                     var1.accept(Long2BooleanLinkedOpenHashMap.this.value[var2]);
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
      long[] var2 = this.key;
      boolean[] var3 = this.value;
      int var4 = var1 - 1;
      long[] var5 = new long[var1 + 1];
      boolean[] var6 = new boolean[var1 + 1];
      int var7 = this.first;
      int var8 = -1;
      int var9 = -1;
      long[] var12 = this.link;
      long[] var13 = new long[var1 + 1];
      this.first = -1;

      int var10;
      for(int var14 = this.size; var14-- != 0; var8 = var10) {
         int var11;
         if (var2[var7] == 0L) {
            var11 = var1;
         } else {
            for(var11 = (int)HashCommon.mix(var2[var7]) & var4; var5[var11] != 0L; var11 = var11 + 1 & var4) {
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

   public Long2BooleanLinkedOpenHashMap clone() {
      Long2BooleanLinkedOpenHashMap var1;
      try {
         var1 = (Long2BooleanLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (long[])this.key.clone();
      var1.value = (boolean[])this.value.clone();
      var1.link = (long[])this.link.clone();
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(boolean var4 = false; var2-- != 0; ++var3) {
         while(this.key[var3] == 0L) {
            ++var3;
         }

         int var5 = HashCommon.long2int(this.key[var3]);
         var5 ^= this.value[var3] ? 1231 : 1237;
         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] ? 1231 : 1237;
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      long[] var2 = this.key;
      boolean[] var3 = this.value;
      Long2BooleanLinkedOpenHashMap.MapIterator var4 = new Long2BooleanLinkedOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeLong(var2[var6]);
         var1.writeBoolean(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      long[] var2 = this.key = new long[this.n + 1];
      boolean[] var3 = this.value = new boolean[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var9 = this.size;

      while(var9-- != 0) {
         long var6 = var1.readLong();
         boolean var8 = var1.readBoolean();
         int var10;
         if (var6 == 0L) {
            var10 = this.n;
            this.containsNullKey = true;
         } else {
            for(var10 = (int)HashCommon.mix(var6) & this.mask; var2[var10] != 0L; var10 = var10 + 1 & this.mask) {
            }
         }

         var2[var10] = var6;
         var3[var10] = var8;
         if (this.first != -1) {
            var4[var5] ^= (var4[var5] ^ (long)var10 & 4294967295L) & 4294967295L;
            var4[var10] ^= (var4[var10] ^ ((long)var5 & 4294967295L) << 32) & -4294967296L;
            var5 = var10;
         } else {
            var5 = this.first = var10;
            var4[var10] |= -4294967296L;
         }
      }

      this.last = var5;
      if (var5 != -1) {
         var4[var5] |= 4294967295L;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Long2BooleanLinkedOpenHashMap.MapIterator implements BooleanListIterator {
      public boolean previousBoolean() {
         return Long2BooleanLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public boolean nextBoolean() {
         return Long2BooleanLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractLongSortedSet {
      private KeySet() {
         super();
      }

      public LongListIterator iterator(long var1) {
         return Long2BooleanLinkedOpenHashMap.this.new KeyIterator(var1);
      }

      public LongListIterator iterator() {
         return Long2BooleanLinkedOpenHashMap.this.new KeyIterator();
      }

      public void forEach(java.util.function.LongConsumer var1) {
         if (Long2BooleanLinkedOpenHashMap.this.containsNullKey) {
            var1.accept(Long2BooleanLinkedOpenHashMap.this.key[Long2BooleanLinkedOpenHashMap.this.n]);
         }

         int var2 = Long2BooleanLinkedOpenHashMap.this.n;

         while(var2-- != 0) {
            long var3 = Long2BooleanLinkedOpenHashMap.this.key[var2];
            if (var3 != 0L) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Long2BooleanLinkedOpenHashMap.this.size;
      }

      public boolean contains(long var1) {
         return Long2BooleanLinkedOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(long var1) {
         int var3 = Long2BooleanLinkedOpenHashMap.this.size;
         Long2BooleanLinkedOpenHashMap.this.remove(var1);
         return Long2BooleanLinkedOpenHashMap.this.size != var3;
      }

      public void clear() {
         Long2BooleanLinkedOpenHashMap.this.clear();
      }

      public long firstLong() {
         if (Long2BooleanLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Long2BooleanLinkedOpenHashMap.this.key[Long2BooleanLinkedOpenHashMap.this.first];
         }
      }

      public long lastLong() {
         if (Long2BooleanLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Long2BooleanLinkedOpenHashMap.this.key[Long2BooleanLinkedOpenHashMap.this.last];
         }
      }

      public LongComparator comparator() {
         return null;
      }

      public LongSortedSet tailSet(long var1) {
         throw new UnsupportedOperationException();
      }

      public LongSortedSet headSet(long var1) {
         throw new UnsupportedOperationException();
      }

      public LongSortedSet subSet(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Long2BooleanLinkedOpenHashMap.MapIterator implements LongListIterator {
      public KeyIterator(long var2) {
         super(var2, null);
      }

      public long previousLong() {
         return Long2BooleanLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public long nextLong() {
         return Long2BooleanLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Long2BooleanMap.Entry> implements Long2BooleanSortedMap.FastSortedEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Long2BooleanMap.Entry> iterator() {
         return Long2BooleanLinkedOpenHashMap.this.new EntryIterator();
      }

      public Comparator<? super Long2BooleanMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Long2BooleanMap.Entry> subSet(Long2BooleanMap.Entry var1, Long2BooleanMap.Entry var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Long2BooleanMap.Entry> headSet(Long2BooleanMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Long2BooleanMap.Entry> tailSet(Long2BooleanMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public Long2BooleanMap.Entry first() {
         if (Long2BooleanLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Long2BooleanLinkedOpenHashMap.this.new MapEntry(Long2BooleanLinkedOpenHashMap.this.first);
         }
      }

      public Long2BooleanMap.Entry last() {
         if (Long2BooleanLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Long2BooleanLinkedOpenHashMap.this.new MapEntry(Long2BooleanLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  long var3 = (Long)var2.getKey();
                  boolean var5 = (Boolean)var2.getValue();
                  if (var3 == 0L) {
                     return Long2BooleanLinkedOpenHashMap.this.containsNullKey && Long2BooleanLinkedOpenHashMap.this.value[Long2BooleanLinkedOpenHashMap.this.n] == var5;
                  } else {
                     long[] var8 = Long2BooleanLinkedOpenHashMap.this.key;
                     long var6;
                     int var9;
                     if ((var6 = var8[var9 = (int)HashCommon.mix(var3) & Long2BooleanLinkedOpenHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (var3 == var6) {
                        return Long2BooleanLinkedOpenHashMap.this.value[var9] == var5;
                     } else {
                        while((var6 = var8[var9 = var9 + 1 & Long2BooleanLinkedOpenHashMap.this.mask]) != 0L) {
                           if (var3 == var6) {
                              return Long2BooleanLinkedOpenHashMap.this.value[var9] == var5;
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
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  long var3 = (Long)var2.getKey();
                  boolean var5 = (Boolean)var2.getValue();
                  if (var3 == 0L) {
                     if (Long2BooleanLinkedOpenHashMap.this.containsNullKey && Long2BooleanLinkedOpenHashMap.this.value[Long2BooleanLinkedOpenHashMap.this.n] == var5) {
                        Long2BooleanLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     long[] var8 = Long2BooleanLinkedOpenHashMap.this.key;
                     long var6;
                     int var9;
                     if ((var6 = var8[var9 = (int)HashCommon.mix(var3) & Long2BooleanLinkedOpenHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (var6 == var3) {
                        if (Long2BooleanLinkedOpenHashMap.this.value[var9] == var5) {
                           Long2BooleanLinkedOpenHashMap.this.removeEntry(var9);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var6 = var8[var9 = var9 + 1 & Long2BooleanLinkedOpenHashMap.this.mask]) == 0L) {
                              return false;
                           }
                        } while(var6 != var3 || Long2BooleanLinkedOpenHashMap.this.value[var9] != var5);

                        Long2BooleanLinkedOpenHashMap.this.removeEntry(var9);
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
         return Long2BooleanLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Long2BooleanLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Long2BooleanMap.Entry> iterator(Long2BooleanMap.Entry var1) {
         return Long2BooleanLinkedOpenHashMap.this.new EntryIterator(var1.getLongKey());
      }

      public ObjectListIterator<Long2BooleanMap.Entry> fastIterator() {
         return Long2BooleanLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Long2BooleanMap.Entry> fastIterator(Long2BooleanMap.Entry var1) {
         return Long2BooleanLinkedOpenHashMap.this.new FastEntryIterator(var1.getLongKey());
      }

      public void forEach(Consumer<? super Long2BooleanMap.Entry> var1) {
         int var2 = Long2BooleanLinkedOpenHashMap.this.size;
         int var4 = Long2BooleanLinkedOpenHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Long2BooleanLinkedOpenHashMap.this.link[var4];
            var1.accept(new AbstractLong2BooleanMap.BasicEntry(Long2BooleanLinkedOpenHashMap.this.key[var3], Long2BooleanLinkedOpenHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Long2BooleanMap.Entry> var1) {
         AbstractLong2BooleanMap.BasicEntry var2 = new AbstractLong2BooleanMap.BasicEntry();
         int var3 = Long2BooleanLinkedOpenHashMap.this.size;
         int var5 = Long2BooleanLinkedOpenHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Long2BooleanLinkedOpenHashMap.this.link[var5];
            var2.key = Long2BooleanLinkedOpenHashMap.this.key[var4];
            var2.value = Long2BooleanLinkedOpenHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Long2BooleanLinkedOpenHashMap.MapIterator implements ObjectListIterator<Long2BooleanMap.Entry> {
      final Long2BooleanLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Long2BooleanLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(long var2) {
         super(var2, null);
         this.entry = Long2BooleanLinkedOpenHashMap.this.new MapEntry();
      }

      public Long2BooleanLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Long2BooleanLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Long2BooleanLinkedOpenHashMap.MapIterator implements ObjectListIterator<Long2BooleanMap.Entry> {
      private Long2BooleanLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(long var2) {
         super(var2, null);
      }

      public Long2BooleanLinkedOpenHashMap.MapEntry next() {
         return this.entry = Long2BooleanLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Long2BooleanLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Long2BooleanLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
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
         this.next = Long2BooleanLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(long var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (var2 == 0L) {
            if (Long2BooleanLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Long2BooleanLinkedOpenHashMap.this.link[Long2BooleanLinkedOpenHashMap.this.n];
               this.prev = Long2BooleanLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Long2BooleanLinkedOpenHashMap.this.key[Long2BooleanLinkedOpenHashMap.this.last] == var2) {
            this.prev = Long2BooleanLinkedOpenHashMap.this.last;
            this.index = Long2BooleanLinkedOpenHashMap.this.size;
         } else {
            for(int var4 = (int)HashCommon.mix(var2) & Long2BooleanLinkedOpenHashMap.this.mask; Long2BooleanLinkedOpenHashMap.this.key[var4] != 0L; var4 = var4 + 1 & Long2BooleanLinkedOpenHashMap.this.mask) {
               if (Long2BooleanLinkedOpenHashMap.this.key[var4] == var2) {
                  this.next = (int)Long2BooleanLinkedOpenHashMap.this.link[var4];
                  this.prev = var4;
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
               this.index = Long2BooleanLinkedOpenHashMap.this.size;
            } else {
               int var1 = Long2BooleanLinkedOpenHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Long2BooleanLinkedOpenHashMap.this.link[var1];
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
            this.next = (int)Long2BooleanLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Long2BooleanLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
               this.prev = (int)(Long2BooleanLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Long2BooleanLinkedOpenHashMap.this.link[this.curr];
            }

            --Long2BooleanLinkedOpenHashMap.this.size;
            int var10001;
            long[] var7;
            if (this.prev == -1) {
               Long2BooleanLinkedOpenHashMap.this.first = this.next;
            } else {
               var7 = Long2BooleanLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var7[var10001] ^= (Long2BooleanLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Long2BooleanLinkedOpenHashMap.this.last = this.prev;
            } else {
               var7 = Long2BooleanLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var7[var10001] ^= (Long2BooleanLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Long2BooleanLinkedOpenHashMap.this.n) {
               Long2BooleanLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               long[] var6 = Long2BooleanLinkedOpenHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Long2BooleanLinkedOpenHashMap.this.mask;

                  long var4;
                  while(true) {
                     if ((var4 = var6[var3]) == 0L) {
                        var6[var1] = 0L;
                        return;
                     }

                     int var2 = (int)HashCommon.mix(var4) & Long2BooleanLinkedOpenHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Long2BooleanLinkedOpenHashMap.this.mask;
                  }

                  var6[var1] = var4;
                  Long2BooleanLinkedOpenHashMap.this.value[var1] = Long2BooleanLinkedOpenHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Long2BooleanLinkedOpenHashMap.this.fixPointers(var3, var1);
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

      public void set(Long2BooleanMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Long2BooleanMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(long var2, Object var4) {
         this(var2);
      }
   }

   final class MapEntry implements Long2BooleanMap.Entry, java.util.Map.Entry<Long, Boolean> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public long getLongKey() {
         return Long2BooleanLinkedOpenHashMap.this.key[this.index];
      }

      public boolean getBooleanValue() {
         return Long2BooleanLinkedOpenHashMap.this.value[this.index];
      }

      public boolean setValue(boolean var1) {
         boolean var2 = Long2BooleanLinkedOpenHashMap.this.value[this.index];
         Long2BooleanLinkedOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Long getKey() {
         return Long2BooleanLinkedOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Boolean getValue() {
         return Long2BooleanLinkedOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Boolean setValue(Boolean var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Long2BooleanLinkedOpenHashMap.this.key[this.index] == (Long)var2.getKey() && Long2BooleanLinkedOpenHashMap.this.value[this.index] == (Boolean)var2.getValue();
         }
      }

      public int hashCode() {
         return HashCommon.long2int(Long2BooleanLinkedOpenHashMap.this.key[this.index]) ^ (Long2BooleanLinkedOpenHashMap.this.value[this.index] ? 1231 : 1237);
      }

      public String toString() {
         return Long2BooleanLinkedOpenHashMap.this.key[this.index] + "=>" + Long2BooleanLinkedOpenHashMap.this.value[this.index];
      }
   }
}
