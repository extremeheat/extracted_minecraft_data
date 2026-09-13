package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

final class NetworkManager$3 extends ChannelInitializer {
   NetworkManager$3(NetworkManager var1) {
      super();
      this.field_150778_a = var1;
   }

   protected void initChannel(Channel var1) {
      var1.pipeline().addLast("packet_handler", this.field_150778_a);
   }
}
