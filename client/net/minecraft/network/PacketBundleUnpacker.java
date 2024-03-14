package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;

public class PacketBundleUnpacker extends MessageToMessageEncoder<Packet<?>> {
   private final BundlerInfo bundlerInfo;

   public PacketBundleUnpacker(BundlerInfo var1) {
      super();
      this.bundlerInfo = var1;
   }

   protected void encode(ChannelHandlerContext var1, Packet<?> var2, List<Object> var3) throws Exception {
      this.bundlerInfo.unbundlePacket(var2, var3::add);
      if (var2.isTerminal()) {
         var1.pipeline().remove(var1.name());
      }
   }
}
