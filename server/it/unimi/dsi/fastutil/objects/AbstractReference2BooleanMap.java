package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractReference2BooleanMap<K> extends AbstractReference2BooleanFunction<K> implements Reference2BooleanMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2BooleanMap() {
      super();
   }

   public boolean containsValue(boolean var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.reference2BooleanEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Reference2BooleanMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         public boolean contains(Object var1) {
            return AbstractReference2BooleanMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractReference2BooleanMap.this.size();
         }

         public void clear() {
            AbstractReference2BooleanMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2BooleanMap.Entry<K>> i = Reference2BooleanMaps.fastIterator(AbstractReference2BooleanMap.this);

               public K next() {
                  return ((Reference2BooleanMap.Entry)this.i.next()).getKey();
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
            return AbstractReference2BooleanMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractReference2BooleanMap.this.size();
         }

         public void clear() {
            AbstractReference2BooleanMap.this.clear();
         }

         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Reference2BooleanMap.Entry<K>> i = Reference2BooleanMaps.fastIterator(AbstractReference2BooleanMap.this);

               public boolean nextBoolean() {
                  return ((Reference2BooleanMap.Entry)this.i.next()).getBooleanValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Boolean> var1) {
      if (var1 instanceof Reference2BooleanMap) {
         ObjectIterator var2 = Reference2BooleanMaps.fastIterator((Reference2BooleanMap)var1);

         while(var2.hasNext()) {
            Reference2BooleanMap.Entry var3 = (Reference2BooleanMap.Entry)var2.next();
            this.put(var3.getKey(), var3.getBooleanValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put(var4.getKey(), (Boolean)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Reference2BooleanMaps.fastIterator(this); var2-- != 0; var1 += ((Reference2BooleanMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.reference2BooleanEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Reference2BooleanMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Reference2BooleanMap.Entry var4 = (Reference2BooleanMap.Entry)var2.next();
         if (this == var4.getKey()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getKey()));
         }

         var1.append("=>");
         var1.append(String.valueOf(var4.getBooleanValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2BooleanMap.Entry<K>> {
      protected final Reference2BooleanMap<K> map;

      public BasicEntrySet(Reference2BooleanMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Reference2BooleanMap.Entry) {
               Reference2BooleanMap.Entry var5 = (Reference2BooleanMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && this.map.getBoolean(var3) == var5.getBooleanValue();
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Boolean) {
                  return this.map.containsKey(var3) && this.map.getBoolean(var3) == (Boolean)var4;
               } else {
                  return false;
               }
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Reference2BooleanMap.Entry) {
            Reference2BooleanMap.Entry var6 = (Reference2BooleanMap.Entry)var1;
            return this.map.remove(var6.getKey(), var6.getBooleanValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Boolean) {
               boolean var5 = (Boolean)var4;
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

   public static class BasicEntry<K> implements Reference2BooleanMap.Entry<K> {
      protected K key;
      protected boolean value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(K var1, Boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(K var1, boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
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
         } else if (var1 instanceof Reference2BooleanMap.Entry) {
            Reference2BooleanMap.Entry var5 = (Reference2BooleanMap.Entry)var1;
            return this.key == var5.getKey() && this.value == var5.getBooleanValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Boolean) {
               return this.key == var3 && this.value == (Boolean)var4;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return System.identityHashCode(this.key) ^ (this.value ? 1231 : 1237);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
