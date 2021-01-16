package org.apache.logging.log4j.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.TriConsumer;

public class DefaultThreadContextMap implements ThreadContextMap, ReadOnlyStringMap {
   public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
   private final boolean useMap;
   private final ThreadLocal<Map<String, String>> localMap;

   public DefaultThreadContextMap() {
      this(true);
   }

   public DefaultThreadContextMap(boolean var1) {
      super();
      this.useMap = var1;
      this.localMap = createThreadLocalMap(var1);
   }

   static ThreadLocal<Map<String, String>> createThreadLocalMap(final boolean var0) {
      PropertiesUtil var1 = PropertiesUtil.getProperties();
      boolean var2 = var1.getBooleanProperty("isThreadContextMapInheritable");
      return (ThreadLocal)(var2 ? new InheritableThreadLocal<Map<String, String>>() {
         protected Map<String, String> childValue(Map<String, String> var1) {
            return var1 != null && var0 ? Collections.unmodifiableMap(new HashMap(var1)) : null;
         }
      } : new ThreadLocal());
   }

   public void put(String var1, String var2) {
      if (this.useMap) {
         Map var3 = (Map)this.localMap.get();
         HashMap var4 = var3 == null ? new HashMap(1) : new HashMap(var3);
         var4.put(var1, var2);
         this.localMap.set(Collections.unmodifiableMap(var4));
      }
   }

   public void putAll(Map<String, String> var1) {
      if (this.useMap) {
         Map var2 = (Map)this.localMap.get();
         HashMap var5 = var2 == null ? new HashMap(var1.size()) : new HashMap(var2);
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var5.put(var4.getKey(), var4.getValue());
         }

         this.localMap.set(Collections.unmodifiableMap(var5));
      }
   }

   public String get(String var1) {
      Map var2 = (Map)this.localMap.get();
      return var2 == null ? null : (String)var2.get(var1);
   }

   public void remove(String var1) {
      Map var2 = (Map)this.localMap.get();
      if (var2 != null) {
         HashMap var3 = new HashMap(var2);
         var3.remove(var1);
         this.localMap.set(Collections.unmodifiableMap(var3));
      }

   }

   public void removeAll(Iterable<String> var1) {
      Map var2 = (Map)this.localMap.get();
      if (var2 != null) {
         HashMap var3 = new HashMap(var2);
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            var3.remove(var5);
         }

         this.localMap.set(Collections.unmodifiableMap(var3));
      }

   }

   public void clear() {
      this.localMap.remove();
   }

   public Map<String, String> toMap() {
      return this.getCopy();
   }

   public boolean containsKey(String var1) {
      Map var2 = (Map)this.localMap.get();
      return var2 != null && var2.containsKey(var1);
   }

   public <V> void forEach(BiConsumer<String, ? super V> var1) {
      Map var2 = (Map)this.localMap.get();
      if (var2 != null) {
         Iterator var3 = var2.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var1.accept(var4.getKey(), var4.getValue());
         }

      }
   }

   public <V, S> void forEach(TriConsumer<String, ? super V, S> var1, S var2) {
      Map var3 = (Map)this.localMap.get();
      if (var3 != null) {
         Iterator var4 = var3.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            var1.accept(var5.getKey(), var5.getValue(), var2);
         }

      }
   }

   public <V> V getValue(String var1) {
      Map var2 = (Map)this.localMap.get();
      return var2 == null ? null : (String)var2.get(var1);
   }

   public Map<String, String> getCopy() {
      Map var1 = (Map)this.localMap.get();
      return var1 == null ? new HashMap() : new HashMap(var1);
   }

   public Map<String, String> getImmutableMapOrNull() {
      return (Map)this.localMap.get();
   }

   public boolean isEmpty() {
      Map var1 = (Map)this.localMap.get();
      return var1 == null || var1.size() == 0;
   }

   public int size() {
      Map var1 = (Map)this.localMap.get();
      return var1 == null ? 0 : var1.size();
   }

   public String toString() {
      Map var1 = (Map)this.localMap.get();
      return var1 == null ? "{}" : var1.toString();
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      Map var3 = (Map)this.localMap.get();
      int var4 = 31 * var2 + (var3 == null ? 0 : var3.hashCode());
      var4 = 31 * var4 + Boolean.valueOf(this.useMap).hashCode();
      return var4;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         if (var1 instanceof DefaultThreadContextMap) {
            DefaultThreadContextMap var2 = (DefaultThreadContextMap)var1;
            if (this.useMap != var2.useMap) {
               return false;
            }
         }

         if (!(var1 instanceof ThreadContextMap)) {
            return false;
         } else {
            ThreadContextMap var5 = (ThreadContextMap)var1;
            Map var3 = (Map)this.localMap.get();
            Map var4 = var5.getImmutableMapOrNull();
            if (var3 == null) {
               if (var4 != null) {
                  return false;
               }
            } else if (!var3.equals(var4)) {
               return false;
            }

            return true;
         }
      }
   }
}
