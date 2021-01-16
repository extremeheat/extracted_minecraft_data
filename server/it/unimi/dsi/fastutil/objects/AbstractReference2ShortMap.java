package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractReference2ShortMap<K> extends AbstractReference2ShortFunction<K> implements Reference2ShortMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2ShortMap() {
      super();
   }

   public boolean containsValue(short var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.reference2ShortEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Reference2ShortMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         public boolean contains(Object var1) {
            return AbstractReference2ShortMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractReference2ShortMap.this.size();
         }

         public void clear() {
            AbstractReference2ShortMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2ShortMap.Entry<K>> i = Reference2ShortMaps.fastIterator(AbstractReference2ShortMap.this);

               public K next() {
                  return ((Reference2ShortMap.Entry)this.i.next()).getKey();
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
            return AbstractReference2ShortMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractReference2ShortMap.this.size();
         }

         public void clear() {
            AbstractReference2ShortMap.this.clear();
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Reference2ShortMap.Entry<K>> i = Reference2ShortMaps.fastIterator(AbstractReference2ShortMap.this);

               public short nextShort() {
                  return ((Reference2ShortMap.Entry)this.i.next()).getShortValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Short> var1) {
      if (var1 instanceof Reference2ShortMap) {
         ObjectIterator var2 = Reference2ShortMaps.fastIterator((Reference2ShortMap)var1);

         while(var2.hasNext()) {
            Reference2ShortMap.Entry var3 = (Reference2ShortMap.Entry)var2.next();
            this.put(var3.getKey(), var3.getShortValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put(var4.getKey(), (Short)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Reference2ShortMaps.fastIterator(this); var2-- != 0; var1 += ((Reference2ShortMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.reference2ShortEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Reference2ShortMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Reference2ShortMap.Entry var4 = (Reference2ShortMap.Entry)var2.next();
         if (this == var4.getKey()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getKey()));
         }

         var1.append("=>");
         var1.append(String.valueOf(var4.getShortValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2ShortMap.Entry<K>> {
      protected final Reference2ShortMap<K> map;

      public BasicEntrySet(Reference2ShortMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Reference2ShortMap.Entry) {
               Reference2ShortMap.Entry var5 = (Reference2ShortMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && this.map.getShort(var3) == var5.getShortValue();
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Short) {
                  return this.map.containsKey(var3) && this.map.getShort(var3) == (Short)var4;
               } else {
                  return false;
               }
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Reference2ShortMap.Entry) {
            Reference2ShortMap.Entry var6 = (Reference2ShortMap.Entry)var1;
            return this.map.remove(var6.getKey(), var6.getShortValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Short) {
               short var5 = (Short)var4;
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

   public static class BasicEntry<K> implements Reference2ShortMap.Entry<K> {
      protected K key;
      protected short value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(K var1, Short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(K var1, short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
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
         } else if (var1 instanceof Reference2ShortMap.Entry) {
            Reference2ShortMap.Entry var5 = (Reference2ShortMap.Entry)var1;
            return this.key == var5.getKey() && this.value == var5.getShortValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Short) {
               return this.key == var3 && this.value == (Short)var4;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return System.identityHashCode(this.key) ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
