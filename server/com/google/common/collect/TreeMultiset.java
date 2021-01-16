package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class TreeMultiset<E> extends AbstractSortedMultiset<E> implements Serializable {
   private final transient TreeMultiset.Reference<TreeMultiset.AvlNode<E>> rootReference;
   private final transient GeneralRange<E> range;
   private final transient TreeMultiset.AvlNode<E> header;
   @GwtIncompatible
   private static final long serialVersionUID = 1L;

   public static <E extends Comparable> TreeMultiset<E> create() {
      return new TreeMultiset(Ordering.natural());
   }

   public static <E> TreeMultiset<E> create(@Nullable Comparator<? super E> var0) {
      return var0 == null ? new TreeMultiset(Ordering.natural()) : new TreeMultiset(var0);
   }

   public static <E extends Comparable> TreeMultiset<E> create(Iterable<? extends E> var0) {
      TreeMultiset var1 = create();
      Iterables.addAll(var1, var0);
      return var1;
   }

   TreeMultiset(TreeMultiset.Reference<TreeMultiset.AvlNode<E>> var1, GeneralRange<E> var2, TreeMultiset.AvlNode<E> var3) {
      super(var2.comparator());
      this.rootReference = var1;
      this.range = var2;
      this.header = var3;
   }

   TreeMultiset(Comparator<? super E> var1) {
      super(var1);
      this.range = GeneralRange.all(var1);
      this.header = new TreeMultiset.AvlNode((Object)null, 1);
      successor(this.header, this.header);
      this.rootReference = new TreeMultiset.Reference();
   }

   private long aggregateForEntries(TreeMultiset.Aggregate var1) {
      TreeMultiset.AvlNode var2 = (TreeMultiset.AvlNode)this.rootReference.get();
      long var3 = var1.treeAggregate(var2);
      if (this.range.hasLowerBound()) {
         var3 -= this.aggregateBelowRange(var1, var2);
      }

      if (this.range.hasUpperBound()) {
         var3 -= this.aggregateAboveRange(var1, var2);
      }

      return var3;
   }

   private long aggregateBelowRange(TreeMultiset.Aggregate var1, @Nullable TreeMultiset.AvlNode<E> var2) {
      if (var2 == null) {
         return 0L;
      } else {
         int var3 = this.comparator().compare(this.range.getLowerEndpoint(), var2.elem);
         if (var3 < 0) {
            return this.aggregateBelowRange(var1, var2.left);
         } else if (var3 == 0) {
            switch(this.range.getLowerBoundType()) {
            case OPEN:
               return (long)var1.nodeAggregate(var2) + var1.treeAggregate(var2.left);
            case CLOSED:
               return var1.treeAggregate(var2.left);
            default:
               throw new AssertionError();
            }
         } else {
            return var1.treeAggregate(var2.left) + (long)var1.nodeAggregate(var2) + this.aggregateBelowRange(var1, var2.right);
         }
      }
   }

   private long aggregateAboveRange(TreeMultiset.Aggregate var1, @Nullable TreeMultiset.AvlNode<E> var2) {
      if (var2 == null) {
         return 0L;
      } else {
         int var3 = this.comparator().compare(this.range.getUpperEndpoint(), var2.elem);
         if (var3 > 0) {
            return this.aggregateAboveRange(var1, var2.right);
         } else if (var3 == 0) {
            switch(this.range.getUpperBoundType()) {
            case OPEN:
               return (long)var1.nodeAggregate(var2) + var1.treeAggregate(var2.right);
            case CLOSED:
               return var1.treeAggregate(var2.right);
            default:
               throw new AssertionError();
            }
         } else {
            return var1.treeAggregate(var2.right) + (long)var1.nodeAggregate(var2) + this.aggregateAboveRange(var1, var2.left);
         }
      }
   }

   public int size() {
      return Ints.saturatedCast(this.aggregateForEntries(TreeMultiset.Aggregate.SIZE));
   }

   int distinctElements() {
      return Ints.saturatedCast(this.aggregateForEntries(TreeMultiset.Aggregate.DISTINCT));
   }

   public int count(@Nullable Object var1) {
      try {
         TreeMultiset.AvlNode var3 = (TreeMultiset.AvlNode)this.rootReference.get();
         return this.range.contains(var1) && var3 != null ? var3.count(this.comparator(), var1) : 0;
      } catch (ClassCastException var4) {
         return 0;
      } catch (NullPointerException var5) {
         return 0;
      }
   }

   @CanIgnoreReturnValue
   public int add(@Nullable E var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "occurrences");
      if (var2 == 0) {
         return this.count(var1);
      } else {
         Preconditions.checkArgument(this.range.contains(var1));
         TreeMultiset.AvlNode var3 = (TreeMultiset.AvlNode)this.rootReference.get();
         if (var3 == null) {
            this.comparator().compare(var1, var1);
            TreeMultiset.AvlNode var6 = new TreeMultiset.AvlNode(var1, var2);
            successor(this.header, var6, this.header);
            this.rootReference.checkAndSet(var3, var6);
            return 0;
         } else {
            int[] var4 = new int[1];
            TreeMultiset.AvlNode var5 = var3.add(this.comparator(), var1, var2, var4);
            this.rootReference.checkAndSet(var3, var5);
            return var4[0];
         }
      }
   }

   @CanIgnoreReturnValue
   public int remove(@Nullable Object var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "occurrences");
      if (var2 == 0) {
         return this.count(var1);
      } else {
         TreeMultiset.AvlNode var3 = (TreeMultiset.AvlNode)this.rootReference.get();
         int[] var4 = new int[1];

         TreeMultiset.AvlNode var5;
         try {
            if (!this.range.contains(var1) || var3 == null) {
               return 0;
            }

            var5 = var3.remove(this.comparator(), var1, var2, var4);
         } catch (ClassCastException var7) {
            return 0;
         } catch (NullPointerException var8) {
            return 0;
         }

         this.rootReference.checkAndSet(var3, var5);
         return var4[0];
      }
   }

   @CanIgnoreReturnValue
   public int setCount(@Nullable E var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "count");
      if (!this.range.contains(var1)) {
         Preconditions.checkArgument(var2 == 0);
         return 0;
      } else {
         TreeMultiset.AvlNode var3 = (TreeMultiset.AvlNode)this.rootReference.get();
         if (var3 == null) {
            if (var2 > 0) {
               this.add(var1, var2);
            }

            return 0;
         } else {
            int[] var4 = new int[1];
            TreeMultiset.AvlNode var5 = var3.setCount(this.comparator(), var1, var2, var4);
            this.rootReference.checkAndSet(var3, var5);
            return var4[0];
         }
      }
   }

   @CanIgnoreReturnValue
   public boolean setCount(@Nullable E var1, int var2, int var3) {
      CollectPreconditions.checkNonnegative(var3, "newCount");
      CollectPreconditions.checkNonnegative(var2, "oldCount");
      Preconditions.checkArgument(this.range.contains(var1));
      TreeMultiset.AvlNode var4 = (TreeMultiset.AvlNode)this.rootReference.get();
      if (var4 == null) {
         if (var2 == 0) {
            if (var3 > 0) {
               this.add(var1, var3);
            }

            return true;
         } else {
            return false;
         }
      } else {
         int[] var5 = new int[1];
         TreeMultiset.AvlNode var6 = var4.setCount(this.comparator(), var1, var2, var3, var5);
         this.rootReference.checkAndSet(var4, var6);
         return var5[0] == var2;
      }
   }

   private Multiset.Entry<E> wrapEntry(final TreeMultiset.AvlNode<E> var1) {
      return new Multisets.AbstractEntry<E>() {
         public E getElement() {
            return var1.getElement();
         }

         public int getCount() {
            int var1x = var1.getCount();
            return var1x == 0 ? TreeMultiset.this.count(this.getElement()) : var1x;
         }
      };
   }

   @Nullable
   private TreeMultiset.AvlNode<E> firstNode() {
      TreeMultiset.AvlNode var1 = (TreeMultiset.AvlNode)this.rootReference.get();
      if (var1 == null) {
         return null;
      } else {
         TreeMultiset.AvlNode var2;
         if (this.range.hasLowerBound()) {
            Object var3 = this.range.getLowerEndpoint();
            var2 = ((TreeMultiset.AvlNode)this.rootReference.get()).ceiling(this.comparator(), var3);
            if (var2 == null) {
               return null;
            }

            if (this.range.getLowerBoundType() == BoundType.OPEN && this.comparator().compare(var3, var2.getElement()) == 0) {
               var2 = var2.succ;
            }
         } else {
            var2 = this.header.succ;
         }

         return var2 != this.header && this.range.contains(var2.getElement()) ? var2 : null;
      }
   }

   @Nullable
   private TreeMultiset.AvlNode<E> lastNode() {
      TreeMultiset.AvlNode var1 = (TreeMultiset.AvlNode)this.rootReference.get();
      if (var1 == null) {
         return null;
      } else {
         TreeMultiset.AvlNode var2;
         if (this.range.hasUpperBound()) {
            Object var3 = this.range.getUpperEndpoint();
            var2 = ((TreeMultiset.AvlNode)this.rootReference.get()).floor(this.comparator(), var3);
            if (var2 == null) {
               return null;
            }

            if (this.range.getUpperBoundType() == BoundType.OPEN && this.comparator().compare(var3, var2.getElement()) == 0) {
               var2 = var2.pred;
            }
         } else {
            var2 = this.header.pred;
         }

         return var2 != this.header && this.range.contains(var2.getElement()) ? var2 : null;
      }
   }

   Iterator<Multiset.Entry<E>> entryIterator() {
      return new Iterator<Multiset.Entry<E>>() {
         TreeMultiset.AvlNode<E> current = TreeMultiset.this.firstNode();
         Multiset.Entry<E> prevEntry;

         public boolean hasNext() {
            if (this.current == null) {
               return false;
            } else if (TreeMultiset.this.range.tooHigh(this.current.getElement())) {
               this.current = null;
               return false;
            } else {
               return true;
            }
         }

         public Multiset.Entry<E> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Multiset.Entry var1 = TreeMultiset.this.wrapEntry(this.current);
               this.prevEntry = var1;
               if (this.current.succ == TreeMultiset.this.header) {
                  this.current = null;
               } else {
                  this.current = this.current.succ;
               }

               return var1;
            }
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.prevEntry != null);
            TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
            this.prevEntry = null;
         }
      };
   }

   Iterator<Multiset.Entry<E>> descendingEntryIterator() {
      return new Iterator<Multiset.Entry<E>>() {
         TreeMultiset.AvlNode<E> current = TreeMultiset.this.lastNode();
         Multiset.Entry<E> prevEntry = null;

         public boolean hasNext() {
            if (this.current == null) {
               return false;
            } else if (TreeMultiset.this.range.tooLow(this.current.getElement())) {
               this.current = null;
               return false;
            } else {
               return true;
            }
         }

         public Multiset.Entry<E> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Multiset.Entry var1 = TreeMultiset.this.wrapEntry(this.current);
               this.prevEntry = var1;
               if (this.current.pred == TreeMultiset.this.header) {
                  this.current = null;
               } else {
                  this.current = this.current.pred;
               }

               return var1;
            }
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.prevEntry != null);
            TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
            this.prevEntry = null;
         }
      };
   }

   public SortedMultiset<E> headMultiset(@Nullable E var1, BoundType var2) {
      return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.upTo(this.comparator(), var1, var2)), this.header);
   }

   public SortedMultiset<E> tailMultiset(@Nullable E var1, BoundType var2) {
      return new TreeMultiset(this.rootReference, this.range.intersect(GeneralRange.downTo(this.comparator(), var1, var2)), this.header);
   }

   static int distinctElements(@Nullable TreeMultiset.AvlNode<?> var0) {
      return var0 == null ? 0 : var0.distinctElements;
   }

   private static <T> void successor(TreeMultiset.AvlNode<T> var0, TreeMultiset.AvlNode<T> var1) {
      var0.succ = var1;
      var1.pred = var0;
   }

   private static <T> void successor(TreeMultiset.AvlNode<T> var0, TreeMultiset.AvlNode<T> var1, TreeMultiset.AvlNode<T> var2) {
      successor(var0, var1);
      successor(var1, var2);
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.elementSet().comparator());
      Serialization.writeMultiset(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Comparator var2 = (Comparator)var1.readObject();
      Serialization.getFieldSetter(AbstractSortedMultiset.class, "comparator").set(this, var2);
      Serialization.getFieldSetter(TreeMultiset.class, "range").set(this, GeneralRange.all(var2));
      Serialization.getFieldSetter(TreeMultiset.class, "rootReference").set(this, new TreeMultiset.Reference());
      TreeMultiset.AvlNode var3 = new TreeMultiset.AvlNode((Object)null, 1);
      Serialization.getFieldSetter(TreeMultiset.class, "header").set(this, var3);
      successor(var3, var3);
      Serialization.populateMultiset(this, var1);
   }

   private static final class AvlNode<E> extends Multisets.AbstractEntry<E> {
      @Nullable
      private final E elem;
      private int elemCount;
      private int distinctElements;
      private long totalCount;
      private int height;
      private TreeMultiset.AvlNode<E> left;
      private TreeMultiset.AvlNode<E> right;
      private TreeMultiset.AvlNode<E> pred;
      private TreeMultiset.AvlNode<E> succ;

      AvlNode(@Nullable E var1, int var2) {
         super();
         Preconditions.checkArgument(var2 > 0);
         this.elem = var1;
         this.elemCount = var2;
         this.totalCount = (long)var2;
         this.distinctElements = 1;
         this.height = 1;
         this.left = null;
         this.right = null;
      }

      public int count(Comparator<? super E> var1, E var2) {
         int var3 = var1.compare(var2, this.elem);
         if (var3 < 0) {
            return this.left == null ? 0 : this.left.count(var1, var2);
         } else if (var3 > 0) {
            return this.right == null ? 0 : this.right.count(var1, var2);
         } else {
            return this.elemCount;
         }
      }

      private TreeMultiset.AvlNode<E> addRightChild(E var1, int var2) {
         this.right = new TreeMultiset.AvlNode(var1, var2);
         TreeMultiset.successor(this, this.right, this.succ);
         this.height = Math.max(2, this.height);
         ++this.distinctElements;
         this.totalCount += (long)var2;
         return this;
      }

      private TreeMultiset.AvlNode<E> addLeftChild(E var1, int var2) {
         this.left = new TreeMultiset.AvlNode(var1, var2);
         TreeMultiset.successor(this.pred, this.left, this);
         this.height = Math.max(2, this.height);
         ++this.distinctElements;
         this.totalCount += (long)var2;
         return this;
      }

      TreeMultiset.AvlNode<E> add(Comparator<? super E> var1, @Nullable E var2, int var3, int[] var4) {
         int var5 = var1.compare(var2, this.elem);
         int var7;
         TreeMultiset.AvlNode var8;
         if (var5 < 0) {
            var8 = this.left;
            if (var8 == null) {
               var4[0] = 0;
               return this.addLeftChild(var2, var3);
            } else {
               var7 = var8.height;
               this.left = var8.add(var1, var2, var3, var4);
               if (var4[0] == 0) {
                  ++this.distinctElements;
               }

               this.totalCount += (long)var3;
               return this.left.height == var7 ? this : this.rebalance();
            }
         } else if (var5 > 0) {
            var8 = this.right;
            if (var8 == null) {
               var4[0] = 0;
               return this.addRightChild(var2, var3);
            } else {
               var7 = var8.height;
               this.right = var8.add(var1, var2, var3, var4);
               if (var4[0] == 0) {
                  ++this.distinctElements;
               }

               this.totalCount += (long)var3;
               return this.right.height == var7 ? this : this.rebalance();
            }
         } else {
            var4[0] = this.elemCount;
            long var6 = (long)this.elemCount + (long)var3;
            Preconditions.checkArgument(var6 <= 2147483647L);
            this.elemCount += var3;
            this.totalCount += (long)var3;
            return this;
         }
      }

      TreeMultiset.AvlNode<E> remove(Comparator<? super E> var1, @Nullable E var2, int var3, int[] var4) {
         int var5 = var1.compare(var2, this.elem);
         TreeMultiset.AvlNode var6;
         if (var5 < 0) {
            var6 = this.left;
            if (var6 == null) {
               var4[0] = 0;
               return this;
            } else {
               this.left = var6.remove(var1, var2, var3, var4);
               if (var4[0] > 0) {
                  if (var3 >= var4[0]) {
                     --this.distinctElements;
                     this.totalCount -= (long)var4[0];
                  } else {
                     this.totalCount -= (long)var3;
                  }
               }

               return var4[0] == 0 ? this : this.rebalance();
            }
         } else if (var5 > 0) {
            var6 = this.right;
            if (var6 == null) {
               var4[0] = 0;
               return this;
            } else {
               this.right = var6.remove(var1, var2, var3, var4);
               if (var4[0] > 0) {
                  if (var3 >= var4[0]) {
                     --this.distinctElements;
                     this.totalCount -= (long)var4[0];
                  } else {
                     this.totalCount -= (long)var3;
                  }
               }

               return this.rebalance();
            }
         } else {
            var4[0] = this.elemCount;
            if (var3 >= this.elemCount) {
               return this.deleteMe();
            } else {
               this.elemCount -= var3;
               this.totalCount -= (long)var3;
               return this;
            }
         }
      }

      TreeMultiset.AvlNode<E> setCount(Comparator<? super E> var1, @Nullable E var2, int var3, int[] var4) {
         int var5 = var1.compare(var2, this.elem);
         TreeMultiset.AvlNode var6;
         if (var5 < 0) {
            var6 = this.left;
            if (var6 == null) {
               var4[0] = 0;
               return var3 > 0 ? this.addLeftChild(var2, var3) : this;
            } else {
               this.left = var6.setCount(var1, var2, var3, var4);
               if (var3 == 0 && var4[0] != 0) {
                  --this.distinctElements;
               } else if (var3 > 0 && var4[0] == 0) {
                  ++this.distinctElements;
               }

               this.totalCount += (long)(var3 - var4[0]);
               return this.rebalance();
            }
         } else if (var5 > 0) {
            var6 = this.right;
            if (var6 == null) {
               var4[0] = 0;
               return var3 > 0 ? this.addRightChild(var2, var3) : this;
            } else {
               this.right = var6.setCount(var1, var2, var3, var4);
               if (var3 == 0 && var4[0] != 0) {
                  --this.distinctElements;
               } else if (var3 > 0 && var4[0] == 0) {
                  ++this.distinctElements;
               }

               this.totalCount += (long)(var3 - var4[0]);
               return this.rebalance();
            }
         } else {
            var4[0] = this.elemCount;
            if (var3 == 0) {
               return this.deleteMe();
            } else {
               this.totalCount += (long)(var3 - this.elemCount);
               this.elemCount = var3;
               return this;
            }
         }
      }

      TreeMultiset.AvlNode<E> setCount(Comparator<? super E> var1, @Nullable E var2, int var3, int var4, int[] var5) {
         int var6 = var1.compare(var2, this.elem);
         TreeMultiset.AvlNode var7;
         if (var6 < 0) {
            var7 = this.left;
            if (var7 == null) {
               var5[0] = 0;
               return var3 == 0 && var4 > 0 ? this.addLeftChild(var2, var4) : this;
            } else {
               this.left = var7.setCount(var1, var2, var3, var4, var5);
               if (var5[0] == var3) {
                  if (var4 == 0 && var5[0] != 0) {
                     --this.distinctElements;
                  } else if (var4 > 0 && var5[0] == 0) {
                     ++this.distinctElements;
                  }

                  this.totalCount += (long)(var4 - var5[0]);
               }

               return this.rebalance();
            }
         } else if (var6 > 0) {
            var7 = this.right;
            if (var7 == null) {
               var5[0] = 0;
               return var3 == 0 && var4 > 0 ? this.addRightChild(var2, var4) : this;
            } else {
               this.right = var7.setCount(var1, var2, var3, var4, var5);
               if (var5[0] == var3) {
                  if (var4 == 0 && var5[0] != 0) {
                     --this.distinctElements;
                  } else if (var4 > 0 && var5[0] == 0) {
                     ++this.distinctElements;
                  }

                  this.totalCount += (long)(var4 - var5[0]);
               }

               return this.rebalance();
            }
         } else {
            var5[0] = this.elemCount;
            if (var3 == this.elemCount) {
               if (var4 == 0) {
                  return this.deleteMe();
               }

               this.totalCount += (long)(var4 - this.elemCount);
               this.elemCount = var4;
            }

            return this;
         }
      }

      private TreeMultiset.AvlNode<E> deleteMe() {
         int var1 = this.elemCount;
         this.elemCount = 0;
         TreeMultiset.successor(this.pred, this.succ);
         if (this.left == null) {
            return this.right;
         } else if (this.right == null) {
            return this.left;
         } else {
            TreeMultiset.AvlNode var2;
            if (this.left.height >= this.right.height) {
               var2 = this.pred;
               var2.left = this.left.removeMax(var2);
               var2.right = this.right;
               var2.distinctElements = this.distinctElements - 1;
               var2.totalCount = this.totalCount - (long)var1;
               return var2.rebalance();
            } else {
               var2 = this.succ;
               var2.right = this.right.removeMin(var2);
               var2.left = this.left;
               var2.distinctElements = this.distinctElements - 1;
               var2.totalCount = this.totalCount - (long)var1;
               return var2.rebalance();
            }
         }
      }

      private TreeMultiset.AvlNode<E> removeMin(TreeMultiset.AvlNode<E> var1) {
         if (this.left == null) {
            return this.right;
         } else {
            this.left = this.left.removeMin(var1);
            --this.distinctElements;
            this.totalCount -= (long)var1.elemCount;
            return this.rebalance();
         }
      }

      private TreeMultiset.AvlNode<E> removeMax(TreeMultiset.AvlNode<E> var1) {
         if (this.right == null) {
            return this.left;
         } else {
            this.right = this.right.removeMax(var1);
            --this.distinctElements;
            this.totalCount -= (long)var1.elemCount;
            return this.rebalance();
         }
      }

      private void recomputeMultiset() {
         this.distinctElements = 1 + TreeMultiset.distinctElements(this.left) + TreeMultiset.distinctElements(this.right);
         this.totalCount = (long)this.elemCount + totalCount(this.left) + totalCount(this.right);
      }

      private void recomputeHeight() {
         this.height = 1 + Math.max(height(this.left), height(this.right));
      }

      private void recompute() {
         this.recomputeMultiset();
         this.recomputeHeight();
      }

      private TreeMultiset.AvlNode<E> rebalance() {
         switch(this.balanceFactor()) {
         case -2:
            if (this.right.balanceFactor() > 0) {
               this.right = this.right.rotateRight();
            }

            return this.rotateLeft();
         case 2:
            if (this.left.balanceFactor() < 0) {
               this.left = this.left.rotateLeft();
            }

            return this.rotateRight();
         default:
            this.recomputeHeight();
            return this;
         }
      }

      private int balanceFactor() {
         return height(this.left) - height(this.right);
      }

      private TreeMultiset.AvlNode<E> rotateLeft() {
         Preconditions.checkState(this.right != null);
         TreeMultiset.AvlNode var1 = this.right;
         this.right = var1.left;
         var1.left = this;
         var1.totalCount = this.totalCount;
         var1.distinctElements = this.distinctElements;
         this.recompute();
         var1.recomputeHeight();
         return var1;
      }

      private TreeMultiset.AvlNode<E> rotateRight() {
         Preconditions.checkState(this.left != null);
         TreeMultiset.AvlNode var1 = this.left;
         this.left = var1.right;
         var1.right = this;
         var1.totalCount = this.totalCount;
         var1.distinctElements = this.distinctElements;
         this.recompute();
         var1.recomputeHeight();
         return var1;
      }

      private static long totalCount(@Nullable TreeMultiset.AvlNode<?> var0) {
         return var0 == null ? 0L : var0.totalCount;
      }

      private static int height(@Nullable TreeMultiset.AvlNode<?> var0) {
         return var0 == null ? 0 : var0.height;
      }

      @Nullable
      private TreeMultiset.AvlNode<E> ceiling(Comparator<? super E> var1, E var2) {
         int var3 = var1.compare(var2, this.elem);
         if (var3 < 0) {
            return this.left == null ? this : (TreeMultiset.AvlNode)MoreObjects.firstNonNull(this.left.ceiling(var1, var2), this);
         } else if (var3 == 0) {
            return this;
         } else {
            return this.right == null ? null : this.right.ceiling(var1, var2);
         }
      }

      @Nullable
      private TreeMultiset.AvlNode<E> floor(Comparator<? super E> var1, E var2) {
         int var3 = var1.compare(var2, this.elem);
         if (var3 > 0) {
            return this.right == null ? this : (TreeMultiset.AvlNode)MoreObjects.firstNonNull(this.right.floor(var1, var2), this);
         } else if (var3 == 0) {
            return this;
         } else {
            return this.left == null ? null : this.left.floor(var1, var2);
         }
      }

      public E getElement() {
         return this.elem;
      }

      public int getCount() {
         return this.elemCount;
      }

      public String toString() {
         return Multisets.immutableEntry(this.getElement(), this.getCount()).toString();
      }
   }

   private static final class Reference<T> {
      @Nullable
      private T value;

      private Reference() {
         super();
      }

      @Nullable
      public T get() {
         return this.value;
      }

      public void checkAndSet(@Nullable T var1, T var2) {
         if (this.value != var1) {
            throw new ConcurrentModificationException();
         } else {
            this.value = var2;
         }
      }

      // $FF: synthetic method
      Reference(Object var1) {
         this();
      }
   }

   private static enum Aggregate {
      SIZE {
         int nodeAggregate(TreeMultiset.AvlNode<?> var1) {
            return var1.elemCount;
         }

         long treeAggregate(@Nullable TreeMultiset.AvlNode<?> var1) {
            return var1 == null ? 0L : var1.totalCount;
         }
      },
      DISTINCT {
         int nodeAggregate(TreeMultiset.AvlNode<?> var1) {
            return 1;
         }

         long treeAggregate(@Nullable TreeMultiset.AvlNode<?> var1) {
            return var1 == null ? 0L : (long)var1.distinctElements;
         }
      };

      private Aggregate() {
      }

      abstract int nodeAggregate(TreeMultiset.AvlNode<?> var1);

      abstract long treeAggregate(@Nullable TreeMultiset.AvlNode<?> var1);

      // $FF: synthetic method
      Aggregate(Object var3) {
         this();
      }
   }
}
