package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractObject2ObjectMap<K, V> extends AbstractObject2ObjectFunction<K, V> implements Object2ObjectMap<K, V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractObject2ObjectMap() {
      super();
   }

   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.object2ObjectEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Object2ObjectMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object var1) {
            return AbstractObject2ObjectMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractObject2ObjectMap.this.size();
         }

         public void clear() {
            AbstractObject2ObjectMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(AbstractObject2ObjectMap.this);

               public K next() {
                  return ((Object2ObjectMap.Entry)this.i.next()).getKey();
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
            return AbstractObject2ObjectMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractObject2ObjectMap.this.size();
         }

         public void clear() {
            AbstractObject2ObjectMap.this.clear();
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Object2ObjectMap.Entry<K, V>> i = Object2ObjectMaps.fastIterator(AbstractObject2ObjectMap.this);

               public V next() {
                  return ((Object2ObjectMap.Entry)this.i.next()).getValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      if (var1 instanceof Object2ObjectMap) {
         ObjectIterator var2 = Object2ObjectMaps.fastIterator((Object2ObjectMap)var1);

         while(var2.hasNext()) {
            Object2ObjectMap.Entry var3 = (Object2ObjectMap.Entry)var2.next();
            this.put(var3.getKey(), var3.getValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put(var4.getKey(), var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Object2ObjectMaps.fastIterator(this); var2-- != 0; var1 += ((Object2ObjectMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.object2ObjectEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Object2ObjectMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Object2ObjectMap.Entry var4 = (Object2ObjectMap.Entry)var2.next();
         if (this == var4.getKey()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getKey()));
         }

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

   public abstract static class BasicEntrySet<K, V> extends AbstractObjectSet<Object2ObjectMap.Entry<K, V>> {
      protected final Object2ObjectMap<K, V> map;

      public BasicEntrySet(Object2ObjectMap<K, V> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Object2ObjectMap.Entry) {
               Object2ObjectMap.Entry var5 = (Object2ObjectMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && Objects.equals(this.map.get(var3), var5.getValue());
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               return this.map.containsKey(var3) && Objects.equals(this.map.get(var3), var4);
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Object2ObjectMap.Entry) {
            Object2ObjectMap.Entry var5 = (Object2ObjectMap.Entry)var1;
            return this.map.remove(var5.getKey(), var5.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            return this.map.remove(var3, var4);
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry<K, V> implements Object2ObjectMap.Entry<K, V> {
      protected K key;
      protected V value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(K var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
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
         } else if (var1 instanceof Object2ObjectMap.Entry) {
            Object2ObjectMap.Entry var5 = (Object2ObjectMap.Entry)var1;
            return Objects.equals(this.key, var5.getKey()) && Objects.equals(this.value, var5.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            return Objects.equals(this.key, var3) && Objects.equals(this.value, var4);
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
