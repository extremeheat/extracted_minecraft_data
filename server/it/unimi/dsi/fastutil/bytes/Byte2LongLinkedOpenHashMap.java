package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
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
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToLongFunction;
import java.util.function.LongConsumer;

public class Byte2LongLinkedOpenHashMap extends AbstractByte2LongSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient byte[] key;
   protected transient long[] value;
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
   protected transient Byte2LongSortedMap.FastSortedEntrySet entries;
   protected transient ByteSortedSet keys;
   protected transient LongCollection values;

   public Byte2LongLinkedOpenHashMap(int var1, float var2) {
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
            this.key = new byte[this.n + 1];
            this.value = new long[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Byte2LongLinkedOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Byte2LongLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Byte2LongLinkedOpenHashMap(Map<? extends Byte, ? extends Long> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Byte2LongLinkedOpenHashMap(Map<? extends Byte, ? extends Long> var1) {
      this(var1, 0.75F);
   }

   public Byte2LongLinkedOpenHashMap(Byte2LongMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Byte2LongLinkedOpenHashMap(Byte2LongMap var1) {
      this(var1, 0.75F);
   }

   public Byte2LongLinkedOpenHashMap(byte[] var1, long[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Byte2LongLinkedOpenHashMap(byte[] var1, long[] var2) {
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

   private long removeEntry(int var1) {
      long var2 = this.value[var1];
      --this.size;
      this.fixPointers(var1);
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private long removeNullEntry() {
      this.containsNullKey = false;
      long var1 = this.value[this.n];
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Byte, ? extends Long> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(byte var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         byte[] var3 = this.key;
         byte var2;
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

   private void insert(int var1, byte var2, long var3) {
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

   public long put(byte var1, long var2) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return this.defRetValue;
      } else {
         long var5 = this.value[var4];
         this.value[var4] = var2;
         return var5;
      }
   }

   private long addToValue(int var1, long var2) {
      long var4 = this.value[var1];
      this.value[var1] = var4 + var2;
      return var4;
   }

   public long addTo(byte var1, long var2) {
      int var4;
      if (var1 == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var4 = this.n;
         this.containsNullKey = true;
      } else {
         byte[] var6 = this.key;
         byte var5;
         if ((var5 = var6[var4 = HashCommon.mix(var1) & this.mask]) != 0) {
            if (var5 == var1) {
               return this.addToValue(var4, var2);
            }

            while((var5 = var6[var4 = var4 + 1 & this.mask]) != 0) {
               if (var5 == var1) {
                  return this.addToValue(var4, var2);
               }
            }
         }
      }

      this.key[var4] = var1;
      this.value[var4] = this.defRetValue + var2;
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
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return this.defRetValue;
   }

   protected final void shiftKeys(int var1) {
      byte[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         byte var4;
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

   public long remove(byte var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         byte[] var3 = this.key;
         byte var2;
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

   private long setValue(int var1, long var2) {
      long var4 = this.value[var1];
      this.value[var1] = var2;
      return var4;
   }

   public long removeFirstLong() {
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
         long var2 = this.value[var1];
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

   public long removeLastLong() {
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
         long var2 = this.value[var1];
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

   public long getAndMoveToFirst(byte var1) {
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         byte[] var3 = this.key;
         byte var2;
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

   public long getAndMoveToLast(byte var1) {
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         byte[] var3 = this.key;
         byte var2;
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

   public long putAndMoveToFirst(byte var1, long var2) {
      int var4;
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var4 = this.n;
      } else {
         byte[] var6 = this.key;
         byte var5;
         if ((var5 = var6[var4 = HashCommon.mix(var1) & this.mask]) != 0) {
            if (var5 == var1) {
               this.moveIndexToFirst(var4);
               return this.setValue(var4, var2);
            }

            while((var5 = var6[var4 = var4 + 1 & this.mask]) != 0) {
               if (var5 == var1) {
                  this.moveIndexToFirst(var4);
                  return this.setValue(var4, var2);
               }
            }
         }
      }

      this.key[var4] = var1;
      this.value[var4] = var2;
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

   public long putAndMoveToLast(byte var1, long var2) {
      int var4;
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var4 = this.n;
      } else {
         byte[] var6 = this.key;
         byte var5;
         if ((var5 = var6[var4 = HashCommon.mix(var1) & this.mask]) != 0) {
            if (var5 == var1) {
               this.moveIndexToLast(var4);
               return this.setValue(var4, var2);
            }

            while((var5 = var6[var4 = var4 + 1 & this.mask]) != 0) {
               if (var5 == var1) {
                  this.moveIndexToLast(var4);
                  return this.setValue(var4, var2);
               }
            }
         }
      }

      this.key[var4] = var1;
      this.value[var4] = var2;
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

   public long get(byte var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         byte[] var3 = this.key;
         byte var2;
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

   public boolean containsKey(byte var1) {
      if (var1 == 0) {
         return this.containsNullKey;
      } else {
         byte[] var3 = this.key;
         byte var2;
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

   public boolean containsValue(long var1) {
      long[] var3 = this.value;
      byte[] var4 = this.key;
      if (this.containsNullKey && var3[this.n] == var1) {
         return true;
      } else {
         int var5 = this.n;

         do {
            if (var5-- == 0) {
               return false;
            }
         } while(var4[var5] == 0 || var3[var5] != var1);

         return true;
      }
   }

   public long getOrDefault(byte var1, long var2) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         byte[] var5 = this.key;
         byte var4;
         int var6;
         if ((var4 = var5[var6 = HashCommon.mix(var1) & this.mask]) == 0) {
            return var2;
         } else if (var1 == var4) {
            return this.value[var6];
         } else {
            while((var4 = var5[var6 = var6 + 1 & this.mask]) != 0) {
               if (var1 == var4) {
                  return this.value[var6];
               }
            }

            return var2;
         }
      }
   }

   public long putIfAbsent(byte var1, long var2) {
      int var4 = this.find(var1);
      if (var4 >= 0) {
         return this.value[var4];
      } else {
         this.insert(-var4 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(byte var1, long var2) {
      if (var1 == 0) {
         if (this.containsNullKey && var2 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         byte[] var5 = this.key;
         byte var4;
         int var6;
         if ((var4 = var5[var6 = HashCommon.mix(var1) & this.mask]) == 0) {
            return false;
         } else if (var1 == var4 && var2 == this.value[var6]) {
            this.removeEntry(var6);
            return true;
         } else {
            do {
               if ((var4 = var5[var6 = var6 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(var1 != var4 || var2 != this.value[var6]);

            this.removeEntry(var6);
            return true;
         }
      }
   }

   public boolean replace(byte var1, long var2, long var4) {
      int var6 = this.find(var1);
      if (var6 >= 0 && var2 == this.value[var6]) {
         this.value[var6] = var4;
         return true;
      } else {
         return false;
      }
   }

   public long replace(byte var1, long var2) {
      int var4 = this.find(var1);
      if (var4 < 0) {
         return this.defRetValue;
      } else {
         long var5 = this.value[var4];
         this.value[var4] = var2;
         return var5;
      }
   }

   public long computeIfAbsent(byte var1, IntToLongFunction var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         long var4 = var2.applyAsLong(var1);
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public long computeIfAbsentNullable(byte var1, IntFunction<? extends Long> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Long var4 = (Long)var2.apply(var1);
         if (var4 == null) {
            return this.defRetValue;
         } else {
            long var5 = var4;
            this.insert(-var3 - 1, var1, var5);
            return var5;
         }
      }
   }

   public long computeIfPresent(byte var1, BiFunction<? super Byte, ? super Long, ? extends Long> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Long var4 = (Long)var2.apply(var1, this.value[var3]);
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

   public long compute(byte var1, BiFunction<? super Byte, ? super Long, ? extends Long> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Long var4 = (Long)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
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
         long var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public long merge(byte var1, long var2, BiFunction<? super Long, ? super Long, ? extends Long> var4) {
      Objects.requireNonNull(var4);
      int var5 = this.find(var1);
      if (var5 < 0) {
         this.insert(-var5 - 1, var1, var2);
         return var2;
      } else {
         Long var6 = (Long)var4.apply(this.value[var5], var2);
         if (var6 == null) {
            if (var1 == 0) {
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
         Arrays.fill(this.key, (byte)0);
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

   public byte firstByteKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public byte lastByteKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Byte2LongSortedMap tailMap(byte var1) {
      throw new UnsupportedOperationException();
   }

   public Byte2LongSortedMap headMap(byte var1) {
      throw new UnsupportedOperationException();
   }

   public Byte2LongSortedMap subMap(byte var1, byte var2) {
      throw new UnsupportedOperationException();
   }

   public ByteComparator comparator() {
      return null;
   }

   public Byte2LongSortedMap.FastSortedEntrySet byte2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Byte2LongLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ByteSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2LongLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            public LongIterator iterator() {
               return Byte2LongLinkedOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Byte2LongLinkedOpenHashMap.this.size;
            }

            public boolean contains(long var1) {
               return Byte2LongLinkedOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Byte2LongLinkedOpenHashMap.this.clear();
            }

            public void forEach(LongConsumer var1) {
               if (Byte2LongLinkedOpenHashMap.this.containsNullKey) {
                  var1.accept(Byte2LongLinkedOpenHashMap.this.value[Byte2LongLinkedOpenHashMap.this.n]);
               }

               int var2 = Byte2LongLinkedOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Byte2LongLinkedOpenHashMap.this.key[var2] != 0) {
                     var1.accept(Byte2LongLinkedOpenHashMap.this.value[var2]);
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
      byte[] var2 = this.key;
      long[] var3 = this.value;
      int var4 = var1 - 1;
      byte[] var5 = new byte[var1 + 1];
      long[] var6 = new long[var1 + 1];
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

   public Byte2LongLinkedOpenHashMap clone() {
      Byte2LongLinkedOpenHashMap var1;
      try {
         var1 = (Byte2LongLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (byte[])this.key.clone();
      var1.value = (long[])this.value.clone();
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

         byte var5 = this.key[var3];
         int var6 = var5 ^ HashCommon.long2int(this.value[var3]);
         var1 += var6;
      }

      if (this.containsNullKey) {
         var1 += HashCommon.long2int(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      byte[] var2 = this.key;
      long[] var3 = this.value;
      Byte2LongLinkedOpenHashMap.MapIterator var4 = new Byte2LongLinkedOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeByte(var2[var6]);
         var1.writeLong(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      byte[] var2 = this.key = new byte[this.n + 1];
      long[] var3 = this.value = new long[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var9 = this.size;

      while(var9-- != 0) {
         byte var6 = var1.readByte();
         long var7 = var1.readLong();
         int var10;
         if (var6 == 0) {
            var10 = this.n;
            this.containsNullKey = true;
         } else {
            for(var10 = HashCommon.mix(var6) & this.mask; var2[var10] != 0; var10 = var10 + 1 & this.mask) {
            }
         }

         var2[var10] = var6;
         var3[var10] = var7;
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

   private final class ValueIterator extends Byte2LongLinkedOpenHashMap.MapIterator implements LongListIterator {
      public long previousLong() {
         return Byte2LongLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public long nextLong() {
         return Byte2LongLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractByteSortedSet {
      private KeySet() {
         super();
      }

      public ByteListIterator iterator(byte var1) {
         return Byte2LongLinkedOpenHashMap.this.new KeyIterator(var1);
      }

      public ByteListIterator iterator() {
         return Byte2LongLinkedOpenHashMap.this.new KeyIterator();
      }

      public void forEach(IntConsumer var1) {
         if (Byte2LongLinkedOpenHashMap.this.containsNullKey) {
            var1.accept(Byte2LongLinkedOpenHashMap.this.key[Byte2LongLinkedOpenHashMap.this.n]);
         }

         int var2 = Byte2LongLinkedOpenHashMap.this.n;

         while(var2-- != 0) {
            byte var3 = Byte2LongLinkedOpenHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Byte2LongLinkedOpenHashMap.this.size;
      }

      public boolean contains(byte var1) {
         return Byte2LongLinkedOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(byte var1) {
         int var2 = Byte2LongLinkedOpenHashMap.this.size;
         Byte2LongLinkedOpenHashMap.this.remove(var1);
         return Byte2LongLinkedOpenHashMap.this.size != var2;
      }

      public void clear() {
         Byte2LongLinkedOpenHashMap.this.clear();
      }

      public byte firstByte() {
         if (Byte2LongLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Byte2LongLinkedOpenHashMap.this.key[Byte2LongLinkedOpenHashMap.this.first];
         }
      }

      public byte lastByte() {
         if (Byte2LongLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Byte2LongLinkedOpenHashMap.this.key[Byte2LongLinkedOpenHashMap.this.last];
         }
      }

      public ByteComparator comparator() {
         return null;
      }

      public ByteSortedSet tailSet(byte var1) {
         throw new UnsupportedOperationException();
      }

      public ByteSortedSet headSet(byte var1) {
         throw new UnsupportedOperationException();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Byte2LongLinkedOpenHashMap.MapIterator implements ByteListIterator {
      public KeyIterator(byte var2) {
         super(var2, null);
      }

      public byte previousByte() {
         return Byte2LongLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public byte nextByte() {
         return Byte2LongLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Byte2LongMap.Entry> implements Byte2LongSortedMap.FastSortedEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Byte2LongMap.Entry> iterator() {
         return Byte2LongLinkedOpenHashMap.this.new EntryIterator();
      }

      public Comparator<? super Byte2LongMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Byte2LongMap.Entry> subSet(Byte2LongMap.Entry var1, Byte2LongMap.Entry var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Byte2LongMap.Entry> headSet(Byte2LongMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Byte2LongMap.Entry> tailSet(Byte2LongMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public Byte2LongMap.Entry first() {
         if (Byte2LongLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Byte2LongLinkedOpenHashMap.this.new MapEntry(Byte2LongLinkedOpenHashMap.this.first);
         }
      }

      public Byte2LongMap.Entry last() {
         if (Byte2LongLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Byte2LongLinkedOpenHashMap.this.new MapEntry(Byte2LongLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Byte) {
               if (var2.getValue() != null && var2.getValue() instanceof Long) {
                  byte var3 = (Byte)var2.getKey();
                  long var4 = (Long)var2.getValue();
                  if (var3 == 0) {
                     return Byte2LongLinkedOpenHashMap.this.containsNullKey && Byte2LongLinkedOpenHashMap.this.value[Byte2LongLinkedOpenHashMap.this.n] == var4;
                  } else {
                     byte[] var7 = Byte2LongLinkedOpenHashMap.this.key;
                     byte var6;
                     int var8;
                     if ((var6 = var7[var8 = HashCommon.mix(var3) & Byte2LongLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var3 == var6) {
                        return Byte2LongLinkedOpenHashMap.this.value[var8] == var4;
                     } else {
                        while((var6 = var7[var8 = var8 + 1 & Byte2LongLinkedOpenHashMap.this.mask]) != 0) {
                           if (var3 == var6) {
                              return Byte2LongLinkedOpenHashMap.this.value[var8] == var4;
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
            if (var2.getKey() != null && var2.getKey() instanceof Byte) {
               if (var2.getValue() != null && var2.getValue() instanceof Long) {
                  byte var3 = (Byte)var2.getKey();
                  long var4 = (Long)var2.getValue();
                  if (var3 == 0) {
                     if (Byte2LongLinkedOpenHashMap.this.containsNullKey && Byte2LongLinkedOpenHashMap.this.value[Byte2LongLinkedOpenHashMap.this.n] == var4) {
                        Byte2LongLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     byte[] var7 = Byte2LongLinkedOpenHashMap.this.key;
                     byte var6;
                     int var8;
                     if ((var6 = var7[var8 = HashCommon.mix(var3) & Byte2LongLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var6 == var3) {
                        if (Byte2LongLinkedOpenHashMap.this.value[var8] == var4) {
                           Byte2LongLinkedOpenHashMap.this.removeEntry(var8);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var6 = var7[var8 = var8 + 1 & Byte2LongLinkedOpenHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(var6 != var3 || Byte2LongLinkedOpenHashMap.this.value[var8] != var4);

                        Byte2LongLinkedOpenHashMap.this.removeEntry(var8);
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
         return Byte2LongLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Byte2LongLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Byte2LongMap.Entry> iterator(Byte2LongMap.Entry var1) {
         return Byte2LongLinkedOpenHashMap.this.new EntryIterator(var1.getByteKey());
      }

      public ObjectListIterator<Byte2LongMap.Entry> fastIterator() {
         return Byte2LongLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Byte2LongMap.Entry> fastIterator(Byte2LongMap.Entry var1) {
         return Byte2LongLinkedOpenHashMap.this.new FastEntryIterator(var1.getByteKey());
      }

      public void forEach(Consumer<? super Byte2LongMap.Entry> var1) {
         int var2 = Byte2LongLinkedOpenHashMap.this.size;
         int var4 = Byte2LongLinkedOpenHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Byte2LongLinkedOpenHashMap.this.link[var4];
            var1.accept(new AbstractByte2LongMap.BasicEntry(Byte2LongLinkedOpenHashMap.this.key[var3], Byte2LongLinkedOpenHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Byte2LongMap.Entry> var1) {
         AbstractByte2LongMap.BasicEntry var2 = new AbstractByte2LongMap.BasicEntry();
         int var3 = Byte2LongLinkedOpenHashMap.this.size;
         int var5 = Byte2LongLinkedOpenHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Byte2LongLinkedOpenHashMap.this.link[var5];
            var2.key = Byte2LongLinkedOpenHashMap.this.key[var4];
            var2.value = Byte2LongLinkedOpenHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Byte2LongLinkedOpenHashMap.MapIterator implements ObjectListIterator<Byte2LongMap.Entry> {
      final Byte2LongLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Byte2LongLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(byte var2) {
         super(var2, null);
         this.entry = Byte2LongLinkedOpenHashMap.this.new MapEntry();
      }

      public Byte2LongLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Byte2LongLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Byte2LongLinkedOpenHashMap.MapIterator implements ObjectListIterator<Byte2LongMap.Entry> {
      private Byte2LongLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(byte var2) {
         super(var2, null);
      }

      public Byte2LongLinkedOpenHashMap.MapEntry next() {
         return this.entry = Byte2LongLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Byte2LongLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Byte2LongLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
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
         this.next = Byte2LongLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(byte var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (var2 == 0) {
            if (Byte2LongLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Byte2LongLinkedOpenHashMap.this.link[Byte2LongLinkedOpenHashMap.this.n];
               this.prev = Byte2LongLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Byte2LongLinkedOpenHashMap.this.key[Byte2LongLinkedOpenHashMap.this.last] == var2) {
            this.prev = Byte2LongLinkedOpenHashMap.this.last;
            this.index = Byte2LongLinkedOpenHashMap.this.size;
         } else {
            for(int var3 = HashCommon.mix(var2) & Byte2LongLinkedOpenHashMap.this.mask; Byte2LongLinkedOpenHashMap.this.key[var3] != 0; var3 = var3 + 1 & Byte2LongLinkedOpenHashMap.this.mask) {
               if (Byte2LongLinkedOpenHashMap.this.key[var3] == var2) {
                  this.next = (int)Byte2LongLinkedOpenHashMap.this.link[var3];
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
               this.index = Byte2LongLinkedOpenHashMap.this.size;
            } else {
               int var1 = Byte2LongLinkedOpenHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Byte2LongLinkedOpenHashMap.this.link[var1];
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
            this.next = (int)Byte2LongLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Byte2LongLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
               this.prev = (int)(Byte2LongLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Byte2LongLinkedOpenHashMap.this.link[this.curr];
            }

            --Byte2LongLinkedOpenHashMap.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               Byte2LongLinkedOpenHashMap.this.first = this.next;
            } else {
               var6 = Byte2LongLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (Byte2LongLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Byte2LongLinkedOpenHashMap.this.last = this.prev;
            } else {
               var6 = Byte2LongLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var6[var10001] ^= (Byte2LongLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Byte2LongLinkedOpenHashMap.this.n) {
               Byte2LongLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               byte[] var5 = Byte2LongLinkedOpenHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Byte2LongLinkedOpenHashMap.this.mask;

                  byte var4;
                  while(true) {
                     if ((var4 = var5[var3]) == 0) {
                        var5[var1] = 0;
                        return;
                     }

                     int var2 = HashCommon.mix(var4) & Byte2LongLinkedOpenHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Byte2LongLinkedOpenHashMap.this.mask;
                  }

                  var5[var1] = var4;
                  Byte2LongLinkedOpenHashMap.this.value[var1] = Byte2LongLinkedOpenHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Byte2LongLinkedOpenHashMap.this.fixPointers(var3, var1);
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

      public void set(Byte2LongMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Byte2LongMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(byte var2, Object var3) {
         this(var2);
      }
   }

   final class MapEntry implements Byte2LongMap.Entry, java.util.Map.Entry<Byte, Long> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public byte getByteKey() {
         return Byte2LongLinkedOpenHashMap.this.key[this.index];
      }

      public long getLongValue() {
         return Byte2LongLinkedOpenHashMap.this.value[this.index];
      }

      public long setValue(long var1) {
         long var3 = Byte2LongLinkedOpenHashMap.this.value[this.index];
         Byte2LongLinkedOpenHashMap.this.value[this.index] = var1;
         return var3;
      }

      /** @deprecated */
      @Deprecated
      public Byte getKey() {
         return Byte2LongLinkedOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Long getValue() {
         return Byte2LongLinkedOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Long setValue(Long var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Byte2LongLinkedOpenHashMap.this.key[this.index] == (Byte)var2.getKey() && Byte2LongLinkedOpenHashMap.this.value[this.index] == (Long)var2.getValue();
         }
      }

      public int hashCode() {
         return Byte2LongLinkedOpenHashMap.this.key[this.index] ^ HashCommon.long2int(Byte2LongLinkedOpenHashMap.this.value[this.index]);
      }

      public String toString() {
         return Byte2LongLinkedOpenHashMap.this.key[this.index] + "=>" + Byte2LongLinkedOpenHashMap.this.value[this.index];
      }
   }
}
