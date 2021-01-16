package org.apache.logging.log4j.core.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.core.util.Cancellable;
import org.apache.logging.log4j.core.util.DefaultShutdownCallbackRegistry;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class Log4jContextFactory implements LoggerContextFactory, ShutdownCallbackRegistry {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static final boolean SHUTDOWN_HOOK_ENABLED = PropertiesUtil.getProperties().getBooleanProperty("log4j.shutdownHookEnabled", true);
   private final ContextSelector selector;
   private final ShutdownCallbackRegistry shutdownCallbackRegistry;

   public Log4jContextFactory() {
      this(createContextSelector(), createShutdownCallbackRegistry());
   }

   public Log4jContextFactory(ContextSelector var1) {
      this(var1, createShutdownCallbackRegistry());
   }

   public Log4jContextFactory(ShutdownCallbackRegistry var1) {
      this(createContextSelector(), var1);
   }

   public Log4jContextFactory(ContextSelector var1, ShutdownCallbackRegistry var2) {
      super();
      this.selector = (ContextSelector)Objects.requireNonNull(var1, "No ContextSelector provided");
      this.shutdownCallbackRegistry = (ShutdownCallbackRegistry)Objects.requireNonNull(var2, "No ShutdownCallbackRegistry provided");
      LOGGER.debug("Using ShutdownCallbackRegistry {}", var2.getClass());
      this.initializeShutdownCallbackRegistry();
   }

   private static ContextSelector createContextSelector() {
      try {
         ContextSelector var0 = (ContextSelector)LoaderUtil.newCheckedInstanceOfProperty("Log4jContextSelector", ContextSelector.class);
         if (var0 != null) {
            return var0;
         }
      } catch (Exception var1) {
         LOGGER.error("Unable to create custom ContextSelector. Falling back to default.", var1);
      }

      return new ClassLoaderContextSelector();
   }

   private static ShutdownCallbackRegistry createShutdownCallbackRegistry() {
      try {
         ShutdownCallbackRegistry var0 = (ShutdownCallbackRegistry)LoaderUtil.newCheckedInstanceOfProperty("log4j.shutdownCallbackRegistry", ShutdownCallbackRegistry.class);
         if (var0 != null) {
            return var0;
         }
      } catch (Exception var1) {
         LOGGER.error("Unable to create custom ShutdownCallbackRegistry. Falling back to default.", var1);
      }

      return new DefaultShutdownCallbackRegistry();
   }

   private void initializeShutdownCallbackRegistry() {
      if (SHUTDOWN_HOOK_ENABLED && this.shutdownCallbackRegistry instanceof LifeCycle) {
         try {
            ((LifeCycle)this.shutdownCallbackRegistry).start();
         } catch (IllegalStateException var2) {
            LOGGER.error("Cannot start ShutdownCallbackRegistry, already shutting down.");
            throw var2;
         } catch (RuntimeException var3) {
            LOGGER.error("There was an error starting the ShutdownCallbackRegistry.", var3);
         }
      }

   }

   public LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4) {
      LoggerContext var5 = this.selector.getContext(var1, var2, var4);
      if (var3 != null && var5.getExternalContext() == null) {
         var5.setExternalContext(var3);
      }

      if (var5.getState() == LifeCycle.State.INITIALIZED) {
         var5.start();
      }

      return var5;
   }

   public LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4, ConfigurationSource var5) {
      LoggerContext var6 = this.selector.getContext(var1, var2, var4, (URI)null);
      if (var3 != null && var6.getExternalContext() == null) {
         var6.setExternalContext(var3);
      }

      if (var6.getState() == LifeCycle.State.INITIALIZED) {
         if (var5 != null) {
            ContextAnchor.THREAD_CONTEXT.set(var6);
            Configuration var7 = ConfigurationFactory.getInstance().getConfiguration(var6, var5);
            LOGGER.debug("Starting LoggerContext[name={}] from configuration {}", var6.getName(), var5);
            var6.start(var7);
            ContextAnchor.THREAD_CONTEXT.remove();
         } else {
            var6.start();
         }
      }

      return var6;
   }

   public LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4, Configuration var5) {
      LoggerContext var6 = this.selector.getContext(var1, var2, var4, (URI)null);
      if (var3 != null && var6.getExternalContext() == null) {
         var6.setExternalContext(var3);
      }

      if (var6.getState() == LifeCycle.State.INITIALIZED) {
         ContextAnchor.THREAD_CONTEXT.set(var6);

         try {
            var6.start(var5);
         } finally {
            ContextAnchor.THREAD_CONTEXT.remove();
         }
      }

      return var6;
   }

   public LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4, URI var5, String var6) {
      LoggerContext var7 = this.selector.getContext(var1, var2, var4, var5);
      if (var3 != null && var7.getExternalContext() == null) {
         var7.setExternalContext(var3);
      }

      if (var6 != null) {
         var7.setName(var6);
      }

      if (var7.getState() == LifeCycle.State.INITIALIZED) {
         if (var5 == null && var6 == null) {
            var7.start();
         } else {
            ContextAnchor.THREAD_CONTEXT.set(var7);
            Configuration var8 = ConfigurationFactory.getInstance().getConfiguration(var7, var6, var5);
            LOGGER.debug("Starting LoggerContext[name={}] from configuration at {}", var7.getName(), var5);
            var7.start(var8);
            ContextAnchor.THREAD_CONTEXT.remove();
         }
      }

      return var7;
   }

   public LoggerContext getContext(String var1, ClassLoader var2, Object var3, boolean var4, List<URI> var5, String var6) {
      LoggerContext var7 = this.selector.getContext(var1, var2, var4, (URI)null);
      if (var3 != null && var7.getExternalContext() == null) {
         var7.setExternalContext(var3);
      }

      if (var6 != null) {
         var7.setName(var6);
      }

      if (var7.getState() == LifeCycle.State.INITIALIZED) {
         if (var5 != null && !var5.isEmpty()) {
            ContextAnchor.THREAD_CONTEXT.set(var7);
            ArrayList var8 = new ArrayList(var5.size());
            Iterator var9 = var5.iterator();

            while(var9.hasNext()) {
               URI var10 = (URI)var9.next();
               Configuration var11 = ConfigurationFactory.getInstance().getConfiguration(var7, var6, var10);
               if (var11 instanceof AbstractConfiguration) {
                  var8.add((AbstractConfiguration)var11);
               } else {
                  LOGGER.error("Found configuration {}, which is not an AbstractConfiguration and can't be handled by CompositeConfiguration", var10);
               }
            }

            CompositeConfiguration var12 = new CompositeConfiguration(var8);
            LOGGER.debug("Starting LoggerContext[name={}] from configurations at {}", var7.getName(), var5);
            var7.start(var12);
            ContextAnchor.THREAD_CONTEXT.remove();
         } else {
            var7.start();
         }
      }

      return var7;
   }

   public ContextSelector getSelector() {
      return this.selector;
   }

   public ShutdownCallbackRegistry getShutdownCallbackRegistry() {
      return this.shutdownCallbackRegistry;
   }

   public void removeContext(org.apache.logging.log4j.spi.LoggerContext var1) {
      if (var1 instanceof LoggerContext) {
         this.selector.removeContext((LoggerContext)var1);
      }

   }

   public Cancellable addShutdownCallback(Runnable var1) {
      return SHUTDOWN_HOOK_ENABLED ? this.shutdownCallbackRegistry.addShutdownCallback(var1) : null;
   }
}
