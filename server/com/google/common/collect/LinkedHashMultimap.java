package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class LinkedHashMultimap<K, V> extends AbstractSetMultimap<K, V> {
   private static final int DEFAULT_KEY_CAPACITY = 16;
   private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
   @VisibleForTesting
   static final double VALUE_SET_LOAD_FACTOR = 1.0D;
   @VisibleForTesting
   transient int valueSetCapacity = 2;
   private transient LinkedHashMultimap.ValueEntry<K, V> multimapHeaderEntry;
   @GwtIncompatible
   private static final long serialVersionUID = 1L;

   public static <K, V> LinkedHashMultimap<K, V> create() {
      return new LinkedHashMultimap(16, 2);
   }

   public static <K, V> LinkedHashMultimap<K, V> create(int var0, int var1) {
      return new LinkedHashMultimap(Maps.capacity(var0), Maps.capacity(var1));
   }

   public static <K, V> LinkedHashMultimap<K, V> create(Multimap<? extends K, ? extends V> var0) {
      LinkedHashMultimap var1 = create(var0.keySet().size(), 2);
      var1.putAll(var0);
      return var1;
   }

   private static <K, V> void succeedsInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var0, LinkedHashMultimap.ValueSetLink<K, V> var1) {
      var0.setSuccessorInValueSet(var1);
      var1.setPredecessorInValueSet(var0);
   }

   private static <K, V> void succeedsInMultimap(LinkedHashMultimap.ValueEntry<K, V> var0, LinkedHashMultimap.ValueEntry<K, V> var1) {
      var0.setSuccessorInMultimap(var1);
      var1.setPredecessorInMultimap(var0);
   }

   private static <K, V> void deleteFromValueSet(LinkedHashMultimap.ValueSetLink<K, V> var0) {
      succeedsInValueSet(var0.getPredecessorInValueSet(), var0.getSuccessorInValueSet());
   }

   private static <K, V> void deleteFromMultimap(LinkedHashMultimap.ValueEntry<K, V> var0) {
      succeedsInMultimap(var0.getPredecessorInMultimap(), var0.getSuccessorInMultimap());
   }

   private LinkedHashMultimap(int var1, int var2) {
      super(new LinkedHashMap(var1));
      CollectPreconditions.checkNonnegative(var2, "expectedValuesPerKey");
      this.valueSetCapacity = var2;
      this.multimapHeaderEntry = new LinkedHashMultimap.ValueEntry((Object)null, (Object)null, 0, (LinkedHashMultimap.ValueEntry)null);
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
   }

   Set<V> createCollection() {
      return new LinkedHashSet(this.valueSetCapacity);
   }

   Collection<V> createCollection(K var1) {
      return new LinkedHashMultimap.ValueSet(var1, this.valueSetCapacity);
   }

   @CanIgnoreReturnValue
   public Set<V> replaceValues(@Nullable K var1, Iterable<? extends V> var2) {
      return super.replaceValues(var1, var2);
   }

   public Set<Entry<K, V>> entries() {
      return super.entries();
   }

   public Set<K> keySet() {
      return super.keySet();
   }

   public Collection<V> values() {
      return super.values();
   }

   Iterator<Entry<K, V>> entryIterator() {
      return new Iterator<Entry<K, V>>() {
         LinkedHashMultimap.ValueEntry<K, V> nextEntry;
         LinkedHashMultimap.ValueEntry<K, V> toRemove;

         {
            this.nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.successorInMultimap;
         }

         public boolean hasNext() {
            return this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry;
         }

         public Entry<K, V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               LinkedHashMultimap.ValueEntry var1 = this.nextEntry;
               this.toRemove = var1;
               this.nextEntry = this.nextEntry.successorInMultimap;
               return var1;
            }
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.toRemove != null);
            LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
            this.toRemove = null;
         }
      };
   }

   Spliterator<Entry<K, V>> entrySpliterator() {
      return Spliterators.spliterator(this.entries(), 17);
   }

   Iterator<V> valueIterator() {
      return Maps.valueIterator(this.entryIterator());
   }

   Spliterator<V> valueSpliterator() {
      return CollectSpliterators.map(this.entrySpliterator(), Entry::getValue);
   }

   public void clear() {
      super.clear();
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.keySet().size());
      Iterator var2 = this.keySet().iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.writeObject(var3);
      }

      var1.writeInt(this.size());
      var2 = this.entries().iterator();

      while(var2.hasNext()) {
         Entry var4 = (Entry)var2.next();
         var1.writeObject(var4.getKey());
         var1.writeObject(var4.getValue());
      }

   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.multimapHeaderEntry = new LinkedHashMultimap.ValueEntry((Object)null, (Object)null, 0, (LinkedHashMultimap.ValueEntry)null);
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
      this.valueSetCapacity = 2;
      int var2 = var1.readInt();
      LinkedHashMap var3 = new LinkedHashMap();

      int var4;
      for(var4 = 0; var4 < var2; ++var4) {
         Object var5 = var1.readObject();
         var3.put(var5, this.createCollection(var5));
      }

      var4 = var1.readInt();

      for(int var8 = 0; var8 < var4; ++var8) {
         Object var6 = var1.readObject();
         Object var7 = var1.readObject();
         ((Collection)var3.get(var6)).add(var7);
      }

      this.setMap(var3);
   }

   @VisibleForTesting
   final class ValueSet extends Sets.ImprovedAbstractSet<V> implements LinkedHashMultimap.ValueSetLink<K, V> {
      private final K key;
      @VisibleForTesting
      LinkedHashMultimap.ValueEntry<K, V>[] hashTable;
      private int size = 0;
      private int modCount = 0;
      private LinkedHashMultimap.ValueSetLink<K, V> firstEntry;
      private LinkedHashMultimap.ValueSetLink<K, V> lastEntry;

      ValueSet(K var2, int var3) {
         super();
         this.key = var2;
         this.firstEntry = this;
         this.lastEntry = this;
         int var4 = Hashing.closedTableSize(var3, 1.0D);
         LinkedHashMultimap.ValueEntry[] var5 = new LinkedHashMultimap.ValueEntry[var4];
         this.hashTable = var5;
      }

      private int mask() {
         return this.hashTable.length - 1;
      }

      public LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet() {
         return this.lastEntry;
      }

      public LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet() {
         return this.firstEntry;
      }

      public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1) {
         this.lastEntry = var1;
      }

      public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1) {
         this.firstEntry = var1;
      }

      public Iterator<V> iterator() {
         return new Iterator<V>() {
            LinkedHashMultimap.ValueSetLink<K, V> nextEntry;
            LinkedHashMultimap.ValueEntry<K, V> toRemove;
            int expectedModCount;

            {
               this.nextEntry = ValueSet.this.firstEntry;
               this.expectedModCount = ValueSet.this.modCount;
            }

            private void checkForComodification() {
               if (ValueSet.this.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               }
            }

            public boolean hasNext() {
               this.checkForComodification();
               return this.nextEntry != ValueSet.this;
            }

            public V next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  LinkedHashMultimap.ValueEntry var1 = (LinkedHashMultimap.ValueEntry)this.nextEntry;
                  Object var2 = var1.getValue();
                  this.toRemove = var1;
                  this.nextEntry = var1.getSuccessorInValueSet();
                  return var2;
               }
            }

            public void remove() {
               this.checkForComodification();
               CollectPreconditions.checkRemove(this.toRemove != null);
               ValueSet.this.remove(this.toRemove.getValue());
               this.expectedModCount = ValueSet.this.modCount;
               this.toRemove = null;
            }
         };
      }

      public void forEach(Consumer<? super V> var1) {
         Preconditions.checkNotNull(var1);

         for(LinkedHashMultimap.ValueSetLink var2 = this.firstEntry; var2 != this; var2 = var2.getSuccessorInValueSet()) {
            var1.accept(((LinkedHashMultimap.ValueEntry)var2).getValue());
         }

      }

      public int size() {
         return this.size;
      }

      public boolean contains(@Nullable Object var1) {
         int var2 = Hashing.smearedHash(var1);

         for(LinkedHashMultimap.ValueEntry var3 = this.hashTable[var2 & this.mask()]; var3 != null; var3 = var3.nextInValueBucket) {
            if (var3.matchesValue(var1, var2)) {
               return true;
            }
         }

         return false;
      }

      public boolean add(@Nullable V var1) {
         int var2 = Hashing.smearedHash(var1);
         int var3 = var2 & this.mask();
         LinkedHashMultimap.ValueEntry var4 = this.hashTable[var3];

         LinkedHashMultimap.ValueEntry var5;
         for(var5 = var4; var5 != null; var5 = var5.nextInValueBucket) {
            if (var5.matchesValue(var1, var2)) {
               return false;
            }
         }

         var5 = new LinkedHashMultimap.ValueEntry(this.key, var1, var2, var4);
         LinkedHashMultimap.succeedsInValueSet(this.lastEntry, var5);
         LinkedHashMultimap.succeedsInValueSet(var5, this);
         LinkedHashMultimap.succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), var5);
         LinkedHashMultimap.succeedsInMultimap(var5, LinkedHashMultimap.this.multimapHeaderEntry);
         this.hashTable[var3] = var5;
         ++this.size;
         ++this.modCount;
         this.rehashIfNecessary();
         return true;
      }

      private void rehashIfNecessary() {
         if (Hashing.needsResizing(this.size, this.hashTable.length, 1.0D)) {
            LinkedHashMultimap.ValueEntry[] var1 = new LinkedHashMultimap.ValueEntry[this.hashTable.length * 2];
            this.hashTable = var1;
            int var2 = var1.length - 1;

            for(LinkedHashMultimap.ValueSetLink var3 = this.firstEntry; var3 != this; var3 = var3.getSuccessorInValueSet()) {
               LinkedHashMultimap.ValueEntry var4 = (LinkedHashMultimap.ValueEntry)var3;
               int var5 = var4.smearedValueHash & var2;
               var4.nextInValueBucket = var1[var5];
               var1[var5] = var4;
            }
         }

      }

      @CanIgnoreReturnValue
      public boolean remove(@Nullable Object var1) {
         int var2 = Hashing.smearedHash(var1);
         int var3 = var2 & this.mask();
         LinkedHashMultimap.ValueEntry var4 = null;

         for(LinkedHashMultimap.ValueEntry var5 = this.hashTable[var3]; var5 != null; var5 = var5.nextInValueBucket) {
            if (var5.matchesValue(var1, var2)) {
               if (var4 == null) {
                  this.hashTable[var3] = var5.nextInValueBucket;
               } else {
                  var4.nextInValueBucket = var5.nextInValueBucket;
               }

               LinkedHashMultimap.deleteFromValueSet(var5);
               LinkedHashMultimap.deleteFromMultimap(var5);
               --this.size;
               ++this.modCount;
               return true;
            }

            var4 = var5;
         }

         return false;
      }

      public void clear() {
         Arrays.fill(this.hashTable, (Object)null);
         this.size = 0;

         for(LinkedHashMultimap.ValueSetLink var1 = this.firstEntry; var1 != this; var1 = var1.getSuccessorInValueSet()) {
            LinkedHashMultimap.ValueEntry var2 = (LinkedHashMultimap.ValueEntry)var1;
            LinkedHashMultimap.deleteFromMultimap(var2);
         }

         LinkedHashMultimap.succeedsInValueSet(this, this);
         ++this.modCount;
      }
   }

   @VisibleForTesting
   static final class ValueEntry<K, V> extends ImmutableEntry<K, V> implements LinkedHashMultimap.ValueSetLink<K, V> {
      final int smearedValueHash;
      @Nullable
      LinkedHashMultimap.ValueEntry<K, V> nextInValueBucket;
      LinkedHashMultimap.ValueSetLink<K, V> predecessorInValueSet;
      LinkedHashMultimap.ValueSetLink<K, V> successorInValueSet;
      LinkedHashMultimap.ValueEntry<K, V> predecessorInMultimap;
      LinkedHashMultimap.ValueEntry<K, V> successorInMultimap;

      ValueEntry(@Nullable K var1, @Nullable V var2, int var3, @Nullable LinkedHashMultimap.ValueEntry<K, V> var4) {
         super(var1, var2);
         this.smearedValueHash = var3;
         this.nextInValueBucket = var4;
      }

      boolean matchesValue(@Nullable Object var1, int var2) {
         return this.smearedValueHash == var2 && Objects.equal(this.getValue(), var1);
      }

      public LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet() {
         return this.predecessorInValueSet;
      }

      public LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet() {
         return this.successorInValueSet;
      }

      public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1) {
         this.predecessorInValueSet = var1;
      }

      public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1) {
         this.successorInValueSet = var1;
      }

      public LinkedHashMultimap.ValueEntry<K, V> getPredecessorInMultimap() {
         return this.predecessorInMultimap;
      }

      public LinkedHashMultimap.ValueEntry<K, V> getSuccessorInMultimap() {
         return this.successorInMultimap;
      }

      public void setSuccessorInMultimap(LinkedHashMultimap.ValueEntry<K, V> var1) {
         this.successorInMultimap = var1;
      }

      public void setPredecessorInMultimap(LinkedHashMultimap.ValueEntry<K, V> var1) {
         this.predecessorInMultimap = var1;
      }
   }

   private interface ValueSetLink<K, V> {
      LinkedHashMultimap.ValueSetLink<K, V> getPredecessorInValueSet();

      LinkedHashMultimap.ValueSetLink<K, V> getSuccessorInValueSet();

      void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1);

      void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink<K, V> var1);
   }
}
