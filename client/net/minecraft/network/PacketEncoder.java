package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.IOException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final AttributeKey<ConnectionProtocol.CodecData<?>> codecKey;

   public PacketEncoder(AttributeKey<ConnectionProtocol.CodecData<?>> var1) {
      super();
      this.codecKey = var1;
   }

   protected void encode(ChannelHandlerContext var1, Packet<?> var2, ByteBuf var3) throws Exception {
      Attribute var4 = var1.channel().attr(this.codecKey);
      ConnectionProtocol.CodecData var5 = (ConnectionProtocol.CodecData)var4.get();
      if (var5 == null) {
         throw new RuntimeException("ConnectionProtocol unknown: " + var2);
      } else {
         int var6 = var5.packetId(var2);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Connection.PACKET_SENT_MARKER, "OUT: [{}:{}] {}", new Object[]{var5.protocol().id(), var6, var2.getClass().getName()});
         }

         if (var6 == -1) {
            throw new IOException("Can't serialize unregistered packet");
         } else {
            FriendlyByteBuf var7 = new FriendlyByteBuf(var3);
            var7.writeVarInt(var6);

            try {
               int var8 = var7.writerIndex();
               var2.write(var7);
               int var9 = var7.writerIndex() - var8;
               if (var9 > 8388608) {
                  throw new IllegalArgumentException("Packet too big (is " + var9 + ", should be less than 8388608): " + var2);
               }

               JvmProfiler.INSTANCE.onPacketSent(var5.protocol(), var6, var1.channel().remoteAddress(), var9);
            } catch (Throwable var13) {
               LOGGER.error("Error receiving packet {}", var6, var13);
               if (var2.isSkippable()) {
                  throw new SkipPacketException(var13);
               }

               throw var13;
            } finally {
               ProtocolSwapHandler.swapProtocolIfNeeded(var4, var2);
            }
         }
      }
   }
}
