package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import net.minecraft.client.network.NetHandlerHandshakeMemory;

class NetworkSystem$2 extends ChannelInitializer {
   NetworkSystem$2(NetworkSystem var1) {
      super();
      this.field_151281_a = var1;
   }

   protected void initChannel(Channel var1) {
      NetworkManager var2 = new NetworkManager(false);
      var2.func_150719_a(new NetHandlerHandshakeMemory(NetworkSystem.access$100(this.field_151281_a), var2));
      NetworkSystem.access$000(this.field_151281_a).add(var2);
      var1.pipeline().addLast("packet_handler", var2);
   }
}
