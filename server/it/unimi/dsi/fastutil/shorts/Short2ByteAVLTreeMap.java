package it.unimi.dsi.fastutil.shorts;

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

public class Short2ByteAVLTreeMap extends AbstractShort2ByteSortedMap implements Serializable, Cloneable {
   protected transient Short2ByteAVLTreeMap.Entry tree;
   protected int count;
   protected transient Short2ByteAVLTreeMap.Entry firstEntry;
   protected transient Short2ByteAVLTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Short2ByteMap.Entry> entries;
   protected transient ShortSortedSet keys;
   protected transient ByteCollection values;
   protected transient boolean modified;
   protected Comparator<? super Short> storedComparator;
   protected transient ShortComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Short2ByteAVLTreeMap() {
      super();
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = ShortComparators.asShortComparator(this.storedComparator);
   }

   public Short2ByteAVLTreeMap(Comparator<? super Short> var1) {
      this();
      this.storedComparator = var1;
      this.setActualComparator();
   }

   public Short2ByteAVLTreeMap(Map<? extends Short, ? extends Byte> var1) {
      this();
      this.putAll(var1);
   }

   public Short2ByteAVLTreeMap(SortedMap<Short, Byte> var1) {
      this(var1.comparator());
      this.putAll(var1);
   }

   public Short2ByteAVLTreeMap(Short2ByteMap var1) {
      this();
      this.putAll(var1);
   }

   public Short2ByteAVLTreeMap(Short2ByteSortedMap var1) {
      this((Comparator)var1.comparator());
      this.putAll(var1);
   }

   public Short2ByteAVLTreeMap(short[] var1, byte[] var2, Comparator<? super Short> var3) {
      this(var3);
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + var1.length + " and " + var2.length + ")");
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            this.put(var1[var4], var2[var4]);
         }

      }
   }

   public Short2ByteAVLTreeMap(short[] var1, byte[] var2) {
      this(var1, var2, (Comparator)null);
   }

   final int compare(short var1, short var2) {
      return this.actualComparator == null ? Short.compare(var1, var2) : this.actualComparator.compare(var1, var2);
   }

   final Short2ByteAVLTreeMap.Entry findKey(short var1) {
      Short2ByteAVLTreeMap.Entry var2;
      int var3;
      for(var2 = this.tree; var2 != null && (var3 = this.compare(var1, var2.key)) != 0; var2 = var3 < 0 ? var2.left() : var2.right()) {
      }

      return var2;
   }

   final Short2ByteAVLTreeMap.Entry locateKey(short var1) {
      Short2ByteAVLTreeMap.Entry var2 = this.tree;
      Short2ByteAVLTreeMap.Entry var3 = this.tree;

      int var4;
      for(var4 = 0; var2 != null && (var4 = this.compare(var1, var2.key)) != 0; var2 = var4 < 0 ? var2.left() : var2.right()) {
         var3 = var2;
      }

      return var4 == 0 ? var2 : var3;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public byte addTo(short var1, byte var2) {
      Short2ByteAVLTreeMap.Entry var3 = this.add(var1);
      byte var4 = var3.value;
      var3.value += var2;
      return var4;
   }

   public byte put(short var1, byte var2) {
      Short2ByteAVLTreeMap.Entry var3 = this.add(var1);
      byte var4 = var3.value;
      var3.value = var2;
      return var4;
   }

   private Short2ByteAVLTreeMap.Entry add(short var1) {
      this.modified = false;
      Short2ByteAVLTreeMap.Entry var2 = null;
      if (this.tree == null) {
         ++this.count;
         var2 = this.tree = this.lastEntry = this.firstEntry = new Short2ByteAVLTreeMap.Entry(var1, this.defRetValue);
         this.modified = true;
      } else {
         Short2ByteAVLTreeMap.Entry var3 = this.tree;
         Short2ByteAVLTreeMap.Entry var4 = null;
         Short2ByteAVLTreeMap.Entry var5 = this.tree;
         Short2ByteAVLTreeMap.Entry var6 = null;
         Short2ByteAVLTreeMap.Entry var7 = null;
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
                  var2 = new Short2ByteAVLTreeMap.Entry(var1, this.defRetValue);
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
                  var2 = new Short2ByteAVLTreeMap.Entry(var1, this.defRetValue);
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

         Short2ByteAVLTreeMap.Entry var10;
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

   private Short2ByteAVLTreeMap.Entry parent(Short2ByteAVLTreeMap.Entry var1) {
      if (var1 == this.tree) {
         return null;
      } else {
         Short2ByteAVLTreeMap.Entry var3 = var1;

         Short2ByteAVLTreeMap.Entry var2;
         Short2ByteAVLTreeMap.Entry var4;
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

   public byte remove(short var1) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Short2ByteAVLTreeMap.Entry var3 = this.tree;
         Short2ByteAVLTreeMap.Entry var4 = null;
         boolean var5 = false;
         short var6 = var1;

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

         Short2ByteAVLTreeMap.Entry var7;
         Short2ByteAVLTreeMap.Entry var8;
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
            Short2ByteAVLTreeMap.Entry var9;
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

   public boolean containsValue(byte var1) {
      Short2ByteAVLTreeMap.ValueIterator var2 = new Short2ByteAVLTreeMap.ValueIterator();
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

   public boolean containsKey(short var1) {
      return this.findKey(var1) != null;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public byte get(short var1) {
      Short2ByteAVLTreeMap.Entry var2 = this.findKey(var1);
      return var2 == null ? this.defRetValue : var2.value;
   }

   public short firstShortKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   public short lastShortKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   public ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Short2ByteMap.Entry>() {
            final Comparator<? super Short2ByteMap.Entry> comparator = (var1x, var2) -> {
               return Short2ByteAVLTreeMap.this.actualComparator.compare(var1x.getShortKey(), var2.getShortKey());
            };

            public Comparator<? super Short2ByteMap.Entry> comparator() {
               return this.comparator;
            }

            public ObjectBidirectionalIterator<Short2ByteMap.Entry> iterator() {
               return Short2ByteAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Short2ByteMap.Entry> iterator(Short2ByteMap.Entry var1) {
               return Short2ByteAVLTreeMap.this.new EntryIterator(var1.getShortKey());
            }

            public boolean contains(Object var1) {
               if (!(var1 instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                  if (var2.getKey() != null && var2.getKey() instanceof Short) {
                     if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                        Short2ByteAVLTreeMap.Entry var3 = Short2ByteAVLTreeMap.this.findKey((Short)var2.getKey());
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
                  if (var2.getKey() != null && var2.getKey() instanceof Short) {
                     if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                        Short2ByteAVLTreeMap.Entry var3 = Short2ByteAVLTreeMap.this.findKey((Short)var2.getKey());
                        if (var3 != null && var3.getByteValue() == (Byte)var2.getValue()) {
                           Short2ByteAVLTreeMap.this.remove(var3.key);
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
               return Short2ByteAVLTreeMap.this.count;
            }

            public void clear() {
               Short2ByteAVLTreeMap.this.clear();
            }

            public Short2ByteMap.Entry first() {
               return Short2ByteAVLTreeMap.this.firstEntry;
            }

            public Short2ByteMap.Entry last() {
               return Short2ByteAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Short2ByteMap.Entry> subSet(Short2ByteMap.Entry var1, Short2ByteMap.Entry var2) {
               return Short2ByteAVLTreeMap.this.subMap(var1.getShortKey(), var2.getShortKey()).short2ByteEntrySet();
            }

            public ObjectSortedSet<Short2ByteMap.Entry> headSet(Short2ByteMap.Entry var1) {
               return Short2ByteAVLTreeMap.this.headMap(var1.getShortKey()).short2ByteEntrySet();
            }

            public ObjectSortedSet<Short2ByteMap.Entry> tailSet(Short2ByteMap.Entry var1) {
               return Short2ByteAVLTreeMap.this.tailMap(var1.getShortKey()).short2ByteEntrySet();
            }
         };
      }

      return this.entries;
   }

   public ShortSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Short2ByteAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   public ByteCollection values() {
      if (this.values == null) {
         this.values = new AbstractByteCollection() {
            public ByteIterator iterator() {
               return Short2ByteAVLTreeMap.this.new ValueIterator();
            }

            public boolean contains(byte var1) {
               return Short2ByteAVLTreeMap.this.containsValue(var1);
            }

            public int size() {
               return Short2ByteAVLTreeMap.this.count;
            }

            public void clear() {
               Short2ByteAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public ShortComparator comparator() {
      return this.actualComparator;
   }

   public Short2ByteSortedMap headMap(short var1) {
      return new Short2ByteAVLTreeMap.Submap((short)0, true, var1, false);
   }

   public Short2ByteSortedMap tailMap(short var1) {
      return new Short2ByteAVLTreeMap.Submap(var1, false, (short)0, true);
   }

   public Short2ByteSortedMap subMap(short var1, short var2) {
      return new Short2ByteAVLTreeMap.Submap(var1, false, var2, false);
   }

   public Short2ByteAVLTreeMap clone() {
      Short2ByteAVLTreeMap var1;
      try {
         var1 = (Short2ByteAVLTreeMap)super.clone();
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
         Short2ByteAVLTreeMap.Entry var5 = new Short2ByteAVLTreeMap.Entry();
         Short2ByteAVLTreeMap.Entry var6 = new Short2ByteAVLTreeMap.Entry();
         Short2ByteAVLTreeMap.Entry var3 = var5;
         var5.left(this.tree);
         Short2ByteAVLTreeMap.Entry var4 = var6;
         var6.pred((Short2ByteAVLTreeMap.Entry)null);

         while(true) {
            Short2ByteAVLTreeMap.Entry var2;
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
      Short2ByteAVLTreeMap.EntryIterator var3 = new Short2ByteAVLTreeMap.EntryIterator();
      var1.defaultWriteObject();

      while(var2-- != 0) {
         Short2ByteAVLTreeMap.Entry var4 = var3.nextEntry();
         var1.writeShort(var4.key);
         var1.writeByte(var4.value);
      }

   }

   private Short2ByteAVLTreeMap.Entry readTree(ObjectInputStream var1, int var2, Short2ByteAVLTreeMap.Entry var3, Short2ByteAVLTreeMap.Entry var4) throws IOException, ClassNotFoundException {
      Short2ByteAVLTreeMap.Entry var8;
      if (var2 == 1) {
         var8 = new Short2ByteAVLTreeMap.Entry(var1.readShort(), var1.readByte());
         var8.pred(var3);
         var8.succ(var4);
         return var8;
      } else if (var2 == 2) {
         var8 = new Short2ByteAVLTreeMap.Entry(var1.readShort(), var1.readByte());
         var8.right(new Short2ByteAVLTreeMap.Entry(var1.readShort(), var1.readByte()));
         var8.right.pred(var8);
         var8.balance(1);
         var8.pred(var3);
         var8.right.succ(var4);
         return var8;
      } else {
         int var5 = var2 / 2;
         int var6 = var2 - var5 - 1;
         Short2ByteAVLTreeMap.Entry var7 = new Short2ByteAVLTreeMap.Entry();
         var7.left(this.readTree(var1, var6, var3, var7));
         var7.key = var1.readShort();
         var7.value = var1.readByte();
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
         this.tree = this.readTree(var1, this.count, (Short2ByteAVLTreeMap.Entry)null, (Short2ByteAVLTreeMap.Entry)null);

         Short2ByteAVLTreeMap.Entry var2;
         for(var2 = this.tree; var2.left() != null; var2 = var2.left()) {
         }

         this.firstEntry = var2;

         for(var2 = this.tree; var2.right() != null; var2 = var2.right()) {
         }

         this.lastEntry = var2;
      }

   }

   private final class Submap extends AbstractShort2ByteSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      short from;
      short to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Short2ByteMap.Entry> entries;
      protected transient ShortSortedSet keys;
      protected transient ByteCollection values;

      public Submap(short var2, boolean var3, short var4, boolean var5) {
         super();
         if (!var3 && !var5 && Short2ByteAVLTreeMap.this.compare(var2, var4) > 0) {
            throw new IllegalArgumentException("Start key (" + var2 + ") is larger than end key (" + var4 + ")");
         } else {
            this.from = var2;
            this.bottom = var3;
            this.to = var4;
            this.top = var5;
            this.defRetValue = Short2ByteAVLTreeMap.this.defRetValue;
         }
      }

      public void clear() {
         Short2ByteAVLTreeMap.Submap.SubmapIterator var1 = new Short2ByteAVLTreeMap.Submap.SubmapIterator();

         while(var1.hasNext()) {
            var1.nextEntry();
            var1.remove();
         }

      }

      final boolean in(short var1) {
         return (this.bottom || Short2ByteAVLTreeMap.this.compare(var1, this.from) >= 0) && (this.top || Short2ByteAVLTreeMap.this.compare(var1, this.to) < 0);
      }

      public ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Short2ByteMap.Entry>() {
               public ObjectBidirectionalIterator<Short2ByteMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Short2ByteMap.Entry> iterator(Short2ByteMap.Entry var1) {
                  return Submap.this.new SubmapEntryIterator(var1.getShortKey());
               }

               public Comparator<? super Short2ByteMap.Entry> comparator() {
                  return Short2ByteAVLTreeMap.this.short2ByteEntrySet().comparator();
               }

               public boolean contains(Object var1) {
                  if (!(var1 instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
                     if (var2.getKey() != null && var2.getKey() instanceof Short) {
                        if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                           Short2ByteAVLTreeMap.Entry var3 = Short2ByteAVLTreeMap.this.findKey((Short)var2.getKey());
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
                     if (var2.getKey() != null && var2.getKey() instanceof Short) {
                        if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                           Short2ByteAVLTreeMap.Entry var3 = Short2ByteAVLTreeMap.this.findKey((Short)var2.getKey());
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

               public Short2ByteMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Short2ByteMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Short2ByteMap.Entry> subSet(Short2ByteMap.Entry var1, Short2ByteMap.Entry var2) {
                  return Submap.this.subMap(var1.getShortKey(), var2.getShortKey()).short2ByteEntrySet();
               }

               public ObjectSortedSet<Short2ByteMap.Entry> headSet(Short2ByteMap.Entry var1) {
                  return Submap.this.headMap(var1.getShortKey()).short2ByteEntrySet();
               }

               public ObjectSortedSet<Short2ByteMap.Entry> tailSet(Short2ByteMap.Entry var1) {
                  return Submap.this.tailMap(var1.getShortKey()).short2ByteEntrySet();
               }
            };
         }

         return this.entries;
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Short2ByteAVLTreeMap.Submap.KeySet();
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

      public boolean containsKey(short var1) {
         return this.in(var1) && Short2ByteAVLTreeMap.this.containsKey(var1);
      }

      public boolean containsValue(byte var1) {
         Short2ByteAVLTreeMap.Submap.SubmapIterator var2 = new Short2ByteAVLTreeMap.Submap.SubmapIterator();

         byte var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = var2.nextEntry().value;
         } while(var3 != var1);

         return true;
      }

      public byte get(short var1) {
         Short2ByteAVLTreeMap.Entry var2;
         return this.in(var1) && (var2 = Short2ByteAVLTreeMap.this.findKey(var1)) != null ? var2.value : this.defRetValue;
      }

      public byte put(short var1, byte var2) {
         Short2ByteAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            throw new IllegalArgumentException("Key (" + var1 + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
         } else {
            byte var3 = Short2ByteAVLTreeMap.this.put(var1, var2);
            return Short2ByteAVLTreeMap.this.modified ? this.defRetValue : var3;
         }
      }

      public byte remove(short var1) {
         Short2ByteAVLTreeMap.this.modified = false;
         if (!this.in(var1)) {
            return this.defRetValue;
         } else {
            byte var2 = Short2ByteAVLTreeMap.this.remove(var1);
            return Short2ByteAVLTreeMap.this.modified ? var2 : this.defRetValue;
         }
      }

      public int size() {
         Short2ByteAVLTreeMap.Submap.SubmapIterator var1 = new Short2ByteAVLTreeMap.Submap.SubmapIterator();
         int var2 = 0;

         while(var1.hasNext()) {
            ++var2;
            var1.nextEntry();
         }

         return var2;
      }

      public boolean isEmpty() {
         return !(new Short2ByteAVLTreeMap.Submap.SubmapIterator()).hasNext();
      }

      public ShortComparator comparator() {
         return Short2ByteAVLTreeMap.this.actualComparator;
      }

      public Short2ByteSortedMap headMap(short var1) {
         if (this.top) {
            return Short2ByteAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false);
         } else {
            return Short2ByteAVLTreeMap.this.compare(var1, this.to) < 0 ? Short2ByteAVLTreeMap.this.new Submap(this.from, this.bottom, var1, false) : this;
         }
      }

      public Short2ByteSortedMap tailMap(short var1) {
         if (this.bottom) {
            return Short2ByteAVLTreeMap.this.new Submap(var1, false, this.to, this.top);
         } else {
            return Short2ByteAVLTreeMap.this.compare(var1, this.from) > 0 ? Short2ByteAVLTreeMap.this.new Submap(var1, false, this.to, this.top) : this;
         }
      }

      public Short2ByteSortedMap subMap(short var1, short var2) {
         if (this.top && this.bottom) {
            return Short2ByteAVLTreeMap.this.new Submap(var1, false, var2, false);
         } else {
            if (!this.top) {
               var2 = Short2ByteAVLTreeMap.this.compare(var2, this.to) < 0 ? var2 : this.to;
            }

            if (!this.bottom) {
               var1 = Short2ByteAVLTreeMap.this.compare(var1, this.from) > 0 ? var1 : this.from;
            }

            return !this.top && !this.bottom && var1 == this.from && var2 == this.to ? this : Short2ByteAVLTreeMap.this.new Submap(var1, false, var2, false);
         }
      }

      public Short2ByteAVLTreeMap.Entry firstEntry() {
         if (Short2ByteAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Short2ByteAVLTreeMap.Entry var1;
            if (this.bottom) {
               var1 = Short2ByteAVLTreeMap.this.firstEntry;
            } else {
               var1 = Short2ByteAVLTreeMap.this.locateKey(this.from);
               if (Short2ByteAVLTreeMap.this.compare(var1.key, this.from) < 0) {
                  var1 = var1.next();
               }
            }

            return var1 != null && (this.top || Short2ByteAVLTreeMap.this.compare(var1.key, this.to) < 0) ? var1 : null;
         }
      }

      public Short2ByteAVLTreeMap.Entry lastEntry() {
         if (Short2ByteAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Short2ByteAVLTreeMap.Entry var1;
            if (this.top) {
               var1 = Short2ByteAVLTreeMap.this.lastEntry;
            } else {
               var1 = Short2ByteAVLTreeMap.this.locateKey(this.to);
               if (Short2ByteAVLTreeMap.this.compare(var1.key, this.to) >= 0) {
                  var1 = var1.prev();
               }
            }

            return var1 != null && (this.bottom || Short2ByteAVLTreeMap.this.compare(var1.key, this.from) >= 0) ? var1 : null;
         }
      }

      public short firstShortKey() {
         Short2ByteAVLTreeMap.Entry var1 = this.firstEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      public short lastShortKey() {
         Short2ByteAVLTreeMap.Entry var1 = this.lastEntry();
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1.key;
         }
      }

      private final class SubmapValueIterator extends Short2ByteAVLTreeMap.Submap.SubmapIterator implements ByteListIterator {
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

      private final class SubmapKeyIterator extends Short2ByteAVLTreeMap.Submap.SubmapIterator implements ShortListIterator {
         public SubmapKeyIterator() {
            super();
         }

         public SubmapKeyIterator(short var2) {
            super(var2);
         }

         public short nextShort() {
            return this.nextEntry().key;
         }

         public short previousShort() {
            return this.previousEntry().key;
         }
      }

      private class SubmapEntryIterator extends Short2ByteAVLTreeMap.Submap.SubmapIterator implements ObjectListIterator<Short2ByteMap.Entry> {
         SubmapEntryIterator() {
            super();
         }

         SubmapEntryIterator(short var2) {
            super(var2);
         }

         public Short2ByteMap.Entry next() {
            return this.nextEntry();
         }

         public Short2ByteMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Short2ByteAVLTreeMap.TreeIterator {
         SubmapIterator() {
            super();
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(short var2) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Short2ByteAVLTreeMap.this.compare(var2, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Short2ByteAVLTreeMap.this.compare(var2, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Short2ByteAVLTreeMap.this.locateKey(var2);
                  if (Short2ByteAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Short2ByteAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }

         }

         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Short2ByteAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }

         }
      }

      private class KeySet extends AbstractShort2ByteSortedMap.KeySet {
         private KeySet() {
            super();
         }

         public ShortBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         public ShortBidirectionalIterator iterator(short var1) {
            return Submap.this.new SubmapKeyIterator(var1);
         }

         // $FF: synthetic method
         KeySet(Object var2) {
            this();
         }
      }
   }

   private final class ValueIterator extends Short2ByteAVLTreeMap.TreeIterator implements ByteListIterator {
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

   private class KeySet extends AbstractShort2ByteSortedMap.KeySet {
      private KeySet() {
         super();
      }

      public ShortBidirectionalIterator iterator() {
         return Short2ByteAVLTreeMap.this.new KeyIterator();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return Short2ByteAVLTreeMap.this.new KeyIterator(var1);
      }

      // $FF: synthetic method
      KeySet(Object var2) {
         this();
      }
   }

   private final class KeyIterator extends Short2ByteAVLTreeMap.TreeIterator implements ShortListIterator {
      public KeyIterator() {
         super();
      }

      public KeyIterator(short var2) {
         super(var2);
      }

      public short nextShort() {
         return this.nextEntry().key;
      }

      public short previousShort() {
         return this.previousEntry().key;
      }
   }

   private class EntryIterator extends Short2ByteAVLTreeMap.TreeIterator implements ObjectListIterator<Short2ByteMap.Entry> {
      EntryIterator() {
         super();
      }

      EntryIterator(short var2) {
         super(var2);
      }

      public Short2ByteMap.Entry next() {
         return this.nextEntry();
      }

      public Short2ByteMap.Entry previous() {
         return this.previousEntry();
      }

      public void set(Short2ByteMap.Entry var1) {
         throw new UnsupportedOperationException();
      }

      public void add(Short2ByteMap.Entry var1) {
         throw new UnsupportedOperationException();
      }
   }

   private class TreeIterator {
      Short2ByteAVLTreeMap.Entry prev;
      Short2ByteAVLTreeMap.Entry next;
      Short2ByteAVLTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         super();
         this.next = Short2ByteAVLTreeMap.this.firstEntry;
      }

      TreeIterator(short var2) {
         super();
         if ((this.next = Short2ByteAVLTreeMap.this.locateKey(var2)) != null) {
            if (Short2ByteAVLTreeMap.this.compare(this.next.key, var2) <= 0) {
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

      Short2ByteAVLTreeMap.Entry nextEntry() {
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

      Short2ByteAVLTreeMap.Entry previousEntry() {
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
            Short2ByteAVLTreeMap.this.remove(this.curr.key);
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

   private static final class Entry extends AbstractShort2ByteMap.BasicEntry implements Cloneable {
      private static final int SUCC_MASK = -2147483648;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Short2ByteAVLTreeMap.Entry left;
      Short2ByteAVLTreeMap.Entry right;
      int info;

      Entry() {
         super((short)0, (byte)0);
      }

      Entry(short var1, byte var2) {
         super(var1, var2);
         this.info = -1073741824;
      }

      Short2ByteAVLTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Short2ByteAVLTreeMap.Entry right() {
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

      void pred(Short2ByteAVLTreeMap.Entry var1) {
         this.info |= 1073741824;
         this.left = var1;
      }

      void succ(Short2ByteAVLTreeMap.Entry var1) {
         this.info |= -2147483648;
         this.right = var1;
      }

      void left(Short2ByteAVLTreeMap.Entry var1) {
         this.info &= -1073741825;
         this.left = var1;
      }

      void right(Short2ByteAVLTreeMap.Entry var1) {
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

      Short2ByteAVLTreeMap.Entry next() {
         Short2ByteAVLTreeMap.Entry var1 = this.right;
         if ((this.info & -2147483648) == 0) {
            while((var1.info & 1073741824) == 0) {
               var1 = var1.left;
            }
         }

         return var1;
      }

      Short2ByteAVLTreeMap.Entry prev() {
         Short2ByteAVLTreeMap.Entry var1 = this.left;
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

      public Short2ByteAVLTreeMap.Entry clone() {
         Short2ByteAVLTreeMap.Entry var1;
         try {
            var1 = (Short2ByteAVLTreeMap.Entry)super.clone();
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
            return this.key == (Short)var2.getKey() && this.value == (Byte)var2.getValue();
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
