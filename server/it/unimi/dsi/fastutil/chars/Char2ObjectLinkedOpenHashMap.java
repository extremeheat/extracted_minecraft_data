package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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

public class Char2ObjectLinkedOpenHashMap<V> extends AbstractChar2ObjectSortedMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient char[] key;
   protected transient V[] value;
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
   protected transient Char2ObjectSortedMap.FastSortedEntrySet<V> entries;
   protected transient CharSortedSet keys;
   protected transient ObjectCollection<V> values;

   public Char2ObjectLinkedOpenHashMap(int var1, float var2) {
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
            this.key = new char[this.n + 1];
            this.value = new Object[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Char2ObjectLinkedOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Char2ObjectLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Char2ObjectLinkedOpenHashMap(Map<? extends Character, ? extends V> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Char2ObjectLinkedOpenHashMap(Map<? extends Character, ? extends V> var1) {
      this(var1, 0.75F);
   }

   public Char2ObjectLinkedOpenHashMap(Char2ObjectMap<V> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Char2ObjectLinkedOpenHashMap(Char2ObjectMap<V> var1) {
      this(var1, 0.75F);
   }

   public Char2ObjectLinkedOpenHashMap(char[] var1, V[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Char2ObjectLinkedOpenHashMap(char[] var1, V[] var2) {
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

   private V removeEntry(int var1) {
      Object var2 = this.value[var1];
      this.value[var1] = null;
      --this.size;
      this.fixPointers(var1);
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private V removeNullEntry() {
      this.containsNullKey = false;
      Object var1 = this.value[this.n];
      this.value[this.n] = null;
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Character, ? extends V> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(char var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         char[] var3 = this.key;
         char var2;
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

   private void insert(int var1, char var2, V var3) {
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

   public V put(char var1, V var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         Object var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   protected final void shiftKeys(int var1) {
      char[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         char var4;
         while(true) {
            if ((var4 = var5[var1]) == 0) {
               var5[var2] = 0;
               this.value[var2] = null;
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

   public V remove(char var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         char[] var3 = this.key;
         char var2;
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

   private V setValue(int var1, V var2) {
      Object var3 = this.value[var1];
      this.value[var1] = var2;
      return var3;
   }

   public V removeFirst() {
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
         Object var2 = this.value[var1];
         if (var1 == this.n) {
            this.containsNullKey = false;
            this.value[this.n] = null;
         } else {
            this.shiftKeys(var1);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return var2;
      }
   }

   public V removeLast() {
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
         Object var2 = this.value[var1];
         if (var1 == this.n) {
            this.containsNullKey = false;
            this.value[this.n] = null;
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

   public V getAndMoveToFirst(char var1) {
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         char[] var3 = this.key;
         char var2;
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

   public V getAndMoveToLast(char var1) {
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         char[] var3 = this.key;
         char var2;
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

   public V putAndMoveToFirst(char var1, V var2) {
      int var3;
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         char[] var5 = this.key;
         char var4;
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

   public V putAndMoveToLast(char var1, V var2) {
      int var3;
      if (var1 == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         char[] var5 = this.key;
         char var4;
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

   public V get(char var1) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         char[] var3 = this.key;
         char var2;
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

   public boolean containsKey(char var1) {
      if (var1 == 0) {
         return this.containsNullKey;
      } else {
         char[] var3 = this.key;
         char var2;
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

   public boolean containsValue(Object var1) {
      Object[] var2 = this.value;
      char[] var3 = this.key;
      if (this.containsNullKey && Objects.equals(var2[this.n], var1)) {
         return true;
      } else {
         int var4 = this.n;

         do {
            if (var4-- == 0) {
               return false;
            }
         } while(var3[var4] == 0 || !Objects.equals(var2[var4], var1));

         return true;
      }
   }

   public V getOrDefault(char var1, V var2) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         char[] var4 = this.key;
         char var3;
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

   public V putIfAbsent(char var1, V var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(char var1, Object var2) {
      if (var1 == 0) {
         if (this.containsNullKey && Objects.equals(var2, this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         char[] var4 = this.key;
         char var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(var1) & this.mask]) == 0) {
            return false;
         } else if (var1 == var3 && Objects.equals(var2, this.value[var5])) {
            this.removeEntry(var5);
            return true;
         } else {
            do {
               if ((var3 = var4[var5 = var5 + 1 & this.mask]) == 0) {
                  return false;
               }
            } while(var1 != var3 || !Objects.equals(var2, this.value[var5]));

            this.removeEntry(var5);
            return true;
         }
      }
   }

   public boolean replace(char var1, V var2, V var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && Objects.equals(var2, this.value[var4])) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public V replace(char var1, V var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Object var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public V computeIfAbsent(char var1, IntFunction<? extends V> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         Object var4 = var2.apply(var1);
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public V computeIfPresent(char var1, BiFunction<? super Character, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Object var4 = var2.apply(var1, this.value[var3]);
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

   public V compute(char var1, BiFunction<? super Character, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Object var4 = var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (var1 == 0) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }
         }

         return this.defRetValue;
      } else if (var3 < 0) {
         this.insert(-var3 - 1, var1, var4);
         return var4;
      } else {
         return this.value[var3] = var4;
      }
   }

   public V merge(char var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 >= 0 && this.value[var4] != null) {
         Object var5 = var3.apply(this.value[var4], var2);
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
      } else if (var2 == null) {
         return this.defRetValue;
      } else {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      }
   }

   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, '\u0000');
         Arrays.fill(this.value, (Object)null);
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

   public char firstCharKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   public char lastCharKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   public Char2ObjectSortedMap<V> tailMap(char var1) {
      throw new UnsupportedOperationException();
   }

   public Char2ObjectSortedMap<V> headMap(char var1) {
      throw new UnsupportedOperationException();
   }

   public Char2ObjectSortedMap<V> subMap(char var1, char var2) {
      throw new UnsupportedOperationException();
   }

   public CharComparator comparator() {
      return null;
   }

   public Char2ObjectSortedMap.FastSortedEntrySet<V> char2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2ObjectLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public CharSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2ObjectLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractObjectCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Char2ObjectLinkedOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Char2ObjectLinkedOpenHashMap.this.size;
            }

            public boolean contains(Object var1) {
               return Char2ObjectLinkedOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Char2ObjectLinkedOpenHashMap.this.clear();
            }

            public void forEach(Consumer<? super V> var1) {
               if (Char2ObjectLinkedOpenHashMap.this.containsNullKey) {
                  var1.accept(Char2ObjectLinkedOpenHashMap.this.value[Char2ObjectLinkedOpenHashMap.this.n]);
               }

               int var2 = Char2ObjectLinkedOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Char2ObjectLinkedOpenHashMap.this.key[var2] != 0) {
                     var1.accept(Char2ObjectLinkedOpenHashMap.this.value[var2]);
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
      char[] var2 = this.key;
      Object[] var3 = this.value;
      int var4 = var1 - 1;
      char[] var5 = new char[var1 + 1];
      Object[] var6 = new Object[var1 + 1];
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

   public Char2ObjectLinkedOpenHashMap<V> clone() {
      Char2ObjectLinkedOpenHashMap var1;
      try {
         var1 = (Char2ObjectLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (char[])this.key.clone();
      var1.value = (Object[])this.value.clone();
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
         if (this != this.value[var3]) {
            var5 ^= this.value[var3] == null ? 0 : this.value[var3].hashCode();
         }

         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      char[] var2 = this.key;
      Object[] var3 = this.value;
      Char2ObjectLinkedOpenHashMap.MapIterator var4 = new Char2ObjectLinkedOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeChar(var2[var6]);
         var1.writeObject(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      char[] var2 = this.key = new char[this.n + 1];
      Object[] var3 = this.value = new Object[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var8 = this.size;

      while(var8-- != 0) {
         char var6 = var1.readChar();
         Object var7 = var1.readObject();
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

   private final class ValueIterator extends Char2ObjectLinkedOpenHashMap<V>.MapIterator implements ObjectListIterator<V> {
      public V previous() {
         return Char2ObjectLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public V next() {
         return Char2ObjectLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractCharSortedSet {
      private KeySet() {
         super();
      }

      public CharListIterator iterator(char var1) {
         return Char2ObjectLinkedOpenHashMap.this.new KeyIterator(var1);
      }

      public CharListIterator iterator() {
         return Char2ObjectLinkedOpenHashMap.this.new KeyIterator();
      }

      public void forEach(IntConsumer var1) {
         if (Char2ObjectLinkedOpenHashMap.this.containsNullKey) {
            var1.accept(Char2ObjectLinkedOpenHashMap.this.key[Char2ObjectLinkedOpenHashMap.this.n]);
         }

         int var2 = Char2ObjectLinkedOpenHashMap.this.n;

         while(var2-- != 0) {
            char var3 = Char2ObjectLinkedOpenHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Char2ObjectLinkedOpenHashMap.this.size;
      }

      public boolean contains(char var1) {
         return Char2ObjectLinkedOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(char var1) {
         int var2 = Char2ObjectLinkedOpenHashMap.this.size;
         Char2ObjectLinkedOpenHashMap.this.remove(var1);
         return Char2ObjectLinkedOpenHashMap.this.size != var2;
      }

      public void clear() {
         Char2ObjectLinkedOpenHashMap.this.clear();
      }

      public char firstChar() {
         if (Char2ObjectLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Char2ObjectLinkedOpenHashMap.this.key[Char2ObjectLinkedOpenHashMap.this.first];
         }
      }

      public char lastChar() {
         if (Char2ObjectLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Char2ObjectLinkedOpenHashMap.this.key[Char2ObjectLinkedOpenHashMap.this.last];
         }
      }

      public CharComparator comparator() {
         return null;
      }

      public CharSortedSet tailSet(char var1) {
         throw new UnsupportedOperationException();
      }

      public CharSortedSet headSet(char var1) {
         throw new UnsupportedOperationException();
      }

      public CharSortedSet subSet(char var1, char var2) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Char2ObjectLinkedOpenHashMap<V>.MapIterator implements CharListIterator {
      public KeyIterator(char var2) {
         super(var2, null);
      }

      public char previousChar() {
         return Char2ObjectLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public char nextChar() {
         return Char2ObjectLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Char2ObjectMap.Entry<V>> implements Char2ObjectSortedMap.FastSortedEntrySet<V> {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Char2ObjectMap.Entry<V>> iterator() {
         return Char2ObjectLinkedOpenHashMap.this.new EntryIterator();
      }

      public Comparator<? super Char2ObjectMap.Entry<V>> comparator() {
         return null;
      }

      public ObjectSortedSet<Char2ObjectMap.Entry<V>> subSet(Char2ObjectMap.Entry<V> var1, Char2ObjectMap.Entry<V> var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Char2ObjectMap.Entry<V>> headSet(Char2ObjectMap.Entry<V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Char2ObjectMap.Entry<V>> tailSet(Char2ObjectMap.Entry<V> var1) {
         throw new UnsupportedOperationException();
      }

      public Char2ObjectMap.Entry<V> first() {
         if (Char2ObjectLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Char2ObjectLinkedOpenHashMap.this.new MapEntry(Char2ObjectLinkedOpenHashMap.this.first);
         }
      }

      public Char2ObjectMap.Entry<V> last() {
         if (Char2ObjectLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Char2ObjectLinkedOpenHashMap.this.new MapEntry(Char2ObjectLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Character) {
               char var3 = (Character)var2.getKey();
               Object var4 = var2.getValue();
               if (var3 == 0) {
                  return Char2ObjectLinkedOpenHashMap.this.containsNullKey && Objects.equals(Char2ObjectLinkedOpenHashMap.this.value[Char2ObjectLinkedOpenHashMap.this.n], var4);
               } else {
                  char[] var6 = Char2ObjectLinkedOpenHashMap.this.key;
                  char var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(var3) & Char2ObjectLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (var3 == var5) {
                     return Objects.equals(Char2ObjectLinkedOpenHashMap.this.value[var7], var4);
                  } else {
                     while((var5 = var6[var7 = var7 + 1 & Char2ObjectLinkedOpenHashMap.this.mask]) != 0) {
                        if (var3 == var5) {
                           return Objects.equals(Char2ObjectLinkedOpenHashMap.this.value[var7], var4);
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
            if (var2.getKey() != null && var2.getKey() instanceof Character) {
               char var3 = (Character)var2.getKey();
               Object var4 = var2.getValue();
               if (var3 == 0) {
                  if (Char2ObjectLinkedOpenHashMap.this.containsNullKey && Objects.equals(Char2ObjectLinkedOpenHashMap.this.value[Char2ObjectLinkedOpenHashMap.this.n], var4)) {
                     Char2ObjectLinkedOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  char[] var6 = Char2ObjectLinkedOpenHashMap.this.key;
                  char var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(var3) & Char2ObjectLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (var5 == var3) {
                     if (Objects.equals(Char2ObjectLinkedOpenHashMap.this.value[var7], var4)) {
                        Char2ObjectLinkedOpenHashMap.this.removeEntry(var7);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((var5 = var6[var7 = var7 + 1 & Char2ObjectLinkedOpenHashMap.this.mask]) == 0) {
                           return false;
                        }
                     } while(var5 != var3 || !Objects.equals(Char2ObjectLinkedOpenHashMap.this.value[var7], var4));

                     Char2ObjectLinkedOpenHashMap.this.removeEntry(var7);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Char2ObjectLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Char2ObjectLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Char2ObjectMap.Entry<V>> iterator(Char2ObjectMap.Entry<V> var1) {
         return Char2ObjectLinkedOpenHashMap.this.new EntryIterator(var1.getCharKey());
      }

      public ObjectListIterator<Char2ObjectMap.Entry<V>> fastIterator() {
         return Char2ObjectLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Char2ObjectMap.Entry<V>> fastIterator(Char2ObjectMap.Entry<V> var1) {
         return Char2ObjectLinkedOpenHashMap.this.new FastEntryIterator(var1.getCharKey());
      }

      public void forEach(Consumer<? super Char2ObjectMap.Entry<V>> var1) {
         int var2 = Char2ObjectLinkedOpenHashMap.this.size;
         int var4 = Char2ObjectLinkedOpenHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Char2ObjectLinkedOpenHashMap.this.link[var4];
            var1.accept(new AbstractChar2ObjectMap.BasicEntry(Char2ObjectLinkedOpenHashMap.this.key[var3], Char2ObjectLinkedOpenHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Char2ObjectMap.Entry<V>> var1) {
         AbstractChar2ObjectMap.BasicEntry var2 = new AbstractChar2ObjectMap.BasicEntry();
         int var3 = Char2ObjectLinkedOpenHashMap.this.size;
         int var5 = Char2ObjectLinkedOpenHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Char2ObjectLinkedOpenHashMap.this.link[var5];
            var2.key = Char2ObjectLinkedOpenHashMap.this.key[var4];
            var2.value = Char2ObjectLinkedOpenHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Char2ObjectLinkedOpenHashMap<V>.MapIterator implements ObjectListIterator<Char2ObjectMap.Entry<V>> {
      final Char2ObjectLinkedOpenHashMap<V>.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Char2ObjectLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(char var2) {
         super(var2, null);
         this.entry = Char2ObjectLinkedOpenHashMap.this.new MapEntry();
      }

      public Char2ObjectLinkedOpenHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Char2ObjectLinkedOpenHashMap<V>.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Char2ObjectLinkedOpenHashMap<V>.MapIterator implements ObjectListIterator<Char2ObjectMap.Entry<V>> {
      private Char2ObjectLinkedOpenHashMap<V>.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(char var2) {
         super(var2, null);
      }

      public Char2ObjectLinkedOpenHashMap<V>.MapEntry next() {
         return this.entry = Char2ObjectLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Char2ObjectLinkedOpenHashMap<V>.MapEntry previous() {
         return this.entry = Char2ObjectLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
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
         this.next = Char2ObjectLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(char var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (var2 == 0) {
            if (Char2ObjectLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Char2ObjectLinkedOpenHashMap.this.link[Char2ObjectLinkedOpenHashMap.this.n];
               this.prev = Char2ObjectLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Char2ObjectLinkedOpenHashMap.this.key[Char2ObjectLinkedOpenHashMap.this.last] == var2) {
            this.prev = Char2ObjectLinkedOpenHashMap.this.last;
            this.index = Char2ObjectLinkedOpenHashMap.this.size;
         } else {
            for(int var3 = HashCommon.mix(var2) & Char2ObjectLinkedOpenHashMap.this.mask; Char2ObjectLinkedOpenHashMap.this.key[var3] != 0; var3 = var3 + 1 & Char2ObjectLinkedOpenHashMap.this.mask) {
               if (Char2ObjectLinkedOpenHashMap.this.key[var3] == var2) {
                  this.next = (int)Char2ObjectLinkedOpenHashMap.this.link[var3];
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
               this.index = Char2ObjectLinkedOpenHashMap.this.size;
            } else {
               int var1 = Char2ObjectLinkedOpenHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Char2ObjectLinkedOpenHashMap.this.link[var1];
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
            this.next = (int)Char2ObjectLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Char2ObjectLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
               this.prev = (int)(Char2ObjectLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Char2ObjectLinkedOpenHashMap.this.link[this.curr];
            }

            --Char2ObjectLinkedOpenHashMap.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               Char2ObjectLinkedOpenHashMap.this.first = this.next;
            } else {
               var6 = Char2ObjectLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (Char2ObjectLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Char2ObjectLinkedOpenHashMap.this.last = this.prev;
            } else {
               var6 = Char2ObjectLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var6[var10001] ^= (Char2ObjectLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Char2ObjectLinkedOpenHashMap.this.n) {
               Char2ObjectLinkedOpenHashMap.this.containsNullKey = false;
               Char2ObjectLinkedOpenHashMap.this.value[Char2ObjectLinkedOpenHashMap.this.n] = null;
            } else {
               char[] var5 = Char2ObjectLinkedOpenHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Char2ObjectLinkedOpenHashMap.this.mask;

                  char var4;
                  while(true) {
                     if ((var4 = var5[var3]) == 0) {
                        var5[var1] = 0;
                        Char2ObjectLinkedOpenHashMap.this.value[var1] = null;
                        return;
                     }

                     int var2 = HashCommon.mix(var4) & Char2ObjectLinkedOpenHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Char2ObjectLinkedOpenHashMap.this.mask;
                  }

                  var5[var1] = var4;
                  Char2ObjectLinkedOpenHashMap.this.value[var1] = Char2ObjectLinkedOpenHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Char2ObjectLinkedOpenHashMap.this.fixPointers(var3, var1);
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

      public void set(Char2ObjectMap.Entry<V> var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Char2ObjectMap.Entry<V> var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(char var2, Object var3) {
         this(var2);
      }
   }

   final class MapEntry implements Char2ObjectMap.Entry<V>, java.util.Map.Entry<Character, V> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public char getCharKey() {
         return Char2ObjectLinkedOpenHashMap.this.key[this.index];
      }

      public V getValue() {
         return Char2ObjectLinkedOpenHashMap.this.value[this.index];
      }

      public V setValue(V var1) {
         Object var2 = Char2ObjectLinkedOpenHashMap.this.value[this.index];
         Char2ObjectLinkedOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Character getKey() {
         return Char2ObjectLinkedOpenHashMap.this.key[this.index];
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Char2ObjectLinkedOpenHashMap.this.key[this.index] == (Character)var2.getKey() && Objects.equals(Char2ObjectLinkedOpenHashMap.this.value[this.index], var2.getValue());
         }
      }

      public int hashCode() {
         return Char2ObjectLinkedOpenHashMap.this.key[this.index] ^ (Char2ObjectLinkedOpenHashMap.this.value[this.index] == null ? 0 : Char2ObjectLinkedOpenHashMap.this.value[this.index].hashCode());
      }

      public String toString() {
         return Char2ObjectLinkedOpenHashMap.this.key[this.index] + "=>" + Char2ObjectLinkedOpenHashMap.this.value[this.index];
      }
   }
}
