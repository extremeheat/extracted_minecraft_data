package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketDecoder extends ByteToMessageDecoder implements ProtocolSwapHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final AttributeKey<ConnectionProtocol.CodecData<?>> codecKey;

   public PacketDecoder(AttributeKey<ConnectionProtocol.CodecData<?>> var1) {
      super();
      this.codecKey = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      int var4 = var2.readableBytes();
      if (var4 != 0) {
         Attribute var5 = var1.channel().attr(this.codecKey);
         ConnectionProtocol.CodecData var6 = (ConnectionProtocol.CodecData)var5.get();
         FriendlyByteBuf var7 = new FriendlyByteBuf(var2);
         int var8 = var7.readVarInt();
         Packet var9 = var6.createPacket(var8, var7);
         if (var9 == null) {
            throw new IOException("Bad packet id " + var8);
         } else {
            JvmProfiler.INSTANCE.onPacketReceived(var6.protocol(), var8, var1.channel().remoteAddress(), var4);
            if (var7.readableBytes() > 0) {
               throw new IOException(
                  "Packet "
                     + var6.protocol().id()
                     + "/"
                     + var8
                     + " ("
                     + var9.getClass().getSimpleName()
                     + ") was larger than I expected, found "
                     + var7.readableBytes()
                     + " bytes extra whilst reading packet "
                     + var8
               );
            } else {
               var3.add(var9);
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug(Connection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {}", new Object[]{var6.protocol().id(), var8, var9.getClass().getName()});
               }

               ProtocolSwapHandler.swapProtocolIfNeeded(var5, var9);
            }
         }
      }
   }
}
