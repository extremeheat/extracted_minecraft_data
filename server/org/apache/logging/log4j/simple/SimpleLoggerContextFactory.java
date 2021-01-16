package org.apache.logging.log4j.simple;

import java.net.URI;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;

public class SimpleLoggerContextFactory implements LoggerContextFactory {
   private static LoggerContext context = new SimpleLoggerContext();

   public SimpleLoggerContextFactory() {
      super();
   }

   public LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4) {
      return context;
   }

   public LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4, URI var5, String var6) {
      return context;
   }

   public void removeContext(LoggerContext var1) {
   }
}
