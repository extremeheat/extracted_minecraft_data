package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Char2ByteRBTreeMap extends AbstractChar2ByteSortedMap implements Serializable, Cloneable {
   protected transient Char2ByteRBTreeMap.Entry tree;
   protected int count;
   protected transient Char2ByteRBTreeMap.Entry firstEntry;
   protected transient Char2ByteRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Char2ByteMap.Entry> entries;
   protected transient CharSortedSet keys;
   protected transient ByteCollection values;
   protected transient boolean modified;
   protected Comparator<? super Character> storedComparator;
   protected transient CharComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Char2ByteRBTreeMap.Entry[] nodePath;

   public Char2ByteRBTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = CharComparators.asCharComparator(this.storedComparator);
   }

   public Char2ByteRBTreeMap(Comparator<? super Character> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Char2ByteRBTreeMap(Map<? extends Character, ? extends Byte> var1) {
      this();
      this.putAll(var1);
   }

   public Char2ByteRBTreeMap(SortedMap<Character, Byte> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Char2ByteRBTreeMap(Char2ByteMap var1) {
      this();
      this.putAll(var1);
   }

   public Char2ByteRBTreeMap(Char2ByteSortedMap var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Char2ByteRBTreeMap(char[] var1, byte[] var2, Comparator<? super Character> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Char2ByteRBTreeMap(char[] var1, byte[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(char var1, char var2) {
      return this.actualComparator == null ? Character.compare(var1, var2) : this.actualComparator.compare(var1, var2);
   }

   final Char2ByteRBTreeMap.Entry findKey(char var1) {
      Char2ByteRBTreeMap.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final Char2ByteRBTreeMap.Entry locateKey(char var1) {
      Char2ByteRBTreeMap.Entry var2 = this.tree;
      Char2ByteRBTreeMap.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Char2ByteRBTreeMap.Entry[64];
   }

   public byte addTo(char var1, byte var2) {
      Char2ByteRBTreeMap.Entry var3 = this.add(var1);
      byte var4 = var3.value;
      var3.value += var2;
      return var4;
   }

   public byte put(char var1, byte var2) {
      Char2ByteRBTreeMap.Entry var3 = this.add(var1);
      byte var4 = var3.value;
      var3.value = var2;
      return var4;
   }

   private Char2ByteRBTreeMap.Entry add(char var1) {
      this.modified = false;
      int var2 = 0;
      Char2ByteRBTreeMap.Entry var3;
      if (this.tree == null) {
         ++this.count;
         var3 = this.tree = this.lastEntry = this.firstEntry = new Char2ByteRBTreeMap.Entry(var1, this.defRetValue);
      } else {
         Char2ByteRBTreeMap.Entry var4 = this.tree;
         int var6 = 0;

         label123:
         while(true) {
            int var5;
            if ((var5 = this.compare(var1, var4.key)) == 0) {
               while(var6-- != 0) {
                  this.nodePath[var6] = null;
               }

               return var4;
            }

            this.nodePath[var6] = var4;
            if (this.dirPath[var6++] = var5 > 0) {
               if (!var4.succ()) {
                  var4 = var4.right;
                  continue;
               }

               ++this.count;
               var3 = new Char2ByteRBTreeMap.Entry(var1, this.defRetValue);
               if (var4.right == null) {
                  this.lastEntry = var3;
               }

               var3.left = var4;
               var3.right = var4.right;
               var4.right(var3);
            } else {
               if (!var4.pred()) {
                  var4 = var4.left;
                  continue;
               }

               ++this.count;
               var3 = new Char2ByteRBTreeMap.Entry(var1, this.defRetValue);
               if (var4.left == null) {
                  this.firstEntry = var3;
               }

               var3.right = var4;
               var3.left = var4.left;
               var4.left(var3);
            }

            this.modified = true;
            var2 = var6--;

            while(true) {
               if (var6 <= 0 || this.nodePath[var6].black()) {
                  break label123;
               }

               Char2ByteRBTreeMap.Entry var7;
               Char2ByteRBTreeMap.Entry var8;
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

      return var3;
   }

   public byte remove(char var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Char2ByteRBTreeMap.Entry var2 = this.tree;
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

                  return this.defRetValue;
               }
            } else if ((var2 = var2.left()) == null) {
               while(var4-- != 0) {
                  this.nodePath[var4] = null;
               }

               return this.defRetValue;
            }
         }

         if (var2.left == null) {
            this.firstEntry = var2.next();
         }

         if (var2.right == null) {
            this.lastEntry = var2.prev();
         }

         Char2ByteRBTreeMap.Entry var7;
         Char2ByteRBTreeMap.Entry var8;
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

         this.modified = true;
         --this.count;

         while(var10-- != 0) {
            this.nodePath[var10] = null;
         }

         return var2.value;
      }
   }

   public boolean containsValue(byte var1) {
      Char2ByteRBTreeMap.ValueIterator var2 = new Char2ByteRBTreeMap.ValueIterator();
      int var4 = this.count;

      byte var3;
      do {
         if (var4-- == 0) {
            return false;
         }

         var3 = var2.nextByte();
      } while(var3 != var1);

      return true;
   }

   public void clear() {
      this.count = 0;
      this.tree = null;
      this.entries = null;
      this.values = null;
      this.keys = null;
      this.firstEntry = this.lastEntry = null;
   }

   public boolean containsKey(char var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public byte get(char var1) {
      Char2ByteRBTreeMap.Entry var2 = this.findKey(var1);
      return var2 == null ? this.defRetValue : var2.value;
   }

   public char firstCharKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public char lastCharKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Char2ByteMap.Entry>() {
            final Comparator<? super Char2ByteMap.Entry> comparator = (var1x, var2) -> {
               return Char2ByteRBTreeMap.this.actualComparator.compare(var1x.getCharKey(), var2.getCharKey());
            };

            public Comparator<? super Char2ByteMap.Entry> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Char2ByteMap.Entry> iterator() {
               return Char2ByteRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Char2ByteMap.Entry> iterator(Char2ByteMap.Entry var1) {
               return Char2ByteRBTreeMap.this.new EntryIterator(var1.getCharKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Character) {
                     if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                        Char2ByteRBTreeMap.Entry var3 = Char2ByteRBTreeMap.this.findKey((Character)var2.getKey());
                        return var2.equals(var3);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }

            public boolean remove(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Character) {
                     if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                        Char2ByteRBTreeMap.Entry var3 = Char2ByteRBTreeMap.this.findKey((Character)var2.getKey());
                        if (var3 != null && var3.getByteValue() == (Byte)var2.getValue()) {
                           Char2ByteRBTreeMap.this.remove(var3.key);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }

            public int size() {
               return Char2ByteRBTreeMap.this.count;
            }

            public void clear() {
               Char2ByteRBTreeMap.this.clear();
            }

            public Char2ByteMap.Entry first() {
               return Char2ByteRBTreeMap.this.firstEntry;
            }

            public Char2ByteMap.Entry last() {
               return Char2ByteRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Char2ByteMap.Entry> subSet(Char2ByteMap.Entry var1, Char2ByteMap.Entry var2) {
               return Char2ByteRBTreeMap.this.subMap(var1.getCharKey(), var2.getCharKey()).char2ByteEntrySet();
            }

            public ObjectSortedSet<Char2ByteMap.Entry> headSet(Char2ByteMap.Entry var1) {
               return Char2ByteRBTreeMap.this.headMap(var1.getCharKey()).char2ByteEntrySet();
            }

            public ObjectSortedSet<Char2ByteMap.Entry> tailSet(Char2ByteMap.Entry var1) {
               return Char2ByteRBTreeMap.this.tailMap(var1.getCharKey()).char2ByteEntrySet();
            }
         };
      }

      return this.entries;
   }

   public CharSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2ByteRBTreeMap.KeySet();
      }

      return this.keys;
   }

   public ByteCollection values() {
      if (this.values == null) {
         this.values = new AbstractByteCollection() {
            public ByteIterator iterator() {
               return Char2ByteRBTreeMap.this.new ValueIterator();
            }

            public boolean contains(byte var1) {
               return Char2ByteRBTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Char2ByteRBTreeMap.this.count;
            }

            public void clear() {
               Char2ByteRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public CharComparator comparator() {
      return this.actualComparator;
   }

   public Char2ByteSortedMap headMap(char var1) {
      return new Char2ByteRBTreeMap.Submap('\u0000', true, var1, false);
   }

   public Char2ByteSortedMap tailMap(char var1) {
      return new Char2ByteRBTreeMap.Submap(var1, false, '\u0000', true);
   }

   public Char2ByteSortedMap subMap(char var1, char var2) {
      return new Char2ByteRBTreeMap.Submap(var1, false, var2, false);
   }

   public Char2ByteRBTreeMap clone() {
      Char2ByteRBTreeMap var1;
      try {
         var1 = (Char2ByteRBTreeMap)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      var1.keys = null;
      var1.values = null;
      var1.entries = null;
      var1.allocatePaths();
      if (this.count == 0) {
         return var1;
      } else {
         Char2ByteRBTreeMap.Entry var5 = new Char2ByteRBTreeMap.Entry();
         Char2ByteRBTreeMap.Entry var6 = new Char2ByteRBTreeMap.Entry();
         Char2ByteRBTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Char2ByteRBTreeMap.Entry var4 = var6;
         var6.pred((Char2ByteRBTreeMap.Entry)null);

         while(true) {
            Char2ByteRBTreeMap.Entry var2;
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
      Char2ByteRBTreeMap.EntryIterator var3 = new Char2ByteRBTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Char2ByteRBTreeMap.Entry var4 = var3.nextEntry();
         var1.writeChar(var4.key);
         var1.writeByte(var4.value);
      }

   }

   private Char2ByteRBTreeMap.Entry readTree(ObjectInputStream var1, int var2, Char2ByteRBTreeMap.Entry var3, Char2ByteRBTreeMap.Entry var4) throws IOException, ClassNotFoundException {
      Char2ByteRBTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Char2ByteRBTreeMap.Entry(var1.readChar(), var1.readByte());
         var8.pred(var3);
         var8.succ(var4);
         var8.black(true);
         return var8;
      } else if (var2 == 2) {
         var8 = new Char2ByteRBTreeMap.Entry(var1.readChar(), var1.readByte());
         var8.black(true);
         var8.right(new Char2ByteRBTreeMap.Entry(var1.readChar(), var1.readByte()));
         var8.right.pred(var8);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Char2ByteRBTreeMap.Entry var7 = new Char2ByteRBTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readChar();
         var7.value = var1.readByte();
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
         this.tree = this.readTree(var1, this.count, (Char2ByteRBTreeMap.Entry)null, (Char2ByteRBTreeMap.Entry)null);

         Char2ByteRBTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractChar2ByteSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      char from;
      char to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Char2ByteMap.Entry> entries;
      protected transient CharSortedSet keys;
      protected transient ByteCollection values;

      public Submap(char var2, boolean var3, char var4, boolean var5) {
         super();
         if (!var3 && !var5 && Char2ByteRBTreeMap.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
            this.defRetValue = Char2ByteRBTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Char2ByteRBTreeMap.Submap.SubmapIterator var1 = new Char2ByteRBTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(char var1) {
         return (this.bottom || Char2ByteRBTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Char2ByteRBTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Char2ByteMap.Entry> char2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Char2ByteMap.Entry>() {
               public ObjectBidirectionalIterator<Char2ByteMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Char2ByteMap.Entry> iterator(Char2ByteMap.Entry var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getCharKey());
               }

               public Comparator<? super Char2ByteMap.Entry> comparator() {
                  return Char2ByteRBTreeMap.this.char2ByteEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Character) {
                        if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                           Char2ByteRBTreeMap.Entry var3 = Char2ByteRBTreeMap.this.findKey((Character)var2.getKey());
                           return var3 != null && Submap.this.in(var3.key) && var2.equals(var3);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  }
               }

               public boolean remove(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Character) {
                        if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                           Char2ByteRBTreeMap.Entry var3 = Char2ByteRBTreeMap.this.findKey((Character)var2.getKey());
                           if (var3 != null && Submap.this.in(var3.key)) {
                              Submap.this.remove(var3.key);
                           }

                           return var3 != null;
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  }
               }

               public int size() {
                  int var1 = 0;
                  ObjectBidirectionalIterator var2 = this.iterator();

                  while(var2.hasNext()) {
                     ++var1;
                     var2.next();
                  }

                  return var1;
               }

               public boolean isEmpty() {
                  return !(Submap.this.new SubmapIterator()).hasNext();
               }

               public void clear() {
                  Submap.this.clear();
               }

               public Char2ByteMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Char2ByteMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Char2ByteMap.Entry> subSet(Char2ByteMap.Entry var1, Char2ByteMap.Entry var2) {
                  return Submap.this.subMap(var1.getCharKey(), var2.getCharKey()).char2ByteEntrySet();
               }

               public ObjectSortedSet<Char2ByteMap.Entry> headSet(Char2ByteMap.Entry var1) {
                  return Submap.this.headMap(var1.getCharKey()).char2ByteEntrySet();
               }

               public ObjectSortedSet<Char2ByteMap.Entry> tailSet(Char2ByteMap.Entry var1) {
                  return Submap.this.tailMap(var1.getCharKey()).char2ByteEntrySet();
               }
            };
         }

         return this.entries;
      }

      public CharSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Char2ByteRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      public ByteCollection values() {
         if (this.values == null) {
            this.values = new AbstractByteCollection() {
               public ByteIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               public boolean contains(byte var1) {
                  return Submap.this.containsValue(var1);
               }

               public int size() {
                  return Submap.this.size();
               }

               public void clear() {
                  Submap.this.clear();
               }
            };
         }

         return this.values;
      }

      public boolean containsKey(char var1) {
         return this.in(var1) && Char2ByteRBTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(byte var1) {
         Char2ByteRBTreeMap.Submap.SubmapIterator var2 = new Char2ByteRBTreeMap.Submap.SubmapIterator();

         byte var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = var2.nextEntry().value;
         } while(var3 != var1);

         return true;
      }

      public byte get(char var1) {
         Char2ByteRBTreeMap.Entry var2;
         return this.in(var1) && (var2 = Char2ByteRBTreeMap.this.findKey(var1)) != null ? var2.value : this.defRetValue;
      }

      public byte put(char var1, byte var2) {
         Char2ByteRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            byte var3 = Char2ByteRBTreeMap.this.put(var1, var2);
            return Char2ByteRBTreeMap.this.modified ? this.defRetValue : var3;
         }
      }

      public byte remove(char var1) {
         Char2ByteRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            byte var2 = Char2ByteRBTreeMap.this.remove(var1);
            return Char2ByteRBTreeMap.this.modified ? var2 : this.defRetValue;
         }
      }

      public int size() {
         Char2ByteRBTreeMap.Submap.SubmapIterator var1 = new Char2ByteRBTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Char2ByteRBTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public CharComparator comparator() {
         return Char2ByteRBTreeMap.this.actualComparator;
      }

      public Char2ByteSortedMap headMap(char var1) {
         if (this.top) {
            return Char2ByteRBTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Char2ByteRBTreeMap.this.compare(var1, this.to) < 0 ? Char2ByteRBTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Char2ByteSortedMap tailMap(char var1) {
         if (this.bottom) {
            return Char2ByteRBTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Char2ByteRBTreeMap.this.compare(var1, this.from) > 0 ? Char2ByteRBTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Char2ByteSortedMap subMap(char var1, char var2) {
         if (this.top && this.bottom) {
            return Char2ByteRBTreeMap.this.new Submap(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = Char2ByteRBTreeMap.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = Char2ByteRBTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : Char2ByteRBTreeMap.this.new Submap(var1, false, var2, false);
         }
      }

      public Char2ByteRBTreeMap.Entry firstEntry() {
         if (Char2ByteRBTreeMap.this.tree == null) {
            return null;
         } else {
            Char2ByteRBTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Char2ByteRBTreeMap.this.firstEntry;
            } else {
               var1 = Char2ByteRBTreeMap.this.locateKey(this.from);
               if (Char2ByteRBTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Char2ByteRBTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Char2ByteRBTreeMap.Entry lastEntry() {
         if (Char2ByteRBTreeMap.this.tree == null) {
            return null;
         } else {
            Char2ByteRBTreeMap.Entry var1;
            if (this.top) {
               var1 = Char2ByteRBTreeMap.this.lastEntry;
            } else {
               var1 = Char2ByteRBTreeMap.this.locateKey(this.to);
               if (Char2ByteRBTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Char2ByteRBTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public char firstCharKey() {
         Char2ByteRBTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public char lastCharKey() {
         Char2ByteRBTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Char2ByteRBTreeMap.Submap.SubmapIterator implements ByteListIterator {
         private SubmapValueIterator() {
            super();
         }

         public byte nextByte() {
            return this.nextEntry().value;
         }

         public byte previousByte() {
            return this.previousEntry().value;
         }

         // $FF: synthetic method
         SubmapValueIterator(Object var2) {
            this();
         }
      }

      private final class SubmapKeyIterator extends Char2ByteRBTreeMap.Submap.SubmapIterator implements CharListIterator {
         public SubmapKeyIterator() {
            super();
         }

         public SubmapKeyIterator(char var2) {
            super(var2);
         }

         public char nextChar() {
            return this.nextEntry().key;
         }

         public char previousChar() {
            return this.previousEntry().key;
         }
      }

      private class SubmapEntryIterator extends Char2ByteRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Char2ByteMap.Entry> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(char var2) {
            super(var2);
         }

         public Char2ByteMap.Entry next() {
            return this.nextEntry();
         }

         public Char2ByteMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Char2ByteRBTreeMap.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(char var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Char2ByteRBTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Char2ByteRBTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Char2ByteRBTreeMap.this.locateKey(var2);
                  if (Char2ByteRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Char2ByteRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Char2ByteRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractChar2ByteSortedMap.KeySet {
         private KeySet() {
            super();
         }

         public CharBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         public CharBidirectionalIterator iterator(char var1) {
            return Submap.this.new SubmapKeyIterator(var1);
         }

         // $FF: synthetic method
         KeySet(Object var2) {
            this();
         }
      }
   }

   private final class ValueIterator extends Char2ByteRBTreeMap.TreeIterator implements ByteListIterator {
      private ValueIterator() {
         super();
      }

      public byte nextByte() {
         return this.nextEntry().value;
      }

      public byte previousByte() {
         return this.previousEntry().value;
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractChar2ByteSortedMap.KeySet {
      private KeySet() {
         super();
      }

      public CharBidirectionalIterator iterator() {
         return Char2ByteRBTreeMap.this.new KeyIterator();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return Char2ByteRBTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Char2ByteRBTreeMap.TreeIterator implements CharListIterator {
      public KeyIterator() {
         super();
      }

      public KeyIterator(char var2) {
         super(var2);
      }

      public char nextChar() {
         return this.nextEntry().key;
      }

      public char previousChar() {
         return this.previousEntry().key;
      }
   }

   private class EntryIterator extends Char2ByteRBTreeMap.TreeIterator implements ObjectListIterator<Char2ByteMap.Entry> {
      EntryIterator() {
         super();
      }

      EntryIterator(char var2) {
         super(var2);
      }

      public Char2ByteMap.Entry next() {
         return this.nextEntry();
      }

      public Char2ByteMap.Entry previous() {
         return this.previousEntry();
      }
   }

   private class TreeIterator {
      Char2ByteRBTreeMap.Entry prev;
      Char2ByteRBTreeMap.Entry next;
      Char2ByteRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Char2ByteRBTreeMap.this.firstEntry;
      }

      TreeIterator(char var2) {
         super();
         if ((this.next = Char2ByteRBTreeMap.this.locateKey(var2)) != null) {
            if (Char2ByteRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Char2ByteRBTreeMap.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            ++this.index;
            this.updateNext();
            return this.curr;
         }
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      Char2ByteRBTreeMap.Entry previousEntry() {
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
            Char2ByteRBTreeMap.this.remove(this.curr.key);
            this.curr = null;
         }
      }

      public int skip(int var1) {
         int var2 = var1;

         while(var2-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return var1 - var2 - 1;
      }

      public int back(int var1) {
         int var2 = var1;

         while(var2-- != 0 && this.hasPrevious()) {
            this.previousEntry();
         }

         return var1 - var2 - 1;
      }
   }

   private static final class Entry extends AbstractChar2ByteMap.BasicEntry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      Char2ByteRBTreeMap.Entry left;
      Char2ByteRBTreeMap.Entry right;
      int info;

      Entry() {
         super('\u0000', (byte)0);
      }

      Entry(char var1, byte var2) {
         super(var1, var2);
         this.info = -1073741824;
      }

      Char2ByteRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Char2ByteRBTreeMap.Entry right() {
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

      void pred(Char2ByteRBTreeMap.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Char2ByteRBTreeMap.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Char2ByteRBTreeMap.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Char2ByteRBTreeMap.Entry var1) {
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

      Char2ByteRBTreeMap.Entry next() {
         Char2ByteRBTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Char2ByteRBTreeMap.Entry prev() {
         Char2ByteRBTreeMap.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public byte setValue(byte var1) {
         byte var2 = this.value;
         this.value = var1;
         return var2;
      }

      public Char2ByteRBTreeMap.Entry clone() {
         Char2ByteRBTreeMap.Entry var1;
         try {
            var1 = (Char2ByteRBTreeMap.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         var1.key = this.key;
         var1.value = this.value;
         var1.info = this.info;
         return var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            return this.key == (Character)var2.getKey() && this.value == (Byte)var2.getValue();
         }
      }

      public int hashCode() {
         return this.key ^ this.value;
      }

      public String toString() {
         return this.key + "=>" + this.value;
      }
   }
}
