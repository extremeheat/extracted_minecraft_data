package net.minecraft.server.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.SocketAddress;
import java.util.Locale;
import net.minecraft.network.Connection;
import net.minecraft.network.QueryProperties;
import net.minecraft.network.ServerConnectionDetails;
import net.minecraft.server.ServerInfo;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter {
   private static final ServerConnectionDetails UNKNOWN_SERVER_DETAILS;
   private static final Logger LOGGER;
   private final ServerInfo server;
   private final Connection connection;

   public LegacyQueryHandler(final ServerInfo server, final Connection connection) {
      super();
      this.server = server;
      this.connection = connection;
   }

   public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
      ByteBuf in = (ByteBuf)msg;
      in.markReaderIndex();
      boolean connectNormally = true;

      try {
         if (in.readUnsignedByte() == 254) {
            SocketAddress socket = ctx.channel().remoteAddress();
            int length = in.readableBytes();
            if (length == 0) {
               LOGGER.debug("Ping: (<1.3.x) from {}", socket);
               if (this.server.acceptsConnection(this.connection, UNKNOWN_SERVER_DETAILS)) {
                  String body = createVersion0Response(this.server);
                  sendFlushAndClose(ctx, createLegacyDisconnectPacket(ctx.alloc(), body));
               } else {
                  ctx.close();
               }
            } else {
               if (in.readUnsignedByte() != 1) {
                  return;
               }

               ServerConnectionDetails connectionDetails;
               if (in.isReadable()) {
                  connectionDetails = readCustomPayloadPacket(in);
                  if (connectionDetails == null) {
                     return;
                  }

                  LOGGER.debug("Ping: (1.6) from {}, host: {}", socket, connectionDetails);
               } else {
                  LOGGER.debug("Ping: (1.4-1.5.x) from {}", socket);
                  connectionDetails = UNKNOWN_SERVER_DETAILS;
               }

               if (this.server.acceptsConnection(this.connection, connectionDetails)) {
                  String body = createVersion1Response(this.server);
                  sendFlushAndClose(ctx, createLegacyDisconnectPacket(ctx.alloc(), body));
               } else {
                  ctx.close();
               }
            }

            in.release();
            connectNormally = false;
            return;
         }
      } catch (RuntimeException var12) {
         return;
      } finally {
         if (connectNormally) {
            in.resetReaderIndex();
            ctx.channel().pipeline().remove(this);
            ctx.fireChannelRead(msg);
         }

      }

   }

   private static @Nullable ServerConnectionDetails readCustomPayloadPacket(final ByteBuf in) {
      short packetId = in.readUnsignedByte();
      if (packetId != 250) {
         return null;
      } else {
         String channelId = LegacyProtocolUtils.readLegacyString(in);
         if (!"MC|PingHost".equals(channelId)) {
            return null;
         } else {
            int payloadSize = in.readUnsignedShort();
            if (in.readableBytes() != payloadSize) {
               return null;
            } else {
               short protocolVersion = in.readUnsignedByte();
               if (protocolVersion < 73) {
                  return null;
               } else {
                  String host = LegacyProtocolUtils.readLegacyString(in);
                  int port = in.readInt();
                  return port > 65535 ? null : ServerConnectionDetails.fromIntentPacket(host, port);
               }
            }
         }
      }
   }

   private static String createVersion0Response(final ServerInfo server) {
      return String.format(Locale.ROOT, "%s\u00a7%d\u00a7%d", server.getMotd(), server.getPlayerCount(), server.getMaxPlayers());
   }

   private static String createVersion1Response(final ServerInfo server) {
      return String.format(Locale.ROOT, "\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", 127, server.getServerVersion(), server.getMotd(), server.getPlayerCount(), server.getMaxPlayers());
   }

   private static void sendFlushAndClose(final ChannelHandlerContext ctx, final ByteBuf out) {
      ctx.pipeline().firstContext().writeAndFlush(out).addListener(ChannelFutureListener.CLOSE);
   }

   private static ByteBuf createLegacyDisconnectPacket(final ByteBufAllocator alloc, final String reason) {
      ByteBuf out = alloc.buffer();
      out.writeByte(255);
      LegacyProtocolUtils.writeLegacyString(out, reason);
      return out;
   }

   static {
      UNKNOWN_SERVER_DETAILS = new ServerConnectionDetails("unknown.invalid", -1, QueryProperties.EMPTY);
      LOGGER = LogUtils.getLogger();
   }
}
