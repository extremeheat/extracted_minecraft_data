package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.AttributeKey;
import java.util.List;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;

public class PacketBundleUnpacker extends MessageToMessageEncoder<Packet<?>> {
   private final AttributeKey<? extends BundlerInfo.Provider> bundlerAttributeKey;

   public PacketBundleUnpacker(AttributeKey<? extends BundlerInfo.Provider> var1) {
      super();
      this.bundlerAttributeKey = var1;
   }

   protected void encode(ChannelHandlerContext var1, Packet<?> var2, List<Object> var3) throws Exception {
      BundlerInfo.Provider var4 = (BundlerInfo.Provider)var1.channel().attr(this.bundlerAttributeKey).get();
      if (var4 == null) {
         throw new EncoderException("Bundler not configured: " + var2);
      } else {
         var4.bundlerInfo().unbundlePacket(var2, var3::add);
      }
   }
}
