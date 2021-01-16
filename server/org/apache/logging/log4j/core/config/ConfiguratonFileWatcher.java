package org.apache.logging.log4j.core.config;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.core.util.FileWatcher;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;

public class ConfiguratonFileWatcher implements FileWatcher {
   private final Reconfigurable reconfigurable;
   private final List<ConfigurationListener> configurationListeners;
   private final Log4jThreadFactory threadFactory;

   public ConfiguratonFileWatcher(Reconfigurable var1, List<ConfigurationListener> var2) {
      super();
      this.reconfigurable = var1;
      this.configurationListeners = var2;
      this.threadFactory = Log4jThreadFactory.createDaemonThreadFactory("ConfiguratonFileWatcher");
   }

   public List<ConfigurationListener> getListeners() {
      return this.configurationListeners;
   }

   public void fileModified(File var1) {
      Iterator var2 = this.configurationListeners.iterator();

      while(var2.hasNext()) {
         ConfigurationListener var3 = (ConfigurationListener)var2.next();
         Thread var4 = this.threadFactory.newThread(new ConfiguratonFileWatcher.ReconfigurationRunnable(var3, this.reconfigurable));
         var4.start();
      }

   }

   private static class ReconfigurationRunnable implements Runnable {
      private final ConfigurationListener configurationListener;
      private final Reconfigurable reconfigurable;

      public ReconfigurationRunnable(ConfigurationListener var1, Reconfigurable var2) {
         super();
         this.configurationListener = var1;
         this.reconfigurable = var2;
      }

      public void run() {
         this.configurationListener.onChange(this.reconfigurable);
      }
   }
}
