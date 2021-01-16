package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class Int2ReferenceOpenHashMap<V> extends AbstractInt2ReferenceMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient int[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Int2ReferenceMap.FastEntrySet<V> entries;
   protected transient IntSet keys;
   protected transient ReferenceCollection<V> values;

   public Int2ReferenceOpenHashMap(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new int[this.n + 1];
            this.value = new Object[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Int2ReferenceOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Int2ReferenceOpenHashMap() {
      this(16, 0.75F);
   }

   public Int2ReferenceOpenHashMap(Map<? extends Integer, ? extends V> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Int2ReferenceOpenHashMap(Map<? extends Integer, ? extends V> var1) {
      this(var1, 0.75F);
   }

   public Int2ReferenceOpenHashMap(Int2ReferenceMap<V> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Int2ReferenceOpenHashMap(Int2ReferenceMap<V> var1) {
      this(var1, 0.75F);
   }

   public Int2ReferenceOpenHashMap(int[] var1, V[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Int2ReferenceOpenHashMap(int[] var1, V[] var2) {
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
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Integer, ? extends V> var1) {
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

   private void insert(int var1, int var2, V var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public V put(int var1, V var2) {
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
      int[] var5 = this.key;

      while(true) {
         int var2 = var1;
         var1 = var1 + 1 & this.mask;

         int var4;
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
      }
   }

   public V remove(int var1) {
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

   public V get(int var1) {
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

   public boolean containsValue(Object var1) {
      Object[] var2 = this.value;
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

   public V getOrDefault(int var1, V var2) {
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

   public V putIfAbsent(int var1, V var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(int var1, Object var2) {
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

   public boolean replace(int var1, V var2, V var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public V replace(int var1, V var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Object var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public V computeIfAbsent(int var1, IntFunction<? extends V> var2) {
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

   public V computeIfPresent(int var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
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

   public V compute(int var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
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

   public V merge(int var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
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
         Arrays.fill(this.key, 0);
         Arrays.fill(this.value, (Object)null);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Int2ReferenceMap.FastEntrySet<V> int2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2ReferenceOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public IntSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2ReferenceOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Int2ReferenceOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Int2ReferenceOpenHashMap.this.size;
            }

            public boolean contains(Object var1) {
               return Int2ReferenceOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Int2ReferenceOpenHashMap.this.clear();
            }

            public void forEach(Consumer<? super V> var1) {
               if (Int2ReferenceOpenHashMap.this.containsNullKey) {
                  var1.accept(Int2ReferenceOpenHashMap.this.value[Int2ReferenceOpenHashMap.this.n]);
               }

               int var2 = Int2ReferenceOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Int2ReferenceOpenHashMap.this.key[var2] != 0) {
                     var1.accept(Int2ReferenceOpenHashMap.this.value[var2]);
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
      Object[] var3 = this.value;
      int var4 = var1 - 1;
      int[] var5 = new int[var1 + 1];
      Object[] var6 = new Object[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == 0);

         if (var5[var8 = HashCommon.mix(var2[var7]) & var4] != 0) {
            while(var5[var8 = var8 + 1 & var4] != 0) {
            }
         }

         var5[var8] = var2[var7];
      }

      var6[var1] = var3[this.n];
      this.n = var1;
      this.mask = var4;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = var5;
      this.value = var6;
   }

   public Int2ReferenceOpenHashMap<V> clone() {
      Int2ReferenceOpenHashMap var1;
      try {
         var1 = (Int2ReferenceOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (int[])this.key.clone();
      var1.value = (Object[])this.value.clone();
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
            var5 ^= this.value[var3] == null ? 0 : System.identityHashCode(this.value[var3]);
         }

         var1 += var5;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] == null ? 0 : System.identityHashCode(this.value[this.n]);
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      int[] var2 = this.key;
      Object[] var3 = this.value;
      Int2ReferenceOpenHashMap.MapIterator var4 = new Int2ReferenceOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeInt(var2[var6]);
         var1.writeObject(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      int[] var2 = this.key = new int[this.n + 1];
      Object[] var3 = this.value = new Object[this.n + 1];

      Object var5;
      int var7;
      for(int var6 = this.size; var6-- != 0; var3[var7] = var5) {
         int var4 = var1.readInt();
         var5 = var1.readObject();
         if (var4 == 0) {
            var7 = this.n;
            this.containsNullKey = true;
         } else {
            for(var7 = HashCommon.mix(var4) & this.mask; var2[var7] != 0; var7 = var7 + 1 & this.mask) {
            }
         }

         var2[var7] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Int2ReferenceOpenHashMap<V>.MapIterator implements ObjectIterator<V> {
      public ValueIterator() {
         super(null);
      }

      public V next() {
         return Int2ReferenceOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractIntSet {
      private KeySet() {
         super();
      }

      public IntIterator iterator() {
         return Int2ReferenceOpenHashMap.this.new KeyIterator();
      }

      public void forEach(java.util.function.IntConsumer var1) {
         if (Int2ReferenceOpenHashMap.this.containsNullKey) {
            var1.accept(Int2ReferenceOpenHashMap.this.key[Int2ReferenceOpenHashMap.this.n]);
         }

         int var2 = Int2ReferenceOpenHashMap.this.n;

         while(var2-- != 0) {
            int var3 = Int2ReferenceOpenHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Int2ReferenceOpenHashMap.this.size;
      }

      public boolean contains(int var1) {
         return Int2ReferenceOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(int var1) {
         int var2 = Int2ReferenceOpenHashMap.this.size;
         Int2ReferenceOpenHashMap.this.remove(var1);
         return Int2ReferenceOpenHashMap.this.size != var2;
      }

      public void clear() {
         Int2ReferenceOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Int2ReferenceOpenHashMap<V>.MapIterator implements IntIterator {
      public KeyIterator() {
         super(null);
      }

      public int nextInt() {
         return Int2ReferenceOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Int2ReferenceMap.Entry<V>> implements Int2ReferenceMap.FastEntrySet<V> {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Int2ReferenceMap.Entry<V>> iterator() {
         return Int2ReferenceOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Int2ReferenceMap.Entry<V>> fastIterator() {
         return Int2ReferenceOpenHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Integer) {
               int var3 = (Integer)var2.getKey();
               Object var4 = var2.getValue();
               if (var3 == 0) {
                  return Int2ReferenceOpenHashMap.this.containsNullKey && Int2ReferenceOpenHashMap.this.value[Int2ReferenceOpenHashMap.this.n] == var4;
               } else {
                  int[] var6 = Int2ReferenceOpenHashMap.this.key;
                  int var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(var3) & Int2ReferenceOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (var3 == var5) {
                     return Int2ReferenceOpenHashMap.this.value[var7] == var4;
                  } else {
                     while((var5 = var6[var7 = var7 + 1 & Int2ReferenceOpenHashMap.this.mask]) != 0) {
                        if (var3 == var5) {
                           return Int2ReferenceOpenHashMap.this.value[var7] == var4;
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
            if (var2.getKey() != null && var2.getKey() instanceof Integer) {
               int var3 = (Integer)var2.getKey();
               Object var4 = var2.getValue();
               if (var3 == 0) {
                  if (Int2ReferenceOpenHashMap.this.containsNullKey && Int2ReferenceOpenHashMap.this.value[Int2ReferenceOpenHashMap.this.n] == var4) {
                     Int2ReferenceOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  int[] var6 = Int2ReferenceOpenHashMap.this.key;
                  int var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(var3) & Int2ReferenceOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (var5 == var3) {
                     if (Int2ReferenceOpenHashMap.this.value[var7] == var4) {
                        Int2ReferenceOpenHashMap.this.removeEntry(var7);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((var5 = var6[var7 = var7 + 1 & Int2ReferenceOpenHashMap.this.mask]) == 0) {
                           return false;
                        }
                     } while(var5 != var3 || Int2ReferenceOpenHashMap.this.value[var7] != var4);

                     Int2ReferenceOpenHashMap.this.removeEntry(var7);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Int2ReferenceOpenHashMap.this.size;
      }

      public void clear() {
         Int2ReferenceOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Int2ReferenceMap.Entry<V>> var1) {
         if (Int2ReferenceOpenHashMap.this.containsNullKey) {
            var1.accept(new AbstractInt2ReferenceMap.BasicEntry(Int2ReferenceOpenHashMap.this.key[Int2ReferenceOpenHashMap.this.n], Int2ReferenceOpenHashMap.this.value[Int2ReferenceOpenHashMap.this.n]));
         }

         int var2 = Int2ReferenceOpenHashMap.this.n;

         while(var2-- != 0) {
            if (Int2ReferenceOpenHashMap.this.key[var2] != 0) {
               var1.accept(new AbstractInt2ReferenceMap.BasicEntry(Int2ReferenceOpenHashMap.this.key[var2], Int2ReferenceOpenHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Int2ReferenceMap.Entry<V>> var1) {
         AbstractInt2ReferenceMap.BasicEntry var2 = new AbstractInt2ReferenceMap.BasicEntry();
         if (Int2ReferenceOpenHashMap.this.containsNullKey) {
            var2.key = Int2ReferenceOpenHashMap.this.key[Int2ReferenceOpenHashMap.this.n];
            var2.value = Int2ReferenceOpenHashMap.this.value[Int2ReferenceOpenHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Int2ReferenceOpenHashMap.this.n;

         while(var3-- != 0) {
            if (Int2ReferenceOpenHashMap.this.key[var3] != 0) {
               var2.key = Int2ReferenceOpenHashMap.this.key[var3];
               var2.value = Int2ReferenceOpenHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Int2ReferenceOpenHashMap<V>.MapIterator implements ObjectIterator<Int2ReferenceMap.Entry<V>> {
      private final Int2ReferenceOpenHashMap<V>.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Int2ReferenceOpenHashMap.this.new MapEntry();
      }

      public Int2ReferenceOpenHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Int2ReferenceOpenHashMap<V>.MapIterator implements ObjectIterator<Int2ReferenceMap.Entry<V>> {
      private Int2ReferenceOpenHashMap<V>.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Int2ReferenceOpenHashMap<V>.MapEntry next() {
         return this.entry = Int2ReferenceOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public void remove() {
         super.remove();
         this.entry.index = -1;
      }

      // $FF: synthetic method
      EntryIterator(Object var2) {
         this();
      }
   }

   private class MapIterator {
      int pos;
      int last;
      int c;
      boolean mustReturnNullKey;
      IntArrayList wrapped;

      private MapIterator() {
         super();
         this.pos = Int2ReferenceOpenHashMap.this.n;
         this.last = -1;
         this.c = Int2ReferenceOpenHashMap.this.size;
         this.mustReturnNullKey = Int2ReferenceOpenHashMap.this.containsNullKey;
      }

      public boolean hasNext() {
         return this.c != 0;
      }

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            --this.c;
            if (this.mustReturnNullKey) {
               this.mustReturnNullKey = false;
               return this.last = Int2ReferenceOpenHashMap.this.n;
            } else {
               int[] var1 = Int2ReferenceOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               int var2 = this.wrapped.getInt(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(var2) & Int2ReferenceOpenHashMap.this.mask; var2 != var1[var3]; var3 = var3 + 1 & Int2ReferenceOpenHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         int[] var5 = Int2ReferenceOpenHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Int2ReferenceOpenHashMap.this.mask;

            int var4;
            while(true) {
               if ((var4 = var5[var1]) == 0) {
                  var5[var2] = 0;
                  Int2ReferenceOpenHashMap.this.value[var2] = null;
                  return;
               }

               int var3 = HashCommon.mix(var4) & Int2ReferenceOpenHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Int2ReferenceOpenHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new IntArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Int2ReferenceOpenHashMap.this.value[var2] = Int2ReferenceOpenHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Int2ReferenceOpenHashMap.this.n) {
               Int2ReferenceOpenHashMap.this.containsNullKey = false;
               Int2ReferenceOpenHashMap.this.value[Int2ReferenceOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Int2ReferenceOpenHashMap.this.remove(this.wrapped.getInt(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Int2ReferenceOpenHashMap.this.size;
            this.last = -1;
         }
      }

      public int skip(int var1) {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return var1 - var2 - 1;
      }

      // $FF: synthetic method
      MapIterator(Object var2) {
         this();
      }
   }

   final class MapEntry implements Int2ReferenceMap.Entry<V>, java.util.Map.Entry<Integer, V> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public int getIntKey() {
         return Int2ReferenceOpenHashMap.this.key[this.index];
      }

      public V getValue() {
         return Int2ReferenceOpenHashMap.this.value[this.index];
      }

      public V setValue(V var1) {
         Object var2 = Int2ReferenceOpenHashMap.this.value[this.index];
         Int2ReferenceOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Integer getKey() {
         return Int2ReferenceOpenHashMap.this.key[this.index];
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Int2ReferenceOpenHashMap.this.key[this.index] == (Integer)var2.getKey() && Int2ReferenceOpenHashMap.this.value[this.index] == var2.getValue();
         }
      }

      public int hashCode() {
         return Int2ReferenceOpenHashMap.this.key[this.index] ^ (Int2ReferenceOpenHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Int2ReferenceOpenHashMap.this.value[this.index]));
      }

      public String toString() {
         return Int2ReferenceOpenHashMap.this.key[this.index] + "=>" + Int2ReferenceOpenHashMap.this.value[this.index];
      }
   }
}
