package org.apache.logging.log4j.core.config.plugins.visitors;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

public class PluginValueVisitor extends AbstractPluginVisitor<PluginValue> {
   public PluginValueVisitor() {
      super(PluginValue.class);
   }

   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      String var5 = ((PluginValue)this.annotation).value();
      String var6 = var2.getValue();
      String var7 = (String)var2.getAttributes().get("value");
      String var8 = null;
      if (Strings.isNotEmpty(var6)) {
         if (Strings.isNotEmpty(var7)) {
            LOGGER.error((String)"Configuration contains {} with both attribute value ({}) AND element value ({}). Please specify only one value. Using the element value.", (Object)var2.getName(), var7, var6);
         }

         var8 = var6;
      } else {
         var8 = removeAttributeValue(var2.getAttributes(), "value", new String[0]);
      }

      String var9 = this.substitutor.replace(var3, var8);
      StringBuilders.appendKeyDqValue(var4, var5, var9);
      return var9;
   }
}
