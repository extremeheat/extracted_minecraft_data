package net.minecraft.network;

import java.util.concurrent.Callable;

class NetworkSystem$3 implements Callable {
   NetworkSystem$3(NetworkSystem var1, NetworkManager var2) {
      super();
      this.field_151279_b = var1;
      this.field_151280_a = var2;
   }

   public String call() {
      return this.field_151280_a.toString();
   }
}
