package org.apache.logging.log4j.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.spi.ReadOnlyThreadContextMap;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;

public class ThreadContextDataInjector {
   public ThreadContextDataInjector() {
      super();
   }

   public static void copyProperties(List<Property> var0, StringMap var1) {
      if (var0 != null) {
         for(int var2 = 0; var2 < var0.size(); ++var2) {
            Property var3 = (Property)var0.get(var2);
            var1.putValue(var3.getName(), var3.getValue());
         }
      }

   }

   public static class ForCopyOnWriteThreadContextMap implements ContextDataInjector {
      public ForCopyOnWriteThreadContextMap() {
         super();
      }

      public StringMap injectContextData(List<Property> var1, StringMap var2) {
         StringMap var3 = ThreadContext.getThreadContextMap().getReadOnlyContextData();
         if (var1 != null && !var1.isEmpty()) {
            StringMap var4 = ContextDataFactory.createContextData(var1.size() + var3.size());
            ThreadContextDataInjector.copyProperties(var1, var4);
            var4.putAll(var3);
            return var4;
         } else {
            return var3;
         }
      }

      public ReadOnlyStringMap rawContextData() {
         return ThreadContext.getThreadContextMap().getReadOnlyContextData();
      }
   }

   public static class ForGarbageFreeThreadContextMap implements ContextDataInjector {
      public ForGarbageFreeThreadContextMap() {
         super();
      }

      public StringMap injectContextData(List<Property> var1, StringMap var2) {
         ThreadContextDataInjector.copyProperties(var1, var2);
         StringMap var3 = ThreadContext.getThreadContextMap().getReadOnlyContextData();
         var2.putAll(var3);
         return var2;
      }

      public ReadOnlyStringMap rawContextData() {
         return ThreadContext.getThreadContextMap().getReadOnlyContextData();
      }
   }

   public static class ForDefaultThreadContextMap implements ContextDataInjector {
      public ForDefaultThreadContextMap() {
         super();
      }

      public StringMap injectContextData(List<Property> var1, StringMap var2) {
         Map var3 = ThreadContext.getImmutableContext();
         if (var1 != null && !var1.isEmpty()) {
            JdkMapAdapterStringMap var4 = new JdkMapAdapterStringMap(new HashMap(var3));

            for(int var5 = 0; var5 < var1.size(); ++var5) {
               Property var6 = (Property)var1.get(var5);
               if (!var3.containsKey(var6.getName())) {
                  var4.putValue(var6.getName(), var6.getValue());
               }
            }

            var4.freeze();
            return var4;
         } else {
            return (StringMap)(var3.isEmpty() ? ContextDataFactory.emptyFrozenContextData() : frozenStringMap(var3));
         }
      }

      private static JdkMapAdapterStringMap frozenStringMap(Map<String, String> var0) {
         JdkMapAdapterStringMap var1 = new JdkMapAdapterStringMap(var0);
         var1.freeze();
         return var1;
      }

      public ReadOnlyStringMap rawContextData() {
         ReadOnlyThreadContextMap var1 = ThreadContext.getThreadContextMap();
         if (var1 instanceof ReadOnlyStringMap) {
            return (ReadOnlyStringMap)var1;
         } else {
            Map var2 = ThreadContext.getImmutableContext();
            return (ReadOnlyStringMap)(var2.isEmpty() ? ContextDataFactory.emptyFrozenContextData() : new JdkMapAdapterStringMap(var2));
         }
      }
   }
}
