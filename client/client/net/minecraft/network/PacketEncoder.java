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
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", new Object[]{this.protocolInfo.id().id(), var4, var2.getClass().getName()});
      }

      try {
         int var5 = var3.writerIndex();
         this.protocolInfo.codec().encode(var3, var2);
         int var6 = var3.writerIndex() - var5;
         if (var6 > 8388608) {
            throw new IllegalArgumentException("Packet too big (is " + var6 + ", should be less than 8388608): " + var2);
         }

         JvmProfiler.INSTANCE.onPacketSent(this.protocolInfo.id(), var4, var1.channel().remoteAddress(), var6);
      } catch (Throwable var10) {
         LOGGER.error("Error sending packet {}", var4, var10);
         if (var2.isSkippable()) {
            throw new SkipPacketException(var10);
         }

         throw var10;
      } finally {
         ProtocolSwapHandler.handleOutboundTerminalPacket(var1, var2);
      }
   }
}
