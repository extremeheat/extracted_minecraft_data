package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
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

public class Byte2CharRBTreeMap extends AbstractByte2CharSortedMap implements Serializable, Cloneable {
   protected transient Byte2CharRBTreeMap.Entry tree;
   protected int count;
   protected transient Byte2CharRBTreeMap.Entry firstEntry;
   protected transient Byte2CharRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Byte2CharMap.Entry> entries;
   protected transient ByteSortedSet keys;
   protected transient CharCollection values;
   protected transient boolean modified;
   protected Comparator<? super Byte> storedComparator;
   protected transient ByteComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Byte2CharRBTreeMap.Entry[] nodePath;

   public Byte2CharRBTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = ByteComparators.asByteComparator(this.storedComparator);
   }

   public Byte2CharRBTreeMap(Comparator<? super Byte> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Byte2CharRBTreeMap(Map<? extends Byte, ? extends Character> var1) {
      this();
      this.putAll(var1);
   }

   public Byte2CharRBTreeMap(SortedMap<Byte, Character> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Byte2CharRBTreeMap(Byte2CharMap var1) {
      this();
      this.putAll(var1);
   }

   public Byte2CharRBTreeMap(Byte2CharSortedMap var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Byte2CharRBTreeMap(byte[] var1, char[] var2, Comparator<? super Byte> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Byte2CharRBTreeMap(byte[] var1, char[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(byte var1, byte var2) {
      return this.actualComparator == null ? Byte.compare(var1, var2) : this.actualComparator.compare(var1, var2);
   }

   final Byte2CharRBTreeMap.Entry findKey(byte var1) {
      Byte2CharRBTreeMap.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final Byte2CharRBTreeMap.Entry locateKey(byte var1) {
      Byte2CharRBTreeMap.Entry var2 = this.tree;
      Byte2CharRBTreeMap.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Byte2CharRBTreeMap.Entry[64];
   }

   public char addTo(byte var1, char var2) {
      Byte2CharRBTreeMap.Entry var3 = this.add(var1);
      char var4 = var3.value;
      var3.value += var2;
      return var4;
   }

   public char put(byte var1, char var2) {
      Byte2CharRBTreeMap.Entry var3 = this.add(var1);
      char var4 = var3.value;
      var3.value = var2;
      return var4;
   }

   private Byte2CharRBTreeMap.Entry add(byte var1) {
      this.modified = false;
      int var2 = 0;
      Byte2CharRBTreeMap.Entry var3;
      if (this.tree == null) {
         ++this.count;
         var3 = this.tree = this.lastEntry = this.firstEntry = new Byte2CharRBTreeMap.Entry(var1, this.defRetValue);
      } else {
         Byte2CharRBTreeMap.Entry var4 = this.tree;
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
               var3 = new Byte2CharRBTreeMap.Entry(var1, this.defRetValue);
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
               var3 = new Byte2CharRBTreeMap.Entry(var1, this.defRetValue);
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

               Byte2CharRBTreeMap.Entry var7;
               Byte2CharRBTreeMap.Entry var8;
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

   public char remove(byte var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Byte2CharRBTreeMap.Entry var2 = this.tree;
         int var4 = 0;
         byte var5 = var1;

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

         Byte2CharRBTreeMap.Entry var7;
         Byte2CharRBTreeMap.Entry var8;
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

   public boolean containsValue(char var1) {
      Byte2CharRBTreeMap.ValueIterator var2 = new Byte2CharRBTreeMap.ValueIterator();
      int var4 = this.count;

      char var3;
      do {
         if (var4-- == 0) {
            return false;
         }

         var3 = var2.nextChar();
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

   public boolean containsKey(byte var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public char get(byte var1) {
      Byte2CharRBTreeMap.Entry var2 = this.findKey(var1);
      return var2 == null ? this.defRetValue : var2.value;
   }

   public byte firstByteKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public byte lastByteKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Byte2CharMap.Entry>() {
            final Comparator<? super Byte2CharMap.Entry> comparator = (var1x, var2) -> {
               return Byte2CharRBTreeMap.this.actualComparator.compare(var1x.getByteKey(), var2.getByteKey());
            };

            public Comparator<? super Byte2CharMap.Entry> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Byte2CharMap.Entry> iterator() {
               return Byte2CharRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Byte2CharMap.Entry> iterator(Byte2CharMap.Entry var1) {
               return Byte2CharRBTreeMap.this.new EntryIterator(var1.getByteKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Byte) {
                     if (var2.getValue() != null && var2.getValue() instanceof Character) {
                        Byte2CharRBTreeMap.Entry var3 = Byte2CharRBTreeMap.this.findKey((Byte)var2.getKey());
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
                  if (var2.getKey() != null && var2.getKey() instanceof Byte) {
                     if (var2.getValue() != null && var2.getValue() instanceof Character) {
                        Byte2CharRBTreeMap.Entry var3 = Byte2CharRBTreeMap.this.findKey((Byte)var2.getKey());
                        if (var3 != null && var3.getCharValue() == (Character)var2.getValue()) {
                           Byte2CharRBTreeMap.this.remove(var3.key);
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
               return Byte2CharRBTreeMap.this.count;
            }

            public void clear() {
               Byte2CharRBTreeMap.this.clear();
            }

            public Byte2CharMap.Entry first() {
               return Byte2CharRBTreeMap.this.firstEntry;
            }

            public Byte2CharMap.Entry last() {
               return Byte2CharRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Byte2CharMap.Entry> subSet(Byte2CharMap.Entry var1, Byte2CharMap.Entry var2) {
               return Byte2CharRBTreeMap.this.subMap(var1.getByteKey(), var2.getByteKey()).byte2CharEntrySet();
            }

            public ObjectSortedSet<Byte2CharMap.Entry> headSet(Byte2CharMap.Entry var1) {
               return Byte2CharRBTreeMap.this.headMap(var1.getByteKey()).byte2CharEntrySet();
            }

            public ObjectSortedSet<Byte2CharMap.Entry> tailSet(Byte2CharMap.Entry var1) {
               return Byte2CharRBTreeMap.this.tailMap(var1.getByteKey()).byte2CharEntrySet();
            }
         };
      }

      return this.entries;
   }

   public ByteSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2CharRBTreeMap.KeySet();
      }

      return this.keys;
   }

   public CharCollection values() {
      if (this.values == null) {
         this.values = new AbstractCharCollection() {
            public CharIterator iterator() {
               return Byte2CharRBTreeMap.this.new ValueIterator();
            }

            public boolean contains(char var1) {
               return Byte2CharRBTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Byte2CharRBTreeMap.this.count;
            }

            public void clear() {
               Byte2CharRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public ByteComparator comparator() {
      return this.actualComparator;
   }

   public Byte2CharSortedMap headMap(byte var1) {
      return new Byte2CharRBTreeMap.Submap((byte)0, true, var1, false);
   }

   public Byte2CharSortedMap tailMap(byte var1) {
      return new Byte2CharRBTreeMap.Submap(var1, false, (byte)0, true);
   }

   public Byte2CharSortedMap subMap(byte var1, byte var2) {
      return new Byte2CharRBTreeMap.Submap(var1, false, var2, false);
   }

   public Byte2CharRBTreeMap clone() {
      Byte2CharRBTreeMap var1;
      try {
         var1 = (Byte2CharRBTreeMap)super.clone();
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
         Byte2CharRBTreeMap.Entry var5 = new Byte2CharRBTreeMap.Entry();
         Byte2CharRBTreeMap.Entry var6 = new Byte2CharRBTreeMap.Entry();
         Byte2CharRBTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Byte2CharRBTreeMap.Entry var4 = var6;
         var6.pred((Byte2CharRBTreeMap.Entry)null);

         while(true) {
            Byte2CharRBTreeMap.Entry var2;
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
      Byte2CharRBTreeMap.EntryIterator var3 = new Byte2CharRBTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Byte2CharRBTreeMap.Entry var4 = var3.nextEntry();
         var1.writeByte(var4.key);
         var1.writeChar(var4.value);
      }

   }

   private Byte2CharRBTreeMap.Entry readTree(ObjectInputStream var1, int var2, Byte2CharRBTreeMap.Entry var3, Byte2CharRBTreeMap.Entry var4) throws IOException, ClassNotFoundException {
      Byte2CharRBTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Byte2CharRBTreeMap.Entry(var1.readByte(), var1.readChar());
         var8.pred(var3);
         var8.succ(var4);
         var8.black(true);
         return var8;
      } else if (var2 == 2) {
         var8 = new Byte2CharRBTreeMap.Entry(var1.readByte(), var1.readChar());
         var8.black(true);
         var8.right(new Byte2CharRBTreeMap.Entry(var1.readByte(), var1.readChar()));
         var8.right.pred(var8);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Byte2CharRBTreeMap.Entry var7 = new Byte2CharRBTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readByte();
         var7.value = var1.readChar();
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
         this.tree = this.readTree(var1, this.count, (Byte2CharRBTreeMap.Entry)null, (Byte2CharRBTreeMap.Entry)null);

         Byte2CharRBTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractByte2CharSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      byte from;
      byte to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Byte2CharMap.Entry> entries;
      protected transient ByteSortedSet keys;
      protected transient CharCollection values;

      public Submap(byte var2, boolean var3, byte var4, boolean var5) {
         super();
         if (!var3 && !var5 && Byte2CharRBTreeMap.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
            this.defRetValue = Byte2CharRBTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Byte2CharRBTreeMap.Submap.SubmapIterator var1 = new Byte2CharRBTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(byte var1) {
         return (this.bottom || Byte2CharRBTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Byte2CharRBTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Byte2CharMap.Entry> byte2CharEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Byte2CharMap.Entry>() {
               public ObjectBidirectionalIterator<Byte2CharMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Byte2CharMap.Entry> iterator(Byte2CharMap.Entry var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getByteKey());
               }

               public Comparator<? super Byte2CharMap.Entry> comparator() {
                  return Byte2CharRBTreeMap.this.byte2CharEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Byte) {
                        if (var2.getValue() != null && var2.getValue() instanceof Character) {
                           Byte2CharRBTreeMap.Entry var3 = Byte2CharRBTreeMap.this.findKey((Byte)var2.getKey());
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
                     if (var2.getKey() != null && var2.getKey() instanceof Byte) {
                        if (var2.getValue() != null && var2.getValue() instanceof Character) {
                           Byte2CharRBTreeMap.Entry var3 = Byte2CharRBTreeMap.this.findKey((Byte)var2.getKey());
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

               public Byte2CharMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Byte2CharMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Byte2CharMap.Entry> subSet(Byte2CharMap.Entry var1, Byte2CharMap.Entry var2) {
                  return Submap.this.subMap(var1.getByteKey(), var2.getByteKey()).byte2CharEntrySet();
               }

               public ObjectSortedSet<Byte2CharMap.Entry> headSet(Byte2CharMap.Entry var1) {
                  return Submap.this.headMap(var1.getByteKey()).byte2CharEntrySet();
               }

               public ObjectSortedSet<Byte2CharMap.Entry> tailSet(Byte2CharMap.Entry var1) {
                  return Submap.this.tailMap(var1.getByteKey()).byte2CharEntrySet();
               }
            };
         }

         return this.entries;
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Byte2CharRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      public CharCollection values() {
         if (this.values == null) {
            this.values = new AbstractCharCollection() {
               public CharIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               public boolean contains(char var1) {
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

      public boolean containsKey(byte var1) {
         return this.in(var1) && Byte2CharRBTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(char var1) {
         Byte2CharRBTreeMap.Submap.SubmapIterator var2 = new Byte2CharRBTreeMap.Submap.SubmapIterator();

         char var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = var2.nextEntry().value;
         } while(var3 != var1);

         return true;
      }

      public char get(byte var1) {
         Byte2CharRBTreeMap.Entry var2;
         return this.in(var1) && (var2 = Byte2CharRBTreeMap.this.findKey(var1)) != null ? var2.value : this.defRetValue;
      }

      public char put(byte var1, char var2) {
         Byte2CharRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            char var3 = Byte2CharRBTreeMap.this.put(var1, var2);
            return Byte2CharRBTreeMap.this.modified ? this.defRetValue : var3;
         }
      }

      public char remove(byte var1) {
         Byte2CharRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            char var2 = Byte2CharRBTreeMap.this.remove(var1);
            return Byte2CharRBTreeMap.this.modified ? var2 : this.defRetValue;
         }
      }

      public int size() {
         Byte2CharRBTreeMap.Submap.SubmapIterator var1 = new Byte2CharRBTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Byte2CharRBTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public ByteComparator comparator() {
         return Byte2CharRBTreeMap.this.actualComparator;
      }

      public Byte2CharSortedMap headMap(byte var1) {
         if (this.top) {
            return Byte2CharRBTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Byte2CharRBTreeMap.this.compare(var1, this.to) < 0 ? Byte2CharRBTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Byte2CharSortedMap tailMap(byte var1) {
         if (this.bottom) {
            return Byte2CharRBTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Byte2CharRBTreeMap.this.compare(var1, this.from) > 0 ? Byte2CharRBTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Byte2CharSortedMap subMap(byte var1, byte var2) {
         if (this.top && this.bottom) {
            return Byte2CharRBTreeMap.this.new Submap(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = Byte2CharRBTreeMap.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = Byte2CharRBTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : Byte2CharRBTreeMap.this.new Submap(var1, false, var2, false);
         }
      }

      public Byte2CharRBTreeMap.Entry firstEntry() {
         if (Byte2CharRBTreeMap.this.tree == null) {
            return null;
         } else {
            Byte2CharRBTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Byte2CharRBTreeMap.this.firstEntry;
            } else {
               var1 = Byte2CharRBTreeMap.this.locateKey(this.from);
               if (Byte2CharRBTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Byte2CharRBTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Byte2CharRBTreeMap.Entry lastEntry() {
         if (Byte2CharRBTreeMap.this.tree == null) {
            return null;
         } else {
            Byte2CharRBTreeMap.Entry var1;
            if (this.top) {
               var1 = Byte2CharRBTreeMap.this.lastEntry;
            } else {
               var1 = Byte2CharRBTreeMap.this.locateKey(this.to);
               if (Byte2CharRBTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Byte2CharRBTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public byte firstByteKey() {
         Byte2CharRBTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public byte lastByteKey() {
         Byte2CharRBTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Byte2CharRBTreeMap.Submap.SubmapIterator implements CharListIterator {
         private SubmapValueIterator() {
            super();
         }

         public char nextChar() {
            return this.nextEntry().value;
         }

         public char previousChar() {
            return this.previousEntry().value;
         }

         // $FF: synthetic method
         SubmapValueIterator(Object var2) {
            this();
         }
      }

      private final class SubmapKeyIterator extends Byte2CharRBTreeMap.Submap.SubmapIterator implements ByteListIterator {
         public SubmapKeyIterator() {
            super();
         }

         public SubmapKeyIterator(byte var2) {
            super(var2);
         }

         public byte nextByte() {
            return this.nextEntry().key;
         }

         public byte previousByte() {
            return this.previousEntry().key;
         }
      }

      private class SubmapEntryIterator extends Byte2CharRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Byte2CharMap.Entry> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(byte var2) {
            super(var2);
         }

         public Byte2CharMap.Entry next() {
            return this.nextEntry();
         }

         public Byte2CharMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Byte2CharRBTreeMap.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(byte var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Byte2CharRBTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Byte2CharRBTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Byte2CharRBTreeMap.this.locateKey(var2);
                  if (Byte2CharRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Byte2CharRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Byte2CharRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractByte2CharSortedMap.KeySet {
         private KeySet() {
            super();
         }

         public ByteBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         public ByteBidirectionalIterator iterator(byte var1) {
            return Submap.this.new SubmapKeyIterator(var1);
         }

         // $FF: synthetic method
         KeySet(Object var2) {
            this();
         }
      }
   }

   private final class ValueIterator extends Byte2CharRBTreeMap.TreeIterator implements CharListIterator {
      private ValueIterator() {
         super();
      }

      public char nextChar() {
         return this.nextEntry().value;
      }

      public char previousChar() {
         return this.previousEntry().value;
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractByte2CharSortedMap.KeySet {
      private KeySet() {
         super();
      }

      public ByteBidirectionalIterator iterator() {
         return Byte2CharRBTreeMap.this.new KeyIterator();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return Byte2CharRBTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Byte2CharRBTreeMap.TreeIterator implements ByteListIterator {
      public KeyIterator() {
         super();
      }

      public KeyIterator(byte var2) {
         super(var2);
      }

      public byte nextByte() {
         return this.nextEntry().key;
      }

      public byte previousByte() {
         return this.previousEntry().key;
      }
   }

   private class EntryIterator extends Byte2CharRBTreeMap.TreeIterator implements ObjectListIterator<Byte2CharMap.Entry> {
      EntryIterator() {
         super();
      }

      EntryIterator(byte var2) {
         super(var2);
      }

      public Byte2CharMap.Entry next() {
         return this.nextEntry();
      }

      public Byte2CharMap.Entry previous() {
         return this.previousEntry();
      }
   }

   private class TreeIterator {
      Byte2CharRBTreeMap.Entry prev;
      Byte2CharRBTreeMap.Entry next;
      Byte2CharRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Byte2CharRBTreeMap.this.firstEntry;
      }

      TreeIterator(byte var2) {
         super();
         if ((this.next = Byte2CharRBTreeMap.this.locateKey(var2)) != null) {
            if (Byte2CharRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Byte2CharRBTreeMap.Entry nextEntry() {
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

      Byte2CharRBTreeMap.Entry previousEntry() {
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
            Byte2CharRBTreeMap.this.remove(this.curr.key);
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

   private static final class Entry extends AbstractByte2CharMap.BasicEntry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      Byte2CharRBTreeMap.Entry left;
      Byte2CharRBTreeMap.Entry right;
      int info;

      Entry() {
         super((byte)0, '\u0000');
      }

      Entry(byte var1, char var2) {
         super(var1, var2);
         this.info = -1073741824;
      }

      Byte2CharRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Byte2CharRBTreeMap.Entry right() {
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

      void pred(Byte2CharRBTreeMap.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Byte2CharRBTreeMap.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Byte2CharRBTreeMap.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Byte2CharRBTreeMap.Entry var1) {
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

      Byte2CharRBTreeMap.Entry next() {
         Byte2CharRBTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Byte2CharRBTreeMap.Entry prev() {
         Byte2CharRBTreeMap.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public char setValue(char var1) {
         char var2 = this.value;
         this.value = var1;
         return var2;
      }

      public Byte2CharRBTreeMap.Entry clone() {
         Byte2CharRBTreeMap.Entry var1;
         try {
            var1 = (Byte2CharRBTreeMap.Entry)super.clone();
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
            return this.key == (Byte)var2.getKey() && this.value == (Character)var2.getValue();
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
