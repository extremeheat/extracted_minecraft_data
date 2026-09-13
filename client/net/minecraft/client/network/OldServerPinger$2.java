package net.minecraft.client.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;

class OldServerPinger$2 extends ChannelInitializer {
   OldServerPinger$2(OldServerPinger var1, ServerAddress var2, ServerData var3) {
      super();
      this.field_147217_c = var1;
      this.field_147218_a = var2;
      this.field_147216_b = var3;
   }

   protected void initChannel(Channel var1) {
      try {
         var1.config().setOption(ChannelOption.IP_TOS, 24);
      } catch (ChannelException var4) {
      }

      try {
         var1.config().setOption(ChannelOption.TCP_NODELAY, false);
      } catch (ChannelException var3) {
      }

      var1.pipeline().addLast(new ChannelHandler[]{new OldServerPinger$2$1(this)});
   }
}
