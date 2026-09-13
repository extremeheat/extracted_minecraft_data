package net.minecraft.client.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import javax.crypto.SecretKey;

class NetHandlerLoginClient$1 implements GenericFutureListener {
   NetHandlerLoginClient$1(NetHandlerLoginClient var1, SecretKey var2) {
      super();
      this.field_147494_b = var1;
      this.field_147495_a = var2;
   }

   public void operationComplete(Future var1) {
      NetHandlerLoginClient.access$000(this.field_147494_b).func_150727_a(this.field_147495_a);
   }
}
