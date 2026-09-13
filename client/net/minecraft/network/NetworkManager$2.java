package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;

final class NetworkManager$2 extends ChannelInitializer {
   NetworkManager$2(NetworkManager var1) {
      super();
      this.field_150705_a = var1;
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

      var1.pipeline()
         .addLast("timeout", new ReadTimeoutHandler(20))
         .addLast("splitter", new MessageDeserializer2())
         .addLast("decoder", new MessageDeserializer(NetworkManager.field_152462_h))
         .addLast("prepender", new MessageSerializer2())
         .addLast("encoder", new MessageSerializer(NetworkManager.field_152462_h))
         .addLast("packet_handler", this.field_150705_a);
   }
}
