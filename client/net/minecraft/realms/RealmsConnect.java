package net.minecraft.realms;

import net.minecraft.network.NetworkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConnect {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen onlineScreen;
   private volatile boolean aborted = false;
   private NetworkManager connection;

   public RealmsConnect(RealmsScreen var1) {
      super();
      this.onlineScreen = var1;
   }

   public void connect(String var1, int var2) {
      new RealmsConnect$1(this, "Realms-connect-task", var1, var2).start();
   }

   public void abort() {
      this.aborted = true;
   }

   public void tick() {
      if (this.connection != null) {
         if (this.connection.func_150724_d()) {
            this.connection.func_74428_b();
         } else if (this.connection.func_150730_f() != null) {
            this.connection.func_150729_e().func_147231_a(this.connection.func_150730_f());
         }
      }
   }
}
