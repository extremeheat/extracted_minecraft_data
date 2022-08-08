package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketDecoder extends ByteToMessageDecoder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PacketFlow flow;

   public PacketDecoder(PacketFlow var1) {
      super();
      this.flow = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      int var4 = var2.readableBytes();
      if (var4 != 0) {
         FriendlyByteBuf var5 = new FriendlyByteBuf(var2);
         int var6 = var5.readVarInt();
         Packet var7 = ((ConnectionProtocol)var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).createPacket(this.flow, var6, var5);
         if (var7 == null) {
            throw new IOException("Bad packet id " + var6);
         } else {
            int var8 = ((ConnectionProtocol)var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId();
            JvmProfiler.INSTANCE.onPacketReceived(var8, var6, var1.channel().remoteAddress(), var4);
            if (var5.readableBytes() > 0) {
               int var10002 = ((ConnectionProtocol)var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId();
               throw new IOException("Packet " + var10002 + "/" + var6 + " (" + var7.getClass().getSimpleName() + ") was larger than I expected, found " + var5.readableBytes() + " bytes extra whilst reading packet " + var6);
            } else {
               var3.add(var7);
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug(Connection.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {}", new Object[]{var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), var6, var7.getClass().getName()});
               }

            }
         }
      }
   }
}
