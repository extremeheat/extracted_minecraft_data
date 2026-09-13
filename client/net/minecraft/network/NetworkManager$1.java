package net.minecraft.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.GenericFutureListener;

class NetworkManager$1 implements Runnable {
   NetworkManager$1(NetworkManager var1, EnumConnectionState var2, EnumConnectionState var3, Packet var4, GenericFutureListener[] var5) {
      super();
      this.field_150714_e = var1;
      this.field_150717_a = var2;
      this.field_150715_b = var3;
      this.field_150716_c = var4;
      this.field_150713_d = var5;
   }

   @Override
   public void run() {
      if (this.field_150717_a != this.field_150715_b) {
         this.field_150714_e.func_150723_a(this.field_150717_a);
      }

      NetworkManager.access$000(this.field_150714_e)
         .writeAndFlush(this.field_150716_c)
         .addListeners(this.field_150713_d)
         .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }
}
