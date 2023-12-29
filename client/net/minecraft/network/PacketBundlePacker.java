package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;

public class PacketBundlePacker extends MessageToMessageDecoder<Packet<?>> {
   @Nullable
   private BundlerInfo.Bundler currentBundler;
   @Nullable
   private BundlerInfo infoForCurrentBundler;
   private final AttributeKey<? extends BundlerInfo.Provider> bundlerAttributeKey;

   public PacketBundlePacker(AttributeKey<? extends BundlerInfo.Provider> var1) {
      super();
      this.bundlerAttributeKey = var1;
   }

   protected void decode(ChannelHandlerContext var1, Packet<?> var2, List<Object> var3) throws Exception {
      BundlerInfo.Provider var4 = (BundlerInfo.Provider)var1.channel().attr(this.bundlerAttributeKey).get();
      if (var4 == null) {
         throw new DecoderException("Bundler not configured: " + var2);
      } else {
         BundlerInfo var5 = var4.bundlerInfo();
         if (this.currentBundler != null) {
            if (this.infoForCurrentBundler != var5) {
               throw new DecoderException("Bundler handler changed during bundling");
            }

            Packet var6 = this.currentBundler.addPacket(var2);
            if (var6 != null) {
               this.infoForCurrentBundler = null;
               this.currentBundler = null;
               var3.add(var6);
            }
         } else {
            BundlerInfo.Bundler var7 = var5.startPacketBundling(var2);
            if (var7 != null) {
               this.currentBundler = var7;
               this.infoForCurrentBundler = var5;
            } else {
               var3.add(var2);
            }
         }
      }
   }
}