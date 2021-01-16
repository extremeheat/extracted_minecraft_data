package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;

public class Object2LongAVLTreeMap<K> extends AbstractObject2LongSortedMap<K> implements Serializable, Cloneable {
   protected transient Object2LongAVLTreeMap.Entry<K> tree;
   protected int count;
   protected transient Object2LongAVLTreeMap.Entry<K> firstEntry;
   protected transient Object2LongAVLTreeMap.Entry<K> lastEntry;
   protected transient ObjectSortedSet<Object2LongMap.Entry<K>> entries;
   protected transient ObjectSortedSet<K> keys;
   protected transient LongCollection values;
   protected transient boolean modified;
   protected Comparator<? super K> storedComparator;
   protected transient Comparator<? super K> actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Object2LongAVLTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = this.storedComparator;
   }

   public Object2LongAVLTreeMap(Comparator<? super K> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Object2LongAVLTreeMap(Map<? extends K, ? extends Long> var1) {
      this();
      this.putAll(var1);
   }

   public Object2LongAVLTreeMap(SortedMap<K, Long> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Object2LongAVLTreeMap(Object2LongMap<? extends K> var1) {
      this();
      this.putAll(var1);
   }

   public Object2LongAVLTreeMap(Object2LongSortedMap<K> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Object2LongAVLTreeMap(K[] var1, long[] var2, Comparator<? super K> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Object2LongAVLTreeMap(K[] var1, long[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(K var1, K var2) {
      return this.actualComparator == null ? ((Comparable)var1).compareTo(var2) : this.actualComparator.compare(var1, var2);
   }

   final Object2LongAVLTreeMap.Entry<K> findKey(K var1) {
      Object2LongAVLTreeMap.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final Object2LongAVLTreeMap.Entry<K> locateKey(K var1) {
      Object2LongAVLTreeMap.Entry var2 = this.tree;
      Object2LongAVLTreeMap.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public long addTo(K var1, long var2) {
      Object2LongAVLTreeMap.Entry var4 = this.add(var1);
      long var5 = var4.value;
      var4.value += var2;
      return var5;
   }

   public long put(K var1, long var2) {
      Object2LongAVLTreeMap.Entry var4 = this.add(var1);
      long var5 = var4.value;
      var4.value = var2;
      return var5;
   }

   private Object2LongAVLTreeMap.Entry<K> add(K var1) {
      this.modified = false;
      Object2LongAVLTreeMap.Entry var2 = null;
      if (this.tree == null) {
         ++this.count;
         var2 = this.tree = this.lastEntry = this.firstEntry = new Object2LongAVLTreeMap.Entry(var1, this.defRetValue);
         this.modified = true;
      } else {
         Object2LongAVLTreeMap.Entry var3 = this.tree;
         Object2LongAVLTreeMap.Entry var4 = null;
         Object2LongAVLTreeMap.Entry var5 = this.tree;
         Object2LongAVLTreeMap.Entry var6 = null;
         Object2LongAVLTreeMap.Entry var7 = null;
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
                  var2 = new Object2LongAVLTreeMap.Entry(var1, this.defRetValue);
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
                  var2 = new Object2LongAVLTreeMap.Entry(var1, this.defRetValue);
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

         Object2LongAVLTreeMap.Entry var10;
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

   private Object2LongAVLTreeMap.Entry<K> parent(Object2LongAVLTreeMap.Entry<K> var1) {
      if (var1 == this.tree) {
         return null;
      } else {
         Object2LongAVLTreeMap.Entry var3 = var1;

         Object2LongAVLTreeMap.Entry var2;
         Object2LongAVLTreeMap.Entry var4;
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

   public long removeLong(Object var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Object2LongAVLTreeMap.Entry var3 = this.tree;
         Object2LongAVLTreeMap.Entry var4 = null;
         boolean var5 = false;
         Object var6 = var1;

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

         Object2LongAVLTreeMap.Entry var7;
         Object2LongAVLTreeMap.Entry var8;
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
            Object2LongAVLTreeMap.Entry var9;
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

   public boolean containsValue(long var1) {
      Object2LongAVLTreeMap.ValueIterator var3 = new Object2LongAVLTreeMap.ValueIterator();
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

   public boolean containsKey(Object var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public long getLong(Object var1) {
      Object2LongAVLTreeMap.Entry var2 = this.findKey(var1);
      return var2 == null ? this.defRetValue : var2.value;
   }

   public K firstKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public K lastKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public ObjectSortedSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Object2LongMap.Entry<K>>() {
            final Comparator<? super Object2LongMap.Entry<K>> comparator = (var1x, var2) -> {
               return Object2LongAVLTreeMap.this.actualComparator.compare(var1x.getKey(), var2.getKey());
            };

            public Comparator<? super Object2LongMap.Entry<K>> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Object2LongMap.Entry<K>> iterator() {
               return Object2LongAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Object2LongMap.Entry<K>> iterator(Object2LongMap.Entry<K> var1) {
               return Object2LongAVLTreeMap.this.new EntryIterator(var1.getKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getValue() != null && var2.getValue() instanceof Long) {
                     Object2LongAVLTreeMap.Entry var3 = Object2LongAVLTreeMap.this.findKey(var2.getKey());
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
                  if (var2.getValue() != null && var2.getValue() instanceof Long) {
                     Object2LongAVLTreeMap.Entry var3 = Object2LongAVLTreeMap.this.findKey(var2.getKey());
                     if (var3 != null && var3.getLongValue() == (Long)var2.getValue()) {
                        Object2LongAVLTreeMap.this.removeLong(var3.key);
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
               return Object2LongAVLTreeMap.this.count;
            }

            public void clear() {
               Object2LongAVLTreeMap.this.clear();
            }

            public Object2LongMap.Entry<K> first() {
               return Object2LongAVLTreeMap.this.firstEntry;
            }

            public Object2LongMap.Entry<K> last() {
               return Object2LongAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Object2LongMap.Entry<K>> subSet(Object2LongMap.Entry<K> var1, Object2LongMap.Entry<K> var2) {
               return Object2LongAVLTreeMap.this.subMap(var1.getKey(), var2.getKey()).object2LongEntrySet();
            }

            public ObjectSortedSet<Object2LongMap.Entry<K>> headSet(Object2LongMap.Entry<K> var1) {
               return Object2LongAVLTreeMap.this.headMap(var1.getKey()).object2LongEntrySet();
            }

            public ObjectSortedSet<Object2LongMap.Entry<K>> tailSet(Object2LongMap.Entry<K> var1) {
               return Object2LongAVLTreeMap.this.tailMap(var1.getKey()).object2LongEntrySet();
            }
         };
      }

      return this.entries;
   }

   public ObjectSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2LongAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            public LongIterator iterator() {
               return Object2LongAVLTreeMap.this.new ValueIterator();
            }

            public boolean contains(long var1) {
               return Object2LongAVLTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Object2LongAVLTreeMap.this.count;
            }

            public void clear() {
               Object2LongAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public Comparator<? super K> comparator() {
      return this.actualComparator;
   }

   public Object2LongSortedMap<K> headMap(K var1) {
      return new Object2LongAVLTreeMap.Submap((Object)null, true, var1, false);
   }

   public Object2LongSortedMap<K> tailMap(K var1) {
      return new Object2LongAVLTreeMap.Submap(var1, false, (Object)null, true);
   }

   public Object2LongSortedMap<K> subMap(K var1, K var2) {
      return new Object2LongAVLTreeMap.Submap(var1, false, var2, false);
   }

   public Object2LongAVLTreeMap<K> clone() {
      Object2LongAVLTreeMap var1;
      try {
         var1 = (Object2LongAVLTreeMap)super.clone();
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
         Object2LongAVLTreeMap.Entry var5 = new Object2LongAVLTreeMap.Entry();
         Object2LongAVLTreeMap.Entry var6 = new Object2LongAVLTreeMap.Entry();
         Object2LongAVLTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Object2LongAVLTreeMap.Entry var4 = var6;
         var6.pred((Object2LongAVLTreeMap.Entry)null);

         while(true) {
            Object2LongAVLTreeMap.Entry var2;
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
      Object2LongAVLTreeMap.EntryIterator var3 = new Object2LongAVLTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Object2LongAVLTreeMap.Entry var4 = var3.nextEntry();
         var1.writeObject(var4.key);
         var1.writeLong(var4.value);
      }

   }

   private Object2LongAVLTreeMap.Entry<K> readTree(ObjectInputStream var1, int var2, Object2LongAVLTreeMap.Entry<K> var3, Object2LongAVLTreeMap.Entry<K> var4) throws IOException, ClassNotFoundException {
      Object2LongAVLTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Object2LongAVLTreeMap.Entry(var1.readObject(), var1.readLong());
         var8.pred(var3);
         var8.succ(var4);
         return var8;
      } else if (var2 == 2) {
         var8 = new Object2LongAVLTreeMap.Entry(var1.readObject(), var1.readLong());
         var8.right(new Object2LongAVLTreeMap.Entry(var1.readObject(), var1.readLong()));
         var8.right.pred(var8);
         var8.balance(1);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Object2LongAVLTreeMap.Entry var7 = new Object2LongAVLTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readObject();
         var7.value = var1.readLong();
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
         this.tree = this.readTree(var1, this.count, (Object2LongAVLTreeMap.Entry)null, (Object2LongAVLTreeMap.Entry)null);

         Object2LongAVLTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractObject2LongSortedMap<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      K from;
      K to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Object2LongMap.Entry<K>> entries;
      protected transient ObjectSortedSet<K> keys;
      protected transient LongCollection values;

      public Submap(K var2, boolean var3, K var4, boolean var5) {
         super();
         if (!var3 && !var5 && Object2LongAVLTreeMap.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
            this.defRetValue = Object2LongAVLTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Object2LongAVLTreeMap.Submap.SubmapIterator var1 = new Object2LongAVLTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(K var1) {
         return (this.bottom || Object2LongAVLTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Object2LongAVLTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Object2LongMap.Entry<K>>() {
               public ObjectBidirectionalIterator<Object2LongMap.Entry<K>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Object2LongMap.Entry<K>> iterator(Object2LongMap.Entry<K> var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getKey());
               }

               public Comparator<? super Object2LongMap.Entry<K>> comparator() {
                  return Object2LongAVLTreeMap.this.object2LongEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getValue() != null && var2.getValue() instanceof Long) {
                        Object2LongAVLTreeMap.Entry var3 = Object2LongAVLTreeMap.this.findKey(var2.getKey());
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
                     if (var2.getValue() != null && var2.getValue() instanceof Long) {
                        Object2LongAVLTreeMap.Entry var3 = Object2LongAVLTreeMap.this.findKey(var2.getKey());
                        if (var3 != null && Submap.this.in(var3.key)) {
                           Submap.this.removeLong(var3.key);
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

               public Object2LongMap.Entry<K> first() {
                  return Submap.this.firstEntry();
               }

               public Object2LongMap.Entry<K> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Object2LongMap.Entry<K>> subSet(Object2LongMap.Entry<K> var1, Object2LongMap.Entry<K> var2) {
                  return Submap.this.subMap(var1.getKey(), var2.getKey()).object2LongEntrySet();
               }

               public ObjectSortedSet<Object2LongMap.Entry<K>> headSet(Object2LongMap.Entry<K> var1) {
                  return Submap.this.headMap(var1.getKey()).object2LongEntrySet();
               }

               public ObjectSortedSet<Object2LongMap.Entry<K>> tailSet(Object2LongMap.Entry<K> var1) {
                  return Submap.this.tailMap(var1.getKey()).object2LongEntrySet();
               }
            };
         }

         return this.entries;
      }

      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = new Object2LongAVLTreeMap.Submap.KeySet();
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

      public boolean containsKey(Object var1) {
         return this.in(var1) && Object2LongAVLTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(long var1) {
         Object2LongAVLTreeMap.Submap.SubmapIterator var3 = new Object2LongAVLTreeMap.Submap.SubmapIterator();

         long var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = var3.nextEntry().value;
         } while(var4 != var1);

         return true;
      }

      public long getLong(Object var1) {
         Object2LongAVLTreeMap.Entry var2;
         return this.in(var1) && (var2 = Object2LongAVLTreeMap.this.findKey(var1)) != null ? var2.value : this.defRetValue;
      }

      public long put(K var1, long var2) {
         Object2LongAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            long var4 = Object2LongAVLTreeMap.this.put(var1, var2);
            return Object2LongAVLTreeMap.this.modified ? this.defRetValue : var4;
         }
      }

      public long removeLong(Object var1) {
         Object2LongAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            long var2 = Object2LongAVLTreeMap.this.removeLong(var1);
            return Object2LongAVLTreeMap.this.modified ? var2 : this.defRetValue;
         }
      }

      public int size() {
         Object2LongAVLTreeMap.Submap.SubmapIterator var1 = new Object2LongAVLTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Object2LongAVLTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public Comparator<? super K> comparator() {
         return Object2LongAVLTreeMap.this.actualComparator;
      }

      public Object2LongSortedMap<K> headMap(K var1) {
         if (this.top) {
            return Object2LongAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Object2LongAVLTreeMap.this.compare(var1, this.to) < 0 ? Object2LongAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Object2LongSortedMap<K> tailMap(K var1) {
         if (this.bottom) {
            return Object2LongAVLTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Object2LongAVLTreeMap.this.compare(var1, this.from) > 0 ? Object2LongAVLTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Object2LongSortedMap<K> subMap(K var1, K var2) {
         if (this.top && this.bottom) {
            return Object2LongAVLTreeMap.this.new Submap(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = Object2LongAVLTreeMap.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = Object2LongAVLTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : Object2LongAVLTreeMap.this.new Submap(var1, false, var2, false);
         }
      }

      public Object2LongAVLTreeMap.Entry<K> firstEntry() {
         if (Object2LongAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Object2LongAVLTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Object2LongAVLTreeMap.this.firstEntry;
            } else {
               var1 = Object2LongAVLTreeMap.this.locateKey(this.from);
               if (Object2LongAVLTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Object2LongAVLTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Object2LongAVLTreeMap.Entry<K> lastEntry() {
         if (Object2LongAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Object2LongAVLTreeMap.Entry var1;
            if (this.top) {
               var1 = Object2LongAVLTreeMap.this.lastEntry;
            } else {
               var1 = Object2LongAVLTreeMap.this.locateKey(this.to);
               if (Object2LongAVLTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Object2LongAVLTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public K firstKey() {
         Object2LongAVLTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public K lastKey() {
         Object2LongAVLTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Object2LongAVLTreeMap<K>.Submap.SubmapIterator implements LongListIterator {
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

      private final class SubmapKeyIterator extends Object2LongAVLTreeMap<K>.Submap.SubmapIterator implements ObjectListIterator<K> {
         public SubmapKeyIterator() {
            super();
         }

         public SubmapKeyIterator(K var2) {
            super(var2);
         }

         public K next() {
            return this.nextEntry().key;
         }

         public K previous() {
            return this.previousEntry().key;
         }
      }

      private class SubmapEntryIterator extends Object2LongAVLTreeMap<K>.Submap.SubmapIterator implements ObjectListIterator<Object2LongMap.Entry<K>> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(K var2) {
            super(var2);
         }

         public Object2LongMap.Entry<K> next() {
            return this.nextEntry();
         }

         public Object2LongMap.Entry<K> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Object2LongAVLTreeMap<K>.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(K var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Object2LongAVLTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Object2LongAVLTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Object2LongAVLTreeMap.this.locateKey(var2);
                  if (Object2LongAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Object2LongAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Object2LongAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractObject2LongSortedMap<K>.KeySet {
         private KeySet() {
            super();
         }

         public ObjectBidirectionalIterator<K> iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         public ObjectBidirectionalIterator<K> iterator(K var1) {
            return Submap.this.new SubmapKeyIterator(var1);
         }

         // $FF: synthetic method
         KeySet(Object var2) {
            this();
         }
      }
   }

   private final class ValueIterator extends Object2LongAVLTreeMap<K>.TreeIterator implements LongListIterator {
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

   private class KeySet extends AbstractObject2LongSortedMap<K>.KeySet {
      private KeySet() {
         super();
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return Object2LongAVLTreeMap.this.new KeyIterator();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return Object2LongAVLTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Object2LongAVLTreeMap<K>.TreeIterator implements ObjectListIterator<K> {
      public KeyIterator() {
         super();
      }

      public KeyIterator(K var2) {
         super(var2);
      }

      public K next() {
         return this.nextEntry().key;
      }

      public K previous() {
         return this.previousEntry().key;
      }
   }

   private class EntryIterator extends Object2LongAVLTreeMap<K>.TreeIterator implements ObjectListIterator<Object2LongMap.Entry<K>> {
      EntryIterator() {
         super();
      }

      EntryIterator(K var2) {
         super(var2);
      }

      public Object2LongMap.Entry<K> next() {
         return this.nextEntry();
      }

      public Object2LongMap.Entry<K> previous() {
         return this.previousEntry();
      }

      public void set(Object2LongMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Object2LongMap.Entry<K> var1) {
         throw new UnsupportedOperationException();
      }
   }

   private class TreeIterator {
      Object2LongAVLTreeMap.Entry<K> prev;
      Object2LongAVLTreeMap.Entry<K> next;
      Object2LongAVLTreeMap.Entry<K> curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Object2LongAVLTreeMap.this.firstEntry;
      }

      TreeIterator(K var2) {
         super();
         if ((this.next = Object2LongAVLTreeMap.this.locateKey(var2)) != null) {
            if (Object2LongAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Object2LongAVLTreeMap.Entry<K> nextEntry() {
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

      Object2LongAVLTreeMap.Entry<K> previousEntry() {
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
            Object2LongAVLTreeMap.this.removeLong(this.curr.key);
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

   private static final class Entry<K> extends AbstractObject2LongMap.BasicEntry<K> implements Cloneable {
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Object2LongAVLTreeMap.Entry<K> left;
      Object2LongAVLTreeMap.Entry<K> right;
      int info;

      Entry() {
         super((Object)null, 0L);
      }

      Entry(K var1, long var2) {
         super(var1, var2);
         this.info = -1073741824;
      }

      Object2LongAVLTreeMap.Entry<K> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Object2LongAVLTreeMap.Entry<K> right() {
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

      void pred(Object2LongAVLTreeMap.Entry<K> var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Object2LongAVLTreeMap.Entry<K> var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Object2LongAVLTreeMap.Entry<K> var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Object2LongAVLTreeMap.Entry<K> var1) {
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

      Object2LongAVLTreeMap.Entry<K> next() {
         Object2LongAVLTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Object2LongAVLTreeMap.Entry<K> prev() {
         Object2LongAVLTreeMap.Entry var1 = this.left;
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

      public Object2LongAVLTreeMap.Entry<K> clone() {
         Object2LongAVLTreeMap.Entry var1;
         try {
            var1 = (Object2LongAVLTreeMap.Entry)super.clone();
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
            return Objects.equals(this.key, var2.getKey()) && this.value == (Long)var2.getValue();
         }
      }

      public int hashCode() {
         return this.key.hashCode() ^ HashCommon.long2int(this.value);
      }

      public String toString() {
         return this.key + "=>" + this.value;
      }
   }
}
