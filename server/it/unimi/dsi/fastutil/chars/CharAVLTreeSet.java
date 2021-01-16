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

public class CharAVLTreeSet extends AbstractCharSortedSet implements Serializable, Cloneable, CharSortedSet {
   protected transient CharAVLTreeSet.Entry tree;
   protected int count;
   protected transient CharAVLTreeSet.Entry firstEntry;
   protected transient CharAVLTreeSet.Entry lastEntry;
   protected Comparator<? super Character> storedComparator;
   protected transient CharComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353130L;
   private transient boolean[] dirPath;

   public CharAVLTreeSet() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = CharComparators.asCharComparator(this.storedComparator);
   }

   public CharAVLTreeSet(Comparator<? super Character> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public CharAVLTreeSet(Collection<? extends Character> var1) {
      this();
      this.addAll(var1);
   }

   public CharAVLTreeSet(SortedSet<Character> var1) {
      this(var1.comparator());
      this.addAll(var1);
   }

   public CharAVLTreeSet(CharCollection var1) {
      this();
      this.addAll(var1);
   }

   public CharAVLTreeSet(CharSortedSet var1) {
      this((Comparator)var1.comparator());
      this.addAll(var1);
   }

   public CharAVLTreeSet(CharIterator var1) {
      super();
      this.allocatePaths();

      while(var1.hasNext()) {
         this.add(var1.nextChar());
      }

   }

   public CharAVLTreeSet(Iterator<?> var1) {
      this(CharIterators.asCharIterator(var1));
   }

   public CharAVLTreeSet(char[] var1, int var2, int var3, Comparator<? super Character> var4) {
      this(var4);
      CharArrays.ensureOffsetLength(var1, var2, var3);

      for(int var5 = 0; var5 < var3; ++var5) {
         this.add(var1[var2 + var5]);
      }

   }

   public CharAVLTreeSet(char[] var1, int var2, int var3) {
      this(var1, var2, var3, (Comparator)null);
   }

   public CharAVLTreeSet(char[] var1) {
      this();
      int var2 = var1.length;

      while(var2-- != 0) {
         this.add(var1[var2]);
      }

   }

   public CharAVLTreeSet(char[] var1, Comparator<? super Character> var2) {
      this(var2);
      int var3 = var1.length;

      while(var3-- != 0) {
         this.add(var1[var3]);
      }

   }

   final int compare(char var1, char var2) {
      return this.actualComparator == null ? Character.compare(var1, var2) : this.actualComparator.compare(var1, var2);
   }

   private CharAVLTreeSet.Entry findKey(char var1) {
      CharAVLTreeSet.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final CharAVLTreeSet.Entry locateKey(char var1) {
      CharAVLTreeSet.Entry var2 = this.tree;
      CharAVLTreeSet.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public boolean add(char var1) {
      if (this.tree == null) {
         ++this.count;
         this.tree = this.lastEntry = this.firstEntry = new CharAVLTreeSet.Entry(var1);
      } else {
         CharAVLTreeSet.Entry var2 = this.tree;
         CharAVLTreeSet.Entry var3 = null;
         CharAVLTreeSet.Entry var4 = this.tree;
         CharAVLTreeSet.Entry var5 = null;
         CharAVLTreeSet.Entry var6 = null;
         CharAVLTreeSet.Entry var7 = null;
         int var9 = 0;

         while(true) {
            int var8;
            if ((var8 = this.compare(var1, var2.key)) == 0) {
               return false;
            }

            if (var2.balance() != 0) {
               var9 = 0;
               var5 = var3;
               var4 = var2;
            }

            if (this.dirPath[var9++] = var8 > 0) {
               if (var2.succ()) {
                  ++this.count;
                  var6 = new CharAVLTreeSet.Entry(var1);
                  if (var2.right == null) {
                     this.lastEntry = var6;
                  }

                  var6.left = var2;
                  var6.right = var2.right;
                  var2.right(var6);
                  break;
               }

               var3 = var2;
               var2 = var2.right;
            } else {
               if (var2.pred()) {
                  ++this.count;
                  var6 = new CharAVLTreeSet.Entry(var1);
                  if (var2.left == null) {
                     this.firstEntry = var6;
                  }

                  var6.right = var2;
                  var6.left = var2.left;
                  var2.left(var6);
                  break;
               }

               var3 = var2;
               var2 = var2.left;
            }
         }

         var2 = var4;

         for(var9 = 0; var2 != var6; var2 = this.dirPath[var9++] ? var2.right : var2.left) {
            if (this.dirPath[var9]) {
               var2.incBalance();
            } else {
               var2.decBalance();
            }
         }

         CharAVLTreeSet.Entry var10;
         if (var4.balance() == -2) {
            var10 = var4.left;
            if (var10.balance() == -1) {
               var7 = var10;
               if (var10.succ()) {
                  var10.succ(false);
                  var4.pred(var10);
               } else {
                  var4.left = var10.right;
               }

               var10.right = var4;
               var10.balance(0);
               var4.balance(0);
            } else {
               assert var10.balance() == 1;

               var7 = var10.right;
               var10.right = var7.left;
               var7.left = var10;
               var4.left = var7.right;
               var7.right = var4;
               if (var7.balance() == -1) {
                  var10.balance(0);
                  var4.balance(1);
               } else if (var7.balance() == 0) {
                  var10.balance(0);
                  var4.balance(0);
               } else {
                  var10.balance(-1);
                  var4.balance(0);
               }

               var7.balance(0);
               if (var7.pred()) {
                  var10.succ(var7);
                  var7.pred(false);
               }

               if (var7.succ()) {
                  var4.pred(var7);
                  var7.succ(false);
               }
            }
         } else {
            if (var4.balance() != 2) {
               return true;
            }

            var10 = var4.right;
            if (var10.balance() == 1) {
               var7 = var10;
               if (var10.pred()) {
                  var10.pred(false);
                  var4.succ(var10);
               } else {
                  var4.right = var10.left;
               }

               var10.left = var4;
               var10.balance(0);
               var4.balance(0);
            } else {
               assert var10.balance() == -1;

               var7 = var10.left;
               var10.left = var7.right;
               var7.right = var10;
               var4.right = var7.left;
               var7.left = var4;
               if (var7.balance() == 1) {
                  var10.balance(0);
                  var4.balance(-1);
               } else if (var7.balance() == 0) {
                  var10.balance(0);
                  var4.balance(0);
               } else {
                  var10.balance(1);
                  var4.balance(0);
               }

               var7.balance(0);
               if (var7.pred()) {
                  var4.succ(var7);
                  var7.pred(false);
               }

               if (var7.succ()) {
                  var10.pred(var7);
                  var7.succ(false);
               }
            }
         }

         if (var5 == null) {
            this.tree = var7;
         } else if (var5.left == var4) {
            var5.left = var7;
         } else {
            var5.right = var7;
         }
      }

      return true;
   }

   private CharAVLTreeSet.Entry parent(CharAVLTreeSet.Entry var1) {
      if (var1 == this.tree) {
         return null;
      } else {
         CharAVLTreeSet.Entry var3 = var1;

         CharAVLTreeSet.Entry var2;
         CharAVLTreeSet.Entry var4;
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

   public boolean remove(char var1) {
      if (this.tree == null) {
         return false;
      } else {
         CharAVLTreeSet.Entry var3 = this.tree;
         CharAVLTreeSet.Entry var4 = null;
         boolean var5 = false;
         char var6 = var1;

         int var2;
         while((var2 = this.compare(var6, var3.key)) != 0) {
            if (var5 = var2 > 0) {
               var4 = var3;
               if ((var3 = var3.right()) == null) {
                  return false;
               }
            } else {
               var4 = var3;
               if ((var3 = var3.left()) == null) {
                  return false;
               }
            }
         }

         if (var3.left == null) {
            this.firstEntry = var3.next();
         }

         if (var3.right == null) {
            this.lastEntry = var3.prev();
         }

         CharAVLTreeSet.Entry var7;
         CharAVLTreeSet.Entry var8;
         if (var3.succ()) {
            if (var3.pred()) {
               if (var4 != null) {
                  if (var5) {
                     var4.succ(var3.right);
                  } else {
                     var4.pred(var3.left);
                  }
               } else {
                  this.tree = var5 ? var3.right : var3.left;
               }
            } else {
               var3.prev().right = var3.right;
               if (var4 != null) {
                  if (var5) {
                     var4.right = var3.left;
                  } else {
                     var4.left = var3.left;
                  }
               } else {
                  this.tree = var3.left;
               }
            }
         } else {
            var7 = var3.right;
            if (var7.pred()) {
               var7.left = var3.left;
               var7.pred(var3.pred());
               if (!var7.pred()) {
                  var7.prev().right = var7;
               }

               if (var4 != null) {
                  if (var5) {
                     var4.right = var7;
                  } else {
                     var4.left = var7;
                  }
               } else {
                  this.tree = var7;
               }

               var7.balance(var3.balance());
               var4 = var7;
               var5 = true;
            } else {
               while(true) {
                  var8 = var7.left;
                  if (var8.pred()) {
                     if (var8.succ()) {
                        var7.pred(var8);
                     } else {
                        var7.left = var8.right;
                     }

                     var8.left = var3.left;
                     if (!var3.pred()) {
                        var3.prev().right = var8;
                        var8.pred(false);
                     }

                     var8.right = var3.right;
                     var8.succ(false);
                     if (var4 != null) {
                        if (var5) {
                           var4.right = var8;
                        } else {
                           var4.left = var8;
                        }
                     } else {
                        this.tree = var8;
                     }

                     var8.balance(var3.balance());
                     var4 = var7;
                     var5 = false;
                     break;
                  }

                  var7 = var8;
               }
            }
         }

         while(var4 != null) {
            var7 = var4;
            var4 = this.parent(var4);
            CharAVLTreeSet.Entry var9;
            if (!var5) {
               var5 = var4 != null && var4.left != var7;
               var7.incBalance();
               if (var7.balance() == 1) {
                  break;
               }

               if (var7.balance() == 2) {
                  var8 = var7.right;

                  assert var8 != null;

                  if (var8.balance() == -1) {
                     assert var8.balance() == -1;

                     var9 = var8.left;
                     var8.left = var9.right;
                     var9.right = var8;
                     var7.right = var9.left;
                     var9.left = var7;
                     if (var9.balance() == 1) {
                        var8.balance(0);
                        var7.balance(-1);
                     } else if (var9.balance() == 0) {
                        var8.balance(0);
                        var7.balance(0);
                     } else {
                        assert var9.balance() == -1;

                        var8.balance(1);
                        var7.balance(0);
                     }

                     var9.balance(0);
                     if (var9.pred()) {
                        var7.succ(var9);
                        var9.pred(false);
                     }

                     if (var9.succ()) {
                        var8.pred(var9);
                        var9.succ(false);
                     }

                     if (var4 != null) {
                        if (var5) {
                           var4.right = var9;
                        } else {
                           var4.left = var9;
                        }
                     } else {
                        this.tree = var9;
                     }
                  } else {
                     if (var4 != null) {
                        if (var5) {
                           var4.right = var8;
                        } else {
                           var4.left = var8;
                        }
                     } else {
                        this.tree = var8;
                     }

                     if (var8.balance() == 0) {
                        var7.right = var8.left;
                        var8.left = var7;
                        var8.balance(-1);
                        var7.balance(1);
                        break;
                     }

                     assert var8.balance() == 1;

                     if (var8.pred()) {
                        var7.succ(true);
                        var8.pred(false);
                     } else {
                        var7.right = var8.left;
                     }

                     var8.left = var7;
                     var7.balance(0);
                     var8.balance(0);
                  }
               }
            } else {
               var5 = var4 != null && var4.left != var7;
               var7.decBalance();
               if (var7.balance() == -1) {
                  break;
               }

               if (var7.balance() == -2) {
                  var8 = var7.left;

                  assert var8 != null;

                  if (var8.balance() == 1) {
                     assert var8.balance() == 1;

                     var9 = var8.right;
                     var8.right = var9.left;
                     var9.left = var8;
                     var7.left = var9.right;
                     var9.right = var7;
                     if (var9.balance() == -1) {
                        var8.balance(0);
                        var7.balance(1);
                     } else if (var9.balance() == 0) {
                        var8.balance(0);
                        var7.balance(0);
                     } else {
                        assert var9.balance() == 1;

                        var8.balance(-1);
                        var7.balance(0);
                     }

                     var9.balance(0);
                     if (var9.pred()) {
                        var8.succ(var9);
                        var9.pred(false);
                     }

                     if (var9.succ()) {
                        var7.pred(var9);
                        var9.succ(false);
                     }

                     if (var4 != null) {
                        if (var5) {
                           var4.right = var9;
                        } else {
                           var4.left = var9;
                        }
                     } else {
                        this.tree = var9;
                     }
                  } else {
                     if (var4 != null) {
                        if (var5) {
                           var4.right = var8;
                        } else {
                           var4.left = var8;
                        }
                     } else {
                        this.tree = var8;
                     }

                     if (var8.balance() == 0) {
                        var7.left = var8.right;
                        var8.right = var7;
                        var8.balance(1);
                        var7.balance(-1);
                        break;
                     }

                     assert var8.balance() == -1;

                     if (var8.succ()) {
                        var7.pred(true);
                        var8.succ(false);
                     } else {
                        var7.left = var8.right;
                     }

                     var8.right = var7;
                     var7.balance(0);
                     var8.balance(0);
                  }
               }
            }
         }

         --this.count;
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
      return new CharAVLTreeSet.SetIterator();
   }

   public CharBidirectionalIterator iterator(char var1) {
      return new CharAVLTreeSet.SetIterator(var1);
   }

   public CharComparator comparator() {
      return this.actualComparator;
   }

   public CharSortedSet headSet(char var1) {
      return new CharAVLTreeSet.Subset('\u0000', true, var1, false);
   }

   public CharSortedSet tailSet(char var1) {
      return new CharAVLTreeSet.Subset(var1, false, '\u0000', true);
   }

   public CharSortedSet subSet(char var1, char var2) {
      return new CharAVLTreeSet.Subset(var1, false, var2, false);
   }

   public Object clone() {
      CharAVLTreeSet var1;
      try {
         var1 = (CharAVLTreeSet)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      var1.allocatePaths();
      if (this.count == 0) {
         return var1;
      } else {
         CharAVLTreeSet.Entry var5 = new CharAVLTreeSet.Entry();
         CharAVLTreeSet.Entry var6 = new CharAVLTreeSet.Entry();
         CharAVLTreeSet.Entry var3 = var5;
         var5.left(this.tree);
         CharAVLTreeSet.Entry var4 = var6;
         var6.pred((CharAVLTreeSet.Entry)null);

         while(true) {
            CharAVLTreeSet.Entry var2;
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
      CharAVLTreeSet.SetIterator var3 = new CharAVLTreeSet.SetIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         var1.writeChar(var3.nextChar());
      }

   }

   private CharAVLTreeSet.Entry readTree(ObjectInputStream var1, int var2, CharAVLTreeSet.Entry var3, CharAVLTreeSet.Entry var4) throws IOException, ClassNotFoundException {
      CharAVLTreeSet.Entry var8;
      if (var2 == 1) {
         var8 = new CharAVLTreeSet.Entry(var1.readChar());
         var8.pred(var3);
         var8.succ(var4);
         return var8;
      } else if (var2 == 2) {
         var8 = new CharAVLTreeSet.Entry(var1.readChar());
         var8.right(new CharAVLTreeSet.Entry(var1.readChar()));
         var8.right.pred(var8);
         var8.balance(1);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         CharAVLTreeSet.Entry var7 = new CharAVLTreeSet.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readChar();
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
         this.tree = this.readTree(var1, this.count, (CharAVLTreeSet.Entry)null, (CharAVLTreeSet.Entry)null);

         CharAVLTreeSet.Entry var2;
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
         if (!var3 && !var5 && CharAVLTreeSet.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start element (" + var2 + ") is larger than end element (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
         }
      }

      public void clear() {
         CharAVLTreeSet.Subset.SubsetIterator var1 = new CharAVLTreeSet.Subset.SubsetIterator();

         while(var1.hasNext()) {
            var1.nextChar();
            var1.remove();
         }

      }

      final boolean in(char var1) {
         return (this.bottom || CharAVLTreeSet.this.compare(var1, this.from) >= 0) && (this.top || CharAVLTreeSet.this.compare(var1, this.to) < 0);
      }

      public boolean contains(char var1) {
         return this.in(var1) && CharAVLTreeSet.this.contains(var1);
      }

      public boolean add(char var1) {
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Element (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            return CharAVLTreeSet.this.add(var1);
         }
      }

      public boolean remove(char var1) {
         return !this.in(var1) ? false : CharAVLTreeSet.this.remove(var1);
      }

      public int size() {
         CharAVLTreeSet.Subset.SubsetIterator var1 = new CharAVLTreeSet.Subset.SubsetIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextChar();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new CharAVLTreeSet.Subset.SubsetIterator()).hasNext();
      }

      public CharComparator comparator() {
         return CharAVLTreeSet.this.actualComparator;
      }

      public CharBidirectionalIterator iterator() {
         return new CharAVLTreeSet.Subset.SubsetIterator();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new CharAVLTreeSet.Subset.SubsetIterator(var1);
      }

      public CharSortedSet headSet(char var1) {
         if (this.top) {
            return CharAVLTreeSet.this.new Subset(this.from, this.bottom, var1, false);
         } else {
            return CharAVLTreeSet.this.compare(var1, this.to) < 0 ? CharAVLTreeSet.this.new Subset(this.from, this.bottom, var1, false) : this;
         }
      }

      public CharSortedSet tailSet(char var1) {
         if (this.bottom) {
            return CharAVLTreeSet.this.new Subset(var1, false, this.to, this.top);
         } else {
            return CharAVLTreeSet.this.compare(var1, this.from) > 0 ? CharAVLTreeSet.this.new Subset(var1, false, this.to, this.top) : this;
         }
      }

      public CharSortedSet subSet(char var1, char var2) {
         if (this.top && this.bottom) {
            return CharAVLTreeSet.this.new Subset(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = CharAVLTreeSet.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = CharAVLTreeSet.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : CharAVLTreeSet.this.new Subset(var1, false, var2, false);
         }
      }

      public CharAVLTreeSet.Entry firstEntry() {
         if (CharAVLTreeSet.this.tree == null) {
            return null;
         } else {
            CharAVLTreeSet.Entry var1;
            if (this.bottom) {
               var1 = CharAVLTreeSet.this.firstEntry;
            } else {
               var1 = CharAVLTreeSet.this.locateKey(this.from);
               if (CharAVLTreeSet.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || CharAVLTreeSet.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public CharAVLTreeSet.Entry lastEntry() {
         if (CharAVLTreeSet.this.tree == null) {
            return null;
         } else {
            CharAVLTreeSet.Entry var1;
            if (this.top) {
               var1 = CharAVLTreeSet.this.lastEntry;
            } else {
               var1 = CharAVLTreeSet.this.locateKey(this.to);
               if (CharAVLTreeSet.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || CharAVLTreeSet.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public char firstChar() {
         CharAVLTreeSet.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public char lastChar() {
         CharAVLTreeSet.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubsetIterator extends CharAVLTreeSet.SetIterator {
         SubsetIterator() {
            super();
            this.next = Subset.this.firstEntry();
         }

         SubsetIterator(char var2) {
            this();
            if (this.next != null) {
               if (!Subset.this.bottom && CharAVLTreeSet.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Subset.this.top && CharAVLTreeSet.this.compare(var2, (this.prev = Subset.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = CharAVLTreeSet.this.locateKey(var2);
                  if (CharAVLTreeSet.this.compare(this.next.key, var2) <= 0) {
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
            if (!Subset.this.bottom && this.prev != null && CharAVLTreeSet.this.compare(this.prev.key, Subset.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Subset.this.top && this.next != null && CharAVLTreeSet.this.compare(this.next.key, Subset.this.to) >= 0) {
               this.next = null;
            }

         }
      }
   }

   private class SetIterator implements CharListIterator {
      CharAVLTreeSet.Entry prev;
      CharAVLTreeSet.Entry next;
      CharAVLTreeSet.Entry curr;
      int index = 0;

      SetIterator() {
         super();
         this.next = CharAVLTreeSet.this.firstEntry;
      }

      SetIterator(char var2) {
         super();
         if ((this.next = CharAVLTreeSet.this.locateKey(var2)) != null) {
            if (CharAVLTreeSet.this.compare(this.next.key, var2) <= 0) {
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

      CharAVLTreeSet.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
         }
      }

      public char nextChar() {
         return this.nextEntry().key;
      }

      public char previousChar() {
         return this.previousEntry().key;
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      CharAVLTreeSet.Entry previousEntry() {
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
            CharAVLTreeSet.this.remove(this.curr.key);
            this.curr = null;
         }
      }
   }

   private static final class Entry implements Cloneable {
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      char key;
      CharAVLTreeSet.Entry left;
      CharAVLTreeSet.Entry right;
      int info;

      Entry() {
         super();
      }

      Entry(char var1) {
         super();
         this.key = var1;
         this.info = -1073741824;
      }

      CharAVLTreeSet.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      CharAVLTreeSet.Entry right() {
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

      void pred(CharAVLTreeSet.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(CharAVLTreeSet.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(CharAVLTreeSet.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(CharAVLTreeSet.Entry var1) {
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

      CharAVLTreeSet.Entry next() {
         CharAVLTreeSet.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      CharAVLTreeSet.Entry prev() {
         CharAVLTreeSet.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public CharAVLTreeSet.Entry clone() {
         CharAVLTreeSet.Entry var1;
         try {
            var1 = (CharAVLTreeSet.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         var1.key = this.key;
         var1.info = this.info;
         return var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof CharAVLTreeSet.Entry)) {
            return false;
         } else {
            CharAVLTreeSet.Entry var2 = (CharAVLTreeSet.Entry)var1;
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
