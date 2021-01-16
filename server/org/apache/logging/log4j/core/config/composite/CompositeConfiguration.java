package org.apache.logging.log4j.core.config.composite;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.ConfiguratonFileWatcher;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class CompositeConfiguration extends AbstractConfiguration implements Reconfigurable {
   public static final String MERGE_STRATEGY_PROPERTY = "log4j.mergeStrategy";
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private final List<? extends AbstractConfiguration> configurations;
   private MergeStrategy mergeStrategy;

   public CompositeConfiguration(List<? extends AbstractConfiguration> var1) {
      super(((AbstractConfiguration)var1.get(0)).getLoggerContext(), ConfigurationSource.NULL_SOURCE);
      this.rootNode = ((AbstractConfiguration)var1.get(0)).getRootNode();
      this.configurations = var1;
      String var2 = PropertiesUtil.getProperties().getStringProperty("log4j.mergeStrategy", DefaultMergeStrategy.class.getName());

      try {
         this.mergeStrategy = (MergeStrategy)LoaderUtil.newInstanceOf(var2);
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException | ClassNotFoundException var8) {
         this.mergeStrategy = new DefaultMergeStrategy();
      }

      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         AbstractConfiguration var4 = (AbstractConfiguration)var3.next();
         this.mergeStrategy.mergeRootProperties(this.rootNode, var4);
      }

      StatusConfiguration var9 = (new StatusConfiguration()).withVerboseClasses(VERBOSE_CLASSES).withStatus(this.getDefaultStatus());
      Iterator var10 = this.rootNode.getAttributes().entrySet().iterator();

      while(var10.hasNext()) {
         Entry var5 = (Entry)var10.next();
         String var6 = (String)var5.getKey();
         String var7 = this.getStrSubstitutor().replace((String)var5.getValue());
         if ("status".equalsIgnoreCase(var6)) {
            var9.withStatus(var7.toUpperCase());
         } else if ("dest".equalsIgnoreCase(var6)) {
            var9.withDestination(var7);
         } else if ("shutdownHook".equalsIgnoreCase(var6)) {
            this.isShutdownHookEnabled = !"disable".equalsIgnoreCase(var7);
         } else if ("shutdownTimeout".equalsIgnoreCase(var6)) {
            this.shutdownTimeoutMillis = Long.parseLong(var7);
         } else if ("verbose".equalsIgnoreCase(var6)) {
            var9.withVerbosity(var7);
         } else if ("packages".equalsIgnoreCase(var6)) {
            this.pluginPackages.addAll(Arrays.asList(var7.split(Patterns.COMMA_SEPARATOR)));
         } else if ("name".equalsIgnoreCase(var6)) {
            this.setName(var7);
         }
      }

      var9.initialize();
   }

   public void setup() {
      AbstractConfiguration var1 = (AbstractConfiguration)this.configurations.get(0);
      this.staffChildConfiguration(var1);
      WatchManager var2 = this.getWatchManager();
      WatchManager var3 = var1.getWatchManager();
      ConfiguratonFileWatcher var4 = new ConfiguratonFileWatcher(this, this.listeners);
      if (var3.getIntervalSeconds() > 0) {
         var2.setIntervalSeconds(var3.getIntervalSeconds());
         Map var5 = var3.getWatchers();
         Iterator var6 = var5.entrySet().iterator();

         while(var6.hasNext()) {
            Entry var7 = (Entry)var6.next();
            if (var7.getValue() instanceof ConfiguratonFileWatcher) {
               var2.watchFile((File)var7.getKey(), var4);
            }
         }
      }

      Iterator var14 = this.configurations.subList(1, this.configurations.size()).iterator();

      while(true) {
         AbstractConfiguration var15;
         int var17;
         do {
            if (!var14.hasNext()) {
               return;
            }

            var15 = (AbstractConfiguration)var14.next();
            this.staffChildConfiguration(var15);
            Node var16 = var15.getRootNode();
            this.mergeStrategy.mergConfigurations(this.rootNode, var16, this.getPluginManager());
            if (LOGGER.isEnabled(Level.ALL)) {
               StringBuilder var8 = new StringBuilder();
               this.printNodes("", this.rootNode, var8);
               System.out.println(var8.toString());
            }

            var17 = var15.getWatchManager().getIntervalSeconds();
         } while(var17 <= 0);

         int var9 = var2.getIntervalSeconds();
         if (var9 <= 0 || var17 < var9) {
            var2.setIntervalSeconds(var17);
         }

         WatchManager var10 = var15.getWatchManager();
         Map var11 = var10.getWatchers();
         Iterator var12 = var11.entrySet().iterator();

         while(var12.hasNext()) {
            Entry var13 = (Entry)var12.next();
            if (var13.getValue() instanceof ConfiguratonFileWatcher) {
               var2.watchFile((File)var13.getKey(), var4);
            }
         }
      }
   }

   public Configuration reconfigure() {
      LOGGER.debug("Reconfiguring composite configuration");
      ArrayList var1 = new ArrayList();
      ConfigurationFactory var2 = ConfigurationFactory.getInstance();

      Object var7;
      for(Iterator var3 = this.configurations.iterator(); var3.hasNext(); var1.add((AbstractConfiguration)var7)) {
         AbstractConfiguration var4 = (AbstractConfiguration)var3.next();
         ConfigurationSource var5 = var4.getConfigurationSource();
         URI var6 = var5.getURI();
         if (var6 != null) {
            LOGGER.warn((String)"Unable to determine URI for configuration {}, changes to it will be ignored", (Object)var4.getName());
            var7 = var2.getConfiguration(this.getLoggerContext(), var4.getName(), var6);
            if (var7 == null) {
               LOGGER.warn((String)"Unable to reload configuration {}, changes to it will be ignored", (Object)var4.getName());
               var7 = var4;
            }
         } else {
            var7 = var4;
         }
      }

      return new CompositeConfiguration(var1);
   }

   private void staffChildConfiguration(AbstractConfiguration var1) {
      var1.setPluginManager(this.pluginManager);
      var1.setScriptManager(this.scriptManager);
      var1.setup();
   }

   private void printNodes(String var1, Node var2, StringBuilder var3) {
      var3.append(var1).append(var2.getName()).append(" type: ").append(var2.getType()).append("\n");
      var3.append(var1).append(var2.getAttributes().toString()).append("\n");
      Iterator var4 = var2.getChildren().iterator();

      while(var4.hasNext()) {
         Node var5 = (Node)var4.next();
         this.printNodes(var1 + "  ", var5, var3);
      }

   }
}
