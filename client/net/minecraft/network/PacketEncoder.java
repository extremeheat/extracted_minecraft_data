package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketEncoder<T extends PacketListener> extends MessageToByteEncoder<Packet<T>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ProtocolInfo<T> protocolInfo;

   public PacketEncoder(ProtocolInfo<T> var1) {
      super();
      this.protocolInfo = var1;
   }

   protected void encode(ChannelHandlerContext var1, Packet<T> var2, ByteBuf var3) throws Exception {
      PacketType var4 = var2.type();

      try {
         this.protocolInfo.codec().encode(var3, var2);
         int var5 = var3.readableBytes();
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {} -> {} bytes", new Object[]{this.protocolInfo.id().id(), var4, var2.getClass().getName(), var5});
         }

         JvmProfiler.INSTANCE.onPacketSent(this.protocolInfo.id(), var4, var1.channel().remoteAddress(), var5);
      } catch (Throwable var9) {
         LOGGER.error("Error sending packet {}", var4, var9);
         if (var2.isSkippable()) {
            throw new SkipPacketException(var9);
         }

         throw var9;
      } finally {
         ProtocolSwapHandler.handleOutboundTerminalPacket(var1, var2);
      }

   }

   // $FF: synthetic method
   protected void encode(final ChannelHandlerContext var1, final Object var2, final ByteBuf var3) throws Exception {
      this.encode(var1, (Packet)var2, var3);
   }
}
