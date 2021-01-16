package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
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

public class Reference2CharLinkedOpenHashMap<K> extends AbstractReference2CharSortedMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient char[] value;
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
   protected transient Reference2CharSortedMap.FastSortedEntrySet<K> entries;
   protected transient ReferenceSortedSet<K> keys;
   protected transient CharCollection values;

   public Reference2CharLinkedOpenHashMap(int var1, float var2) {
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
            this.key = new Object[this.n + 1];
            this.value = new char[this.n + 1];
            this.link = new long[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Reference2CharLinkedOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Reference2CharLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Reference2CharLinkedOpenHashMap(Map<? extends K, ? extends Character> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Reference2CharLinkedOpenHashMap(Map<? extends K, ? extends Character> var1) {
      this(var1, 0.75F);
   }

   public Reference2CharLinkedOpenHashMap(Reference2CharMap<K> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Reference2CharLinkedOpenHashMap(Reference2CharMap<K> var1) {
      this(var1, 0.75F);
   }

   public Reference2CharLinkedOpenHashMap(K[] var1, char[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Reference2CharLinkedOpenHashMap(K[] var1, char[] var2) {
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

   private char removeEntry(int var1) {
      char var2 = this.value[var1];
      --this.size;
      this.fixPointers(var1);
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private char removeNullEntry() {
      this.containsNullKey = false;
      this.key[this.n] = null;
      char var1 = this.value[this.n];
      --this.size;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends K, ? extends Character> var1) {
      if ((double)this.f <= 0.5D) {
         this.ensureCapacity(var1.size());
      } else {
         this.tryCapacity((long)(this.size() + var1.size()));
      }

      super.putAll(var1);
   }

   private int find(K var1) {
      if (var1 == null) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return -(var4 + 1);
         } else if (var1 == var2) {
            return var4;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  return var4;
               }
            }

            return -(var4 + 1);
         }
      }
   }

   private void insert(int var1, K var2, char var3) {
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

   public char put(K var1, char var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         char var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
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

            int var3 = HashCommon.mix(System.identityHashCode(var4)) & this.mask;
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

   public char removeChar(Object var1) {
      if (var1 == null) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (var1 == var2) {
            return this.removeEntry(var4);
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  return this.removeEntry(var4);
               }
            }

            return this.defRetValue;
         }
      }
   }

   private char setValue(int var1, char var2) {
      char var3 = this.value[var1];
      this.value[var1] = var2;
      return var3;
   }

   public char removeFirstChar() {
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
         char var2 = this.value[var1];
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

   public char removeLastChar() {
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
         char var2 = this.value[var1];
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

   public char getAndMoveToFirst(K var1) {
      if (var1 == null) {
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
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (var1 == var2) {
            this.moveIndexToFirst(var4);
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  this.moveIndexToFirst(var4);
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public char getAndMoveToLast(K var1) {
      if (var1 == null) {
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
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (var1 == var2) {
            this.moveIndexToLast(var4);
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  this.moveIndexToLast(var4);
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public char putAndMoveToFirst(K var1, char var2) {
      int var3;
      if (var1 == null) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         Object[] var5 = this.key;
         Object var4;
         if ((var4 = var5[var3 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) != null) {
            if (var4 == var1) {
               this.moveIndexToFirst(var3);
               return this.setValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != null) {
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

   public char putAndMoveToLast(K var1, char var2) {
      int var3;
      if (var1 == null) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, var2);
         }

         this.containsNullKey = true;
         var3 = this.n;
      } else {
         Object[] var5 = this.key;
         Object var4;
         if ((var4 = var5[var3 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) != null) {
            if (var4 == var1) {
               this.moveIndexToLast(var3);
               return this.setValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != null) {
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

   public char getChar(Object var1) {
      if (var1 == null) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (var1 == var2) {
            return this.value[var4];
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  return this.value[var4];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public boolean containsKey(Object var1) {
      if (var1 == null) {
         return this.containsNullKey;
      } else {
         Object[] var3 = this.key;
         Object var2;
         int var4;
         if ((var2 = var3[var4 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return false;
         } else if (var1 == var2) {
            return true;
         } else {
            while((var2 = var3[var4 = var4 + 1 & this.mask]) != null) {
               if (var1 == var2) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public boolean containsValue(char var1) {
      char[] var2 = this.value;
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

   public char getOrDefault(Object var1, char var2) {
      if (var1 == null) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         Object[] var4 = this.key;
         Object var3;
         int var5;
         if ((var3 = var4[var5 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return var2;
         } else if (var1 == var3) {
            return this.value[var5];
         } else {
            while((var3 = var4[var5 = var5 + 1 & this.mask]) != null) {
               if (var1 == var3) {
                  return this.value[var5];
               }
            }

            return var2;
         }
      }
   }

   public char putIfAbsent(K var1, char var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(Object var1, char var2) {
      if (var1 == null) {
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
         if ((var3 = var4[var5 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) == null) {
            return false;
         } else if (var1 == var3 && var2 == this.value[var5]) {
            this.removeEntry(var5);
            return true;
         } else {
            do {
               if ((var3 = var4[var5 = var5 + 1 & this.mask]) == null) {
                  return false;
               }
            } while(var1 != var3 || var2 != this.value[var5]);

            this.removeEntry(var5);
            return true;
         }
      }
   }

   public boolean replace(K var1, char var2, char var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public char replace(K var1, char var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         char var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public char computeCharIfAbsent(K var1, ToIntFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         char var4 = SafeMath.safeIntToChar(var2.applyAsInt(var1));
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public char computeCharIfPresent(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Character var4 = (Character)var2.apply(var1, this.value[var3]);
         if (var4 == null) {
            if (var1 == null) {
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

   public char computeChar(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Character var4 = (Character)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
      if (var4 == null) {
         if (var3 >= 0) {
            if (var1 == null) {
               this.removeNullEntry();
            } else {
               this.removeEntry(var3);
            }
         }

         return this.defRetValue;
      } else {
         char var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public char mergeChar(K var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      } else {
         Character var5 = (Character)var3.apply(this.value[var4], var2);
         if (var5 == null) {
            if (var1 == null) {
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

   public Reference2CharSortedMap<K> tailMap(K var1) {
      throw new UnsupportedOperationException();
   }

   public Reference2CharSortedMap<K> headMap(K var1) {
      throw new UnsupportedOperationException();
   }

   public Reference2CharSortedMap<K> subMap(K var1, K var2) {
      throw new UnsupportedOperationException();
   }

   public Comparator<? super K> comparator() {
      return null;
   }

   public Reference2CharSortedMap.FastSortedEntrySet<K> reference2CharEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2CharLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ReferenceSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2CharLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public CharCollection values() {
      if (this.values == null) {
         this.values = new AbstractCharCollection() {
            public CharIterator iterator() {
               return Reference2CharLinkedOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Reference2CharLinkedOpenHashMap.this.size;
            }

            public boolean contains(char var1) {
               return Reference2CharLinkedOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Reference2CharLinkedOpenHashMap.this.clear();
            }

            public void forEach(IntConsumer var1) {
               if (Reference2CharLinkedOpenHashMap.this.containsNullKey) {
                  var1.accept(Reference2CharLinkedOpenHashMap.this.value[Reference2CharLinkedOpenHashMap.this.n]);
               }

               int var2 = Reference2CharLinkedOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Reference2CharLinkedOpenHashMap.this.key[var2] != null) {
                     var1.accept(Reference2CharLinkedOpenHashMap.this.value[var2]);
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
      char[] var3 = this.value;
      int var4 = var1 - 1;
      Object[] var5 = new Object[var1 + 1];
      char[] var6 = new char[var1 + 1];
      int var7 = this.first;
      int var8 = -1;
      int var9 = -1;
      long[] var12 = this.link;
      long[] var13 = new long[var1 + 1];
      this.first = -1;

      int var10;
      for(int var14 = this.size; var14-- != 0; var8 = var10) {
         int var11;
         if (var2[var7] == null) {
            var11 = var1;
         } else {
            for(var11 = HashCommon.mix(System.identityHashCode(var2[var7])) & var4; var5[var11] != null; var11 = var11 + 1 & var4) {
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

   public Reference2CharLinkedOpenHashMap<K> clone() {
      Reference2CharLinkedOpenHashMap var1;
      try {
         var1 = (Reference2CharLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (Object[])this.key.clone();
      var1.value = (char[])this.value.clone();
      var1.link = (long[])this.link.clone();
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
            var4 = System.identityHashCode(this.key[var3]);
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
      char[] var3 = this.value;
      Reference2CharLinkedOpenHashMap.MapIterator var4 = new Reference2CharLinkedOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeObject(var2[var6]);
         var1.writeChar(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      Object[] var2 = this.key = new Object[this.n + 1];
      char[] var3 = this.value = new char[this.n + 1];
      long[] var4 = this.link = new long[this.n + 1];
      int var5 = -1;
      this.first = this.last = -1;
      int var8 = this.size;

      while(var8-- != 0) {
         Object var6 = var1.readObject();
         char var7 = var1.readChar();
         int var9;
         if (var6 == null) {
            var9 = this.n;
            this.containsNullKey = true;
         } else {
            for(var9 = HashCommon.mix(System.identityHashCode(var6)) & this.mask; var2[var9] != null; var9 = var9 + 1 & this.mask) {
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

   private final class ValueIterator extends Reference2CharLinkedOpenHashMap<K>.MapIterator implements CharListIterator {
      public char previousChar() {
         return Reference2CharLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
         super();
      }

      public char nextChar() {
         return Reference2CharLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractReferenceSortedSet<K> {
      private KeySet() {
         super();
      }

      public ObjectListIterator<K> iterator(K var1) {
         return Reference2CharLinkedOpenHashMap.this.new KeyIterator(var1);
      }

      public ObjectListIterator<K> iterator() {
         return Reference2CharLinkedOpenHashMap.this.new KeyIterator();
      }

      public void forEach(Consumer<? super K> var1) {
         if (Reference2CharLinkedOpenHashMap.this.containsNullKey) {
            var1.accept(Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.n]);
         }

         int var2 = Reference2CharLinkedOpenHashMap.this.n;

         while(var2-- != 0) {
            Object var3 = Reference2CharLinkedOpenHashMap.this.key[var2];
            if (var3 != null) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Reference2CharLinkedOpenHashMap.this.size;
      }

      public boolean contains(Object var1) {
         return Reference2CharLinkedOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         int var2 = Reference2CharLinkedOpenHashMap.this.size;
         Reference2CharLinkedOpenHashMap.this.removeChar(var1);
         return Reference2CharLinkedOpenHashMap.this.size != var2;
      }

      public void clear() {
         Reference2CharLinkedOpenHashMap.this.clear();
      }

      public K first() {
         if (Reference2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.first];
         }
      }

      public K last() {
         if (Reference2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.last];
         }
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         throw new UnsupportedOperationException();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         throw new UnsupportedOperationException();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Reference2CharLinkedOpenHashMap<K>.MapIterator implements ObjectListIterator<K> {
      public KeyIterator(K var2) {
         super(var2, null);
      }

      public K previous() {
         return Reference2CharLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
         super();
      }

      public K next() {
         return Reference2CharLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Reference2CharMap.Entry<K>> implements Reference2CharSortedMap.FastSortedEntrySet<K> {
      private MapEntrySet() {
         super();
      }

      public ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> iterator() {
         return Reference2CharLinkedOpenHashMap.this.new EntryIterator();
      }

      public Comparator<? super Reference2CharMap.Entry<K>> comparator() {
         return null;
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> subSet(Reference2CharMap.Entry<K> var1, Reference2CharMap.Entry<K> var2) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> headSet(Reference2CharMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> tailSet(Reference2CharMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      public Reference2CharMap.Entry<K> first() {
         if (Reference2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Reference2CharLinkedOpenHashMap.this.new MapEntry(Reference2CharLinkedOpenHashMap.this.first);
         }
      }

      public Reference2CharMap.Entry<K> last() {
         if (Reference2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Reference2CharLinkedOpenHashMap.this.new MapEntry(Reference2CharLinkedOpenHashMap.this.last);
         }
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getValue() != null && var2.getValue() instanceof Character) {
               Object var3 = var2.getKey();
               char var4 = (Character)var2.getValue();
               if (var3 == null) {
                  return Reference2CharLinkedOpenHashMap.this.containsNullKey && Reference2CharLinkedOpenHashMap.this.value[Reference2CharLinkedOpenHashMap.this.n] == var4;
               } else {
                  Object[] var6 = Reference2CharLinkedOpenHashMap.this.key;
                  Object var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(System.identityHashCode(var3)) & Reference2CharLinkedOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (var3 == var5) {
                     return Reference2CharLinkedOpenHashMap.this.value[var7] == var4;
                  } else {
                     while((var5 = var6[var7 = var7 + 1 & Reference2CharLinkedOpenHashMap.this.mask]) != null) {
                        if (var3 == var5) {
                           return Reference2CharLinkedOpenHashMap.this.value[var7] == var4;
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
            if (var2.getValue() != null && var2.getValue() instanceof Character) {
               Object var3 = var2.getKey();
               char var4 = (Character)var2.getValue();
               if (var3 == null) {
                  if (Reference2CharLinkedOpenHashMap.this.containsNullKey && Reference2CharLinkedOpenHashMap.this.value[Reference2CharLinkedOpenHashMap.this.n] == var4) {
                     Reference2CharLinkedOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  Object[] var6 = Reference2CharLinkedOpenHashMap.this.key;
                  Object var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(System.identityHashCode(var3)) & Reference2CharLinkedOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (var5 == var3) {
                     if (Reference2CharLinkedOpenHashMap.this.value[var7] == var4) {
                        Reference2CharLinkedOpenHashMap.this.removeEntry(var7);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((var5 = var6[var7 = var7 + 1 & Reference2CharLinkedOpenHashMap.this.mask]) == null) {
                           return false;
                        }
                     } while(var5 != var3 || Reference2CharLinkedOpenHashMap.this.value[var7] != var4);

                     Reference2CharLinkedOpenHashMap.this.removeEntry(var7);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Reference2CharLinkedOpenHashMap.this.size;
      }

      public void clear() {
         Reference2CharLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Reference2CharMap.Entry<K>> iterator(Reference2CharMap.Entry<K> var1) {
         return Reference2CharLinkedOpenHashMap.this.new EntryIterator(var1.getKey());
      }

      public ObjectListIterator<Reference2CharMap.Entry<K>> fastIterator() {
         return Reference2CharLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Reference2CharMap.Entry<K>> fastIterator(Reference2CharMap.Entry<K> var1) {
         return Reference2CharLinkedOpenHashMap.this.new FastEntryIterator(var1.getKey());
      }

      public void forEach(Consumer<? super Reference2CharMap.Entry<K>> var1) {
         int var2 = Reference2CharLinkedOpenHashMap.this.size;
         int var4 = Reference2CharLinkedOpenHashMap.this.first;

         while(var2-- != 0) {
            int var3 = var4;
            var4 = (int)Reference2CharLinkedOpenHashMap.this.link[var4];
            var1.accept(new AbstractReference2CharMap.BasicEntry(Reference2CharLinkedOpenHashMap.this.key[var3], Reference2CharLinkedOpenHashMap.this.value[var3]));
         }

      }

      public void fastForEach(Consumer<? super Reference2CharMap.Entry<K>> var1) {
         AbstractReference2CharMap.BasicEntry var2 = new AbstractReference2CharMap.BasicEntry();
         int var3 = Reference2CharLinkedOpenHashMap.this.size;
         int var5 = Reference2CharLinkedOpenHashMap.this.first;

         while(var3-- != 0) {
            int var4 = var5;
            var5 = (int)Reference2CharLinkedOpenHashMap.this.link[var5];
            var2.key = Reference2CharLinkedOpenHashMap.this.key[var4];
            var2.value = Reference2CharLinkedOpenHashMap.this.value[var4];
            var1.accept(var2);
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Reference2CharLinkedOpenHashMap<K>.MapIterator implements ObjectListIterator<Reference2CharMap.Entry<K>> {
      final Reference2CharLinkedOpenHashMap<K>.MapEntry entry;

      public FastEntryIterator() {
         super();
         this.entry = Reference2CharLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(K var2) {
         super(var2, null);
         this.entry = Reference2CharLinkedOpenHashMap.this.new MapEntry();
      }

      public Reference2CharLinkedOpenHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Reference2CharLinkedOpenHashMap<K>.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private class EntryIterator extends Reference2CharLinkedOpenHashMap<K>.MapIterator implements ObjectListIterator<Reference2CharMap.Entry<K>> {
      private Reference2CharLinkedOpenHashMap<K>.MapEntry entry;

      public EntryIterator() {
         super();
      }

      public EntryIterator(K var2) {
         super(var2, null);
      }

      public Reference2CharLinkedOpenHashMap<K>.MapEntry next() {
         return this.entry = Reference2CharLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Reference2CharLinkedOpenHashMap<K>.MapEntry previous() {
         return this.entry = Reference2CharLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
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
         this.next = Reference2CharLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(K var2) {
         super();
         this.prev = -1;
         this.next = -1;
         this.curr = -1;
         this.index = -1;
         if (var2 == null) {
            if (Reference2CharLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Reference2CharLinkedOpenHashMap.this.link[Reference2CharLinkedOpenHashMap.this.n];
               this.prev = Reference2CharLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + var2 + " does not belong to this map.");
            }
         } else if (Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.last] == var2) {
            this.prev = Reference2CharLinkedOpenHashMap.this.last;
            this.index = Reference2CharLinkedOpenHashMap.this.size;
         } else {
            for(int var3 = HashCommon.mix(System.identityHashCode(var2)) & Reference2CharLinkedOpenHashMap.this.mask; Reference2CharLinkedOpenHashMap.this.key[var3] != null; var3 = var3 + 1 & Reference2CharLinkedOpenHashMap.this.mask) {
               if (Reference2CharLinkedOpenHashMap.this.key[var3] == var2) {
                  this.next = (int)Reference2CharLinkedOpenHashMap.this.link[var3];
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
               this.index = Reference2CharLinkedOpenHashMap.this.size;
            } else {
               int var1 = Reference2CharLinkedOpenHashMap.this.first;

               for(this.index = 1; var1 != this.prev; ++this.index) {
                  var1 = (int)Reference2CharLinkedOpenHashMap.this.link[var1];
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
            this.next = (int)Reference2CharLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Reference2CharLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
               this.prev = (int)(Reference2CharLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Reference2CharLinkedOpenHashMap.this.link[this.curr];
            }

            --Reference2CharLinkedOpenHashMap.this.size;
            int var10001;
            long[] var6;
            if (this.prev == -1) {
               Reference2CharLinkedOpenHashMap.this.first = this.next;
            } else {
               var6 = Reference2CharLinkedOpenHashMap.this.link;
               var10001 = this.prev;
               var6[var10001] ^= (Reference2CharLinkedOpenHashMap.this.link[this.prev] ^ (long)this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Reference2CharLinkedOpenHashMap.this.last = this.prev;
            } else {
               var6 = Reference2CharLinkedOpenHashMap.this.link;
               var10001 = this.next;
               var6[var10001] ^= (Reference2CharLinkedOpenHashMap.this.link[this.next] ^ ((long)this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int var3 = this.curr;
            this.curr = -1;
            if (var3 == Reference2CharLinkedOpenHashMap.this.n) {
               Reference2CharLinkedOpenHashMap.this.containsNullKey = false;
               Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.n] = null;
            } else {
               Object[] var5 = Reference2CharLinkedOpenHashMap.this.key;

               while(true) {
                  int var1 = var3;
                  var3 = var3 + 1 & Reference2CharLinkedOpenHashMap.this.mask;

                  Object var4;
                  while(true) {
                     if ((var4 = var5[var3]) == null) {
                        var5[var1] = null;
                        return;
                     }

                     int var2 = HashCommon.mix(System.identityHashCode(var4)) & Reference2CharLinkedOpenHashMap.this.mask;
                     if (var1 <= var3) {
                        if (var1 >= var2 || var2 > var3) {
                           break;
                        }
                     } else if (var1 >= var2 && var2 > var3) {
                        break;
                     }

                     var3 = var3 + 1 & Reference2CharLinkedOpenHashMap.this.mask;
                  }

                  var5[var1] = var4;
                  Reference2CharLinkedOpenHashMap.this.value[var1] = Reference2CharLinkedOpenHashMap.this.value[var3];
                  if (this.next == var3) {
                     this.next = var1;
                  }

                  if (this.prev == var3) {
                     this.prev = var1;
                  }

                  Reference2CharLinkedOpenHashMap.this.fixPointers(var3, var1);
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

      public void set(Reference2CharMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Reference2CharMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      MapIterator(Object var2, Object var3) {
         this(var2);
      }
   }

   final class MapEntry implements Reference2CharMap.Entry<K>, java.util.Map.Entry<K, Character> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public K getKey() {
         return Reference2CharLinkedOpenHashMap.this.key[this.index];
      }

      public char getCharValue() {
         return Reference2CharLinkedOpenHashMap.this.value[this.index];
      }

      public char setValue(char var1) {
         char var2 = Reference2CharLinkedOpenHashMap.this.value[this.index];
         Reference2CharLinkedOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Character getValue() {
         return Reference2CharLinkedOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Character setValue(Character var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Reference2CharLinkedOpenHashMap.this.key[this.index] == var2.getKey() && Reference2CharLinkedOpenHashMap.this.value[this.index] == (Character)var2.getValue();
         }
      }

      public int hashCode() {
         return System.identityHashCode(Reference2CharLinkedOpenHashMap.this.key[this.index]) ^ Reference2CharLinkedOpenHashMap.this.value[this.index];
      }

      public String toString() {
         return Reference2CharLinkedOpenHashMap.this.key[this.index] + "=>" + Reference2CharLinkedOpenHashMap.this.value[this.index];
      }
   }
}
