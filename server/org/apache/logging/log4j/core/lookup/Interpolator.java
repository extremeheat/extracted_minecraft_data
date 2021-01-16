package org.apache.logging.log4j.core.lookup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.ConfigurationAware;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.status.StatusLogger;

public class Interpolator extends AbstractConfigurationAwareLookup {
   private static final String LOOKUP_KEY_WEB = "web";
   private static final String LOOKUP_KEY_JNDI = "jndi";
   private static final String LOOKUP_KEY_JVMRUNARGS = "jvmrunargs";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final char PREFIX_SEPARATOR = ':';
   private final Map<String, StrLookup> lookups;
   private final StrLookup defaultLookup;

   public Interpolator(StrLookup var1) {
      this(var1, (List)null);
   }

   public Interpolator(StrLookup var1, List<String> var2) {
      super();
      this.lookups = new HashMap();
      this.defaultLookup = (StrLookup)(var1 == null ? new MapLookup(new HashMap()) : var1);
      PluginManager var3 = new PluginManager("Lookup");
      var3.collectPlugins(var2);
      Map var4 = var3.getPlugins();
      Iterator var5 = var4.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();

         try {
            Class var7 = ((PluginType)var6.getValue()).getPluginClass().asSubclass(StrLookup.class);
            this.lookups.put(var6.getKey(), ReflectionUtil.instantiate(var7));
         } catch (Throwable var8) {
            this.handleError((String)var6.getKey(), var8);
         }
      }

   }

   public Interpolator() {
      this((Map)null);
   }

   public Interpolator(Map<String, String> var1) {
      super();
      this.lookups = new HashMap();
      this.defaultLookup = new MapLookup((Map)(var1 == null ? new HashMap() : var1));
      this.lookups.put("log4j", new Log4jLookup());
      this.lookups.put("sys", new SystemPropertiesLookup());
      this.lookups.put("env", new EnvironmentLookup());
      this.lookups.put("main", MainMapLookup.MAIN_SINGLETON);
      this.lookups.put("marker", new MarkerLookup());
      this.lookups.put("java", new JavaLookup());

      try {
         this.lookups.put("jndi", Loader.newCheckedInstanceOf("org.apache.logging.log4j.core.lookup.JndiLookup", StrLookup.class));
      } catch (Exception | LinkageError var5) {
         this.handleError("jndi", var5);
      }

      try {
         this.lookups.put("jvmrunargs", Loader.newCheckedInstanceOf("org.apache.logging.log4j.core.lookup.JmxRuntimeInputArgumentsLookup", StrLookup.class));
      } catch (Exception | LinkageError var4) {
         this.handleError("jvmrunargs", var4);
      }

      this.lookups.put("date", new DateLookup());
      this.lookups.put("ctx", new ContextMapLookup());
      if (Loader.isClassAvailable("javax.servlet.ServletContext")) {
         try {
            this.lookups.put("web", Loader.newCheckedInstanceOf("org.apache.logging.log4j.web.WebLookup", StrLookup.class));
         } catch (Exception var3) {
            this.handleError("web", var3);
         }
      } else {
         LOGGER.debug("Not in a ServletContext environment, thus not loading WebLookup plugin.");
      }

   }

   private void handleError(String var1, Throwable var2) {
      byte var4 = -1;
      switch(var1.hashCode()) {
      case 117588:
         if (var1.equals("web")) {
            var4 = 2;
         }
         break;
      case 3266761:
         if (var1.equals("jndi")) {
            var4 = 0;
         }
         break;
      case 356346407:
         if (var1.equals("jvmrunargs")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         LOGGER.warn("JNDI lookup class is not available because this JRE does not support JNDI. JNDI string lookups will not be available, continuing configuration. Ignoring " + var2);
         break;
      case 1:
         LOGGER.warn("JMX runtime input lookup class is not available because this JRE does not support JMX. JMX lookups will not be available, continuing configuration. Ignoring " + var2);
         break;
      case 2:
         LOGGER.info("Log4j appears to be running in a Servlet environment, but there's no log4j-web module available. If you want better web container support, please add the log4j-web JAR to your web archive or server lib directory.");
         break;
      default:
         LOGGER.error((String)"Unable to create Lookup for {}", (Object)var1, (Object)var2);
      }

   }

   public String lookup(LogEvent var1, String var2) {
      if (var2 == null) {
         return null;
      } else {
         int var3 = var2.indexOf(58);
         if (var3 >= 0) {
            String var4 = var2.substring(0, var3).toLowerCase(Locale.US);
            String var5 = var2.substring(var3 + 1);
            StrLookup var6 = (StrLookup)this.lookups.get(var4);
            if (var6 instanceof ConfigurationAware) {
               ((ConfigurationAware)var6).setConfiguration(this.configuration);
            }

            String var7 = null;
            if (var6 != null) {
               var7 = var1 == null ? var6.lookup(var5) : var6.lookup(var1, var5);
            }

            if (var7 != null) {
               return var7;
            }

            var2 = var2.substring(var3 + 1);
         }

         if (this.defaultLookup != null) {
            return var1 == null ? this.defaultLookup.lookup(var2) : this.defaultLookup.lookup(var1, var2);
         } else {
            return null;
         }
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      String var3;
      for(Iterator var2 = this.lookups.keySet().iterator(); var2.hasNext(); var1.append(var3)) {
         var3 = (String)var2.next();
         if (var1.length() == 0) {
            var1.append('{');
         } else {
            var1.append(", ");
         }
      }

      if (var1.length() > 0) {
         var1.append('}');
      }

      return var1.toString();
   }
}
