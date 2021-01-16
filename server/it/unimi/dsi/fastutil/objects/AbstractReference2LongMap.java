package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractReference2LongMap<K> extends AbstractReference2LongFunction<K> implements Reference2LongMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2LongMap() {
      super();
   }

   public boolean containsValue(long var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.reference2LongEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Reference2LongMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         public boolean contains(Object var1) {
            return AbstractReference2LongMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractReference2LongMap.this.size();
         }

         public void clear() {
            AbstractReference2LongMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2LongMap.Entry<K>> i = Reference2LongMaps.fastIterator(AbstractReference2LongMap.this);

               public K next() {
                  return ((Reference2LongMap.Entry)this.i.next()).getKey();
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
            return AbstractReference2LongMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractReference2LongMap.this.size();
         }

         public void clear() {
            AbstractReference2LongMap.this.clear();
         }

         public LongIterator iterator() {
            return new LongIterator() {
               private final ObjectIterator<Reference2LongMap.Entry<K>> i = Reference2LongMaps.fastIterator(AbstractReference2LongMap.this);

               public long nextLong() {
                  return ((Reference2LongMap.Entry)this.i.next()).getLongValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Long> var1) {
      if (var1 instanceof Reference2LongMap) {
         ObjectIterator var2 = Reference2LongMaps.fastIterator((Reference2LongMap)var1);

         while(var2.hasNext()) {
            Reference2LongMap.Entry var3 = (Reference2LongMap.Entry)var2.next();
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

      for(ObjectIterator var3 = Reference2LongMaps.fastIterator(this); var2-- != 0; var1 += ((Reference2LongMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.reference2LongEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Reference2LongMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Reference2LongMap.Entry var4 = (Reference2LongMap.Entry)var2.next();
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

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2LongMap.Entry<K>> {
      protected final Reference2LongMap<K> map;

      public BasicEntrySet(Reference2LongMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Reference2LongMap.Entry) {
               Reference2LongMap.Entry var5 = (Reference2LongMap.Entry)var1;
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
         } else if (var1 instanceof Reference2LongMap.Entry) {
            Reference2LongMap.Entry var7 = (Reference2LongMap.Entry)var1;
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

   public static class BasicEntry<K> implements Reference2LongMap.Entry<K> {
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
         } else if (var1 instanceof Reference2LongMap.Entry) {
            Reference2LongMap.Entry var5 = (Reference2LongMap.Entry)var1;
            return this.key == var5.getKey() && this.value == var5.getLongValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Long) {
               return this.key == var3 && this.value == (Long)var4;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return System.identityHashCode(this.key) ^ HashCommon.long2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
