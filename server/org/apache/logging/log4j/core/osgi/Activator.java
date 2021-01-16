package org.apache.logging.log4j.core.osgi;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleWiring;

public final class Activator implements BundleActivator, SynchronousBundleListener {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final AtomicReference<BundleContext> contextRef = new AtomicReference();

   public Activator() {
      super();
   }

   public void start(BundleContext var1) throws Exception {
      if (PropertiesUtil.getProperties().getStringProperty("Log4jContextSelector") == null) {
         System.setProperty("Log4jContextSelector", BundleContextSelector.class.getName());
      }

      if (this.contextRef.compareAndSet((Object)null, var1)) {
         var1.addBundleListener(this);
         scanInstalledBundlesForPlugins(var1);
      }

   }

   private static void scanInstalledBundlesForPlugins(BundleContext var0) {
      Bundle[] var1 = var0.getBundles();
      Bundle[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Bundle var5 = var2[var4];
         if (var5.getState() == 32 && var5.getBundleId() != 0L) {
            scanBundleForPlugins(var5);
         }
      }

   }

   private static void scanBundleForPlugins(Bundle var0) {
      LOGGER.trace((String)"Scanning bundle [{}] for plugins.", (Object)var0.getSymbolicName());
      PluginRegistry.getInstance().loadFromBundle(var0.getBundleId(), ((BundleWiring)var0.adapt(BundleWiring.class)).getClassLoader());
   }

   private static void stopBundlePlugins(Bundle var0) {
      LOGGER.trace((String)"Stopping bundle [{}] plugins.", (Object)var0.getSymbolicName());
      PluginRegistry.getInstance().clearBundlePlugins(var0.getBundleId());
   }

   public void stop(BundleContext var1) throws Exception {
      this.contextRef.compareAndSet(var1, (Object)null);
      LogManager.shutdown();
   }

   public void bundleChanged(BundleEvent var1) {
      switch(var1.getType()) {
      case 2:
         scanBundleForPlugins(var1.getBundle());
         break;
      case 256:
         stopBundlePlugins(var1.getBundle());
      }

   }
}
