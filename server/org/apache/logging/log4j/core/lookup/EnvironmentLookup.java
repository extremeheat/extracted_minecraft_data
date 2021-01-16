package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "env",
   category = "Lookup"
)
public class EnvironmentLookup extends AbstractLookup {
   public EnvironmentLookup() {
      super();
   }

   public String lookup(LogEvent var1, String var2) {
      return System.getenv(var2);
   }
}
