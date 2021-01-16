package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
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
import java.util.function.IntUnaryOperator;

public class Short2ByteLinkedOpenHashMap extends AbstractShort2ByteSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient short[] key;
   protected transient byte[] value;
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
   protected transient Short2ByteSortedMap.FastSortedEntrySet entries;
   protected transient ShortSortedSet keys;
   protected transient ByteCollection values;

   public Short2ByteLinkedOpenHashMap(int var1, float var2) {
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
            this.key = new short[this.n + 1];
            this.value = new byte[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Short2ByteLinkedOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Short2ByteLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Short2ByteLinkedOpenHashMap(Map<? extends Short, ? extends Byte> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Short2ByteLinkedOpenHashMap(Map<? extends Short, ? extends Byte> var1) {
      this(var1, 0.75F);
   }

   public Short2ByteLinkedOpenHashMap(Short2ByteMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Short2ByteLinkedOpenHashMap(Short2ByteMap var1) {
      this(var1, 0.75F);
   }

   public Short2ByteLinkedOpenHashMap(short[] var1, byte[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Short2ByteLinkedOpenHashMap(short[] var1, byte[] var2) {
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

   private byte removeEntry(int var1) {
      byte var2 = this.value[var1];
      --this.size;
      this.fixPointers(var1);
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private byte removeNullEntry() {
      this.containsNullKey = false;
      byte var1 = this.value[this.n];
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Short, ? extends Byte> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(short var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         short[] var3 = this.key;
         short var2;
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

   private void insert(int var1, short var2, byte var3) {
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

   public byte put(short var1, byte var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         byte var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   private byte addToValue(int var1, byte var2) {
      byte var3 = this.value[var1];
      this.value[var1] = (byte)(var3 + var2);
      return var3;
   }

   public byte addTo(short var1, byte var2) {
      int var3;
      if (var1 == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var3 = this.n;
         this.containsNullKey = true;
      } else {
         short[] var5 = this.key;
         short var4;
         if ((var4 = var5[var3 = HashCommon.mix(var1) & this.mask]) != 0) {
            if (var4 == var1) {
               return this.addToValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != 0) {
               if (var4 == var1) {
                  return this.addToValue(var3, var2);
               }
            }
         }
      }

      this.key[var3] = var1;
      this.value[var3] = (byte)(this.defRetValue + var2);
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
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return this.defRetValue;
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

   public byte remove(short var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         short[] var3 = this.key;
         short var2;
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

   private byte setValue(int var1, byte var2) {
      byte var3 = this.value[var1];
      this.value[var1] = var2;
      return var3;
   }

   public byte removeFirstByte() {
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
         byte var2 = this.value[var1];
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

   public byte removeLastByte() {
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
         byte var2 = this.value[var1];
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

   public byte getAndMoveToFirst(short var1) {
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         short[] var3 = this.key;
         short var2;
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

   public byte getAndMoveToLast(short var1) {
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         short[] var3 = this.key;
         short var2;
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

   public byte putAndMoveToFirst(short var1, byte var2) {
      int var3;
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         short[] var5 = this.key;
         short var4;
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

   public byte putAndMoveToLast(short var1, byte var2) {
      int var3;
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         short[] var5 = this.key;
         short var4;
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

   public byte get(short var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         short[] var3 = this.key;
         short var2;
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

   public boolean containsKey(short var1) {
      if (var1 == 0) {
         return this.containsNullKey;
      } else {
         short[] var3 = this.key;
         short var2;
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

   public boolean containsValue(byte var1) {
      byte[] var2 = this.value;
      short[] var3 = this.key;
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

   public byte getOrDefault(short var1, byte var2) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         short[] var4 = this.key;
         short var3;
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

   public byte putIfAbsent(short var1, byte var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(short var1, byte var2) {
      if (var1 == 0) {
         if (this.containsNullKey && var2 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         short[] var4 = this.key;
         short var3;
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

   public boolean replace(short var1, byte var2, byte var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public byte replace(short var1, byte var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         byte var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public byte computeIfAbsent(short var1, IntUnaryOperator var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         byte var4 = SafeMath.safeIntToByte(var2.applyAsInt(var1));
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public byte computeIfAbsentNullable(short var1, IntFunction<? extends Byte> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Byte var4 = (Byte)var2.apply(var1);
         if (var4 == null) {
            return this.defRetValue;
         } else {
            byte var5 = var4;
            this.insert(-var3 - 1, var1, var5);
            return var5;
         }
      }
   }

   public byte computeIfPresent(short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Byte var4 = (Byte)var2.apply(var1, this.value[var3]);
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

   public byte compute(short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Byte var4 = (Byte)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
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
         byte var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public byte merge(short var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      } else {
         Byte var5 = (Byte)var3.apply(this.value[var4], var2);
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
         Arrays.fill(this.key, (short)0);
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

   public short firstShortKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public short lastShortKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Short2ByteSortedMap tailMap(short var1) {
      throw new UnsupportedOperationException();
   }

   public Short2ByteSortedMap headMap(short var1) {
      throw new UnsupportedOperationException();
   }

   public Short2ByteSortedMap subMap(short var1, short var2) {
      throw new UnsupportedOperationException();
   }

   public ShortComparator comparator() {
      return null;
   }

   public Short2ByteSortedMap.FastSortedEntrySet short2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new Short2ByteLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ShortSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Short2ByteLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public ByteCollection values() {
      if (this.values == null) {
         this.values = new AbstractByteCollection() {
            public ByteIterator iterator() {
               return Short2ByteLinkedOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Short2ByteLinkedOpenHashMap.this.size;
            }

            public boolean contains(byte var1) {
               return Short2ByteLinkedOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Short2ByteLinkedOpenHashMap.this.clear();
            }

            public void forEach(IntConsumer var1) {
               if (Short2ByteLinkedOpenHashMap.this.containsNullKey) {
                  var1.accept(Short2ByteLinkedOpenHashMap.this.value[Short2ByteLinkedOpenHashMap.this.n]);
               }

               int var2 = Short2ByteLinkedOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Short2ByteLinkedOpenHashMap.this.key[var2] != 0) {
                     var1.accept(Short2ByteLinkedOpenHashMap.this.value[var2]);
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
      short[] var2 = this.key;
      byte[] var3 = this.value;
      int var4 = var1 - 1;
      short[] var5 = new short[var1 + 1];
      byte[] var6 = new byte[var1 + 1];
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

   public Short2ByteLinkedOpenHashMap clone() {
      Short2ByteLinkedOpenHashMap var1;
      try {
         var1 = (Short2ByteLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (short[])this.key.clone();
      var1.value = (byte[])this.value.clone();
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

         short var5 = this.key[var3];
         int var6 = var5 ^ this.value[var3];
         var1 += var6;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n];
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      short[] var2 = this.key;
      byte[] var3 = this.value;
      Short2ByteLinkedOpenHashMap.MapIterator var4 = new Short2ByteLinkedOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeShort(var2[var6]);
         var1.writeByte(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      short[] var2 = this.key = new short[this.n + 1];
      byte[] var3 = this.value = new byte[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var8 = this.size;

      while(var8-- != 0) {
         short var6 = var1.readShort();
         byte var7 = var1.readByte();
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

   private final class ValueIterator extends Short2ByteLinkedOpenHashMap.MapIterator implements ByteListIterator {
      public byte previousByte() {
         return Short2ByteLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public byte nextByte() {
         return Short2ByteLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractShortSortedSet {
      private KeySet() {
         super();
      }

      public ShortListIterator iterator(short var1) {
         return Short2ByteLinkedOpenHashMap.this.new KeyIterator(var1);
      }

      public ShortListIterator iterator() {
         return Short2ByteLinkedOpenHashMap.this.new KeyIterator();
      }

      public void forEach(IntConsumer var1) {
         if (Short2ByteLinkedOpenHashMap.this.containsNullKey) {
            var1.accept(Short2ByteLinkedOpenHashMap.this.key[Short2ByteLinkedOpenHashMap.this.n]);
         }

         int var2 = Short2ByteLinkedOpenHashMap.this.n;

         while(var2-- != 0) {
            short var3 = Short2ByteLinkedOpenHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Short2ByteLinkedOpenHashMap.this.size;
      }

      public boolean contains(short var1) {
         return Short2ByteLinkedOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(short var1) {
         int var2 = Short2ByteLinkedOpenHashMap.this.size;
         Short2ByteLinkedOpenHashMap.this.remove(var1);
         return Short2ByteLinkedOpenHashMap.this.size != var2;
      }

      public void clear() {
         Short2ByteLinkedOpenHashMap.this.clear();
      }

      public short firstShort() {
         if (Short2ByteLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Short2ByteLinkedOpenHashMap.this.key[Short2ByteLinkedOpenHashMap.this.first];
         }
      }

      public short lastShort() {
         if (Short2ByteLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Short2ByteLinkedOpenHashMap.this.key[Short2ByteLinkedOpenHashMap.this.last];
         }
      }

      public ShortComparator comparator() {
         return null;
      }

      public ShortSortedSet tailSet(short var1) {
         throw new UnsupportedOperationException();
      }

      public ShortSortedSet headSet(short var1) {
         throw new UnsupportedOperationException();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Short2ByteLinkedOpenHashMap.MapIterator implements ShortListIterator {
      public KeyIterator(short var2) {
         super(var2, null);
      }

      public short previousShort() {
         return Short2ByteLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public short nextShort() {
         return Short2ByteLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Short2ByteMap.Entry> implements Short2ByteSortedMap.FastSortedEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Short2ByteMap.Entry> iterator() {
         return Short2ByteLinkedOpenHashMap.this.new EntryIterator();
      }

      public Comparator<? super Short2ByteMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Short2ByteMap.Entry> subSet(Short2ByteMap.Entry var1, Short2ByteMap.Entry var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Short2ByteMap.Entry> headSet(Short2ByteMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Short2ByteMap.Entry> tailSet(Short2ByteMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public Short2ByteMap.Entry first() {
         if (Short2ByteLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Short2ByteLinkedOpenHashMap.this.new MapEntry(Short2ByteLinkedOpenHashMap.this.first);
         }
      }

      public Short2ByteMap.Entry last() {
         if (Short2ByteLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Short2ByteLinkedOpenHashMap.this.new MapEntry(Short2ByteLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Short) {
               if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                  short var3 = (Short)var2.getKey();
                  byte var4 = (Byte)var2.getValue();
                  if (var3 == 0) {
                     return Short2ByteLinkedOpenHashMap.this.containsNullKey && Short2ByteLinkedOpenHashMap.this.value[Short2ByteLinkedOpenHashMap.this.n] == var4;
                  } else {
                     short[] var6 = Short2ByteLinkedOpenHashMap.this.key;
                     short var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(var3) & Short2ByteLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var3 == var5) {
                        return Short2ByteLinkedOpenHashMap.this.value[var7] == var4;
                     } else {
                        while((var5 = var6[var7 = var7 + 1 & Short2ByteLinkedOpenHashMap.this.mask]) != 0) {
                           if (var3 == var5) {
                              return Short2ByteLinkedOpenHashMap.this.value[var7] == var4;
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
            if (var2.getKey() != null && var2.getKey() instanceof Short) {
               if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                  short var3 = (Short)var2.getKey();
                  byte var4 = (Byte)var2.getValue();
                  if (var3 == 0) {
                     if (Short2ByteLinkedOpenHashMap.this.containsNullKey && Short2ByteLinkedOpenHashMap.this.value[Short2ByteLinkedOpenHashMap.this.n] == var4) {
                        Short2ByteLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     short[] var6 = Short2ByteLinkedOpenHashMap.this.key;
                     short var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(var3) & Short2ByteLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var5 == var3) {
                        if (Short2ByteLinkedOpenHashMap.this.value[var7] == var4) {
                           Short2ByteLinkedOpenHashMap.this.removeEntry(var7);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var5 = var6[var7 = var7 + 1 & Short2ByteLinkedOpenHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(var5 != var3 || Short2ByteLinkedOpenHashMap.this.value[var7] != var4);

                        Short2ByteLinkedOpenHashMap.this.removeEntry(var7);
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
         return Short2ByteLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Short2ByteLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Short2ByteMap.Entry> iterator(Short2ByteMap.Entry var1) {
         return Short2ByteLinkedOpenHashMap.this.new EntryIterator(var1.getShortKey());
      }

      public ObjectListIterator<Short2ByteMap.Entry> fastIterator() {
         return Short2ByteLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Short2ByteMap.Entry> fastIterator(Short2ByteMap.Entry var1) {
         return Short2ByteLinkedOpenHashMap.this.new FastEntryIterator(var1.getShortKey());
      }

      public void forEach(Consumer<? super Short2ByteMap.Entry> var1) {
         int var2 = Short2ByteLinkedOpenHashMap.this.size;
         int var4 = Short2ByteLinkedOpenHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Short2ByteLinkedOpenHashMap.this.link[var4];
            var1.accept(new AbstractShort2ByteMap.BasicEntry(Short2ByteLinkedOpenHashMap.this.key[var3], Short2ByteLinkedOpenHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Short2ByteMap.Entry> var1) {
         AbstractShort2ByteMap.BasicEntry var2 = new AbstractShort2ByteMap.BasicEntry();
         int var3 = Short2ByteLinkedOpenHashMap.this.size;
         int var5 = Short2ByteLinkedOpenHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Short2ByteLinkedOpenHashMap.this.link[var5];
            var2.key = Short2ByteLinkedOpenHashMap.this.key[var4];
            var2.value = Short2ByteLinkedOpenHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Short2ByteLinkedOpenHashMap.MapIterator implements ObjectListIterator<Short2ByteMap.Entry> {
      final Short2ByteLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Short2ByteLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(short var2) {
         super(var2, null);
         this.entry = Short2ByteLinkedOpenHashMap.this.new MapEntry();
      }

      public Short2ByteLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Short2ByteLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Short2ByteLinkedOpenHashMap.MapIterator implements ObjectListIterator<Short2ByteMap.Entry> {
      private Short2ByteLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(short var2) {
         super(var2, null);
      }

      public Short2ByteLinkedOpenHashMap.MapEntry next() {
         return this.entry = Short2ByteLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Short2ByteLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Short2ByteLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
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
         this.next = Short2ByteLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(short var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (var2 == 0) {
            if (Short2ByteLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Short2ByteLinkedOpenHashMap.this.link[Short2ByteLinkedOpenHashMap.this.n];
               this.prev = Short2ByteLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Short2ByteLinkedOpenHashMap.this.key[Short2ByteLinkedOpenHashMap.this.last] == var2) {
            this.prev = Short2ByteLinkedOpenHashMap.this.last;
            this.index = Short2ByteLinkedOpenHashMap.this.size;
         } else {
            for(int var3 = HashCommon.mix(var2) & Short2ByteLinkedOpenHashMap.this.mask; Short2ByteLinkedOpenHashMap.this.key[var3] != 0; var3 = var3 + 1 & Short2ByteLinkedOpenHashMap.this.mask) {
               if (Short2ByteLinkedOpenHashMap.this.key[var3] == var2) {
                  this.next = (int)Short2ByteLinkedOpenHashMap.this.link[var3];
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
               this.index = Short2ByteLinkedOpenHashMap.this.size;
            } else {
               int var1 = Short2ByteLinkedOpenHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Short2ByteLinkedOpenHashMap.this.link[var1];
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
            this.next = (int)Short2ByteLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Short2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
               this.prev = (int)(Short2ByteLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Short2ByteLinkedOpenHashMap.this.link[this.curr];
            }

            --Short2ByteLinkedOpenHashMap.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               Short2ByteLinkedOpenHashMap.this.first = this.next;
            } else {
               var6 = Short2ByteLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (Short2ByteLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Short2ByteLinkedOpenHashMap.this.last = this.prev;
            } else {
               var6 = Short2ByteLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var6[var10001] ^= (Short2ByteLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Short2ByteLinkedOpenHashMap.this.n) {
               Short2ByteLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               short[] var5 = Short2ByteLinkedOpenHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Short2ByteLinkedOpenHashMap.this.mask;

                  short var4;
                  while(true) {
                     if ((var4 = var5[var3]) == 0) {
                        var5[var1] = 0;
                        return;
                     }

                     int var2 = HashCommon.mix(var4) & Short2ByteLinkedOpenHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Short2ByteLinkedOpenHashMap.this.mask;
                  }

                  var5[var1] = var4;
                  Short2ByteLinkedOpenHashMap.this.value[var1] = Short2ByteLinkedOpenHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Short2ByteLinkedOpenHashMap.this.fixPointers(var3, var1);
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

      public void set(Short2ByteMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Short2ByteMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(short var2, Object var3) {
         this(var2);
      }
   }

   final class MapEntry implements Short2ByteMap.Entry, java.util.Map.Entry<Short, Byte> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public short getShortKey() {
         return Short2ByteLinkedOpenHashMap.this.key[this.index];
      }

      public byte getByteValue() {
         return Short2ByteLinkedOpenHashMap.this.value[this.index];
      }

      public byte setValue(byte var1) {
         byte var2 = Short2ByteLinkedOpenHashMap.this.value[this.index];
         Short2ByteLinkedOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Short getKey() {
         return Short2ByteLinkedOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Byte getValue() {
         return Short2ByteLinkedOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Byte setValue(Byte var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Short2ByteLinkedOpenHashMap.this.key[this.index] == (Short)var2.getKey() && Short2ByteLinkedOpenHashMap.this.value[this.index] == (Byte)var2.getValue();
         }
      }

      public int hashCode() {
         return Short2ByteLinkedOpenHashMap.this.key[this.index] ^ Short2ByteLinkedOpenHashMap.this.value[this.index];
      }

      public String toString() {
         return Short2ByteLinkedOpenHashMap.this.key[this.index] + "=>" + Short2ByteLinkedOpenHashMap.this.value[this.index];
      }
   }
}
