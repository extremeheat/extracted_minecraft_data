package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketDecoder<T extends PacketListener> extends ByteToMessageDecoder implements ProtocolSwapHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ProtocolInfo<T> protocolInfo;

   public PacketDecoder(ProtocolInfo<T> var1) {
      super();
      this.protocolInfo = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      int var4 = var2.readableBytes();
      if (var4 != 0) {
         Packet var5 = (Packet)this.protocolInfo.codec().decode(var2);
         PacketType var6 = var5.type();
         JvmProfiler.INSTANCE.onPacketReceived(this.protocolInfo.id(), var6, var1.channel().remoteAddress(), var4);
         if (var2.readableBytes() > 0) {
            String var10002 = this.protocolInfo.id().id();
            throw new IOException("Packet " + var10002 + "/" + String.valueOf(var6) + " (" + var5.getClass().getSimpleName() + ") was larger than I expected, found " + var2.readableBytes() + " bytes extra whilst reading packet " + String.valueOf(var6));
         } else {
            var3.add(var5);
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(Connection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {} -> {} bytes", new Object[]{this.protocolInfo.id().id(), var6, var5.getClass().getName(), var4});
            }

            ProtocolSwapHandler.handleInboundTerminalPacket(var1, var5);
         }
      }
   }
}
