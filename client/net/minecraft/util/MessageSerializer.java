package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageSerializer extends MessageToByteEncoder<Packet> {
   private static final Logger field_150798_a = LogManager.getLogger();
   private static final Marker field_150797_b;
   private final EnumPacketDirection field_152500_c;

   public MessageSerializer(EnumPacketDirection var1) {
      super();
      this.field_152500_c = var1;
   }

   protected void encode(ChannelHandlerContext var1, Packet var2, ByteBuf var3) throws Exception {
      Integer var4 = ((EnumConnectionState)var1.channel().attr(NetworkManager.field_150739_c).get()).func_179246_a(this.field_152500_c, var2);
      if (field_150798_a.isDebugEnabled()) {
         field_150798_a.debug(field_150797_b, "OUT: [{}:{}] {}", new Object[]{var1.channel().attr(NetworkManager.field_150739_c).get(), var4, var2.getClass().getName()});
      }

      if (var4 == null) {
         throw new IOException("Can't serialize unregistered packet");
      } else {
         PacketBuffer var5 = new PacketBuffer(var3);
         var5.func_150787_b(var4);

         try {
            if (var2 instanceof S0CPacketSpawnPlayer) {
               var2 = var2;
            }

            var2.func_148840_b(var5);
         } catch (Throwable var7) {
            field_150798_a.error(var7);
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
