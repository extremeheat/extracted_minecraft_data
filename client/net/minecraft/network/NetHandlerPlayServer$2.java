package net.minecraft.network;

import java.util.concurrent.Callable;

class NetHandlerPlayServer$2 implements Callable {
   NetHandlerPlayServer$2(NetHandlerPlayServer var1, Packet var2) {
      super();
      this.field_151286_b = var1;
      this.field_151287_a = var2;
   }

   public String call() {
      return this.field_151287_a.getClass().getCanonicalName();
   }
}
