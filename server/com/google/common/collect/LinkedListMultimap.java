package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public class LinkedListMultimap<K, V> extends AbstractMultimap<K, V> implements ListMultimap<K, V>, Serializable {
   private transient LinkedListMultimap.Node<K, V> head;
   private transient LinkedListMultimap.Node<K, V> tail;
   private transient Map<K, LinkedListMultimap.KeyList<K, V>> keyToKeyList;
   private transient int size;
   private transient int modCount;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <K, V> LinkedListMultimap<K, V> create() {
      return new LinkedListMultimap();
   }

   public static <K, V> LinkedListMultimap<K, V> create(int var0) {
      return new LinkedListMultimap(var0);
   }

   public static <K, V> LinkedListMultimap<K, V> create(Multimap<? extends K, ? extends V> var0) {
      return new LinkedListMultimap(var0);
   }

   LinkedListMultimap() {
      super();
      this.keyToKeyList = Maps.newHashMap();
   }

   private LinkedListMultimap(int var1) {
      super();
      this.keyToKeyList = new HashMap(var1);
   }

   private LinkedListMultimap(Multimap<? extends K, ? extends V> var1) {
      this(var1.keySet().size());
      this.putAll(var1);
   }

   @CanIgnoreReturnValue
   private LinkedListMultimap.Node<K, V> addNode(@Nullable K var1, @Nullable V var2, @Nullable LinkedListMultimap.Node<K, V> var3) {
      LinkedListMultimap.Node var4 = new LinkedListMultimap.Node(var1, var2);
      if (this.head == null) {
         this.head = this.tail = var4;
         this.keyToKeyList.put(var1, new LinkedListMultimap.KeyList(var4));
         ++this.modCount;
      } else {
         LinkedListMultimap.KeyList var5;
         if (var3 == null) {
            this.tail.next = var4;
            var4.previous = this.tail;
            this.tail = var4;
            var5 = (LinkedListMultimap.KeyList)this.keyToKeyList.get(var1);
            if (var5 == null) {
               this.keyToKeyList.put(var1, new LinkedListMultimap.KeyList(var4));
               ++this.modCount;
            } else {
               ++var5.count;
               LinkedListMultimap.Node var6 = var5.tail;
               var6.nextSibling = var4;
               var4.previousSibling = var6;
               var5.tail = var4;
            }
         } else {
            var5 = (LinkedListMultimap.KeyList)this.keyToKeyList.get(var1);
            ++var5.count;
            var4.previous = var3.previous;
            var4.previousSibling = var3.previousSibling;
            var4.next = var3;
            var4.nextSibling = var3;
            if (var3.previousSibling == null) {
               ((LinkedListMultimap.KeyList)this.keyToKeyList.get(var1)).head = var4;
            } else {
               var3.previousSibling.nextSibling = var4;
            }

            if (var3.previous == null) {
               this.head = var4;
            } else {
               var3.previous.next = var4;
            }

            var3.previous = var4;
            var3.previousSibling = var4;
         }
      }

      ++this.size;
      return var4;
   }

   private void removeNode(LinkedListMultimap.Node<K, V> var1) {
      if (var1.previous != null) {
         var1.previous.next = var1.next;
      } else {
         this.head = var1.next;
      }

      if (var1.next != null) {
         var1.next.previous = var1.previous;
      } else {
         this.tail = var1.previous;
      }

      LinkedListMultimap.KeyList var2;
      if (var1.previousSibling == null && var1.nextSibling == null) {
         var2 = (LinkedListMultimap.KeyList)this.keyToKeyList.remove(var1.key);
         var2.count = 0;
         ++this.modCount;
      } else {
         var2 = (LinkedListMultimap.KeyList)this.keyToKeyList.get(var1.key);
         --var2.count;
         if (var1.previousSibling == null) {
            var2.head = var1.nextSibling;
         } else {
            var1.previousSibling.nextSibling = var1.nextSibling;
         }

         if (var1.nextSibling == null) {
            var2.tail = var1.previousSibling;
         } else {
            var1.nextSibling.previousSibling = var1.previousSibling;
         }
      }

      --this.size;
   }

   private void removeAllNodes(@Nullable Object var1) {
      Iterators.clear(new LinkedListMultimap.ValueForKeyIterator(var1));
   }

   private static void checkElement(@Nullable Object var0) {
      if (var0 == null) {
         throw new NoSuchElementException();
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.head == null;
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.keyToKeyList.containsKey(var1);
   }

   public boolean containsValue(@Nullable Object var1) {
      return this.values().contains(var1);
   }

   @CanIgnoreReturnValue
   public boolean put(@Nullable K var1, @Nullable V var2) {
      this.addNode(var1, var2, (LinkedListMultimap.Node)null);
      return true;
   }

   @CanIgnoreReturnValue
   public List<V> replaceValues(@Nullable K var1, Iterable<? extends V> var2) {
      List var3 = this.getCopy(var1);
      LinkedListMultimap.ValueForKeyIterator var4 = new LinkedListMultimap.ValueForKeyIterator(var1);
      Iterator var5 = var2.iterator();

      while(var4.hasNext() && var5.hasNext()) {
         var4.next();
         var4.set(var5.next());
      }

      while(var4.hasNext()) {
         var4.next();
         var4.remove();
      }

      while(var5.hasNext()) {
         var4.add(var5.next());
      }

      return var3;
   }

   private List<V> getCopy(@Nullable Object var1) {
      return Collections.unmodifiableList(Lists.newArrayList((Iterator)(new LinkedListMultimap.ValueForKeyIterator(var1))));
   }

   @CanIgnoreReturnValue
   public List<V> removeAll(@Nullable Object var1) {
      List var2 = this.getCopy(var1);
      this.removeAllNodes(var1);
      return var2;
   }

   public void clear() {
      this.head = null;
      this.tail = null;
      this.keyToKeyList.clear();
      this.size = 0;
      ++this.modCount;
   }

   public List<V> get(@Nullable final K var1) {
      return new AbstractSequentialList<V>() {
         public int size() {
            LinkedListMultimap.KeyList var1x = (LinkedListMultimap.KeyList)LinkedListMultimap.this.keyToKeyList.get(var1);
            return var1x == null ? 0 : var1x.count;
         }

         public ListIterator<V> listIterator(int var1x) {
            return LinkedListMultimap.this.new ValueForKeyIterator(var1, var1x);
         }
      };
   }

   Set<K> createKeySet() {
      class 1KeySetImpl extends Sets.ImprovedAbstractSet<K> {
         _KeySetImpl/* $FF was: 1KeySetImpl*/() {
            super();
         }

         public int size() {
            return LinkedListMultimap.this.keyToKeyList.size();
         }

         public Iterator<K> iterator() {
            return LinkedListMultimap.this.new DistinctKeyIterator();
         }

         public boolean contains(Object var1) {
            return LinkedListMultimap.this.containsKey(var1);
         }

         public boolean remove(Object var1) {
            return !LinkedListMultimap.this.removeAll(var1).isEmpty();
         }
      }

      return new 1KeySetImpl();
   }

   public List<V> values() {
      return (List)super.values();
   }

   List<V> createValues() {
      class 1ValuesImpl extends AbstractSequentialList<V> {
         _ValuesImpl/* $FF was: 1ValuesImpl*/() {
            super();
         }

         public int size() {
            return LinkedListMultimap.this.size;
         }

         public ListIterator<V> listIterator(int var1) {
            final LinkedListMultimap.NodeIterator var2 = LinkedListMultimap.this.new NodeIterator(var1);
            return new TransformedListIterator<Entry<K, V>, V>(var2) {
               V transform(Entry<K, V> var1) {
                  return var1.getValue();
               }

               public void set(V var1) {
                  var2.setValue(var1);
               }
            };
         }
      }

      return new 1ValuesImpl();
   }

   public List<Entry<K, V>> entries() {
      return (List)super.entries();
   }

   List<Entry<K, V>> createEntries() {
      class 1EntriesImpl extends AbstractSequentialList<Entry<K, V>> {
         _EntriesImpl/* $FF was: 1EntriesImpl*/() {
            super();
         }

         public int size() {
            return LinkedListMultimap.this.size;
         }

         public ListIterator<Entry<K, V>> listIterator(int var1) {
            return LinkedListMultimap.this.new NodeIterator(var1);
         }

         public void forEach(Consumer<? super Entry<K, V>> var1) {
            Preconditions.checkNotNull(var1);

            for(LinkedListMultimap.Node var2 = LinkedListMultimap.this.head; var2 != null; var2 = var2.next) {
               var1.accept(var2);
            }

         }
      }

      return new 1EntriesImpl();
   }

   Iterator<Entry<K, V>> entryIterator() {
      throw new AssertionError("should never be called");
   }

   Map<K, Collection<V>> createAsMap() {
      return new Multimaps.AsMap(this);
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.size());
      Iterator var2 = this.entries().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.writeObject(var3.getKey());
         var1.writeObject(var3.getValue());
      }

   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.keyToKeyList = Maps.newLinkedHashMap();
      int var2 = var1.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         Object var4 = var1.readObject();
         Object var5 = var1.readObject();
         this.put(var4, var5);
      }

   }

   private class ValueForKeyIterator implements ListIterator<V> {
      final Object key;
      int nextIndex;
      LinkedListMultimap.Node<K, V> next;
      LinkedListMultimap.Node<K, V> current;
      LinkedListMultimap.Node<K, V> previous;

      ValueForKeyIterator(@Nullable Object var2) {
         super();
         this.key = var2;
         LinkedListMultimap.KeyList var3 = (LinkedListMultimap.KeyList)LinkedListMultimap.this.keyToKeyList.get(var2);
         this.next = var3 == null ? null : var3.head;
      }

      public ValueForKeyIterator(@Nullable Object var2, int var3) {
         super();
         LinkedListMultimap.KeyList var4 = (LinkedListMultimap.KeyList)LinkedListMultimap.this.keyToKeyList.get(var2);
         int var5 = var4 == null ? 0 : var4.count;
         Preconditions.checkPositionIndex(var3, var5);
         if (var3 >= var5 / 2) {
            this.previous = var4 == null ? null : var4.tail;
            this.nextIndex = var5;

            while(var3++ < var5) {
               this.previous();
            }
         } else {
            this.next = var4 == null ? null : var4.head;

            while(var3-- > 0) {
               this.next();
            }
         }

         this.key = var2;
         this.current = null;
      }

      public boolean hasNext() {
         return this.next != null;
      }

      @CanIgnoreReturnValue
      public V next() {
         LinkedListMultimap.checkElement(this.next);
         this.previous = this.current = this.next;
         this.next = this.next.nextSibling;
         ++this.nextIndex;
         return this.current.value;
      }

      public boolean hasPrevious() {
         return this.previous != null;
      }

      @CanIgnoreReturnValue
      public V previous() {
         LinkedListMultimap.checkElement(this.previous);
         this.next = this.current = this.previous;
         this.previous = this.previous.previousSibling;
         --this.nextIndex;
         return this.current.value;
      }

      public int nextIndex() {
         return this.nextIndex;
      }

      public int previousIndex() {
         return this.nextIndex - 1;
      }

      public void remove() {
         CollectPreconditions.checkRemove(this.current != null);
         if (this.current != this.next) {
            this.previous = this.current.previousSibling;
            --this.nextIndex;
         } else {
            this.next = this.current.nextSibling;
         }

         LinkedListMultimap.this.removeNode(this.current);
         this.current = null;
      }

      public void set(V var1) {
         Preconditions.checkState(this.current != null);
         this.current.value = var1;
      }

      public void add(V var1) {
         this.previous = LinkedListMultimap.this.addNode(this.key, var1, this.next);
         ++this.nextIndex;
         this.current = null;
      }
   }

   private class DistinctKeyIterator implements Iterator<K> {
      final Set<K> seenKeys;
      LinkedListMultimap.Node<K, V> next;
      LinkedListMultimap.Node<K, V> current;
      int expectedModCount;

      private DistinctKeyIterator() {
         super();
         this.seenKeys = Sets.newHashSetWithExpectedSize(LinkedListMultimap.this.keySet().size());
         this.next = LinkedListMultimap.this.head;
         this.expectedModCount = LinkedListMultimap.this.modCount;
      }

      private void checkForConcurrentModification() {
         if (LinkedListMultimap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      public boolean hasNext() {
         this.checkForConcurrentModification();
         return this.next != null;
      }

      public K next() {
         this.checkForConcurrentModification();
         LinkedListMultimap.checkElement(this.next);
         this.current = this.next;
         this.seenKeys.add(this.current.key);

         do {
            this.next = this.next.next;
         } while(this.next != null && !this.seenKeys.add(this.next.key));

         return this.current.key;
      }

      public void remove() {
         this.checkForConcurrentModification();
         CollectPreconditions.checkRemove(this.current != null);
         LinkedListMultimap.this.removeAllNodes(this.current.key);
         this.current = null;
         this.expectedModCount = LinkedListMultimap.this.modCount;
      }

      // $FF: synthetic method
      DistinctKeyIterator(Object var2) {
         this();
      }
   }

   private class NodeIterator implements ListIterator<Entry<K, V>> {
      int nextIndex;
      LinkedListMultimap.Node<K, V> next;
      LinkedListMultimap.Node<K, V> current;
      LinkedListMultimap.Node<K, V> previous;
      int expectedModCount;

      NodeIterator(int var2) {
         super();
         this.expectedModCount = LinkedListMultimap.this.modCount;
         int var3 = LinkedListMultimap.this.size();
         Preconditions.checkPositionIndex(var2, var3);
         if (var2 >= var3 / 2) {
            this.previous = LinkedListMultimap.this.tail;
            this.nextIndex = var3;

            while(var2++ < var3) {
               this.previous();
            }
         } else {
            this.next = LinkedListMultimap.this.head;

            while(var2-- > 0) {
               this.next();
            }
         }

         this.current = null;
      }

      private void checkForConcurrentModification() {
         if (LinkedListMultimap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      public boolean hasNext() {
         this.checkForConcurrentModification();
         return this.next != null;
      }

      @CanIgnoreReturnValue
      public LinkedListMultimap.Node<K, V> next() {
         this.checkForConcurrentModification();
         LinkedListMultimap.checkElement(this.next);
         this.previous = this.current = this.next;
         this.next = this.next.next;
         ++this.nextIndex;
         return this.current;
      }

      public void remove() {
         this.checkForConcurrentModification();
         CollectPreconditions.checkRemove(this.current != null);
         if (this.current != this.next) {
            this.previous = this.current.previous;
            --this.nextIndex;
         } else {
            this.next = this.current.next;
         }

         LinkedListMultimap.this.removeNode(this.current);
         this.current = null;
         this.expectedModCount = LinkedListMultimap.this.modCount;
      }

      public boolean hasPrevious() {
         this.checkForConcurrentModification();
         return this.previous != null;
      }

      @CanIgnoreReturnValue
      public LinkedListMultimap.Node<K, V> previous() {
         this.checkForConcurrentModification();
         LinkedListMultimap.checkElement(this.previous);
         this.next = this.current = this.previous;
         this.previous = this.previous.previous;
         --this.nextIndex;
         return this.current;
      }

      public int nextIndex() {
         return this.nextIndex;
      }

      public int previousIndex() {
         return this.nextIndex - 1;
      }

      public void set(Entry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Entry<K, V> var1) {
         throw new UnsupportedOperationException();
      }

      void setValue(V var1) {
         Preconditions.checkState(this.current != null);
         this.current.value = var1;
      }
   }

   private static class KeyList<K, V> {
      LinkedListMultimap.Node<K, V> head;
      LinkedListMultimap.Node<K, V> tail;
      int count;

      KeyList(LinkedListMultimap.Node<K, V> var1) {
         super();
         this.head = var1;
         this.tail = var1;
         var1.previousSibling = null;
         var1.nextSibling = null;
         this.count = 1;
      }
   }

   private static final class Node<K, V> extends AbstractMapEntry<K, V> {
      final K key;
      V value;
      LinkedListMultimap.Node<K, V> next;
      LinkedListMultimap.Node<K, V> previous;
      LinkedListMultimap.Node<K, V> nextSibling;
      LinkedListMultimap.Node<K, V> previousSibling;

      Node(@Nullable K var1, @Nullable V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(@Nullable V var1) {
         Object var2 = this.value;
         this.value = var1;
         return var2;
      }
   }
}
