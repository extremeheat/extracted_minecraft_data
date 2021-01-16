package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
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
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;

public class Reference2ShortOpenHashMap<K> extends AbstractReference2ShortMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient short[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Reference2ShortMap.FastEntrySet<K> entries;
   protected transient ReferenceSet<K> keys;
   protected transient ShortCollection values;

   public Reference2ShortOpenHashMap(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new Object[this.n + 1];
            this.value = new short[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Reference2ShortOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Reference2ShortOpenHashMap() {
      this(16, 0.75F);
   }

   public Reference2ShortOpenHashMap(Map<? extends K, ? extends Short> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Reference2ShortOpenHashMap(Map<? extends K, ? extends Short> var1) {
      this(var1, 0.75F);
   }

   public Reference2ShortOpenHashMap(Reference2ShortMap<K> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Reference2ShortOpenHashMap(Reference2ShortMap<K> var1) {
      this(var1, 0.75F);
   }

   public Reference2ShortOpenHashMap(K[] var1, short[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Reference2ShortOpenHashMap(K[] var1, short[] var2) {
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

   private short removeEntry(int var1) {
      short var2 = this.value[var1];
      --this.size;
      this.shiftKeys(var1);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var2;
   }

   private short removeNullEntry() {
      this.containsNullKey = false;
      this.key[this.n] = null;
      short var1 = this.value[this.n];
      --this.size;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends K, ? extends Short> var1) {
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

   private void insert(int var1, K var2, short var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public short put(K var1, short var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      } else {
         short var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   private short addToValue(int var1, short var2) {
      short var3 = this.value[var1];
      this.value[var1] = (short)(var3 + var2);
      return var3;
   }

   public short addTo(K var1, short var2) {
      int var3;
      if (var1 == null) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, var2);
         }

         var3 = this.n;
         this.containsNullKey = true;
      } else {
         Object[] var5 = this.key;
         Object var4;
         if ((var4 = var5[var3 = HashCommon.mix(System.identityHashCode(var1)) & this.mask]) != null) {
            if (var4 == var1) {
               return this.addToValue(var3, var2);
            }

            while((var4 = var5[var3 = var3 + 1 & this.mask]) != null) {
               if (var4 == var1) {
                  return this.addToValue(var3, var2);
               }
            }
         }
      }

      this.key[var3] = var1;
      this.value[var3] = (short)(this.defRetValue + var2);
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
      }
   }

   public short removeShort(Object var1) {
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

   public short getShort(Object var1) {
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

   public boolean containsValue(short var1) {
      short[] var2 = this.value;
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

   public short getOrDefault(Object var1, short var2) {
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

   public short putIfAbsent(K var1, short var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(Object var1, short var2) {
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

   public boolean replace(K var1, short var2, short var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public short replace(K var1, short var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         short var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public short computeShortIfAbsent(K var1, ToIntFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         short var4 = SafeMath.safeIntToShort(var2.applyAsInt(var1));
         this.insert(-var3 - 1, var1, var4);
         return var4;
      }
   }

   public short computeShortIfPresent(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         Short var4 = (Short)var2.apply(var1, this.value[var3]);
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

   public short computeShort(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.find(var1);
      Short var4 = (Short)var2.apply(var1, var3 >= 0 ? this.value[var3] : null);
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
         short var5 = var4;
         if (var3 < 0) {
            this.insert(-var3 - 1, var1, var5);
            return var5;
         } else {
            return this.value[var3] = var5;
         }
      }
   }

   public short mergeShort(K var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.find(var1);
      if (var4 < 0) {
         this.insert(-var4 - 1, var1, var2);
         return var2;
      } else {
         Short var5 = (Short)var3.apply(this.value[var4], var2);
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
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Reference2ShortMap.FastEntrySet<K> reference2ShortEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2ShortOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ReferenceSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2ShortOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public ShortCollection values() {
      if (this.values == null) {
         this.values = new AbstractShortCollection() {
            public ShortIterator iterator() {
               return Reference2ShortOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Reference2ShortOpenHashMap.this.size;
            }

            public boolean contains(short var1) {
               return Reference2ShortOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Reference2ShortOpenHashMap.this.clear();
            }

            public void forEach(IntConsumer var1) {
               if (Reference2ShortOpenHashMap.this.containsNullKey) {
                  var1.accept(Reference2ShortOpenHashMap.this.value[Reference2ShortOpenHashMap.this.n]);
               }

               int var2 = Reference2ShortOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Reference2ShortOpenHashMap.this.key[var2] != null) {
                     var1.accept(Reference2ShortOpenHashMap.this.value[var2]);
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
      short[] var3 = this.value;
      int var4 = var1 - 1;
      Object[] var5 = new Object[var1 + 1];
      short[] var6 = new short[var1 + 1];
      int var7 = this.n;

      int var8;
      for(int var9 = this.realSize(); var9-- != 0; var6[var8] = var3[var7]) {
         do {
            --var7;
         } while(var2[var7] == null);

         if (var5[var8 = HashCommon.mix(System.identityHashCode(var2[var7])) & var4] != null) {
            while(var5[var8 = var8 + 1 & var4] != null) {
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

   public Reference2ShortOpenHashMap<K> clone() {
      Reference2ShortOpenHashMap var1;
      try {
         var1 = (Reference2ShortOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (Object[])this.key.clone();
      var1.value = (short[])this.value.clone();
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
      short[] var3 = this.value;
      Reference2ShortOpenHashMap.MapIterator var4 = new Reference2ShortOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeObject(var2[var6]);
         var1.writeShort(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      Object[] var2 = this.key = new Object[this.n + 1];
      short[] var3 = this.value = new short[this.n + 1];

      short var5;
      int var7;
      for(int var6 = this.size; var6-- != 0; var3[var7] = var5) {
         Object var4 = var1.readObject();
         var5 = var1.readShort();
         if (var4 == null) {
            var7 = this.n;
            this.containsNullKey = true;
         } else {
            for(var7 = HashCommon.mix(System.identityHashCode(var4)) & this.mask; var2[var7] != null; var7 = var7 + 1 & this.mask) {
            }
         }

         var2[var7] = var4;
      }

   }

   private void checkTable() {
   }

   private final class ValueIterator extends Reference2ShortOpenHashMap<K>.MapIterator implements ShortIterator {
      public ValueIterator() {
         super(null);
      }

      public short nextShort() {
         return Reference2ShortOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
         super();
      }

      public ObjectIterator<K> iterator() {
         return Reference2ShortOpenHashMap.this.new KeyIterator();
      }

      public void forEach(Consumer<? super K> var1) {
         if (Reference2ShortOpenHashMap.this.containsNullKey) {
            var1.accept(Reference2ShortOpenHashMap.this.key[Reference2ShortOpenHashMap.this.n]);
         }

         int var2 = Reference2ShortOpenHashMap.this.n;

         while(var2-- != 0) {
            Object var3 = Reference2ShortOpenHashMap.this.key[var2];
            if (var3 != null) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Reference2ShortOpenHashMap.this.size;
      }

      public boolean contains(Object var1) {
         return Reference2ShortOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         int var2 = Reference2ShortOpenHashMap.this.size;
         Reference2ShortOpenHashMap.this.removeShort(var1);
         return Reference2ShortOpenHashMap.this.size != var2;
      }

      public void clear() {
         Reference2ShortOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Reference2ShortOpenHashMap<K>.MapIterator implements ObjectIterator<K> {
      public KeyIterator() {
         super(null);
      }

      public K next() {
         return Reference2ShortOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Reference2ShortMap.Entry<K>> implements Reference2ShortMap.FastEntrySet<K> {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Reference2ShortMap.Entry<K>> iterator() {
         return Reference2ShortOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Reference2ShortMap.Entry<K>> fastIterator() {
         return Reference2ShortOpenHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getValue() != null && var2.getValue() instanceof Short) {
               Object var3 = var2.getKey();
               short var4 = (Short)var2.getValue();
               if (var3 == null) {
                  return Reference2ShortOpenHashMap.this.containsNullKey && Reference2ShortOpenHashMap.this.value[Reference2ShortOpenHashMap.this.n] == var4;
               } else {
                  Object[] var6 = Reference2ShortOpenHashMap.this.key;
                  Object var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(System.identityHashCode(var3)) & Reference2ShortOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (var3 == var5) {
                     return Reference2ShortOpenHashMap.this.value[var7] == var4;
                  } else {
                     while((var5 = var6[var7 = var7 + 1 & Reference2ShortOpenHashMap.this.mask]) != null) {
                        if (var3 == var5) {
                           return Reference2ShortOpenHashMap.this.value[var7] == var4;
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
            if (var2.getValue() != null && var2.getValue() instanceof Short) {
               Object var3 = var2.getKey();
               short var4 = (Short)var2.getValue();
               if (var3 == null) {
                  if (Reference2ShortOpenHashMap.this.containsNullKey && Reference2ShortOpenHashMap.this.value[Reference2ShortOpenHashMap.this.n] == var4) {
                     Reference2ShortOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  Object[] var6 = Reference2ShortOpenHashMap.this.key;
                  Object var5;
                  int var7;
                  if ((var5 = var6[var7 = HashCommon.mix(System.identityHashCode(var3)) & Reference2ShortOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (var5 == var3) {
                     if (Reference2ShortOpenHashMap.this.value[var7] == var4) {
                        Reference2ShortOpenHashMap.this.removeEntry(var7);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     do {
                        if ((var5 = var6[var7 = var7 + 1 & Reference2ShortOpenHashMap.this.mask]) == null) {
                           return false;
                        }
                     } while(var5 != var3 || Reference2ShortOpenHashMap.this.value[var7] != var4);

                     Reference2ShortOpenHashMap.this.removeEntry(var7);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return Reference2ShortOpenHashMap.this.size;
      }

      public void clear() {
         Reference2ShortOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Reference2ShortMap.Entry<K>> var1) {
         if (Reference2ShortOpenHashMap.this.containsNullKey) {
            var1.accept(new AbstractReference2ShortMap.BasicEntry(Reference2ShortOpenHashMap.this.key[Reference2ShortOpenHashMap.this.n], Reference2ShortOpenHashMap.this.value[Reference2ShortOpenHashMap.this.n]));
         }

         int var2 = Reference2ShortOpenHashMap.this.n;

         while(var2-- != 0) {
            if (Reference2ShortOpenHashMap.this.key[var2] != null) {
               var1.accept(new AbstractReference2ShortMap.BasicEntry(Reference2ShortOpenHashMap.this.key[var2], Reference2ShortOpenHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Reference2ShortMap.Entry<K>> var1) {
         AbstractReference2ShortMap.BasicEntry var2 = new AbstractReference2ShortMap.BasicEntry();
         if (Reference2ShortOpenHashMap.this.containsNullKey) {
            var2.key = Reference2ShortOpenHashMap.this.key[Reference2ShortOpenHashMap.this.n];
            var2.value = Reference2ShortOpenHashMap.this.value[Reference2ShortOpenHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Reference2ShortOpenHashMap.this.n;

         while(var3-- != 0) {
            if (Reference2ShortOpenHashMap.this.key[var3] != null) {
               var2.key = Reference2ShortOpenHashMap.this.key[var3];
               var2.value = Reference2ShortOpenHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Reference2ShortOpenHashMap<K>.MapIterator implements ObjectIterator<Reference2ShortMap.Entry<K>> {
      private final Reference2ShortOpenHashMap<K>.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Reference2ShortOpenHashMap.this.new MapEntry();
      }

      public Reference2ShortOpenHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Reference2ShortOpenHashMap<K>.MapIterator implements ObjectIterator<Reference2ShortMap.Entry<K>> {
      private Reference2ShortOpenHashMap<K>.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Reference2ShortOpenHashMap<K>.MapEntry next() {
         return this.entry = Reference2ShortOpenHashMap.this.new MapEntry(this.nextEntry());
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
      ReferenceArrayList<K> wrapped;

      private MapIterator() {
         super();
         this.pos = Reference2ShortOpenHashMap.this.n;
         this.last = -1;
         this.c = Reference2ShortOpenHashMap.this.size;
         this.mustReturnNullKey = Reference2ShortOpenHashMap.this.containsNullKey;
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
               return this.last = Reference2ShortOpenHashMap.this.n;
            } else {
               Object[] var1 = Reference2ShortOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != null) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               Object var2 = this.wrapped.get(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(System.identityHashCode(var2)) & Reference2ShortOpenHashMap.this.mask; var2 != var1[var3]; var3 = var3 + 1 & Reference2ShortOpenHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         Object[] var5 = Reference2ShortOpenHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Reference2ShortOpenHashMap.this.mask;

            Object var4;
            while(true) {
               if ((var4 = var5[var1]) == null) {
                  var5[var2] = null;
                  return;
               }

               int var3 = HashCommon.mix(System.identityHashCode(var4)) & Reference2ShortOpenHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Reference2ShortOpenHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new ReferenceArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Reference2ShortOpenHashMap.this.value[var2] = Reference2ShortOpenHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Reference2ShortOpenHashMap.this.n) {
               Reference2ShortOpenHashMap.this.containsNullKey = false;
               Reference2ShortOpenHashMap.this.key[Reference2ShortOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Reference2ShortOpenHashMap.this.removeShort(this.wrapped.set(-this.pos - 1, (Object)null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Reference2ShortOpenHashMap.this.size;
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

   final class MapEntry implements Reference2ShortMap.Entry<K>, java.util.Map.Entry<K, Short> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public K getKey() {
         return Reference2ShortOpenHashMap.this.key[this.index];
      }

      public short getShortValue() {
         return Reference2ShortOpenHashMap.this.value[this.index];
      }

      public short setValue(short var1) {
         short var2 = Reference2ShortOpenHashMap.this.value[this.index];
         Reference2ShortOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Short getValue() {
         return Reference2ShortOpenHashMap.this.value[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Short setValue(Short var1) {
         return this.setValue(var1);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return Reference2ShortOpenHashMap.this.key[this.index] == var2.getKey() && Reference2ShortOpenHashMap.this.value[this.index] == (Short)var2.getValue();
         }
      }

      public int hashCode() {
         return System.identityHashCode(Reference2ShortOpenHashMap.this.key[this.index]) ^ Reference2ShortOpenHashMap.this.value[this.index];
      }

      public String toString() {
         return Reference2ShortOpenHashMap.this.key[this.index] + "=>" + Reference2ShortOpenHashMap.this.value[this.index];
      }
   }
}
