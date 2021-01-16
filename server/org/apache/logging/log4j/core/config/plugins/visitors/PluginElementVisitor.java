package org.apache.logging.log4j.core.config.plugins.visitors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;

public class PluginElementVisitor extends AbstractPluginVisitor<PluginElement> {
   public PluginElementVisitor() {
      super(PluginElement.class);
   }

   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      String var5 = ((PluginElement)this.annotation).value();
      if (!this.conversionType.isArray()) {
         Node var13 = this.findNamedNode(var5, var2.getChildren());
         if (var13 == null) {
            var4.append(var5).append("=null");
            return null;
         } else {
            var4.append(var13.getName()).append('(').append(var13.toString()).append(')');
            var2.getChildren().remove(var13);
            return var13.getObject();
         }
      } else {
         this.setConversionType(this.conversionType.getComponentType());
         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();
         var4.append("={");
         boolean var8 = true;
         Iterator var9 = var2.getChildren().iterator();

         while(true) {
            Node var10;
            PluginType var11;
            do {
               if (!var9.hasNext()) {
                  var4.append('}');
                  if (!var6.isEmpty() && !this.conversionType.isAssignableFrom(var6.get(0).getClass())) {
                     LOGGER.error((String)"Attempted to assign attribute {} to list of type {} which is incompatible with {}.", (Object)var5, var6.get(0).getClass(), this.conversionType);
                     return null;
                  }

                  var2.getChildren().removeAll(var7);
                  Object[] var14 = (Object[])((Object[])Array.newInstance(this.conversionType, var6.size()));

                  for(int var15 = 0; var15 < var14.length; ++var15) {
                     var14[var15] = var6.get(var15);
                  }

                  return var14;
               }

               var10 = (Node)var9.next();
               var11 = var10.getType();
            } while(!var5.equalsIgnoreCase(var11.getElementName()) && !this.conversionType.isAssignableFrom(var11.getPluginClass()));

            if (!var8) {
               var4.append(", ");
            }

            var8 = false;
            var7.add(var10);
            Object var12 = var10.getObject();
            if (var12 == null) {
               LOGGER.error((String)"Null object returned for {} in {}.", (Object)var10.getName(), (Object)var2.getName());
            } else {
               if (var12.getClass().isArray()) {
                  var4.append(Arrays.toString((Object[])((Object[])var12))).append('}');
                  return var12;
               }

               var4.append(var10.toString());
               var6.add(var12);
            }
         }
      }
   }

   private Node findNamedNode(String var1, Iterable<Node> var2) {
      Iterator var3 = var2.iterator();

      Node var4;
      PluginType var5;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (Node)var3.next();
         var5 = var4.getType();
         if (var5 == null) {
         }
      } while(!var1.equalsIgnoreCase(var5.getElementName()) && !this.conversionType.isAssignableFrom(var5.getPluginClass()));

      return var4;
   }
}
