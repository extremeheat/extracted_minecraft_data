package org.apache.logging.log4j.core.config.plugins.visitors;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;

public class PluginConfigurationVisitor extends AbstractPluginVisitor<PluginConfiguration> {
   public PluginConfigurationVisitor() {
      super(PluginConfiguration.class);
   }

   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      if (this.conversionType.isInstance(var1)) {
         var4.append("Configuration");
         if (var1.getName() != null) {
            var4.append('(').append(var1.getName()).append(')');
         }

         return var1;
      } else {
         LOGGER.warn((String)"Variable annotated with @PluginConfiguration is not compatible with type {}.", (Object)var1.getClass());
         return null;
      }
   }
}
