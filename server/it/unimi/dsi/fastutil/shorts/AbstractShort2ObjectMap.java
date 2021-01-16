package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractShort2ObjectMap<V> extends AbstractShort2ObjectFunction<V> implements Short2ObjectMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractShort2ObjectMap() {
      super();
   }

   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(short var1) {
      ObjectIterator var2 = this.short2ObjectEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Short2ObjectMap.Entry)var2.next()).getShortKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ShortSet keySet() {
      return new AbstractShortSet() {
         public boolean contains(short var1) {
            return AbstractShort2ObjectMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractShort2ObjectMap.this.size();
         }

         public void clear() {
            AbstractShort2ObjectMap.this.clear();
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Short2ObjectMap.Entry<V>> i = Short2ObjectMaps.fastIterator(AbstractShort2ObjectMap.this);

               public short nextShort() {
                  return ((Short2ObjectMap.Entry)this.i.next()).getShortKey();
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

   public ObjectCollection<V> values() {
      return new AbstractObjectCollection<V>() {
         public boolean contains(Object var1) {
            return AbstractShort2ObjectMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractShort2ObjectMap.this.size();
         }

         public void clear() {
            AbstractShort2ObjectMap.this.clear();
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Short2ObjectMap.Entry<V>> i = Short2ObjectMaps.fastIterator(AbstractShort2ObjectMap.this);

               public V next() {
                  return ((Short2ObjectMap.Entry)this.i.next()).getValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Short, ? extends V> var1) {
      if (var1 instanceof Short2ObjectMap) {
         ObjectIterator var2 = Short2ObjectMaps.fastIterator((Short2ObjectMap)var1);

         while(var2.hasNext()) {
            Short2ObjectMap.Entry var3 = (Short2ObjectMap.Entry)var2.next();
            this.put(var3.getShortKey(), var3.getValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Short)var4.getKey(), var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Short2ObjectMaps.fastIterator(this); var2-- != 0; var1 += ((Short2ObjectMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.short2ObjectEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Short2ObjectMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Short2ObjectMap.Entry var4 = (Short2ObjectMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getShortKey()));
         var1.append("=>");
         if (this == var4.getValue()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getValue()));
         }
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Short2ObjectMap.Entry<V>> {
      protected final Short2ObjectMap<V> map;

      public BasicEntrySet(Short2ObjectMap<V> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Short2ObjectMap.Entry) {
            Short2ObjectMap.Entry var6 = (Short2ObjectMap.Entry)var1;
            short var7 = var6.getShortKey();
            return this.map.containsKey(var7) && Objects.equals(this.map.get(var7), var6.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Short) {
               short var4 = (Short)var3;
               Object var5 = var2.getValue();
               return this.map.containsKey(var4) && Objects.equals(this.map.get(var4), var5);
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Short2ObjectMap.Entry) {
            Short2ObjectMap.Entry var6 = (Short2ObjectMap.Entry)var1;
            return this.map.remove(var6.getShortKey(), var6.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Short) {
               short var4 = (Short)var3;
               Object var5 = var2.getValue();
               return this.map.remove(var4, var5);
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry<V> implements Short2ObjectMap.Entry<V> {
      protected short key;
      protected V value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Short var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(short var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public short getShortKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Short2ObjectMap.Entry) {
            Short2ObjectMap.Entry var5 = (Short2ObjectMap.Entry)var1;
            return this.key == var5.getShortKey() && Objects.equals(this.value, var5.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Short) {
               Object var4 = var2.getValue();
               return this.key == (Short)var3 && Objects.equals(this.value, var4);
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
