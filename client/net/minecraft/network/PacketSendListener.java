package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFutureListener;
import java.util.function.Supplier;
import net.minecraft.network.protocol.Packet;
import org.slf4j.Logger;

public class PacketSendListener {
   private static final Logger LOGGER = LogUtils.getLogger();

   public PacketSendListener() {
      super();
   }

   public static ChannelFutureListener thenRun(Runnable var0) {
      return (var1) -> {
         var0.run();
         if (!var1.isSuccess()) {
            var1.channel().pipeline().fireExceptionCaught(var1.cause());
         }

      };
   }

   public static ChannelFutureListener exceptionallySend(Supplier<Packet<?>> var0) {
      return (var1) -> {
         if (!var1.isSuccess()) {
            Packet var2 = (Packet)var0.get();
            if (var2 != null) {
               LOGGER.warn("Failed to deliver packet, sending fallback {}", var2.type(), var1.cause());
               var1.channel().writeAndFlush(var2, var1.channel().voidPromise());
            } else {
               var1.channel().pipeline().fireExceptionCaught(var1.cause());
            }
         }

      };
   }
}
