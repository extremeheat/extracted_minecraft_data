package org.apache.logging.log4j.core.async;

import java.net.URI;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.apache.logging.log4j.util.PropertiesUtil;

public class AsyncLoggerContextSelector extends ClassLoaderContextSelector {
   public AsyncLoggerContextSelector() {
      super();
   }

   public static boolean isSelected() {
      return AsyncLoggerContextSelector.class.getName().equals(PropertiesUtil.getProperties().getStringProperty("Log4jContextSelector"));
   }

   protected LoggerContext createContext(String var1, URI var2) {
      return new AsyncLoggerContext(var1, (Object)null, var2);
   }

   protected String toContextMapKey(ClassLoader var1) {
      return "AsyncContext@" + Integer.toHexString(System.identityHashCode(var1));
   }

   protected String defaultContextName() {
      return "DefaultAsyncContext@" + Thread.currentThread().getName();
   }
}
