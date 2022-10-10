package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NettyPacketEncoder extends MessageToByteEncoder<Packet<?>> {
   private static final Logger field_150798_a = LogManager.getLogger();
   private static final Marker field_150797_b;
   private final EnumPacketDirection field_152500_c;

   public NettyPacketEncoder(EnumPacketDirection var1) {
      super();
      this.field_152500_c = var1;
   }

   protected void encode(ChannelHandlerContext var1, Packet<?> var2, ByteBuf var3) throws Exception {
      EnumConnectionState var4 = (EnumConnectionState)var1.channel().attr(NetworkManager.field_150739_c).get();
      if (var4 == null) {
         throw new RuntimeException("ConnectionProtocol unknown: " + var2);
      } else {
         Integer var5 = var4.func_179246_a(this.field_152500_c, var2);
         if (field_150798_a.isDebugEnabled()) {
            field_150798_a.debug(field_150797_b, "OUT: [{}:{}] {}", var1.channel().attr(NetworkManager.field_150739_c).get(), var5, var2.getClass().getName());
         }

         if (var5 == null) {
            throw new IOException("Can't serialize unregistered packet");
         } else {
            PacketBuffer var6 = new PacketBuffer(var3);
            var6.func_150787_b(var5);

            try {
               var2.func_148840_b(var6);
            } catch (Throwable var8) {
               field_150798_a.error(var8);
               if (var2.func_211402_a()) {
                  throw new SkipableEncoderException(var8);
               } else {
                  throw var8;
               }
            }
         }
      }
   }

   // $FF: synthetic method
   protected void encode(ChannelHandlerContext var1, Object var2, ByteBuf var3) throws Exception {
      this.encode(var1, (Packet)var2, var3);
   }

   static {
      field_150797_b = MarkerManager.getMarker("PACKET_SENT", NetworkManager.field_150738_b);
   }
}
