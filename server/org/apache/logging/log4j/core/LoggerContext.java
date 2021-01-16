package org.apache.logging.log4j.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.NullConfiguration;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.jmx.Server;
import org.apache.logging.log4j.core.util.Cancellable;
import org.apache.logging.log4j.core.util.ExecutorServices;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.apache.logging.log4j.spi.Terminable;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class LoggerContext extends AbstractLifeCycle implements org.apache.logging.log4j.spi.LoggerContext, AutoCloseable, Terminable, ConfigurationListener {
   public static final String PROPERTY_CONFIG = "config";
   private static final Configuration NULL_CONFIGURATION;
   private final LoggerRegistry<Logger> loggerRegistry;
   private final CopyOnWriteArrayList<PropertyChangeListener> propertyChangeListeners;
   private volatile Configuration configuration;
   private Object externalContext;
   private String contextName;
   private volatile URI configLocation;
   private Cancellable shutdownCallback;
   private final Lock configLock;

   public LoggerContext(String var1) {
      this(var1, (Object)null, (URI)((URI)null));
   }

   public LoggerContext(String var1, Object var2) {
      this(var1, var2, (URI)null);
   }

   public LoggerContext(String var1, Object var2, URI var3) {
      super();
      this.loggerRegistry = new LoggerRegistry();
      this.propertyChangeListeners = new CopyOnWriteArrayList();
      this.configuration = new DefaultConfiguration();
      this.configLock = new ReentrantLock();
      this.contextName = var1;
      this.externalContext = var2;
      this.configLocation = var3;
   }

   public LoggerContext(String var1, Object var2, String var3) {
      super();
      this.loggerRegistry = new LoggerRegistry();
      this.propertyChangeListeners = new CopyOnWriteArrayList();
      this.configuration = new DefaultConfiguration();
      this.configLock = new ReentrantLock();
      this.contextName = var1;
      this.externalContext = var2;
      if (var3 != null) {
         URI var4;
         try {
            var4 = (new File(var3)).toURI();
         } catch (Exception var6) {
            var4 = null;
         }

         this.configLocation = var4;
      } else {
         this.configLocation = null;
      }

   }

   public static LoggerContext getContext() {
      return (LoggerContext)LogManager.getContext();
   }

   public static LoggerContext getContext(boolean var0) {
      return (LoggerContext)LogManager.getContext(var0);
   }

   public static LoggerContext getContext(ClassLoader var0, boolean var1, URI var2) {
      return (LoggerContext)LogManager.getContext(var0, var1, var2);
   }

   public void start() {
      LOGGER.debug((String)"Starting LoggerContext[name={}, {}]...", (Object)this.getName(), (Object)this);
      if (PropertiesUtil.getProperties().getBooleanProperty("log4j.LoggerContext.stacktrace.on.start", false)) {
         LOGGER.debug((String)"Stack trace to locate invoker", (Throwable)(new Exception("Not a real error, showing stack trace to locate invoker")));
      }

      if (this.configLock.tryLock()) {
         try {
            if (this.isInitialized() || this.isStopped()) {
               this.setStarting();
               this.reconfigure();
               if (this.configuration.isShutdownHookEnabled()) {
                  this.setUpShutdownHook();
               }

               this.setStarted();
            }
         } finally {
            this.configLock.unlock();
         }
      }

      LOGGER.debug((String)"LoggerContext[name={}, {}] started OK.", (Object)this.getName(), (Object)this);
   }

   public void start(Configuration var1) {
      LOGGER.debug((String)"Starting LoggerContext[name={}, {}] with configuration {}...", (Object)this.getName(), this, var1);
      if (this.configLock.tryLock()) {
         try {
            if (this.isInitialized() || this.isStopped()) {
               if (this.configuration.isShutdownHookEnabled()) {
                  this.setUpShutdownHook();
               }

               this.setStarted();
            }
         } finally {
            this.configLock.unlock();
         }
      }

      this.setConfiguration(var1);
      LOGGER.debug((String)"LoggerContext[name={}, {}] started OK with configuration {}.", (Object)this.getName(), this, var1);
   }

   private void setUpShutdownHook() {
      if (this.shutdownCallback == null) {
         LoggerContextFactory var1 = LogManager.getFactory();
         if (var1 instanceof ShutdownCallbackRegistry) {
            LOGGER.debug(ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, "Shutdown hook enabled. Registering a new one.");

            try {
               final long var2 = this.configuration.getShutdownTimeoutMillis();
               this.shutdownCallback = ((ShutdownCallbackRegistry)var1).addShutdownCallback(new Runnable() {
                  public void run() {
                     LoggerContext var1 = LoggerContext.this;
                     AbstractLifeCycle.LOGGER.debug((Marker)ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, (String)"Stopping LoggerContext[name={}, {}]", var1.getName(), var1);
                     var1.stop(var2, TimeUnit.MILLISECONDS);
                  }

                  public String toString() {
                     return "Shutdown callback for LoggerContext[name=" + LoggerContext.this.getName() + ']';
                  }
               });
            } catch (IllegalStateException var4) {
               throw new IllegalStateException("Unable to register Log4j shutdown hook because JVM is shutting down.", var4);
            } catch (SecurityException var5) {
               LOGGER.error((Marker)ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, (String)"Unable to register shutdown hook due to security restrictions", (Throwable)var5);
            }
         }
      }

   }

   public void close() {
      this.stop();
   }

   public void terminate() {
      this.stop();
   }

   public boolean stop(long var1, TimeUnit var3) {
      LOGGER.debug((String)"Stopping LoggerContext[name={}, {}]...", (Object)this.getName(), (Object)this);
      this.configLock.lock();

      label78: {
         boolean var4;
         try {
            if (!this.isStopped()) {
               this.setStopping();

               try {
                  Server.unregisterLoggerContext(this.getName());
               } catch (Exception | LinkageError var8) {
                  LOGGER.error((String)"Unable to unregister MBeans", (Throwable)var8);
               }

               if (this.shutdownCallback != null) {
                  this.shutdownCallback.cancel();
                  this.shutdownCallback = null;
               }

               Configuration var10 = this.configuration;
               this.configuration = NULL_CONFIGURATION;
               this.updateLoggers();
               if (var10 instanceof LifeCycle2) {
                  ((LifeCycle2)var10).stop(var1, var3);
               } else {
                  var10.stop();
               }

               this.externalContext = null;
               LogManager.getFactory().removeContext(this);
               break label78;
            }

            var4 = true;
         } finally {
            this.configLock.unlock();
            this.setStopped();
         }

         return var4;
      }

      LOGGER.debug((String)"Stopped LoggerContext[name={}, {}] with status {}", (Object)this.getName(), this, true);
      return true;
   }

   public String getName() {
      return this.contextName;
   }

   public Logger getRootLogger() {
      return this.getLogger("");
   }

   public void setName(String var1) {
      this.contextName = (String)Objects.requireNonNull(var1);
   }

   public void setExternalContext(Object var1) {
      this.externalContext = var1;
   }

   public Object getExternalContext() {
      return this.externalContext;
   }

   public Logger getLogger(String var1) {
      return this.getLogger(var1, (MessageFactory)null);
   }

   public Collection<Logger> getLoggers() {
      return this.loggerRegistry.getLoggers();
   }

   public Logger getLogger(String var1, MessageFactory var2) {
      Logger var3 = (Logger)this.loggerRegistry.getLogger(var1, var2);
      if (var3 != null) {
         AbstractLogger.checkMessageFactory(var3, var2);
         return var3;
      } else {
         var3 = this.newInstance(this, var1, var2);
         this.loggerRegistry.putIfAbsent(var1, var2, var3);
         return (Logger)this.loggerRegistry.getLogger(var1, var2);
      }
   }

   public boolean hasLogger(String var1) {
      return this.loggerRegistry.hasLogger(var1);
   }

   public boolean hasLogger(String var1, MessageFactory var2) {
      return this.loggerRegistry.hasLogger(var1, var2);
   }

   public boolean hasLogger(String var1, Class<? extends MessageFactory> var2) {
      return this.loggerRegistry.hasLogger(var1, var2);
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public void addFilter(Filter var1) {
      this.configuration.addFilter(var1);
   }

   public void removeFilter(Filter var1) {
      this.configuration.removeFilter(var1);
   }

   private Configuration setConfiguration(Configuration var1) {
      if (var1 == null) {
         LOGGER.error((String)"No configuration found for context '{}'.", (Object)this.contextName);
         return this.configuration;
      } else {
         this.configLock.lock();

         Configuration var4;
         try {
            Configuration var2 = this.configuration;
            var1.addListener(this);
            ConcurrentMap var3 = (ConcurrentMap)var1.getComponent("ContextProperties");

            try {
               var3.putIfAbsent("hostName", NetUtils.getLocalHostname());
            } catch (Exception var10) {
               LOGGER.debug((String)"Ignoring {}, setting hostName to 'unknown'", (Object)var10.toString());
               var3.putIfAbsent("hostName", "unknown");
            }

            var3.putIfAbsent("contextName", this.contextName);
            var1.start();
            this.configuration = var1;
            this.updateLoggers();
            if (var2 != null) {
               var2.removeListener(this);
               var2.stop();
            }

            this.firePropertyChangeEvent(new PropertyChangeEvent(this, "config", var2, var1));

            try {
               Server.reregisterMBeansAfterReconfigure();
            } catch (Exception | LinkageError var9) {
               LOGGER.error((String)"Could not reconfigure JMX", (Throwable)var9);
            }

            Log4jLogEvent.setNanoClock(this.configuration.getNanoClock());
            var4 = var2;
         } finally {
            this.configLock.unlock();
         }

         return var4;
      }
   }

   private void firePropertyChangeEvent(PropertyChangeEvent var1) {
      Iterator var2 = this.propertyChangeListeners.iterator();

      while(var2.hasNext()) {
         PropertyChangeListener var3 = (PropertyChangeListener)var2.next();
         var3.propertyChange(var1);
      }

   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      this.propertyChangeListeners.add(Objects.requireNonNull(var1, "listener"));
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      this.propertyChangeListeners.remove(var1);
   }

   public URI getConfigLocation() {
      return this.configLocation;
   }

   public void setConfigLocation(URI var1) {
      this.configLocation = var1;
      this.reconfigure(var1);
   }

   private void reconfigure(URI var1) {
      ClassLoader var2 = ClassLoader.class.isInstance(this.externalContext) ? (ClassLoader)this.externalContext : null;
      LOGGER.debug((String)"Reconfiguration started for context[name={}] at URI {} ({}) with optional ClassLoader: {}", (Object)this.contextName, var1, this, var2);
      Configuration var3 = ConfigurationFactory.getInstance().getConfiguration(this, this.contextName, var1, var2);
      if (var3 == null) {
         LOGGER.error((String)"Reconfiguration failed: No configuration found for '{}' at '{}' in '{}'", (Object)this.contextName, var1, var2);
      } else {
         this.setConfiguration(var3);
         String var4 = this.configuration == null ? "?" : String.valueOf(this.configuration.getConfigurationSource());
         LOGGER.debug((String)"Reconfiguration complete for context[name={}] at URI {} ({}) with optional ClassLoader: {}", (Object)this.contextName, var4, this, var2);
      }

   }

   public void reconfigure() {
      this.reconfigure(this.configLocation);
   }

   public void updateLoggers() {
      this.updateLoggers(this.configuration);
   }

   public void updateLoggers(Configuration var1) {
      Configuration var2 = this.configuration;
      Iterator var3 = this.loggerRegistry.getLoggers().iterator();

      while(var3.hasNext()) {
         Logger var4 = (Logger)var3.next();
         var4.updateConfiguration(var1);
      }

      this.firePropertyChangeEvent(new PropertyChangeEvent(this, "config", var2, var1));
   }

   public synchronized void onChange(Reconfigurable var1) {
      LOGGER.debug((String)"Reconfiguration started for context {} ({})", (Object)this.contextName, (Object)this);
      Configuration var2 = var1.reconfigure();
      if (var2 != null) {
         this.setConfiguration(var2);
         LOGGER.debug((String)"Reconfiguration completed for {} ({})", (Object)this.contextName, (Object)this);
      } else {
         LOGGER.debug((String)"Reconfiguration failed for {} ({})", (Object)this.contextName, (Object)this);
      }

   }

   protected Logger newInstance(LoggerContext var1, String var2, MessageFactory var3) {
      return new Logger(var1, var2, var3);
   }

   static {
      try {
         LoaderUtil.loadClass(ExecutorServices.class.getName());
      } catch (Exception var1) {
         LOGGER.error((String)"Failed to preload ExecutorServices class.", (Throwable)var1);
      }

      NULL_CONFIGURATION = new NullConfiguration();
   }
}
