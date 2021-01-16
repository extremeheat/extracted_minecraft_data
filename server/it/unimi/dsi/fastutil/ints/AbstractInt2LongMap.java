package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractInt2LongMap extends AbstractInt2LongFunction implements Int2LongMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2LongMap() {
      super();
   }

   public boolean containsValue(long var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(int var1) {
      ObjectIterator var2 = this.int2LongEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Int2LongMap.Entry)var2.next()).getIntKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public IntSet keySet() {
      return new AbstractIntSet() {
         public boolean contains(int var1) {
            return AbstractInt2LongMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractInt2LongMap.this.size();
         }

         public void clear() {
            AbstractInt2LongMap.this.clear();
         }

         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator(AbstractInt2LongMap.this);

               public int nextInt() {
                  return ((Int2LongMap.Entry)this.i.next()).getIntKey();
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

   public LongCollection values() {
      return new AbstractLongCollection() {
         public boolean contains(long var1) {
            return AbstractInt2LongMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractInt2LongMap.this.size();
         }

         public void clear() {
            AbstractInt2LongMap.this.clear();
         }

         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Int2LongMap.Entry> i = Int2LongMaps.fastIterator(AbstractInt2LongMap.this);

               public long nextLong() {
                  return ((Int2LongMap.Entry)this.i.next()).getLongValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Integer, ? extends Long> var1) {
      if (var1 instanceof Int2LongMap) {
         ObjectIterator var2 = Int2LongMaps.fastIterator((Int2LongMap)var1);

         while(var2.hasNext()) {
            Int2LongMap.Entry var3 = (Int2LongMap.Entry)var2.next();
            this.put(var3.getIntKey(), var3.getLongValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Integer)var4.getKey(), (Long)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Int2LongMaps.fastIterator(this); var2-- != 0; var1 += ((Int2LongMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.int2LongEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Int2LongMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Int2LongMap.Entry var4 = (Int2LongMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getIntKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getLongValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2LongMap.Entry> {
      protected final Int2LongMap map;

      public BasicEntrySet(Int2LongMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Int2LongMap.Entry) {
            Int2LongMap.Entry var6 = (Int2LongMap.Entry)var1;
            int var7 = var6.getIntKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getLongValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               int var4 = (Integer)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Long) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Long)var5;
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
         } else if (var1 instanceof Int2LongMap.Entry) {
            Int2LongMap.Entry var8 = (Int2LongMap.Entry)var1;
            return this.map.remove(var8.getIntKey(), var8.getLongValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               int var4 = (Integer)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Long) {
                  long var6 = (Long)var5;
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

   public static class BasicEntry implements Int2LongMap.Entry {
      protected int key;
      protected long value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Integer var1, Long var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(int var1, long var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public int getIntKey() {
         return this.key;
      }

      public long getLongValue() {
         return this.value;
      }

      public long setValue(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Int2LongMap.Entry) {
            Int2LongMap.Entry var5 = (Int2LongMap.Entry)var1;
            return this.key == var5.getIntKey() && this.value == var5.getLongValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Long) {
                  return this.key == (Integer)var3 && this.value == (Long)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ HashCommon.long2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
