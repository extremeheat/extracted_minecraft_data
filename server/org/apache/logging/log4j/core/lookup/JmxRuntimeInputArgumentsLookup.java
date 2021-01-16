package org.apache.logging.log4j.core.lookup;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "jvmrunargs",
   category = "Lookup"
)
public class JmxRuntimeInputArgumentsLookup extends MapLookup {
   public static final JmxRuntimeInputArgumentsLookup JMX_SINGLETON;

   public JmxRuntimeInputArgumentsLookup() {
      super();
   }

   public JmxRuntimeInputArgumentsLookup(Map<String, String> var1) {
      super(var1);
   }

   static {
      List var0 = ManagementFactory.getRuntimeMXBean().getInputArguments();
      JMX_SINGLETON = new JmxRuntimeInputArgumentsLookup(MapLookup.toMap(var0));
   }
}
