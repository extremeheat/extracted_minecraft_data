package org.apache.logging.log4j.core.config.plugins.visitors;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginNode;

public class PluginNodeVisitor extends AbstractPluginVisitor<PluginNode> {
   public PluginNodeVisitor() {
      super(PluginNode.class);
   }

   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      if (this.conversionType.isInstance(var2)) {
         var4.append("Node=").append(var2.getName());
         return var2;
      } else {
         LOGGER.warn((String)"Variable annotated with @PluginNode is not compatible with the type {}.", (Object)var2.getClass());
         return null;
      }
   }
}
