package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Int2ReferenceAVLTreeMap<V> extends AbstractInt2ReferenceSortedMap<V> implements Serializable, Cloneable {
   protected transient Int2ReferenceAVLTreeMap.Entry<V> tree;
   protected int count;
   protected transient Int2ReferenceAVLTreeMap.Entry<V> firstEntry;
   protected transient Int2ReferenceAVLTreeMap.Entry<V> lastEntry;
   protected transient ObjectSortedSet<Int2ReferenceMap.Entry<V>> entries;
   protected transient IntSortedSet keys;
   protected transient ReferenceCollection<V> values;
   protected transient boolean modified;
   protected Comparator<? super Integer> storedComparator;
   protected transient IntComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Int2ReferenceAVLTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = IntComparators.asIntComparator(this.storedComparator);
   }

   public Int2ReferenceAVLTreeMap(Comparator<? super Integer> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Int2ReferenceAVLTreeMap(Map<? extends Integer, ? extends V> var1) {
      this();
      this.putAll(var1);
   }

   public Int2ReferenceAVLTreeMap(SortedMap<Integer, V> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Int2ReferenceAVLTreeMap(Int2ReferenceMap<? extends V> var1) {
      this();
      this.putAll(var1);
   }

   public Int2ReferenceAVLTreeMap(Int2ReferenceSortedMap<V> var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Int2ReferenceAVLTreeMap(int[] var1, V[] var2, Comparator<? super Integer> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Int2ReferenceAVLTreeMap(int[] var1, V[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(int var1, int var2) {
      return this.actualComparator == null ? Integer.compare(var1, var2) : this.actualComparator.compare(var1, var2);
   }

   final Int2ReferenceAVLTreeMap.Entry<V> findKey(int var1) {
      Int2ReferenceAVLTreeMap.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final Int2ReferenceAVLTreeMap.Entry<V> locateKey(int var1) {
      Int2ReferenceAVLTreeMap.Entry var2 = this.tree;
      Int2ReferenceAVLTreeMap.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public V put(int var1, V var2) {
      Int2ReferenceAVLTreeMap.Entry var3 = this.add(var1);
      Object var4 = var3.value;
      var3.value = var2;
      return var4;
   }

   private Int2ReferenceAVLTreeMap.Entry<V> add(int var1) {
      this.modified = false;
      Int2ReferenceAVLTreeMap.Entry var2 = null;
      if (this.tree == null) {
         ++this.count;
         var2 = this.tree = this.lastEntry = this.firstEntry = new Int2ReferenceAVLTreeMap.Entry(var1, this.defRetValue);
         this.modified = true;
      } else {
         Int2ReferenceAVLTreeMap.Entry var3 = this.tree;
         Int2ReferenceAVLTreeMap.Entry var4 = null;
         Int2ReferenceAVLTreeMap.Entry var5 = this.tree;
         Int2ReferenceAVLTreeMap.Entry var6 = null;
         Int2ReferenceAVLTreeMap.Entry var7 = null;
         int var9 = 0;

         while(true) {
            int var8;
            if ((var8 = this.compare(var1, var3.key)) == 0) {
               return var3;
            }

            if (var3.balance() != 0) {
               var9 = 0;
               var6 = var4;
               var5 = var3;
            }

            if (this.dirPath[var9++] = var8 > 0) {
               if (var3.succ()) {
                  ++this.count;
                  var2 = new Int2ReferenceAVLTreeMap.Entry(var1, this.defRetValue);
                  this.modified = true;
                  if (var3.right == null) {
                     this.lastEntry = var2;
                  }

                  var2.left = var3;
                  var2.right = var3.right;
                  var3.right(var2);
                  break;
               }

               var4 = var3;
               var3 = var3.right;
            } else {
               if (var3.pred()) {
                  ++this.count;
                  var2 = new Int2ReferenceAVLTreeMap.Entry(var1, this.defRetValue);
                  this.modified = true;
                  if (var3.left == null) {
                     this.firstEntry = var2;
                  }

                  var2.right = var3;
                  var2.left = var3.left;
                  var3.left(var2);
                  break;
               }

               var4 = var3;
               var3 = var3.left;
            }
         }

         var3 = var5;

         for(var9 = 0; var3 != var2; var3 = this.dirPath[var9++] ? var3.right : var3.left) {
            if (this.dirPath[var9]) {
               var3.incBalance();
            } else {
               var3.decBalance();
            }
         }

         Int2ReferenceAVLTreeMap.Entry var10;
         if (var5.balance() == -2) {
            var10 = var5.left;
            if (var10.balance() == -1) {
               var7 = var10;
               if (var10.succ()) {
                  var10.succ(false);
                  var5.pred(var10);
               } else {
                  var5.left = var10.right;
               }

               var10.right = var5;
               var10.balance(0);
               var5.balance(0);
            } else {
               assert var10.balance() == 1;

               var7 = var10.right;
               var10.right = var7.left;
               var7.left = var10;
               var5.left = var7.right;
               var7.right = var5;
               if (var7.balance() == -1) {
                  var10.balance(0);
                  var5.balance(1);
               } else if (var7.balance() == 0) {
                  var10.balance(0);
                  var5.balance(0);
               } else {
                  var10.balance(-1);
                  var5.balance(0);
               }

               var7.balance(0);
               if (var7.pred()) {
                  var10.succ(var7);
                  var7.pred(false);
               }

               if (var7.succ()) {
                  var5.pred(var7);
                  var7.succ(false);
               }
            }
         } else {
            if (var5.balance() != 2) {
               return var2;
            }

            var10 = var5.right;
            if (var10.balance() == 1) {
               var7 = var10;
               if (var10.pred()) {
                  var10.pred(false);
                  var5.succ(var10);
               } else {
                  var5.right = var10.left;
               }

               var10.left = var5;
               var10.balance(0);
               var5.balance(0);
            } else {
               assert var10.balance() == -1;

               var7 = var10.left;
               var10.left = var7.right;
               var7.right = var10;
               var5.right = var7.left;
               var7.left = var5;
               if (var7.balance() == 1) {
                  var10.balance(0);
                  var5.balance(-1);
               } else if (var7.balance() == 0) {
                  var10.balance(0);
                  var5.balance(0);
               } else {
                  var10.balance(1);
                  var5.balance(0);
               }

               var7.balance(0);
               if (var7.pred()) {
                  var5.succ(var7);
                  var7.pred(false);
               }

               if (var7.succ()) {
                  var10.pred(var7);
                  var7.succ(false);
               }
            }
         }

         if (var6 == null) {
            this.tree = var7;
         } else if (var6.left == var5) {
            var6.left = var7;
         } else {
            var6.right = var7;
         }
      }

      return var2;
   }

   private Int2ReferenceAVLTreeMap.Entry<V> parent(Int2ReferenceAVLTreeMap.Entry<V> var1) {
      if (var1 == this.tree) {
         return null;
      } else {
         Int2ReferenceAVLTreeMap.Entry var3 = var1;

         Int2ReferenceAVLTreeMap.Entry var2;
         Int2ReferenceAVLTreeMap.Entry var4;
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

   public V remove(int var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Int2ReferenceAVLTreeMap.Entry var3 = this.tree;
         Int2ReferenceAVLTreeMap.Entry var4 = null;
         boolean var5 = false;
         int var6 = var1;

         int var2;
         while((var2 = this.compare(var6, var3.key)) != 0) {
            if (var5 = var2 > 0) {
               var4 = var3;
               if ((var3 = var3.right()) == null) {
                  return this.defRetValue;
               }
            } else {
               var4 = var3;
               if ((var3 = var3.left()) == null) {
                  return this.defRetValue;
               }
            }
         }

         if (var3.left == null) {
            this.firstEntry = var3.next();
         }

         if (var3.right == null) {
            this.lastEntry = var3.prev();
         }

         Int2ReferenceAVLTreeMap.Entry var7;
         Int2ReferenceAVLTreeMap.Entry var8;
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
            Int2ReferenceAVLTreeMap.Entry var9;
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

         this.modified = true;
         --this.count;
         return var3.value;
      }
   }

   public boolean containsValue(Object var1) {
      Int2ReferenceAVLTreeMap.ValueIterator var2 = new Int2ReferenceAVLTreeMap.ValueIterator();
      int var4 = this.count;

      Object var3;
      do {
         if (var4-- == 0) {
            return false;
         }

         var3 = var2.next();
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

   public boolean containsKey(int var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public V get(int var1) {
      Int2ReferenceAVLTreeMap.Entry var2 = this.findKey(var1);
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

   public ObjectSortedSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Int2ReferenceMap.Entry<V>>() {
            final Comparator<? super Int2ReferenceMap.Entry<V>> comparator = (var1x, var2) -> {
               return Int2ReferenceAVLTreeMap.this.actualComparator.compare(var1x.getIntKey(), var2.getIntKey());
            };

            public Comparator<? super Int2ReferenceMap.Entry<V>> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> iterator() {
               return Int2ReferenceAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> iterator(Int2ReferenceMap.Entry<V> var1) {
               return Int2ReferenceAVLTreeMap.this.new EntryIterator(var1.getIntKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Integer) {
                     Int2ReferenceAVLTreeMap.Entry var3 = Int2ReferenceAVLTreeMap.this.findKey((Integer)var2.getKey());
                     return var2.equals(var3);
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
                     Int2ReferenceAVLTreeMap.Entry var3 = Int2ReferenceAVLTreeMap.this.findKey((Integer)var2.getKey());
                     if (var3 != null && var3.getValue() == var2.getValue()) {
                        Int2ReferenceAVLTreeMap.this.remove(var3.key);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }

            public int size() {
               return Int2ReferenceAVLTreeMap.this.count;
            }

            public void clear() {
               Int2ReferenceAVLTreeMap.this.clear();
            }

            public Int2ReferenceMap.Entry<V> first() {
               return Int2ReferenceAVLTreeMap.this.firstEntry;
            }

            public Int2ReferenceMap.Entry<V> last() {
               return Int2ReferenceAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Int2ReferenceMap.Entry<V>> subSet(Int2ReferenceMap.Entry<V> var1, Int2ReferenceMap.Entry<V> var2) {
               return Int2ReferenceAVLTreeMap.this.subMap(var1.getIntKey(), var2.getIntKey()).int2ReferenceEntrySet();
            }

            public ObjectSortedSet<Int2ReferenceMap.Entry<V>> headSet(Int2ReferenceMap.Entry<V> var1) {
               return Int2ReferenceAVLTreeMap.this.headMap(var1.getIntKey()).int2ReferenceEntrySet();
            }

            public ObjectSortedSet<Int2ReferenceMap.Entry<V>> tailSet(Int2ReferenceMap.Entry<V> var1) {
               return Int2ReferenceAVLTreeMap.this.tailMap(var1.getIntKey()).int2ReferenceEntrySet();
            }
         };
      }

      return this.entries;
   }

   public IntSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2ReferenceAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Int2ReferenceAVLTreeMap.this.new ValueIterator();
            }

            public boolean contains(Object var1) {
               return Int2ReferenceAVLTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Int2ReferenceAVLTreeMap.this.count;
            }

            public void clear() {
               Int2ReferenceAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public IntComparator comparator() {
      return this.actualComparator;
   }

   public Int2ReferenceSortedMap<V> headMap(int var1) {
      return new Int2ReferenceAVLTreeMap.Submap(0, true, var1, false);
   }

   public Int2ReferenceSortedMap<V> tailMap(int var1) {
      return new Int2ReferenceAVLTreeMap.Submap(var1, false, 0, true);
   }

   public Int2ReferenceSortedMap<V> subMap(int var1, int var2) {
      return new Int2ReferenceAVLTreeMap.Submap(var1, false, var2, false);
   }

   public Int2ReferenceAVLTreeMap<V> clone() {
      Int2ReferenceAVLTreeMap var1;
      try {
         var1 = (Int2ReferenceAVLTreeMap)super.clone();
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
         Int2ReferenceAVLTreeMap.Entry var5 = new Int2ReferenceAVLTreeMap.Entry();
         Int2ReferenceAVLTreeMap.Entry var6 = new Int2ReferenceAVLTreeMap.Entry();
         Int2ReferenceAVLTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Int2ReferenceAVLTreeMap.Entry var4 = var6;
         var6.pred((Int2ReferenceAVLTreeMap.Entry)null);

         while(true) {
            Int2ReferenceAVLTreeMap.Entry var2;
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
      Int2ReferenceAVLTreeMap.EntryIterator var3 = new Int2ReferenceAVLTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Int2ReferenceAVLTreeMap.Entry var4 = var3.nextEntry();
         var1.writeInt(var4.key);
         var1.writeObject(var4.value);
      }

   }

   private Int2ReferenceAVLTreeMap.Entry<V> readTree(ObjectInputStream var1, int var2, Int2ReferenceAVLTreeMap.Entry<V> var3, Int2ReferenceAVLTreeMap.Entry<V> var4) throws IOException, ClassNotFoundException {
      Int2ReferenceAVLTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Int2ReferenceAVLTreeMap.Entry(var1.readInt(), var1.readObject());
         var8.pred(var3);
         var8.succ(var4);
         return var8;
      } else if (var2 == 2) {
         var8 = new Int2ReferenceAVLTreeMap.Entry(var1.readInt(), var1.readObject());
         var8.right(new Int2ReferenceAVLTreeMap.Entry(var1.readInt(), var1.readObject()));
         var8.right.pred(var8);
         var8.balance(1);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Int2ReferenceAVLTreeMap.Entry var7 = new Int2ReferenceAVLTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readInt();
         var7.value = var1.readObject();
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
         this.tree = this.readTree(var1, this.count, (Int2ReferenceAVLTreeMap.Entry)null, (Int2ReferenceAVLTreeMap.Entry)null);

         Int2ReferenceAVLTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractInt2ReferenceSortedMap<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      int from;
      int to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Int2ReferenceMap.Entry<V>> entries;
      protected transient IntSortedSet keys;
      protected transient ReferenceCollection<V> values;

      public Submap(int var2, boolean var3, int var4, boolean var5) {
         super();
         if (!var3 && !var5 && Int2ReferenceAVLTreeMap.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
            this.defRetValue = Int2ReferenceAVLTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Int2ReferenceAVLTreeMap.Submap.SubmapIterator var1 = new Int2ReferenceAVLTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(int var1) {
         return (this.bottom || Int2ReferenceAVLTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Int2ReferenceAVLTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Int2ReferenceMap.Entry<V>>() {
               public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> iterator(Int2ReferenceMap.Entry<V> var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getIntKey());
               }

               public Comparator<? super Int2ReferenceMap.Entry<V>> comparator() {
                  return Int2ReferenceAVLTreeMap.this.int2ReferenceEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Integer) {
                        Int2ReferenceAVLTreeMap.Entry var3 = Int2ReferenceAVLTreeMap.this.findKey((Integer)var2.getKey());
                        return var3 != null && Submap.this.in(var3.key) && var2.equals(var3);
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
                        Int2ReferenceAVLTreeMap.Entry var3 = Int2ReferenceAVLTreeMap.this.findKey((Integer)var2.getKey());
                        if (var3 != null && Submap.this.in(var3.key)) {
                           Submap.this.remove(var3.key);
                        }

                        return var3 != null;
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

               public Int2ReferenceMap.Entry<V> first() {
                  return Submap.this.firstEntry();
               }

               public Int2ReferenceMap.Entry<V> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Int2ReferenceMap.Entry<V>> subSet(Int2ReferenceMap.Entry<V> var1, Int2ReferenceMap.Entry<V> var2) {
                  return Submap.this.subMap(var1.getIntKey(), var2.getIntKey()).int2ReferenceEntrySet();
               }

               public ObjectSortedSet<Int2ReferenceMap.Entry<V>> headSet(Int2ReferenceMap.Entry<V> var1) {
                  return Submap.this.headMap(var1.getIntKey()).int2ReferenceEntrySet();
               }

               public ObjectSortedSet<Int2ReferenceMap.Entry<V>> tailSet(Int2ReferenceMap.Entry<V> var1) {
                  return Submap.this.tailMap(var1.getIntKey()).int2ReferenceEntrySet();
               }
            };
         }

         return this.entries;
      }

      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Int2ReferenceAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      public ReferenceCollection<V> values() {
         if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>() {
               public ObjectIterator<V> iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               public boolean contains(Object var1) {
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
         return this.in(var1) && Int2ReferenceAVLTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(Object var1) {
         Int2ReferenceAVLTreeMap.Submap.SubmapIterator var2 = new Int2ReferenceAVLTreeMap.Submap.SubmapIterator();

         Object var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = var2.nextEntry().value;
         } while(var3 != var1);

         return true;
      }

      public V get(int var1) {
         Int2ReferenceAVLTreeMap.Entry var2;
         return this.in(var1) && (var2 = Int2ReferenceAVLTreeMap.this.findKey(var1)) != null ? var2.value : this.defRetValue;
      }

      public V put(int var1, V var2) {
         Int2ReferenceAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            Object var3 = Int2ReferenceAVLTreeMap.this.put(var1, var2);
            return Int2ReferenceAVLTreeMap.this.modified ? this.defRetValue : var3;
         }
      }

      public V remove(int var1) {
         Int2ReferenceAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            Object var2 = Int2ReferenceAVLTreeMap.this.remove(var1);
            return Int2ReferenceAVLTreeMap.this.modified ? var2 : this.defRetValue;
         }
      }

      public int size() {
         Int2ReferenceAVLTreeMap.Submap.SubmapIterator var1 = new Int2ReferenceAVLTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Int2ReferenceAVLTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public IntComparator comparator() {
         return Int2ReferenceAVLTreeMap.this.actualComparator;
      }

      public Int2ReferenceSortedMap<V> headMap(int var1) {
         if (this.top) {
            return Int2ReferenceAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Int2ReferenceAVLTreeMap.this.compare(var1, this.to) < 0 ? Int2ReferenceAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Int2ReferenceSortedMap<V> tailMap(int var1) {
         if (this.bottom) {
            return Int2ReferenceAVLTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Int2ReferenceAVLTreeMap.this.compare(var1, this.from) > 0 ? Int2ReferenceAVLTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Int2ReferenceSortedMap<V> subMap(int var1, int var2) {
         if (this.top && this.bottom) {
            return Int2ReferenceAVLTreeMap.this.new Submap(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = Int2ReferenceAVLTreeMap.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = Int2ReferenceAVLTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : Int2ReferenceAVLTreeMap.this.new Submap(var1, false, var2, false);
         }
      }

      public Int2ReferenceAVLTreeMap.Entry<V> firstEntry() {
         if (Int2ReferenceAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Int2ReferenceAVLTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Int2ReferenceAVLTreeMap.this.firstEntry;
            } else {
               var1 = Int2ReferenceAVLTreeMap.this.locateKey(this.from);
               if (Int2ReferenceAVLTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Int2ReferenceAVLTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Int2ReferenceAVLTreeMap.Entry<V> lastEntry() {
         if (Int2ReferenceAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Int2ReferenceAVLTreeMap.Entry var1;
            if (this.top) {
               var1 = Int2ReferenceAVLTreeMap.this.lastEntry;
            } else {
               var1 = Int2ReferenceAVLTreeMap.this.locateKey(this.to);
               if (Int2ReferenceAVLTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Int2ReferenceAVLTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public int firstIntKey() {
         Int2ReferenceAVLTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public int lastIntKey() {
         Int2ReferenceAVLTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Int2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<V> {
         private SubmapValueIterator() {
            super();
         }

         public V next() {
            return this.nextEntry().value;
         }

         public V previous() {
            return this.previousEntry().value;
         }

         // $FF: synthetic method
         SubmapValueIterator(Object var2) {
            this();
         }
      }

      private final class SubmapKeyIterator extends Int2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements IntListIterator {
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

      private class SubmapEntryIterator extends Int2ReferenceAVLTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<Int2ReferenceMap.Entry<V>> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(int var2) {
            super(var2);
         }

         public Int2ReferenceMap.Entry<V> next() {
            return this.nextEntry();
         }

         public Int2ReferenceMap.Entry<V> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Int2ReferenceAVLTreeMap<V>.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(int var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Int2ReferenceAVLTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Int2ReferenceAVLTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Int2ReferenceAVLTreeMap.this.locateKey(var2);
                  if (Int2ReferenceAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Int2ReferenceAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Int2ReferenceAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractInt2ReferenceSortedMap<V>.KeySet {
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

   private final class ValueIterator extends Int2ReferenceAVLTreeMap<V>.TreeIterator implements ObjectListIterator<V> {
      private ValueIterator() {
         super();
      }

      public V next() {
         return this.nextEntry().value;
      }

      public V previous() {
         return this.previousEntry().value;
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractInt2ReferenceSortedMap<V>.KeySet {
      private KeySet() {
         super();
      }

      public IntBidirectionalIterator iterator() {
         return Int2ReferenceAVLTreeMap.this.new KeyIterator();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return Int2ReferenceAVLTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Int2ReferenceAVLTreeMap<V>.TreeIterator implements IntListIterator {
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

   private class EntryIterator extends Int2ReferenceAVLTreeMap<V>.TreeIterator implements ObjectListIterator<Int2ReferenceMap.Entry<V>> {
      EntryIterator() {
         super();
      }

      EntryIterator(int var2) {
         super(var2);
      }

      public Int2ReferenceMap.Entry<V> next() {
         return this.nextEntry();
      }

      public Int2ReferenceMap.Entry<V> previous() {
         return this.previousEntry();
      }

      public void set(Int2ReferenceMap.Entry<V> var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Int2ReferenceMap.Entry<V> var1) {
         throw new UnsupportedOperationException();
      }
   }

   private class TreeIterator {
      Int2ReferenceAVLTreeMap.Entry<V> prev;
      Int2ReferenceAVLTreeMap.Entry<V> next;
      Int2ReferenceAVLTreeMap.Entry<V> curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Int2ReferenceAVLTreeMap.this.firstEntry;
      }

      TreeIterator(int var2) {
         super();
         if ((this.next = Int2ReferenceAVLTreeMap.this.locateKey(var2)) != null) {
            if (Int2ReferenceAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Int2ReferenceAVLTreeMap.Entry<V> nextEntry() {
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

      Int2ReferenceAVLTreeMap.Entry<V> previousEntry() {
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
            Int2ReferenceAVLTreeMap.this.remove(this.curr.key);
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

   private static final class Entry<V> extends AbstractInt2ReferenceMap.BasicEntry<V> implements Cloneable {
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Int2ReferenceAVLTreeMap.Entry<V> left;
      Int2ReferenceAVLTreeMap.Entry<V> right;
      int info;

      Entry() {
         super(0, (Object)null);
      }

      Entry(int var1, V var2) {
         super(var1, var2);
         this.info = -1073741824;
      }

      Int2ReferenceAVLTreeMap.Entry<V> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Int2ReferenceAVLTreeMap.Entry<V> right() {
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

      void pred(Int2ReferenceAVLTreeMap.Entry<V> var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Int2ReferenceAVLTreeMap.Entry<V> var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Int2ReferenceAVLTreeMap.Entry<V> var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Int2ReferenceAVLTreeMap.Entry<V> var1) {
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

      Int2ReferenceAVLTreeMap.Entry<V> next() {
         Int2ReferenceAVLTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Int2ReferenceAVLTreeMap.Entry<V> prev() {
         Int2ReferenceAVLTreeMap.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public V setValue(V var1) {
         Object var2 = this.value;
         this.value = var1;
         return var2;
      }

      public Int2ReferenceAVLTreeMap.Entry<V> clone() {
         Int2ReferenceAVLTreeMap.Entry var1;
         try {
            var1 = (Int2ReferenceAVLTreeMap.Entry)super.clone();
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
            return this.key == (Integer)var2.getKey() && this.value == var2.getValue();
         }
      }

      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      public String toString() {
         return this.key + "=>" + this.value;
      }
   }
}
