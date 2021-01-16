package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
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

public class Long2IntAVLTreeMap extends AbstractLong2IntSortedMap implements Serializable, Cloneable {
   protected transient Long2IntAVLTreeMap.Entry tree;
   protected int count;
   protected transient Long2IntAVLTreeMap.Entry firstEntry;
   protected transient Long2IntAVLTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Long2IntMap.Entry> entries;
   protected transient LongSortedSet keys;
   protected transient IntCollection values;
   protected transient boolean modified;
   protected Comparator<? super Long> storedComparator;
   protected transient LongComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Long2IntAVLTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = LongComparators.asLongComparator(this.storedComparator);
   }

   public Long2IntAVLTreeMap(Comparator<? super Long> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Long2IntAVLTreeMap(Map<? extends Long, ? extends Integer> var1) {
      this();
      this.putAll(var1);
   }

   public Long2IntAVLTreeMap(SortedMap<Long, Integer> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Long2IntAVLTreeMap(Long2IntMap var1) {
      this();
      this.putAll(var1);
   }

   public Long2IntAVLTreeMap(Long2IntSortedMap var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Long2IntAVLTreeMap(long[] var1, int[] var2, Comparator<? super Long> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Long2IntAVLTreeMap(long[] var1, int[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(long var1, long var3) {
      return this.actualComparator == null ? Long.compare(var1, var3) : this.actualComparator.compare(var1, var3);
   }

   final Long2IntAVLTreeMap.Entry findKey(long var1) {
      Long2IntAVLTreeMap.Entry var3;
      int var4;
      for(var3 = this.tree; var3 != null && (var4 = this.compare(var1, var3.key)) != 0; var3 = var4 < 0 ? var3.left() : var3.right()) {
      }

      return var3;
   }

   final Long2IntAVLTreeMap.Entry locateKey(long var1) {
      Long2IntAVLTreeMap.Entry var3 = this.tree;
      Long2IntAVLTreeMap.Entry var4 = this.tree;

      int var5;
      for(var5 = 0; var3 != null && (var5 = this.compare(var1, var3.key)) != 0; var3 = var5 < 0 ? var3.left() : var3.right()) {
         var4 = var3;
      }

      return var5 == 0 ? var3 : var4;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public int addTo(long var1, int var3) {
      Long2IntAVLTreeMap.Entry var4 = this.add(var1);
      int var5 = var4.value;
      var4.value += var3;
      return var5;
   }

   public int put(long var1, int var3) {
      Long2IntAVLTreeMap.Entry var4 = this.add(var1);
      int var5 = var4.value;
      var4.value = var3;
      return var5;
   }

   private Long2IntAVLTreeMap.Entry add(long var1) {
      this.modified = false;
      Long2IntAVLTreeMap.Entry var3 = null;
      if (this.tree == null) {
         ++this.count;
         var3 = this.tree = this.lastEntry = this.firstEntry = new Long2IntAVLTreeMap.Entry(var1, this.defRetValue);
         this.modified = true;
      } else {
         Long2IntAVLTreeMap.Entry var4 = this.tree;
         Long2IntAVLTreeMap.Entry var5 = null;
         Long2IntAVLTreeMap.Entry var6 = this.tree;
         Long2IntAVLTreeMap.Entry var7 = null;
         Long2IntAVLTreeMap.Entry var8 = null;
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
                  var3 = new Long2IntAVLTreeMap.Entry(var1, this.defRetValue);
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
                  var3 = new Long2IntAVLTreeMap.Entry(var1, this.defRetValue);
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

         Long2IntAVLTreeMap.Entry var11;
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

   private Long2IntAVLTreeMap.Entry parent(Long2IntAVLTreeMap.Entry var1) {
      if (var1 == this.tree) {
         return null;
      } else {
         Long2IntAVLTreeMap.Entry var3 = var1;

         Long2IntAVLTreeMap.Entry var2;
         Long2IntAVLTreeMap.Entry var4;
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

   public int remove(long var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Long2IntAVLTreeMap.Entry var4 = this.tree;
         Long2IntAVLTreeMap.Entry var5 = null;
         boolean var6 = false;
         long var7 = var1;

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

         Long2IntAVLTreeMap.Entry var9;
         Long2IntAVLTreeMap.Entry var10;
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
            Long2IntAVLTreeMap.Entry var11;
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

   public boolean containsValue(int var1) {
      Long2IntAVLTreeMap.ValueIterator var2 = new Long2IntAVLTreeMap.ValueIterator();
      int var4 = this.count;

      int var3;
      do {
         if (var4-- == 0) {
            return false;
         }

         var3 = var2.nextInt();
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

   public boolean containsKey(long var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public int get(long var1) {
      Long2IntAVLTreeMap.Entry var3 = this.findKey(var1);
      return var3 == null ? this.defRetValue : var3.value;
   }

   public long firstLongKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public long lastLongKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Long2IntMap.Entry>() {
            final Comparator<? super Long2IntMap.Entry> comparator = (var1x, var2) -> {
               return Long2IntAVLTreeMap.this.actualComparator.compare(var1x.getLongKey(), var2.getLongKey());
            };

            public Comparator<? super Long2IntMap.Entry> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Long2IntMap.Entry> iterator() {
               return Long2IntAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Long2IntMap.Entry> iterator(Long2IntMap.Entry var1) {
               return Long2IntAVLTreeMap.this.new EntryIterator(var1.getLongKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Long) {
                     if (var2.getValue() != null && var2.getValue() instanceof Integer) {
                        Long2IntAVLTreeMap.Entry var3 = Long2IntAVLTreeMap.this.findKey((Long)var2.getKey());
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
                  if (var2.getKey() != null && var2.getKey() instanceof Long) {
                     if (var2.getValue() != null && var2.getValue() instanceof Integer) {
                        Long2IntAVLTreeMap.Entry var3 = Long2IntAVLTreeMap.this.findKey((Long)var2.getKey());
                        if (var3 != null && var3.getIntValue() == (Integer)var2.getValue()) {
                           Long2IntAVLTreeMap.this.remove(var3.key);
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
               return Long2IntAVLTreeMap.this.count;
            }

            public void clear() {
               Long2IntAVLTreeMap.this.clear();
            }

            public Long2IntMap.Entry first() {
               return Long2IntAVLTreeMap.this.firstEntry;
            }

            public Long2IntMap.Entry last() {
               return Long2IntAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Long2IntMap.Entry> subSet(Long2IntMap.Entry var1, Long2IntMap.Entry var2) {
               return Long2IntAVLTreeMap.this.subMap(var1.getLongKey(), var2.getLongKey()).long2IntEntrySet();
            }

            public ObjectSortedSet<Long2IntMap.Entry> headSet(Long2IntMap.Entry var1) {
               return Long2IntAVLTreeMap.this.headMap(var1.getLongKey()).long2IntEntrySet();
            }

            public ObjectSortedSet<Long2IntMap.Entry> tailSet(Long2IntMap.Entry var1) {
               return Long2IntAVLTreeMap.this.tailMap(var1.getLongKey()).long2IntEntrySet();
            }
         };
      }

      return this.entries;
   }

   public LongSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2IntAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   public IntCollection values() {
      if (this.values == null) {
         this.values = new AbstractIntCollection() {
            public IntIterator iterator() {
               return Long2IntAVLTreeMap.this.new ValueIterator();
            }

            public boolean contains(int var1) {
               return Long2IntAVLTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Long2IntAVLTreeMap.this.count;
            }

            public void clear() {
               Long2IntAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public LongComparator comparator() {
      return this.actualComparator;
   }

   public Long2IntSortedMap headMap(long var1) {
      return new Long2IntAVLTreeMap.Submap(0L, true, var1, false);
   }

   public Long2IntSortedMap tailMap(long var1) {
      return new Long2IntAVLTreeMap.Submap(var1, false, 0L, true);
   }

   public Long2IntSortedMap subMap(long var1, long var3) {
      return new Long2IntAVLTreeMap.Submap(var1, false, var3, false);
   }

   public Long2IntAVLTreeMap clone() {
      Long2IntAVLTreeMap var1;
      try {
         var1 = (Long2IntAVLTreeMap)super.clone();
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
         Long2IntAVLTreeMap.Entry var5 = new Long2IntAVLTreeMap.Entry();
         Long2IntAVLTreeMap.Entry var6 = new Long2IntAVLTreeMap.Entry();
         Long2IntAVLTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Long2IntAVLTreeMap.Entry var4 = var6;
         var6.pred((Long2IntAVLTreeMap.Entry)null);

         while(true) {
            Long2IntAVLTreeMap.Entry var2;
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
      Long2IntAVLTreeMap.EntryIterator var3 = new Long2IntAVLTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Long2IntAVLTreeMap.Entry var4 = var3.nextEntry();
         var1.writeLong(var4.key);
         var1.writeInt(var4.value);
      }

   }

   private Long2IntAVLTreeMap.Entry readTree(ObjectInputStream var1, int var2, Long2IntAVLTreeMap.Entry var3, Long2IntAVLTreeMap.Entry var4) throws IOException, ClassNotFoundException {
      Long2IntAVLTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Long2IntAVLTreeMap.Entry(var1.readLong(), var1.readInt());
         var8.pred(var3);
         var8.succ(var4);
         return var8;
      } else if (var2 == 2) {
         var8 = new Long2IntAVLTreeMap.Entry(var1.readLong(), var1.readInt());
         var8.right(new Long2IntAVLTreeMap.Entry(var1.readLong(), var1.readInt()));
         var8.right.pred(var8);
         var8.balance(1);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Long2IntAVLTreeMap.Entry var7 = new Long2IntAVLTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readLong();
         var7.value = var1.readInt();
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
         this.tree = this.readTree(var1, this.count, (Long2IntAVLTreeMap.Entry)null, (Long2IntAVLTreeMap.Entry)null);

         Long2IntAVLTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractLong2IntSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      long from;
      long to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Long2IntMap.Entry> entries;
      protected transient LongSortedSet keys;
      protected transient IntCollection values;

      public Submap(long var2, boolean var4, long var5, boolean var7) {
         super();
         if (!var4 && !var7 && Long2IntAVLTreeMap.this.compare(var2, var5) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var5 + ")");
         } else {
            this.from = var2;
            this.bottom = var4;
            this.to = var5;
            this.top = var7;
            this.defRetValue = Long2IntAVLTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Long2IntAVLTreeMap.Submap.SubmapIterator var1 = new Long2IntAVLTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(long var1) {
         return (this.bottom || Long2IntAVLTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Long2IntAVLTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Long2IntMap.Entry>() {
               public ObjectBidirectionalIterator<Long2IntMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Long2IntMap.Entry> iterator(Long2IntMap.Entry var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getLongKey());
               }

               public Comparator<? super Long2IntMap.Entry> comparator() {
                  return Long2IntAVLTreeMap.this.long2IntEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Long) {
                        if (var2.getValue() != null && var2.getValue() instanceof Integer) {
                           Long2IntAVLTreeMap.Entry var3 = Long2IntAVLTreeMap.this.findKey((Long)var2.getKey());
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
                     if (var2.getKey() != null && var2.getKey() instanceof Long) {
                        if (var2.getValue() != null && var2.getValue() instanceof Integer) {
                           Long2IntAVLTreeMap.Entry var3 = Long2IntAVLTreeMap.this.findKey((Long)var2.getKey());
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

               public Long2IntMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Long2IntMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Long2IntMap.Entry> subSet(Long2IntMap.Entry var1, Long2IntMap.Entry var2) {
                  return Submap.this.subMap(var1.getLongKey(), var2.getLongKey()).long2IntEntrySet();
               }

               public ObjectSortedSet<Long2IntMap.Entry> headSet(Long2IntMap.Entry var1) {
                  return Submap.this.headMap(var1.getLongKey()).long2IntEntrySet();
               }

               public ObjectSortedSet<Long2IntMap.Entry> tailSet(Long2IntMap.Entry var1) {
                  return Submap.this.tailMap(var1.getLongKey()).long2IntEntrySet();
               }
            };
         }

         return this.entries;
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Long2IntAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      public IntCollection values() {
         if (this.values == null) {
            this.values = new AbstractIntCollection() {
               public IntIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               public boolean contains(int var1) {
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

      public boolean containsKey(long var1) {
         return this.in(var1) && Long2IntAVLTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(int var1) {
         Long2IntAVLTreeMap.Submap.SubmapIterator var2 = new Long2IntAVLTreeMap.Submap.SubmapIterator();

         int var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = var2.nextEntry().value;
         } while(var3 != var1);

         return true;
      }

      public int get(long var1) {
         Long2IntAVLTreeMap.Entry var3;
         return this.in(var1) && (var3 = Long2IntAVLTreeMap.this.findKey(var1)) != null ? var3.value : this.defRetValue;
      }

      public int put(long var1, int var3) {
         Long2IntAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            int var4 = Long2IntAVLTreeMap.this.put(var1, var3);
            return Long2IntAVLTreeMap.this.modified ? this.defRetValue : var4;
         }
      }

      public int remove(long var1) {
         Long2IntAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            int var3 = Long2IntAVLTreeMap.this.remove(var1);
            return Long2IntAVLTreeMap.this.modified ? var3 : this.defRetValue;
         }
      }

      public int size() {
         Long2IntAVLTreeMap.Submap.SubmapIterator var1 = new Long2IntAVLTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Long2IntAVLTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public LongComparator comparator() {
         return Long2IntAVLTreeMap.this.actualComparator;
      }

      public Long2IntSortedMap headMap(long var1) {
         if (this.top) {
            return Long2IntAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Long2IntAVLTreeMap.this.compare(var1, this.to) < 0 ? Long2IntAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Long2IntSortedMap tailMap(long var1) {
         if (this.bottom) {
            return Long2IntAVLTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Long2IntAVLTreeMap.this.compare(var1, this.from) > 0 ? Long2IntAVLTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Long2IntSortedMap subMap(long var1, long var3) {
         if (this.top && this.bottom) {
            return Long2IntAVLTreeMap.this.new Submap(var1, false, var3, false);
         } else {
            if (!this.top) {
               var3 = Long2IntAVLTreeMap.this.compare(var3, this.to) < 0 ? var3 : this.to;
            }

            if (!this.bottom) {
               var1 = Long2IntAVLTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var3 == this.to ? this : Long2IntAVLTreeMap.this.new Submap(var1, false, var3, false);
         }
      }

      public Long2IntAVLTreeMap.Entry firstEntry() {
         if (Long2IntAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Long2IntAVLTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Long2IntAVLTreeMap.this.firstEntry;
            } else {
               var1 = Long2IntAVLTreeMap.this.locateKey(this.from);
               if (Long2IntAVLTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Long2IntAVLTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Long2IntAVLTreeMap.Entry lastEntry() {
         if (Long2IntAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Long2IntAVLTreeMap.Entry var1;
            if (this.top) {
               var1 = Long2IntAVLTreeMap.this.lastEntry;
            } else {
               var1 = Long2IntAVLTreeMap.this.locateKey(this.to);
               if (Long2IntAVLTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Long2IntAVLTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public long firstLongKey() {
         Long2IntAVLTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public long lastLongKey() {
         Long2IntAVLTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Long2IntAVLTreeMap.Submap.SubmapIterator implements IntListIterator {
         private SubmapValueIterator() {
            super();
         }

         public int nextInt() {
            return this.nextEntry().value;
         }

         public int previousInt() {
            return this.previousEntry().value;
         }

         // $FF: synthetic method
         SubmapValueIterator(Object var2) {
            this();
         }
      }

      private final class SubmapKeyIterator extends Long2IntAVLTreeMap.Submap.SubmapIterator implements LongListIterator {
         public SubmapKeyIterator() {
            super();
         }

         public SubmapKeyIterator(long var2) {
            super(var2);
         }

         public long nextLong() {
            return this.nextEntry().key;
         }

         public long previousLong() {
            return this.previousEntry().key;
         }
      }

      private class SubmapEntryIterator extends Long2IntAVLTreeMap.Submap.SubmapIterator implements ObjectListIterator<Long2IntMap.Entry> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(long var2) {
            super(var2);
         }

         public Long2IntMap.Entry next() {
            return this.nextEntry();
         }

         public Long2IntMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Long2IntAVLTreeMap.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(long var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Long2IntAVLTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Long2IntAVLTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Long2IntAVLTreeMap.this.locateKey(var2);
                  if (Long2IntAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Long2IntAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Long2IntAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractLong2IntSortedMap.KeySet {
         private KeySet() {
            super();
         }

         public LongBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         public LongBidirectionalIterator iterator(long var1) {
            return Submap.this.new SubmapKeyIterator(var1);
         }

         // $FF: synthetic method
         KeySet(Object var2) {
            this();
         }
      }
   }

   private final class ValueIterator extends Long2IntAVLTreeMap.TreeIterator implements IntListIterator {
      private ValueIterator() {
         super();
      }

      public int nextInt() {
         return this.nextEntry().value;
      }

      public int previousInt() {
         return this.previousEntry().value;
      }

      // $FF: synthetic method
      ValueIterator(Object var2) {
         this();
      }
   }

   private class KeySet extends AbstractLong2IntSortedMap.KeySet {
      private KeySet() {
         super();
      }

      public LongBidirectionalIterator iterator() {
         return Long2IntAVLTreeMap.this.new KeyIterator();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return Long2IntAVLTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Long2IntAVLTreeMap.TreeIterator implements LongListIterator {
      public KeyIterator() {
         super();
      }

      public KeyIterator(long var2) {
         super(var2);
      }

      public long nextLong() {
         return this.nextEntry().key;
      }

      public long previousLong() {
         return this.previousEntry().key;
      }
   }

   private class EntryIterator extends Long2IntAVLTreeMap.TreeIterator implements ObjectListIterator<Long2IntMap.Entry> {
      EntryIterator() {
         super();
      }

      EntryIterator(long var2) {
         super(var2);
      }

      public Long2IntMap.Entry next() {
         return this.nextEntry();
      }

      public Long2IntMap.Entry previous() {
         return this.previousEntry();
      }

      public void set(Long2IntMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Long2IntMap.Entry var1) {
         throw new UnsupportedOperationException();
      }
   }

   private class TreeIterator {
      Long2IntAVLTreeMap.Entry prev;
      Long2IntAVLTreeMap.Entry next;
      Long2IntAVLTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Long2IntAVLTreeMap.this.firstEntry;
      }

      TreeIterator(long var2) {
         super();
         if ((this.next = Long2IntAVLTreeMap.this.locateKey(var2)) != null) {
            if (Long2IntAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Long2IntAVLTreeMap.Entry nextEntry() {
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

      Long2IntAVLTreeMap.Entry previousEntry() {
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
            Long2IntAVLTreeMap.this.remove(this.curr.key);
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

   private static final class Entry extends AbstractLong2IntMap.BasicEntry implements Cloneable {
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Long2IntAVLTreeMap.Entry left;
      Long2IntAVLTreeMap.Entry right;
      int info;

      Entry() {
         super(0L, 0);
      }

      Entry(long var1, int var3) {
         super(var1, var3);
         this.info = -1073741824;
      }

      Long2IntAVLTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Long2IntAVLTreeMap.Entry right() {
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

      void pred(Long2IntAVLTreeMap.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Long2IntAVLTreeMap.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Long2IntAVLTreeMap.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Long2IntAVLTreeMap.Entry var1) {
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

      Long2IntAVLTreeMap.Entry next() {
         Long2IntAVLTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Long2IntAVLTreeMap.Entry prev() {
         Long2IntAVLTreeMap.Entry var1 = this.left;
         if ((this.info & 1073741824) == 0) {
            while((var1.info & -2147483648) == 0) {
               var1 = var1.right;
            }
         }

         return var1;
      }

      public int setValue(int var1) {
         int var2 = this.value;
         this.value = var1;
         return var2;
      }

      public Long2IntAVLTreeMap.Entry clone() {
         Long2IntAVLTreeMap.Entry var1;
         try {
            var1 = (Long2IntAVLTreeMap.Entry)super.clone();
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
            return this.key == (Long)var2.getKey() && this.value == (Integer)var2.getValue();
         }
      }

      public int hashCode() {
         return HashCommon.long2int(this.key) ^ this.value;
      }

      public String toString() {
         return this.key + "=>" + this.value;
      }
   }
}
