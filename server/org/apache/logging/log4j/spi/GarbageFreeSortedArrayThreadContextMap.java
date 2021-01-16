package org.apache.logging.log4j.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

class GarbageFreeSortedArrayThreadContextMap implements ReadOnlyThreadContextMap, ObjectThreadContextMap {
   public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
   protected static final int DEFAULT_INITIAL_CAPACITY = 16;
   protected static final String PROPERTY_NAME_INITIAL_CAPACITY = "log4j2.ThreadContext.initial.capacity";
   protected final ThreadLocal<StringMap> localMap = this.createThreadLocalMap();

   public GarbageFreeSortedArrayThreadContextMap() {
      super();
   }

   private ThreadLocal<StringMap> createThreadLocalMap() {
      PropertiesUtil var1 = PropertiesUtil.getProperties();
      boolean var2 = var1.getBooleanProperty("isThreadContextMapInheritable");
      return (ThreadLocal)(var2 ? new InheritableThreadLocal<StringMap>() {
         protected StringMap childValue(StringMap var1) {
            return var1 != null ? GarbageFreeSortedArrayThreadContextMap.this.createStringMap(var1) : null;
         }
      } : new ThreadLocal());
   }

   protected StringMap createStringMap() {
      return new SortedArrayStringMap(PropertiesUtil.getProperties().getIntegerProperty("log4j2.ThreadContext.initial.capacity", 16));
   }

   protected StringMap createStringMap(ReadOnlyStringMap var1) {
      return new SortedArrayStringMap(var1);
   }

   private StringMap getThreadLocalMap() {
      StringMap var1 = (StringMap)this.localMap.get();
      if (var1 == null) {
         var1 = this.createStringMap();
         this.localMap.set(var1);
      }

      return var1;
   }

   public void put(String var1, String var2) {
      this.getThreadLocalMap().putValue(var1, var2);
   }

   public void putValue(String var1, Object var2) {
      this.getThreadLocalMap().putValue(var1, var2);
   }

   public void putAll(Map<String, String> var1) {
      if (var1 != null && !var1.isEmpty()) {
         StringMap var2 = this.getThreadLocalMap();
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var2.putValue((String)var4.getKey(), var4.getValue());
         }

      }
   }

   public <V> void putAllValues(Map<String, V> var1) {
      if (var1 != null && !var1.isEmpty()) {
         StringMap var2 = this.getThreadLocalMap();
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            var2.putValue((String)var4.getKey(), var4.getValue());
         }

      }
   }

   public String get(String var1) {
      return (String)this.getValue(var1);
   }

   public Object getValue(String var1) {
      StringMap var2 = (StringMap)this.localMap.get();
      return var2 == null ? null : var2.getValue(var1);
   }

   public void remove(String var1) {
      StringMap var2 = (StringMap)this.localMap.get();
      if (var2 != null) {
         var2.remove(var1);
      }

   }

   public void removeAll(Iterable<String> var1) {
      StringMap var2 = (StringMap)this.localMap.get();
      if (var2 != null) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var2.remove(var4);
         }
      }

   }

   public void clear() {
      StringMap var1 = (StringMap)this.localMap.get();
      if (var1 != null) {
         var1.clear();
      }

   }

   public boolean containsKey(String var1) {
      StringMap var2 = (StringMap)this.localMap.get();
      return var2 != null && var2.containsKey(var1);
   }

   public Map<String, String> getCopy() {
      StringMap var1 = (StringMap)this.localMap.get();
      return (Map)(var1 == null ? new HashMap() : var1.toMap());
   }

   public StringMap getReadOnlyContextData() {
      StringMap var1 = (StringMap)this.localMap.get();
      if (var1 == null) {
         var1 = this.createStringMap();
         this.localMap.set(var1);
      }

      return var1;
   }

   public Map<String, String> getImmutableMapOrNull() {
      StringMap var1 = (StringMap)this.localMap.get();
      return var1 == null ? null : Collections.unmodifiableMap(var1.toMap());
   }

   public boolean isEmpty() {
      StringMap var1 = (StringMap)this.localMap.get();
      return var1 == null || var1.size() == 0;
   }

   public String toString() {
      StringMap var1 = (StringMap)this.localMap.get();
      return var1 == null ? "{}" : var1.toString();
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      StringMap var3 = (StringMap)this.localMap.get();
      int var4 = 31 * var2 + (var3 == null ? 0 : var3.hashCode());
      return var4;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ThreadContextMap)) {
         return false;
      } else {
         ThreadContextMap var2 = (ThreadContextMap)var1;
         Map var3 = this.getImmutableMapOrNull();
         Map var4 = var2.getImmutableMapOrNull();
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
