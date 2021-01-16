package com.google.gson.internal;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class LinkedTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {
   private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>() {
      public int compare(Comparable var1, Comparable var2) {
         return var1.compareTo(var2);
      }
   };
   Comparator<? super K> comparator;
   LinkedTreeMap.Node<K, V> root;
   int size;
   int modCount;
   final LinkedTreeMap.Node<K, V> header;
   private LinkedTreeMap<K, V>.EntrySet entrySet;
   private LinkedTreeMap<K, V>.KeySet keySet;

   public LinkedTreeMap() {
      this(NATURAL_ORDER);
   }

   public LinkedTreeMap(Comparator<? super K> var1) {
      super();
      this.size = 0;
      this.modCount = 0;
      this.header = new LinkedTreeMap.Node();
      this.comparator = var1 != null ? var1 : NATURAL_ORDER;
   }

   public int size() {
      return this.size;
   }

   public V get(Object var1) {
      LinkedTreeMap.Node var2 = this.findByObject(var1);
      return var2 != null ? var2.value : null;
   }

   public boolean containsKey(Object var1) {
      return this.findByObject(var1) != null;
   }

   public V put(K var1, V var2) {
      if (var1 == null) {
         throw new NullPointerException("key == null");
      } else {
         LinkedTreeMap.Node var3 = this.find(var1, true);
         Object var4 = var3.value;
         var3.value = var2;
         return var4;
      }
   }

   public void clear() {
      this.root = null;
      this.size = 0;
      ++this.modCount;
      LinkedTreeMap.Node var1 = this.header;
      var1.next = var1.prev = var1;
   }

   public V remove(Object var1) {
      LinkedTreeMap.Node var2 = this.removeInternalByKey(var1);
      return var2 != null ? var2.value : null;
   }

   LinkedTreeMap.Node<K, V> find(K var1, boolean var2) {
      Comparator var3 = this.comparator;
      LinkedTreeMap.Node var4 = this.root;
      int var5 = 0;
      LinkedTreeMap.Node var7;
      if (var4 != null) {
         Comparable var6 = var3 == NATURAL_ORDER ? (Comparable)var1 : null;

         while(true) {
            var5 = var6 != null ? var6.compareTo(var4.key) : var3.compare(var1, var4.key);
            if (var5 == 0) {
               return var4;
            }

            var7 = var5 < 0 ? var4.left : var4.right;
            if (var7 == null) {
               break;
            }

            var4 = var7;
         }
      }

      if (!var2) {
         return null;
      } else {
         LinkedTreeMap.Node var8 = this.header;
         if (var4 == null) {
            if (var3 == NATURAL_ORDER && !(var1 instanceof Comparable)) {
               throw new ClassCastException(var1.getClass().getName() + " is not Comparable");
            }

            var7 = new LinkedTreeMap.Node(var4, var1, var8, var8.prev);
            this.root = var7;
         } else {
            var7 = new LinkedTreeMap.Node(var4, var1, var8, var8.prev);
            if (var5 < 0) {
               var4.left = var7;
            } else {
               var4.right = var7;
            }

            this.rebalance(var4, true);
         }

         ++this.size;
         ++this.modCount;
         return var7;
      }
   }

   LinkedTreeMap.Node<K, V> findByObject(Object var1) {
      try {
         return var1 != null ? this.find(var1, false) : null;
      } catch (ClassCastException var3) {
         return null;
      }
   }

   LinkedTreeMap.Node<K, V> findByEntry(Entry<?, ?> var1) {
      LinkedTreeMap.Node var2 = this.findByObject(var1.getKey());
      boolean var3 = var2 != null && this.equal(var2.value, var1.getValue());
      return var3 ? var2 : null;
   }

   private boolean equal(Object var1, Object var2) {
      return var1 == var2 || var1 != null && var1.equals(var2);
   }

   void removeInternal(LinkedTreeMap.Node<K, V> var1, boolean var2) {
      if (var2) {
         var1.prev.next = var1.next;
         var1.next.prev = var1.prev;
      }

      LinkedTreeMap.Node var3 = var1.left;
      LinkedTreeMap.Node var4 = var1.right;
      LinkedTreeMap.Node var5 = var1.parent;
      if (var3 != null && var4 != null) {
         LinkedTreeMap.Node var6 = var3.height > var4.height ? var3.last() : var4.first();
         this.removeInternal(var6, false);
         int var7 = 0;
         var3 = var1.left;
         if (var3 != null) {
            var7 = var3.height;
            var6.left = var3;
            var3.parent = var6;
            var1.left = null;
         }

         int var8 = 0;
         var4 = var1.right;
         if (var4 != null) {
            var8 = var4.height;
            var6.right = var4;
            var4.parent = var6;
            var1.right = null;
         }

         var6.height = Math.max(var7, var8) + 1;
         this.replaceInParent(var1, var6);
      } else {
         if (var3 != null) {
            this.replaceInParent(var1, var3);
            var1.left = null;
         } else if (var4 != null) {
            this.replaceInParent(var1, var4);
            var1.right = null;
         } else {
            this.replaceInParent(var1, (LinkedTreeMap.Node)null);
         }

         this.rebalance(var5, false);
         --this.size;
         ++this.modCount;
      }
   }

   LinkedTreeMap.Node<K, V> removeInternalByKey(Object var1) {
      LinkedTreeMap.Node var2 = this.findByObject(var1);
      if (var2 != null) {
         this.removeInternal(var2, true);
      }

      return var2;
   }

   private void replaceInParent(LinkedTreeMap.Node<K, V> var1, LinkedTreeMap.Node<K, V> var2) {
      LinkedTreeMap.Node var3 = var1.parent;
      var1.parent = null;
      if (var2 != null) {
         var2.parent = var3;
      }

      if (var3 != null) {
         if (var3.left == var1) {
            var3.left = var2;
         } else {
            assert var3.right == var1;

            var3.right = var2;
         }
      } else {
         this.root = var2;
      }

   }

   private void rebalance(LinkedTreeMap.Node<K, V> var1, boolean var2) {
      for(LinkedTreeMap.Node var3 = var1; var3 != null; var3 = var3.parent) {
         LinkedTreeMap.Node var4 = var3.left;
         LinkedTreeMap.Node var5 = var3.right;
         int var6 = var4 != null ? var4.height : 0;
         int var7 = var5 != null ? var5.height : 0;
         int var8 = var6 - var7;
         LinkedTreeMap.Node var9;
         LinkedTreeMap.Node var10;
         int var11;
         int var12;
         int var13;
         if (var8 == -2) {
            var9 = var5.left;
            var10 = var5.right;
            var11 = var10 != null ? var10.height : 0;
            var12 = var9 != null ? var9.height : 0;
            var13 = var12 - var11;
            if (var13 != -1 && (var13 != 0 || var2)) {
               assert var13 == 1;

               this.rotateRight(var5);
               this.rotateLeft(var3);
            } else {
               this.rotateLeft(var3);
            }

            if (var2) {
               break;
            }
         } else if (var8 == 2) {
            var9 = var4.left;
            var10 = var4.right;
            var11 = var10 != null ? var10.height : 0;
            var12 = var9 != null ? var9.height : 0;
            var13 = var12 - var11;
            if (var13 == 1 || var13 == 0 && !var2) {
               this.rotateRight(var3);
            } else {
               assert var13 == -1;

               this.rotateLeft(var4);
               this.rotateRight(var3);
            }

            if (var2) {
               break;
            }
         } else if (var8 == 0) {
            var3.height = var6 + 1;
            if (var2) {
               break;
            }
         } else {
            assert var8 == -1 || var8 == 1;

            var3.height = Math.max(var6, var7) + 1;
            if (!var2) {
               break;
            }
         }
      }

   }

   private void rotateLeft(LinkedTreeMap.Node<K, V> var1) {
      LinkedTreeMap.Node var2 = var1.left;
      LinkedTreeMap.Node var3 = var1.right;
      LinkedTreeMap.Node var4 = var3.left;
      LinkedTreeMap.Node var5 = var3.right;
      var1.right = var4;
      if (var4 != null) {
         var4.parent = var1;
      }

      this.replaceInParent(var1, var3);
      var3.left = var1;
      var1.parent = var3;
      var1.height = Math.max(var2 != null ? var2.height : 0, var4 != null ? var4.height : 0) + 1;
      var3.height = Math.max(var1.height, var5 != null ? var5.height : 0) + 1;
   }

   private void rotateRight(LinkedTreeMap.Node<K, V> var1) {
      LinkedTreeMap.Node var2 = var1.left;
      LinkedTreeMap.Node var3 = var1.right;
      LinkedTreeMap.Node var4 = var2.left;
      LinkedTreeMap.Node var5 = var2.right;
      var1.left = var5;
      if (var5 != null) {
         var5.parent = var1;
      }

      this.replaceInParent(var1, var2);
      var2.right = var1;
      var1.parent = var2;
      var1.height = Math.max(var3 != null ? var3.height : 0, var5 != null ? var5.height : 0) + 1;
      var2.height = Math.max(var1.height, var4 != null ? var4.height : 0) + 1;
   }

   public Set<Entry<K, V>> entrySet() {
      LinkedTreeMap.EntrySet var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new LinkedTreeMap.EntrySet());
   }

   public Set<K> keySet() {
      LinkedTreeMap.KeySet var1 = this.keySet;
      return var1 != null ? var1 : (this.keySet = new LinkedTreeMap.KeySet());
   }

   private Object writeReplace() throws ObjectStreamException {
      return new LinkedHashMap(this);
   }

   final class KeySet extends AbstractSet<K> {
      KeySet() {
         super();
      }

      public int size() {
         return LinkedTreeMap.this.size;
      }

      public Iterator<K> iterator() {
         return new LinkedTreeMap<K, V>.LinkedTreeMapIterator<K>() {
            public K next() {
               return this.nextNode().key;
            }
         };
      }

      public boolean contains(Object var1) {
         return LinkedTreeMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return LinkedTreeMap.this.removeInternalByKey(var1) != null;
      }

      public void clear() {
         LinkedTreeMap.this.clear();
      }
   }

   class EntrySet extends AbstractSet<Entry<K, V>> {
      EntrySet() {
         super();
      }

      public int size() {
         return LinkedTreeMap.this.size;
      }

      public Iterator<Entry<K, V>> iterator() {
         return new LinkedTreeMap<K, V>.LinkedTreeMapIterator<Entry<K, V>>() {
            public Entry<K, V> next() {
               return this.nextNode();
            }
         };
      }

      public boolean contains(Object var1) {
         return var1 instanceof Entry && LinkedTreeMap.this.findByEntry((Entry)var1) != null;
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            LinkedTreeMap.Node var2 = LinkedTreeMap.this.findByEntry((Entry)var1);
            if (var2 == null) {
               return false;
            } else {
               LinkedTreeMap.this.removeInternal(var2, true);
               return true;
            }
         }
      }

      public void clear() {
         LinkedTreeMap.this.clear();
      }
   }

   private abstract class LinkedTreeMapIterator<T> implements Iterator<T> {
      LinkedTreeMap.Node<K, V> next;
      LinkedTreeMap.Node<K, V> lastReturned;
      int expectedModCount;

      LinkedTreeMapIterator() {
         super();
         this.next = LinkedTreeMap.this.header.next;
         this.lastReturned = null;
         this.expectedModCount = LinkedTreeMap.this.modCount;
      }

      public final boolean hasNext() {
         return this.next != LinkedTreeMap.this.header;
      }

      final LinkedTreeMap.Node<K, V> nextNode() {
         LinkedTreeMap.Node var1 = this.next;
         if (var1 == LinkedTreeMap.this.header) {
            throw new NoSuchElementException();
         } else if (LinkedTreeMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.next = var1.next;
            return this.lastReturned = var1;
         }
      }

      public final void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else {
            LinkedTreeMap.this.removeInternal(this.lastReturned, true);
            this.lastReturned = null;
            this.expectedModCount = LinkedTreeMap.this.modCount;
         }
      }
   }

   static final class Node<K, V> implements Entry<K, V> {
      LinkedTreeMap.Node<K, V> parent;
      LinkedTreeMap.Node<K, V> left;
      LinkedTreeMap.Node<K, V> right;
      LinkedTreeMap.Node<K, V> next;
      LinkedTreeMap.Node<K, V> prev;
      final K key;
      V value;
      int height;

      Node() {
         super();
         this.key = null;
         this.next = this.prev = this;
      }

      Node(LinkedTreeMap.Node<K, V> var1, K var2, LinkedTreeMap.Node<K, V> var3, LinkedTreeMap.Node<K, V> var4) {
         super();
         this.parent = var1;
         this.key = var2;
         this.height = 1;
         this.next = var3;
         this.prev = var4;
         var4.next = this;
         var3.prev = this;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         Object var2 = this.value;
         this.value = var1;
         return var2;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Entry)) {
            return false;
         } else {
            boolean var10000;
            label38: {
               label27: {
                  Entry var2 = (Entry)var1;
                  if (this.key == null) {
                     if (var2.getKey() != null) {
                        break label27;
                     }
                  } else if (!this.key.equals(var2.getKey())) {
                     break label27;
                  }

                  if (this.value == null) {
                     if (var2.getValue() == null) {
                        break label38;
                     }
                  } else if (this.value.equals(var2.getValue())) {
                     break label38;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "=" + this.value;
      }

      public LinkedTreeMap.Node<K, V> first() {
         LinkedTreeMap.Node var1 = this;

         for(LinkedTreeMap.Node var2 = this.left; var2 != null; var2 = var2.left) {
            var1 = var2;
         }

         return var1;
      }

      public LinkedTreeMap.Node<K, V> last() {
         LinkedTreeMap.Node var1 = this;

         for(LinkedTreeMap.Node var2 = this.right; var2 != null; var2 = var2.right) {
            var1 = var2;
         }

         return var1;
      }
   }
}
