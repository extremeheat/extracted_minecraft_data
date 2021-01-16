package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
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

public class Float2ReferenceRBTreeMap<V> extends AbstractFloat2ReferenceSortedMap<V> implements Serializable, Cloneable {
   protected transient Float2ReferenceRBTreeMap.Entry<V> tree;
   protected int count;
   protected transient Float2ReferenceRBTreeMap.Entry<V> firstEntry;
   protected transient Float2ReferenceRBTreeMap.Entry<V> lastEntry;
   protected transient ObjectSortedSet<Float2ReferenceMap.Entry<V>> entries;
   protected transient FloatSortedSet keys;
   protected transient ReferenceCollection<V> values;
   protected transient boolean modified;
   protected Comparator<? super Float> storedComparator;
   protected transient FloatComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Float2ReferenceRBTreeMap.Entry<V>[] nodePath;

   public Float2ReferenceRBTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = FloatComparators.asFloatComparator(this.storedComparator);
   }

   public Float2ReferenceRBTreeMap(Comparator<? super Float> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Float2ReferenceRBTreeMap(Map<? extends Float, ? extends V> var1) {
      this();
      this.putAll(var1);
   }

   public Float2ReferenceRBTreeMap(SortedMap<Float, V> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Float2ReferenceRBTreeMap(Float2ReferenceMap<? extends V> var1) {
      this();
      this.putAll(var1);
   }

   public Float2ReferenceRBTreeMap(Float2ReferenceSortedMap<V> var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Float2ReferenceRBTreeMap(float[] var1, V[] var2, Comparator<? super Float> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Float2ReferenceRBTreeMap(float[] var1, V[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(float var1, float var2) {
      return this.actualComparator == null ? Float.compare(var1, var2) : this.actualComparator.compare(var1, var2);
   }

   final Float2ReferenceRBTreeMap.Entry<V> findKey(float var1) {
      Float2ReferenceRBTreeMap.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final Float2ReferenceRBTreeMap.Entry<V> locateKey(float var1) {
      Float2ReferenceRBTreeMap.Entry var2 = this.tree;
      Float2ReferenceRBTreeMap.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Float2ReferenceRBTreeMap.Entry[64];
   }

   public V put(float var1, V var2) {
      Float2ReferenceRBTreeMap.Entry var3 = this.add(var1);
      Object var4 = var3.value;
      var3.value = var2;
      return var4;
   }

   private Float2ReferenceRBTreeMap.Entry<V> add(float var1) {
      this.modified = false;
      int var2 = 0;
      Float2ReferenceRBTreeMap.Entry var3;
      if (this.tree == null) {
         ++this.count;
         var3 = this.tree = this.lastEntry = this.firstEntry = new Float2ReferenceRBTreeMap.Entry(var1, this.defRetValue);
      } else {
         Float2ReferenceRBTreeMap.Entry var4 = this.tree;
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
               var3 = new Float2ReferenceRBTreeMap.Entry(var1, this.defRetValue);
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
               var3 = new Float2ReferenceRBTreeMap.Entry(var1, this.defRetValue);
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

               Float2ReferenceRBTreeMap.Entry var7;
               Float2ReferenceRBTreeMap.Entry var8;
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

   public V remove(float var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Float2ReferenceRBTreeMap.Entry var2 = this.tree;
         int var4 = 0;
         float var5 = var1;

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

         Float2ReferenceRBTreeMap.Entry var7;
         Float2ReferenceRBTreeMap.Entry var8;
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

   public boolean containsValue(Object var1) {
      Float2ReferenceRBTreeMap.ValueIterator var2 = new Float2ReferenceRBTreeMap.ValueIterator();
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

   public boolean containsKey(float var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public V get(float var1) {
      Float2ReferenceRBTreeMap.Entry var2 = this.findKey(var1);
      return var2 == null ? this.defRetValue : var2.value;
   }

   public float firstFloatKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public float lastFloatKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public ObjectSortedSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Float2ReferenceMap.Entry<V>>() {
            final Comparator<? super Float2ReferenceMap.Entry<V>> comparator = (var1x, var2) -> {
               return Float2ReferenceRBTreeMap.this.actualComparator.compare(var1x.getFloatKey(), var2.getFloatKey());
            };

            public Comparator<? super Float2ReferenceMap.Entry<V>> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> iterator() {
               return Float2ReferenceRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> iterator(Float2ReferenceMap.Entry<V> var1) {
               return Float2ReferenceRBTreeMap.this.new EntryIterator(var1.getFloatKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Float) {
                     Float2ReferenceRBTreeMap.Entry var3 = Float2ReferenceRBTreeMap.this.findKey((Float)var2.getKey());
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
                  if (var2.getKey() != null && var2.getKey() instanceof Float) {
                     Float2ReferenceRBTreeMap.Entry var3 = Float2ReferenceRBTreeMap.this.findKey((Float)var2.getKey());
                     if (var3 != null && var3.getValue() == var2.getValue()) {
                        Float2ReferenceRBTreeMap.this.remove(var3.key);
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
               return Float2ReferenceRBTreeMap.this.count;
            }

            public void clear() {
               Float2ReferenceRBTreeMap.this.clear();
            }

            public Float2ReferenceMap.Entry<V> first() {
               return Float2ReferenceRBTreeMap.this.firstEntry;
            }

            public Float2ReferenceMap.Entry<V> last() {
               return Float2ReferenceRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Float2ReferenceMap.Entry<V>> subSet(Float2ReferenceMap.Entry<V> var1, Float2ReferenceMap.Entry<V> var2) {
               return Float2ReferenceRBTreeMap.this.subMap(var1.getFloatKey(), var2.getFloatKey()).float2ReferenceEntrySet();
            }

            public ObjectSortedSet<Float2ReferenceMap.Entry<V>> headSet(Float2ReferenceMap.Entry<V> var1) {
               return Float2ReferenceRBTreeMap.this.headMap(var1.getFloatKey()).float2ReferenceEntrySet();
            }

            public ObjectSortedSet<Float2ReferenceMap.Entry<V>> tailSet(Float2ReferenceMap.Entry<V> var1) {
               return Float2ReferenceRBTreeMap.this.tailMap(var1.getFloatKey()).float2ReferenceEntrySet();
            }
         };
      }

      return this.entries;
   }

   public FloatSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2ReferenceRBTreeMap.KeySet();
      }

      return this.keys;
   }

   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            public ObjectIterator<V> iterator() {
               return Float2ReferenceRBTreeMap.this.new ValueIterator();
            }

            public boolean contains(Object var1) {
               return Float2ReferenceRBTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Float2ReferenceRBTreeMap.this.count;
            }

            public void clear() {
               Float2ReferenceRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public FloatComparator comparator() {
      return this.actualComparator;
   }

   public Float2ReferenceSortedMap<V> headMap(float var1) {
      return new Float2ReferenceRBTreeMap.Submap(0.0F, true, var1, false);
   }

   public Float2ReferenceSortedMap<V> tailMap(float var1) {
      return new Float2ReferenceRBTreeMap.Submap(var1, false, 0.0F, true);
   }

   public Float2ReferenceSortedMap<V> subMap(float var1, float var2) {
      return new Float2ReferenceRBTreeMap.Submap(var1, false, var2, false);
   }

   public Float2ReferenceRBTreeMap<V> clone() {
      Float2ReferenceRBTreeMap var1;
      try {
         var1 = (Float2ReferenceRBTreeMap)super.clone();
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
         Float2ReferenceRBTreeMap.Entry var5 = new Float2ReferenceRBTreeMap.Entry();
         Float2ReferenceRBTreeMap.Entry var6 = new Float2ReferenceRBTreeMap.Entry();
         Float2ReferenceRBTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Float2ReferenceRBTreeMap.Entry var4 = var6;
         var6.pred((Float2ReferenceRBTreeMap.Entry)null);

         while(true) {
            Float2ReferenceRBTreeMap.Entry var2;
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
      Float2ReferenceRBTreeMap.EntryIterator var3 = new Float2ReferenceRBTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Float2ReferenceRBTreeMap.Entry var4 = var3.nextEntry();
         var1.writeFloat(var4.key);
         var1.writeObject(var4.value);
      }

   }

   private Float2ReferenceRBTreeMap.Entry<V> readTree(ObjectInputStream var1, int var2, Float2ReferenceRBTreeMap.Entry<V> var3, Float2ReferenceRBTreeMap.Entry<V> var4) throws IOException, ClassNotFoundException {
      Float2ReferenceRBTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Float2ReferenceRBTreeMap.Entry(var1.readFloat(), var1.readObject());
         var8.pred(var3);
         var8.succ(var4);
         var8.black(true);
         return var8;
      } else if (var2 == 2) {
         var8 = new Float2ReferenceRBTreeMap.Entry(var1.readFloat(), var1.readObject());
         var8.black(true);
         var8.right(new Float2ReferenceRBTreeMap.Entry(var1.readFloat(), var1.readObject()));
         var8.right.pred(var8);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Float2ReferenceRBTreeMap.Entry var7 = new Float2ReferenceRBTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readFloat();
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
         this.tree = this.readTree(var1, this.count, (Float2ReferenceRBTreeMap.Entry)null, (Float2ReferenceRBTreeMap.Entry)null);

         Float2ReferenceRBTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractFloat2ReferenceSortedMap<V> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      float from;
      float to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Float2ReferenceMap.Entry<V>> entries;
      protected transient FloatSortedSet keys;
      protected transient ReferenceCollection<V> values;

      public Submap(float var2, boolean var3, float var4, boolean var5) {
         super();
         if (!var3 && !var5 && Float2ReferenceRBTreeMap.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
            this.defRetValue = Float2ReferenceRBTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Float2ReferenceRBTreeMap.Submap.SubmapIterator var1 = new Float2ReferenceRBTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(float var1) {
         return (this.bottom || Float2ReferenceRBTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Float2ReferenceRBTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Float2ReferenceMap.Entry<V>>() {
               public ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> iterator(Float2ReferenceMap.Entry<V> var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getFloatKey());
               }

               public Comparator<? super Float2ReferenceMap.Entry<V>> comparator() {
                  return Float2ReferenceRBTreeMap.this.float2ReferenceEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Float) {
                        Float2ReferenceRBTreeMap.Entry var3 = Float2ReferenceRBTreeMap.this.findKey((Float)var2.getKey());
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
                     if (var2.getKey() != null && var2.getKey() instanceof Float) {
                        Float2ReferenceRBTreeMap.Entry var3 = Float2ReferenceRBTreeMap.this.findKey((Float)var2.getKey());
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

               public Float2ReferenceMap.Entry<V> first() {
                  return Submap.this.firstEntry();
               }

               public Float2ReferenceMap.Entry<V> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Float2ReferenceMap.Entry<V>> subSet(Float2ReferenceMap.Entry<V> var1, Float2ReferenceMap.Entry<V> var2) {
                  return Submap.this.subMap(var1.getFloatKey(), var2.getFloatKey()).float2ReferenceEntrySet();
               }

               public ObjectSortedSet<Float2ReferenceMap.Entry<V>> headSet(Float2ReferenceMap.Entry<V> var1) {
                  return Submap.this.headMap(var1.getFloatKey()).float2ReferenceEntrySet();
               }

               public ObjectSortedSet<Float2ReferenceMap.Entry<V>> tailSet(Float2ReferenceMap.Entry<V> var1) {
                  return Submap.this.tailMap(var1.getFloatKey()).float2ReferenceEntrySet();
               }
            };
         }

         return this.entries;
      }

      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Float2ReferenceRBTreeMap.Submap.KeySet();
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

      public boolean containsKey(float var1) {
         return this.in(var1) && Float2ReferenceRBTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(Object var1) {
         Float2ReferenceRBTreeMap.Submap.SubmapIterator var2 = new Float2ReferenceRBTreeMap.Submap.SubmapIterator();

         Object var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = var2.nextEntry().value;
         } while(var3 != var1);

         return true;
      }

      public V get(float var1) {
         Float2ReferenceRBTreeMap.Entry var2;
         return this.in(var1) && (var2 = Float2ReferenceRBTreeMap.this.findKey(var1)) != null ? var2.value : this.defRetValue;
      }

      public V put(float var1, V var2) {
         Float2ReferenceRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            Object var3 = Float2ReferenceRBTreeMap.this.put(var1, var2);
            return Float2ReferenceRBTreeMap.this.modified ? this.defRetValue : var3;
         }
      }

      public V remove(float var1) {
         Float2ReferenceRBTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            Object var2 = Float2ReferenceRBTreeMap.this.remove(var1);
            return Float2ReferenceRBTreeMap.this.modified ? var2 : this.defRetValue;
         }
      }

      public int size() {
         Float2ReferenceRBTreeMap.Submap.SubmapIterator var1 = new Float2ReferenceRBTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Float2ReferenceRBTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public FloatComparator comparator() {
         return Float2ReferenceRBTreeMap.this.actualComparator;
      }

      public Float2ReferenceSortedMap<V> headMap(float var1) {
         if (this.top) {
            return Float2ReferenceRBTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Float2ReferenceRBTreeMap.this.compare(var1, this.to) < 0 ? Float2ReferenceRBTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Float2ReferenceSortedMap<V> tailMap(float var1) {
         if (this.bottom) {
            return Float2ReferenceRBTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Float2ReferenceRBTreeMap.this.compare(var1, this.from) > 0 ? Float2ReferenceRBTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Float2ReferenceSortedMap<V> subMap(float var1, float var2) {
         if (this.top && this.bottom) {
            return Float2ReferenceRBTreeMap.this.new Submap(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = Float2ReferenceRBTreeMap.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = Float2ReferenceRBTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : Float2ReferenceRBTreeMap.this.new Submap(var1, false, var2, false);
         }
      }

      public Float2ReferenceRBTreeMap.Entry<V> firstEntry() {
         if (Float2ReferenceRBTreeMap.this.tree == null) {
            return null;
         } else {
            Float2ReferenceRBTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Float2ReferenceRBTreeMap.this.firstEntry;
            } else {
               var1 = Float2ReferenceRBTreeMap.this.locateKey(this.from);
               if (Float2ReferenceRBTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Float2ReferenceRBTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Float2ReferenceRBTreeMap.Entry<V> lastEntry() {
         if (Float2ReferenceRBTreeMap.this.tree == null) {
            return null;
         } else {
            Float2ReferenceRBTreeMap.Entry var1;
            if (this.top) {
               var1 = Float2ReferenceRBTreeMap.this.lastEntry;
            } else {
               var1 = Float2ReferenceRBTreeMap.this.locateKey(this.to);
               if (Float2ReferenceRBTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Float2ReferenceRBTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public float firstFloatKey() {
         Float2ReferenceRBTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public float lastFloatKey() {
         Float2ReferenceRBTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Float2ReferenceRBTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<V> {
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

      private final class SubmapKeyIterator extends Float2ReferenceRBTreeMap<V>.Submap.SubmapIterator implements FloatListIterator {
         public SubmapKeyIterator() {
            super();
         }

         public SubmapKeyIterator(float var2) {
            super(var2);
         }

         public float nextFloat() {
            return this.nextEntry().key;
         }

         public float previousFloat() {
            return this.previousEntry().key;
         }
      }

      private class SubmapEntryIterator extends Float2ReferenceRBTreeMap<V>.Submap.SubmapIterator implements ObjectListIterator<Float2ReferenceMap.Entry<V>> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(float var2) {
            super(var2);
         }

         public Float2ReferenceMap.Entry<V> next() {
            return this.nextEntry();
         }

         public Float2ReferenceMap.Entry<V> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Float2ReferenceRBTreeMap<V>.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(float var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Float2ReferenceRBTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Float2ReferenceRBTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Float2ReferenceRBTreeMap.this.locateKey(var2);
                  if (Float2ReferenceRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Float2ReferenceRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Float2ReferenceRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractFloat2ReferenceSortedMap<V>.KeySet {
         private KeySet() {
            super();
         }

         public FloatBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         public FloatBidirectionalIterator iterator(float var1) {
            return Submap.this.new SubmapKeyIterator(var1);
         }

         // $FF: synthetic method
         KeySet(Object var2) {
            this();
         }
      }
   }

   private final class ValueIterator extends Float2ReferenceRBTreeMap<V>.TreeIterator implements ObjectListIterator<V> {
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

   private class KeySet extends AbstractFloat2ReferenceSortedMap<V>.KeySet {
      private KeySet() {
         super();
      }

      public FloatBidirectionalIterator iterator() {
         return Float2ReferenceRBTreeMap.this.new KeyIterator();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return Float2ReferenceRBTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Float2ReferenceRBTreeMap<V>.TreeIterator implements FloatListIterator {
      public KeyIterator() {
         super();
      }

      public KeyIterator(float var2) {
         super(var2);
      }

      public float nextFloat() {
         return this.nextEntry().key;
      }

      public float previousFloat() {
         return this.previousEntry().key;
      }
   }

   private class EntryIterator extends Float2ReferenceRBTreeMap<V>.TreeIterator implements ObjectListIterator<Float2ReferenceMap.Entry<V>> {
      EntryIterator() {
         super();
      }

      EntryIterator(float var2) {
         super(var2);
      }

      public Float2ReferenceMap.Entry<V> next() {
         return this.nextEntry();
      }

      public Float2ReferenceMap.Entry<V> previous() {
         return this.previousEntry();
      }
   }

   private class TreeIterator {
      Float2ReferenceRBTreeMap.Entry<V> prev;
      Float2ReferenceRBTreeMap.Entry<V> next;
      Float2ReferenceRBTreeMap.Entry<V> curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Float2ReferenceRBTreeMap.this.firstEntry;
      }

      TreeIterator(float var2) {
         super();
         if ((this.next = Float2ReferenceRBTreeMap.this.locateKey(var2)) != null) {
            if (Float2ReferenceRBTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Float2ReferenceRBTreeMap.Entry<V> nextEntry() {
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

      Float2ReferenceRBTreeMap.Entry<V> previousEntry() {
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
            Float2ReferenceRBTreeMap.this.remove(this.curr.key);
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

   private static final class Entry<V> extends AbstractFloat2ReferenceMap.BasicEntry<V> implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      Float2ReferenceRBTreeMap.Entry<V> left;
      Float2ReferenceRBTreeMap.Entry<V> right;
      int info;

      Entry() {
         super(0.0F, (Object)null);
      }

      Entry(float var1, V var2) {
         super(var1, var2);
         this.info = -1073741824;
      }

      Float2ReferenceRBTreeMap.Entry<V> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Float2ReferenceRBTreeMap.Entry<V> right() {
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

      void pred(Float2ReferenceRBTreeMap.Entry<V> var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Float2ReferenceRBTreeMap.Entry<V> var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Float2ReferenceRBTreeMap.Entry<V> var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Float2ReferenceRBTreeMap.Entry<V> var1) {
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

      Float2ReferenceRBTreeMap.Entry<V> next() {
         Float2ReferenceRBTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Float2ReferenceRBTreeMap.Entry<V> prev() {
         Float2ReferenceRBTreeMap.Entry var1 = this.left;
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

      public Float2ReferenceRBTreeMap.Entry<V> clone() {
         Float2ReferenceRBTreeMap.Entry var1;
         try {
            var1 = (Float2ReferenceRBTreeMap.Entry)super.clone();
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
            return Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)var2.getKey()) && this.value == var2.getValue();
         }
      }

      public int hashCode() {
         return HashCommon.float2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      public String toString() {
         return this.key + "=>" + this.value;
      }
   }
}
