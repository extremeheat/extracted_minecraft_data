package org.apache.logging.log4j.core.config.plugins.util;

import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.processor.PluginCache;
import org.apache.logging.log4j.core.config.plugins.processor.PluginEntry;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class PluginRegistry {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static volatile PluginRegistry INSTANCE;
   private static final Object INSTANCE_LOCK = new Object();
   private final AtomicReference<Map<String, List<PluginType<?>>>> pluginsByCategoryRef = new AtomicReference();
   private final ConcurrentMap<Long, Map<String, List<PluginType<?>>>> pluginsByCategoryByBundleId = new ConcurrentHashMap();
   private final ConcurrentMap<String, Map<String, List<PluginType<?>>>> pluginsByCategoryByPackage = new ConcurrentHashMap();

   private PluginRegistry() {
      super();
   }

   public static PluginRegistry getInstance() {
      PluginRegistry var0 = INSTANCE;
      if (var0 == null) {
         synchronized(INSTANCE_LOCK) {
            var0 = INSTANCE;
            if (var0 == null) {
               INSTANCE = var0 = new PluginRegistry();
            }
         }
      }

      return var0;
   }

   public void clear() {
      this.pluginsByCategoryRef.set((Object)null);
      this.pluginsByCategoryByPackage.clear();
      this.pluginsByCategoryByBundleId.clear();
   }

   public Map<Long, Map<String, List<PluginType<?>>>> getPluginsByCategoryByBundleId() {
      return this.pluginsByCategoryByBundleId;
   }

   public Map<String, List<PluginType<?>>> loadFromMainClassLoader() {
      Map var1 = (Map)this.pluginsByCategoryRef.get();
      if (var1 != null) {
         return var1;
      } else {
         Map var2 = this.decodeCacheFiles(Loader.getClassLoader());
         return this.pluginsByCategoryRef.compareAndSet((Object)null, var2) ? var2 : (Map)this.pluginsByCategoryRef.get();
      }
   }

   public void clearBundlePlugins(long var1) {
      this.pluginsByCategoryByBundleId.remove(var1);
   }

   public Map<String, List<PluginType<?>>> loadFromBundle(long var1, ClassLoader var3) {
      Map var4 = (Map)this.pluginsByCategoryByBundleId.get(var1);
      if (var4 != null) {
         return var4;
      } else {
         Map var5 = this.decodeCacheFiles(var3);
         var4 = (Map)this.pluginsByCategoryByBundleId.putIfAbsent(var1, var5);
         return var4 != null ? var4 : var5;
      }
   }

   private Map<String, List<PluginType<?>>> decodeCacheFiles(ClassLoader var1) {
      long var2 = System.nanoTime();
      PluginCache var4 = new PluginCache();

      try {
         Enumeration var5 = var1.getResources("META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat");
         if (var5 == null) {
            LOGGER.info((String)"Plugin preloads not available from class loader {}", (Object)var1);
         } else {
            var4.loadCacheFiles(var5);
         }
      } catch (IOException var19) {
         LOGGER.warn((String)"Unable to preload plugins", (Throwable)var19);
      }

      HashMap var20 = new HashMap();
      int var6 = 0;
      Iterator var7 = var4.getAllCategories().entrySet().iterator();

      while(var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         String var9 = (String)var8.getKey();
         ArrayList var10 = new ArrayList(((Map)var8.getValue()).size());
         var20.put(var9, var10);
         Iterator var11 = ((Map)var8.getValue()).entrySet().iterator();

         while(var11.hasNext()) {
            Entry var12 = (Entry)var11.next();
            PluginEntry var13 = (PluginEntry)var12.getValue();
            String var14 = var13.getClassName();

            try {
               Class var15 = var1.loadClass(var14);
               PluginType var16 = new PluginType(var13, var15, var13.getName());
               var10.add(var16);
               ++var6;
            } catch (ClassNotFoundException var17) {
               LOGGER.info((String)"Plugin [{}] could not be loaded due to missing classes.", (Object)var14, (Object)var17);
            } catch (VerifyError var18) {
               LOGGER.info((String)"Plugin [{}] could not be loaded due to verification error.", (Object)var14, (Object)var18);
            }
         }
      }

      long var21 = System.nanoTime();
      DecimalFormat var22 = new DecimalFormat("#0.000000");
      double var23 = (double)(var21 - var2) * 1.0E-9D;
      LOGGER.debug((String)"Took {} seconds to load {} plugins from {}", (Object)var22.format(var23), var6, var1);
      return var20;
   }

   public Map<String, List<PluginType<?>>> loadFromPackage(String var1) {
      if (Strings.isBlank(var1)) {
         return Collections.emptyMap();
      } else {
         Map var2 = (Map)this.pluginsByCategoryByPackage.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            long var3 = System.nanoTime();
            ResolverUtil var5 = new ResolverUtil();
            ClassLoader var6 = Loader.getClassLoader();
            if (var6 != null) {
               var5.setClassLoader(var6);
            }

            var5.findInPackage(new PluginRegistry.PluginTest(), var1);
            HashMap var7 = new HashMap();
            Iterator var8 = var5.getClasses().iterator();

            while(true) {
               Class var9;
               Plugin var10;
               Object var12;
               PluginAliases var16;
               do {
                  if (!var8.hasNext()) {
                     long var24 = System.nanoTime();
                     DecimalFormat var25 = new DecimalFormat("#0.000000");
                     double var26 = (double)(var24 - var3) * 1.0E-9D;
                     LOGGER.debug((String)"Took {} seconds to load {} plugins from package {}", (Object)var25.format(var26), var5.getClasses().size(), var1);
                     var2 = (Map)this.pluginsByCategoryByPackage.putIfAbsent(var1, var7);
                     if (var2 != null) {
                        return var2;
                     }

                     return var7;
                  }

                  var9 = (Class)var8.next();
                  var10 = (Plugin)var9.getAnnotation(Plugin.class);
                  String var11 = var10.category().toLowerCase();
                  var12 = (List)var7.get(var11);
                  if (var12 == null) {
                     var7.put(var11, var12 = new ArrayList());
                  }

                  PluginEntry var13 = new PluginEntry();
                  String var14 = var10.elementType().equals("") ? var10.name() : var10.elementType();
                  var13.setKey(var10.name().toLowerCase());
                  var13.setName(var10.name());
                  var13.setCategory(var10.category());
                  var13.setClassName(var9.getName());
                  var13.setPrintable(var10.printObject());
                  var13.setDefer(var10.deferChildren());
                  PluginType var15 = new PluginType(var13, var9, var14);
                  ((List)var12).add(var15);
                  var16 = (PluginAliases)var9.getAnnotation(PluginAliases.class);
               } while(var16 == null);

               String[] var17 = var16.value();
               int var18 = var17.length;

               for(int var19 = 0; var19 < var18; ++var19) {
                  String var20 = var17[var19];
                  PluginEntry var21 = new PluginEntry();
                  String var22 = var10.elementType().equals("") ? var20.trim() : var10.elementType();
                  var21.setKey(var20.trim().toLowerCase());
                  var21.setName(var10.name());
                  var21.setCategory(var10.category());
                  var21.setClassName(var9.getName());
                  var21.setPrintable(var10.printObject());
                  var21.setDefer(var10.deferChildren());
                  PluginType var23 = new PluginType(var21, var9, var22);
                  ((List)var12).add(var23);
               }
            }
         }
      }
   }

   public static class PluginTest implements ResolverUtil.Test {
      public PluginTest() {
         super();
      }

      public boolean matches(Class<?> var1) {
         return var1 != null && var1.isAnnotationPresent(Plugin.class);
      }

      public String toString() {
         return "annotated with @" + Plugin.class.getSimpleName();
      }

      public boolean matches(URI var1) {
         throw new UnsupportedOperationException();
      }

      public boolean doesMatchClass() {
         return true;
      }

      public boolean doesMatchResource() {
         return false;
      }
   }
}
