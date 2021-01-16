package org.apache.logging.log4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CloseableThreadContext {
   private CloseableThreadContext() {
      super();
   }

   public static CloseableThreadContext.Instance push(String var0) {
      return (new CloseableThreadContext.Instance()).push(var0);
   }

   public static CloseableThreadContext.Instance push(String var0, Object... var1) {
      return (new CloseableThreadContext.Instance()).push(var0, var1);
   }

   public static CloseableThreadContext.Instance put(String var0, String var1) {
      return (new CloseableThreadContext.Instance()).put(var0, var1);
   }

   public static CloseableThreadContext.Instance pushAll(List<String> var0) {
      return (new CloseableThreadContext.Instance()).pushAll(var0);
   }

   public static CloseableThreadContext.Instance putAll(Map<String, String> var0) {
      return (new CloseableThreadContext.Instance()).putAll(var0);
   }

   public static class Instance implements AutoCloseable {
      private int pushCount;
      private final Map<String, String> originalValues;

      private Instance() {
         super();
         this.pushCount = 0;
         this.originalValues = new HashMap();
      }

      public CloseableThreadContext.Instance push(String var1) {
         ThreadContext.push(var1);
         ++this.pushCount;
         return this;
      }

      public CloseableThreadContext.Instance push(String var1, Object[] var2) {
         ThreadContext.push(var1, var2);
         ++this.pushCount;
         return this;
      }

      public CloseableThreadContext.Instance put(String var1, String var2) {
         if (!this.originalValues.containsKey(var1)) {
            this.originalValues.put(var1, ThreadContext.get(var1));
         }

         ThreadContext.put(var1, var2);
         return this;
      }

      public CloseableThreadContext.Instance putAll(Map<String, String> var1) {
         Map var2 = ThreadContext.getContext();
         ThreadContext.putAll(var1);
         Iterator var3 = var1.keySet().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            if (!this.originalValues.containsKey(var4)) {
               this.originalValues.put(var4, var2.get(var4));
            }
         }

         return this;
      }

      public CloseableThreadContext.Instance pushAll(List<String> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            this.push(var3);
         }

         return this;
      }

      public void close() {
         this.closeStack();
         this.closeMap();
      }

      private void closeMap() {
         for(Iterator var1 = this.originalValues.entrySet().iterator(); var1.hasNext(); var1.remove()) {
            Entry var2 = (Entry)var1.next();
            String var3 = (String)var2.getKey();
            String var4 = (String)var2.getValue();
            if (null == var4) {
               ThreadContext.remove(var3);
            } else {
               ThreadContext.put(var3, var4);
            }
         }

      }

      private void closeStack() {
         for(int var1 = 0; var1 < this.pushCount; ++var1) {
            ThreadContext.pop();
         }

         this.pushCount = 0;
      }

      // $FF: synthetic method
      Instance(Object var1) {
         this();
      }
   }
}
