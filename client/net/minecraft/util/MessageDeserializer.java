package net.minecraft.util;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder {
   private static final Logger field_150800_a = LogManager.getLogger();
   private static final Marker field_150799_b = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.field_150738_b);
   private final NetworkStatistics field_152499_c;

   public MessageDeserializer(NetworkStatistics var1) {
      super();
      this.field_152499_c = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List var3) {
      int var4 = var2.readableBytes();
      if (var4 != 0) {
         PacketBuffer var5 = new PacketBuffer(var2);
         int var6 = var5.func_150792_a();
         Packet var7 = Packet.func_148839_a((BiMap)var1.channel().attr(NetworkManager.field_150736_d).get(), var6);
         if (var7 == null) {
            throw new IOException("Bad packet id " + var6);
         } else {
            var7.func_148837_a(var5);
            if (var5.readableBytes() > 0) {
               throw new IOException("Packet was larger than I expected, found " + var5.readableBytes() + " bytes extra whilst reading packet " + var6);
            } else {
               var3.add(var7);
               this.field_152499_c.func_152469_a(var6, (long)var4);
               if (field_150800_a.isDebugEnabled()) {
                  field_150800_a.debug(
                     field_150799_b,
                     " IN: [{}:{}] {}[{}]",
                     new Object[]{var1.channel().attr(NetworkManager.field_150739_c).get(), var6, var7.getClass().getName(), var7.func_148835_b()}
                  );
               }
            }
         }
      }
   }
}
