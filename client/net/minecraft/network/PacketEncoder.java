package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PacketFlow flow;

   public PacketEncoder(PacketFlow var1) {
      super();
      this.flow = var1;
   }

   protected void encode(ChannelHandlerContext var1, Packet<?> var2, ByteBuf var3) throws Exception {
      ConnectionProtocol var4 = (ConnectionProtocol)var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
      if (var4 == null) {
         throw new RuntimeException("ConnectionProtocol unknown: " + var2);
      } else {
         Integer var5 = var4.getPacketId(this.flow, var2);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
               Connection.PACKET_SENT_MARKER,
               "OUT: [{}:{}] {}",
               new Object[]{var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), var5, var2.getClass().getName()}
            );
         }

         if (var5 == null) {
            throw new IOException("Can't serialize unregistered packet");
         } else {
            FriendlyByteBuf var6 = new FriendlyByteBuf(var3);
            var6.writeVarInt(var5);

            try {
               int var7 = var6.writerIndex();
               var2.write(var6);
               int var8 = var6.writerIndex() - var7;
               if (var8 > 8388608) {
                  throw new IllegalArgumentException("Packet too big (is " + var8 + ", should be less than 8388608): " + var2);
               } else {
                  int var9 = ((ConnectionProtocol)var1.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get()).getId();
                  JvmProfiler.INSTANCE.onPacketSent(var9, var5, var1.channel().remoteAddress(), var8);
               }
            } catch (Throwable var10) {
               LOGGER.error("Error receiving packet {}", var5, var10);
               if (var2.isSkippable()) {
                  throw new SkipPacketException(var10);
               } else {
                  throw var10;
               }
            }
         }
      }
   }
}
