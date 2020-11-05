package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketDecoder extends ByteToMessageDecoder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker MARKER;
   private final PacketFlow flow;

   public PacketDecoder(PacketFlow var1) {
      super();
      this.flow = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.readableBytes() != 0) {
         FriendlyByteBuf var4 = new FriendlyByteBuf(var2);
         int var5 = var4.readVarInt();
         Packet var6 = ((ConnectionProtocol)var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).createPacket(this.flow, var5);
         if (var6 == null) {
            throw new IOException("Bad packet id " + var5);
         } else {
            var6.read(var4);
            if (var4.readableBytes() > 0) {
               throw new IOException("Packet " + ((ConnectionProtocol)var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId() + "/" + var5 + " (" + var6.getClass().getSimpleName() + ") was larger than I expected, found " + var4.readableBytes() + " bytes extra whilst reading packet " + var5);
            } else {
               var3.add(var6);
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug(MARKER, " IN: [{}:{}] {}", var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), var5, var6.getClass().getName());
               }

            }
         }
      }
   }

   static {
      MARKER = MarkerManager.getMarker("PACKET_RECEIVED", Connection.PACKET_MARKER);
   }
}
