package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractLong2LongMap extends AbstractLong2LongFunction implements Long2LongMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2LongMap() {
      super();
   }

   public boolean containsValue(long var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(long var1) {
      ObjectIterator var3 = this.long2LongEntrySet().iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(((Long2LongMap.Entry)var3.next()).getLongKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public LongSet keySet() {
      return new AbstractLongSet() {
         public boolean contains(long var1) {
            return AbstractLong2LongMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractLong2LongMap.this.size();
         }

         public void clear() {
            AbstractLong2LongMap.this.clear();
         }

         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2LongMap.Entry> i = Long2LongMaps.fastIterator(AbstractLong2LongMap.this);

               public long nextLong() {
                  return ((Long2LongMap.Entry)this.i.next()).getLongKey();
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
            return AbstractLong2LongMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractLong2LongMap.this.size();
         }

         public void clear() {
            AbstractLong2LongMap.this.clear();
         }

         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2LongMap.Entry> i = Long2LongMaps.fastIterator(AbstractLong2LongMap.this);

               public long nextLong() {
                  return ((Long2LongMap.Entry)this.i.next()).getLongValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Long, ? extends Long> var1) {
      if (var1 instanceof Long2LongMap) {
         ObjectIterator var2 = Long2LongMaps.fastIterator((Long2LongMap)var1);

         while(var2.hasNext()) {
            Long2LongMap.Entry var3 = (Long2LongMap.Entry)var2.next();
            this.put(var3.getLongKey(), var3.getLongValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Long)var4.getKey(), (Long)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Long2LongMaps.fastIterator(this); var2-- != 0; var1 += ((Long2LongMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.long2LongEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Long2LongMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Long2LongMap.Entry var4 = (Long2LongMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getLongKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getLongValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Long2LongMap.Entry> {
      protected final Long2LongMap map;

      public BasicEntrySet(Long2LongMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Long2LongMap.Entry) {
            Long2LongMap.Entry var7 = (Long2LongMap.Entry)var1;
            long var8 = var7.getLongKey();
            return this.map.containsKey(var8) && this.map.get(var8) == var7.getLongValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               long var4 = (Long)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Long) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Long)var6;
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
         } else if (var1 instanceof Long2LongMap.Entry) {
            Long2LongMap.Entry var9 = (Long2LongMap.Entry)var1;
            return this.map.remove(var9.getLongKey(), var9.getLongValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               long var4 = (Long)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Long) {
                  long var7 = (Long)var6;
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

   public static class BasicEntry implements Long2LongMap.Entry {
      protected long key;
      protected long value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Long var1, Long var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(long var1, long var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public long getLongKey() {
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
         } else if (var1 instanceof Long2LongMap.Entry) {
            Long2LongMap.Entry var5 = (Long2LongMap.Entry)var1;
            return this.key == var5.getLongKey() && this.value == var5.getLongValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Long) {
                  return this.key == (Long)var3 && this.value == (Long)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.long2int(this.key) ^ HashCommon.long2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
