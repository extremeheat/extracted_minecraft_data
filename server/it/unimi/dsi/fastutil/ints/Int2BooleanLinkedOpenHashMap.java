package it.unimi.dsi.fastutil.ints;

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
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public class Int2BooleanLinkedOpenHashMap extends AbstractInt2BooleanSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient int[] key;
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
   protected transient Int2BooleanSortedMap.FastSortedEntrySet entries;
   protected transient IntSortedSet keys;
   protected transient BooleanCollection values;

   public Int2BooleanLinkedOpenHashMap(int var1, float var2) {
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
            this.key = new int[this.n + 1];
            this.value = new boolean[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Int2BooleanLinkedOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Int2BooleanLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Int2BooleanLinkedOpenHashMap(Map<? extends Integer, ? extends Boolean> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Int2BooleanLinkedOpenHashMap(Map<? extends Integer, ? extends Boolean> var1) {
      this(var1, 0.75F);
   }

   public Int2BooleanLinkedOpenHashMap(Int2BooleanMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Int2BooleanLinkedOpenHashMap(Int2BooleanMap var1) {
      this(var1, 0.75F);
   }

   public Int2BooleanLinkedOpenHashMap(int[] var1, boolean[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Int2BooleanLinkedOpenHashMap(int[] var1, boolean[] var2) {
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

   public void putAll(Map<? extends Integer, ? extends Boolean> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(int var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         int[] var3 = this.key;
         int var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return -(var4 + 1);
         } else if (var1 == var2) {
            return var4;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, int var2, boolean var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
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

   public boolean put(int var1, boolean var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         boolean var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   protected final void shiftKeys(int var1) {
      int[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         int var4;
         while(true) {
            if ((var4 = var5[var1]) == 0) {
               var5[var2] = 0;
               return;
            }

            int var3 = HashCommon.mix(var4) & this.mask;
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
         this.value[var2] = this.value[var1];
         this.fixPointers(var1, var2);
      }
   }

   public boolean remove(int var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         int[] var3 = this.key;
         int var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (var1 == var2) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  return this.removeEntry(var4);
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

   public boolean getAndMoveToFirst(int var1) {
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         int[] var3 = this.key;
         int var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (var1 == var2) {
            this.moveIndexToFirst(var4);
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  this.moveIndexToFirst(var4);
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean getAndMoveToLast(int var1) {
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         int[] var3 = this.key;
         int var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (var1 == var2) {
            this.moveIndexToLast(var4);
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  this.moveIndexToLast(var4);
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean putAndMoveToFirst(int var1, boolean var2) {
      int var3;
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         int[] var5 = this.key;
         int var4;
         if ((var4 = var5[var3 = HashCommon.mix(var1) & this.mask]) != 0) {
            if (var4 == var1) {
               this.moveIndexToFirst(var3);
               return this.setValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != 0) {
               if (var4 == var1) {
                  this.moveIndexToFirst(var3);
                  return this.setValue(var3, var2);
               }
            }
         }
      }

      this.key[var3] = var1;
      this.value[var3] = var2;
      if (this.size == 0) {
         this.first = this.last = var3;
         this.link[var3] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.first;
         var10000[var10001] ^= (this.link[this.first] ^ ((long)var3 & 4294967295L) << 32) & -4294967296L;
         this.link[var3] = -4294967296L | (long)this.first & 4294967295L;
         this.first = var3;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public boolean putAndMoveToLast(int var1, boolean var2) {
      int var3;
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         int[] var5 = this.key;
         int var4;
         if ((var4 = var5[var3 = HashCommon.mix(var1) & this.mask]) != 0) {
            if (var4 == var1) {
               this.moveIndexToLast(var3);
               return this.setValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != 0) {
               if (var4 == var1) {
                  this.moveIndexToLast(var3);
                  return this.setValue(var3, var2);
               }
            }
         }
      }

      this.key[var3] = var1;
      this.value[var3] = var2;
      if (this.size == 0) {
         this.first = this.last = var3;
         this.link[var3] = -1L;
      } else {
         long[] var10000 = this.link;
         int var10001 = this.last;
         var10000[var10001] ^= (this.link[this.last] ^ (long)var3 & 4294967295L) & 4294967295L;
         this.link[var3] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
         this.last = var3;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public boolean get(int var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         int[] var3 = this.key;
         int var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (var1 == var2) {
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(int var1) {
      if (var1 == 0) {
         return this.containsNullKey;
      } else {
         int[] var3 = this.key;
         int var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(var1) & this.mask]) == 0) {
            return false;
         } else if (var1 == var2) {
            return true;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != 0) {
               if (var1 == var2) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(boolean var1) {
      boolean[] var2 = this.value;
      int[] var3 = this.key;
      if (this.containsNullKey && var2[this.n] == var1) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(var3[var4] == 0 || var2[var4] != var1);

         return true;
      }
   }

   public boolean getOrDefault(int var1, boolean var2) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         int[] var4 = this.key;
         int var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(var1) & this.mask]) == 0) {
            return var2;
         } else if (var1 == var3) {
            return this.value[var5];
         } else {
            while((var3 = var4[var5 = var5 + 1 & this.mask]) != 0) {
               if (var1 == var3) {
                  return this.value[var5];
               }
            }

            return var2;
         }
      }
   }

   public boolean putIfAbsent(int var1, boolean var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(int var1, boolean var2) {
      if (var1 == 0) {
         if (this.containsNullKey && var2 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         int[] var4 = this.key;
         int var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(var1) & this.mask]) == 0) {
            return false;
         } else if (var1 == var3 && var2 == this.value[var5]) {
            this.removeEntry(var5);
            return true;
         } else {
            do {
               if ((var3 = var4[var5 = var5 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(var1 != var3 || var2 != this.value[var5]);

            this.removeEntry(var5);
            return true;
         }
      }
   }

   public boolean replace(int var1, boolean var2, boolean var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public boolean replace(int var1, boolean var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         boolean var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public boolean computeIfAbsent(int var1, IntPredicate var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         boolean var4 = var2.test(var1);
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public boolean computeIfAbsentNullable(int var1, IntFunction<? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Boolean var4 = (Boolean)var2.apply(var1);
         if (var4 == null) {
            return this.defRetValue;
         } else {
            boolean var5 = var4;
            this.insert(-var3 - 1, var1, var5);
            return var5;
         }
      }
   }

   public boolean computeIfPresent(int var1, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Boolean var4 = (Boolean)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (var1 == 0) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }

            return this.defRetValue;
         } else {
            return this.value[var3] = var4;
         }
      }
   }

   public boolean compute(int var1, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Boolean var4 = (Boolean)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (var1 == 0) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }
         }

         return this.defRetValue;
      } else {
         boolean var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public boolean merge(int var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      } else {
         Boolean var5 = (Boolean)var3.apply(this.value[var4], var2);
         if (var5 == null) {
            if (var1 == 0) {
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

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, 0);
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

   public int firstIntKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public int lastIntKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Int2BooleanSortedMap tailMap(int var1) {
      throw new UnsupportedOperationException();
   }

   public Int2BooleanSortedMap headMap(int var1) {
      throw new UnsupportedOperationException();
   }

   public Int2BooleanSortedMap subMap(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   public IntComparator comparator() {
      return null;
   }

   public Int2BooleanSortedMap.FastSortedEntrySet int2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2BooleanLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public IntSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2BooleanLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new AbstractBooleanCollection() {
            public BooleanIterator iterator() {
               return Int2BooleanLinkedOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Int2BooleanLinkedOpenHashMap.this.size;
            }

            public boolean contains(boolean var1) {
               return Int2BooleanLinkedOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Int2BooleanLinkedOpenHashMap.this.clear();
            }

            public void forEach(BooleanConsumer var1) {
               if (Int2BooleanLinkedOpenHashMap.this.containsNullKey) {
                  var1.accept(Int2BooleanLinkedOpenHashMap.this.value[Int2BooleanLinkedOpenHashMap.this.n]);
               }

               int var2 = Int2BooleanLinkedOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Int2BooleanLinkedOpenHashMap.this.key[var2] != 0) {
                     var1.accept(Int2BooleanLinkedOpenHashMap.this.value[var2]);
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
      int[] var2 = this.key;
      boolean[] var3 = this.value;
      int var4 = var1 - 1;
      int[] var5 = new int[var1 + 1];
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
         if (var2[var7] == 0) {
            var11 = var1;
         } else {
            for(var11 = HashCommon.mix(var2[var7]) & var4; var5[var11] != 0; var11 = var11 + 1 & var4) {
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

   public Int2BooleanLinkedOpenHashMap clone() {
      Int2BooleanLinkedOpenHashMap var1;
      try {
         var1 = (Int2BooleanLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (int[])this.key.clone();
      var1.value = (boolean[])this.value.clone();
      var1.link = (long[])this.link.clone();
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(boolean var4 = false; var2-- != 0; ++var3) {
         while(this.key[var3] == 0) {
            ++var3;
         }

         int var5 = this.key[var3];
         var5 ^= this.value[var3] ? 1231 : 1237;
         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] ? 1231 : 1237;
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      int[] var2 = this.key;
      boolean[] var3 = this.value;
      Int2BooleanLinkedOpenHashMap.MapIterator var4 = new Int2BooleanLinkedOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeInt(var2[var6]);
         var1.writeBoolean(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      int[] var2 = this.key = new int[this.n + 1];
      boolean[] var3 = this.value = new boolean[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var8 = this.size;

      while(var8-- != 0) {
         int var6 = var1.readInt();
         boolean var7 = var1.readBoolean();
         int var9;
         if (var6 == 0) {
            var9 = this.n;
            this.containsNullKey = true;
         } else {
            for(var9 = HashCommon.mix(var6) & this.mask; var2[var9] != 0; var9 = var9 + 1 & this.mask) {
            }
         }

         var2[var9] = var6;
         var3[var9] = var7;
         if (this.first != -1) {
            var4[var5] ^= (var4[var5] ^ (long)var9 & 4294967295L) & 4294967295L;
            var4[var9] ^= (var4[var9] ^ ((long)var5 & 4294967295L) << 32) & -4294967296L;
            var5 = var9;
         } else {
            var5 = this.first = var9;
            var4[var9] |= -4294967296L;
         }
      }

      this.last = var5;
      if (var5 != -1) {
         var4[var5] |= 4294967295L;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Int2BooleanLinkedOpenHashMap.MapIterator implements BooleanListIterator {
      public boolean previousBoolean() {
         return Int2BooleanLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public boolean nextBoolean() {
         return Int2BooleanLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractIntSortedSet {
      private KeySet() {
         super();
      }

      public IntListIterator iterator(int var1) {
         return Int2BooleanLinkedOpenHashMap.this.new KeyIterator(var1);
      }

      public IntListIterator iterator() {
         return Int2BooleanLinkedOpenHashMap.this.new KeyIterator();
      }

      public void forEach(java.util.function.IntConsumer var1) {
         if (Int2BooleanLinkedOpenHashMap.this.containsNullKey) {
            var1.accept(Int2BooleanLinkedOpenHashMap.this.key[Int2BooleanLinkedOpenHashMap.this.n]);
         }

         int var2 = Int2BooleanLinkedOpenHashMap.this.n;

         while(var2-- != 0) {
            int var3 = Int2BooleanLinkedOpenHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Int2BooleanLinkedOpenHashMap.this.size;
      }

      public boolean contains(int var1) {
         return Int2BooleanLinkedOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(int var1) {
         int var2 = Int2BooleanLinkedOpenHashMap.this.size;
         Int2BooleanLinkedOpenHashMap.this.remove(var1);
         return Int2BooleanLinkedOpenHashMap.this.size != var2;
      }

      public void clear() {
         Int2BooleanLinkedOpenHashMap.this.clear();
      }

      public int firstInt() {
         if (Int2BooleanLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Int2BooleanLinkedOpenHashMap.this.key[Int2BooleanLinkedOpenHashMap.this.first];
         }
      }

      public int lastInt() {
         if (Int2BooleanLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Int2BooleanLinkedOpenHashMap.this.key[Int2BooleanLinkedOpenHashMap.this.last];
         }
      }

      public IntComparator comparator() {
         return null;
      }

      public IntSortedSet tailSet(int var1) {
         throw new UnsupportedOperationException();
      }

      public IntSortedSet headSet(int var1) {
         throw new UnsupportedOperationException();
      }

      public IntSortedSet subSet(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Int2BooleanLinkedOpenHashMap.MapIterator implements IntListIterator {
      public KeyIterator(int var2) {
         super(var2, null);
      }

      public int previousInt() {
         return Int2BooleanLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public int nextInt() {
         return Int2BooleanLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Int2BooleanMap.Entry> implements Int2BooleanSortedMap.FastSortedEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Int2BooleanMap.Entry> iterator() {
         return Int2BooleanLinkedOpenHashMap.this.new EntryIterator();
      }

      public Comparator<? super Int2BooleanMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Int2BooleanMap.Entry> subSet(Int2BooleanMap.Entry var1, Int2BooleanMap.Entry var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Int2BooleanMap.Entry> headSet(Int2BooleanMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Int2BooleanMap.Entry> tailSet(Int2BooleanMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public Int2BooleanMap.Entry first() {
         if (Int2BooleanLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Int2BooleanLinkedOpenHashMap.this.new MapEntry(Int2BooleanLinkedOpenHashMap.this.first);
         }
      }

      public Int2BooleanMap.Entry last() {
         if (Int2BooleanLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Int2BooleanLinkedOpenHashMap.this.new MapEntry(Int2BooleanLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Integer) {
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  int var3 = (Integer)var2.getKey();
                  boolean var4 = (Boolean)var2.getValue();
                  if (var3 == 0) {
                     return Int2BooleanLinkedOpenHashMap.this.containsNullKey && Int2BooleanLinkedOpenHashMap.this.value[Int2BooleanLinkedOpenHashMap.this.n] == var4;
                  } else {
                     int[] var6 = Int2BooleanLinkedOpenHashMap.this.key;
                     int var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(var3) & Int2BooleanLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var3 == var5) {
                        return Int2BooleanLinkedOpenHashMap.this.value[var7] == var4;
                     } else {
                        while((var5 = var6[var7 = var7 + 1 & Int2BooleanLinkedOpenHashMap.this.mask]) != 0) {
                           if (var3 == var5) {
                              return Int2BooleanLinkedOpenHashMap.this.value[var7] == var4;
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
            if (var2.getKey() != null && var2.getKey() instanceof Integer) {
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  int var3 = (Integer)var2.getKey();
                  boolean var4 = (Boolean)var2.getValue();
                  if (var3 == 0) {
                     if (Int2BooleanLinkedOpenHashMap.this.containsNullKey && Int2BooleanLinkedOpenHashMap.this.value[Int2BooleanLinkedOpenHashMap.this.n] == var4) {
                        Int2BooleanLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     int[] var6 = Int2BooleanLinkedOpenHashMap.this.key;
                     int var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(var3) & Int2BooleanLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var5 == var3) {
                        if (Int2BooleanLinkedOpenHashMap.this.value[var7] == var4) {
                           Int2BooleanLinkedOpenHashMap.this.removeEntry(var7);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var5 = var6[var7 = var7 + 1 & Int2BooleanLinkedOpenHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(var5 != var3 || Int2BooleanLinkedOpenHashMap.this.value[var7] != var4);

                        Int2BooleanLinkedOpenHashMap.this.removeEntry(var7);
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
         return Int2BooleanLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Int2BooleanLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Int2BooleanMap.Entry> iterator(Int2BooleanMap.Entry var1) {
         return Int2BooleanLinkedOpenHashMap.this.new EntryIterator(var1.getIntKey());
      }

      public ObjectListIterator<Int2BooleanMap.Entry> fastIterator() {
         return Int2BooleanLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Int2BooleanMap.Entry> fastIterator(Int2BooleanMap.Entry var1) {
         return Int2BooleanLinkedOpenHashMap.this.new FastEntryIterator(var1.getIntKey());
      }

      public void forEach(Consumer<? super Int2BooleanMap.Entry> var1) {
         int var2 = Int2BooleanLinkedOpenHashMap.this.size;
         int var4 = Int2BooleanLinkedOpenHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Int2BooleanLinkedOpenHashMap.this.link[var4];
            var1.accept(new AbstractInt2BooleanMap.BasicEntry(Int2BooleanLinkedOpenHashMap.this.key[var3], Int2BooleanLinkedOpenHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Int2BooleanMap.Entry> var1) {
         AbstractInt2BooleanMap.BasicEntry var2 = new AbstractInt2BooleanMap.BasicEntry();
         int var3 = Int2BooleanLinkedOpenHashMap.this.size;
         int var5 = Int2BooleanLinkedOpenHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Int2BooleanLinkedOpenHashMap.this.link[var5];
            var2.key = Int2BooleanLinkedOpenHashMap.this.key[var4];
            var2.value = Int2BooleanLinkedOpenHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Int2BooleanLinkedOpenHashMap.MapIterator implements ObjectListIterator<Int2BooleanMap.Entry> {
      final Int2BooleanLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Int2BooleanLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(int var2) {
         super(var2, null);
         this.entry = Int2BooleanLinkedOpenHashMap.this.new MapEntry();
      }

      public Int2BooleanLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Int2BooleanLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Int2BooleanLinkedOpenHashMap.MapIterator implements ObjectListIterator<Int2BooleanMap.Entry> {
      private Int2BooleanLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(int var2) {
         super(var2, null);
      }

      public Int2BooleanLinkedOpenHashMap.MapEntry next() {
         return this.entry = Int2BooleanLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Int2BooleanLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Int2BooleanLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
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
         this.next = Int2BooleanLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(int var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (var2 == 0) {
            if (Int2BooleanLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Int2BooleanLinkedOpenHashMap.this.link[Int2BooleanLinkedOpenHashMap.this.n];
               this.prev = Int2BooleanLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Int2BooleanLinkedOpenHashMap.this.key[Int2BooleanLinkedOpenHashMap.this.last] == var2) {
            this.prev = Int2BooleanLinkedOpenHashMap.this.last;
            this.index = Int2BooleanLinkedOpenHashMap.this.size;
         } else {
            for(int var3 = HashCommon.mix(var2) & Int2BooleanLinkedOpenHashMap.this.mask; Int2BooleanLinkedOpenHashMap.this.key[var3] != 0; var3 = var3 + 1 & Int2BooleanLinkedOpenHashMap.this.mask) {
               if (Int2BooleanLinkedOpenHashMap.this.key[var3] == var2) {
                  this.next = (int)Int2BooleanLinkedOpenHashMap.this.link[var3];
                  this.prev = var3;
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
               this.index = Int2BooleanLinkedOpenHashMap.this.size;
            } else {
               int var1 = Int2BooleanLinkedOpenHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Int2BooleanLinkedOpenHashMap.this.link[var1];
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
            this.next = (int)Int2BooleanLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Int2BooleanLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
               this.prev = (int)(Int2BooleanLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Int2BooleanLinkedOpenHashMap.this.link[this.curr];
            }

            --Int2BooleanLinkedOpenHashMap.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               Int2BooleanLinkedOpenHashMap.this.first = this.next;
            } else {
               var6 = Int2BooleanLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (Int2BooleanLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Int2BooleanLinkedOpenHashMap.this.last = this.prev;
            } else {
               var6 = Int2BooleanLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var6[var10001] ^= (Int2BooleanLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Int2BooleanLinkedOpenHashMap.this.n) {
               Int2BooleanLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               int[] var5 = Int2BooleanLinkedOpenHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Int2BooleanLinkedOpenHashMap.this.mask;

                  int var4;
                  while(true) {
                     if ((var4 = var5[var3]) == 0) {
                        var5[var1] = 0;
                        return;
                     }

                     int var2 = HashCommon.mix(var4) & Int2BooleanLinkedOpenHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Int2BooleanLinkedOpenHashMap.this.mask;
                  }

                  var5[var1] = var4;
                  Int2BooleanLinkedOpenHashMap.this.value[var1] = Int2BooleanLinkedOpenHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Int2BooleanLinkedOpenHashMap.this.fixPointers(var3, var1);
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

      public void set(Int2BooleanMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Int2BooleanMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(int var2, Object var3) {
         this(var2);
      }
   }

   final class MapEntry implements Int2BooleanMap.Entry, java.util.Map.Entry<Integer, Boolean> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public int getIntKey() {
         return Int2BooleanLinkedOpenHashMap.this.key[this.index];
      }

      public boolean getBooleanValue() {
         return Int2BooleanLinkedOpenHashMap.this.value[this.index];
      }

      public boolean setValue(boolean var1) {
         boolean var2 = Int2BooleanLinkedOpenHashMap.this.value[this.index];
         Int2BooleanLinkedOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Integer getKey() {
         return Int2BooleanLinkedOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Boolean getValue() {
         return Int2BooleanLinkedOpenHashMap.this.value[this.index];
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
            return Int2BooleanLinkedOpenHashMap.this.key[this.index] == (Integer)var2.getKey() && Int2BooleanLinkedOpenHashMap.this.value[this.index] == (Boolean)var2.getValue();
         }
      }

      public int hashCode() {
         return Int2BooleanLinkedOpenHashMap.this.key[this.index] ^ (Int2BooleanLinkedOpenHashMap.this.value[this.index] ? 1231 : 1237);
      }

      public String toString() {
         return Int2BooleanLinkedOpenHashMap.this.key[this.index] + "=>" + Int2BooleanLinkedOpenHashMap.this.value[this.index];
      }
   }
}
