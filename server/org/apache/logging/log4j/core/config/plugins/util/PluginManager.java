package org.apache.logging.log4j.core.config.plugins.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class PluginManager {
   private static final CopyOnWriteArrayList<String> PACKAGES = new CopyOnWriteArrayList();
   private static final String LOG4J_PACKAGES = "org.apache.logging.log4j.core";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private Map<String, PluginType<?>> plugins = new HashMap();
   private final String category;

   public PluginManager(String var1) {
      super();
      this.category = var1;
   }

   /** @deprecated */
   @Deprecated
   public static void main(String[] var0) {
      System.err.println("ERROR: this tool is superseded by the annotation processor included in log4j-core.");
      System.err.println("If the annotation processor does not work for you, please see the manual page:");
      System.err.println("http://logging.apache.org/log4j/2.x/manual/configuration.html#ConfigurationSyntax");
      System.exit(-1);
   }

   public static void addPackage(String var0) {
      if (!Strings.isBlank(var0)) {
         PACKAGES.addIfAbsent(var0);
      }
   }

   public static void addPackages(Collection<String> var0) {
      Iterator var1 = var0.iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         if (Strings.isNotBlank(var2)) {
            PACKAGES.addIfAbsent(var2);
         }
      }

   }

   public PluginType<?> getPluginType(String var1) {
      return (PluginType)this.plugins.get(var1.toLowerCase());
   }

   public Map<String, PluginType<?>> getPlugins() {
      return this.plugins;
   }

   public void collectPlugins() {
      this.collectPlugins((List)null);
   }

   public void collectPlugins(List<String> var1) {
      String var2 = this.category.toLowerCase();
      LinkedHashMap var3 = new LinkedHashMap();
      Map var4 = PluginRegistry.getInstance().loadFromMainClassLoader();
      if (var4.isEmpty()) {
         var4 = PluginRegistry.getInstance().loadFromPackage("org.apache.logging.log4j.core");
      }

      mergeByName(var3, (List)var4.get(var2));
      Iterator var5 = PluginRegistry.getInstance().getPluginsByCategoryByBundleId().values().iterator();

      while(var5.hasNext()) {
         Map var6 = (Map)var5.next();
         mergeByName(var3, (List)var6.get(var2));
      }

      var5 = PACKAGES.iterator();

      String var7;
      while(var5.hasNext()) {
         var7 = (String)var5.next();
         mergeByName(var3, (List)PluginRegistry.getInstance().loadFromPackage(var7).get(var2));
      }

      if (var1 != null) {
         var5 = var1.iterator();

         while(var5.hasNext()) {
            var7 = (String)var5.next();
            mergeByName(var3, (List)PluginRegistry.getInstance().loadFromPackage(var7).get(var2));
         }
      }

      LOGGER.debug((String)"PluginManager '{}' found {} plugins", (Object)this.category, (Object)var3.size());
      this.plugins = var3;
   }

   private static void mergeByName(Map<String, PluginType<?>> var0, List<PluginType<?>> var1) {
      if (var1 != null) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            PluginType var3 = (PluginType)var2.next();
            String var4 = var3.getKey();
            PluginType var5 = (PluginType)var0.get(var4);
            if (var5 == null) {
               var0.put(var4, var3);
            } else if (!var5.getPluginClass().equals(var3.getPluginClass())) {
               LOGGER.warn((String)"Plugin [{}] is already mapped to {}, ignoring {}", (Object)var4, var5.getPluginClass(), var3.getPluginClass());
            }
         }

      }
   }
}
