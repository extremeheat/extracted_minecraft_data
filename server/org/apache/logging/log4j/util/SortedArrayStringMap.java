package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.MarshalledObject;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.status.StatusLogger;

public class SortedArrayStringMap implements IndexedStringMap {
   private static final int DEFAULT_INITIAL_CAPACITY = 4;
   private static final long serialVersionUID = -5748905872274478116L;
   private static final int HASHVAL = 31;
   private static final TriConsumer<String, Object, StringMap> PUT_ALL = new TriConsumer<String, Object, StringMap>() {
      public void accept(String var1, Object var2, StringMap var3) {
         var3.putValue(var1, var2);
      }
   };
   private static final String[] EMPTY = new String[0];
   private static final String FROZEN = "Frozen collection cannot be modified";
   private transient String[] keys;
   private transient Object[] values;
   private transient int size;
   private int threshold;
   private boolean immutable;
   private transient boolean iterating;

   public SortedArrayStringMap() {
      this(4);
   }

   public SortedArrayStringMap(int var1) {
      super();
      this.keys = EMPTY;
      this.values = EMPTY;
      if (var1 < 1) {
         throw new IllegalArgumentException("Initial capacity must be at least one but was " + var1);
      } else {
         this.threshold = ceilingNextPowerOfTwo(var1);
      }
   }

   public SortedArrayStringMap(ReadOnlyStringMap var1) {
      super();
      this.keys = EMPTY;
      this.values = EMPTY;
      if (var1 instanceof SortedArrayStringMap) {
         this.initFrom0((SortedArrayStringMap)var1);
      } else if (var1 != null) {
         this.resize(ceilingNextPowerOfTwo(var1.size()));
         var1.forEach(PUT_ALL, this);
      }

   }

   public SortedArrayStringMap(Map<String, ?> var1) {
      super();
      this.keys = EMPTY;
      this.values = EMPTY;
      this.resize(ceilingNextPowerOfTwo(var1.size()));
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.putValue((String)var3.getKey(), var3.getValue());
      }

   }

   private void assertNotFrozen() {
      if (this.immutable) {
         throw new UnsupportedOperationException("Frozen collection cannot be modified");
      }
   }

   private void assertNoConcurrentModification() {
      if (this.iterating) {
         throw new ConcurrentModificationException();
      }
   }

   public void clear() {
      if (this.keys != EMPTY) {
         this.assertNotFrozen();
         this.assertNoConcurrentModification();
         Arrays.fill(this.keys, 0, this.size, (Object)null);
         Arrays.fill(this.values, 0, this.size, (Object)null);
         this.size = 0;
      }
   }

   public boolean containsKey(String var1) {
      return this.indexOfKey(var1) >= 0;
   }

   public Map<String, String> toMap() {
      HashMap var1 = new HashMap(this.size());

      for(int var2 = 0; var2 < this.size(); ++var2) {
         Object var3 = this.getValueAt(var2);
         var1.put(this.getKeyAt(var2), var3 == null ? null : String.valueOf(var3));
      }

      return var1;
   }

   public void freeze() {
      this.immutable = true;
   }

   public boolean isFrozen() {
      return this.immutable;
   }

   public <V> V getValue(String var1) {
      int var2 = this.indexOfKey(var1);
      return var2 < 0 ? null : this.values[var2];
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public int indexOfKey(String var1) {
      if (this.keys == EMPTY) {
         return -1;
      } else if (var1 == null) {
         return this.nullKeyIndex();
      } else {
         int var2 = this.size > 0 && this.keys[0] == null ? 1 : 0;
         return Arrays.binarySearch(this.keys, var2, this.size, var1);
      }
   }

   private int nullKeyIndex() {
      return this.size > 0 && this.keys[0] == null ? 0 : -1;
   }

   public void putValue(String var1, Object var2) {
      this.assertNotFrozen();
      this.assertNoConcurrentModification();
      if (this.keys == EMPTY) {
         this.inflateTable(this.threshold);
      }

      int var3 = this.indexOfKey(var1);
      if (var3 >= 0) {
         this.keys[var3] = var1;
         this.values[var3] = var2;
      } else {
         this.insertAt(~var3, var1, var2);
      }

   }

   private void insertAt(int var1, String var2, Object var3) {
      this.ensureCapacity();
      System.arraycopy(this.keys, var1, this.keys, var1 + 1, this.size - var1);
      System.arraycopy(this.values, var1, this.values, var1 + 1, this.size - var1);
      this.keys[var1] = var2;
      this.values[var1] = var3;
      ++this.size;
   }

   public void putAll(ReadOnlyStringMap var1) {
      if (var1 != this && var1 != null && !var1.isEmpty()) {
         this.assertNotFrozen();
         this.assertNoConcurrentModification();
         if (var1 instanceof SortedArrayStringMap) {
            if (this.size == 0) {
               this.initFrom0((SortedArrayStringMap)var1);
            } else {
               this.merge((SortedArrayStringMap)var1);
            }
         } else if (var1 != null) {
            var1.forEach(PUT_ALL, this);
         }

      }
   }

   private void initFrom0(SortedArrayStringMap var1) {
      if (this.keys.length < var1.size) {
         this.keys = new String[var1.threshold];
         this.values = new Object[var1.threshold];
      }

      System.arraycopy(var1.keys, 0, this.keys, 0, var1.size);
      System.arraycopy(var1.values, 0, this.values, 0, var1.size);
      this.size = var1.size;
      this.threshold = var1.threshold;
   }

   private void merge(SortedArrayStringMap var1) {
      String[] var2 = this.keys;
      Object[] var3 = this.values;
      int var4 = var1.size + this.size;
      this.threshold = ceilingNextPowerOfTwo(var4);
      if (this.keys.length < this.threshold) {
         this.keys = new String[this.threshold];
         this.values = new Object[this.threshold];
      }

      boolean var5 = true;
      if (var1.size() > this.size()) {
         System.arraycopy(var2, 0, this.keys, var1.size, this.size);
         System.arraycopy(var3, 0, this.values, var1.size, this.size);
         System.arraycopy(var1.keys, 0, this.keys, 0, var1.size);
         System.arraycopy(var1.values, 0, this.values, 0, var1.size);
         this.size = var1.size;
         var5 = false;
      } else {
         System.arraycopy(var2, 0, this.keys, 0, this.size);
         System.arraycopy(var3, 0, this.values, 0, this.size);
         System.arraycopy(var1.keys, 0, this.keys, this.size, var1.size);
         System.arraycopy(var1.values, 0, this.values, this.size, var1.size);
      }

      for(int var6 = this.size; var6 < var4; ++var6) {
         int var7 = this.indexOfKey(this.keys[var6]);
         if (var7 < 0) {
            this.insertAt(~var7, this.keys[var6], this.values[var6]);
         } else if (var5) {
            this.keys[var7] = this.keys[var6];
            this.values[var7] = this.values[var6];
         }
      }

      Arrays.fill(this.keys, this.size, var4, (Object)null);
      Arrays.fill(this.values, this.size, var4, (Object)null);
   }

   private void ensureCapacity() {
      if (this.size >= this.threshold) {
         this.resize(this.threshold * 2);
      }

   }

   private void resize(int var1) {
      String[] var2 = this.keys;
      Object[] var3 = this.values;
      this.keys = new String[var1];
      this.values = new Object[var1];
      System.arraycopy(var2, 0, this.keys, 0, this.size);
      System.arraycopy(var3, 0, this.values, 0, this.size);
      this.threshold = var1;
   }

   private void inflateTable(int var1) {
      this.threshold = var1;
      this.keys = new String[var1];
      this.values = new Object[var1];
   }

   public void remove(String var1) {
      if (this.keys != EMPTY) {
         int var2 = this.indexOfKey(var1);
         if (var2 >= 0) {
            this.assertNotFrozen();
            this.assertNoConcurrentModification();
            System.arraycopy(this.keys, var2 + 1, this.keys, var2, this.size - 1 - var2);
            System.arraycopy(this.values, var2 + 1, this.values, var2, this.size - 1 - var2);
            this.keys[this.size - 1] = null;
            this.values[this.size - 1] = null;
            --this.size;
         }

      }
   }

   public String getKeyAt(int var1) {
      return var1 >= 0 && var1 < this.size ? this.keys[var1] : null;
   }

   public <V> V getValueAt(int var1) {
      return var1 >= 0 && var1 < this.size ? this.values[var1] : null;
   }

   public int size() {
      return this.size;
   }

   public <V> void forEach(BiConsumer<String, ? super V> var1) {
      this.iterating = true;

      try {
         for(int var2 = 0; var2 < this.size; ++var2) {
            var1.accept(this.keys[var2], this.values[var2]);
         }
      } finally {
         this.iterating = false;
      }

   }

   public <V, T> void forEach(TriConsumer<String, ? super V, T> var1, T var2) {
      this.iterating = true;

      try {
         for(int var3 = 0; var3 < this.size; ++var3) {
            var1.accept(this.keys[var3], this.values[var3], var2);
         }
      } finally {
         this.iterating = false;
      }

   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SortedArrayStringMap)) {
         return false;
      } else {
         SortedArrayStringMap var2 = (SortedArrayStringMap)var1;
         if (this.size() != var2.size()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.size(); ++var3) {
               if (!Objects.equals(this.keys[var3], var2.keys[var3])) {
                  return false;
               }

               if (!Objects.equals(this.values[var3], var2.values[var3])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      byte var1 = 37;
      int var2 = 31 * var1 + this.size;
      var2 = 31 * var2 + hashCode(this.keys, this.size);
      var2 = 31 * var2 + hashCode(this.values, this.size);
      return var2;
   }

   private static int hashCode(Object[] var0, int var1) {
      int var2 = 1;

      for(int var3 = 0; var3 < var1; ++var3) {
         var2 = 31 * var2 + (var0[var3] == null ? 0 : var0[var3].hashCode());
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(256);
      var1.append('{');

      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         var1.append(this.keys[var2]).append('=');
         var1.append(this.values[var2] == this ? "(this map)" : this.values[var2]);
      }

      var1.append('}');
      return var1.toString();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.keys == EMPTY) {
         var1.writeInt(ceilingNextPowerOfTwo(this.threshold));
      } else {
         var1.writeInt(this.keys.length);
      }

      var1.writeInt(this.size);
      if (this.size > 0) {
         for(int var2 = 0; var2 < this.size; ++var2) {
            var1.writeObject(this.keys[var2]);

            try {
               var1.writeObject(new MarshalledObject(this.values[var2]));
            } catch (Exception var4) {
               this.handleSerializationException(var4, var2, this.keys[var2]);
               var1.writeObject((Object)null);
            }
         }
      }

   }

   private static int ceilingNextPowerOfTwo(int var0) {
      boolean var1 = true;
      return 1 << 32 - Integer.numberOfLeadingZeros(var0 - 1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.keys = EMPTY;
      this.values = EMPTY;
      int var2 = var1.readInt();
      if (var2 < 0) {
         throw new InvalidObjectException("Illegal capacity: " + var2);
      } else {
         int var3 = var1.readInt();
         if (var3 < 0) {
            throw new InvalidObjectException("Illegal mappings count: " + var3);
         } else {
            if (var3 > 0) {
               this.inflateTable(var2);
            } else {
               this.threshold = var2;
            }

            for(int var4 = 0; var4 < var3; ++var4) {
               this.keys[var4] = (String)var1.readObject();

               try {
                  MarshalledObject var5 = (MarshalledObject)var1.readObject();
                  this.values[var4] = var5 == null ? null : var5.get();
               } catch (LinkageError | Exception var6) {
                  this.handleSerializationException(var6, var4, this.keys[var4]);
                  this.values[var4] = null;
               }
            }

            this.size = var3;
         }
      }
   }

   private void handleSerializationException(Throwable var1, int var2, String var3) {
      StatusLogger.getLogger().warn("Ignoring {} for key[{}] ('{}')", String.valueOf(var1), var2, this.keys[var2]);
   }
}
