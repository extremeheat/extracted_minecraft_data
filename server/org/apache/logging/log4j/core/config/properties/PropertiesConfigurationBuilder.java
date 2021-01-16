package org.apache.logging.log4j.core.config.properties;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterableComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggableComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ScriptComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ScriptFileComponentBuilder;
import org.apache.logging.log4j.core.util.Builder;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public class PropertiesConfigurationBuilder extends ConfigurationBuilderFactory implements Builder<PropertiesConfiguration> {
   private static final String ADVERTISER_KEY = "advertiser";
   private static final String STATUS_KEY = "status";
   private static final String SHUTDOWN_HOOK = "shutdownHook";
   private static final String SHUTDOWN_TIMEOUT = "shutdownTimeout";
   private static final String VERBOSE = "verbose";
   private static final String DEST = "dest";
   private static final String PACKAGES = "packages";
   private static final String CONFIG_NAME = "name";
   private static final String MONITOR_INTERVAL = "monitorInterval";
   private static final String CONFIG_TYPE = "type";
   private final ConfigurationBuilder<PropertiesConfiguration> builder = newConfigurationBuilder(PropertiesConfiguration.class);
   private LoggerContext loggerContext;
   private Properties rootProperties;

   public PropertiesConfigurationBuilder() {
      super();
   }

   public PropertiesConfigurationBuilder setRootProperties(Properties var1) {
      this.rootProperties = var1;
      return this;
   }

   public PropertiesConfigurationBuilder setConfigurationSource(ConfigurationSource var1) {
      this.builder.setConfigurationSource(var1);
      return this;
   }

   public PropertiesConfiguration build() {
      Iterator var1 = this.rootProperties.stringPropertyNames().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         if (!var2.contains(".")) {
            this.builder.addRootProperty(var2, this.rootProperties.getProperty(var2));
         }
      }

      this.builder.setStatusLevel(Level.toLevel(this.rootProperties.getProperty("status"), Level.ERROR)).setShutdownHook(this.rootProperties.getProperty("shutdownHook")).setShutdownTimeout(Long.parseLong(this.rootProperties.getProperty("shutdownTimeout", "0")), TimeUnit.MILLISECONDS).setVerbosity(this.rootProperties.getProperty("verbose")).setDestination(this.rootProperties.getProperty("dest")).setPackages(this.rootProperties.getProperty("packages")).setConfigurationName(this.rootProperties.getProperty("name")).setMonitorInterval(this.rootProperties.getProperty("monitorInterval", "0")).setAdvertiser(this.rootProperties.getProperty("advertiser"));
      Properties var13 = PropertiesUtil.extractSubset(this.rootProperties, "property");
      Iterator var14 = var13.stringPropertyNames().iterator();

      while(var14.hasNext()) {
         String var3 = (String)var14.next();
         this.builder.addProperty(var3, var13.getProperty(var3));
      }

      Map var15 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(this.rootProperties, "script"));
      Iterator var16 = var15.entrySet().iterator();

      String var6;
      while(var16.hasNext()) {
         Entry var4 = (Entry)var16.next();
         Properties var5 = (Properties)var4.getValue();
         var6 = (String)var5.remove("type");
         if (var6 == null) {
            throw new ConfigurationException("No type provided for script - must be Script or ScriptFile");
         }

         if (var6.equalsIgnoreCase("script")) {
            this.builder.add(this.createScript(var5));
         } else {
            this.builder.add(this.createScriptFile(var5));
         }
      }

      Properties var17 = PropertiesUtil.extractSubset(this.rootProperties, "customLevel");
      String var20;
      if (var17.size() > 0) {
         Iterator var18 = var17.stringPropertyNames().iterator();

         while(var18.hasNext()) {
            var20 = (String)var18.next();
            this.builder.add(this.builder.newCustomLevel(var20, Integer.parseInt(var17.getProperty(var20))));
         }
      }

      String var19 = this.rootProperties.getProperty("filters");
      int var8;
      String var10;
      String[] var23;
      if (var19 != null) {
         String[] var21 = var19.split(",");
         var23 = var21;
         int var7 = var21.length;

         for(var8 = 0; var8 < var7; ++var8) {
            String var9 = var23[var8];
            var10 = var9.trim();
            this.builder.add(this.createFilter(var10, PropertiesUtil.extractSubset(this.rootProperties, "filter." + var10)));
         }
      } else {
         Map var22 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(this.rootProperties, "filter"));
         Iterator var24 = var22.entrySet().iterator();

         while(var24.hasNext()) {
            Entry var25 = (Entry)var24.next();
            this.builder.add(this.createFilter(((String)var25.getKey()).trim(), (Properties)var25.getValue()));
         }
      }

      var20 = this.rootProperties.getProperty("appenders");
      String var11;
      String[] var27;
      int var32;
      if (var20 != null) {
         var23 = var20.split(",");
         var27 = var23;
         var8 = var23.length;

         for(var32 = 0; var32 < var8; ++var32) {
            var10 = var27[var32];
            var11 = var10.trim();
            this.builder.add(this.createAppender(var10.trim(), PropertiesUtil.extractSubset(this.rootProperties, "appender." + var11)));
         }
      } else {
         Map var26 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(this.rootProperties, "appender"));
         Iterator var28 = var26.entrySet().iterator();

         while(var28.hasNext()) {
            Entry var30 = (Entry)var28.next();
            this.builder.add(this.createAppender(((String)var30.getKey()).trim(), (Properties)var30.getValue()));
         }
      }

      var6 = this.rootProperties.getProperty("loggers");
      if (var6 != null) {
         var27 = var6.split(",");
         String[] var33 = var27;
         var32 = var27.length;

         for(int var36 = 0; var36 < var32; ++var36) {
            var11 = var33[var36];
            String var12 = var11.trim();
            if (!var12.equals("root")) {
               this.builder.add(this.createLogger(var12, PropertiesUtil.extractSubset(this.rootProperties, "logger." + var12)));
            }
         }
      } else {
         Map var29 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(this.rootProperties, "logger"));
         Iterator var34 = var29.entrySet().iterator();

         while(var34.hasNext()) {
            Entry var35 = (Entry)var34.next();
            var10 = ((String)var35.getKey()).trim();
            if (!var10.equals("root")) {
               this.builder.add(this.createLogger(var10, (Properties)var35.getValue()));
            }
         }
      }

      Properties var31 = PropertiesUtil.extractSubset(this.rootProperties, "rootLogger");
      if (var31.size() > 0) {
         this.builder.add(this.createRootLogger(var31));
      }

      this.builder.setLoggerContext(this.loggerContext);
      return (PropertiesConfiguration)this.builder.build(false);
   }

   private ScriptComponentBuilder createScript(Properties var1) {
      String var2 = (String)var1.remove("name");
      String var3 = (String)var1.remove("language");
      String var4 = (String)var1.remove("text");
      ScriptComponentBuilder var5 = this.builder.newScript(var2, var3, var4);
      return (ScriptComponentBuilder)processRemainingProperties(var5, var1);
   }

   private ScriptFileComponentBuilder createScriptFile(Properties var1) {
      String var2 = (String)var1.remove("name");
      String var3 = (String)var1.remove("path");
      ScriptFileComponentBuilder var4 = this.builder.newScriptFile(var2, var3);
      return (ScriptFileComponentBuilder)processRemainingProperties(var4, var1);
   }

   private AppenderComponentBuilder createAppender(String var1, Properties var2) {
      String var3 = (String)var2.remove("name");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No name attribute provided for Appender " + var1);
      } else {
         String var4 = (String)var2.remove("type");
         if (Strings.isEmpty(var4)) {
            throw new ConfigurationException("No type attribute provided for Appender " + var1);
         } else {
            AppenderComponentBuilder var5 = this.builder.newAppender(var3, var4);
            this.addFiltersToComponent(var5, var2);
            Properties var6 = PropertiesUtil.extractSubset(var2, "layout");
            if (var6.size() > 0) {
               var5.add(this.createLayout(var3, var6));
            }

            return (AppenderComponentBuilder)processRemainingProperties(var5, var2);
         }
      }
   }

   private FilterComponentBuilder createFilter(String var1, Properties var2) {
      String var3 = (String)var2.remove("type");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No type attribute provided for Appender " + var1);
      } else {
         String var4 = (String)var2.remove("onMatch");
         String var5 = (String)var2.remove("onMisMatch");
         FilterComponentBuilder var6 = this.builder.newFilter(var3, var4, var5);
         return (FilterComponentBuilder)processRemainingProperties(var6, var2);
      }
   }

   private AppenderRefComponentBuilder createAppenderRef(String var1, Properties var2) {
      String var3 = (String)var2.remove("ref");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No ref attribute provided for AppenderRef " + var1);
      } else {
         AppenderRefComponentBuilder var4 = this.builder.newAppenderRef(var3);
         String var5 = (String)var2.remove("level");
         if (!Strings.isEmpty(var5)) {
            var4.addAttribute("level", var5);
         }

         return (AppenderRefComponentBuilder)this.addFiltersToComponent(var4, var2);
      }
   }

   private LoggerComponentBuilder createLogger(String var1, Properties var2) {
      String var3 = (String)var2.remove("name");
      String var4 = (String)var2.remove("includeLocation");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No name attribute provided for Logger " + var1);
      } else {
         String var5 = (String)var2.remove("level");
         String var6 = (String)var2.remove("type");
         LoggerComponentBuilder var7;
         boolean var8;
         if (var6 != null) {
            if (!var6.equalsIgnoreCase("asyncLogger")) {
               throw new ConfigurationException("Unknown Logger type " + var6 + " for Logger " + var3);
            }

            if (var4 != null) {
               var8 = Boolean.parseBoolean(var4);
               var7 = this.builder.newAsyncLogger(var3, var5, var8);
            } else {
               var7 = this.builder.newAsyncLogger(var3, var5);
            }
         } else if (var4 != null) {
            var8 = Boolean.parseBoolean(var4);
            var7 = this.builder.newLogger(var3, var5, var8);
         } else {
            var7 = this.builder.newLogger(var3, var5);
         }

         this.addLoggersToComponent(var7, var2);
         this.addFiltersToComponent(var7, var2);
         String var9 = (String)var2.remove("additivity");
         if (!Strings.isEmpty(var9)) {
            var7.addAttribute("additivity", var9);
         }

         return var7;
      }
   }

   private RootLoggerComponentBuilder createRootLogger(Properties var1) {
      String var2 = (String)var1.remove("level");
      String var3 = (String)var1.remove("type");
      String var4 = (String)var1.remove("includeLocation");
      boolean var5;
      RootLoggerComponentBuilder var6;
      if (var3 != null) {
         if (!var3.equalsIgnoreCase("asyncRoot")) {
            throw new ConfigurationException("Unknown Logger type for root logger" + var3);
         }

         if (var4 != null) {
            var5 = Boolean.parseBoolean(var4);
            var6 = this.builder.newAsyncRootLogger(var2, var5);
         } else {
            var6 = this.builder.newAsyncRootLogger(var2);
         }
      } else if (var4 != null) {
         var5 = Boolean.parseBoolean(var4);
         var6 = this.builder.newRootLogger(var2, var5);
      } else {
         var6 = this.builder.newRootLogger(var2);
      }

      this.addLoggersToComponent(var6, var1);
      return (RootLoggerComponentBuilder)this.addFiltersToComponent(var6, var1);
   }

   private LayoutComponentBuilder createLayout(String var1, Properties var2) {
      String var3 = (String)var2.remove("type");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No type attribute provided for Layout on Appender " + var1);
      } else {
         LayoutComponentBuilder var4 = this.builder.newLayout(var3);
         return (LayoutComponentBuilder)processRemainingProperties(var4, var2);
      }
   }

   private static <B extends ComponentBuilder<B>> ComponentBuilder<B> createComponent(ComponentBuilder<?> var0, String var1, Properties var2) {
      String var3 = (String)var2.remove("name");
      String var4 = (String)var2.remove("type");
      if (Strings.isEmpty(var4)) {
         throw new ConfigurationException("No type attribute provided for component " + var1);
      } else {
         ComponentBuilder var5 = var0.getBuilder().newComponent(var3, var4);
         return processRemainingProperties(var5, var2);
      }
   }

   private static <B extends ComponentBuilder<?>> B processRemainingProperties(B var0, Properties var1) {
      while(var1.size() > 0) {
         String var2 = (String)var1.stringPropertyNames().iterator().next();
         int var3 = var2.indexOf(46);
         if (var3 > 0) {
            String var4 = var2.substring(0, var3);
            Properties var5 = PropertiesUtil.extractSubset(var1, var4);
            var0.addComponent(createComponent(var0, var4, var5));
         } else {
            var0.addAttribute(var2, var1.getProperty(var2));
            var1.remove(var2);
         }
      }

      return var0;
   }

   private <B extends FilterableComponentBuilder<? extends ComponentBuilder<?>>> B addFiltersToComponent(B var1, Properties var2) {
      Map var3 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(var2, "filter"));
      Iterator var4 = var3.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var1.add(this.createFilter(((String)var5.getKey()).trim(), (Properties)var5.getValue()));
      }

      return var1;
   }

   private <B extends LoggableComponentBuilder<? extends ComponentBuilder<?>>> B addLoggersToComponent(B var1, Properties var2) {
      Map var3 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(var2, "appenderRef"));
      Iterator var4 = var3.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var1.add(this.createAppenderRef(((String)var5.getKey()).trim(), (Properties)var5.getValue()));
      }

      return var1;
   }

   public PropertiesConfigurationBuilder setLoggerContext(LoggerContext var1) {
      this.loggerContext = var1;
      return this;
   }

   public LoggerContext getLoggerContext() {
      return this.loggerContext;
   }
}
