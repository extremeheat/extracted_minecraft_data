package net.minecraft.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.ChatComponentText;

class NetworkSystem$4 implements GenericFutureListener {
   NetworkSystem$4(NetworkSystem var1, NetworkManager var2, ChatComponentText var3) {
      super();
      this.field_151283_c = var1;
      this.field_151284_a = var2;
      this.field_151282_b = var3;
   }

   public void operationComplete(Future var1) {
      this.field_151284_a.func_150718_a(this.field_151282_b);
   }
}
