package org.apache.logging.log4j.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.message.MessageFactory;

public class LoggerRegistry<T extends ExtendedLogger> {
   private static final String DEFAULT_FACTORY_KEY;
   private final LoggerRegistry.MapFactory<T> factory;
   private final Map<String, Map<String, T>> map;

   public LoggerRegistry() {
      this(new LoggerRegistry.ConcurrentMapFactory());
   }

   public LoggerRegistry(LoggerRegistry.MapFactory<T> var1) {
      super();
      this.factory = (LoggerRegistry.MapFactory)Objects.requireNonNull(var1, "factory");
      this.map = var1.createOuterMap();
   }

   private static String factoryClassKey(Class<? extends MessageFactory> var0) {
      return var0 == null ? DEFAULT_FACTORY_KEY : var0.getName();
   }

   private static String factoryKey(MessageFactory var0) {
      return var0 == null ? DEFAULT_FACTORY_KEY : var0.getClass().getName();
   }

   public T getLogger(String var1) {
      return (ExtendedLogger)this.getOrCreateInnerMap(DEFAULT_FACTORY_KEY).get(var1);
   }

   public T getLogger(String var1, MessageFactory var2) {
      return (ExtendedLogger)this.getOrCreateInnerMap(factoryKey(var2)).get(var1);
   }

   public Collection<T> getLoggers() {
      return this.getLoggers(new ArrayList());
   }

   public Collection<T> getLoggers(Collection<T> var1) {
      Iterator var2 = this.map.values().iterator();

      while(var2.hasNext()) {
         Map var3 = (Map)var2.next();
         var1.addAll(var3.values());
      }

      return var1;
   }

   private Map<String, T> getOrCreateInnerMap(String var1) {
      Map var2 = (Map)this.map.get(var1);
      if (var2 == null) {
         var2 = this.factory.createInnerMap();
         this.map.put(var1, var2);
      }

      return var2;
   }

   public boolean hasLogger(String var1) {
      return this.getOrCreateInnerMap(DEFAULT_FACTORY_KEY).containsKey(var1);
   }

   public boolean hasLogger(String var1, MessageFactory var2) {
      return this.getOrCreateInnerMap(factoryKey(var2)).containsKey(var1);
   }

   public boolean hasLogger(String var1, Class<? extends MessageFactory> var2) {
      return this.getOrCreateInnerMap(factoryClassKey(var2)).containsKey(var1);
   }

   public void putIfAbsent(String var1, MessageFactory var2, T var3) {
      this.factory.putIfAbsent(this.getOrCreateInnerMap(factoryKey(var2)), var1, var3);
   }

   static {
      DEFAULT_FACTORY_KEY = AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS.getName();
   }

   public static class WeakMapFactory<T extends ExtendedLogger> implements LoggerRegistry.MapFactory<T> {
      public WeakMapFactory() {
         super();
      }

      public Map<String, T> createInnerMap() {
         return new WeakHashMap();
      }

      public Map<String, Map<String, T>> createOuterMap() {
         return new WeakHashMap();
      }

      public void putIfAbsent(Map<String, T> var1, String var2, T var3) {
         var1.put(var2, var3);
      }
   }

   public static class ConcurrentMapFactory<T extends ExtendedLogger> implements LoggerRegistry.MapFactory<T> {
      public ConcurrentMapFactory() {
         super();
      }

      public Map<String, T> createInnerMap() {
         return new ConcurrentHashMap();
      }

      public Map<String, Map<String, T>> createOuterMap() {
         return new ConcurrentHashMap();
      }

      public void putIfAbsent(Map<String, T> var1, String var2, T var3) {
         ((ConcurrentMap)var1).putIfAbsent(var2, var3);
      }
   }

   public interface MapFactory<T extends ExtendedLogger> {
      Map<String, T> createInnerMap();

      Map<String, Map<String, T>> createOuterMap();

      void putIfAbsent(Map<String, T> var1, String var2, T var3);
   }
}
