package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;

public class PacketBundlePacker extends MessageToMessageDecoder<Packet<?>> {
   private final BundlerInfo bundlerInfo;
   @Nullable
   private BundlerInfo.Bundler currentBundler;

   public PacketBundlePacker(BundlerInfo var1) {
      super();
      this.bundlerInfo = var1;
   }

   protected void decode(ChannelHandlerContext var1, Packet<?> var2, List<Object> var3) throws Exception {
      if (this.currentBundler != null) {
         verifyNonTerminalPacket(var2);
         Packet var4 = this.currentBundler.addPacket(var2);
         if (var4 != null) {
            this.currentBundler = null;
            var3.add(var4);
         }
      } else {
         BundlerInfo.Bundler var5 = this.bundlerInfo.startPacketBundling(var2);
         if (var5 != null) {
            verifyNonTerminalPacket(var2);
            this.currentBundler = var5;
         } else {
            var3.add(var2);
            if (var2.isTerminal()) {
               var1.pipeline().remove(var1.name());
            }
         }
      }

   }

   private static void verifyNonTerminalPacket(Packet<?> var0) {
      if (var0.isTerminal()) {
         throw new DecoderException("Terminal message received in bundle");
      }
   }

   // $FF: synthetic method
   protected void decode(ChannelHandlerContext var1, Object var2, List var3) throws Exception {
      this.decode(var1, (Packet)var2, var3);
   }
}
