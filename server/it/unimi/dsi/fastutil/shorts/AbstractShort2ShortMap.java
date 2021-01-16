package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractShort2ShortMap extends AbstractShort2ShortFunction implements Short2ShortMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractShort2ShortMap() {
      super();
   }

   public boolean containsValue(short var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(short var1) {
      ObjectIterator var2 = this.short2ShortEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Short2ShortMap.Entry)var2.next()).getShortKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ShortSet keySet() {
      return new AbstractShortSet() {
         public boolean contains(short var1) {
            return AbstractShort2ShortMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractShort2ShortMap.this.size();
         }

         public void clear() {
            AbstractShort2ShortMap.this.clear();
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator(AbstractShort2ShortMap.this);

               public short nextShort() {
                  return ((Short2ShortMap.Entry)this.i.next()).getShortKey();
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

   public ShortCollection values() {
      return new AbstractShortCollection() {
         public boolean contains(short var1) {
            return AbstractShort2ShortMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractShort2ShortMap.this.size();
         }

         public void clear() {
            AbstractShort2ShortMap.this.clear();
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2ShortMap.Entry> i = Short2ShortMaps.fastIterator(AbstractShort2ShortMap.this);

               public short nextShort() {
                  return ((Short2ShortMap.Entry)this.i.next()).getShortValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Short, ? extends Short> var1) {
      if (var1 instanceof Short2ShortMap) {
         ObjectIterator var2 = Short2ShortMaps.fastIterator((Short2ShortMap)var1);

         while(var2.hasNext()) {
            Short2ShortMap.Entry var3 = (Short2ShortMap.Entry)var2.next();
            this.put(var3.getShortKey(), var3.getShortValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Short)var4.getKey(), (Short)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Short2ShortMaps.fastIterator(this); var2-- != 0; var1 += ((Short2ShortMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.short2ShortEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Short2ShortMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Short2ShortMap.Entry var4 = (Short2ShortMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getShortKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getShortValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Short2ShortMap.Entry> {
      protected final Short2ShortMap map;

      public BasicEntrySet(Short2ShortMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Short2ShortMap.Entry) {
            Short2ShortMap.Entry var6 = (Short2ShortMap.Entry)var1;
            short var7 = var6.getShortKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getShortValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Short) {
               short var4 = (Short)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Short) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Short)var5;
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
         } else if (var1 instanceof Short2ShortMap.Entry) {
            Short2ShortMap.Entry var7 = (Short2ShortMap.Entry)var1;
            return this.map.remove(var7.getShortKey(), var7.getShortValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Short) {
               short var4 = (Short)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Short) {
                  short var6 = (Short)var5;
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

   public static class BasicEntry implements Short2ShortMap.Entry {
      protected short key;
      protected short value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Short var1, Short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(short var1, short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public short getShortKey() {
         return this.key;
      }

      public short getShortValue() {
         return this.value;
      }

      public short setValue(short var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Short2ShortMap.Entry) {
            Short2ShortMap.Entry var5 = (Short2ShortMap.Entry)var1;
            return this.key == var5.getShortKey() && this.value == var5.getShortValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Short) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Short) {
                  return this.key == (Short)var3 && this.value == (Short)var4;
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
