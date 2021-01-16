package org.apache.logging.log4j.core.config.plugins.visitors;

import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.util.NameUtil;
import org.apache.logging.log4j.util.StringBuilders;

public class PluginAttributeVisitor extends AbstractPluginVisitor<PluginAttribute> {
   public PluginAttributeVisitor() {
      super(PluginAttribute.class);
   }

   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      String var5 = ((PluginAttribute)this.annotation).value();
      Map var6 = var2.getAttributes();
      String var7 = removeAttributeValue(var6, var5, this.aliases);
      String var8 = this.substitutor.replace(var3, var7);
      Object var9 = this.findDefaultValue(var3);
      Object var10 = this.convert(var8, var9);
      Object var11 = ((PluginAttribute)this.annotation).sensitive() ? NameUtil.md5(var10 + this.getClass().getName()) : var10;
      StringBuilders.appendKeyDqValue(var4, var5, var11);
      return var10;
   }

   private Object findDefaultValue(LogEvent var1) {
      if (this.conversionType != Integer.TYPE && this.conversionType != Integer.class) {
         if (this.conversionType != Long.TYPE && this.conversionType != Long.class) {
            if (this.conversionType != Boolean.TYPE && this.conversionType != Boolean.class) {
               if (this.conversionType != Float.TYPE && this.conversionType != Float.class) {
                  if (this.conversionType != Double.TYPE && this.conversionType != Double.class) {
                     if (this.conversionType != Byte.TYPE && this.conversionType != Byte.class) {
                        if (this.conversionType != Character.TYPE && this.conversionType != Character.class) {
                           if (this.conversionType != Short.TYPE && this.conversionType != Short.class) {
                              return this.conversionType == Class.class ? ((PluginAttribute)this.annotation).defaultClass() : this.substitutor.replace(var1, ((PluginAttribute)this.annotation).defaultString());
                           } else {
                              return ((PluginAttribute)this.annotation).defaultShort();
                           }
                        } else {
                           return ((PluginAttribute)this.annotation).defaultChar();
                        }
                     } else {
                        return ((PluginAttribute)this.annotation).defaultByte();
                     }
                  } else {
                     return ((PluginAttribute)this.annotation).defaultDouble();
                  }
               } else {
                  return ((PluginAttribute)this.annotation).defaultFloat();
               }
            } else {
               return ((PluginAttribute)this.annotation).defaultBoolean();
            }
         } else {
            return ((PluginAttribute)this.annotation).defaultLong();
         }
      } else {
         return ((PluginAttribute)this.annotation).defaultInt();
      }
   }
}
