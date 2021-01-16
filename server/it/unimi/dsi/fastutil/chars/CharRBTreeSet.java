package it.unimi.dsi.fastutil.chars;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class CharRBTreeSet extends AbstractCharSortedSet implements Serializable, Cloneable, CharSortedSet {
   protected transient CharRBTreeSet.Entry tree;
   protected int count;
   protected transient CharRBTreeSet.Entry firstEntry;
   protected transient CharRBTreeSet.Entry lastEntry;
   protected Comparator<? super Character> storedComparator;
   protected transient CharComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353130L;
   private transient boolean[] dirPath;
   private transient CharRBTreeSet.Entry[] nodePath;

   public CharRBTreeSet() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = CharComparators.asCharComparator(this.storedComparator);
   }

   public CharRBTreeSet(Comparator<? super Character> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public CharRBTreeSet(Collection<? extends Character> var1) {
      this();
      this.addAll(var1);
   }

   public CharRBTreeSet(SortedSet<Character> var1) {
      this(var1.comparator());
      this.addAll(var1);
   }

   public CharRBTreeSet(CharCollection var1) {
      this();
      this.addAll(var1);
   }

   public CharRBTreeSet(CharSortedSet var1) {
      this((Comparator)var1.comparator());
      this.addAll(var1);
   }

   public CharRBTreeSet(CharIterator var1) {
      super();
      this.allocatePaths();

      while(var1.hasNext()) {
         this.add(var1.nextChar());
      }

   }

   public CharRBTreeSet(Iterator<?> var1) {
      this(CharIterators.asCharIterator(var1));
   }

   public CharRBTreeSet(char[] var1, int var2, int var3, Comparator<? super Character> var4) {
      this(var4);
      CharArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public CharRBTreeSet(char[] var1, int var2, int var3) {
      this(var1, var2, var3, (Comparator)null);
   }

   public CharRBTreeSet(char[] var1) {
      this();
      int var2 = var1.length;

      while(var2-- != 0) {
         this.add(var1[var2]);
      }

   }

   public CharRBTreeSet(char[] var1, Comparator<? super Character> var2) {
      this(var2);
      int var3 = var1.length;

      while(var3-- != 0) {
         this.add(var1[var3]);
      }

   }

   final int compare(char var1, char var2) {
      return this.actualComparator == null ? Character.compare(var1, var2) : this.actualComparator.compare(var1, var2);
   }

   private CharRBTreeSet.Entry findKey(char var1) {
      CharRBTreeSet.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final CharRBTreeSet.Entry locateKey(char var1) {
      CharRBTreeSet.Entry var2 = this.tree;
      CharRBTreeSet.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new CharRBTreeSet.Entry[64];
   }

   public boolean add(char var1) {
      int var2 = 0;
      if (this.tree == null) {
         ++this.count;
         this.tree = this.lastEntry = this.firstEntry = new CharRBTreeSet.Entry(var1);
      } else {
         CharRBTreeSet.Entry var3 = this.tree;
         int var6 = 0;

         label123:
         while(true) {
            int var5;
            if ((var5 = this.compare(var1, var3.key)) == 0) {
               while(var6-- != 0) {
                  this.nodePath[var6] = null;
               }

               return false;
            }

            this.nodePath[var6] = var3;
            CharRBTreeSet.Entry var4;
            if (this.dirPath[var6++] = var5 > 0) {
               if (!var3.succ()) {
                  var3 = var3.right;
                  continue;
               }

               ++this.count;
               var4 = new CharRBTreeSet.Entry(var1);
               if (var3.right == null) {
                  this.lastEntry = var4;
               }

               var4.left = var3;
               var4.right = var3.right;
               var3.right(var4);
            } else {
               if (!var3.pred()) {
                  var3 = var3.left;
                  continue;
               }

               ++this.count;
               var4 = new CharRBTreeSet.Entry(var1);
               if (var3.left == null) {
                  this.firstEntry = var4;
               }

               var4.right = var3;
               var4.left = var3.left;
               var3.left(var4);
            }

            var2 = var6--;

            while(true) {
               if (var6 <= 0 || this.nodePath[var6].black()) {
                  break label123;
               }

               CharRBTreeSet.Entry var7;
               CharRBTreeSet.Entry var8;
               if (!this.dirPath[var6 - 1]) {
                  var7 = this.nodePath[var6 - 1].right;
                  if (this.nodePath[var6 - 1].succ() || var7.black()) {
                     if (!this.dirPath[var6]) {
                        var7 = this.nodePath[var6];
                     } else {
                        var8 = this.nodePath[var6];
                        var7 = var8.right;
                        var8.right = var7.left;
                        var7.left = var8;
                        this.nodePath[var6 - 1].left = var7;
                        if (var7.pred()) {
                           var7.pred(false);
                           var8.succ(var7);
                        }
                     }

                     var8 = this.nodePath[var6 - 1];
                     var8.black(false);
                     var7.black(true);
                     var8.left = var7.right;
                     var7.right = var8;
                     if (var6 < 2) {
                        this.tree = var7;
                     } else if (this.dirPath[var6 - 2]) {
                        this.nodePath[var6 - 2].right = var7;
                     } else {
                        this.nodePath[var6 - 2].left = var7;
                     }

                     if (var7.succ()) {
                        var7.succ(false);
                        var8.pred(var7);
                     }
                     break label123;
                  }

                  this.nodePath[var6].black(true);
                  var7.black(true);
                  this.nodePath[var6 - 1].black(false);
                  var6 -= 2;
               } else {
                  var7 = this.nodePath[var6 - 1].left;
                  if (this.nodePath[var6 - 1].pred() || var7.black()) {
                     if (this.dirPath[var6]) {
                        var7 = this.nodePath[var6];
                     } else {
                        var8 = this.nodePath[var6];
                        var7 = var8.left;
                        var8.left = var7.right;
                        var7.right = var8;
                        this.nodePath[var6 - 1].right = var7;
                        if (var7.succ()) {
                           var7.succ(false);
                           var8.pred(var7);
                        }
                     }

                     var8 = this.nodePath[var6 - 1];
                     var8.black(false);
                     var7.black(true);
                     var8.right = var7.left;
                     var7.left = var8;
                     if (var6 < 2) {
                        this.tree = var7;
                     } else if (this.dirPath[var6 - 2]) {
                        this.nodePath[var6 - 2].right = var7;
                     } else {
                        this.nodePath[var6 - 2].left = var7;
                     }

                     if (var7.pred()) {
                        var7.pred(false);
                        var8.succ(var7);
                     }
                     break label123;
                  }

                  this.nodePath[var6].black(true);
                  var7.black(true);
                  this.nodePath[var6 - 1].black(false);
                  var6 -= 2;
               }
            }
         }
      }

      this.tree.black(true);

      while(var2-- != 0) {
         this.nodePath[var2] = null;
      }

      return true;
   }

   public boolean remove(char var1) {
      if (this.tree == null) {
         return false;
      } else {
         CharRBTreeSet.Entry var2 = this.tree;
         int var4 = 0;
         char var5 = var1;

         int var3;
         while((var3 = this.compare(var5, var2.key)) != 0) {
            this.dirPath[var4] = var3 > 0;
            this.nodePath[var4] = var2;
            if (this.dirPath[var4++]) {
               if ((var2 = var2.right()) == null) {
                  while(var4-- != 0) {
                     this.nodePath[var4] = null;
                  }

                  return false;
               }
            } else if ((var2 = var2.left()) == null) {
               while(var4-- != 0) {
                  this.nodePath[var4] = null;
               }

               return false;
            }
         }

         if (var2.left == null) {
            this.firstEntry = var2.next();
         }

         if (var2.right == null) {
            this.lastEntry = var2.prev();
         }

         CharRBTreeSet.Entry var7;
         CharRBTreeSet.Entry var8;
         if (var2.succ()) {
            if (var2.pred()) {
               if (var4 == 0) {
                  this.tree = var2.left;
               } else if (this.dirPath[var4 - 1]) {
                  this.nodePath[var4 - 1].succ(var2.right);
               } else {
                  this.nodePath[var4 - 1].pred(var2.left);
               }
            } else {
               var2.prev().right = var2.right;
               if (var4 == 0) {
                  this.tree = var2.left;
               } else if (this.dirPath[var4 - 1]) {
                  this.nodePath[var4 - 1].right = var2.left;
               } else {
                  this.nodePath[var4 - 1].left = var2.left;
               }
            }
         } else {
            var7 = var2.right;
            boolean var6;
            if (var7.pred()) {
               var7.left = var2.left;
               var7.pred(var2.pred());
               if (!var7.pred()) {
                  var7.prev().right = var7;
               }

               if (var4 == 0) {
                  this.tree = var7;
               } else if (this.dirPath[var4 - 1]) {
                  this.nodePath[var4 - 1].right = var7;
               } else {
                  this.nodePath[var4 - 1].left = var7;
               }

               var6 = var7.black();
               var7.black(var2.black());
               var2.black(var6);
               this.dirPath[var4] = true;
               this.nodePath[var4++] = var7;
            } else {
               int var9 = var4++;

               while(true) {
                  this.dirPath[var4] = false;
                  this.nodePath[var4++] = var7;
                  var8 = var7.left;
                  if (var8.pred()) {
                     this.dirPath[var9] = true;
                     this.nodePath[var9] = var8;
                     if (var8.succ()) {
                        var7.pred(var8);
                     } else {
                        var7.left = var8.right;
                     }

                     var8.left = var2.left;
                     if (!var2.pred()) {
                        var2.prev().right = var8;
                        var8.pred(false);
                     }

                     var8.right(var2.right);
                     var6 = var8.black();
                     var8.black(var2.black());
                     var2.black(var6);
                     if (var9 == 0) {
                        this.tree = var8;
                     } else if (this.dirPath[var9 - 1]) {
                        this.nodePath[var9 - 1].right = var8;
                     } else {
                        this.nodePath[var9 - 1].left = var8;
                     }
                     break;
                  }

                  var7 = var8;
               }
            }
         }

         int var10 = var4;
         if (var2.black()) {
            for(; var4 > 0; --var4) {
               if (this.dirPath[var4 - 1] && !this.nodePath[var4 - 1].succ() || !this.dirPath[var4 - 1] && !this.nodePath[var4 - 1].pred()) {
                  var7 = this.dirPath[var4 - 1] ? this.nodePath[var4 - 1].right : this.nodePath[var4 - 1].left;
                  if (!var7.black()) {
                     var7.black(true);
                     break;
                  }
               }

               if (!this.dirPath[var4 - 1]) {
                  var7 = this.nodePath[var4 - 1].right;
                  if (!var7.black()) {
                     var7.black(true);
                     this.nodePath[var4 - 1].black(false);
                     this.nodePath[var4 - 1].right = var7.left;
                     var7.left = this.nodePath[var4 - 1];
                     if (var4 < 2) {
                        this.tree = var7;
                     } else if (this.dirPath[var4 - 2]) {
                        this.nodePath[var4 - 2].right = var7;
                     } else {
                        this.nodePath[var4 - 2].left = var7;
                     }

                     this.nodePath[var4] = this.nodePath[var4 - 1];
                     this.dirPath[var4] = false;
                     this.nodePath[var4 - 1] = var7;
                     if (var10 == var4++) {
                        ++var10;
                     }

                     var7 = this.nodePath[var4 - 1].right;
                  }

                  if (!var7.pred() && !var7.left.black() || !var7.succ() && !var7.right.black()) {
                     if (var7.succ() || var7.right.black()) {
                        var8 = var7.left;
                        var8.black(true);
                        var7.black(false);
                        var7.left = var8.right;
                        var8.right = var7;
                        var7 = this.nodePath[var4 - 1].right = var8;
                        if (var7.succ()) {
                           var7.succ(false);
                           var7.right.pred(var7);
                        }
                     }

                     var7.black(this.nodePath[var4 - 1].black());
                     this.nodePath[var4 - 1].black(true);
                     var7.right.black(true);
                     this.nodePath[var4 - 1].right = var7.left;
                     var7.left = this.nodePath[var4 - 1];
                     if (var4 < 2) {
                        this.tree = var7;
                     } else if (this.dirPath[var4 - 2]) {
                        this.nodePath[var4 - 2].right = var7;
                     } else {
                        this.nodePath[var4 - 2].left = var7;
                     }

                     if (var7.pred()) {
                        var7.pred(false);
                        this.nodePath[var4 - 1].succ(var7);
                     }
                     break;
                  }

                  var7.black(false);
               } else {
                  var7 = this.nodePath[var4 - 1].left;
                  if (!var7.black()) {
                     var7.black(true);
                     this.nodePath[var4 - 1].black(false);
                     this.nodePath[var4 - 1].left = var7.right;
                     var7.right = this.nodePath[var4 - 1];
                     if (var4 < 2) {
                        this.tree = var7;
                     } else if (this.dirPath[var4 - 2]) {
                        this.nodePath[var4 - 2].right = var7;
                     } else {
                        this.nodePath[var4 - 2].left = var7;
                     }

                     this.nodePath[var4] = this.nodePath[var4 - 1];
                     this.dirPath[var4] = true;
                     this.nodePath[var4 - 1] = var7;
                     if (var10 == var4++) {
                        ++var10;
                     }

                     var7 = this.nodePath[var4 - 1].left;
                  }

                  if (!var7.pred() && !var7.left.black() || !var7.succ() && !var7.right.black()) {
                     if (var7.pred() || var7.left.black()) {
                        var8 = var7.right;
                        var8.black(true);
                        var7.black(false);
                        var7.right = var8.left;
                        var8.left = var7;
                        var7 = this.nodePath[var4 - 1].left = var8;
                        if (var7.pred()) {
                           var7.pred(false);
                           var7.left.succ(var7);
                        }
                     }

                     var7.black(this.nodePath[var4 - 1].black());
                     this.nodePath[var4 - 1].black(true);
                     var7.left.black(true);
                     this.nodePath[var4 - 1].left = var7.right;
                     var7.right = this.nodePath[var4 - 1];
                     if (var4 < 2) {
                        this.tree = var7;
                     } else if (this.dirPath[var4 - 2]) {
                        this.nodePath[var4 - 2].right = var7;
                     } else {
                        this.nodePath[var4 - 2].left = var7;
                     }

                     if (var7.succ()) {
                        var7.succ(false);
                        this.nodePath[var4 - 1].pred(var7);
                     }
                     break;
                  }

                  var7.black(false);
               }
            }

            if (this.tree != null) {
               this.tree.black(true);
            }
         }

         --this.count;

         while(var10-- != 0) {
            this.nodePath[var10] = null;
         }

         return true;
      }
   }

   public boolean contains(char var1) {
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

   public char firstChar() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public char lastChar() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public CharBidirectionalIterator iterator() {
      return new CharRBTreeSet.SetIterator();
   }

   public CharBidirectionalIterator iterator(char var1) {
      return new CharRBTreeSet.SetIterator(var1);
   }

   public CharComparator comparator() {
      return this.actualComparator;
   }

   public CharSortedSet headSet(char var1) {
      return new CharRBTreeSet.Subset('\u0000', true, var1, false);
   }

   public CharSortedSet tailSet(char var1) {
      return new CharRBTreeSet.Subset(var1, false, '\u0000', true);
   }

   public CharSortedSet subSet(char var1, char var2) {
      return new CharRBTreeSet.Subset(var1, false, var2, false);
   }

   public Object clone() {
      CharRBTreeSet var1;
      try {
         var1 = (CharRBTreeSet)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      var1.allocatePaths();
      if (this.count == 0) {
         return var1;
      } else {
         CharRBTreeSet.Entry var5 = new CharRBTreeSet.Entry();
         CharRBTreeSet.Entry var6 = new CharRBTreeSet.Entry();
         CharRBTreeSet.Entry var3 = var5;
         var5.left(this.tree);
         CharRBTreeSet.Entry var4 = var6;
         var6.pred((CharRBTreeSet.Entry)null);

         while(true) {
            CharRBTreeSet.Entry var2;
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
      CharRBTreeSet.SetIterator var3 = new CharRBTreeSet.SetIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         var1.writeChar(var3.nextChar());
      }

   }

   private CharRBTreeSet.Entry readTree(ObjectInputStream var1, int var2, CharRBTreeSet.Entry var3, CharRBTreeSet.Entry var4) throws IOException, ClassNotFoundException {
      CharRBTreeSet.Entry var8;
      if (var2 == 1) {
         var8 = new CharRBTreeSet.Entry(var1.readChar());
         var8.pred(var3);
         var8.succ(var4);
         var8.black(true);
         return var8;
      } else if (var2 == 2) {
         var8 = new CharRBTreeSet.Entry(var1.readChar());
         var8.black(true);
         var8.right(new CharRBTreeSet.Entry(var1.readChar()));
         var8.right.pred(var8);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         CharRBTreeSet.Entry var7 = new CharRBTreeSet.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readChar();
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
         this.tree = this.readTree(var1, this.count, (CharRBTreeSet.Entry)null, (CharRBTreeSet.Entry)null);

         CharRBTreeSet.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Subset extends AbstractCharSortedSet implements Serializable, CharSortedSet {
      private static final long serialVersionUID = -7046029254386353129L;
      char from;
      char to;
      boolean bottom;
      boolean top;

      public Subset(char var2, boolean var3, char var4, boolean var5) {
         super();
         if (!var3 && !var5 && CharRBTreeSet.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start element (" + var2 + ") is larger than end element (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
         }
      }

      public void clear() {
         CharRBTreeSet.Subset.SubsetIterator var1 = new CharRBTreeSet.Subset.SubsetIterator();

         while(var1.hasNext()) {
            var1.nextChar();
            var1.remove();
         }

      }

      final boolean in(char var1) {
         return (this.bottom || CharRBTreeSet.this.compare(var1, this.from) >= 0) && (this.top || CharRBTreeSet.this.compare(var1, this.to) < 0);
      }

      public boolean contains(char var1) {
         return this.in(var1) && CharRBTreeSet.this.contains(var1);
      }

      public boolean add(char var1) {
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Element (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            return CharRBTreeSet.this.add(var1);
         }
      }

      public boolean remove(char var1) {
         return !this.in(var1) ? false : CharRBTreeSet.this.remove(var1);
      }

      public int size() {
         CharRBTreeSet.Subset.SubsetIterator var1 = new CharRBTreeSet.Subset.SubsetIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextChar();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new CharRBTreeSet.Subset.SubsetIterator()).hasNext();
      }

      public CharComparator comparator() {
         return CharRBTreeSet.this.actualComparator;
      }

      public CharBidirectionalIterator iterator() {
         return new CharRBTreeSet.Subset.SubsetIterator();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new CharRBTreeSet.Subset.SubsetIterator(var1);
      }

      public CharSortedSet headSet(char var1) {
         if (this.top) {
            return CharRBTreeSet.this.new Subset(this.from, this.bottom, var1, false);
         } else {
            return CharRBTreeSet.this.compare(var1, this.to) < 0 ? CharRBTreeSet.this.new Subset(this.from, this.bottom, var1, false) : this;
         }
      }

      public CharSortedSet tailSet(char var1) {
         if (this.bottom) {
            return CharRBTreeSet.this.new Subset(var1, false, this.to, this.top);
         } else {
            return CharRBTreeSet.this.compare(var1, this.from) > 0 ? CharRBTreeSet.this.new Subset(var1, false, this.to, this.top) : this;
         }
      }

      public CharSortedSet subSet(char var1, char var2) {
         if (this.top && this.bottom) {
            return CharRBTreeSet.this.new Subset(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = CharRBTreeSet.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = CharRBTreeSet.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : CharRBTreeSet.this.new Subset(var1, false, var2, false);
         }
      }

      public CharRBTreeSet.Entry firstEntry() {
         if (CharRBTreeSet.this.tree == null) {
            return null;
         } else {
            CharRBTreeSet.Entry var1;
            if (this.bottom) {
               var1 = CharRBTreeSet.this.firstEntry;
            } else {
               var1 = CharRBTreeSet.this.locateKey(this.from);
               if (CharRBTreeSet.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || CharRBTreeSet.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public CharRBTreeSet.Entry lastEntry() {
         if (CharRBTreeSet.this.tree == null) {
            return null;
         } else {
            CharRBTreeSet.Entry var1;
            if (this.top) {
               var1 = CharRBTreeSet.this.lastEntry;
            } else {
               var1 = CharRBTreeSet.this.locateKey(this.to);
               if (CharRBTreeSet.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || CharRBTreeSet.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public char firstChar() {
         CharRBTreeSet.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public char lastChar() {
         CharRBTreeSet.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubsetIterator extends CharRBTreeSet.SetIterator {
         SubsetIterator() {
            super();
            this.next = Subset.this.firstEntry();
         }

         SubsetIterator(char var2) {
            this();
            if (this.next != null) {
               if (!Subset.this.bottom && CharRBTreeSet.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Subset.this.top && CharRBTreeSet.this.compare(var2, (this.prev = Subset.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = CharRBTreeSet.this.locateKey(var2);
                  if (CharRBTreeSet.this.compare(this.next.key, var2) <= 0) {
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
            if (!Subset.this.bottom && this.prev != null && CharRBTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Subset.this.top && this.next != null && CharRBTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
               this.next = null;
            }

         }
      }
   }

   private class SetIterator implements CharListIterator {
      CharRBTreeSet.Entry prev;
      CharRBTreeSet.Entry next;
      CharRBTreeSet.Entry curr;
      int index = 0;

      SetIterator() {
         super();
         this.next = CharRBTreeSet.this.firstEntry;
      }

      SetIterator(char var2) {
         super();
         if ((this.next = CharRBTreeSet.this.locateKey(var2)) != null) {
            if (CharRBTreeSet.this.compare(this.next.key, var2) <= 0) {
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

      public char nextChar() {
         return this.nextEntry().key;
      }

      public char previousChar() {
         return this.previousEntry().key;
      }

      CharRBTreeSet.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
         }
      }

      CharRBTreeSet.Entry previousEntry() {
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
            CharRBTreeSet.this.remove(this.curr.key);
            this.curr = null;
         }
      }
   }

   private static final class Entry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      char key;
      CharRBTreeSet.Entry left;
      CharRBTreeSet.Entry right;
      int info;

      Entry() {
         super();
      }

      Entry(char var1) {
         super();
         this.key = var1;
         this.info = -1073741824;
      }

      CharRBTreeSet.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      CharRBTreeSet.Entry right() {
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

      void pred(CharRBTreeSet.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(CharRBTreeSet.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(CharRBTreeSet.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(CharRBTreeSet.Entry var1) {
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

      CharRBTreeSet.Entry next() {
         CharRBTreeSet.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      CharRBTreeSet.Entry prev() {
         CharRBTreeSet.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public CharRBTreeSet.Entry clone() {
         CharRBTreeSet.Entry var1;
         try {
            var1 = (CharRBTreeSet.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         var1.key = this.key;
         var1.info = this.info;
         return var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof CharRBTreeSet.Entry)) {
            return false;
         } else {
            CharRBTreeSet.Entry var2 = (CharRBTreeSet.Entry)var1;
            return this.key == var2.key;
         }
      }

      public int hashCode() {
         return this.key;
      }

      public String toString() {
         return String.valueOf(this.key);
      }
   }
}
