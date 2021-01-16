package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractObject2LongMap<K> extends AbstractObject2LongFunction<K> implements Object2LongMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractObject2LongMap() {
      super();
   }

   public boolean containsValue(long var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.object2LongEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Object2LongMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object var1) {
            return AbstractObject2LongMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractObject2LongMap.this.size();
         }

         public void clear() {
            AbstractObject2LongMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Object2LongMap.Entry<K>> i = Object2LongMaps.fastIterator(AbstractObject2LongMap.this);

               public K next() {
                  return ((Object2LongMap.Entry)this.i.next()).getKey();
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
            return AbstractObject2LongMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractObject2LongMap.this.size();
         }

         public void clear() {
            AbstractObject2LongMap.this.clear();
         }

         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Object2LongMap.Entry<K>> i = Object2LongMaps.fastIterator(AbstractObject2LongMap.this);

               public long nextLong() {
                  return ((Object2LongMap.Entry)this.i.next()).getLongValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Long> var1) {
      if (var1 instanceof Object2LongMap) {
         ObjectIterator var2 = Object2LongMaps.fastIterator((Object2LongMap)var1);

         while(var2.hasNext()) {
            Object2LongMap.Entry var3 = (Object2LongMap.Entry)var2.next();
            this.put(var3.getKey(), var3.getLongValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put(var4.getKey(), (Long)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Object2LongMaps.fastIterator(this); var2-- != 0; var1 += ((Object2LongMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.object2LongEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Object2LongMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Object2LongMap.Entry var4 = (Object2LongMap.Entry)var2.next();
         if (this == var4.getKey()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getKey()));
         }

         var1.append("=>");
         var1.append(String.valueOf(var4.getLongValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Object2LongMap.Entry<K>> {
      protected final Object2LongMap<K> map;

      public BasicEntrySet(Object2LongMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Object2LongMap.Entry) {
               Object2LongMap.Entry var5 = (Object2LongMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && this.map.getLong(var3) == var5.getLongValue();
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Long) {
                  return this.map.containsKey(var3) && this.map.getLong(var3) == (Long)var4;
               } else {
                  return false;
               }
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Object2LongMap.Entry) {
            Object2LongMap.Entry var7 = (Object2LongMap.Entry)var1;
            return this.map.remove(var7.getKey(), var7.getLongValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Long) {
               long var5 = (Long)var4;
               return this.map.remove(var3, var5);
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry<K> implements Object2LongMap.Entry<K> {
      protected K key;
      protected long value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(K var1, Long var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(K var1, long var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
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
         } else if (var1 instanceof Object2LongMap.Entry) {
            Object2LongMap.Entry var5 = (Object2LongMap.Entry)var1;
            return Objects.equals(this.key, var5.getKey()) && this.value == var5.getLongValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Long) {
               return Objects.equals(this.key, var3) && this.value == (Long)var4;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.long2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
