package org.apache.logging.log4j.spi;

import java.net.URI;

public interface LoggerContextFactory {
   LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4);

   LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4, URI var5, String var6);

   void removeContext(LoggerContext var1);
}
