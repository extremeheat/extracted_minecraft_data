package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
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

public class Int2LongRBTreeMap extends AbstractInt2LongSortedMap implements Serializable, Cloneable {
   protected transient Int2LongRBTreeMap.Entry tree;
   protected int count;
   protected transient Int2LongRBTreeMap.Entry firstEntry;
   protected transient Int2LongRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Int2LongMap.Entry> entries;
   protected transient IntSortedSet keys;
   protected transient LongCollection values;
   protected transient boolean modified;
   protected Comparator<? super Integer> storedComparator;
   protected transient IntComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Int2LongRBTreeMap.Entry[] nodePath;

   public Int2LongRBTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = IntComparators.asIntComparator(this.storedComparator);
   }

   public Int2LongRBTreeMap(Comparator<? super Integer> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Int2LongRBTreeMap(Map<? extends Integer, ? extends Long> var1) {
      this();
      this.putAll(var1);
   }

   public Int2LongRBTreeMap(SortedMap<Integer, Long> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Int2LongRBTreeMap(Int2LongMap var1) {
      this();
      this.putAll(var1);
   }

   public Int2LongRBTreeMap(Int2LongSortedMap var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Int2LongRBTreeMap(int[] var1, long[] var2, Comparator<? super Integer> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Int2LongRBTreeMap(int[] var1, long[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(int var1, int var2) {
      return this.actualComparator == null ? Integer.compare(var1, var2) : this.actualComparator.compare(var1, var2);
   }

   final Int2LongRBTreeMap.Entry findKey(int var1) {
      Int2LongRBTreeMap.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final Int2LongRBTreeMap.Entry locateKey(int var1) {
      Int2LongRBTreeMap.Entry var2 = this.tree;
      Int2LongRBTreeMap.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Int2LongRBTreeMap.Entry[64];
   }

   public long addTo(int var1, long var2) {
      Int2LongRBTreeMap.Entry var4 = this.add(var1);
      long var5 = var4.value;
      var4.value += var2;
      return var5;
   }

   public long put(int var1, long var2) {
      Int2LongRBTreeMap.Entry var4 = this.add(var1);
      long var5 = var4.value;
      var4.value = var2;
      return var5;
   }

   private Int2LongRBTreeMap.Entry add(int var1) {
      this.modified = false;
      int var2 = 0;
      Int2LongRBTreeMap.Entry var3;
      if (this.tree == null) {
         ++this.count;
         var3 = this.tree = this.lastEntry = this.firstEntry = new Int2LongRBTreeMap.Entry(var1, this.defRetValue);
      } else {
         Int2LongRBTreeMap.Entry var4 = this.tree;
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
               var3 = new Int2LongRBTreeMap.Entry(var1, this.defRetValue);
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
               var3 = new Int2LongRBTreeMap.Entry(var1, this.defRetValue);
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

               Int2LongRBTreeMap.Entry var7;
               Int2LongRBTreeMap.Entry var8;
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

   public long remove(int var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Int2LongRBTreeMap.Entry var2 = this.tree;
         int var4 = 0;
         int var5 = var1;

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

         Int2LongRBTreeMap.Entry var7;
         Int2LongRBTreeMap.Entry var8;
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

   public boolean containsValue(long var1) {
      Int2LongRBTreeMap.ValueIterator var3 = new Int2LongRBTreeMap.ValueIterator();
      int var6 = this.count;

      long var4;
      do {
         if (var6-- == 0) {
            return false;
         }

         var4 = var3.nextLong();
      } while(var4 != var1);

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

   public boolean containsKey(int var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public long get(int var1) {
      Int2LongRBTreeMap.Entry var2 = this.findKey(var1);
      return var2 == null ? this.defRetValue : var2.value;
   }

   public int firstIntKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public int lastIntKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Int2LongMap.Entry>() {
            final Comparator<? super Int2LongMap.Entry> comparator = (var1x, var2) -> {
               return Int2LongRBTreeMap.this.actualComparator.compare(var1x.getIntKey(), var2.getIntKey());
            };

            public Comparator<? super Int2LongMap.Entry> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Int2LongMap.Entry> iterator() {
               return Int2LongRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Int2LongMap.Entry> iterator(Int2LongMap.Entry var1) {
               return Int2LongRBTreeMap.this.new EntryIterator(var1.getIntKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Integer) {
                     if (var2.getValue() != null && var2.getValue() instanceof Long) {
                        Int2LongRBTreeMap.Entry var3 = Int2LongRBTreeMap.this.findKey((Integer)var2.getKey());
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
                  if (var2.getKey() != null && var2.getKey() instanceof Integer) {
                     if (var2.getValue() != null && var2.getValue() instanceof Long) {
                        Int2LongRBTreeMap.Entry var3 = Int2LongRBTreeMap.this.findKey((Integer)var2.getKey());
                        if (var3 != null && var3.getLongValue() == (Long)var2.getValue()) {
                           Int2LongRBTreeMap.this.remove(var3.key);
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
               return Int2LongRBTreeMap.this.count;
            }

            public void clear() {
               Int2LongRBTreeMap.this.clear();
            }

            public Int2LongMap.Entry first() {
               return Int2LongRBTreeMap.this.firstEntry;
            }

            public Int2LongMap.Entry last() {
               return Int2LongRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Int2LongMap.Entry> subSet(Int2LongMap.Entry var1, Int2LongMap.Entry var2) {
               return Int2LongRBTreeMap.this.subMap(var1.getIntKey(), var2.getIntKey()).int2LongEntrySet();
            }

            public ObjectSortedSet<Int2LongMap.Entry> headSet(Int2LongMap.Entry var1) {
               return Int2LongRBTreeMap.this.headMap(var1.getIntKey()).int2LongEntrySet();
            }

            public ObjectSortedSet<Int2LongMap.Entry> tailSet(Int2LongMap.Entry var1) {
               return Int2LongRBTreeMap.this.tailMap(var1.getIntKey()).int2LongEntrySet();
            }
         };
      }

      return this.entries;
   }

   public IntSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2LongRBTreeMap.KeySet();
      }

      return this.keys;
   }

   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            public LongIterator iterator() {
               return Int2LongRBTreeMap.this.new ValueIterator();
            }

            public boolean contains(long var1) {
               return Int2LongRBTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Int2LongRBTreeMap.this.count;
            }

            public void clear() {
               Int2LongRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public IntComparator comparator() {
      return this.actualComparator;
   }

   public Int2LongSortedMap headMap(int var1) {
      return new Int2LongRBTreeMap.Submap(0, true, var1, false);
   }

   public Int2LongSortedMap tailMap(int var1) {
      return new Int2LongRBTreeMap.Submap(var1, false, 0, true);
   }

   public Int2LongSortedMap subMap(int var1, int var2) {
      return new Int2LongRBTreeMap.Submap(var1, false, var2, false);
   }

   public Int2LongRBTreeMap clone() {
      Int2LongRBTreeMap var1;
      try {
         var1 = (Int2LongRBTreeMap)super.clone();
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
         Int2LongRBTreeMap.Entry var5 = new Int2LongRBTreeMap.Entry();
         Int2LongRBTreeMap.Entry var6 = new Int2LongRBTreeMap.Entry();
         Int2LongRBTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Int2LongRBTreeMap.Entry var4 = var6;
         var6.pred((Int2LongRBTreeMap.Entry)null);

         while(true) {
            Int2LongRBTreeMap.Entry var2;
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
      Int2LongRBTreeMap.EntryIterator var3 = new Int2LongRBTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Int2LongRBTreeMap.Entry var4 = var3.nextEntry();
         var1.writeInt(var4.key);
         var1.writeLong(var4.value);
      }

   }

   private Int2LongRBTreeMap.Entry readTree(ObjectInputStream var1, int var2, Int2LongRBTreeMap.Entry var3, Int2LongRBTreeMap.Entry var4) throws IOException, ClassNotFoundException {
      Int2LongRBTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Int2LongRBTreeMap.Entry(var1.readInt(), var1.readLong());
         var8.pred(var3);
         var8.succ(var4);
         var8.black(true);
         return var8;
      } else if (var2 == 2) {
         var8 = new Int2LongRBTreeMap.Entry(var1.readInt(), var1.readLong());
         var8.black(true);
         var8.right(new Int2LongRBTreeMap.Entry(var1.readInt(), var1.readLong()));
         var8.right.pred(var8);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Int2LongRBTreeMap.Entry var7 = new Int2LongRBTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readInt();
         var7.value = var1.readLong();
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
         this.tree = this.readTree(var1, this.count, (Int2LongRBTreeMap.Entry)null, (Int2LongRBTreeMap.Entry)null);

         Int2LongRBTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractInt2LongSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      int from;
      int to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Int2LongMap.Entry> entries;
      protected transient IntSortedSet keys;
      protected transient LongCollection values;

      public Submap(int var2, boolean var3, int var4, boolean var5) {
         super();
         if (!var3 && !var5 && Int2LongRBTreeMap.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
            this.defRetValue = Int2LongRBTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Int2LongRBTreeMap.Submap.SubmapIterator var1 = new Int2LongRBTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(int var1) {
         return (this.bottom || Int2LongRBTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Int2LongRBTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Int2LongMap.Entry>() {
               public ObjectBidirectionalIterator<Int2LongMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Int2LongMap.Entry> iterator(Int2LongMap.Entry var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getIntKey());
               }

               public Comparator<? super Int2LongMap.Entry> comparator() {
                  return Int2LongRBTreeMap.this.int2LongEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Integer) {
                        if (var2.getValue() != null && var2.getValue() instanceof Long) {
                           Int2LongRBTreeMap.Entry var3 = Int2LongRBTreeMap.this.findKey((Integer)var2.getKey());
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
                     if (var2.getKey() != null && var2.getKey() instanceof Integer) {
                        if (var2.getValue() != null && var2.getValue() instanceof Long) {
                           Int2LongRBTreeMap.Entry var3 = Int2LongRBTreeMap.this.findKey((Integer)var2.getKey());
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

               public Int2LongMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Int2LongMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Int2LongMap.Entry> subSet(Int2LongMap.Entry var1, Int2LongMap.Entry var2) {
                  return Submap.this.subMap(var1.getIntKey(), var2.getIntKey()).int2LongEntrySet();
               }

               public ObjectSortedSet<Int2LongMap.Entry> headSet(Int2LongMap.Entry var1) {
                  return Submap.this.headMap(var1.getIntKey()).int2LongEntrySet();
               }

               public ObjectSortedSet<Int2LongMap.Entry> tailSet(Int2LongMap.Entry var1) {
                  return Submap.this.tailMap(var1.getIntKey()).int2LongEntrySet();
               }
            };
         }

         return this.entries;
      }

      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Int2LongRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      public LongCollection values() {
         if (this.values == null) {
            this.values = new AbstractLongCollection() {
               public LongIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               public boolean contains(long var1) {
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

      public boolean containsKey(int var1) {
         return this.in(var1) && Int2LongRBTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(long var1) {
         Int2LongRBTreeMap.Submap.SubmapIterator var3 = new Int2LongRBTreeMap.Submap.SubmapIterator();

         long var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = var3.nextEntry().value;
         } while(var4 != var1);

         return true;
      }

      public long get(int var1) {
         Int2LongRBTreeMap.Entry var2;
         return this.in(var1) && (var2 = Int2LongRBTreeMap.this.findKey(var1)) != null ? var2.value : this.defRetValue;
      }

      public long put(int var1, long var2) {
         Int2LongRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            long var4 = Int2LongRBTreeMap.this.put(var1, var2);
            return Int2LongRBTreeMap.this.modified ? this.defRetValue : var4;
         }
      }

      public long remove(int var1) {
         Int2LongRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            long var2 = Int2LongRBTreeMap.this.remove(var1);
            return Int2LongRBTreeMap.this.modified ? var2 : this.defRetValue;
         }
      }

      public int size() {
         Int2LongRBTreeMap.Submap.SubmapIterator var1 = new Int2LongRBTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Int2LongRBTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public IntComparator comparator() {
         return Int2LongRBTreeMap.this.actualComparator;
      }

      public Int2LongSortedMap headMap(int var1) {
         if (this.top) {
            return Int2LongRBTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Int2LongRBTreeMap.this.compare(var1, this.to) < 0 ? Int2LongRBTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Int2LongSortedMap tailMap(int var1) {
         if (this.bottom) {
            return Int2LongRBTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Int2LongRBTreeMap.this.compare(var1, this.from) > 0 ? Int2LongRBTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Int2LongSortedMap subMap(int var1, int var2) {
         if (this.top && this.bottom) {
            return Int2LongRBTreeMap.this.new Submap(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = Int2LongRBTreeMap.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = Int2LongRBTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : Int2LongRBTreeMap.this.new Submap(var1, false, var2, false);
         }
      }

      public Int2LongRBTreeMap.Entry firstEntry() {
         if (Int2LongRBTreeMap.this.tree == null) {
            return null;
         } else {
            Int2LongRBTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Int2LongRBTreeMap.this.firstEntry;
            } else {
               var1 = Int2LongRBTreeMap.this.locateKey(this.from);
               if (Int2LongRBTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Int2LongRBTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Int2LongRBTreeMap.Entry lastEntry() {
         if (Int2LongRBTreeMap.this.tree == null) {
            return null;
         } else {
            Int2LongRBTreeMap.Entry var1;
            if (this.top) {
               var1 = Int2LongRBTreeMap.this.lastEntry;
            } else {
               var1 = Int2LongRBTreeMap.this.locateKey(this.to);
               if (Int2LongRBTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Int2LongRBTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public int firstIntKey() {
         Int2LongRBTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public int lastIntKey() {
         Int2LongRBTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Int2LongRBTreeMap.Submap.SubmapIterator implements LongListIterator {
         private SubmapValueIterator() {
            super();
         }

         public long nextLong() {
            return this.nextEntry().value;
         }

         public long previousLong() {
            return this.previousEntry().value;
         }

         // $FF: synthetic method
         SubmapValueIterator(Object var2) {
            this();
         }
      }

      private final class SubmapKeyIterator extends Int2LongRBTreeMap.Submap.SubmapIterator implements IntListIterator {
         public SubmapKeyIterator() {
            super();
         }

         public SubmapKeyIterator(int var2) {
            super(var2);
         }

         public int nextInt() {
            return this.nextEntry().key;
         }

         public int previousInt() {
            return this.previousEntry().key;
         }
      }

      private class SubmapEntryIterator extends Int2LongRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Int2LongMap.Entry> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(int var2) {
            super(var2);
         }

         public Int2LongMap.Entry next() {
            return this.nextEntry();
         }

         public Int2LongMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Int2LongRBTreeMap.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(int var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Int2LongRBTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Int2LongRBTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Int2LongRBTreeMap.this.locateKey(var2);
                  if (Int2LongRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Int2LongRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Int2LongRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractInt2LongSortedMap.KeySet {
         private KeySet() {
            super();
         }

         public IntBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         public IntBidirectionalIterator iterator(int var1) {
            return Submap.this.new SubmapKeyIterator(var1);
         }

         // $FF: synthetic method
         KeySet(Object var2) {
            this();
         }
      }
   }

   private final class ValueIterator extends Int2LongRBTreeMap.TreeIterator implements LongListIterator {
      private ValueIterator() {
         super();
      }

      public long nextLong() {
         return this.nextEntry().value;
      }

      public long previousLong() {
         return this.previousEntry().value;
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractInt2LongSortedMap.KeySet {
      private KeySet() {
         super();
      }

      public IntBidirectionalIterator iterator() {
         return Int2LongRBTreeMap.this.new KeyIterator();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return Int2LongRBTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Int2LongRBTreeMap.TreeIterator implements IntListIterator {
      public KeyIterator() {
         super();
      }

      public KeyIterator(int var2) {
         super(var2);
      }

      public int nextInt() {
         return this.nextEntry().key;
      }

      public int previousInt() {
         return this.previousEntry().key;
      }
   }

   private class EntryIterator extends Int2LongRBTreeMap.TreeIterator implements ObjectListIterator<Int2LongMap.Entry> {
      EntryIterator() {
         super();
      }

      EntryIterator(int var2) {
         super(var2);
      }

      public Int2LongMap.Entry next() {
         return this.nextEntry();
      }

      public Int2LongMap.Entry previous() {
         return this.previousEntry();
      }
   }

   private class TreeIterator {
      Int2LongRBTreeMap.Entry prev;
      Int2LongRBTreeMap.Entry next;
      Int2LongRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Int2LongRBTreeMap.this.firstEntry;
      }

      TreeIterator(int var2) {
         super();
         if ((this.next = Int2LongRBTreeMap.this.locateKey(var2)) != null) {
            if (Int2LongRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Int2LongRBTreeMap.Entry nextEntry() {
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

      Int2LongRBTreeMap.Entry previousEntry() {
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
            Int2LongRBTreeMap.this.remove(this.curr.key);
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

   private static final class Entry extends AbstractInt2LongMap.BasicEntry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      Int2LongRBTreeMap.Entry left;
      Int2LongRBTreeMap.Entry right;
      int info;

      Entry() {
         super(0, 0L);
      }

      Entry(int var1, long var2) {
         super(var1, var2);
         this.info = -1073741824;
      }

      Int2LongRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Int2LongRBTreeMap.Entry right() {
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

      void pred(Int2LongRBTreeMap.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Int2LongRBTreeMap.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Int2LongRBTreeMap.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Int2LongRBTreeMap.Entry var1) {
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

      Int2LongRBTreeMap.Entry next() {
         Int2LongRBTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Int2LongRBTreeMap.Entry prev() {
         Int2LongRBTreeMap.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public long setValue(long var1) {
         long var3 = this.value;
         this.value = var1;
         return var3;
      }

      public Int2LongRBTreeMap.Entry clone() {
         Int2LongRBTreeMap.Entry var1;
         try {
            var1 = (Int2LongRBTreeMap.Entry)super.clone();
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
            return this.key == (Integer)var2.getKey() && this.value == (Long)var2.getValue();
         }
      }

      public int hashCode() {
         return this.key ^ HashCommon.long2int(this.value);
      }

      public String toString() {
         return this.key + "=>" + this.value;
      }
   }
}
