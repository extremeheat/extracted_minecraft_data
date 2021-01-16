package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
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

public class Double2FloatAVLTreeMap extends AbstractDouble2FloatSortedMap implements Serializable, Cloneable {
   protected transient Double2FloatAVLTreeMap.Entry tree;
   protected int count;
   protected transient Double2FloatAVLTreeMap.Entry firstEntry;
   protected transient Double2FloatAVLTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Double2FloatMap.Entry> entries;
   protected transient DoubleSortedSet keys;
   protected transient FloatCollection values;
   protected transient boolean modified;
   protected Comparator<? super Double> storedComparator;
   protected transient DoubleComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Double2FloatAVLTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = DoubleComparators.asDoubleComparator(this.storedComparator);
   }

   public Double2FloatAVLTreeMap(Comparator<? super Double> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Double2FloatAVLTreeMap(Map<? extends Double, ? extends Float> var1) {
      this();
      this.putAll(var1);
   }

   public Double2FloatAVLTreeMap(SortedMap<Double, Float> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Double2FloatAVLTreeMap(Double2FloatMap var1) {
      this();
      this.putAll(var1);
   }

   public Double2FloatAVLTreeMap(Double2FloatSortedMap var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Double2FloatAVLTreeMap(double[] var1, float[] var2, Comparator<? super Double> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Double2FloatAVLTreeMap(double[] var1, float[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(double var1, double var3) {
      return this.actualComparator == null ? Double.compare(var1, var3) : this.actualComparator.compare(var1, var3);
   }

   final Double2FloatAVLTreeMap.Entry findKey(double var1) {
      Double2FloatAVLTreeMap.Entry var3;
      int var4;
      for(var3 = this.tree; var3 != null && (var4 = this.compare(var1, var3.key)) != 0; var3 = var4 < 0 ? var3.left() : var3.right()) {
      }

      return var3;
   }

   final Double2FloatAVLTreeMap.Entry locateKey(double var1) {
      Double2FloatAVLTreeMap.Entry var3 = this.tree;
      Double2FloatAVLTreeMap.Entry var4 = this.tree;

      int var5;
      for(var5 = 0; var3 != null && (var5 = this.compare(var1, var3.key)) != 0; var3 = var5 < 0 ? var3.left() : var3.right()) {
         var4 = var3;
      }

      return var5 == 0 ? var3 : var4;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public float addTo(double var1, float var3) {
      Double2FloatAVLTreeMap.Entry var4 = this.add(var1);
      float var5 = var4.value;
      var4.value += var3;
      return var5;
   }

   public float put(double var1, float var3) {
      Double2FloatAVLTreeMap.Entry var4 = this.add(var1);
      float var5 = var4.value;
      var4.value = var3;
      return var5;
   }

   private Double2FloatAVLTreeMap.Entry add(double var1) {
      this.modified = false;
      Double2FloatAVLTreeMap.Entry var3 = null;
      if (this.tree == null) {
         ++this.count;
         var3 = this.tree = this.lastEntry = this.firstEntry = new Double2FloatAVLTreeMap.Entry(var1, this.defRetValue);
         this.modified = true;
      } else {
         Double2FloatAVLTreeMap.Entry var4 = this.tree;
         Double2FloatAVLTreeMap.Entry var5 = null;
         Double2FloatAVLTreeMap.Entry var6 = this.tree;
         Double2FloatAVLTreeMap.Entry var7 = null;
         Double2FloatAVLTreeMap.Entry var8 = null;
         int var10 = 0;

         while(true) {
            int var9;
            if ((var9 = this.compare(var1, var4.key)) == 0) {
               return var4;
            }

            if (var4.balance() != 0) {
               var10 = 0;
               var7 = var5;
               var6 = var4;
            }

            if (this.dirPath[var10++] = var9 > 0) {
               if (var4.succ()) {
                  ++this.count;
                  var3 = new Double2FloatAVLTreeMap.Entry(var1, this.defRetValue);
                  this.modified = true;
                  if (var4.right == null) {
                     this.lastEntry = var3;
                  }

                  var3.left = var4;
                  var3.right = var4.right;
                  var4.right(var3);
                  break;
               }

               var5 = var4;
               var4 = var4.right;
            } else {
               if (var4.pred()) {
                  ++this.count;
                  var3 = new Double2FloatAVLTreeMap.Entry(var1, this.defRetValue);
                  this.modified = true;
                  if (var4.left == null) {
                     this.firstEntry = var3;
                  }

                  var3.right = var4;
                  var3.left = var4.left;
                  var4.left(var3);
                  break;
               }

               var5 = var4;
               var4 = var4.left;
            }
         }

         var4 = var6;

         for(var10 = 0; var4 != var3; var4 = this.dirPath[var10++] ? var4.right : var4.left) {
            if (this.dirPath[var10]) {
               var4.incBalance();
            } else {
               var4.decBalance();
            }
         }

         Double2FloatAVLTreeMap.Entry var11;
         if (var6.balance() == -2) {
            var11 = var6.left;
            if (var11.balance() == -1) {
               var8 = var11;
               if (var11.succ()) {
                  var11.succ(false);
                  var6.pred(var11);
               } else {
                  var6.left = var11.right;
               }

               var11.right = var6;
               var11.balance(0);
               var6.balance(0);
            } else {
               assert var11.balance() == 1;

               var8 = var11.right;
               var11.right = var8.left;
               var8.left = var11;
               var6.left = var8.right;
               var8.right = var6;
               if (var8.balance() == -1) {
                  var11.balance(0);
                  var6.balance(1);
               } else if (var8.balance() == 0) {
                  var11.balance(0);
                  var6.balance(0);
               } else {
                  var11.balance(-1);
                  var6.balance(0);
               }

               var8.balance(0);
               if (var8.pred()) {
                  var11.succ(var8);
                  var8.pred(false);
               }

               if (var8.succ()) {
                  var6.pred(var8);
                  var8.succ(false);
               }
            }
         } else {
            if (var6.balance() != 2) {
               return var3;
            }

            var11 = var6.right;
            if (var11.balance() == 1) {
               var8 = var11;
               if (var11.pred()) {
                  var11.pred(false);
                  var6.succ(var11);
               } else {
                  var6.right = var11.left;
               }

               var11.left = var6;
               var11.balance(0);
               var6.balance(0);
            } else {
               assert var11.balance() == -1;

               var8 = var11.left;
               var11.left = var8.right;
               var8.right = var11;
               var6.right = var8.left;
               var8.left = var6;
               if (var8.balance() == 1) {
                  var11.balance(0);
                  var6.balance(-1);
               } else if (var8.balance() == 0) {
                  var11.balance(0);
                  var6.balance(0);
               } else {
                  var11.balance(1);
                  var6.balance(0);
               }

               var8.balance(0);
               if (var8.pred()) {
                  var6.succ(var8);
                  var8.pred(false);
               }

               if (var8.succ()) {
                  var11.pred(var8);
                  var8.succ(false);
               }
            }
         }

         if (var7 == null) {
            this.tree = var8;
         } else if (var7.left == var6) {
            var7.left = var8;
         } else {
            var7.right = var8;
         }
      }

      return var3;
   }

   private Double2FloatAVLTreeMap.Entry parent(Double2FloatAVLTreeMap.Entry var1) {
      if (var1 == this.tree) {
         return null;
      } else {
         Double2FloatAVLTreeMap.Entry var3 = var1;

         Double2FloatAVLTreeMap.Entry var2;
         Double2FloatAVLTreeMap.Entry var4;
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

   public float remove(double var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Double2FloatAVLTreeMap.Entry var4 = this.tree;
         Double2FloatAVLTreeMap.Entry var5 = null;
         boolean var6 = false;
         double var7 = var1;

         int var3;
         while((var3 = this.compare(var7, var4.key)) != 0) {
            if (var6 = var3 > 0) {
               var5 = var4;
               if ((var4 = var4.right()) == null) {
                  return this.defRetValue;
               }
            } else {
               var5 = var4;
               if ((var4 = var4.left()) == null) {
                  return this.defRetValue;
               }
            }
         }

         if (var4.left == null) {
            this.firstEntry = var4.next();
         }

         if (var4.right == null) {
            this.lastEntry = var4.prev();
         }

         Double2FloatAVLTreeMap.Entry var9;
         Double2FloatAVLTreeMap.Entry var10;
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
            Double2FloatAVLTreeMap.Entry var11;
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

         this.modified = true;
         --this.count;
         return var4.value;
      }
   }

   public boolean containsValue(float var1) {
      Double2FloatAVLTreeMap.ValueIterator var2 = new Double2FloatAVLTreeMap.ValueIterator();
      int var4 = this.count;

      float var3;
      do {
         if (var4-- == 0) {
            return false;
         }

         var3 = var2.nextFloat();
      } while(Float.floatToIntBits(var3) != Float.floatToIntBits(var1));

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

   public float get(double var1) {
      Double2FloatAVLTreeMap.Entry var3 = this.findKey(var1);
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

   public ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Double2FloatMap.Entry>() {
            final Comparator<? super Double2FloatMap.Entry> comparator = (var1x, var2) -> {
               return Double2FloatAVLTreeMap.this.actualComparator.compare(var1x.getDoubleKey(), var2.getDoubleKey());
            };

            public Comparator<? super Double2FloatMap.Entry> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Double2FloatMap.Entry> iterator() {
               return Double2FloatAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Double2FloatMap.Entry> iterator(Double2FloatMap.Entry var1) {
               return Double2FloatAVLTreeMap.this.new EntryIterator(var1.getDoubleKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Double) {
                     if (var2.getValue() != null && var2.getValue() instanceof Float) {
                        Double2FloatAVLTreeMap.Entry var3 = Double2FloatAVLTreeMap.this.findKey((Double)var2.getKey());
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
                  if (var2.getKey() != null && var2.getKey() instanceof Double) {
                     if (var2.getValue() != null && var2.getValue() instanceof Float) {
                        Double2FloatAVLTreeMap.Entry var3 = Double2FloatAVLTreeMap.this.findKey((Double)var2.getKey());
                        if (var3 != null && Float.floatToIntBits(var3.getFloatValue()) == Float.floatToIntBits((Float)var2.getValue())) {
                           Double2FloatAVLTreeMap.this.remove(var3.key);
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
               return Double2FloatAVLTreeMap.this.count;
            }

            public void clear() {
               Double2FloatAVLTreeMap.this.clear();
            }

            public Double2FloatMap.Entry first() {
               return Double2FloatAVLTreeMap.this.firstEntry;
            }

            public Double2FloatMap.Entry last() {
               return Double2FloatAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Double2FloatMap.Entry> subSet(Double2FloatMap.Entry var1, Double2FloatMap.Entry var2) {
               return Double2FloatAVLTreeMap.this.subMap(var1.getDoubleKey(), var2.getDoubleKey()).double2FloatEntrySet();
            }

            public ObjectSortedSet<Double2FloatMap.Entry> headSet(Double2FloatMap.Entry var1) {
               return Double2FloatAVLTreeMap.this.headMap(var1.getDoubleKey()).double2FloatEntrySet();
            }

            public ObjectSortedSet<Double2FloatMap.Entry> tailSet(Double2FloatMap.Entry var1) {
               return Double2FloatAVLTreeMap.this.tailMap(var1.getDoubleKey()).double2FloatEntrySet();
            }
         };
      }

      return this.entries;
   }

   public DoubleSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2FloatAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   public FloatCollection values() {
      if (this.values == null) {
         this.values = new AbstractFloatCollection() {
            public FloatIterator iterator() {
               return Double2FloatAVLTreeMap.this.new ValueIterator();
            }

            public boolean contains(float var1) {
               return Double2FloatAVLTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Double2FloatAVLTreeMap.this.count;
            }

            public void clear() {
               Double2FloatAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public DoubleComparator comparator() {
      return this.actualComparator;
   }

   public Double2FloatSortedMap headMap(double var1) {
      return new Double2FloatAVLTreeMap.Submap(0.0D, true, var1, false);
   }

   public Double2FloatSortedMap tailMap(double var1) {
      return new Double2FloatAVLTreeMap.Submap(var1, false, 0.0D, true);
   }

   public Double2FloatSortedMap subMap(double var1, double var3) {
      return new Double2FloatAVLTreeMap.Submap(var1, false, var3, false);
   }

   public Double2FloatAVLTreeMap clone() {
      Double2FloatAVLTreeMap var1;
      try {
         var1 = (Double2FloatAVLTreeMap)super.clone();
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
         Double2FloatAVLTreeMap.Entry var5 = new Double2FloatAVLTreeMap.Entry();
         Double2FloatAVLTreeMap.Entry var6 = new Double2FloatAVLTreeMap.Entry();
         Double2FloatAVLTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Double2FloatAVLTreeMap.Entry var4 = var6;
         var6.pred((Double2FloatAVLTreeMap.Entry)null);

         while(true) {
            Double2FloatAVLTreeMap.Entry var2;
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
      Double2FloatAVLTreeMap.EntryIterator var3 = new Double2FloatAVLTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Double2FloatAVLTreeMap.Entry var4 = var3.nextEntry();
         var1.writeDouble(var4.key);
         var1.writeFloat(var4.value);
      }

   }

   private Double2FloatAVLTreeMap.Entry readTree(ObjectInputStream var1, int var2, Double2FloatAVLTreeMap.Entry var3, Double2FloatAVLTreeMap.Entry var4) throws IOException, ClassNotFoundException {
      Double2FloatAVLTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Double2FloatAVLTreeMap.Entry(var1.readDouble(), var1.readFloat());
         var8.pred(var3);
         var8.succ(var4);
         return var8;
      } else if (var2 == 2) {
         var8 = new Double2FloatAVLTreeMap.Entry(var1.readDouble(), var1.readFloat());
         var8.right(new Double2FloatAVLTreeMap.Entry(var1.readDouble(), var1.readFloat()));
         var8.right.pred(var8);
         var8.balance(1);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Double2FloatAVLTreeMap.Entry var7 = new Double2FloatAVLTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readDouble();
         var7.value = var1.readFloat();
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
         this.tree = this.readTree(var1, this.count, (Double2FloatAVLTreeMap.Entry)null, (Double2FloatAVLTreeMap.Entry)null);

         Double2FloatAVLTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractDouble2FloatSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      double from;
      double to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Double2FloatMap.Entry> entries;
      protected transient DoubleSortedSet keys;
      protected transient FloatCollection values;

      public Submap(double var2, boolean var4, double var5, boolean var7) {
         super();
         if (!var4 && !var7 && Double2FloatAVLTreeMap.this.compare(var2, var5) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var5 + ")");
         } else {
            this.from = var2;
            this.bottom = var4;
            this.to = var5;
            this.top = var7;
            this.defRetValue = Double2FloatAVLTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Double2FloatAVLTreeMap.Submap.SubmapIterator var1 = new Double2FloatAVLTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(double var1) {
         return (this.bottom || Double2FloatAVLTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Double2FloatAVLTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Double2FloatMap.Entry>() {
               public ObjectBidirectionalIterator<Double2FloatMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Double2FloatMap.Entry> iterator(Double2FloatMap.Entry var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getDoubleKey());
               }

               public Comparator<? super Double2FloatMap.Entry> comparator() {
                  return Double2FloatAVLTreeMap.this.double2FloatEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Double) {
                        if (var2.getValue() != null && var2.getValue() instanceof Float) {
                           Double2FloatAVLTreeMap.Entry var3 = Double2FloatAVLTreeMap.this.findKey((Double)var2.getKey());
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
                     if (var2.getKey() != null && var2.getKey() instanceof Double) {
                        if (var2.getValue() != null && var2.getValue() instanceof Float) {
                           Double2FloatAVLTreeMap.Entry var3 = Double2FloatAVLTreeMap.this.findKey((Double)var2.getKey());
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

               public Double2FloatMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Double2FloatMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Double2FloatMap.Entry> subSet(Double2FloatMap.Entry var1, Double2FloatMap.Entry var2) {
                  return Submap.this.subMap(var1.getDoubleKey(), var2.getDoubleKey()).double2FloatEntrySet();
               }

               public ObjectSortedSet<Double2FloatMap.Entry> headSet(Double2FloatMap.Entry var1) {
                  return Submap.this.headMap(var1.getDoubleKey()).double2FloatEntrySet();
               }

               public ObjectSortedSet<Double2FloatMap.Entry> tailSet(Double2FloatMap.Entry var1) {
                  return Submap.this.tailMap(var1.getDoubleKey()).double2FloatEntrySet();
               }
            };
         }

         return this.entries;
      }

      public DoubleSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Double2FloatAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      public FloatCollection values() {
         if (this.values == null) {
            this.values = new AbstractFloatCollection() {
               public FloatIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               public boolean contains(float var1) {
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
         return this.in(var1) && Double2FloatAVLTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(float var1) {
         Double2FloatAVLTreeMap.Submap.SubmapIterator var2 = new Double2FloatAVLTreeMap.Submap.SubmapIterator();

         float var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = var2.nextEntry().value;
         } while(Float.floatToIntBits(var3) != Float.floatToIntBits(var1));

         return true;
      }

      public float get(double var1) {
         Double2FloatAVLTreeMap.Entry var3;
         return this.in(var1) && (var3 = Double2FloatAVLTreeMap.this.findKey(var1)) != null ? var3.value : this.defRetValue;
      }

      public float put(double var1, float var3) {
         Double2FloatAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            float var4 = Double2FloatAVLTreeMap.this.put(var1, var3);
            return Double2FloatAVLTreeMap.this.modified ? this.defRetValue : var4;
         }
      }

      public float remove(double var1) {
         Double2FloatAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            float var3 = Double2FloatAVLTreeMap.this.remove(var1);
            return Double2FloatAVLTreeMap.this.modified ? var3 : this.defRetValue;
         }
      }

      public int size() {
         Double2FloatAVLTreeMap.Submap.SubmapIterator var1 = new Double2FloatAVLTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Double2FloatAVLTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public DoubleComparator comparator() {
         return Double2FloatAVLTreeMap.this.actualComparator;
      }

      public Double2FloatSortedMap headMap(double var1) {
         if (this.top) {
            return Double2FloatAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Double2FloatAVLTreeMap.this.compare(var1, this.to) < 0 ? Double2FloatAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Double2FloatSortedMap tailMap(double var1) {
         if (this.bottom) {
            return Double2FloatAVLTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Double2FloatAVLTreeMap.this.compare(var1, this.from) > 0 ? Double2FloatAVLTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Double2FloatSortedMap subMap(double var1, double var3) {
         if (this.top && this.bottom) {
            return Double2FloatAVLTreeMap.this.new Submap(var1, false, var3, false);
         } else {
            if (!this.top) {
               var3 = Double2FloatAVLTreeMap.this.compare(var3, this.to) < 0 ? var3 : this.to;
            }

            if (!this.bottom) {
               var1 = Double2FloatAVLTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var3 == this.to ? this : Double2FloatAVLTreeMap.this.new Submap(var1, false, var3, false);
         }
      }

      public Double2FloatAVLTreeMap.Entry firstEntry() {
         if (Double2FloatAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Double2FloatAVLTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Double2FloatAVLTreeMap.this.firstEntry;
            } else {
               var1 = Double2FloatAVLTreeMap.this.locateKey(this.from);
               if (Double2FloatAVLTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Double2FloatAVLTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Double2FloatAVLTreeMap.Entry lastEntry() {
         if (Double2FloatAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Double2FloatAVLTreeMap.Entry var1;
            if (this.top) {
               var1 = Double2FloatAVLTreeMap.this.lastEntry;
            } else {
               var1 = Double2FloatAVLTreeMap.this.locateKey(this.to);
               if (Double2FloatAVLTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Double2FloatAVLTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public double firstDoubleKey() {
         Double2FloatAVLTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public double lastDoubleKey() {
         Double2FloatAVLTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Double2FloatAVLTreeMap.Submap.SubmapIterator implements FloatListIterator {
         private SubmapValueIterator() {
            super();
         }

         public float nextFloat() {
            return this.nextEntry().value;
         }

         public float previousFloat() {
            return this.previousEntry().value;
         }

         // $FF: synthetic method
         SubmapValueIterator(Object var2) {
            this();
         }
      }

      private final class SubmapKeyIterator extends Double2FloatAVLTreeMap.Submap.SubmapIterator implements DoubleListIterator {
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

      private class SubmapEntryIterator extends Double2FloatAVLTreeMap.Submap.SubmapIterator implements ObjectListIterator<Double2FloatMap.Entry> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(double var2) {
            super(var2);
         }

         public Double2FloatMap.Entry next() {
            return this.nextEntry();
         }

         public Double2FloatMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Double2FloatAVLTreeMap.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(double var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Double2FloatAVLTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Double2FloatAVLTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Double2FloatAVLTreeMap.this.locateKey(var2);
                  if (Double2FloatAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Double2FloatAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Double2FloatAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractDouble2FloatSortedMap.KeySet {
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

   private final class ValueIterator extends Double2FloatAVLTreeMap.TreeIterator implements FloatListIterator {
      private ValueIterator() {
         super();
      }

      public float nextFloat() {
         return this.nextEntry().value;
      }

      public float previousFloat() {
         return this.previousEntry().value;
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractDouble2FloatSortedMap.KeySet {
      private KeySet() {
         super();
      }

      public DoubleBidirectionalIterator iterator() {
         return Double2FloatAVLTreeMap.this.new KeyIterator();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return Double2FloatAVLTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Double2FloatAVLTreeMap.TreeIterator implements DoubleListIterator {
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

   private class EntryIterator extends Double2FloatAVLTreeMap.TreeIterator implements ObjectListIterator<Double2FloatMap.Entry> {
      EntryIterator() {
         super();
      }

      EntryIterator(double var2) {
         super(var2);
      }

      public Double2FloatMap.Entry next() {
         return this.nextEntry();
      }

      public Double2FloatMap.Entry previous() {
         return this.previousEntry();
      }

      public void set(Double2FloatMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Double2FloatMap.Entry var1) {
         throw new UnsupportedOperationException();
      }
   }

   private class TreeIterator {
      Double2FloatAVLTreeMap.Entry prev;
      Double2FloatAVLTreeMap.Entry next;
      Double2FloatAVLTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Double2FloatAVLTreeMap.this.firstEntry;
      }

      TreeIterator(double var2) {
         super();
         if ((this.next = Double2FloatAVLTreeMap.this.locateKey(var2)) != null) {
            if (Double2FloatAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Double2FloatAVLTreeMap.Entry nextEntry() {
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

      Double2FloatAVLTreeMap.Entry previousEntry() {
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
            Double2FloatAVLTreeMap.this.remove(this.curr.key);
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

   private static final class Entry extends AbstractDouble2FloatMap.BasicEntry implements Cloneable {
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Double2FloatAVLTreeMap.Entry left;
      Double2FloatAVLTreeMap.Entry right;
      int info;

      Entry() {
         super(0.0D, 0.0F);
      }

      Entry(double var1, float var3) {
         super(var1, var3);
         this.info = -1073741824;
      }

      Double2FloatAVLTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Double2FloatAVLTreeMap.Entry right() {
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

      void pred(Double2FloatAVLTreeMap.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Double2FloatAVLTreeMap.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Double2FloatAVLTreeMap.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Double2FloatAVLTreeMap.Entry var1) {
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

      Double2FloatAVLTreeMap.Entry next() {
         Double2FloatAVLTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Double2FloatAVLTreeMap.Entry prev() {
         Double2FloatAVLTreeMap.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public float setValue(float var1) {
         float var2 = this.value;
         this.value = var1;
         return var2;
      }

      public Double2FloatAVLTreeMap.Entry clone() {
         Double2FloatAVLTreeMap.Entry var1;
         try {
            var1 = (Double2FloatAVLTreeMap.Entry)super.clone();
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
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)var2.getKey()) && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)var2.getValue());
         }
      }

      public int hashCode() {
         return HashCommon.double2int(this.key) ^ HashCommon.float2int(this.value);
      }

      public String toString() {
         return this.key + "=>" + this.value;
      }
   }
}
