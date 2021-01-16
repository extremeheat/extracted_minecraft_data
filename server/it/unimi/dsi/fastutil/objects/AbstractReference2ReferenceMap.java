package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractReference2ReferenceMap<K, V> extends AbstractReference2ReferenceFunction<K, V> implements Reference2ReferenceMap<K, V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2ReferenceMap() {
      super();
   }

   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.reference2ReferenceEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Reference2ReferenceMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         public boolean contains(Object var1) {
            return AbstractReference2ReferenceMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractReference2ReferenceMap.this.size();
         }

         public void clear() {
            AbstractReference2ReferenceMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = Reference2ReferenceMaps.fastIterator(AbstractReference2ReferenceMap.this);

               public K next() {
                  return ((Reference2ReferenceMap.Entry)this.i.next()).getKey();
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

   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         public boolean contains(Object var1) {
            return AbstractReference2ReferenceMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractReference2ReferenceMap.this.size();
         }

         public void clear() {
            AbstractReference2ReferenceMap.this.clear();
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Reference2ReferenceMap.Entry<K, V>> i = Reference2ReferenceMaps.fastIterator(AbstractReference2ReferenceMap.this);

               public V next() {
                  return ((Reference2ReferenceMap.Entry)this.i.next()).getValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends V> var1) {
      if (var1 instanceof Reference2ReferenceMap) {
         ObjectIterator var2 = Reference2ReferenceMaps.fastIterator((Reference2ReferenceMap)var1);

         while(var2.hasNext()) {
            Reference2ReferenceMap.Entry var3 = (Reference2ReferenceMap.Entry)var2.next();
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

      for(ObjectIterator var3 = Reference2ReferenceMaps.fastIterator(this); var2-- != 0; var1 += ((Reference2ReferenceMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.reference2ReferenceEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Reference2ReferenceMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Reference2ReferenceMap.Entry var4 = (Reference2ReferenceMap.Entry)var2.next();
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

   public abstract static class BasicEntrySet<K, V> extends AbstractObjectSet<Reference2ReferenceMap.Entry<K, V>> {
      protected final Reference2ReferenceMap<K, V> map;

      public BasicEntrySet(Reference2ReferenceMap<K, V> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Reference2ReferenceMap.Entry) {
               Reference2ReferenceMap.Entry var5 = (Reference2ReferenceMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && this.map.get(var3) == var5.getValue();
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               return this.map.containsKey(var3) && this.map.get(var3) == var4;
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Reference2ReferenceMap.Entry) {
            Reference2ReferenceMap.Entry var5 = (Reference2ReferenceMap.Entry)var1;
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

   public static class BasicEntry<K, V> implements Reference2ReferenceMap.Entry<K, V> {
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
         } else if (var1 instanceof Reference2ReferenceMap.Entry) {
            Reference2ReferenceMap.Entry var5 = (Reference2ReferenceMap.Entry)var1;
            return this.key == var5.getKey() && this.value == var5.getValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            return this.key == var3 && this.value == var4;
         }
      }

      public int hashCode() {
         return System.identityHashCode(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
