package org.apache.logging.log4j.core.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.async.AsyncLoggerConfigDelegate;
import org.apache.logging.log4j.core.async.AsyncLoggerConfigDisruptor;
import org.apache.logging.log4j.core.config.plugins.util.PluginBuilder;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.MapLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.core.script.ScriptRef;
import org.apache.logging.log4j.core.util.DummyNanoClock;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.NameUtil;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.util.PropertiesUtil;

public abstract class AbstractConfiguration extends AbstractFilterable implements Configuration {
   private static final int BUF_SIZE = 16384;
   protected Node rootNode;
   protected final List<ConfigurationListener> listeners = new CopyOnWriteArrayList();
   protected final List<String> pluginPackages = new ArrayList();
   protected PluginManager pluginManager;
   protected boolean isShutdownHookEnabled = true;
   protected long shutdownTimeoutMillis = 0L;
   protected ScriptManager scriptManager;
   private Advertiser advertiser = new DefaultAdvertiser();
   private Node advertiserNode = null;
   private Object advertisement;
   private String name;
   private ConcurrentMap<String, Appender> appenders = new ConcurrentHashMap();
   private ConcurrentMap<String, LoggerConfig> loggerConfigs = new ConcurrentHashMap();
   private List<CustomLevelConfig> customLevels = Collections.emptyList();
   private final ConcurrentMap<String, String> properties = new ConcurrentHashMap();
   private final StrLookup tempLookup;
   private final StrSubstitutor subst;
   private LoggerConfig root;
   private final ConcurrentMap<String, Object> componentMap;
   private final ConfigurationSource configurationSource;
   private final ConfigurationScheduler configurationScheduler;
   private final WatchManager watchManager;
   private AsyncLoggerConfigDisruptor asyncLoggerConfigDisruptor;
   private NanoClock nanoClock;
   private final WeakReference<LoggerContext> loggerContext;

   protected AbstractConfiguration(LoggerContext var1, ConfigurationSource var2) {
      super();
      this.tempLookup = new Interpolator(this.properties);
      this.subst = new StrSubstitutor(this.tempLookup);
      this.root = new LoggerConfig();
      this.componentMap = new ConcurrentHashMap();
      this.configurationScheduler = new ConfigurationScheduler();
      this.watchManager = new WatchManager(this.configurationScheduler);
      this.nanoClock = new DummyNanoClock();
      this.loggerContext = new WeakReference(var1);
      this.configurationSource = (ConfigurationSource)Objects.requireNonNull(var2, "configurationSource is null");
      this.componentMap.put("ContextProperties", this.properties);
      this.pluginManager = new PluginManager("Core");
      this.rootNode = new Node();
      this.setState(LifeCycle.State.INITIALIZING);
   }

   public ConfigurationSource getConfigurationSource() {
      return this.configurationSource;
   }

   public List<String> getPluginPackages() {
      return this.pluginPackages;
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   public ScriptManager getScriptManager() {
      return this.scriptManager;
   }

   public void setScriptManager(ScriptManager var1) {
      this.scriptManager = var1;
   }

   public PluginManager getPluginManager() {
      return this.pluginManager;
   }

   public void setPluginManager(PluginManager var1) {
      this.pluginManager = var1;
   }

   public WatchManager getWatchManager() {
      return this.watchManager;
   }

   public ConfigurationScheduler getScheduler() {
      return this.configurationScheduler;
   }

   public Node getRootNode() {
      return this.rootNode;
   }

   public AsyncLoggerConfigDelegate getAsyncLoggerConfigDelegate() {
      if (this.asyncLoggerConfigDisruptor == null) {
         this.asyncLoggerConfigDisruptor = new AsyncLoggerConfigDisruptor();
      }

      return this.asyncLoggerConfigDisruptor;
   }

   public void initialize() {
      LOGGER.debug((String)"Initializing configuration {}", (Object)this);
      this.subst.setConfiguration(this);
      this.scriptManager = new ScriptManager(this, this.watchManager);
      this.pluginManager.collectPlugins(this.pluginPackages);
      PluginManager var1 = new PluginManager("Level");
      var1.collectPlugins(this.pluginPackages);
      Map var2 = var1.getPlugins();
      if (var2 != null) {
         Iterator var3 = var2.values().iterator();

         while(var3.hasNext()) {
            PluginType var4 = (PluginType)var3.next();

            try {
               Loader.initializeClass(var4.getPluginClass().getName(), var4.getPluginClass().getClassLoader());
            } catch (Exception var6) {
               LOGGER.error((String)"Unable to initialize {} due to {}", (Object)var4.getPluginClass().getName(), var6.getClass().getSimpleName(), var6);
            }
         }
      }

      this.setup();
      this.setupAdvertisement();
      this.doConfigure();
      this.setState(LifeCycle.State.INITIALIZED);
      LOGGER.debug((String)"Configuration {} initialized", (Object)this);
   }

   public void start() {
      if (this.getState().equals(LifeCycle.State.INITIALIZING)) {
         this.initialize();
      }

      LOGGER.debug((String)"Starting configuration {}", (Object)this);
      this.setStarting();
      if (this.watchManager.getIntervalSeconds() > 0) {
         this.watchManager.start();
      }

      if (this.hasAsyncLoggers()) {
         this.asyncLoggerConfigDisruptor.start();
      }

      HashSet var1 = new HashSet();
      Iterator var2 = this.loggerConfigs.values().iterator();

      while(var2.hasNext()) {
         LoggerConfig var3 = (LoggerConfig)var2.next();
         var3.start();
         var1.add(var3);
      }

      var2 = this.appenders.values().iterator();

      while(var2.hasNext()) {
         Appender var4 = (Appender)var2.next();
         var4.start();
      }

      if (!var1.contains(this.root)) {
         this.root.start();
      }

      super.start();
      LOGGER.debug((String)"Started configuration {} OK.", (Object)this);
   }

   private boolean hasAsyncLoggers() {
      if (this.root instanceof AsyncLoggerConfig) {
         return true;
      } else {
         Iterator var1 = this.loggerConfigs.values().iterator();

         LoggerConfig var2;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            var2 = (LoggerConfig)var1.next();
         } while(!(var2 instanceof AsyncLoggerConfig));

         return true;
      }
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      LOGGER.trace((String)"Stopping {}...", (Object)this);
      Iterator var4 = this.loggerConfigs.values().iterator();

      while(var4.hasNext()) {
         LoggerConfig var5 = (LoggerConfig)var4.next();
         var5.getReliabilityStrategy().beforeStopConfiguration(this);
      }

      this.root.getReliabilityStrategy().beforeStopConfiguration(this);
      String var10 = this.getClass().getSimpleName();
      LOGGER.trace((String)"{} notified {} ReliabilityStrategies that config will be stopped.", (Object)var10, (Object)(this.loggerConfigs.size() + 1));
      if (!this.loggerConfigs.isEmpty()) {
         LOGGER.trace((String)"{} stopping {} LoggerConfigs.", (Object)var10, (Object)this.loggerConfigs.size());
         Iterator var11 = this.loggerConfigs.values().iterator();

         while(var11.hasNext()) {
            LoggerConfig var6 = (LoggerConfig)var11.next();
            var6.stop(var1, var3);
         }
      }

      LOGGER.trace((String)"{} stopping root LoggerConfig.", (Object)var10);
      if (!this.root.isStopped()) {
         this.root.stop(var1, var3);
      }

      if (this.hasAsyncLoggers()) {
         LOGGER.trace((String)"{} stopping AsyncLoggerConfigDisruptor.", (Object)var10);
         this.asyncLoggerConfigDisruptor.stop(var1, var3);
      }

      Appender[] var12 = (Appender[])this.appenders.values().toArray(new Appender[this.appenders.size()]);
      List var13 = this.getAsyncAppenders(var12);
      Iterator var7;
      if (!var13.isEmpty()) {
         LOGGER.trace((String)"{} stopping {} AsyncAppenders.", (Object)var10, (Object)var13.size());
         var7 = var13.iterator();

         while(var7.hasNext()) {
            Appender var8 = (Appender)var7.next();
            if (var8 instanceof LifeCycle2) {
               ((LifeCycle2)var8).stop(var1, var3);
            } else {
               var8.stop();
            }
         }
      }

      LOGGER.trace((String)"{} notifying ReliabilityStrategies that appenders will be stopped.", (Object)var10);
      var7 = this.loggerConfigs.values().iterator();

      while(var7.hasNext()) {
         LoggerConfig var15 = (LoggerConfig)var7.next();
         var15.getReliabilityStrategy().beforeStopAppenders();
      }

      this.root.getReliabilityStrategy().beforeStopAppenders();
      LOGGER.trace((String)"{} stopping remaining Appenders.", (Object)var10);
      int var14 = 0;

      for(int var16 = var12.length - 1; var16 >= 0; --var16) {
         if (var12[var16].isStarted()) {
            if (var12[var16] instanceof LifeCycle2) {
               ((LifeCycle2)var12[var16]).stop(var1, var3);
            } else {
               var12[var16].stop();
            }

            ++var14;
         }
      }

      LOGGER.trace((String)"{} stopped {} remaining Appenders.", (Object)var10, (Object)var14);
      LOGGER.trace((String)"{} cleaning Appenders from {} LoggerConfigs.", (Object)var10, (Object)(this.loggerConfigs.size() + 1));
      Iterator var17 = this.loggerConfigs.values().iterator();

      while(var17.hasNext()) {
         LoggerConfig var9 = (LoggerConfig)var17.next();
         var9.clearAppenders();
      }

      this.root.clearAppenders();
      if (this.watchManager.isStarted()) {
         this.watchManager.stop(var1, var3);
      }

      this.configurationScheduler.stop(var1, var3);
      if (this.advertiser != null && this.advertisement != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

      this.setStopped();
      LOGGER.debug((String)"Stopped {} OK", (Object)this);
      return true;
   }

   private List<Appender> getAsyncAppenders(Appender[] var1) {
      ArrayList var2 = new ArrayList();

      for(int var3 = var1.length - 1; var3 >= 0; --var3) {
         if (var1[var3] instanceof AsyncAppender) {
            var2.add(var1[var3]);
         }
      }

      return var2;
   }

   public boolean isShutdownHookEnabled() {
      return this.isShutdownHookEnabled;
   }

   public long getShutdownTimeoutMillis() {
      return this.shutdownTimeoutMillis;
   }

   public void setup() {
   }

   protected Level getDefaultStatus() {
      String var1 = PropertiesUtil.getProperties().getStringProperty("Log4jDefaultStatusLevel", Level.ERROR.name());

      try {
         return Level.toLevel(var1);
      } catch (Exception var3) {
         return Level.ERROR;
      }
   }

   protected void createAdvertiser(String var1, ConfigurationSource var2, byte[] var3, String var4) {
      if (var1 != null) {
         Node var5 = new Node((Node)null, var1, (PluginType)null);
         Map var6 = var5.getAttributes();
         var6.put("content", new String(var3));
         var6.put("contentType", var4);
         var6.put("name", "configuration");
         if (var2.getLocation() != null) {
            var6.put("location", var2.getLocation());
         }

         this.advertiserNode = var5;
      }

   }

   private void setupAdvertisement() {
      if (this.advertiserNode != null) {
         String var1 = this.advertiserNode.getName();
         PluginType var2 = this.pluginManager.getPluginType(var1);
         if (var2 != null) {
            Class var3 = var2.getPluginClass().asSubclass(Advertiser.class);

            try {
               this.advertiser = (Advertiser)var3.newInstance();
               this.advertisement = this.advertiser.advertise(this.advertiserNode.getAttributes());
            } catch (InstantiationException var5) {
               LOGGER.error((String)"InstantiationException attempting to instantiate advertiser: {}", (Object)var1, (Object)var5);
            } catch (IllegalAccessException var6) {
               LOGGER.error((String)"IllegalAccessException attempting to instantiate advertiser: {}", (Object)var1, (Object)var6);
            }
         }
      }

   }

   public <T> T getComponent(String var1) {
      return this.componentMap.get(var1);
   }

   public void addComponent(String var1, Object var2) {
      this.componentMap.putIfAbsent(var1, var2);
   }

   protected void preConfigure(Node var1) {
      try {
         Iterator var2 = var1.getChildren().iterator();

         while(var2.hasNext()) {
            Node var3 = (Node)var2.next();
            if (var3.getType() == null) {
               LOGGER.error("Unable to locate plugin type for " + var3.getName());
            } else {
               Class var4 = var3.getType().getPluginClass();
               if (var4.isAnnotationPresent(Scheduled.class)) {
                  this.configurationScheduler.incrementScheduledItems();
               }

               this.preConfigure(var3);
            }
         }
      } catch (Exception var5) {
         LOGGER.error((String)("Error capturing node data for node " + var1.getName()), (Throwable)var5);
      }

   }

   protected void doConfigure() {
      this.preConfigure(this.rootNode);
      this.configurationScheduler.start();
      if (this.rootNode.hasChildren() && ((Node)this.rootNode.getChildren().get(0)).getName().equalsIgnoreCase("Properties")) {
         Node var9 = (Node)this.rootNode.getChildren().get(0);
         this.createConfiguration(var9, (LogEvent)null);
         if (var9.getObject() != null) {
            this.subst.setVariableResolver((StrLookup)var9.getObject());
         }
      } else {
         Map var1 = (Map)this.getComponent("ContextProperties");
         MapLookup var2 = var1 == null ? null : new MapLookup(var1);
         this.subst.setVariableResolver(new Interpolator(var2, this.pluginPackages));
      }

      boolean var10 = false;
      boolean var11 = false;
      Iterator var3 = this.rootNode.getChildren().iterator();

      while(true) {
         while(var3.hasNext()) {
            Node var4 = (Node)var3.next();
            if (var4.getName().equalsIgnoreCase("Properties")) {
               if (this.tempLookup == this.subst.getVariableResolver()) {
                  LOGGER.error("Properties declaration must be the first element in the configuration");
               }
            } else {
               this.createConfiguration(var4, (LogEvent)null);
               if (var4.getObject() != null) {
                  if (var4.getName().equalsIgnoreCase("Scripts")) {
                     AbstractScript[] var15 = (AbstractScript[])var4.getObject(AbstractScript[].class);
                     int var6 = var15.length;

                     for(int var7 = 0; var7 < var6; ++var7) {
                        AbstractScript var8 = var15[var7];
                        if (var8 instanceof ScriptRef) {
                           LOGGER.error((String)"Script reference to {} not added. Scripts definition cannot contain script references", (Object)var8.getName());
                        } else {
                           this.scriptManager.addScript(var8);
                        }
                     }
                  } else if (var4.getName().equalsIgnoreCase("Appenders")) {
                     this.appenders = (ConcurrentMap)var4.getObject();
                  } else if (var4.isInstanceOf(Filter.class)) {
                     this.addFilter((Filter)var4.getObject(Filter.class));
                  } else if (var4.getName().equalsIgnoreCase("Loggers")) {
                     Loggers var5 = (Loggers)var4.getObject();
                     this.loggerConfigs = var5.getMap();
                     var10 = true;
                     if (var5.getRoot() != null) {
                        this.root = var5.getRoot();
                        var11 = true;
                     }
                  } else if (var4.getName().equalsIgnoreCase("CustomLevels")) {
                     this.customLevels = ((CustomLevels)var4.getObject(CustomLevels.class)).getCustomLevels();
                  } else if (var4.isInstanceOf(CustomLevelConfig.class)) {
                     ArrayList var13 = new ArrayList(this.customLevels);
                     var13.add(var4.getObject(CustomLevelConfig.class));
                     this.customLevels = var13;
                  } else {
                     List var14 = Arrays.asList("\"Appenders\"", "\"Loggers\"", "\"Properties\"", "\"Scripts\"", "\"CustomLevels\"");
                     LOGGER.error((String)"Unknown object \"{}\" of type {} is ignored: try nesting it inside one of: {}.", (Object)var4.getName(), var4.getObject().getClass().getName(), var14);
                  }
               }
            }
         }

         if (!var10) {
            LOGGER.warn("No Loggers were configured, using default. Is the Loggers element missing?");
            this.setToDefault();
            return;
         }

         if (!var11) {
            LOGGER.warn("No Root logger was configured, creating default ERROR-level Root logger with Console appender");
            this.setToDefault();
         }

         var3 = this.loggerConfigs.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var12 = (Entry)var3.next();
            LoggerConfig var16 = (LoggerConfig)var12.getValue();
            Iterator var17 = var16.getAppenderRefs().iterator();

            while(var17.hasNext()) {
               AppenderRef var18 = (AppenderRef)var17.next();
               Appender var19 = (Appender)this.appenders.get(var18.getRef());
               if (var19 != null) {
                  var16.addAppender(var19, var18.getLevel(), var18.getFilter());
               } else {
                  LOGGER.error((String)"Unable to locate appender \"{}\" for logger config \"{}\"", (Object)var18.getRef(), (Object)var16);
               }
            }
         }

         this.setParents();
         return;
      }
   }

   protected void setToDefault() {
      this.setName("Default@" + Integer.toHexString(this.hashCode()));
      PatternLayout var1 = PatternLayout.newBuilder().withPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n").withConfiguration(this).build();
      ConsoleAppender var2 = ConsoleAppender.createDefaultAppenderForLayout(var1);
      var2.start();
      this.addAppender(var2);
      LoggerConfig var3 = this.getRootLogger();
      var3.addAppender(var2, (Level)null, (Filter)null);
      Level var4 = Level.ERROR;
      String var5 = PropertiesUtil.getProperties().getStringProperty("org.apache.logging.log4j.level", var4.name());
      Level var6 = Level.valueOf(var5);
      var3.setLevel(var6 != null ? var6 : var4);
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public void addListener(ConfigurationListener var1) {
      this.listeners.add(var1);
   }

   public void removeListener(ConfigurationListener var1) {
      this.listeners.remove(var1);
   }

   public <T extends Appender> T getAppender(String var1) {
      return (Appender)this.appenders.get(var1);
   }

   public Map<String, Appender> getAppenders() {
      return this.appenders;
   }

   public void addAppender(Appender var1) {
      this.appenders.putIfAbsent(var1.getName(), var1);
   }

   public StrSubstitutor getStrSubstitutor() {
      return this.subst;
   }

   public void setAdvertiser(Advertiser var1) {
      this.advertiser = var1;
   }

   public Advertiser getAdvertiser() {
      return this.advertiser;
   }

   public ReliabilityStrategy getReliabilityStrategy(LoggerConfig var1) {
      return ReliabilityStrategyFactory.getReliabilityStrategy(var1);
   }

   public synchronized void addLoggerAppender(Logger var1, Appender var2) {
      String var3 = var1.getName();
      this.appenders.putIfAbsent(var2.getName(), var2);
      LoggerConfig var4 = this.getLoggerConfig(var3);
      if (var4.getName().equals(var3)) {
         var4.addAppender(var2, (Level)null, (Filter)null);
      } else {
         LoggerConfig var5 = new LoggerConfig(var3, var4.getLevel(), var4.isAdditive());
         var5.addAppender(var2, (Level)null, (Filter)null);
         var5.setParent(var4);
         this.loggerConfigs.putIfAbsent(var3, var5);
         this.setParents();
         var1.getContext().updateLoggers();
      }

   }

   public synchronized void addLoggerFilter(Logger var1, Filter var2) {
      String var3 = var1.getName();
      LoggerConfig var4 = this.getLoggerConfig(var3);
      if (var4.getName().equals(var3)) {
         var4.addFilter(var2);
      } else {
         LoggerConfig var5 = new LoggerConfig(var3, var4.getLevel(), var4.isAdditive());
         var5.addFilter(var2);
         var5.setParent(var4);
         this.loggerConfigs.putIfAbsent(var3, var5);
         this.setParents();
         var1.getContext().updateLoggers();
      }

   }

   public synchronized void setLoggerAdditive(Logger var1, boolean var2) {
      String var3 = var1.getName();
      LoggerConfig var4 = this.getLoggerConfig(var3);
      if (var4.getName().equals(var3)) {
         var4.setAdditive(var2);
      } else {
         LoggerConfig var5 = new LoggerConfig(var3, var4.getLevel(), var2);
         var5.setParent(var4);
         this.loggerConfigs.putIfAbsent(var3, var5);
         this.setParents();
         var1.getContext().updateLoggers();
      }

   }

   public synchronized void removeAppender(String var1) {
      Iterator var2 = this.loggerConfigs.values().iterator();

      while(var2.hasNext()) {
         LoggerConfig var3 = (LoggerConfig)var2.next();
         var3.removeAppender(var1);
      }

      Appender var4 = (Appender)this.appenders.remove(var1);
      if (var4 != null) {
         var4.stop();
      }

   }

   public List<CustomLevelConfig> getCustomLevels() {
      return Collections.unmodifiableList(this.customLevels);
   }

   public LoggerConfig getLoggerConfig(String var1) {
      LoggerConfig var2 = (LoggerConfig)this.loggerConfigs.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = var1;

         do {
            if ((var3 = NameUtil.getSubName(var3)) == null) {
               return this.root;
            }

            var2 = (LoggerConfig)this.loggerConfigs.get(var3);
         } while(var2 == null);

         return var2;
      }
   }

   public LoggerContext getLoggerContext() {
      return (LoggerContext)this.loggerContext.get();
   }

   public LoggerConfig getRootLogger() {
      return this.root;
   }

   public Map<String, LoggerConfig> getLoggers() {
      return Collections.unmodifiableMap(this.loggerConfigs);
   }

   public LoggerConfig getLogger(String var1) {
      return (LoggerConfig)this.loggerConfigs.get(var1);
   }

   public synchronized void addLogger(String var1, LoggerConfig var2) {
      this.loggerConfigs.putIfAbsent(var1, var2);
      this.setParents();
   }

   public synchronized void removeLogger(String var1) {
      this.loggerConfigs.remove(var1);
      this.setParents();
   }

   public void createConfiguration(Node var1, LogEvent var2) {
      PluginType var3 = var1.getType();
      if (var3 != null && var3.isDeferChildren()) {
         var1.setObject(this.createPluginObject(var3, var1, var2));
      } else {
         Iterator var4 = var1.getChildren().iterator();

         while(var4.hasNext()) {
            Node var5 = (Node)var4.next();
            this.createConfiguration(var5, var2);
         }

         if (var3 == null) {
            if (var1.getParent() != null) {
               LOGGER.error((String)"Unable to locate plugin for {}", (Object)var1.getName());
            }
         } else {
            var1.setObject(this.createPluginObject(var3, var1, var2));
         }
      }

   }

   private Object createPluginObject(PluginType<?> var1, Node var2, LogEvent var3) {
      Class var4 = var1.getPluginClass();
      if (Map.class.isAssignableFrom(var4)) {
         try {
            return createPluginMap(var2);
         } catch (Exception var7) {
            LOGGER.warn((String)"Unable to create Map for {} of class {}", (Object)var1.getElementName(), var4, var7);
         }
      }

      if (Collection.class.isAssignableFrom(var4)) {
         try {
            return createPluginCollection(var2);
         } catch (Exception var6) {
            LOGGER.warn((String)"Unable to create List for {} of class {}", (Object)var1.getElementName(), var4, var6);
         }
      }

      return (new PluginBuilder(var1)).withConfiguration(this).withConfigurationNode(var2).forLogEvent(var3).build();
   }

   private static Map<String, ?> createPluginMap(Node var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      Iterator var2 = var0.getChildren().iterator();

      while(var2.hasNext()) {
         Node var3 = (Node)var2.next();
         Object var4 = var3.getObject();
         var1.put(var3.getName(), var4);
      }

      return var1;
   }

   private static Collection<?> createPluginCollection(Node var0) {
      List var1 = var0.getChildren();
      ArrayList var2 = new ArrayList(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Node var4 = (Node)var3.next();
         Object var5 = var4.getObject();
         var2.add(var5);
      }

      return var2;
   }

   private void setParents() {
      Iterator var1 = this.loggerConfigs.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         LoggerConfig var3 = (LoggerConfig)var2.getValue();
         String var4 = (String)var2.getKey();
         if (!var4.isEmpty()) {
            int var5 = var4.lastIndexOf(46);
            if (var5 > 0) {
               var4 = var4.substring(0, var5);
               LoggerConfig var6 = this.getLoggerConfig(var4);
               if (var6 == null) {
                  var6 = this.root;
               }

               var3.setParent(var6);
            } else {
               var3.setParent(this.root);
            }
         }
      }

   }

   protected static byte[] toByteArray(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      byte[] var3 = new byte[16384];

      int var2;
      while((var2 = var0.read(var3, 0, var3.length)) != -1) {
         var1.write(var3, 0, var2);
      }

      return var1.toByteArray();
   }

   public NanoClock getNanoClock() {
      return this.nanoClock;
   }

   public void setNanoClock(NanoClock var1) {
      this.nanoClock = (NanoClock)Objects.requireNonNull(var1, "nanoClock");
   }
}
