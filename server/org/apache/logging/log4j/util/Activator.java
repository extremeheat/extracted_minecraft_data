package org.apache.logging.log4j.util;

import java.net.URL;
import java.security.Permission;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.AdaptPermission;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

public class Activator implements BundleActivator, SynchronousBundleListener {
   private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();
   private static final Logger LOGGER = StatusLogger.getLogger();
   private boolean lockingProviderUtil;

   public Activator() {
      super();
   }

   private static void checkPermission(Permission var0) {
      if (SECURITY_MANAGER != null) {
         SECURITY_MANAGER.checkPermission(var0);
      }

   }

   private void loadProvider(Bundle var1) {
      if (var1.getState() != 1) {
         try {
            checkPermission(new AdminPermission(var1, "resource"));
            checkPermission(new AdaptPermission(BundleWiring.class.getName(), var1, "adapt"));
            this.loadProvider((BundleWiring)var1.adapt(BundleWiring.class));
         } catch (SecurityException var3) {
            LOGGER.debug((String)"Cannot access bundle [{}] contents. Ignoring.", (Object)var1.getSymbolicName(), (Object)var3);
         } catch (Exception var4) {
            LOGGER.warn((String)"Problem checking bundle {} for Log4j 2 provider.", (Object)var1.getSymbolicName(), (Object)var4);
         }

      }
   }

   private void loadProvider(BundleWiring var1) {
      List var2 = var1.findEntries("META-INF", "log4j-provider.properties", 0);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         URL var4 = (URL)var3.next();
         ProviderUtil.loadProvider(var4, var1.getClassLoader());
      }

   }

   public void start(BundleContext var1) throws Exception {
      ProviderUtil.STARTUP_LOCK.lock();
      this.lockingProviderUtil = true;
      BundleWiring var2 = (BundleWiring)var1.getBundle().adapt(BundleWiring.class);
      List var3 = var2.getRequiredWires(LoggerContextFactory.class.getName());
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         BundleWire var5 = (BundleWire)var4.next();
         this.loadProvider(var5.getProviderWiring());
      }

      var1.addBundleListener(this);
      Bundle[] var9 = var1.getBundles();
      Bundle[] var10 = var9;
      int var6 = var9.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Bundle var8 = var10[var7];
         this.loadProvider(var8);
      }

      this.unlockIfReady();
   }

   private void unlockIfReady() {
      if (this.lockingProviderUtil && !ProviderUtil.PROVIDERS.isEmpty()) {
         ProviderUtil.STARTUP_LOCK.unlock();
         this.lockingProviderUtil = false;
      }

   }

   public void stop(BundleContext var1) throws Exception {
      var1.removeBundleListener(this);
      this.unlockIfReady();
   }

   public void bundleChanged(BundleEvent var1) {
      switch(var1.getType()) {
      case 2:
         this.loadProvider(var1.getBundle());
         this.unlockIfReady();
      default:
      }
   }
}
