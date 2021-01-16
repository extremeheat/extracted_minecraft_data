package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractInt2IntMap extends AbstractInt2IntFunction implements Int2IntMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2IntMap() {
      super();
   }

   public boolean containsValue(int var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(int var1) {
      ObjectIterator var2 = this.int2IntEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Int2IntMap.Entry)var2.next()).getIntKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public IntSet keySet() {
      return new AbstractIntSet() {
         public boolean contains(int var1) {
            return AbstractInt2IntMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractInt2IntMap.this.size();
         }

         public void clear() {
            AbstractInt2IntMap.this.clear();
         }

         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator(AbstractInt2IntMap.this);

               public int nextInt() {
                  return ((Int2IntMap.Entry)this.i.next()).getIntKey();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }

               public void remove() {
                  this.i.remove();
               }
            };
         }
      };
   }

   public IntCollection values() {
      return new AbstractIntCollection() {
         public boolean contains(int var1) {
            return AbstractInt2IntMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractInt2IntMap.this.size();
         }

         public void clear() {
            AbstractInt2IntMap.this.clear();
         }

         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2IntMap.Entry> i = Int2IntMaps.fastIterator(AbstractInt2IntMap.this);

               public int nextInt() {
                  return ((Int2IntMap.Entry)this.i.next()).getIntValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Integer, ? extends Integer> var1) {
      if (var1 instanceof Int2IntMap) {
         ObjectIterator var2 = Int2IntMaps.fastIterator((Int2IntMap)var1);

         while(var2.hasNext()) {
            Int2IntMap.Entry var3 = (Int2IntMap.Entry)var2.next();
            this.put(var3.getIntKey(), var3.getIntValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Integer)var4.getKey(), (Integer)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Int2IntMaps.fastIterator(this); var2-- != 0; var1 += ((Int2IntMap.Entry)var3.next()).hashCode()) {
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Map)) {
         return false;
      } else {
         Map var2 = (Map)var1;
         return var2.size() != this.size() ? false : this.int2IntEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Int2IntMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Int2IntMap.Entry var4 = (Int2IntMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getIntKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getIntValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2IntMap.Entry> {
      protected final Int2IntMap map;

      public BasicEntrySet(Int2IntMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Int2IntMap.Entry) {
            Int2IntMap.Entry var6 = (Int2IntMap.Entry)var1;
            int var7 = var6.getIntKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getIntValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               int var4 = (Integer)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Integer) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Integer)var5;
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
         } else if (var1 instanceof Int2IntMap.Entry) {
            Int2IntMap.Entry var7 = (Int2IntMap.Entry)var1;
            return this.map.remove(var7.getIntKey(), var7.getIntValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               int var4 = (Integer)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Integer) {
                  int var6 = (Integer)var5;
                  return this.map.remove(var4, var6);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry implements Int2IntMap.Entry {
      protected int key;
      protected int value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Integer var1, Integer var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(int var1, int var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public int getIntKey() {
         return this.key;
      }

      public int getIntValue() {
         return this.value;
      }

      public int setValue(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Int2IntMap.Entry) {
            Int2IntMap.Entry var5 = (Int2IntMap.Entry)var1;
            return this.key == var5.getIntKey() && this.value == var5.getIntValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Integer) {
                  return this.key == (Integer)var3 && this.value == (Integer)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
