package net.minecraft.network.protocol.ping;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPingRequestPacket implements Packet<ServerPingPacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundPingRequestPacket> STREAM_CODEC = Packet.codec(ServerboundPingRequestPacket::write, ServerboundPingRequestPacket::new);
   private final long time;

   public ServerboundPingRequestPacket(long var1) {
      super();
      this.time = var1;
   }

   private ServerboundPingRequestPacket(ByteBuf var1) {
      super();
      this.time = var1.readLong();
   }

   private void write(ByteBuf var1) {
      var1.writeLong(this.time);
   }

   public PacketType<ServerboundPingRequestPacket> type() {
      return PingPacketTypes.SERVERBOUND_PING_REQUEST;
   }

   public void handle(ServerPingPacketListener var1) {
      var1.handlePingRequest(this);
   }

   public long getTime() {
      return this.time;
   }
}
