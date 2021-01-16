package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class ReliabilityStrategyFactory {
   private ReliabilityStrategyFactory() {
      super();
   }

   public static ReliabilityStrategy getReliabilityStrategy(LoggerConfig var0) {
      String var1 = PropertiesUtil.getProperties().getStringProperty("log4j.ReliabilityStrategy", "AwaitCompletion");
      if ("AwaitCompletion".equals(var1)) {
         return new AwaitCompletionReliabilityStrategy(var0);
      } else if ("AwaitUnconditionally".equals(var1)) {
         return new AwaitUnconditionallyReliabilityStrategy(var0);
      } else if ("Locking".equals(var1)) {
         return new LockingReliabilityStrategy(var0);
      } else {
         try {
            Class var2 = LoaderUtil.loadClass(var1).asSubclass(ReliabilityStrategy.class);
            return (ReliabilityStrategy)var2.getConstructor(LoggerConfig.class).newInstance(var0);
         } catch (Exception var3) {
            StatusLogger.getLogger().warn("Could not create ReliabilityStrategy for '{}', using default AwaitCompletionReliabilityStrategy: {}", var1, var3);
            return new AwaitCompletionReliabilityStrategy(var0);
         }
      }
   }
}
