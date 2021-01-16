package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

@Plugin(
   name = "ctx",
   category = "Lookup"
)
public class ContextMapLookup implements StrLookup {
   private final ContextDataInjector injector = ContextDataInjectorFactory.createInjector();

   public ContextMapLookup() {
      super();
   }

   public String lookup(String var1) {
      return (String)this.currentContextData().getValue(var1);
   }

   private ReadOnlyStringMap currentContextData() {
      return this.injector.rawContextData();
   }

   public String lookup(LogEvent var1, String var2) {
      return (String)var1.getContextData().getValue(var2);
   }
}
