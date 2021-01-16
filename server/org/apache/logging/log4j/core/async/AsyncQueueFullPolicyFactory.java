package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class AsyncQueueFullPolicyFactory {
   static final String PROPERTY_NAME_ASYNC_EVENT_ROUTER = "log4j2.AsyncQueueFullPolicy";
   static final String PROPERTY_VALUE_DEFAULT_ASYNC_EVENT_ROUTER = "Default";
   static final String PROPERTY_VALUE_DISCARDING_ASYNC_EVENT_ROUTER = "Discard";
   static final String PROPERTY_NAME_DISCARDING_THRESHOLD_LEVEL = "log4j2.DiscardThreshold";
   private static final Logger LOGGER = StatusLogger.getLogger();

   public AsyncQueueFullPolicyFactory() {
      super();
   }

   public static AsyncQueueFullPolicy create() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("log4j2.AsyncQueueFullPolicy");
      if (var0 != null && !"Default".equals(var0) && !DefaultAsyncQueueFullPolicy.class.getSimpleName().equals(var0) && !DefaultAsyncQueueFullPolicy.class.getName().equals(var0)) {
         return !"Discard".equals(var0) && !DiscardingAsyncQueueFullPolicy.class.getSimpleName().equals(var0) && !DiscardingAsyncQueueFullPolicy.class.getName().equals(var0) ? createCustomRouter(var0) : createDiscardingAsyncQueueFullPolicy();
      } else {
         return new DefaultAsyncQueueFullPolicy();
      }
   }

   private static AsyncQueueFullPolicy createCustomRouter(String var0) {
      try {
         Class var1 = LoaderUtil.loadClass(var0).asSubclass(AsyncQueueFullPolicy.class);
         LOGGER.debug((String)"Creating custom AsyncQueueFullPolicy '{}'", (Object)var0);
         return (AsyncQueueFullPolicy)var1.newInstance();
      } catch (Exception var2) {
         LOGGER.debug((String)"Using DefaultAsyncQueueFullPolicy. Could not create custom AsyncQueueFullPolicy '{}': {}", (Object)var0, (Object)var2.toString());
         return new DefaultAsyncQueueFullPolicy();
      }
   }

   private static AsyncQueueFullPolicy createDiscardingAsyncQueueFullPolicy() {
      PropertiesUtil var0 = PropertiesUtil.getProperties();
      String var1 = var0.getStringProperty("log4j2.DiscardThreshold", Level.INFO.name());
      Level var2 = Level.toLevel(var1, Level.INFO);
      LOGGER.debug((String)"Creating custom DiscardingAsyncQueueFullPolicy(discardThreshold:{})", (Object)var2);
      return new DiscardingAsyncQueueFullPolicy(var2);
   }
}
