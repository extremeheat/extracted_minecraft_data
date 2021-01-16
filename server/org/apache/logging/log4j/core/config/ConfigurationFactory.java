package org.apache.logging.log4j.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public abstract class ConfigurationFactory extends ConfigurationBuilderFactory {
   public static final String CONFIGURATION_FACTORY_PROPERTY = "log4j.configurationFactory";
   public static final String CONFIGURATION_FILE_PROPERTY = "log4j.configurationFile";
   public static final String CATEGORY = "ConfigurationFactory";
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected static final String TEST_PREFIX = "log4j2-test";
   protected static final String DEFAULT_PREFIX = "log4j2";
   private static final String CLASS_LOADER_SCHEME = "classloader";
   private static final String CLASS_PATH_SCHEME = "classpath";
   private static volatile List<ConfigurationFactory> factories = null;
   private static ConfigurationFactory configFactory = new ConfigurationFactory.Factory();
   protected final StrSubstitutor substitutor = new StrSubstitutor(new Interpolator());
   private static final Lock LOCK = new ReentrantLock();

   public ConfigurationFactory() {
      super();
   }

   public static ConfigurationFactory getInstance() {
      if (factories == null) {
         LOCK.lock();

         try {
            if (factories == null) {
               ArrayList var0 = new ArrayList();
               String var1 = PropertiesUtil.getProperties().getStringProperty("log4j.configurationFactory");
               if (var1 != null) {
                  addFactory(var0, (String)var1);
               }

               PluginManager var2 = new PluginManager("ConfigurationFactory");
               var2.collectPlugins();
               Map var3 = var2.getPlugins();
               ArrayList var4 = new ArrayList(var3.size());
               Iterator var5 = var3.values().iterator();

               while(var5.hasNext()) {
                  PluginType var6 = (PluginType)var5.next();

                  try {
                     var4.add(var6.getPluginClass().asSubclass(ConfigurationFactory.class));
                  } catch (Exception var11) {
                     LOGGER.warn((String)"Unable to add class {}", (Object)var6.getPluginClass(), (Object)var11);
                  }
               }

               Collections.sort(var4, OrderComparator.getInstance());
               var5 = var4.iterator();

               while(var5.hasNext()) {
                  Class var13 = (Class)var5.next();
                  addFactory(var0, (Class)var13);
               }

               factories = Collections.unmodifiableList(var0);
            }
         } finally {
            LOCK.unlock();
         }
      }

      LOGGER.debug((String)"Using configurationFactory {}", (Object)configFactory);
      return configFactory;
   }

   private static void addFactory(Collection<ConfigurationFactory> var0, String var1) {
      try {
         addFactory(var0, LoaderUtil.loadClass(var1).asSubclass(ConfigurationFactory.class));
      } catch (Exception var3) {
         LOGGER.error((String)"Unable to load class {}", (Object)var1, (Object)var3);
      }

   }

   private static void addFactory(Collection<ConfigurationFactory> var0, Class<? extends ConfigurationFactory> var1) {
      try {
         var0.add(ReflectionUtil.instantiate(var1));
      } catch (Exception var3) {
         LOGGER.error((String)"Unable to create instance of {}", (Object)var1.getName(), (Object)var3);
      }

   }

   public static void setConfigurationFactory(ConfigurationFactory var0) {
      configFactory = var0;
   }

   public static void resetConfigurationFactory() {
      configFactory = new ConfigurationFactory.Factory();
   }

   public static void removeConfigurationFactory(ConfigurationFactory var0) {
      if (configFactory == var0) {
         configFactory = new ConfigurationFactory.Factory();
      }

   }

   protected abstract String[] getSupportedTypes();

   protected boolean isActive() {
      return true;
   }

   public abstract Configuration getConfiguration(LoggerContext var1, ConfigurationSource var2);

   public Configuration getConfiguration(LoggerContext var1, String var2, URI var3) {
      if (!this.isActive()) {
         return null;
      } else {
         if (var3 != null) {
            ConfigurationSource var4 = this.getInputFromUri(var3);
            if (var4 != null) {
               return this.getConfiguration(var1, var4);
            }
         }

         return null;
      }
   }

   public Configuration getConfiguration(LoggerContext var1, String var2, URI var3, ClassLoader var4) {
      if (!this.isActive()) {
         return null;
      } else if (var4 == null) {
         return this.getConfiguration(var1, var2, var3);
      } else {
         if (isClassLoaderUri(var3)) {
            String var5 = extractClassLoaderUriPath(var3);
            ConfigurationSource var6 = this.getInputFromResource(var5, var4);
            if (var6 != null) {
               Configuration var7 = this.getConfiguration(var1, var6);
               if (var7 != null) {
                  return var7;
               }
            }
         }

         return this.getConfiguration(var1, var2, var3);
      }
   }

   protected ConfigurationSource getInputFromUri(URI var1) {
      File var2 = FileUtils.fileFromUri(var1);
      if (var2 != null && var2.exists() && var2.canRead()) {
         try {
            return new ConfigurationSource(new FileInputStream(var2), var2);
         } catch (FileNotFoundException var8) {
            LOGGER.error((String)"Cannot locate file {}", (Object)var1.getPath(), (Object)var8);
         }
      }

      if (isClassLoaderUri(var1)) {
         ClassLoader var3 = LoaderUtil.getThreadContextClassLoader();
         String var4 = extractClassLoaderUriPath(var1);
         ConfigurationSource var5 = this.getInputFromResource(var4, var3);
         if (var5 != null) {
            return var5;
         }
      }

      if (!var1.isAbsolute()) {
         LOGGER.error((String)"File not found in file system or classpath: {}", (Object)var1.toString());
         return null;
      } else {
         try {
            return new ConfigurationSource(var1.toURL().openStream(), var1.toURL());
         } catch (MalformedURLException var6) {
            LOGGER.error((String)"Invalid URL {}", (Object)var1.toString(), (Object)var6);
         } catch (Exception var7) {
            LOGGER.error((String)"Unable to access {}", (Object)var1.toString(), (Object)var7);
         }

         return null;
      }
   }

   private static boolean isClassLoaderUri(URI var0) {
      if (var0 == null) {
         return false;
      } else {
         String var1 = var0.getScheme();
         return var1 == null || var1.equals("classloader") || var1.equals("classpath");
      }
   }

   private static String extractClassLoaderUriPath(URI var0) {
      return var0.getScheme() == null ? var0.getPath() : var0.getSchemeSpecificPart();
   }

   protected ConfigurationSource getInputFromString(String var1, ClassLoader var2) {
      try {
         URL var3 = new URL(var1);
         return new ConfigurationSource(var3.openStream(), FileUtils.fileFromUri(var3.toURI()));
      } catch (Exception var7) {
         ConfigurationSource var4 = this.getInputFromResource(var1, var2);
         if (var4 == null) {
            try {
               File var5 = new File(var1);
               return new ConfigurationSource(new FileInputStream(var5), var5);
            } catch (FileNotFoundException var6) {
               LOGGER.catching(Level.DEBUG, var6);
            }
         }

         return var4;
      }
   }

   protected ConfigurationSource getInputFromResource(String var1, ClassLoader var2) {
      URL var3 = Loader.getResource(var1, var2);
      if (var3 == null) {
         return null;
      } else {
         InputStream var4 = null;

         try {
            var4 = var3.openStream();
         } catch (IOException var6) {
            LOGGER.catching(Level.DEBUG, var6);
            return null;
         }

         if (var4 == null) {
            return null;
         } else {
            if (FileUtils.isFile(var3)) {
               try {
                  return new ConfigurationSource(var4, FileUtils.fileFromUri(var3.toURI()));
               } catch (URISyntaxException var7) {
                  LOGGER.catching(Level.DEBUG, var7);
               }
            }

            return new ConfigurationSource(var4, var3);
         }
      }
   }

   static List<ConfigurationFactory> getFactories() {
      return factories;
   }

   private static class Factory extends ConfigurationFactory {
      private static final String ALL_TYPES = "*";

      private Factory() {
         super();
      }

      public Configuration getConfiguration(LoggerContext var1, String var2, URI var3) {
         String var4;
         Iterator var5;
         ConfigurationFactory var6;
         String[] var7;
         String[] var8;
         int var9;
         int var10;
         String var11;
         Configuration var12;
         if (var3 == null) {
            var4 = this.substitutor.replace(PropertiesUtil.getProperties().getStringProperty("log4j.configurationFile"));
            if (var4 != null) {
               String[] var14 = var4.split(",");
               if (var14.length <= 1) {
                  return this.getConfiguration(var1, var4);
               }

               ArrayList var15 = new ArrayList();
               var7 = var14;
               int var16 = var14.length;

               for(var9 = 0; var9 < var16; ++var9) {
                  String var17 = var7[var9];
                  Configuration var18 = this.getConfiguration(var1, var17.trim());
                  if (var18 == null || !(var18 instanceof AbstractConfiguration)) {
                     LOGGER.error((String)"Failed to created configuration at {}", (Object)var17);
                     return null;
                  }

                  var15.add((AbstractConfiguration)var18);
               }

               return new CompositeConfiguration(var15);
            }

            var5 = getFactories().iterator();

            label100:
            while(true) {
               do {
                  if (!var5.hasNext()) {
                     break label100;
                  }

                  var6 = (ConfigurationFactory)var5.next();
                  var7 = var6.getSupportedTypes();
               } while(var7 == null);

               var8 = var7;
               var9 = var7.length;

               for(var10 = 0; var10 < var9; ++var10) {
                  var11 = var8[var10];
                  if (var11.equals("*")) {
                     var12 = var6.getConfiguration(var1, var2, var3);
                     if (var12 != null) {
                        return var12;
                     }
                  }
               }
            }
         } else {
            var4 = var3.toString();
            var5 = getFactories().iterator();

            label86:
            while(true) {
               do {
                  if (!var5.hasNext()) {
                     break label86;
                  }

                  var6 = (ConfigurationFactory)var5.next();
                  var7 = var6.getSupportedTypes();
               } while(var7 == null);

               var8 = var7;
               var9 = var7.length;

               for(var10 = 0; var10 < var9; ++var10) {
                  var11 = var8[var10];
                  if (var11.equals("*") || var4.endsWith(var11)) {
                     var12 = var6.getConfiguration(var1, var2, var3);
                     if (var12 != null) {
                        return var12;
                     }
                  }
               }
            }
         }

         Configuration var13 = this.getConfiguration(var1, true, var2);
         if (var13 == null) {
            var13 = this.getConfiguration(var1, true, (String)null);
            if (var13 == null) {
               var13 = this.getConfiguration(var1, false, var2);
               if (var13 == null) {
                  var13 = this.getConfiguration(var1, false, (String)null);
               }
            }
         }

         if (var13 != null) {
            return var13;
         } else {
            LOGGER.error("No log4j2 configuration file found. Using default configuration: logging only errors to the console. Set system property 'org.apache.logging.log4j.simplelog.StatusLogger.level' to TRACE to show Log4j2 internal initialization logging.");
            return new DefaultConfiguration();
         }
      }

      private Configuration getConfiguration(LoggerContext var1, String var2) {
         ConfigurationSource var3 = null;

         try {
            var3 = this.getInputFromUri(NetUtils.toURI(var2));
         } catch (Exception var12) {
            LOGGER.catching(Level.DEBUG, var12);
         }

         if (var3 == null) {
            ClassLoader var4 = LoaderUtil.getThreadContextClassLoader();
            var3 = this.getInputFromString(var2, var4);
         }

         if (var3 != null) {
            Iterator var13 = getFactories().iterator();

            while(true) {
               ConfigurationFactory var5;
               String[] var6;
               do {
                  if (!var13.hasNext()) {
                     return null;
                  }

                  var5 = (ConfigurationFactory)var13.next();
                  var6 = var5.getSupportedTypes();
               } while(var6 == null);

               String[] var7 = var6;
               int var8 = var6.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  String var10 = var7[var9];
                  if (var10.equals("*") || var2.endsWith(var10)) {
                     Configuration var11 = var5.getConfiguration(var1, var3);
                     if (var11 != null) {
                        return var11;
                     }
                  }
               }
            }
         } else {
            return null;
         }
      }

      private Configuration getConfiguration(LoggerContext var1, boolean var2, String var3) {
         boolean var4 = Strings.isNotEmpty(var3);
         ClassLoader var5 = LoaderUtil.getThreadContextClassLoader();
         Iterator var6 = getFactories().iterator();

         while(true) {
            ConfigurationFactory var7;
            String var9;
            String[] var10;
            do {
               if (!var6.hasNext()) {
                  return null;
               }

               var7 = (ConfigurationFactory)var6.next();
               var9 = var2 ? "log4j2-test" : "log4j2";
               var10 = var7.getSupportedTypes();
            } while(var10 == null);

            String[] var11 = var10;
            int var12 = var10.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               String var14 = var11[var13];
               if (!var14.equals("*")) {
                  String var8 = var4 ? var9 + var3 + var14 : var9 + var14;
                  ConfigurationSource var15 = this.getInputFromResource(var8, var5);
                  if (var15 != null) {
                     return var7.getConfiguration(var1, var15);
                  }
               }
            }
         }
      }

      public String[] getSupportedTypes() {
         return null;
      }

      public Configuration getConfiguration(LoggerContext var1, ConfigurationSource var2) {
         if (var2 != null) {
            String var3 = var2.getLocation();
            Iterator var4 = getFactories().iterator();

            label41:
            while(true) {
               ConfigurationFactory var5;
               String[] var6;
               do {
                  if (!var4.hasNext()) {
                     break label41;
                  }

                  var5 = (ConfigurationFactory)var4.next();
                  var6 = var5.getSupportedTypes();
               } while(var6 == null);

               String[] var7 = var6;
               int var8 = var6.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  String var10 = var7[var9];
                  if (var10.equals("*") || var3 != null && var3.endsWith(var10)) {
                     Configuration var11 = var5.getConfiguration(var1, var2);
                     if (var11 != null) {
                        LOGGER.debug((String)"Loaded configuration from {}", (Object)var2);
                        return var11;
                     } else {
                        LOGGER.error((String)"Cannot determine the ConfigurationFactory to use for {}", (Object)var3);
                        return null;
                     }
                  }
               }
            }
         }

         LOGGER.error("Cannot process configuration, input source is null");
         return null;
      }

      // $FF: synthetic method
      Factory(Object var1) {
         this();
      }
   }
}
