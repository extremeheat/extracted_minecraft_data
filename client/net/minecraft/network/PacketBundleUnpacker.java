package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;

public class PacketBundleUnpacker extends MessageToMessageEncoder<Packet<?>> {
   private final BundlerInfo bundlerInfo;

   public PacketBundleUnpacker(final BundlerInfo bundlerInfo) {
      super();
      this.bundlerInfo = bundlerInfo;
   }

   protected void encode(final ChannelHandlerContext ctx, final Packet<?> msg, final List<Object> out) throws Exception {
      BundlerInfo var10000 = this.bundlerInfo;
      Objects.requireNonNull(out);
      var10000.unbundlePacket(msg, out::add);
      if (msg.isTerminal()) {
         ctx.pipeline().remove(ctx.name());
      }

   }
}
