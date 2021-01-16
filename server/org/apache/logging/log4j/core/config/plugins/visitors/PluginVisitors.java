package org.apache.logging.log4j.core.config.plugins.visitors;

import java.lang.annotation.Annotation;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.PluginVisitorStrategy;
import org.apache.logging.log4j.status.StatusLogger;

public final class PluginVisitors {
   private static final Logger LOGGER = StatusLogger.getLogger();

   private PluginVisitors() {
      super();
   }

   public static PluginVisitor<? extends Annotation> findVisitor(Class<? extends Annotation> var0) {
      PluginVisitorStrategy var1 = (PluginVisitorStrategy)var0.getAnnotation(PluginVisitorStrategy.class);
      if (var1 == null) {
         return null;
      } else {
         try {
            return (PluginVisitor)var1.value().newInstance();
         } catch (Exception var3) {
            LOGGER.error((String)"Error loading PluginVisitor [{}] for annotation [{}].", (Object)var1.value(), var0, var3);
            return null;
         }
      }
   }
}
