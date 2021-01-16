package org.apache.logging.log4j.core.config.plugins.visitors;

import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.util.NameUtil;
import org.apache.logging.log4j.util.StringBuilders;

public class PluginBuilderAttributeVisitor extends AbstractPluginVisitor<PluginBuilderAttribute> {
   public PluginBuilderAttributeVisitor() {
      super(PluginBuilderAttribute.class);
   }

   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      String var5 = ((PluginBuilderAttribute)this.annotation).value();
      String var6 = var5.isEmpty() ? this.member.getName() : var5;
      Map var7 = var2.getAttributes();
      String var8 = removeAttributeValue(var7, var6, this.aliases);
      String var9 = this.substitutor.replace(var3, var8);
      Object var10 = this.convert(var9, (Object)null);
      Object var11 = ((PluginBuilderAttribute)this.annotation).sensitive() ? NameUtil.md5(var10 + this.getClass().getName()) : var10;
      StringBuilders.appendKeyDqValue(var4, var6, var11);
      return var10;
   }
}
