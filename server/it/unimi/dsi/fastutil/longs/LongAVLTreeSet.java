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

public class LongAVLTreeSet extends AbstractLongSortedSet implements Serializable, Cloneable, LongSortedSet {
   protected transient LongAVLTreeSet.Entry tree;
   protected int count;
   protected transient LongAVLTreeSet.Entry firstEntry;
   protected transient LongAVLTreeSet.Entry lastEntry;
   protected Comparator<? super Long> storedComparator;
   protected transient LongComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353130L;
   private transient boolean[] dirPath;

   public LongAVLTreeSet() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = LongComparators.asLongComparator(this.storedComparator);
   }

   public LongAVLTreeSet(Comparator<? super Long> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public LongAVLTreeSet(Collection<? extends Long> var1) {
      this();
      this.addAll(var1);
   }

   public LongAVLTreeSet(SortedSet<Long> var1) {
      this(var1.comparator());
      this.addAll(var1);
   }

   public LongAVLTreeSet(LongCollection var1) {
      this();
      this.addAll(var1);
   }

   public LongAVLTreeSet(LongSortedSet var1) {
      this((Comparator)var1.comparator());
      this.addAll(var1);
   }

   public LongAVLTreeSet(LongIterator var1) {
      super();
      this.allocatePaths();

      while(var1.hasNext()) {
         this.add(var1.nextLong());
      }

   }

   public LongAVLTreeSet(Iterator<?> var1) {
      this(LongIterators.asLongIterator(var1));
   }

   public LongAVLTreeSet(long[] var1, int var2, int var3, Comparator<? super Long> var4) {
      this(var4);
      LongArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public LongAVLTreeSet(long[] var1, int var2, int var3) {
      this(var1, var2, var3, (Comparator)null);
   }

   public LongAVLTreeSet(long[] var1) {
      this();
      int var2 = var1.length;

      while(var2-- != 0) {
         this.add(var1[var2]);
      }

   }

   public LongAVLTreeSet(long[] var1, Comparator<? super Long> var2) {
      this(var2);
      int var3 = var1.length;

      while(var3-- != 0) {
         this.add(var1[var3]);
      }

   }

   final int compare(long var1, long var3) {
      return this.actualComparator == null ? Long.compare(var1, var3) : this.actualComparator.compare(var1, var3);
   }

   private LongAVLTreeSet.Entry findKey(long var1) {
      LongAVLTreeSet.Entry var3;
      int var4;
      for(var3 = this.tree; var3 != null && (var4 = this.compare(var1, var3.key)) != 0; var3 = var4 < 0 ? var3.left() : var3.right()) {
      }

      return var3;
   }

   final LongAVLTreeSet.Entry locateKey(long var1) {
      LongAVLTreeSet.Entry var3 = this.tree;
      LongAVLTreeSet.Entry var4 = this.tree;

      int var5;
      for(var5 = 0; var3 != null && (var5 = this.compare(var1, var3.key)) != 0; var3 = var5 < 0 ? var3.left() : var3.right()) {
         var4 = var3;
      }

      return var5 == 0 ? var3 : var4;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public boolean add(long var1) {
      if (this.tree == null) {
         ++this.count;
         this.tree = this.lastEntry = this.firstEntry = new LongAVLTreeSet.Entry(var1);
      } else {
         LongAVLTreeSet.Entry var3 = this.tree;
         LongAVLTreeSet.Entry var4 = null;
         LongAVLTreeSet.Entry var5 = this.tree;
         LongAVLTreeSet.Entry var6 = null;
         LongAVLTreeSet.Entry var7 = null;
         LongAVLTreeSet.Entry var8 = null;
         int var10 = 0;

         while(true) {
            int var9;
            if ((var9 = this.compare(var1, var3.key)) == 0) {
               return false;
            }

            if (var3.balance() != 0) {
               var10 = 0;
               var6 = var4;
               var5 = var3;
            }

            if (this.dirPath[var10++] = var9 > 0) {
               if (var3.succ()) {
                  ++this.count;
                  var7 = new LongAVLTreeSet.Entry(var1);
                  if (var3.right == null) {
                     this.lastEntry = var7;
                  }

                  var7.left = var3;
                  var7.right = var3.right;
                  var3.right(var7);
                  break;
               }

               var4 = var3;
               var3 = var3.right;
            } else {
               if (var3.pred()) {
                  ++this.count;
                  var7 = new LongAVLTreeSet.Entry(var1);
                  if (var3.left == null) {
                     this.firstEntry = var7;
                  }

                  var7.right = var3;
                  var7.left = var3.left;
                  var3.left(var7);
                  break;
               }

               var4 = var3;
               var3 = var3.left;
            }
         }

         var3 = var5;

         for(var10 = 0; var3 != var7; var3 = this.dirPath[var10++] ? var3.right : var3.left) {
            if (this.dirPath[var10]) {
               var3.incBalance();
            } else {
               var3.decBalance();
            }
         }

         LongAVLTreeSet.Entry var11;
         if (var5.balance() == -2) {
            var11 = var5.left;
            if (var11.balance() == -1) {
               var8 = var11;
               if (var11.succ()) {
                  var11.succ(false);
                  var5.pred(var11);
               } else {
                  var5.left = var11.right;
               }

               var11.right = var5;
               var11.balance(0);
               var5.balance(0);
            } else {
               assert var11.balance() == 1;

               var8 = var11.right;
               var11.right = var8.left;
               var8.left = var11;
               var5.left = var8.right;
               var8.right = var5;
               if (var8.balance() == -1) {
                  var11.balance(0);
                  var5.balance(1);
               } else if (var8.balance() == 0) {
                  var11.balance(0);
                  var5.balance(0);
               } else {
                  var11.balance(-1);
                  var5.balance(0);
               }

               var8.balance(0);
               if (var8.pred()) {
                  var11.succ(var8);
                  var8.pred(false);
               }

               if (var8.succ()) {
                  var5.pred(var8);
                  var8.succ(false);
               }
            }
         } else {
            if (var5.balance() != 2) {
               return true;
            }

            var11 = var5.right;
            if (var11.balance() == 1) {
               var8 = var11;
               if (var11.pred()) {
                  var11.pred(false);
                  var5.succ(var11);
               } else {
                  var5.right = var11.left;
               }

               var11.left = var5;
               var11.balance(0);
               var5.balance(0);
            } else {
               assert var11.balance() == -1;

               var8 = var11.left;
               var11.left = var8.right;
               var8.right = var11;
               var5.right = var8.left;
               var8.left = var5;
               if (var8.balance() == 1) {
                  var11.balance(0);
                  var5.balance(-1);
               } else if (var8.balance() == 0) {
                  var11.balance(0);
                  var5.balance(0);
               } else {
                  var11.balance(1);
                  var5.balance(0);
               }

               var8.balance(0);
               if (var8.pred()) {
                  var5.succ(var8);
                  var8.pred(false);
               }

               if (var8.succ()) {
                  var11.pred(var8);
                  var8.succ(false);
               }
            }
         }

         if (var6 == null) {
            this.tree = var8;
         } else if (var6.left == var5) {
            var6.left = var8;
         } else {
            var6.right = var8;
         }
      }

      return true;
   }

   private LongAVLTreeSet.Entry parent(LongAVLTreeSet.Entry var1) {
      if (var1 == this.tree) {
         return null;
      } else {
         LongAVLTreeSet.Entry var3 = var1;

         LongAVLTreeSet.Entry var2;
         LongAVLTreeSet.Entry var4;
         for(var2 = var1; !var3.succ(); var3 = var3.right) {
            if (var2.pred()) {
               var4 = var2.left;
               if (var4 == null || var4.right != var1) {
                  while(!var3.succ()) {
                     var3 = var3.right;
                  }

                  var4 = var3.right;
               }

               return var4;
            }

            var2 = var2.left;
         }

         var4 = var3.right;
         if (var4 == null || var4.left != var1) {
            while(!var2.pred()) {
               var2 = var2.left;
            }

            var4 = var2.left;
         }

         return var4;
      }
   }

   public boolean remove(long var1) {
      if (this.tree == null) {
         return false;
      } else {
         LongAVLTreeSet.Entry var4 = this.tree;
         LongAVLTreeSet.Entry var5 = null;
         boolean var6 = false;
         long var7 = var1;

         int var3;
         while((var3 = this.compare(var7, var4.key)) != 0) {
            if (var6 = var3 > 0) {
               var5 = var4;
               if ((var4 = var4.right()) == null) {
                  return false;
               }
            } else {
               var5 = var4;
               if ((var4 = var4.left()) == null) {
                  return false;
               }
            }
         }

         if (var4.left == null) {
            this.firstEntry = var4.next();
         }

         if (var4.right == null) {
            this.lastEntry = var4.prev();
         }

         LongAVLTreeSet.Entry var9;
         LongAVLTreeSet.Entry var10;
         if (var4.succ()) {
            if (var4.pred()) {
               if (var5 != null) {
                  if (var6) {
                     var5.succ(var4.right);
                  } else {
                     var5.pred(var4.left);
                  }
               } else {
                  this.tree = var6 ? var4.right : var4.left;
               }
            } else {
               var4.prev().right = var4.right;
               if (var5 != null) {
                  if (var6) {
                     var5.right = var4.left;
                  } else {
                     var5.left = var4.left;
                  }
               } else {
                  this.tree = var4.left;
               }
            }
         } else {
            var9 = var4.right;
            if (var9.pred()) {
               var9.left = var4.left;
               var9.pred(var4.pred());
               if (!var9.pred()) {
                  var9.prev().right = var9;
               }

               if (var5 != null) {
                  if (var6) {
                     var5.right = var9;
                  } else {
                     var5.left = var9;
                  }
               } else {
                  this.tree = var9;
               }

               var9.balance(var4.balance());
               var5 = var9;
               var6 = true;
            } else {
               while(true) {
                  var10 = var9.left;
                  if (var10.pred()) {
                     if (var10.succ()) {
                        var9.pred(var10);
                     } else {
                        var9.left = var10.right;
                     }

                     var10.left = var4.left;
                     if (!var4.pred()) {
                        var4.prev().right = var10;
                        var10.pred(false);
                     }

                     var10.right = var4.right;
                     var10.succ(false);
                     if (var5 != null) {
                        if (var6) {
                           var5.right = var10;
                        } else {
                           var5.left = var10;
                        }
                     } else {
                        this.tree = var10;
                     }

                     var10.balance(var4.balance());
                     var5 = var9;
                     var6 = false;
                     break;
                  }

                  var9 = var10;
               }
            }
         }

         while(var5 != null) {
            var9 = var5;
            var5 = this.parent(var5);
            LongAVLTreeSet.Entry var11;
            if (!var6) {
               var6 = var5 != null && var5.left != var9;
               var9.incBalance();
               if (var9.balance() == 1) {
                  break;
               }

               if (var9.balance() == 2) {
                  var10 = var9.right;

                  assert var10 != null;

                  if (var10.balance() == -1) {
                     assert var10.balance() == -1;

                     var11 = var10.left;
                     var10.left = var11.right;
                     var11.right = var10;
                     var9.right = var11.left;
                     var11.left = var9;
                     if (var11.balance() == 1) {
                        var10.balance(0);
                        var9.balance(-1);
                     } else if (var11.balance() == 0) {
                        var10.balance(0);
                        var9.balance(0);
                     } else {
                        assert var11.balance() == -1;

                        var10.balance(1);
                        var9.balance(0);
                     }

                     var11.balance(0);
                     if (var11.pred()) {
                        var9.succ(var11);
                        var11.pred(false);
                     }

                     if (var11.succ()) {
                        var10.pred(var11);
                        var11.succ(false);
                     }

                     if (var5 != null) {
                        if (var6) {
                           var5.right = var11;
                        } else {
                           var5.left = var11;
                        }
                     } else {
                        this.tree = var11;
                     }
                  } else {
                     if (var5 != null) {
                        if (var6) {
                           var5.right = var10;
                        } else {
                           var5.left = var10;
                        }
                     } else {
                        this.tree = var10;
                     }

                     if (var10.balance() == 0) {
                        var9.right = var10.left;
                        var10.left = var9;
                        var10.balance(-1);
                        var9.balance(1);
                        break;
                     }

                     assert var10.balance() == 1;

                     if (var10.pred()) {
                        var9.succ(true);
                        var10.pred(false);
                     } else {
                        var9.right = var10.left;
                     }

                     var10.left = var9;
                     var9.balance(0);
                     var10.balance(0);
                  }
               }
            } else {
               var6 = var5 != null && var5.left != var9;
               var9.decBalance();
               if (var9.balance() == -1) {
                  break;
               }

               if (var9.balance() == -2) {
                  var10 = var9.left;

                  assert var10 != null;

                  if (var10.balance() == 1) {
                     assert var10.balance() == 1;

                     var11 = var10.right;
                     var10.right = var11.left;
                     var11.left = var10;
                     var9.left = var11.right;
                     var11.right = var9;
                     if (var11.balance() == -1) {
                        var10.balance(0);
                        var9.balance(1);
                     } else if (var11.balance() == 0) {
                        var10.balance(0);
                        var9.balance(0);
                     } else {
                        assert var11.balance() == 1;

                        var10.balance(-1);
                        var9.balance(0);
                     }

                     var11.balance(0);
                     if (var11.pred()) {
                        var10.succ(var11);
                        var11.pred(false);
                     }

                     if (var11.succ()) {
                        var9.pred(var11);
                        var11.succ(false);
                     }

                     if (var5 != null) {
                        if (var6) {
                           var5.right = var11;
                        } else {
                           var5.left = var11;
                        }
                     } else {
                        this.tree = var11;
                     }
                  } else {
                     if (var5 != null) {
                        if (var6) {
                           var5.right = var10;
                        } else {
                           var5.left = var10;
                        }
                     } else {
                        this.tree = var10;
                     }

                     if (var10.balance() == 0) {
                        var9.left = var10.right;
                        var10.right = var9;
                        var10.balance(1);
                        var9.balance(-1);
                        break;
                     }

                     assert var10.balance() == -1;

                     if (var10.succ()) {
                        var9.pred(true);
                        var10.succ(false);
                     } else {
                        var9.left = var10.right;
                     }

                     var10.right = var9;
                     var9.balance(0);
                     var10.balance(0);
                  }
               }
            }
         }

         --this.count;
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
      return new LongAVLTreeSet.SetIterator();
   }

   public LongBidirectionalIterator iterator(long var1) {
      return new LongAVLTreeSet.SetIterator(var1);
   }

   public LongComparator comparator() {
      return this.actualComparator;
   }

   public LongSortedSet headSet(long var1) {
      return new LongAVLTreeSet.Subset(0L, true, var1, false);
   }

   public LongSortedSet tailSet(long var1) {
      return new LongAVLTreeSet.Subset(var1, false, 0L, true);
   }

   public LongSortedSet subSet(long var1, long var3) {
      return new LongAVLTreeSet.Subset(var1, false, var3, false);
   }

   public Object clone() {
      LongAVLTreeSet var1;
      try {
         var1 = (LongAVLTreeSet)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      var1.allocatePaths();
      if (this.count == 0) {
         return var1;
      } else {
         LongAVLTreeSet.Entry var5 = new LongAVLTreeSet.Entry();
         LongAVLTreeSet.Entry var6 = new LongAVLTreeSet.Entry();
         LongAVLTreeSet.Entry var3 = var5;
         var5.left(this.tree);
         LongAVLTreeSet.Entry var4 = var6;
         var6.pred((LongAVLTreeSet.Entry)null);

         while(true) {
            LongAVLTreeSet.Entry var2;
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
      LongAVLTreeSet.SetIterator var3 = new LongAVLTreeSet.SetIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         var1.writeLong(var3.nextLong());
      }

   }

   private LongAVLTreeSet.Entry readTree(ObjectInputStream var1, int var2, LongAVLTreeSet.Entry var3, LongAVLTreeSet.Entry var4) throws IOException, ClassNotFoundException {
      LongAVLTreeSet.Entry var8;
      if (var2 == 1) {
         var8 = new LongAVLTreeSet.Entry(var1.readLong());
         var8.pred(var3);
         var8.succ(var4);
         return var8;
      } else if (var2 == 2) {
         var8 = new LongAVLTreeSet.Entry(var1.readLong());
         var8.right(new LongAVLTreeSet.Entry(var1.readLong()));
         var8.right.pred(var8);
         var8.balance(1);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         LongAVLTreeSet.Entry var7 = new LongAVLTreeSet.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readLong();
         var7.right(this.readTree(var1, var5, var7, var4));
         if (var2 == (var2 & -var2)) {
            var7.balance(1);
         }

         return var7;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.setActualComparator();
      this.allocatePaths();
      if (this.count != 0) {
         this.tree = this.readTree(var1, this.count, (LongAVLTreeSet.Entry)null, (LongAVLTreeSet.Entry)null);

         LongAVLTreeSet.Entry var2;
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
         if (!var4 && !var7 && LongAVLTreeSet.this.compare(var2, var5) > 0) {
            throw new IllegalArgumentException("Start element (" + var2 + ") is larger than end element (" + var5 + ")");
         } else {
            this.from = var2;
            this.bottom = var4;
            this.to = var5;
            this.top = var7;
         }
      }

      public void clear() {
         LongAVLTreeSet.Subset.SubsetIterator var1 = new LongAVLTreeSet.Subset.SubsetIterator();

         while(var1.hasNext()) {
            var1.nextLong();
            var1.remove();
         }

      }

      final boolean in(long var1) {
         return (this.bottom || LongAVLTreeSet.this.compare(var1, this.from) >= 0) && (this.top || LongAVLTreeSet.this.compare(var1, this.to) < 0);
      }

      public boolean contains(long var1) {
         return this.in(var1) && LongAVLTreeSet.this.contains(var1);
      }

      public boolean add(long var1) {
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Element (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            return LongAVLTreeSet.this.add(var1);
         }
      }

      public boolean remove(long var1) {
         return !this.in(var1) ? false : LongAVLTreeSet.this.remove(var1);
      }

      public int size() {
         LongAVLTreeSet.Subset.SubsetIterator var1 = new LongAVLTreeSet.Subset.SubsetIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextLong();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new LongAVLTreeSet.Subset.SubsetIterator()).hasNext();
      }

      public LongComparator comparator() {
         return LongAVLTreeSet.this.actualComparator;
      }

      public LongBidirectionalIterator iterator() {
         return new LongAVLTreeSet.Subset.SubsetIterator();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new LongAVLTreeSet.Subset.SubsetIterator(var1);
      }

      public LongSortedSet headSet(long var1) {
         if (this.top) {
            return LongAVLTreeSet.this.new Subset(this.from, this.bottom, var1, false);
         } else {
            return LongAVLTreeSet.this.compare(var1, this.to) < 0 ? LongAVLTreeSet.this.new Subset(this.from, this.bottom, var1, false) : this;
         }
      }

      public LongSortedSet tailSet(long var1) {
         if (this.bottom) {
            return LongAVLTreeSet.this.new Subset(var1, false, this.to, this.top);
         } else {
            return LongAVLTreeSet.this.compare(var1, this.from) > 0 ? LongAVLTreeSet.this.new Subset(var1, false, this.to, this.top) : this;
         }
      }

      public LongSortedSet subSet(long var1, long var3) {
         if (this.top && this.bottom) {
            return LongAVLTreeSet.this.new Subset(var1, false, var3, false);
         } else {
            if (!this.top) {
               var3 = LongAVLTreeSet.this.compare(var3, this.to) < 0 ? var3 : this.to;
            }

            if (!this.bottom) {
               var1 = LongAVLTreeSet.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var3 == this.to ? this : LongAVLTreeSet.this.new Subset(var1, false, var3, false);
         }
      }

      public LongAVLTreeSet.Entry firstEntry() {
         if (LongAVLTreeSet.this.tree == null) {
            return null;
         } else {
            LongAVLTreeSet.Entry var1;
            if (this.bottom) {
               var1 = LongAVLTreeSet.this.firstEntry;
            } else {
               var1 = LongAVLTreeSet.this.locateKey(this.from);
               if (LongAVLTreeSet.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || LongAVLTreeSet.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public LongAVLTreeSet.Entry lastEntry() {
         if (LongAVLTreeSet.this.tree == null) {
            return null;
         } else {
            LongAVLTreeSet.Entry var1;
            if (this.top) {
               var1 = LongAVLTreeSet.this.lastEntry;
            } else {
               var1 = LongAVLTreeSet.this.locateKey(this.to);
               if (LongAVLTreeSet.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || LongAVLTreeSet.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public long firstLong() {
         LongAVLTreeSet.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public long lastLong() {
         LongAVLTreeSet.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubsetIterator extends LongAVLTreeSet.SetIterator {
         SubsetIterator() {
            super();
            this.next = Subset.this.firstEntry();
         }

         SubsetIterator(long var2) {
            this();
            if (this.next != null) {
               if (!Subset.this.bottom && LongAVLTreeSet.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Subset.this.top && LongAVLTreeSet.this.compare(var2, (this.prev = Subset.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = LongAVLTreeSet.this.locateKey(var2);
                  if (LongAVLTreeSet.this.compare(this.next.key, var2) <= 0) {
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
            if (!Subset.this.bottom && this.prev != null && LongAVLTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Subset.this.top && this.next != null && LongAVLTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
               this.next = null;
            }

         }
      }
   }

   private class SetIterator implements LongListIterator {
      LongAVLTreeSet.Entry prev;
      LongAVLTreeSet.Entry next;
      LongAVLTreeSet.Entry curr;
      int index = 0;

      SetIterator() {
         super();
         this.next = LongAVLTreeSet.this.firstEntry;
      }

      SetIterator(long var2) {
         super();
         if ((this.next = LongAVLTreeSet.this.locateKey(var2)) != null) {
            if (LongAVLTreeSet.this.compare(this.next.key, var2) <= 0) {
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

      LongAVLTreeSet.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
         }
      }

      public long nextLong() {
         return this.nextEntry().key;
      }

      public long previousLong() {
         return this.previousEntry().key;
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      LongAVLTreeSet.Entry previousEntry() {
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
            LongAVLTreeSet.this.remove(this.curr.key);
            this.curr = null;
         }
      }
   }

   private static final class Entry implements Cloneable {
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      long key;
      LongAVLTreeSet.Entry left;
      LongAVLTreeSet.Entry right;
      int info;

      Entry() {
         super();
      }

      Entry(long var1) {
         super();
         this.key = var1;
         this.info = -1073741824;
      }

      LongAVLTreeSet.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      LongAVLTreeSet.Entry right() {
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

      void pred(LongAVLTreeSet.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(LongAVLTreeSet.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(LongAVLTreeSet.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(LongAVLTreeSet.Entry var1) {
         this.info &= 2147483647;
         this.right = var1;
      }

      int balance() {
         return (byte)this.info;
      }

      void balance(int var1) {
         this.info &= -256;
         this.info |= var1 & 255;
      }

      void incBalance() {
         this.info = this.info & -256 | (byte)this.info + 1 & 255;
      }

      protected void decBalance() {
         this.info = this.info & -256 | (byte)this.info - 1 & 255;
      }

      LongAVLTreeSet.Entry next() {
         LongAVLTreeSet.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      LongAVLTreeSet.Entry prev() {
         LongAVLTreeSet.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public LongAVLTreeSet.Entry clone() {
         LongAVLTreeSet.Entry var1;
         try {
            var1 = (LongAVLTreeSet.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         var1.key = this.key;
         var1.info = this.info;
         return var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof LongAVLTreeSet.Entry)) {
            return false;
         } else {
            LongAVLTreeSet.Entry var2 = (LongAVLTreeSet.Entry)var1;
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
