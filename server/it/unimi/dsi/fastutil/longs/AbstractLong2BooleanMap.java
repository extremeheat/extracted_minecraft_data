package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractLong2BooleanMap extends AbstractLong2BooleanFunction implements Long2BooleanMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2BooleanMap() {
      super();
   }

   public boolean containsValue(boolean var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(long var1) {
      ObjectIterator var3 = this.long2BooleanEntrySet().iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(((Long2BooleanMap.Entry)var3.next()).getLongKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public LongSet keySet() {
      return new AbstractLongSet() {
         public boolean contains(long var1) {
            return AbstractLong2BooleanMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractLong2BooleanMap.this.size();
         }

         public void clear() {
            AbstractLong2BooleanMap.this.clear();
         }

         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2BooleanMap.Entry> i = Long2BooleanMaps.fastIterator(AbstractLong2BooleanMap.this);

               public long nextLong() {
                  return ((Long2BooleanMap.Entry)this.i.next()).getLongKey();
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

   public BooleanCollection values() {
      return new AbstractBooleanCollection() {
         public boolean contains(boolean var1) {
            return AbstractLong2BooleanMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractLong2BooleanMap.this.size();
         }

         public void clear() {
            AbstractLong2BooleanMap.this.clear();
         }

         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Long2BooleanMap.Entry> i = Long2BooleanMaps.fastIterator(AbstractLong2BooleanMap.this);

               public boolean nextBoolean() {
                  return ((Long2BooleanMap.Entry)this.i.next()).getBooleanValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Long, ? extends Boolean> var1) {
      if (var1 instanceof Long2BooleanMap) {
         ObjectIterator var2 = Long2BooleanMaps.fastIterator((Long2BooleanMap)var1);

         while(var2.hasNext()) {
            Long2BooleanMap.Entry var3 = (Long2BooleanMap.Entry)var2.next();
            this.put(var3.getLongKey(), var3.getBooleanValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Long)var4.getKey(), (Boolean)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Long2BooleanMaps.fastIterator(this); var2-- != 0; var1 += ((Long2BooleanMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.long2BooleanEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Long2BooleanMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Long2BooleanMap.Entry var4 = (Long2BooleanMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getLongKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getBooleanValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Long2BooleanMap.Entry> {
      protected final Long2BooleanMap map;

      public BasicEntrySet(Long2BooleanMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Long2BooleanMap.Entry) {
            Long2BooleanMap.Entry var7 = (Long2BooleanMap.Entry)var1;
            long var8 = var7.getLongKey();
            return this.map.containsKey(var8) && this.map.get(var8) == var7.getBooleanValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               long var4 = (Long)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Boolean) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Boolean)var6;
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
         } else if (var1 instanceof Long2BooleanMap.Entry) {
            Long2BooleanMap.Entry var8 = (Long2BooleanMap.Entry)var1;
            return this.map.remove(var8.getLongKey(), var8.getBooleanValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               long var4 = (Long)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Boolean) {
                  boolean var7 = (Boolean)var6;
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

   public static class BasicEntry implements Long2BooleanMap.Entry {
      protected long key;
      protected boolean value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Long var1, Boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(long var1, boolean var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public long getLongKey() {
         return this.key;
      }

      public boolean getBooleanValue() {
         return this.value;
      }

      public boolean setValue(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Long2BooleanMap.Entry) {
            Long2BooleanMap.Entry var5 = (Long2BooleanMap.Entry)var1;
            return this.key == var5.getLongKey() && this.value == var5.getBooleanValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Boolean) {
                  return this.key == (Long)var3 && this.value == (Boolean)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.long2int(this.key) ^ (this.value ? 1231 : 1237);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
