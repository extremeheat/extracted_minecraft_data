package org.apache.logging.log4j.core.config;

import java.util.HashMap;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.MapLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin(
   name = "properties",
   category = "Core",
   printObject = true
)
public final class PropertiesPlugin {
   private PropertiesPlugin() {
      super();
   }

   @PluginFactory
   public static StrLookup configureSubstitutor(@PluginElement("Properties") Property[] var0, @PluginConfiguration Configuration var1) {
      if (var0 == null) {
         return new Interpolator(var1.getProperties());
      } else {
         HashMap var2 = new HashMap(var1.getProperties());
         Property[] var3 = var0;
         int var4 = var0.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Property var6 = var3[var5];
            var2.put(var6.getName(), var6.getValue());
         }

         return new Interpolator(new MapLookup(var2), var1.getPluginPackages());
      }
   }
}
