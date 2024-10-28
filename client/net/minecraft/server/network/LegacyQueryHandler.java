package net.minecraft.server.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.SocketAddress;
import java.util.Locale;
import net.minecraft.server.ServerInfo;
import org.slf4j.Logger;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ServerInfo server;

   public LegacyQueryHandler(ServerInfo var1) {
      super();
      this.server = var1;
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) {
      ByteBuf var3 = (ByteBuf)var2;
      var3.markReaderIndex();
      boolean var4 = true;

      try {
         try {
            if (var3.readUnsignedByte() != 254) {
               return;
            }

            SocketAddress var5 = var1.channel().remoteAddress();
            int var6 = var3.readableBytes();
            String var7;
            if (var6 == 0) {
               LOGGER.debug("Ping: (<1.3.x) from {}", var5);
               var7 = createVersion0Response(this.server);
               sendFlushAndClose(var1, createLegacyDisconnectPacket(var1.alloc(), var7));
            } else {
               if (var3.readUnsignedByte() != 1) {
                  return;
               }

               if (var3.isReadable()) {
                  if (!readCustomPayloadPacket(var3)) {
                     return;
                  }

                  LOGGER.debug("Ping: (1.6) from {}", var5);
               } else {
                  LOGGER.debug("Ping: (1.4-1.5.x) from {}", var5);
               }

               var7 = createVersion1Response(this.server);
               sendFlushAndClose(var1, createLegacyDisconnectPacket(var1.alloc(), var7));
            }

            var3.release();
            var4 = false;
         } catch (RuntimeException var11) {
         }

      } finally {
         if (var4) {
            var3.resetReaderIndex();
            var1.channel().pipeline().remove(this);
            var1.fireChannelRead(var2);
         }

      }
   }

   private static boolean readCustomPayloadPacket(ByteBuf var0) {
      short var1 = var0.readUnsignedByte();
      if (var1 != 250) {
         return false;
      } else {
         String var2 = LegacyProtocolUtils.readLegacyString(var0);
         if (!"MC|PingHost".equals(var2)) {
            return false;
         } else {
            int var3 = var0.readUnsignedShort();
            if (var0.readableBytes() != var3) {
               return false;
            } else {
               short var4 = var0.readUnsignedByte();
               if (var4 < 73) {
                  return false;
               } else {
                  String var5 = LegacyProtocolUtils.readLegacyString(var0);
                  int var6 = var0.readInt();
                  return var6 <= 65535;
               }
            }
         }
      }
   }

   private static String createVersion0Response(ServerInfo var0) {
      return String.format(Locale.ROOT, "%s\u00a7%d\u00a7%d", var0.getMotd(), var0.getPlayerCount(), var0.getMaxPlayers());
   }

   private static String createVersion1Response(ServerInfo var0) {
      return String.format(Locale.ROOT, "\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, var0.getServerVersion(), var0.getMotd(), var0.getPlayerCount(), var0.getMaxPlayers());
   }

   private static void sendFlushAndClose(ChannelHandlerContext var0, ByteBuf var1) {
      var0.pipeline().firstContext().writeAndFlush(var1).addListener(ChannelFutureListener.CLOSE);
   }

   private static ByteBuf createLegacyDisconnectPacket(ByteBufAllocator var0, String var1) {
      ByteBuf var2 = var0.buffer();
      var2.writeByte(255);
      LegacyProtocolUtils.writeLegacyString(var2, var1);
      return var2;
   }
}
