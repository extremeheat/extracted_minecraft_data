package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class HashBiMap<K, V> extends Maps.IteratorBasedAbstractMap<K, V> implements BiMap<K, V>, Serializable {
   private static final double LOAD_FACTOR = 1.0D;
   private transient HashBiMap.BiEntry<K, V>[] hashTableKToV;
   private transient HashBiMap.BiEntry<K, V>[] hashTableVToK;
   private transient HashBiMap.BiEntry<K, V> firstInKeyInsertionOrder;
   private transient HashBiMap.BiEntry<K, V> lastInKeyInsertionOrder;
   private transient int size;
   private transient int mask;
   private transient int modCount;
   @RetainedWith
   private transient BiMap<V, K> inverse;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <K, V> HashBiMap<K, V> create() {
      return create(16);
   }

   public static <K, V> HashBiMap<K, V> create(int var0) {
      return new HashBiMap(var0);
   }

   public static <K, V> HashBiMap<K, V> create(Map<? extends K, ? extends V> var0) {
      HashBiMap var1 = create(var0.size());
      var1.putAll(var0);
      return var1;
   }

   private HashBiMap(int var1) {
      super();
      this.init(var1);
   }

   private void init(int var1) {
      CollectPreconditions.checkNonnegative(var1, "expectedSize");
      int var2 = Hashing.closedTableSize(var1, 1.0D);
      this.hashTableKToV = this.createTable(var2);
      this.hashTableVToK = this.createTable(var2);
      this.firstInKeyInsertionOrder = null;
      this.lastInKeyInsertionOrder = null;
      this.size = 0;
      this.mask = var2 - 1;
      this.modCount = 0;
   }

   private void delete(HashBiMap.BiEntry<K, V> var1) {
      int var2 = var1.keyHash & this.mask;
      HashBiMap.BiEntry var3 = null;

      for(HashBiMap.BiEntry var4 = this.hashTableKToV[var2]; var4 != var1; var4 = var4.nextInKToVBucket) {
         var3 = var4;
      }

      if (var3 == null) {
         this.hashTableKToV[var2] = var1.nextInKToVBucket;
      } else {
         var3.nextInKToVBucket = var1.nextInKToVBucket;
      }

      int var6 = var1.valueHash & this.mask;
      var3 = null;

      for(HashBiMap.BiEntry var5 = this.hashTableVToK[var6]; var5 != var1; var5 = var5.nextInVToKBucket) {
         var3 = var5;
      }

      if (var3 == null) {
         this.hashTableVToK[var6] = var1.nextInVToKBucket;
      } else {
         var3.nextInVToKBucket = var1.nextInVToKBucket;
      }

      if (var1.prevInKeyInsertionOrder == null) {
         this.firstInKeyInsertionOrder = var1.nextInKeyInsertionOrder;
      } else {
         var1.prevInKeyInsertionOrder.nextInKeyInsertionOrder = var1.nextInKeyInsertionOrder;
      }

      if (var1.nextInKeyInsertionOrder == null) {
         this.lastInKeyInsertionOrder = var1.prevInKeyInsertionOrder;
      } else {
         var1.nextInKeyInsertionOrder.prevInKeyInsertionOrder = var1.prevInKeyInsertionOrder;
      }

      --this.size;
      ++this.modCount;
   }

   private void insert(HashBiMap.BiEntry<K, V> var1, @Nullable HashBiMap.BiEntry<K, V> var2) {
      int var3 = var1.keyHash & this.mask;
      var1.nextInKToVBucket = this.hashTableKToV[var3];
      this.hashTableKToV[var3] = var1;
      int var4 = var1.valueHash & this.mask;
      var1.nextInVToKBucket = this.hashTableVToK[var4];
      this.hashTableVToK[var4] = var1;
      if (var2 == null) {
         var1.prevInKeyInsertionOrder = this.lastInKeyInsertionOrder;
         var1.nextInKeyInsertionOrder = null;
         if (this.lastInKeyInsertionOrder == null) {
            this.firstInKeyInsertionOrder = var1;
         } else {
            this.lastInKeyInsertionOrder.nextInKeyInsertionOrder = var1;
         }

         this.lastInKeyInsertionOrder = var1;
      } else {
         var1.prevInKeyInsertionOrder = var2.prevInKeyInsertionOrder;
         if (var1.prevInKeyInsertionOrder == null) {
            this.firstInKeyInsertionOrder = var1;
         } else {
            var1.prevInKeyInsertionOrder.nextInKeyInsertionOrder = var1;
         }

         var1.nextInKeyInsertionOrder = var2.nextInKeyInsertionOrder;
         if (var1.nextInKeyInsertionOrder == null) {
            this.lastInKeyInsertionOrder = var1;
         } else {
            var1.nextInKeyInsertionOrder.prevInKeyInsertionOrder = var1;
         }
      }

      ++this.size;
      ++this.modCount;
   }

   private HashBiMap.BiEntry<K, V> seekByKey(@Nullable Object var1, int var2) {
      for(HashBiMap.BiEntry var3 = this.hashTableKToV[var2 & this.mask]; var3 != null; var3 = var3.nextInKToVBucket) {
         if (var2 == var3.keyHash && Objects.equal(var1, var3.key)) {
            return var3;
         }
      }

      return null;
   }

   private HashBiMap.BiEntry<K, V> seekByValue(@Nullable Object var1, int var2) {
      for(HashBiMap.BiEntry var3 = this.hashTableVToK[var2 & this.mask]; var3 != null; var3 = var3.nextInVToKBucket) {
         if (var2 == var3.valueHash && Objects.equal(var1, var3.value)) {
            return var3;
         }
      }

      return null;
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.seekByKey(var1, Hashing.smearedHash(var1)) != null;
   }

   public boolean containsValue(@Nullable Object var1) {
      return this.seekByValue(var1, Hashing.smearedHash(var1)) != null;
   }

   @Nullable
   public V get(@Nullable Object var1) {
      return Maps.valueOrNull(this.seekByKey(var1, Hashing.smearedHash(var1)));
   }

   @CanIgnoreReturnValue
   public V put(@Nullable K var1, @Nullable V var2) {
      return this.put(var1, var2, false);
   }

   @CanIgnoreReturnValue
   public V forcePut(@Nullable K var1, @Nullable V var2) {
      return this.put(var1, var2, true);
   }

   private V put(@Nullable K var1, @Nullable V var2, boolean var3) {
      int var4 = Hashing.smearedHash(var1);
      int var5 = Hashing.smearedHash(var2);
      HashBiMap.BiEntry var6 = this.seekByKey(var1, var4);
      if (var6 != null && var5 == var6.valueHash && Objects.equal(var2, var6.value)) {
         return var2;
      } else {
         HashBiMap.BiEntry var7 = this.seekByValue(var2, var5);
         if (var7 != null) {
            if (!var3) {
               throw new IllegalArgumentException("value already present: " + var2);
            }

            this.delete(var7);
         }

         HashBiMap.BiEntry var8 = new HashBiMap.BiEntry(var1, var4, var2, var5);
         if (var6 != null) {
            this.delete(var6);
            this.insert(var8, var6);
            var6.prevInKeyInsertionOrder = null;
            var6.nextInKeyInsertionOrder = null;
            this.rehashIfNecessary();
            return var6.value;
         } else {
            this.insert(var8, (HashBiMap.BiEntry)null);
            this.rehashIfNecessary();
            return null;
         }
      }
   }

   @Nullable
   private K putInverse(@Nullable V var1, @Nullable K var2, boolean var3) {
      int var4 = Hashing.smearedHash(var1);
      int var5 = Hashing.smearedHash(var2);
      HashBiMap.BiEntry var6 = this.seekByValue(var1, var4);
      if (var6 != null && var5 == var6.keyHash && Objects.equal(var2, var6.key)) {
         return var2;
      } else {
         HashBiMap.BiEntry var7 = this.seekByKey(var2, var5);
         if (var7 != null) {
            if (!var3) {
               throw new IllegalArgumentException("value already present: " + var2);
            }

            this.delete(var7);
         }

         if (var6 != null) {
            this.delete(var6);
         }

         HashBiMap.BiEntry var8 = new HashBiMap.BiEntry(var2, var5, var1, var4);
         this.insert(var8, var7);
         if (var7 != null) {
            var7.prevInKeyInsertionOrder = null;
            var7.nextInKeyInsertionOrder = null;
         }

         this.rehashIfNecessary();
         return Maps.keyOrNull(var6);
      }
   }

   private void rehashIfNecessary() {
      HashBiMap.BiEntry[] var1 = this.hashTableKToV;
      if (Hashing.needsResizing(this.size, var1.length, 1.0D)) {
         int var2 = var1.length * 2;
         this.hashTableKToV = this.createTable(var2);
         this.hashTableVToK = this.createTable(var2);
         this.mask = var2 - 1;
         this.size = 0;

         for(HashBiMap.BiEntry var3 = this.firstInKeyInsertionOrder; var3 != null; var3 = var3.nextInKeyInsertionOrder) {
            this.insert(var3, var3);
         }

         ++this.modCount;
      }

   }

   private HashBiMap.BiEntry<K, V>[] createTable(int var1) {
      return new HashBiMap.BiEntry[var1];
   }

   @CanIgnoreReturnValue
   public V remove(@Nullable Object var1) {
      HashBiMap.BiEntry var2 = this.seekByKey(var1, Hashing.smearedHash(var1));
      if (var2 == null) {
         return null;
      } else {
         this.delete(var2);
         var2.prevInKeyInsertionOrder = null;
         var2.nextInKeyInsertionOrder = null;
         return var2.value;
      }
   }

   public void clear() {
      this.size = 0;
      Arrays.fill(this.hashTableKToV, (Object)null);
      Arrays.fill(this.hashTableVToK, (Object)null);
      this.firstInKeyInsertionOrder = null;
      this.lastInKeyInsertionOrder = null;
      ++this.modCount;
   }

   public int size() {
      return this.size;
   }

   public Set<K> keySet() {
      return new HashBiMap.KeySet();
   }

   public Set<V> values() {
      return this.inverse().keySet();
   }

   Iterator<Entry<K, V>> entryIterator() {
      return new HashBiMap<K, V>.Itr<Entry<K, V>>() {
         Entry<K, V> output(HashBiMap.BiEntry<K, V> var1) {
            return new null.MapEntry(var1);
         }

         class MapEntry extends AbstractMapEntry<K, V> {
            HashBiMap.BiEntry<K, V> delegate;

            MapEntry(HashBiMap.BiEntry<K, V> var2) {
               super();
               this.delegate = var2;
            }

            public K getKey() {
               return this.delegate.key;
            }

            public V getValue() {
               return this.delegate.value;
            }

            public V setValue(V var1) {
               Object var2 = this.delegate.value;
               int var3 = Hashing.smearedHash(var1);
               if (var3 == this.delegate.valueHash && Objects.equal(var1, var2)) {
                  return var1;
               } else {
                  Preconditions.checkArgument(HashBiMap.this.seekByValue(var1, var3) == null, "value already present: %s", var1);
                  HashBiMap.this.delete(this.delegate);
                  HashBiMap.BiEntry var4 = new HashBiMap.BiEntry(this.delegate.key, this.delegate.keyHash, var1, var3);
                  HashBiMap.this.insert(var4, this.delegate);
                  this.delegate.prevInKeyInsertionOrder = null;
                  this.delegate.nextInKeyInsertionOrder = null;
                  expectedModCount = HashBiMap.this.modCount;
                  if (toRemove == this.delegate) {
                     toRemove = var4;
                  }

                  this.delegate = var4;
                  return var2;
               }
            }
         }
      };
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);

      for(HashBiMap.BiEntry var2 = this.firstInKeyInsertionOrder; var2 != null; var2 = var2.nextInKeyInsertionOrder) {
         var1.accept(var2.key, var2.value);
      }

   }

   public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
      Preconditions.checkNotNull(var1);
      HashBiMap.BiEntry var2 = this.firstInKeyInsertionOrder;
      this.clear();

      for(HashBiMap.BiEntry var3 = var2; var3 != null; var3 = var3.nextInKeyInsertionOrder) {
         this.put(var3.key, var1.apply(var3.key, var3.value));
      }

   }

   public BiMap<V, K> inverse() {
      return this.inverse == null ? (this.inverse = new HashBiMap.Inverse()) : this.inverse;
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Serialization.writeMap(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(16);
      int var2 = Serialization.readCount(var1);
      Serialization.populateMap(this, var1, var2);
   }

   private static final class InverseSerializedForm<K, V> implements Serializable {
      private final HashBiMap<K, V> bimap;

      InverseSerializedForm(HashBiMap<K, V> var1) {
         super();
         this.bimap = var1;
      }

      Object readResolve() {
         return this.bimap.inverse();
      }
   }

   private final class Inverse extends Maps.IteratorBasedAbstractMap<V, K> implements BiMap<V, K>, Serializable {
      private Inverse() {
         super();
      }

      BiMap<K, V> forward() {
         return HashBiMap.this;
      }

      public int size() {
         return HashBiMap.this.size;
      }

      public void clear() {
         this.forward().clear();
      }

      public boolean containsKey(@Nullable Object var1) {
         return this.forward().containsValue(var1);
      }

      public K get(@Nullable Object var1) {
         return Maps.keyOrNull(HashBiMap.this.seekByValue(var1, Hashing.smearedHash(var1)));
      }

      @CanIgnoreReturnValue
      public K put(@Nullable V var1, @Nullable K var2) {
         return HashBiMap.this.putInverse(var1, var2, false);
      }

      public K forcePut(@Nullable V var1, @Nullable K var2) {
         return HashBiMap.this.putInverse(var1, var2, true);
      }

      public K remove(@Nullable Object var1) {
         HashBiMap.BiEntry var2 = HashBiMap.this.seekByValue(var1, Hashing.smearedHash(var1));
         if (var2 == null) {
            return null;
         } else {
            HashBiMap.this.delete(var2);
            var2.prevInKeyInsertionOrder = null;
            var2.nextInKeyInsertionOrder = null;
            return var2.key;
         }
      }

      public BiMap<K, V> inverse() {
         return this.forward();
      }

      public Set<V> keySet() {
         return new HashBiMap.Inverse.InverseKeySet();
      }

      public Set<K> values() {
         return this.forward().keySet();
      }

      Iterator<Entry<V, K>> entryIterator() {
         return new HashBiMap<K, V>.Itr<Entry<V, K>>() {
            Entry<V, K> output(HashBiMap.BiEntry<K, V> var1) {
               return new null.InverseEntry(var1);
            }

            class InverseEntry extends AbstractMapEntry<V, K> {
               HashBiMap.BiEntry<K, V> delegate;

               InverseEntry(HashBiMap.BiEntry<K, V> var2) {
                  super();
                  this.delegate = var2;
               }

               public V getKey() {
                  return this.delegate.value;
               }

               public K getValue() {
                  return this.delegate.key;
               }

               public K setValue(K var1) {
                  Object var2 = this.delegate.key;
                  int var3 = Hashing.smearedHash(var1);
                  if (var3 == this.delegate.keyHash && Objects.equal(var1, var2)) {
                     return var1;
                  } else {
                     Preconditions.checkArgument(HashBiMap.this.seekByKey(var1, var3) == null, "value already present: %s", var1);
                     HashBiMap.this.delete(this.delegate);
                     HashBiMap.BiEntry var4 = new HashBiMap.BiEntry(var1, var3, this.delegate.value, this.delegate.valueHash);
                     this.delegate = var4;
                     HashBiMap.this.insert(var4, (HashBiMap.BiEntry)null);
                     expectedModCount = HashBiMap.this.modCount;
                     return var2;
                  }
               }
            }
         };
      }

      public void forEach(BiConsumer<? super V, ? super K> var1) {
         Preconditions.checkNotNull(var1);
         HashBiMap.this.forEach((var1x, var2) -> {
            var1.accept(var2, var1x);
         });
      }

      public void replaceAll(BiFunction<? super V, ? super K, ? extends K> var1) {
         Preconditions.checkNotNull(var1);
         HashBiMap.BiEntry var2 = HashBiMap.this.firstInKeyInsertionOrder;
         this.clear();

         for(HashBiMap.BiEntry var3 = var2; var3 != null; var3 = var3.nextInKeyInsertionOrder) {
            this.put(var3.value, var1.apply(var3.value, var3.key));
         }

      }

      Object writeReplace() {
         return new HashBiMap.InverseSerializedForm(HashBiMap.this);
      }

      // $FF: synthetic method
      Inverse(Object var2) {
         this();
      }

      private final class InverseKeySet extends Maps.KeySet<V, K> {
         InverseKeySet() {
            super(Inverse.this);
         }

         public boolean remove(@Nullable Object var1) {
            HashBiMap.BiEntry var2 = HashBiMap.this.seekByValue(var1, Hashing.smearedHash(var1));
            if (var2 == null) {
               return false;
            } else {
               HashBiMap.this.delete(var2);
               return true;
            }
         }

         public Iterator<V> iterator() {
            return new HashBiMap<K, V>.Itr<V>() {
               V output(HashBiMap.BiEntry<K, V> var1) {
                  return var1.value;
               }
            };
         }
      }
   }

   private final class KeySet extends Maps.KeySet<K, V> {
      KeySet() {
         super(HashBiMap.this);
      }

      public Iterator<K> iterator() {
         return new HashBiMap<K, V>.Itr<K>() {
            K output(HashBiMap.BiEntry<K, V> var1) {
               return var1.key;
            }
         };
      }

      public boolean remove(@Nullable Object var1) {
         HashBiMap.BiEntry var2 = HashBiMap.this.seekByKey(var1, Hashing.smearedHash(var1));
         if (var2 == null) {
            return false;
         } else {
            HashBiMap.this.delete(var2);
            var2.prevInKeyInsertionOrder = null;
            var2.nextInKeyInsertionOrder = null;
            return true;
         }
      }
   }

   abstract class Itr<T> implements Iterator<T> {
      HashBiMap.BiEntry<K, V> next;
      HashBiMap.BiEntry<K, V> toRemove;
      int expectedModCount;

      Itr() {
         super();
         this.next = HashBiMap.this.firstInKeyInsertionOrder;
         this.toRemove = null;
         this.expectedModCount = HashBiMap.this.modCount;
      }

      public boolean hasNext() {
         if (HashBiMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            return this.next != null;
         }
      }

      public T next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            HashBiMap.BiEntry var1 = this.next;
            this.next = var1.nextInKeyInsertionOrder;
            this.toRemove = var1;
            return this.output(var1);
         }
      }

      public void remove() {
         if (HashBiMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            CollectPreconditions.checkRemove(this.toRemove != null);
            HashBiMap.this.delete(this.toRemove);
            this.expectedModCount = HashBiMap.this.modCount;
            this.toRemove = null;
         }
      }

      abstract T output(HashBiMap.BiEntry<K, V> var1);
   }

   private static final class BiEntry<K, V> extends ImmutableEntry<K, V> {
      final int keyHash;
      final int valueHash;
      @Nullable
      HashBiMap.BiEntry<K, V> nextInKToVBucket;
      @Nullable
      HashBiMap.BiEntry<K, V> nextInVToKBucket;
      @Nullable
      HashBiMap.BiEntry<K, V> nextInKeyInsertionOrder;
      @Nullable
      HashBiMap.BiEntry<K, V> prevInKeyInsertionOrder;

      BiEntry(K var1, int var2, V var3, int var4) {
         super(var1, var3);
         this.keyHash = var2;
         this.valueHash = var4;
      }
   }
}
