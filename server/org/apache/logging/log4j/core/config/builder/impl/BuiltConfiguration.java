package org.apache.logging.log4j.core.config.builder.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.ConfiguratonFileWatcher;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;
import org.apache.logging.log4j.core.util.Patterns;

public class BuiltConfiguration extends AbstractConfiguration {
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private final StatusConfiguration statusConfig;
   protected Component rootComponent;
   private Component loggersComponent;
   private Component appendersComponent;
   private Component filtersComponent;
   private Component propertiesComponent;
   private Component customLevelsComponent;
   private Component scriptsComponent;
   private String contentType = "text";

   public BuiltConfiguration(LoggerContext var1, ConfigurationSource var2, Component var3) {
      super(var1, var2);
      this.statusConfig = (new StatusConfiguration()).withVerboseClasses(VERBOSE_CLASSES).withStatus(this.getDefaultStatus());
      Iterator var4 = var3.getComponents().iterator();

      while(var4.hasNext()) {
         Component var5 = (Component)var4.next();
         String var6 = var5.getPluginType();
         byte var7 = -1;
         switch(var6.hashCode()) {
         case -703799064:
            if (var6.equals("Scripts")) {
               var7 = 0;
            }
            break;
         case -281785364:
            if (var6.equals("Appenders")) {
               var7 = 2;
            }
            break;
         case 337938528:
            if (var6.equals("CustomLevels")) {
               var7 = 5;
            }
            break;
         case 810105819:
            if (var6.equals("Filters")) {
               var7 = 3;
            }
            break;
         case 1067411795:
            if (var6.equals("Properties")) {
               var7 = 4;
            }
            break;
         case 2006930627:
            if (var6.equals("Loggers")) {
               var7 = 1;
            }
         }

         switch(var7) {
         case 0:
            this.scriptsComponent = var5;
            break;
         case 1:
            this.loggersComponent = var5;
            break;
         case 2:
            this.appendersComponent = var5;
            break;
         case 3:
            this.filtersComponent = var5;
            break;
         case 4:
            this.propertiesComponent = var5;
            break;
         case 5:
            this.customLevelsComponent = var5;
         }
      }

      this.rootComponent = var3;
   }

   public void setup() {
      List var1 = this.rootNode.getChildren();
      if (this.propertiesComponent.getComponents().size() > 0) {
         var1.add(this.convertToNode(this.rootNode, this.propertiesComponent));
      }

      if (this.scriptsComponent.getComponents().size() > 0) {
         var1.add(this.convertToNode(this.rootNode, this.scriptsComponent));
      }

      if (this.customLevelsComponent.getComponents().size() > 0) {
         var1.add(this.convertToNode(this.rootNode, this.customLevelsComponent));
      }

      var1.add(this.convertToNode(this.rootNode, this.loggersComponent));
      var1.add(this.convertToNode(this.rootNode, this.appendersComponent));
      if (this.filtersComponent.getComponents().size() > 0) {
         if (this.filtersComponent.getComponents().size() == 1) {
            var1.add(this.convertToNode(this.rootNode, (Component)this.filtersComponent.getComponents().get(0)));
         } else {
            var1.add(this.convertToNode(this.rootNode, this.filtersComponent));
         }
      }

      this.rootComponent = null;
   }

   public String getContentType() {
      return this.contentType;
   }

   public void setContentType(String var1) {
      this.contentType = var1;
   }

   public void createAdvertiser(String var1, ConfigurationSource var2) {
      byte[] var3 = null;

      try {
         if (var2 != null) {
            InputStream var4 = var2.getInputStream();
            if (var4 != null) {
               var3 = toByteArray(var4);
            }
         }
      } catch (IOException var5) {
         LOGGER.warn("Unable to read configuration source " + var2.toString());
      }

      super.createAdvertiser(var1, var2, var3, this.contentType);
   }

   public StatusConfiguration getStatusConfiguration() {
      return this.statusConfig;
   }

   public void setPluginPackages(String var1) {
      this.pluginPackages.addAll(Arrays.asList(var1.split(Patterns.COMMA_SEPARATOR)));
   }

   public void setShutdownHook(String var1) {
      this.isShutdownHookEnabled = !"disable".equalsIgnoreCase(var1);
   }

   public void setShutdownTimeoutMillis(long var1) {
      this.shutdownTimeoutMillis = var1;
   }

   public void setMonitorInterval(int var1) {
      if (this instanceof Reconfigurable && var1 > 0) {
         ConfigurationSource var2 = this.getConfigurationSource();
         if (var2 != null) {
            File var3 = var2.getFile();
            if (var1 > 0) {
               this.getWatchManager().setIntervalSeconds(var1);
               if (var3 != null) {
                  ConfiguratonFileWatcher var4 = new ConfiguratonFileWatcher((Reconfigurable)this, this.listeners);
                  this.getWatchManager().watchFile(var3, var4);
               }
            }
         }
      }

   }

   public PluginManager getPluginManager() {
      return this.pluginManager;
   }

   protected Node convertToNode(Node var1, Component var2) {
      String var3 = var2.getPluginType();
      PluginType var4 = this.pluginManager.getPluginType(var3);
      Node var5 = new Node(var1, var3, var4);
      var5.getAttributes().putAll(var2.getAttributes());
      var5.setValue(var2.getValue());
      List var6 = var5.getChildren();
      Iterator var7 = var2.getComponents().iterator();

      while(var7.hasNext()) {
         Component var8 = (Component)var7.next();
         var6.add(this.convertToNode(var5, var8));
      }

      return var5;
   }
}
