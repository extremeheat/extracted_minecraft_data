package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class LongRBTreeSet extends AbstractLongSortedSet implements Serializable, Cloneable, LongSortedSet {
   protected transient LongRBTreeSet.Entry tree;
   protected int count;
   protected transient LongRBTreeSet.Entry firstEntry;
   protected transient LongRBTreeSet.Entry lastEntry;
   protected Comparator<? super Long> storedComparator;
   protected transient LongComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353130L;
   private transient boolean[] dirPath;
   private transient LongRBTreeSet.Entry[] nodePath;

   public LongRBTreeSet() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = LongComparators.asLongComparator(this.storedComparator);
   }

   public LongRBTreeSet(Comparator<? super Long> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public LongRBTreeSet(Collection<? extends Long> var1) {
      this();
      this.addAll(var1);
   }

   public LongRBTreeSet(SortedSet<Long> var1) {
      this(var1.comparator());
      this.addAll(var1);
   }

   public LongRBTreeSet(LongCollection var1) {
      this();
      this.addAll(var1);
   }

   public LongRBTreeSet(LongSortedSet var1) {
      this((Comparator)var1.comparator());
      this.addAll(var1);
   }

   public LongRBTreeSet(LongIterator var1) {
      super();
      this.allocatePaths();

      while(var1.hasNext()) {
         this.add(var1.nextLong());
      }

   }

   public LongRBTreeSet(Iterator<?> var1) {
      this(LongIterators.asLongIterator(var1));
   }

   public LongRBTreeSet(long[] var1, int var2, int var3, Comparator<? super Long> var4) {
      this(var4);
      LongArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public LongRBTreeSet(long[] var1, int var2, int var3) {
      this(var1, var2, var3, (Comparator)null);
   }

   public LongRBTreeSet(long[] var1) {
      this();
      int var2 = var1.length;

      while(var2-- != 0) {
         this.add(var1[var2]);
      }

   }

   public LongRBTreeSet(long[] var1, Comparator<? super Long> var2) {
      this(var2);
      int var3 = var1.length;

      while(var3-- != 0) {
         this.add(var1[var3]);
      }

   }

   final int compare(long var1, long var3) {
      return this.actualComparator == null ? Long.compare(var1, var3) : this.actualComparator.compare(var1, var3);
   }

   private LongRBTreeSet.Entry findKey(long var1) {
      LongRBTreeSet.Entry var3;
      int var4;
      for(var3 = this.tree; var3 != null && (var4 = this.compare(var1, var3.key)) != 0; var3 = var4 < 0 ? var3.left() : var3.right()) {
      }

      return var3;
   }

   final LongRBTreeSet.Entry locateKey(long var1) {
      LongRBTreeSet.Entry var3 = this.tree;
      LongRBTreeSet.Entry var4 = this.tree;

      int var5;
      for(var5 = 0; var3 != null && (var5 = this.compare(var1, var3.key)) != 0; var3 = var5 < 0 ? var3.left() : var3.right()) {
         var4 = var3;
      }

      return var5 == 0 ? var3 : var4;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new LongRBTreeSet.Entry[64];
   }

   public boolean add(long var1) {
      int var3 = 0;
      if (this.tree == null) {
         ++this.count;
         this.tree = this.lastEntry = this.firstEntry = new LongRBTreeSet.Entry(var1);
      } else {
         LongRBTreeSet.Entry var4 = this.tree;
         int var7 = 0;

         label123:
         while(true) {
            int var6;
            if ((var6 = this.compare(var1, var4.key)) == 0) {
               while(var7-- != 0) {
                  this.nodePath[var7] = null;
               }

               return false;
            }

            this.nodePath[var7] = var4;
            LongRBTreeSet.Entry var5;
            if (this.dirPath[var7++] = var6 > 0) {
               if (!var4.succ()) {
                  var4 = var4.right;
                  continue;
               }

               ++this.count;
               var5 = new LongRBTreeSet.Entry(var1);
               if (var4.right == null) {
                  this.lastEntry = var5;
               }

               var5.left = var4;
               var5.right = var4.right;
               var4.right(var5);
            } else {
               if (!var4.pred()) {
                  var4 = var4.left;
                  continue;
               }

               ++this.count;
               var5 = new LongRBTreeSet.Entry(var1);
               if (var4.left == null) {
                  this.firstEntry = var5;
               }

               var5.right = var4;
               var5.left = var4.left;
               var4.left(var5);
            }

            var3 = var7--;

            while(true) {
               if (var7 <= 0 || this.nodePath[var7].black()) {
                  break label123;
               }

               LongRBTreeSet.Entry var8;
               LongRBTreeSet.Entry var9;
               if (!this.dirPath[var7 - 1]) {
                  var8 = this.nodePath[var7 - 1].right;
                  if (this.nodePath[var7 - 1].succ() || var8.black()) {
                     if (!this.dirPath[var7]) {
                        var8 = this.nodePath[var7];
                     } else {
                        var9 = this.nodePath[var7];
                        var8 = var9.right;
                        var9.right = var8.left;
                        var8.left = var9;
                        this.nodePath[var7 - 1].left = var8;
                        if (var8.pred()) {
                           var8.pred(false);
                           var9.succ(var8);
                        }
                     }

                     var9 = this.nodePath[var7 - 1];
                     var9.black(false);
                     var8.black(true);
                     var9.left = var8.right;
                     var8.right = var9;
                     if (var7 < 2) {
                        this.tree = var8;
                     } else if (this.dirPath[var7 - 2]) {
                        this.nodePath[var7 - 2].right = var8;
                     } else {
                        this.nodePath[var7 - 2].left = var8;
                     }

                     if (var8.succ()) {
                        var8.succ(false);
                        var9.pred(var8);
                     }
                     break label123;
                  }

                  this.nodePath[var7].black(true);
                  var8.black(true);
                  this.nodePath[var7 - 1].black(false);
                  var7 -= 2;
               } else {
                  var8 = this.nodePath[var7 - 1].left;
                  if (this.nodePath[var7 - 1].pred() || var8.black()) {
                     if (this.dirPath[var7]) {
                        var8 = this.nodePath[var7];
                     } else {
                        var9 = this.nodePath[var7];
                        var8 = var9.left;
                        var9.left = var8.right;
                        var8.right = var9;
                        this.nodePath[var7 - 1].right = var8;
                        if (var8.succ()) {
                           var8.succ(false);
                           var9.pred(var8);
                        }
                     }

                     var9 = this.nodePath[var7 - 1];
                     var9.black(false);
                     var8.black(true);
                     var9.right = var8.left;
                     var8.left = var9;
                     if (var7 < 2) {
                        this.tree = var8;
                     } else if (this.dirPath[var7 - 2]) {
                        this.nodePath[var7 - 2].right = var8;
                     } else {
                        this.nodePath[var7 - 2].left = var8;
                     }

                     if (var8.pred()) {
                        var8.pred(false);
                        var9.succ(var8);
                     }
                     break label123;
                  }

                  this.nodePath[var7].black(true);
                  var8.black(true);
                  this.nodePath[var7 - 1].black(false);
                  var7 -= 2;
               }
            }
         }
      }

      this.tree.black(true);

      while(var3-- != 0) {
         this.nodePath[var3] = null;
      }

      return true;
   }

   public boolean remove(long var1) {
      if (this.tree == null) {
         return false;
      } else {
         LongRBTreeSet.Entry var3 = this.tree;
         int var5 = 0;
         long var6 = var1;

         int var4;
         while((var4 = this.compare(var6, var3.key)) != 0) {
            this.dirPath[var5] = var4 > 0;
            this.nodePath[var5] = var3;
            if (this.dirPath[var5++]) {
               if ((var3 = var3.right()) == null) {
                  while(var5-- != 0) {
                     this.nodePath[var5] = null;
                  }

                  return false;
               }
            } else if ((var3 = var3.left()) == null) {
               while(var5-- != 0) {
                  this.nodePath[var5] = null;
               }

               return false;
            }
         }

         if (var3.left == null) {
            this.firstEntry = var3.next();
         }

         if (var3.right == null) {
            this.lastEntry = var3.prev();
         }

         LongRBTreeSet.Entry var9;
         LongRBTreeSet.Entry var10;
         if (var3.succ()) {
            if (var3.pred()) {
               if (var5 == 0) {
                  this.tree = var3.left;
               } else if (this.dirPath[var5 - 1]) {
                  this.nodePath[var5 - 1].succ(var3.right);
               } else {
                  this.nodePath[var5 - 1].pred(var3.left);
               }
            } else {
               var3.prev().right = var3.right;
               if (var5 == 0) {
                  this.tree = var3.left;
               } else if (this.dirPath[var5 - 1]) {
                  this.nodePath[var5 - 1].right = var3.left;
               } else {
                  this.nodePath[var5 - 1].left = var3.left;
               }
            }
         } else {
            var9 = var3.right;
            boolean var8;
            if (var9.pred()) {
               var9.left = var3.left;
               var9.pred(var3.pred());
               if (!var9.pred()) {
                  var9.prev().right = var9;
               }

               if (var5 == 0) {
                  this.tree = var9;
               } else if (this.dirPath[var5 - 1]) {
                  this.nodePath[var5 - 1].right = var9;
               } else {
                  this.nodePath[var5 - 1].left = var9;
               }

               var8 = var9.black();
               var9.black(var3.black());
               var3.black(var8);
               this.dirPath[var5] = true;
               this.nodePath[var5++] = var9;
            } else {
               int var11 = var5++;

               while(true) {
                  this.dirPath[var5] = false;
                  this.nodePath[var5++] = var9;
                  var10 = var9.left;
                  if (var10.pred()) {
                     this.dirPath[var11] = true;
                     this.nodePath[var11] = var10;
                     if (var10.succ()) {
                        var9.pred(var10);
                     } else {
                        var9.left = var10.right;
                     }

                     var10.left = var3.left;
                     if (!var3.pred()) {
                        var3.prev().right = var10;
                        var10.pred(false);
                     }

                     var10.right(var3.right);
                     var8 = var10.black();
                     var10.black(var3.black());
                     var3.black(var8);
                     if (var11 == 0) {
                        this.tree = var10;
                     } else if (this.dirPath[var11 - 1]) {
                        this.nodePath[var11 - 1].right = var10;
                     } else {
                        this.nodePath[var11 - 1].left = var10;
                     }
                     break;
                  }

                  var9 = var10;
               }
            }
         }

         int var12 = var5;
         if (var3.black()) {
            for(; var5 > 0; --var5) {
               if (this.dirPath[var5 - 1] && !this.nodePath[var5 - 1].succ() || !this.dirPath[var5 - 1] && !this.nodePath[var5 - 1].pred()) {
                  var9 = this.dirPath[var5 - 1] ? this.nodePath[var5 - 1].right : this.nodePath[var5 - 1].left;
                  if (!var9.black()) {
                     var9.black(true);
                     break;
                  }
               }

               if (!this.dirPath[var5 - 1]) {
                  var9 = this.nodePath[var5 - 1].right;
                  if (!var9.black()) {
                     var9.black(true);
                     this.nodePath[var5 - 1].black(false);
                     this.nodePath[var5 - 1].right = var9.left;
                     var9.left = this.nodePath[var5 - 1];
                     if (var5 < 2) {
                        this.tree = var9;
                     } else if (this.dirPath[var5 - 2]) {
                        this.nodePath[var5 - 2].right = var9;
                     } else {
                        this.nodePath[var5 - 2].left = var9;
                     }

                     this.nodePath[var5] = this.nodePath[var5 - 1];
                     this.dirPath[var5] = false;
                     this.nodePath[var5 - 1] = var9;
                     if (var12 == var5++) {
                        ++var12;
                     }

                     var9 = this.nodePath[var5 - 1].right;
                  }

                  if (!var9.pred() && !var9.left.black() || !var9.succ() && !var9.right.black()) {
                     if (var9.succ() || var9.right.black()) {
                        var10 = var9.left;
                        var10.black(true);
                        var9.black(false);
                        var9.left = var10.right;
                        var10.right = var9;
                        var9 = this.nodePath[var5 - 1].right = var10;
                        if (var9.succ()) {
                           var9.succ(false);
                           var9.right.pred(var9);
                        }
                     }

                     var9.black(this.nodePath[var5 - 1].black());
                     this.nodePath[var5 - 1].black(true);
                     var9.right.black(true);
                     this.nodePath[var5 - 1].right = var9.left;
                     var9.left = this.nodePath[var5 - 1];
                     if (var5 < 2) {
                        this.tree = var9;
                     } else if (this.dirPath[var5 - 2]) {
                        this.nodePath[var5 - 2].right = var9;
                     } else {
                        this.nodePath[var5 - 2].left = var9;
                     }

                     if (var9.pred()) {
                        var9.pred(false);
                        this.nodePath[var5 - 1].succ(var9);
                     }
                     break;
                  }

                  var9.black(false);
               } else {
                  var9 = this.nodePath[var5 - 1].left;
                  if (!var9.black()) {
                     var9.black(true);
                     this.nodePath[var5 - 1].black(false);
                     this.nodePath[var5 - 1].left = var9.right;
                     var9.right = this.nodePath[var5 - 1];
                     if (var5 < 2) {
                        this.tree = var9;
                     } else if (this.dirPath[var5 - 2]) {
                        this.nodePath[var5 - 2].right = var9;
                     } else {
                        this.nodePath[var5 - 2].left = var9;
                     }

                     this.nodePath[var5] = this.nodePath[var5 - 1];
                     this.dirPath[var5] = true;
                     this.nodePath[var5 - 1] = var9;
                     if (var12 == var5++) {
                        ++var12;
                     }

                     var9 = this.nodePath[var5 - 1].left;
                  }

                  if (!var9.pred() && !var9.left.black() || !var9.succ() && !var9.right.black()) {
                     if (var9.pred() || var9.left.black()) {
                        var10 = var9.right;
                        var10.black(true);
                        var9.black(false);
                        var9.right = var10.left;
                        var10.left = var9;
                        var9 = this.nodePath[var5 - 1].left = var10;
                        if (var9.pred()) {
                           var9.pred(false);
                           var9.left.succ(var9);
                        }
                     }

                     var9.black(this.nodePath[var5 - 1].black());
                     this.nodePath[var5 - 1].black(true);
                     var9.left.black(true);
                     this.nodePath[var5 - 1].left = var9.right;
                     var9.right = this.nodePath[var5 - 1];
                     if (var5 < 2) {
                        this.tree = var9;
                     } else if (this.dirPath[var5 - 2]) {
                        this.nodePath[var5 - 2].right = var9;
                     } else {
                        this.nodePath[var5 - 2].left = var9;
                     }

                     if (var9.succ()) {
                        var9.succ(false);
                        this.nodePath[var5 - 1].pred(var9);
                     }
                     break;
                  }

                  var9.black(false);
               }
            }

            if (this.tree != null) {
               this.tree.black(true);
            }
         }

         --this.count;

         while(var12-- != 0) {
            this.nodePath[var12] = null;
         }

         return true;
      }
   }

   public boolean contains(long var1) {
      return this.findKey(var1) != null;
   }

   public void clear() {
      this.count = 0;
      this.tree = null;
      this.firstEntry = this.lastEntry = null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public long firstLong() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public long lastLong() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public LongBidirectionalIterator iterator() {
      return new LongRBTreeSet.SetIterator();
   }

   public LongBidirectionalIterator iterator(long var1) {
      return new LongRBTreeSet.SetIterator(var1);
   }

   public LongComparator comparator() {
      return this.actualComparator;
   }

   public LongSortedSet headSet(long var1) {
      return new LongRBTreeSet.Subset(0L, true, var1, false);
   }

   public LongSortedSet tailSet(long var1) {
      return new LongRBTreeSet.Subset(var1, false, 0L, true);
   }

   public LongSortedSet subSet(long var1, long var3) {
      return new LongRBTreeSet.Subset(var1, false, var3, false);
   }

   public Object clone() {
      LongRBTreeSet var1;
      try {
         var1 = (LongRBTreeSet)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      var1.allocatePaths();
      if (this.count == 0) {
         return var1;
      } else {
         LongRBTreeSet.Entry var5 = new LongRBTreeSet.Entry();
         LongRBTreeSet.Entry var6 = new LongRBTreeSet.Entry();
         LongRBTreeSet.Entry var3 = var5;
         var5.left(this.tree);
         LongRBTreeSet.Entry var4 = var6;
         var6.pred((LongRBTreeSet.Entry)null);

         while(true) {
            LongRBTreeSet.Entry var2;
            if (!var3.pred()) {
               var2 = var3.left.clone();
               var2.pred(var4.left);
               var2.succ(var4);
               var4.left(var2);
               var3 = var3.left;
               var4 = var4.left;
            } else {
               while(var3.succ()) {
                  var3 = var3.right;
                  if (var3 == null) {
                     var4.right = null;
                     var1.tree = var6.left;

                     for(var1.firstEntry = var1.tree; var1.firstEntry.left != null; var1.firstEntry = var1.firstEntry.left) {
                     }

                     for(var1.lastEntry = var1.tree; var1.lastEntry.right != null; var1.lastEntry = var1.lastEntry.right) {
                     }

                     return var1;
                  }

                  var4 = var4.right;
               }

               var3 = var3.right;
               var4 = var4.right;
            }

            if (!var3.succ()) {
               var2 = var3.right.clone();
               var2.succ(var4.right);
               var2.pred(var4);
               var4.right(var2);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      int var2 = this.count;
      LongRBTreeSet.SetIterator var3 = new LongRBTreeSet.SetIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         var1.writeLong(var3.nextLong());
      }

   }

   private LongRBTreeSet.Entry readTree(ObjectInputStream var1, int var2, LongRBTreeSet.Entry var3, LongRBTreeSet.Entry var4) throws IOException, ClassNotFoundException {
      LongRBTreeSet.Entry var8;
      if (var2 == 1) {
         var8 = new LongRBTreeSet.Entry(var1.readLong());
         var8.pred(var3);
         var8.succ(var4);
         var8.black(true);
         return var8;
      } else if (var2 == 2) {
         var8 = new LongRBTreeSet.Entry(var1.readLong());
         var8.black(true);
         var8.right(new LongRBTreeSet.Entry(var1.readLong()));
         var8.right.pred(var8);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         LongRBTreeSet.Entry var7 = new LongRBTreeSet.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readLong();
         var7.black(true);
         var7.right(this.readTree(var1, var5, var7, var4));
         if (var2 + 2 == (var2 + 2 & -(var2 + 2))) {
            var7.right.black(false);
         }

         return var7;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.setActualComparator();
      this.allocatePaths();
      if (this.count != 0) {
         this.tree = this.readTree(var1, this.count, (LongRBTreeSet.Entry)null, (LongRBTreeSet.Entry)null);

         LongRBTreeSet.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Subset extends AbstractLongSortedSet implements Serializable, LongSortedSet {
      private static final long serialVersionUID = -7046029254386353129L;
      long from;
      long to;
      boolean bottom;
      boolean top;

      public Subset(long var2, boolean var4, long var5, boolean var7) {
         super();
         if (!var4 && !var7 && LongRBTreeSet.this.compare(var2, var5) > 0) {
            throw new IllegalArgumentException("Start element (" + var2 + ") is larger than end element (" + var5 + ")");
         } else {
            this.from = var2;
            this.bottom = var4;
            this.to = var5;
            this.top = var7;
         }
      }

      public void clear() {
         LongRBTreeSet.Subset.SubsetIterator var1 = new LongRBTreeSet.Subset.SubsetIterator();

         while(var1.hasNext()) {
            var1.nextLong();
            var1.remove();
         }

      }

      final boolean in(long var1) {
         return (this.bottom || LongRBTreeSet.this.compare(var1, this.from) >= 0) && (this.top || LongRBTreeSet.this.compare(var1, this.to) < 0);
      }

      public boolean contains(long var1) {
         return this.in(var1) && LongRBTreeSet.this.contains(var1);
      }

      public boolean add(long var1) {
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Element (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            return LongRBTreeSet.this.add(var1);
         }
      }

      public boolean remove(long var1) {
         return !this.in(var1) ? false : LongRBTreeSet.this.remove(var1);
      }

      public int size() {
         LongRBTreeSet.Subset.SubsetIterator var1 = new LongRBTreeSet.Subset.SubsetIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextLong();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new LongRBTreeSet.Subset.SubsetIterator()).hasNext();
      }

      public LongComparator comparator() {
         return LongRBTreeSet.this.actualComparator;
      }

      public LongBidirectionalIterator iterator() {
         return new LongRBTreeSet.Subset.SubsetIterator();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new LongRBTreeSet.Subset.SubsetIterator(var1);
      }

      public LongSortedSet headSet(long var1) {
         if (this.top) {
            return LongRBTreeSet.this.new Subset(this.from, this.bottom, var1, false);
         } else {
            return LongRBTreeSet.this.compare(var1, this.to) < 0 ? LongRBTreeSet.this.new Subset(this.from, this.bottom, var1, false) : this;
         }
      }

      public LongSortedSet tailSet(long var1) {
         if (this.bottom) {
            return LongRBTreeSet.this.new Subset(var1, false, this.to, this.top);
         } else {
            return LongRBTreeSet.this.compare(var1, this.from) > 0 ? LongRBTreeSet.this.new Subset(var1, false, this.to, this.top) : this;
         }
      }

      public LongSortedSet subSet(long var1, long var3) {
         if (this.top && this.bottom) {
            return LongRBTreeSet.this.new Subset(var1, false, var3, false);
         } else {
            if (!this.top) {
               var3 = LongRBTreeSet.this.compare(var3, this.to) < 0 ? var3 : this.to;
            }

            if (!this.bottom) {
               var1 = LongRBTreeSet.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var3 == this.to ? this : LongRBTreeSet.this.new Subset(var1, false, var3, false);
         }
      }

      public LongRBTreeSet.Entry firstEntry() {
         if (LongRBTreeSet.this.tree == null) {
            return null;
         } else {
            LongRBTreeSet.Entry var1;
            if (this.bottom) {
               var1 = LongRBTreeSet.this.firstEntry;
            } else {
               var1 = LongRBTreeSet.this.locateKey(this.from);
               if (LongRBTreeSet.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || LongRBTreeSet.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public LongRBTreeSet.Entry lastEntry() {
         if (LongRBTreeSet.this.tree == null) {
            return null;
         } else {
            LongRBTreeSet.Entry var1;
            if (this.top) {
               var1 = LongRBTreeSet.this.lastEntry;
            } else {
               var1 = LongRBTreeSet.this.locateKey(this.to);
               if (LongRBTreeSet.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || LongRBTreeSet.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public long firstLong() {
         LongRBTreeSet.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public long lastLong() {
         LongRBTreeSet.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubsetIterator extends LongRBTreeSet.SetIterator {
         SubsetIterator() {
            super();
            this.next = Subset.this.firstEntry();
         }

         SubsetIterator(long var2) {
            this();
            if (this.next != null) {
               if (!Subset.this.bottom && LongRBTreeSet.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Subset.this.top && LongRBTreeSet.this.compare(var2, (this.prev = Subset.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = LongRBTreeSet.this.locateKey(var2);
                  if (LongRBTreeSet.this.compare(this.next.key, var2) <= 0) {
                     this.prev = this.next;
                     this.next = this.next.next();
                  } else {
                     this.prev = this.next.prev();
                  }
               }
            }

         }

         void updatePrevious() {
            this.prev = this.prev.prev();
            if (!Subset.this.bottom && this.prev != null && LongRBTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Subset.this.top && this.next != null && LongRBTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
               this.next = null;
            }

         }
      }
   }

   private class SetIterator implements LongListIterator {
      LongRBTreeSet.Entry prev;
      LongRBTreeSet.Entry next;
      LongRBTreeSet.Entry curr;
      int index = 0;

      SetIterator() {
         super();
         this.next = LongRBTreeSet.this.firstEntry;
      }

      SetIterator(long var2) {
         super();
         if ((this.next = LongRBTreeSet.this.locateKey(var2)) != null) {
            if (LongRBTreeSet.this.compare(this.next.key, var2) <= 0) {
               this.prev = this.next;
               this.next = this.next.next();
            } else {
               this.prev = this.next.prev();
            }
         }

      }

      public boolean hasNext() {
         return this.next != null;
      }

      public boolean hasPrevious() {
         return this.prev != null;
      }

      void updateNext() {
         this.next = this.next.next();
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      public long nextLong() {
         return this.nextEntry().key;
      }

      public long previousLong() {
         return this.previousEntry().key;
      }

      LongRBTreeSet.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
         }
      }

      LongRBTreeSet.Entry previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next = this.prev;
            --this.index;
            this.updatePrevious();
            return this.curr;
         }
      }

      public int nextIndex() {
         return this.index;
      }

      public int previousIndex() {
         return this.index - 1;
      }

      public void remove() {
         if (this.curr == null) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               --this.index;
            }

            this.next = this.prev = this.curr;
            this.updatePrevious();
            this.updateNext();
            LongRBTreeSet.this.remove(this.curr.key);
            this.curr = null;
         }
      }
   }

   private static final class Entry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      long key;
      LongRBTreeSet.Entry left;
      LongRBTreeSet.Entry right;
      int info;

      Entry() {
         super();
      }

      Entry(long var1) {
         super();
         this.key = var1;
         this.info = -1073741824;
      }

      LongRBTreeSet.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      LongRBTreeSet.Entry right() {
         return (this.info & -2147483648) != 0 ? null : this.right;
      }

      boolean pred() {
         return (this.info & 1073741824) != 0;
      }

      boolean succ() {
         return (this.info & -2147483648) != 0;
      }

      void pred(boolean var1) {
         if (var1) {
            this.info |= 1073741824;
         } else {
            this.info &= -1073741825;
         }

      }

      void succ(boolean var1) {
         if (var1) {
            this.info |= -2147483648;
         } else {
            this.info &= 2147483647;
         }

      }

      void pred(LongRBTreeSet.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(LongRBTreeSet.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(LongRBTreeSet.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(LongRBTreeSet.Entry var1) {
         this.info &= 2147483647;
         this.right = var1;
      }

      boolean black() {
         return (this.info & 1) != 0;
      }

      void black(boolean var1) {
         if (var1) {
            this.info |= 1;
         } else {
            this.info &= -2;
         }

      }

      LongRBTreeSet.Entry next() {
         LongRBTreeSet.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      LongRBTreeSet.Entry prev() {
         LongRBTreeSet.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public LongRBTreeSet.Entry clone() {
         LongRBTreeSet.Entry var1;
         try {
            var1 = (LongRBTreeSet.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         var1.key = this.key;
         var1.info = this.info;
         return var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof LongRBTreeSet.Entry)) {
            return false;
         } else {
            LongRBTreeSet.Entry var2 = (LongRBTreeSet.Entry)var1;
            return this.key == var2.key;
         }
      }

      public int hashCode() {
         return HashCommon.long2int(this.key);
      }

      public String toString() {
         return String.valueOf(this.key);
      }
   }
}
