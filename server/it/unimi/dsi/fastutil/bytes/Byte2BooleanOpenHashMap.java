package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public class Byte2BooleanOpenHashMap extends AbstractByte2BooleanMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient byte[] key;
   protected transient boolean[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Byte2BooleanMap.FastEntrySet entries;
   protected transient ByteSet keys;
   protected transient BooleanCollection values;

   public Byte2BooleanOpenHashMap(int var1, float var2) {
      super();
      if (var2 > 0.0F && var2 <= 1.0F) {
         if (var1 < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
         } else {
            this.f = var2;
            this.minN = this.n = HashCommon.arraySize(var1, var2);
            this.mask = this.n - 1;
            this.maxFill = HashCommon.maxFill(this.n, var2);
            this.key = new byte[this.n + 1];
            this.value = new boolean[this.n + 1];
         }
      } else {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      }
   }

   public Byte2BooleanOpenHashMap(int var1) {
      this(var1, 0.75F);
   }

   public Byte2BooleanOpenHashMap() {
      this(16, 0.75F);
   }

   public Byte2BooleanOpenHashMap(Map<? extends Byte, ? extends Boolean> var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Byte2BooleanOpenHashMap(Map<? extends Byte, ? extends Boolean> var1) {
      this(var1, 0.75F);
   }

   public Byte2BooleanOpenHashMap(Byte2BooleanMap var1, float var2) {
      this(var1.size(), var2);
      this.putAll(var1);
   }

   public Byte2BooleanOpenHashMap(Byte2BooleanMap var1) {
      this(var1, 0.75F);
   }

   public Byte2BooleanOpenHashMap(byte[] var1, boolean[] var2, float var3) {
      this(var1.length, var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Byte2BooleanOpenHashMap(byte[] var1, boolean[] var2) {
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
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return var1;
   }

   public void putAll(Map<? extends Byte, ? extends Boolean> var1) {
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

   private void insert(int var1, byte var2, boolean var3) {
      if (var1 == this.n) {
         this.containsNullKey = true;
      }

      this.key[var1] = var2;
      this.value[var1] = var3;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }

   public boolean put(byte var1, boolean var2) {
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
      }
   }

   public boolean remove(byte var1) {
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

   public boolean get(byte var1) {
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

   public boolean containsValue(boolean var1) {
      boolean[] var2 = this.value;
      byte[] var3 = this.key;
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

   public boolean getOrDefault(byte var1, boolean var2) {
      if (var1 == 0) {
         return this.containsNullKey ? this.value[this.n] : var2;
      } else {
         byte[] var4 = this.key;
         byte var3;
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

   public boolean putIfAbsent(byte var1, boolean var2) {
      int var3 = this.find(var1);
      if (var3 >= 0) {
         return this.value[var3];
      } else {
         this.insert(-var3 - 1, var1, var2);
         return this.defRetValue;
      }
   }

   public boolean remove(byte var1, boolean var2) {
      if (var1 == 0) {
         if (this.containsNullKey && var2 == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         byte[] var4 = this.key;
         byte var3;
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

   public boolean replace(byte var1, boolean var2, boolean var3) {
      int var4 = this.find(var1);
      if (var4 >= 0 && var2 == this.value[var4]) {
         this.value[var4] = var3;
         return true;
      } else {
         return false;
      }
   }

   public boolean replace(byte var1, boolean var2) {
      int var3 = this.find(var1);
      if (var3 < 0) {
         return this.defRetValue;
      } else {
         boolean var4 = this.value[var3];
         this.value[var3] = var2;
         return var4;
      }
   }

   public boolean computeIfAbsent(byte var1, IntPredicate var2) {
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

   public boolean computeIfAbsentNullable(byte var1, IntFunction<? extends Boolean> var2) {
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

   public boolean computeIfPresent(byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
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

   public boolean compute(byte var1, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> var2) {
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

   public boolean merge(byte var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
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
         Arrays.fill(this.key, (byte)0);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public Byte2BooleanMap.FastEntrySet byte2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Byte2BooleanOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   public ByteSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2BooleanOpenHashMap.KeySet();
      }

      return this.keys;
   }

   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new AbstractBooleanCollection() {
            public BooleanIterator iterator() {
               return Byte2BooleanOpenHashMap.this.new ValueIterator();
            }

            public int size() {
               return Byte2BooleanOpenHashMap.this.size;
            }

            public boolean contains(boolean var1) {
               return Byte2BooleanOpenHashMap.this.containsValue(var1);
            }

            public void clear() {
               Byte2BooleanOpenHashMap.this.clear();
            }

            public void forEach(BooleanConsumer var1) {
               if (Byte2BooleanOpenHashMap.this.containsNullKey) {
                  var1.accept(Byte2BooleanOpenHashMap.this.value[Byte2BooleanOpenHashMap.this.n]);
               }

               int var2 = Byte2BooleanOpenHashMap.this.n;

               while(var2-- != 0) {
                  if (Byte2BooleanOpenHashMap.this.key[var2] != 0) {
                     var1.accept(Byte2BooleanOpenHashMap.this.value[var2]);
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
      boolean[] var3 = this.value;
      int var4 = var1 - 1;
      byte[] var5 = new byte[var1 + 1];
      boolean[] var6 = new boolean[var1 + 1];
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

   public Byte2BooleanOpenHashMap clone() {
      Byte2BooleanOpenHashMap var1;
      try {
         var1 = (Byte2BooleanOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.containsNullKey = this.containsNullKey;
      var1.key = (byte[])this.key.clone();
      var1.value = (boolean[])this.value.clone();
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
         int var6 = var5 ^ (this.value[var3] ? 1231 : 1237);
         var1 += var6;
      }

      if (this.containsNullKey) {
         var1 += this.value[this.n] ? 1231 : 1237;
      }

      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      byte[] var2 = this.key;
      boolean[] var3 = this.value;
      Byte2BooleanOpenHashMap.MapIterator var4 = new Byte2BooleanOpenHashMap.MapIterator();
      var1.defaultWriteObject();
      int var5 = this.size;

      while(var5-- != 0) {
         int var6 = var4.nextEntry();
         var1.writeByte(var2[var6]);
         var1.writeBoolean(var3[var6]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      byte[] var2 = this.key = new byte[this.n + 1];
      boolean[] var3 = this.value = new boolean[this.n + 1];

      boolean var5;
      int var7;
      for(int var6 = this.size; var6-- != 0; var3[var7] = var5) {
         byte var4 = var1.readByte();
         var5 = var1.readBoolean();
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

   private final class ValueIterator extends Byte2BooleanOpenHashMap.MapIterator implements BooleanIterator {
      public ValueIterator() {
         super(null);
      }

      public boolean nextBoolean() {
         return Byte2BooleanOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractByteSet {
      private KeySet() {
         super();
      }

      public ByteIterator iterator() {
         return Byte2BooleanOpenHashMap.this.new KeyIterator();
      }

      public void forEach(IntConsumer var1) {
         if (Byte2BooleanOpenHashMap.this.containsNullKey) {
            var1.accept(Byte2BooleanOpenHashMap.this.key[Byte2BooleanOpenHashMap.this.n]);
         }

         int var2 = Byte2BooleanOpenHashMap.this.n;

         while(var2-- != 0) {
            byte var3 = Byte2BooleanOpenHashMap.this.key[var2];
            if (var3 != 0) {
               var1.accept(var3);
            }
         }

      }

      public int size() {
         return Byte2BooleanOpenHashMap.this.size;
      }

      public boolean contains(byte var1) {
         return Byte2BooleanOpenHashMap.this.containsKey(var1);
      }

      public boolean remove(byte var1) {
         int var2 = Byte2BooleanOpenHashMap.this.size;
         Byte2BooleanOpenHashMap.this.remove(var1);
         return Byte2BooleanOpenHashMap.this.size != var2;
      }

      public void clear() {
         Byte2BooleanOpenHashMap.this.clear();
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Byte2BooleanOpenHashMap.MapIterator implements ByteIterator {
      public KeyIterator() {
         super(null);
      }

      public byte nextByte() {
         return Byte2BooleanOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Byte2BooleanMap.Entry> implements Byte2BooleanMap.FastEntrySet {
      private MapEntrySet() {
         super();
      }

      public ObjectIterator<Byte2BooleanMap.Entry> iterator() {
         return Byte2BooleanOpenHashMap.this.new EntryIterator();
      }

      public ObjectIterator<Byte2BooleanMap.Entry> fastIterator() {
         return Byte2BooleanOpenHashMap.this.new FastEntryIterator();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Byte) {
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  byte var3 = (Byte)var2.getKey();
                  boolean var4 = (Boolean)var2.getValue();
                  if (var3 == 0) {
                     return Byte2BooleanOpenHashMap.this.containsNullKey && Byte2BooleanOpenHashMap.this.value[Byte2BooleanOpenHashMap.this.n] == var4;
                  } else {
                     byte[] var6 = Byte2BooleanOpenHashMap.this.key;
                     byte var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(var3) & Byte2BooleanOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var3 == var5) {
                        return Byte2BooleanOpenHashMap.this.value[var7] == var4;
                     } else {
                        while((var5 = var6[var7 = var7 + 1 & Byte2BooleanOpenHashMap.this.mask]) != 0) {
                           if (var3 == var5) {
                              return Byte2BooleanOpenHashMap.this.value[var7] == var4;
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
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  byte var3 = (Byte)var2.getKey();
                  boolean var4 = (Boolean)var2.getValue();
                  if (var3 == 0) {
                     if (Byte2BooleanOpenHashMap.this.containsNullKey && Byte2BooleanOpenHashMap.this.value[Byte2BooleanOpenHashMap.this.n] == var4) {
                        Byte2BooleanOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     byte[] var6 = Byte2BooleanOpenHashMap.this.key;
                     byte var5;
                     int var7;
                     if ((var5 = var6[var7 = HashCommon.mix(var3) & Byte2BooleanOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (var5 == var3) {
                        if (Byte2BooleanOpenHashMap.this.value[var7] == var4) {
                           Byte2BooleanOpenHashMap.this.removeEntry(var7);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        do {
                           if ((var5 = var6[var7 = var7 + 1 & Byte2BooleanOpenHashMap.this.mask]) == 0) {
                              return false;
                           }
                        } while(var5 != var3 || Byte2BooleanOpenHashMap.this.value[var7] != var4);

                        Byte2BooleanOpenHashMap.this.removeEntry(var7);
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
         return Byte2BooleanOpenHashMap.this.size;
      }

      public void clear() {
         Byte2BooleanOpenHashMap.this.clear();
      }

      public void forEach(Consumer<? super Byte2BooleanMap.Entry> var1) {
         if (Byte2BooleanOpenHashMap.this.containsNullKey) {
            var1.accept(new AbstractByte2BooleanMap.BasicEntry(Byte2BooleanOpenHashMap.this.key[Byte2BooleanOpenHashMap.this.n], Byte2BooleanOpenHashMap.this.value[Byte2BooleanOpenHashMap.this.n]));
         }

         int var2 = Byte2BooleanOpenHashMap.this.n;

         while(var2-- != 0) {
            if (Byte2BooleanOpenHashMap.this.key[var2] != 0) {
               var1.accept(new AbstractByte2BooleanMap.BasicEntry(Byte2BooleanOpenHashMap.this.key[var2], Byte2BooleanOpenHashMap.this.value[var2]));
            }
         }

      }

      public void fastForEach(Consumer<? super Byte2BooleanMap.Entry> var1) {
         AbstractByte2BooleanMap.BasicEntry var2 = new AbstractByte2BooleanMap.BasicEntry();
         if (Byte2BooleanOpenHashMap.this.containsNullKey) {
            var2.key = Byte2BooleanOpenHashMap.this.key[Byte2BooleanOpenHashMap.this.n];
            var2.value = Byte2BooleanOpenHashMap.this.value[Byte2BooleanOpenHashMap.this.n];
            var1.accept(var2);
         }

         int var3 = Byte2BooleanOpenHashMap.this.n;

         while(var3-- != 0) {
            if (Byte2BooleanOpenHashMap.this.key[var3] != 0) {
               var2.key = Byte2BooleanOpenHashMap.this.key[var3];
               var2.value = Byte2BooleanOpenHashMap.this.value[var3];
               var1.accept(var2);
            }
         }

      }

      // $FF: synthetic method
      MapEntrySet(Object var2) {
         this();
      }
   }

   private class FastEntryIterator extends Byte2BooleanOpenHashMap.MapIterator implements ObjectIterator<Byte2BooleanMap.Entry> {
      private final Byte2BooleanOpenHashMap.MapEntry entry;

      private FastEntryIterator() {
         super(null);
         this.entry = Byte2BooleanOpenHashMap.this.new MapEntry();
      }

      public Byte2BooleanOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      // $FF: synthetic method
      FastEntryIterator(Object var2) {
         this();
      }
   }

   private class EntryIterator extends Byte2BooleanOpenHashMap.MapIterator implements ObjectIterator<Byte2BooleanMap.Entry> {
      private Byte2BooleanOpenHashMap.MapEntry entry;

      private EntryIterator() {
         super(null);
      }

      public Byte2BooleanOpenHashMap.MapEntry next() {
         return this.entry = Byte2BooleanOpenHashMap.this.new MapEntry(this.nextEntry());
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
      ByteArrayList wrapped;

      private MapIterator() {
         super();
         this.pos = Byte2BooleanOpenHashMap.this.n;
         this.last = -1;
         this.c = Byte2BooleanOpenHashMap.this.size;
         this.mustReturnNullKey = Byte2BooleanOpenHashMap.this.containsNullKey;
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
               return this.last = Byte2BooleanOpenHashMap.this.n;
            } else {
               byte[] var1 = Byte2BooleanOpenHashMap.this.key;

               while(--this.pos >= 0) {
                  if (var1[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = -2147483648;
               byte var2 = this.wrapped.getByte(-this.pos - 1);

               int var3;
               for(var3 = HashCommon.mix(var2) & Byte2BooleanOpenHashMap.this.mask; var2 != var1[var3]; var3 = var3 + 1 & Byte2BooleanOpenHashMap.this.mask) {
               }

               return var3;
            }
         }
      }

      private void shiftKeys(int var1) {
         byte[] var5 = Byte2BooleanOpenHashMap.this.key;

         while(true) {
            int var2 = var1;
            var1 = var1 + 1 & Byte2BooleanOpenHashMap.this.mask;

            byte var4;
            while(true) {
               if ((var4 = var5[var1]) == 0) {
                  var5[var2] = 0;
                  return;
               }

               int var3 = HashCommon.mix(var4) & Byte2BooleanOpenHashMap.this.mask;
               if (var2 <= var1) {
                  if (var2 >= var3 || var3 > var1) {
                     break;
                  }
               } else if (var2 >= var3 && var3 > var1) {
                  break;
               }

               var1 = var1 + 1 & Byte2BooleanOpenHashMap.this.mask;
            }

            if (var1 < var2) {
               if (this.wrapped == null) {
                  this.wrapped = new ByteArrayList(2);
               }

               this.wrapped.add(var5[var1]);
            }

            var5[var2] = var4;
            Byte2BooleanOpenHashMap.this.value[var2] = Byte2BooleanOpenHashMap.this.value[var1];
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Byte2BooleanOpenHashMap.this.n) {
               Byte2BooleanOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Byte2BooleanOpenHashMap.this.remove(this.wrapped.getByte(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            --Byte2BooleanOpenHashMap.this.size;
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

   final class MapEntry implements Byte2BooleanMap.Entry, java.util.Map.Entry<Byte, Boolean> {
      int index;

      MapEntry(int var2) {
         super();
         this.index = var2;
      }

      MapEntry() {
         super();
      }

      public byte getByteKey() {
         return Byte2BooleanOpenHashMap.this.key[this.index];
      }

      public boolean getBooleanValue() {
         return Byte2BooleanOpenHashMap.this.value[this.index];
      }

      public boolean setValue(boolean var1) {
         boolean var2 = Byte2BooleanOpenHashMap.this.value[this.index];
         Byte2BooleanOpenHashMap.this.value[this.index] = var1;
         return var2;
      }

      /** @deprecated */
      @Deprecated
      public Byte getKey() {
         return Byte2BooleanOpenHashMap.this.key[this.index];
      }

      /** @deprecated */
      @Deprecated
      public Boolean getValue() {
         return Byte2BooleanOpenHashMap.this.value[this.index];
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
            return Byte2BooleanOpenHashMap.this.key[this.index] == (Byte)var2.getKey() && Byte2BooleanOpenHashMap.this.value[this.index] == (Boolean)var2.getValue();
         }
      }

      public int hashCode() {
         return Byte2BooleanOpenHashMap.this.key[this.index] ^ (Byte2BooleanOpenHashMap.this.value[this.index] ? 1231 : 1237);
      }

      public String toString() {
         return Byte2BooleanOpenHashMap.this.key[this.index] + "=>" + Byte2BooleanOpenHashMap.this.value[this.index];
      }
   }
}
