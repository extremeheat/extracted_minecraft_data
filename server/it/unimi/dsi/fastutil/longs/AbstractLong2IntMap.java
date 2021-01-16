package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractLong2IntMap extends AbstractLong2IntFunction implements Long2IntMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2IntMap() {
      super();
   }

   public boolean containsValue(int var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(long var1) {
      ObjectIterator var3 = this.long2IntEntrySet().iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(((Long2IntMap.Entry)var3.next()).getLongKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public LongSet keySet() {
      return new AbstractLongSet() {
         public boolean contains(long var1) {
            return AbstractLong2IntMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractLong2IntMap.this.size();
         }

         public void clear() {
            AbstractLong2IntMap.this.clear();
         }

         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(AbstractLong2IntMap.this);

               public long nextLong() {
                  return ((Long2IntMap.Entry)this.i.next()).getLongKey();
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
            return AbstractLong2IntMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractLong2IntMap.this.size();
         }

         public void clear() {
            AbstractLong2IntMap.this.clear();
         }

         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(AbstractLong2IntMap.this);

               public int nextInt() {
                  return ((Long2IntMap.Entry)this.i.next()).getIntValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Long, ? extends Integer> var1) {
      if (var1 instanceof Long2IntMap) {
         ObjectIterator var2 = Long2IntMaps.fastIterator((Long2IntMap)var1);

         while(var2.hasNext()) {
            Long2IntMap.Entry var3 = (Long2IntMap.Entry)var2.next();
            this.put(var3.getLongKey(), var3.getIntValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Long)var4.getKey(), (Integer)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Long2IntMaps.fastIterator(this); var2-- != 0; var1 += ((Long2IntMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.long2IntEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Long2IntMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Long2IntMap.Entry var4 = (Long2IntMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getLongKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getIntValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Long2IntMap.Entry> {
      protected final Long2IntMap map;

      public BasicEntrySet(Long2IntMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Long2IntMap.Entry) {
            Long2IntMap.Entry var7 = (Long2IntMap.Entry)var1;
            long var8 = var7.getLongKey();
            return this.map.containsKey(var8) && this.map.get(var8) == var7.getIntValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               long var4 = (Long)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Integer) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Integer)var6;
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
         } else if (var1 instanceof Long2IntMap.Entry) {
            Long2IntMap.Entry var8 = (Long2IntMap.Entry)var1;
            return this.map.remove(var8.getLongKey(), var8.getIntValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               long var4 = (Long)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Integer) {
                  int var7 = (Integer)var6;
                  return this.map.remove(var4, var7);
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

   public static class BasicEntry implements Long2IntMap.Entry {
      protected long key;
      protected int value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Long var1, Integer var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(long var1, int var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public long getLongKey() {
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
         } else if (var1 instanceof Long2IntMap.Entry) {
            Long2IntMap.Entry var5 = (Long2IntMap.Entry)var1;
            return this.key == var5.getLongKey() && this.value == var5.getIntValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Integer) {
                  return this.key == (Long)var3 && this.value == (Integer)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.long2int(this.key) ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
