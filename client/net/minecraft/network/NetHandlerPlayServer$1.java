package net.minecraft.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.ChatComponentText;

class NetHandlerPlayServer$1 implements GenericFutureListener {
   NetHandlerPlayServer$1(NetHandlerPlayServer var1, ChatComponentText var2) {
      super();
      this.field_151288_b = var1;
      this.field_151289_a = var2;
   }

   public void operationComplete(Future var1) {
      this.field_151288_b.field_147371_a.func_150718_a(this.field_151289_a);
   }
}
