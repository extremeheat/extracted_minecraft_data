package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NettyPacketDecoder extends ByteToMessageDecoder {
   private static final Logger field_150800_a = LogManager.getLogger();
   private static final Marker field_150799_b;
   private final EnumPacketDirection field_152499_c;

   public NettyPacketDecoder(EnumPacketDirection var1) {
      super();
      this.field_152499_c = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.readableBytes() != 0) {
         PacketBuffer var4 = new PacketBuffer(var2);
         int var5 = var4.func_150792_a();
         Packet var6 = ((EnumConnectionState)var1.channel().attr(NetworkManager.field_150739_c).get()).func_179244_a(this.field_152499_c, var5);
         if (var6 == null) {
            throw new IOException("Bad packet id " + var5);
         } else {
            var6.func_148837_a(var4);
            if (var4.readableBytes() > 0) {
               throw new IOException("Packet " + ((EnumConnectionState)var1.channel().attr(NetworkManager.field_150739_c).get()).func_150759_c() + "/" + var5 + " (" + var6.getClass().getSimpleName() + ") was larger than I expected, found " + var4.readableBytes() + " bytes extra whilst reading packet " + var5);
            } else {
               var3.add(var6);
               if (field_150800_a.isDebugEnabled()) {
                  field_150800_a.debug(field_150799_b, " IN: [{}:{}] {}", var1.channel().attr(NetworkManager.field_150739_c).get(), var5, var6.getClass().getName());
               }

            }
         }
      }
   }

   static {
      field_150799_b = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.field_150738_b);
   }
}
