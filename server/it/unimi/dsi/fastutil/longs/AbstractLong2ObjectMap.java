package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractLong2ObjectMap<V> extends AbstractLong2ObjectFunction<V> implements Long2ObjectMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractLong2ObjectMap() {
      super();
   }

   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(long var1) {
      ObjectIterator var3 = this.long2ObjectEntrySet().iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(((Long2ObjectMap.Entry)var3.next()).getLongKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public LongSet keySet() {
      return new AbstractLongSet() {
         public boolean contains(long var1) {
            return AbstractLong2ObjectMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractLong2ObjectMap.this.size();
         }

         public void clear() {
            AbstractLong2ObjectMap.this.clear();
         }

         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Long2ObjectMap.Entry<V>> i = Long2ObjectMaps.fastIterator(AbstractLong2ObjectMap.this);

               public long nextLong() {
                  return ((Long2ObjectMap.Entry)this.i.next()).getLongKey();
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
            return AbstractLong2ObjectMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractLong2ObjectMap.this.size();
         }

         public void clear() {
            AbstractLong2ObjectMap.this.clear();
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Long2ObjectMap.Entry<V>> i = Long2ObjectMaps.fastIterator(AbstractLong2ObjectMap.this);

               public V next() {
                  return ((Long2ObjectMap.Entry)this.i.next()).getValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Long, ? extends V> var1) {
      if (var1 instanceof Long2ObjectMap) {
         ObjectIterator var2 = Long2ObjectMaps.fastIterator((Long2ObjectMap)var1);

         while(var2.hasNext()) {
            Long2ObjectMap.Entry var3 = (Long2ObjectMap.Entry)var2.next();
            this.put(var3.getLongKey(), var3.getValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Long)var4.getKey(), var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Long2ObjectMaps.fastIterator(this); var2-- != 0; var1 += ((Long2ObjectMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.long2ObjectEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Long2ObjectMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Long2ObjectMap.Entry var4 = (Long2ObjectMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getLongKey()));
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

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Long2ObjectMap.Entry<V>> {
      protected final Long2ObjectMap<V> map;

      public BasicEntrySet(Long2ObjectMap<V> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Long2ObjectMap.Entry) {
            Long2ObjectMap.Entry var7 = (Long2ObjectMap.Entry)var1;
            long var8 = var7.getLongKey();
            return this.map.containsKey(var8) && Objects.equals(this.map.get(var8), var7.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               long var4 = (Long)var3;
               Object var6 = var2.getValue();
               return this.map.containsKey(var4) && Objects.equals(this.map.get(var4), var6);
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Long2ObjectMap.Entry) {
            Long2ObjectMap.Entry var7 = (Long2ObjectMap.Entry)var1;
            return this.map.remove(var7.getLongKey(), var7.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               long var4 = (Long)var3;
               Object var6 = var2.getValue();
               return this.map.remove(var4, var6);
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry<V> implements Long2ObjectMap.Entry<V> {
      protected long key;
      protected V value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Long var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(long var1, V var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public long getLongKey() {
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
         } else if (var1 instanceof Long2ObjectMap.Entry) {
            Long2ObjectMap.Entry var5 = (Long2ObjectMap.Entry)var1;
            return this.key == var5.getLongKey() && Objects.equals(this.value, var5.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Long) {
               Object var4 = var2.getValue();
               return this.key == (Long)var3 && Objects.equals(this.value, var4);
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.long2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
