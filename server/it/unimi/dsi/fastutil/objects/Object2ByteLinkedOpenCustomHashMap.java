package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
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
import java.util.function.ToIntFunction;

public class Object2ByteLinkedOpenCustomHashMap<K> extends AbstractObject2ByteSortedMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient byte[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected Hash.Strategy<K> strategy;
   protected transient int first;
   protected transient int last;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Object2ByteSortedMap.FastSortedEntrySet<K> entries;
   protected transient ObjectSortedSet<K> keys;
   protected transient ByteCollection values;

   public Object2ByteLinkedOpenCustomHashMap(int var1, float var2, Hash.Strategy<K> var3) {
      super();
      this.first = -1;
      this.last = -1;
      this.strategy = var3;
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new Object[this.n + 1];
            this.value = new byte[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Object2ByteLinkedOpenCustomHashMap(int var1, Hash.Strategy<K> var2) {
      this(var1, 0.75F, var2);
   }

   public Object2ByteLinkedOpenCustomHashMap(Hash.Strategy<K> var1) {
      this(16, 0.75F, var1);
   }

   public Object2ByteLinkedOpenCustomHashMap(Map<? extends K, ? extends Byte> var1, float var2, Hash.Strategy<K> var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Object2ByteLinkedOpenCustomHashMap(Map<? extends K, ? extends Byte> var1, Hash.Strategy<K> var2) {
      this(var1, 0.75F, var2);
   }

   public Object2ByteLinkedOpenCustomHashMap(Object2ByteMap<K> var1, float var2, Hash.Strategy<K> var3) {
      this(var1.size(), var2, var3);
      this.putAll(var1);
   }

   public Object2ByteLinkedOpenCustomHashMap(Object2ByteMap<K> var1, Hash.Strategy<K> var2) {
      this(var1, 0.75F, var2);
   }

   public Object2ByteLinkedOpenCustomHashMap(K[] var1, byte[] var2, float var3, Hash.Strategy<K> var4) {
      this(var1.length, var3, var4);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            this.put(var1[var5], var2[var5]);
         }

      }
   }

   public Object2ByteLinkedOpenCustomHashMap(K[] var1, byte[] var2, Hash.Strategy<K> var3) {
      this(var1, var2, 0.75F, var3);
   }

   public Hash.Strategy<K> strategy() {
      return this.strategy;
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
      this.key[this.n] = null;
      byte var1 = this.value[this.n];
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends K, ? extends Byte> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(K var1) {
      if (this.strategy.equals(var1, (Object)null)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == null) {
            return -(var4 + 1);
         } else if (this.strategy.equals(var1, var2)) {
            return var4;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var1, var2)) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, K var2, byte var3) {
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

   public byte put(K var1, byte var2) {
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

   public byte addTo(K var1, byte var2) {
      int var3;
      if (this.strategy.equals(var1, (Object)null)) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var3 = this.n;
         this.containsNullKey = true;
      } else {
         Object[] var5 = this.key;
         Object var4;
         if ((var4 = var5[var3 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) != null) {
            if (this.strategy.equals(var4, var1)) {
               return this.addToValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var4, var1)) {
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
      Object[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         Object var4;
         while(true) {
            if ((var4 = var5[var1]) == null) {
               var5[var2] = null;
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
         this.value[var2] = this.value[var1];
         this.fixPointers(var1, var2);
      }
   }

   public byte removeByte(Object var1) {
      if (this.strategy.equals(var1, (Object)null)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var1, var2)) {
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
            this.key[this.n] = null;
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
            this.key[this.n] = null;
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

   public byte getAndMoveToFirst(K var1) {
      if (this.strategy.equals(var1, (Object)null)) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            this.moveIndexToFirst(var4);
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var1, var2)) {
                  this.moveIndexToFirst(var4);
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public byte getAndMoveToLast(K var1) {
      if (this.strategy.equals(var1, (Object)null)) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            this.moveIndexToLast(var4);
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var1, var2)) {
                  this.moveIndexToLast(var4);
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public byte putAndMoveToFirst(K var1, byte var2) {
      int var3;
      if (this.strategy.equals(var1, (Object)null)) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         Object[] var5 = this.key;
         Object var4;
         if ((var4 = var5[var3 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) != null) {
            if (this.strategy.equals(var4, var1)) {
               this.moveIndexToFirst(var3);
               return this.setValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var4, var1)) {
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

   public byte putAndMoveToLast(K var1, byte var2) {
      int var3;
      if (this.strategy.equals(var1, (Object)null)) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         Object[] var5 = this.key;
         Object var4;
         if ((var4 = var5[var3 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) != null) {
            if (this.strategy.equals(var4, var1)) {
               this.moveIndexToLast(var3);
               return this.setValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var4, var1)) {
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

   public byte getByte(Object var1) {
      if (this.strategy.equals(var1, (Object)null)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals(var1, var2)) {
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var1, var2)) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(Object var1) {
      if (this.strategy.equals(var1, (Object)null)) {
         return this.containsNullKey;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == null) {
            return false;
         } else if (this.strategy.equals(var1, var2)) {
            return true;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var1, var2)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(byte var1) {
      byte[] var2 = this.value;
      Object[] var3 = this.key;
      if (this.containsNullKey && var2[this.n] == var1) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(var3[var4] == null || var2[var4] != var1);

         return true;
      }
   }

   public byte getOrDefault(Object var1, byte var2) {
      if (this.strategy.equals(var1, (Object)null)) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         Object[] var4 = this.key;
         Object var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == null) {
            return var2;
         } else if (this.strategy.equals(var1, var3)) {
            return this.value[var5];
         } else {
            while((var3 = var4[var5 = var5 + 1 & this.mask]) != null) {
               if (this.strategy.equals(var1, var3)) {
                  return this.value[var5];
               }
            }

            return var2;
         }
      }
   }

   public byte putIfAbsent(K var1, byte var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(Object var1, byte var2) {
      if (this.strategy.equals(var1, (Object)null)) {
         if (this.containsNullKey && var2 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         Object[] var4 = this.key;
         Object var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(this.strategy.hashCode(var1)) & this.mask]) == null) {
            return false;
         } else if (this.strategy.equals(var1, var3) && var2 == this.value[var5]) {
            this.removeEntry(var5);
            return true;
         } else {
            do {
               if ((var3 = var4[var5 = var5 + 1 & this.mask]) == null) {
                  return false;
               }
            } while(!this.strategy.equals(var1, var3) || var2 != this.value[var5]);

            this.removeEntry(var5);
            return true;
         }
      }
   }

   public boolean replace(K var1, byte var2, byte var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public byte replace(K var1, byte var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         byte var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public byte computeByteIfAbsent(K var1, ToIntFunction<? super K> var2) {
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

   public byte computeByteIfPresent(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Byte var4 = (Byte)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (this.strategy.equals(var1, (Object)null)) {
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

   public byte computeByte(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Byte var4 = (Byte)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (this.strategy.equals(var1, (Object)null)) {
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

   public byte mergeByte(K var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      } else {
         Byte var5 = (Byte)var3.apply(this.value[var4], var2);
         if (var5 == null) {
            if (this.strategy.equals(var1, (Object)null)) {
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
         Arrays.fill(this.key, (Object)null);
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

   public K firstKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public K lastKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Object2ByteSortedMap<K> tailMap(K var1) {
      throw new UnsupportedOperationException();
   }

   public Object2ByteSortedMap<K> headMap(K var1) {
      throw new UnsupportedOperationException();
   }

   public Object2ByteSortedMap<K> subMap(K var1, K var2) {
      throw new UnsupportedOperationException();
   }

   public Comparator<? super K> comparator() {
      return null;
   }

   public Object2ByteSortedMap.FastSortedEntrySet<K> object2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2ByteLinkedOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ObjectSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2ByteLinkedOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   public ByteCollection values() {
      if (this.values == null) {
         this.values = new AbstractByteCollection() {
            public ByteIterator iterator() {
               return Object2ByteLinkedOpenCustomHashMap.this.new ValueIterator();
            }

            public int size() {
               return Object2ByteLinkedOpenCustomHashMap.this.size;
            }

            public boolean contains(byte var1) {
               return Object2ByteLinkedOpenCustomHashMap.this.containsValue(var1);
            }

            public void clear() {
               Object2ByteLinkedOpenCustomHashMap.this.clear();
            }

            public void forEach(IntConsumer var1) {
               if (Object2ByteLinkedOpenCustomHashMap.this.containsNullKey) {
                  var1.accept(Object2ByteLinkedOpenCustomHashMap.this.value[Object2ByteLinkedOpenCustomHashMap.this.n]);
               }

               int var2 = Object2ByteLinkedOpenCustomHashMap.this.n;

               while(var2-- != 0) {
                  if (Object2ByteLinkedOpenCustomHashMap.this.key[var2] != null) {
                     var1.accept(Object2ByteLinkedOpenCustomHashMap.this.value[var2]);
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
      Object[] var2 = this.key;
      byte[] var3 = this.value;
      int var4 = var1 - 1;
      Object[] var5 = new Object[var1 + 1];
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
         if (this.strategy.equals(var2[var7], (Object)null)) {
            var11 = var1;
         } else {
            for(var11 = HashCommon.mix(this.strategy.hashCode(var2[var7])) & var4; var5[var11] != null; var11 = var11 + 1 & var4) {
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

   public Object2ByteLinkedOpenCustomHashMap<K> clone() {
      Object2ByteLinkedOpenCustomHashMap var1;
      try {
         var1 = (Object2ByteLinkedOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (Object[])this.key.clone();
      var1.value = (byte[])this.value.clone();
      var1.link = (long[])this.link.clone();
      var1.strategy = this.strategy;
      return var1;
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.realSize();
      int var3 = 0;

      for(int var4 = 0; var2-- != 0; ++var3) {
         while(this.key[var3] == null) {
            ++var3;
         }

         if (this != this.key[var3]) {
            var4 = this.strategy.hashCode(this.key[var3]);
         }

         var4 ^= this.value[var3];
         var1 += var4;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n];
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Object[] var2 = this.key;
      byte[] var3 = this.value;
      Object2ByteLinkedOpenCustomHashMap.MapIterator var4 = new Object2ByteLinkedOpenCustomHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeObject(var2[var6]);
         var1.writeByte(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      Object[] var2 = this.key = new Object[this.n + 1];
      byte[] var3 = this.value = new byte[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var8 = this.size;

      while(var8-- != 0) {
         Object var6 = var1.readObject();
         byte var7 = var1.readByte();
         int var9;
         if (this.strategy.equals(var6, (Object)null)) {
            var9 = this.n;
            this.containsNullKey = true;
         } else {
            for(var9 = HashCommon.mix(this.strategy.hashCode(var6)) & this.mask; var2[var9] != null; var9 = var9 + 1 & this.mask) {
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

   private final class ValueIterator extends Object2ByteLinkedOpenCustomHashMap<K>.MapIterator implements ByteListIterator {
      public byte previousByte() {
         return Object2ByteLinkedOpenCustomHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public byte nextByte() {
         return Object2ByteLinkedOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractObjectSortedSet<K> {
      private KeySet() {
         super();
      }

      public ObjectListIterator<K> iterator(K var1) {
         return Object2ByteLinkedOpenCustomHashMap.this.new KeyIterator(var1);
      }

      public ObjectListIterator<K> iterator() {
         return Object2ByteLinkedOpenCustomHashMap.this.new KeyIterator();
      }

      public void forEach(Consumer<? super K> var1) {
         if (Object2ByteLinkedOpenCustomHashMap.this.containsNullKey) {
            var1.accept(Object2ByteLinkedOpenCustomHashMap.this.key[Object2ByteLinkedOpenCustomHashMap.this.n]);
         }

         int var2 = Object2ByteLinkedOpenCustomHashMap.this.n;

         while(var2-- != 0) {
            Object var3 = Object2ByteLinkedOpenCustomHashMap.this.key[var2];
            if (var3 != null) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Object2ByteLinkedOpenCustomHashMap.this.size;
      }

      public boolean contains(Object var1) {
         return Object2ByteLinkedOpenCustomHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         int var2 = Object2ByteLinkedOpenCustomHashMap.this.size;
         Object2ByteLinkedOpenCustomHashMap.this.removeByte(var1);
         return Object2ByteLinkedOpenCustomHashMap.this.size != var2;
      }

      public void clear() {
         Object2ByteLinkedOpenCustomHashMap.this.clear();
      }

      public K first() {
         if (Object2ByteLinkedOpenCustomHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2ByteLinkedOpenCustomHashMap.this.key[Object2ByteLinkedOpenCustomHashMap.this.first];
         }
      }

      public K last() {
         if (Object2ByteLinkedOpenCustomHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2ByteLinkedOpenCustomHashMap.this.key[Object2ByteLinkedOpenCustomHashMap.this.last];
         }
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Object2ByteLinkedOpenCustomHashMap<K>.MapIterator implements ObjectListIterator<K> {
      public KeyIterator(K var2) {
         super(var2, null);
      }

      public K previous() {
         return Object2ByteLinkedOpenCustomHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public K next() {
         return Object2ByteLinkedOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Object2ByteMap.Entry<K>> implements Object2ByteSortedMap.FastSortedEntrySet<K> {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator() {
         return Object2ByteLinkedOpenCustomHashMap.this.new EntryIterator();
      }

      public Comparator<? super Object2ByteMap.Entry<K>> comparator() {
         return null;
      }

      public ObjectSortedSet<Object2ByteMap.Entry<K>> subSet(Object2ByteMap.Entry<K> var1, Object2ByteMap.Entry<K> var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Object2ByteMap.Entry<K>> headSet(Object2ByteMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Object2ByteMap.Entry<K>> tailSet(Object2ByteMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      public Object2ByteMap.Entry<K> first() {
         if (Object2ByteLinkedOpenCustomHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2ByteLinkedOpenCustomHashMap.this.new MapEntry(Object2ByteLinkedOpenCustomHashMap.this.first);
         }
      }

      public Object2ByteMap.Entry<K> last() {
         if (Object2ByteLinkedOpenCustomHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2ByteLinkedOpenCustomHashMap.this.new MapEntry(Object2ByteLinkedOpenCustomHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getValue() != null && var2.getValue() instanceof Byte) {
               Object var3 = var2.getKey();
               byte var4 = (Byte)var2.getValue();
               if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(var3, (Object)null)) {
                  return Object2ByteLinkedOpenCustomHashMap.this.containsNullKey && Object2ByteLinkedOpenCustomHashMap.this.value[Object2ByteLinkedOpenCustomHashMap.this.n] == var4;
               } else {
                  Object[] var6 = Object2ByteLinkedOpenCustomHashMap.this.key;
                  Object var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(var3)) & Object2ByteLinkedOpenCustomHashMap.this.mask]) == null) {
                     return false;
                  } else if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(var3, var5)) {
                     return Object2ByteLinkedOpenCustomHashMap.this.value[var7] == var4;
                  } else {
                     while((var5 = var6[var7 = var7 + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask]) != null) {
                        if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(var3, var5)) {
                           return Object2ByteLinkedOpenCustomHashMap.this.value[var7] == var4;
                        }
                     }

                     return false;
                  }
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
            if (var2.getValue() != null && var2.getValue() instanceof Byte) {
               Object var3 = var2.getKey();
               byte var4 = (Byte)var2.getValue();
               if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(var3, (Object)null)) {
                  if (Object2ByteLinkedOpenCustomHashMap.this.containsNullKey && Object2ByteLinkedOpenCustomHashMap.this.value[Object2ByteLinkedOpenCustomHashMap.this.n] == var4) {
                     Object2ByteLinkedOpenCustomHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  Object[] var6 = Object2ByteLinkedOpenCustomHashMap.this.key;
                  Object var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(var3)) & Object2ByteLinkedOpenCustomHashMap.this.mask]) == null) {
                     return false;
                  } else if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(var5, var3)) {
                     if (Object2ByteLinkedOpenCustomHashMap.this.value[var7] == var4) {
                        Object2ByteLinkedOpenCustomHashMap.this.removeEntry(var7);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((var5 = var6[var7 = var7 + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask]) == null) {
                           return false;
                        }
                     } while(!Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(var5, var3) || Object2ByteLinkedOpenCustomHashMap.this.value[var7] != var4);

                     Object2ByteLinkedOpenCustomHashMap.this.removeEntry(var7);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Object2ByteLinkedOpenCustomHashMap.this.size;
      }

      public void clear() {
         Object2ByteLinkedOpenCustomHashMap.this.clear();
      }

      public ObjectListIterator<Object2ByteMap.Entry<K>> iterator(Object2ByteMap.Entry<K> var1) {
         return Object2ByteLinkedOpenCustomHashMap.this.new EntryIterator(var1.getKey());
      }

      public ObjectListIterator<Object2ByteMap.Entry<K>> fastIterator() {
         return Object2ByteLinkedOpenCustomHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteMap.Entry<K> var1) {
         return Object2ByteLinkedOpenCustomHashMap.this.new FastEntryIterator(var1.getKey());
      }

      public void forEach(Consumer<? super Object2ByteMap.Entry<K>> var1) {
         int var2 = Object2ByteLinkedOpenCustomHashMap.this.size;
         int var4 = Object2ByteLinkedOpenCustomHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Object2ByteLinkedOpenCustomHashMap.this.link[var4];
            var1.accept(new AbstractObject2ByteMap.BasicEntry(Object2ByteLinkedOpenCustomHashMap.this.key[var3], Object2ByteLinkedOpenCustomHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Object2ByteMap.Entry<K>> var1) {
         AbstractObject2ByteMap.BasicEntry var2 = new AbstractObject2ByteMap.BasicEntry();
         int var3 = Object2ByteLinkedOpenCustomHashMap.this.size;
         int var5 = Object2ByteLinkedOpenCustomHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Object2ByteLinkedOpenCustomHashMap.this.link[var5];
            var2.key = Object2ByteLinkedOpenCustomHashMap.this.key[var4];
            var2.value = Object2ByteLinkedOpenCustomHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Object2ByteLinkedOpenCustomHashMap<K>.MapIterator implements ObjectListIterator<Object2ByteMap.Entry<K>> {
      final Object2ByteLinkedOpenCustomHashMap<K>.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Object2ByteLinkedOpenCustomHashMap.this.new MapEntry();
      }

      public FastEntryIterator(K var2) {
         super(var2, null);
         this.entry = Object2ByteLinkedOpenCustomHashMap.this.new MapEntry();
      }

      public Object2ByteLinkedOpenCustomHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Object2ByteLinkedOpenCustomHashMap<K>.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Object2ByteLinkedOpenCustomHashMap<K>.MapIterator implements ObjectListIterator<Object2ByteMap.Entry<K>> {
      private Object2ByteLinkedOpenCustomHashMap<K>.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(K var2) {
         super(var2, null);
      }

      public Object2ByteLinkedOpenCustomHashMap<K>.MapEntry next() {
         return this.entry = Object2ByteLinkedOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      public Object2ByteLinkedOpenCustomHashMap<K>.MapEntry previous() {
         return this.entry = Object2ByteLinkedOpenCustomHashMap.this.new MapEntry(this.previousEntry());
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
         this.next = Object2ByteLinkedOpenCustomHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(K var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(var2, (Object)null)) {
            if (Object2ByteLinkedOpenCustomHashMap.this.containsNullKey) {
               this.next = (int)Object2ByteLinkedOpenCustomHashMap.this.link[Object2ByteLinkedOpenCustomHashMap.this.n];
               this.prev = Object2ByteLinkedOpenCustomHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(Object2ByteLinkedOpenCustomHashMap.this.key[Object2ByteLinkedOpenCustomHashMap.this.last], var2)) {
            this.prev = Object2ByteLinkedOpenCustomHashMap.this.last;
            this.index = Object2ByteLinkedOpenCustomHashMap.this.size;
         } else {
            for(int var3 = HashCommon.mix(Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(var2)) & Object2ByteLinkedOpenCustomHashMap.this.mask; Object2ByteLinkedOpenCustomHashMap.this.key[var3] != null; var3 = var3 + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask) {
               if (Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(Object2ByteLinkedOpenCustomHashMap.this.key[var3], var2)) {
                  this.next = (int)Object2ByteLinkedOpenCustomHashMap.this.link[var3];
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
               this.index = Object2ByteLinkedOpenCustomHashMap.this.size;
            } else {
               int var1 = Object2ByteLinkedOpenCustomHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Object2ByteLinkedOpenCustomHashMap.this.link[var1];
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
            this.next = (int)Object2ByteLinkedOpenCustomHashMap.this.link[this.curr];
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
            this.prev = (int)(Object2ByteLinkedOpenCustomHashMap.this.link[this.curr] >>> 32);
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
               this.prev = (int)(Object2ByteLinkedOpenCustomHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Object2ByteLinkedOpenCustomHashMap.this.link[this.curr];
            }

            --Object2ByteLinkedOpenCustomHashMap.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               Object2ByteLinkedOpenCustomHashMap.this.first = this.next;
            } else {
               var6 = Object2ByteLinkedOpenCustomHashMap.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (Object2ByteLinkedOpenCustomHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Object2ByteLinkedOpenCustomHashMap.this.last = this.prev;
            } else {
               var6 = Object2ByteLinkedOpenCustomHashMap.this.link;
               var10001 = this.next;
               var6[var10001] ^= (Object2ByteLinkedOpenCustomHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Object2ByteLinkedOpenCustomHashMap.this.n) {
               Object2ByteLinkedOpenCustomHashMap.this.containsNullKey = false;
               Object2ByteLinkedOpenCustomHashMap.this.key[Object2ByteLinkedOpenCustomHashMap.this.n] = null;
            } else {
               Object[] var5 = Object2ByteLinkedOpenCustomHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask;

                  Object var4;
                  while(true) {
                     if ((var4 = var5[var3]) == null) {
                        var5[var1] = null;
                        return;
                     }

                     int var2 = HashCommon.mix(Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(var4)) & Object2ByteLinkedOpenCustomHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Object2ByteLinkedOpenCustomHashMap.this.mask;
                  }

                  var5[var1] = var4;
                  Object2ByteLinkedOpenCustomHashMap.this.value[var1] = Object2ByteLinkedOpenCustomHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Object2ByteLinkedOpenCustomHashMap.this.fixPointers(var3, var1);
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

      public void set(Object2ByteMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Object2ByteMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(Object var2, Object var3) {
         this(var2);
      }
   }

   final class MapEntry implements Object2ByteMap.Entry<K>, java.util.Map.Entry<K, Byte> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public K getKey() {
         return Object2ByteLinkedOpenCustomHashMap.this.key[this.index];
      }

      public byte getByteValue() {
         return Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
      }

      public byte setValue(byte var1) {
         byte var2 = Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
         Object2ByteLinkedOpenCustomHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Byte getValue() {
         return Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
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
            return Object2ByteLinkedOpenCustomHashMap.this.strategy.equals(Object2ByteLinkedOpenCustomHashMap.this.key[this.index], var2.getKey()) && Object2ByteLinkedOpenCustomHashMap.this.value[this.index] == (Byte)var2.getValue();
         }
      }

      public int hashCode() {
         return Object2ByteLinkedOpenCustomHashMap.this.strategy.hashCode(Object2ByteLinkedOpenCustomHashMap.this.key[this.index]) ^ Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
      }

      public String toString() {
         return Object2ByteLinkedOpenCustomHashMap.this.key[this.index] + "=>" + Object2ByteLinkedOpenCustomHashMap.this.value[this.index];
      }
   }
}
