package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;

public class Double2ObjectRBTreeMap<V> extends AbstractDouble2ObjectSortedMap<V> implements Serializable, Cloneable {
   protected transient Double2ObjectRBTreeMap.Entry<V> tree;
   protected int count;
   protected transient Double2ObjectRBTreeMap.Entry<V> firstEntry;
   protected transient Double2ObjectRBTreeMap.Entry<V> lastEntry;
   protected transient ObjectSortedSet<Double2ObjectMap.Entry<V>> entries;
   protected transient DoubleSortedSet keys;
   protected transient ObjectCollection<V> values;
   protected transient boolean modified;
   protected Comparator<? super Double> storedComparator;
   protected transient DoubleComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Double2ObjectRBTreeMap.Entry<V>[] nodePath;

   public Double2ObjectRBTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = DoubleComparators.asDoubleComparator(this.storedComparator);
   }

   public Double2ObjectRBTreeMap(Comparator<? super Double> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Double2ObjectRBTreeMap(Map<? extends Double, ? extends V> var1) {
      this();
      this.putAll(var1);
   }

   public Double2ObjectRBTreeMap(SortedMap<Double, V> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Double2ObjectRBTreeMap(Double2ObjectMap<? extends V> var1) {
      this();
      this.putAll(var1);
   }

   public Double2ObjectRBTreeMap(Double2ObjectSortedMap<V> var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Double2ObjectRBTreeMap(double[] var1, V[] var2, Comparator<? super Double> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Double2ObjectRBTreeMap(double[] var1, V[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(double var1, double var3) {
      return this.actualComparator == null ? Double.compare(var1, var3) : this.actualComparator.compare(var1, var3);
   }

   final Double2ObjectRBTreeMap.Entry<V> findKey(double var1) {
      Double2ObjectRBTreeMap.Entry var3;
      int var4;
      for(var3 = this.tree; var3 != null && (var4 = this.compare(var1, var3.key)) != 0; var3 = var4 < 0 ? var3.left() : var3.right()) {
      }

      return var3;
   }

   final Double2ObjectRBTreeMap.Entry<V> locateKey(double var1) {
      Double2ObjectRBTreeMap.Entry var3 = this.tree;
      Double2ObjectRBTreeMap.Entry var4 = this.tree;

      int var5;
      for(var5 = 0; var3 != null && (var5 = this.compare(var1, var3.key)) != 0; var3 = var5 < 0 ? var3.left() : var3.right()) {
         var4 = var3;
      }

      return var5 == 0 ? var3 : var4;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Double2ObjectRBTreeMap.Entry[64];
   }

   public V put(double var1, V var3) {
      Double2ObjectRBTreeMap.Entry var4 = this.add(var1);
      Object var5 = var4.value;
      var4.value = var3;
      return var5;
   }

   private Double2ObjectRBTreeMap.Entry<V> add(double var1) {
      this.modified = false;
      int var3 = 0;
      Double2ObjectRBTreeMap.Entry var4;
      if (this.tree == null) {
         ++this.count;
         var4 = this.tree = this.lastEntry = this.firstEntry = new Double2ObjectRBTreeMap.Entry(var1, this.defRetValue);
      } else {
         Double2ObjectRBTreeMap.Entry var5 = this.tree;
         int var7 = 0;

         label123:
         while(true) {
            int var6;
            if ((var6 = this.compare(var1, var5.key)) == 0) {
               while(var7-- != 0) {
                  this.nodePath[var7] = null;
               }

               return var5;
            }

            this.nodePath[var7] = var5;
            if (this.dirPath[var7++] = var6 > 0) {
               if (!var5.succ()) {
                  var5 = var5.right;
                  continue;
               }

               ++this.count;
               var4 = new Double2ObjectRBTreeMap.Entry(var1, this.defRetValue);
               if (var5.right == null) {
                  this.lastEntry = var4;
               }

               var4.left = var5;
               var4.right = var5.right;
               var5.right(var4);
            } else {
               if (!var5.pred()) {
                  var5 = var5.left;
                  continue;
               }

               ++this.count;
               var4 = new Double2ObjectRBTreeMap.Entry(var1, this.defRetValue);
               if (var5.left == null) {
                  this.firstEntry = var4;
               }

               var4.right = var5;
               var4.left = var5.left;
               var5.left(var4);
            }

            this.modified = true;
            var3 = var7--;

            while(true) {
               if (var7 <= 0 || this.nodePath[var7].black()) {
                  break label123;
               }

               Double2ObjectRBTreeMap.Entry var8;
               Double2ObjectRBTreeMap.Entry var9;
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

      return var4;
   }

   public V remove(double var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Double2ObjectRBTreeMap.Entry var3 = this.tree;
         int var5 = 0;
         double var6 = var1;

         int var4;
         while((var4 = this.compare(var6, var3.key)) != 0) {
            this.dirPath[var5] = var4 > 0;
            this.nodePath[var5] = var3;
            if (this.dirPath[var5++]) {
               if ((var3 = var3.right()) == null) {
                  while(var5-- != 0) {
                     this.nodePath[var5] = null;
                  }

                  return this.defRetValue;
               }
            } else if ((var3 = var3.left()) == null) {
               while(var5-- != 0) {
                  this.nodePath[var5] = null;
               }

               return this.defRetValue;
            }
         }

         if (var3.left == null) {
            this.firstEntry = var3.next();
         }

         if (var3.right == null) {
            this.lastEntry = var3.prev();
         }

         Double2ObjectRBTreeMap.Entry var9;
         Double2ObjectRBTreeMap.Entry var10;
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

         this.modified = true;
         --this.count;

         while(var12-- != 0) {
            this.nodePath[var12] = null;
         }

         return var3.value;
      }
   }

   public boolean containsValue(Object var1) {
      Double2ObjectRBTreeMap.ValueIterator var2 = new Double2ObjectRBTreeMap.ValueIterator();
      int var4 = this.count;

      Object var3;
      do {
         if (var4-- == 0) {
            return false;
         }

         var3 = var2.next();
      } while(!Objects.equals(var3, var1));

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

   public boolean containsKey(double var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public V get(double var1) {
      Double2ObjectRBTreeMap.Entry var3 = this.findKey(var1);
      return var3 == null ? this.defRetValue : var3.value;
   }

   public double firstDoubleKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public double lastDoubleKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Double2ObjectMap.Entry<V>>() {
            final Comparator<? super Double2ObjectMap.Entry<V>> comparator = (var1x, var2) -> {
               return Double2ObjectRBTreeMap.this.actualComparator.compare(var1x.getDoubleKey(), var2.getDoubleKey());
            };

            public Comparator<? super Double2ObjectMap.Entry<V>> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> iterator() {
               return Double2ObjectRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> iterator(Double2ObjectMap.Entry<V> var1) {
               return Double2ObjectRBTreeMap.this.new EntryIterator(var1.getDoubleKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Double) {
                     Double2ObjectRBTreeMap.Entry var3 = Double2ObjectRBTreeMap.this.findKey((Double)var2.getKey());
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
                  if (var2.getKey() != null && var2.getKey() instanceof Double) {
                     Double2ObjectRBTreeMap.Entry var3 = Double2ObjectRBTreeMap.this.findKey((Double)var2.getKey());
                     if (var3 != null && Objects.equals(var3.getValue(), var2.getValue())) {
                        Double2ObjectRBTreeMap.this.remove(var3.key);
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
               return Double2ObjectRBTreeMap.this.count;
            }

            public void clear() {
               Double2ObjectRBTreeMap.this.clear();
            }

            public Double2ObjectMap.Entry<V> first() {
               return Double2ObjectRBTreeMap.this.firstEntry;
            }

            public Double2ObjectMap.Entry<V> last() {
               return Double2ObjectRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Double2ObjectMap.Entry<V>> subSet(Double2ObjectMap.Entry<V> var1, Double2ObjectMap.Entry<V> var2) {
               return Double2ObjectRBTreeMap.this.subMap(var1.getDoubleKey(), var2.getDoubleKey()).double2ObjectEntrySet();
            }

            public ObjectSortedSet<Double2ObjectMap.Entry<V>> headSet(Double2ObjectMap.Entry<V> var1) {
               return Double2ObjectRBTreeMap.this.headMap(var1.getDoubleKey()).double2ObjectEntrySet();
            }

            public ObjectSortedSet<Double2ObjectMap.Entry<V>> tailSet(Double2ObjectMap.Entry<V> var1) {
               return Double2ObjectRBTreeMap.this.tailMap(var1.getDoubleKey()).double2ObjectEntrySet();
            }
         };
      }

      return this.entries;
   }

   public DoubleSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2ObjectRBTreeMap.KeySet();
      }

      return this.keys;
   }

   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractObjectCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Double2ObjectRBTreeMap.this.new ValueIterator();
            }

            public boolean contains(Object var1) {
               return Double2ObjectRBTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Double2ObjectRBTreeMap.this.count;
            }

            public void clear() {
               Double2ObjectRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public DoubleComparator comparator() {
      return this.actualComparator;
   }

   public Double2ObjectSortedMap<V> headMap(double var1) {
      return new Double2ObjectRBTreeMap.Submap(0.0D, true, var1, false);
   }

   public Double2ObjectSortedMap<V> tailMap(double var1) {
      return new Double2ObjectRBTreeMap.Submap(var1, false, 0.0D, true);
   }

   public Double2ObjectSortedMap<V> subMap(double var1, double var3) {
      return new Double2ObjectRBTreeMap.Submap(var1, false, var3, false);
   }

   public Double2ObjectRBTreeMap<V> clone() {
      Double2ObjectRBTreeMap var1;
      try {
         var1 = (Double2ObjectRBTreeMap)super.clone();
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
         Double2ObjectRBTreeMap.Entry var5 = new Double2ObjectRBTreeMap.Entry();
         Double2ObjectRBTreeMap.Entry var6 = new Double2ObjectRBTreeMap.Entry();
         Double2ObjectRBTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Double2ObjectRBTreeMap.Entry var4 = var6;
         var6.pred((Double2ObjectRBTreeMap.Entry)null);

         while(true) {
            Double2ObjectRBTreeMap.Entry var2;
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
      Double2ObjectRBTreeMap.EntryIterator var3 = new Double2ObjectRBTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Double2ObjectRBTreeMap.Entry var4 = var3.nextEntry();
         var1.writeDouble(var4.key);
         var1.writeObject(var4.value);
      }

   }

   private Double2ObjectRBTreeMap.Entry<V> readTree(ObjectInputStream var1, int var2, Double2ObjectRBTreeMap.Entry<V> var3, Double2ObjectRBTreeMap.Entry<V> var4) throws IOException, ClassNotFoundException {
      Double2ObjectRBTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Double2ObjectRBTreeMap.Entry(var1.readDouble(), var1.readObject());
         var8.pred(var3);
         var8.succ(var4);
         var8.black(true);
         return var8;
      } else if (var2 == 2) {
         var8 = new Double2ObjectRBTreeMap.Entry(var1.readDouble(), var1.readObject());
         var8.black(true);
         var8.right(new Double2ObjectRBTreeMap.Entry(var1.readDouble(), var1.readObject()));
         var8.right.pred(var8);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Double2ObjectRBTreeMap.Entry var7 = new Double2ObjectRBTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readDouble();
         var7.value = var1.readObject();
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
         this.tree = this.readTree(var1, this.count, (Double2ObjectRBTreeMap.Entry)null, (Double2ObjectRBTreeMap.Entry)null);

         Double2ObjectRBTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractDouble2ObjectSortedMap<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      double from;
      double to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Double2ObjectMap.Entry<V>> entries;
      protected transient DoubleSortedSet keys;
      protected transient ObjectCollection<V> values;

      public Submap(double var2, boolean var4, double var5, boolean var7) {
         super();
         if (!var4 && !var7 && Double2ObjectRBTreeMap.this.compare(var2, var5) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var5 + ")");
         } else {
            this.from = var2;
            this.bottom = var4;
            this.to = var5;
            this.top = var7;
            this.defRetValue = Double2ObjectRBTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Double2ObjectRBTreeMap.Submap.SubmapIterator var1 = new Double2ObjectRBTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(double var1) {
         return (this.bottom || Double2ObjectRBTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Double2ObjectRBTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Double2ObjectMap.Entry<V>>() {
               public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> iterator(Double2ObjectMap.Entry<V> var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getDoubleKey());
               }

               public Comparator<? super Double2ObjectMap.Entry<V>> comparator() {
                  return Double2ObjectRBTreeMap.this.double2ObjectEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Double) {
                        Double2ObjectRBTreeMap.Entry var3 = Double2ObjectRBTreeMap.this.findKey((Double)var2.getKey());
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
                     if (var2.getKey() != null && var2.getKey() instanceof Double) {
                        Double2ObjectRBTreeMap.Entry var3 = Double2ObjectRBTreeMap.this.findKey((Double)var2.getKey());
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

               public Double2ObjectMap.Entry<V> first() {
                  return Submap.this.firstEntry();
               }

               public Double2ObjectMap.Entry<V> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Double2ObjectMap.Entry<V>> subSet(Double2ObjectMap.Entry<V> var1, Double2ObjectMap.Entry<V> var2) {
                  return Submap.this.subMap(var1.getDoubleKey(), var2.getDoubleKey()).double2ObjectEntrySet();
               }

               public ObjectSortedSet<Double2ObjectMap.Entry<V>> headSet(Double2ObjectMap.Entry<V> var1) {
                  return Submap.this.headMap(var1.getDoubleKey()).double2ObjectEntrySet();
               }

               public ObjectSortedSet<Double2ObjectMap.Entry<V>> tailSet(Double2ObjectMap.Entry<V> var1) {
                  return Submap.this.tailMap(var1.getDoubleKey()).double2ObjectEntrySet();
               }
            };
         }

         return this.entries;
      }

      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Double2ObjectRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      public ObjectCollection<V> values() {
         if (this.values == null) {
            this.values = new AbstractObjectCollection<V>() {
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

      public boolean containsKey(double var1) {
         return this.in(var1) && Double2ObjectRBTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(Object var1) {
         Double2ObjectRBTreeMap.Submap.SubmapIterator var2 = new Double2ObjectRBTreeMap.Submap.SubmapIterator();

         Object var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = var2.nextEntry().value;
         } while(!Objects.equals(var3, var1));

         return true;
      }

      public V get(double var1) {
         Double2ObjectRBTreeMap.Entry var3;
         return this.in(var1) && (var3 = Double2ObjectRBTreeMap.this.findKey(var1)) != null ? var3.value : this.defRetValue;
      }

      public V put(double var1, V var3) {
         Double2ObjectRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            Object var4 = Double2ObjectRBTreeMap.this.put(var1, var3);
            return Double2ObjectRBTreeMap.this.modified ? this.defRetValue : var4;
         }
      }

      public V remove(double var1) {
         Double2ObjectRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            Object var3 = Double2ObjectRBTreeMap.this.remove(var1);
            return Double2ObjectRBTreeMap.this.modified ? var3 : this.defRetValue;
         }
      }

      public int size() {
         Double2ObjectRBTreeMap.Submap.SubmapIterator var1 = new Double2ObjectRBTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Double2ObjectRBTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public DoubleComparator comparator() {
         return Double2ObjectRBTreeMap.this.actualComparator;
      }

      public Double2ObjectSortedMap<V> headMap(double var1) {
         if (this.top) {
            return Double2ObjectRBTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Double2ObjectRBTreeMap.this.compare(var1, this.to) < 0 ? Double2ObjectRBTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Double2ObjectSortedMap<V> tailMap(double var1) {
         if (this.bottom) {
            return Double2ObjectRBTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Double2ObjectRBTreeMap.this.compare(var1, this.from) > 0 ? Double2ObjectRBTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Double2ObjectSortedMap<V> subMap(double var1, double var3) {
         if (this.top && this.bottom) {
            return Double2ObjectRBTreeMap.this.new Submap(var1, false, var3, false);
         } else {
            if (!this.top) {
               var3 = Double2ObjectRBTreeMap.this.compare(var3, this.to) < 0 ? var3 : this.to;
            }

            if (!this.bottom) {
               var1 = Double2ObjectRBTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var3 == this.to ? this : Double2ObjectRBTreeMap.this.new Submap(var1, false, var3, false);
         }
      }

      public Double2ObjectRBTreeMap.Entry<V> firstEntry() {
         if (Double2ObjectRBTreeMap.this.tree == null) {
            return null;
         } else {
            Double2ObjectRBTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Double2ObjectRBTreeMap.this.firstEntry;
            } else {
               var1 = Double2ObjectRBTreeMap.this.locateKey(this.from);
               if (Double2ObjectRBTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Double2ObjectRBTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Double2ObjectRBTreeMap.Entry<V> lastEntry() {
         if (Double2ObjectRBTreeMap.this.tree == null) {
            return null;
         } else {
            Double2ObjectRBTreeMap.Entry var1;
            if (this.top) {
               var1 = Double2ObjectRBTreeMap.this.lastEntry;
            } else {
               var1 = Double2ObjectRBTreeMap.this.locateKey(this.to);
               if (Double2ObjectRBTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Double2ObjectRBTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public double firstDoubleKey() {
         Double2ObjectRBTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public double lastDoubleKey() {
         Double2ObjectRBTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Double2ObjectRBTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<V> {
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

      private final class SubmapKeyIterator extends Double2ObjectRBTreeMap<V>.Submap.SubmapIterator implements DoubleListIterator {
         public SubmapKeyIterator() {
            super();
         }

         public SubmapKeyIterator(double var2) {
            super(var2);
         }

         public double nextDouble() {
            return this.nextEntry().key;
         }

         public double previousDouble() {
            return this.previousEntry().key;
         }
      }

      private class SubmapEntryIterator extends Double2ObjectRBTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<Double2ObjectMap.Entry<V>> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(double var2) {
            super(var2);
         }

         public Double2ObjectMap.Entry<V> next() {
            return this.nextEntry();
         }

         public Double2ObjectMap.Entry<V> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Double2ObjectRBTreeMap<V>.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(double var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Double2ObjectRBTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Double2ObjectRBTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Double2ObjectRBTreeMap.this.locateKey(var2);
                  if (Double2ObjectRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Double2ObjectRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Double2ObjectRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractDouble2ObjectSortedMap<V>.KeySet {
         private KeySet() {
            super();
         }

         public DoubleBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         public DoubleBidirectionalIterator iterator(double var1) {
            return Submap.this.new SubmapKeyIterator(var1);
         }

         // $FF: synthetic method
         KeySet(Object var2) {
            this();
         }
      }
   }

   private final class ValueIterator extends Double2ObjectRBTreeMap<V>.TreeIterator implements ObjectListIterator<V> {
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

   private class KeySet extends AbstractDouble2ObjectSortedMap<V>.KeySet {
      private KeySet() {
         super();
      }

      public DoubleBidirectionalIterator iterator() {
         return Double2ObjectRBTreeMap.this.new KeyIterator();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return Double2ObjectRBTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Double2ObjectRBTreeMap<V>.TreeIterator implements DoubleListIterator {
      public KeyIterator() {
         super();
      }

      public KeyIterator(double var2) {
         super(var2);
      }

      public double nextDouble() {
         return this.nextEntry().key;
      }

      public double previousDouble() {
         return this.previousEntry().key;
      }
   }

   private class EntryIterator extends Double2ObjectRBTreeMap<V>.TreeIterator implements ObjectListIterator<Double2ObjectMap.Entry<V>> {
      EntryIterator() {
         super();
      }

      EntryIterator(double var2) {
         super(var2);
      }

      public Double2ObjectMap.Entry<V> next() {
         return this.nextEntry();
      }

      public Double2ObjectMap.Entry<V> previous() {
         return this.previousEntry();
      }
   }

   private class TreeIterator {
      Double2ObjectRBTreeMap.Entry<V> prev;
      Double2ObjectRBTreeMap.Entry<V> next;
      Double2ObjectRBTreeMap.Entry<V> curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Double2ObjectRBTreeMap.this.firstEntry;
      }

      TreeIterator(double var2) {
         super();
         if ((this.next = Double2ObjectRBTreeMap.this.locateKey(var2)) != null) {
            if (Double2ObjectRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Double2ObjectRBTreeMap.Entry<V> nextEntry() {
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

      Double2ObjectRBTreeMap.Entry<V> previousEntry() {
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
            Double2ObjectRBTreeMap.this.remove(this.curr.key);
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

   private static final class Entry<V> extends AbstractDouble2ObjectMap.BasicEntry<V> implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      Double2ObjectRBTreeMap.Entry<V> left;
      Double2ObjectRBTreeMap.Entry<V> right;
      int info;

      Entry() {
         super(0.0D, (Object)null);
      }

      Entry(double var1, V var3) {
         super(var1, var3);
         this.info = -1073741824;
      }

      Double2ObjectRBTreeMap.Entry<V> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Double2ObjectRBTreeMap.Entry<V> right() {
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

      void pred(Double2ObjectRBTreeMap.Entry<V> var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Double2ObjectRBTreeMap.Entry<V> var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Double2ObjectRBTreeMap.Entry<V> var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Double2ObjectRBTreeMap.Entry<V> var1) {
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

      Double2ObjectRBTreeMap.Entry<V> next() {
         Double2ObjectRBTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Double2ObjectRBTreeMap.Entry<V> prev() {
         Double2ObjectRBTreeMap.Entry var1 = this.left;
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

      public Double2ObjectRBTreeMap.Entry<V> clone() {
         Double2ObjectRBTreeMap.Entry var1;
         try {
            var1 = (Double2ObjectRBTreeMap.Entry)super.clone();
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
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)var2.getKey()) && Objects.equals(this.value, var2.getValue());
         }
      }

      public int hashCode() {
         return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "=>" + this.value;
      }
   }
}
