package net.minecraft.network.protocol.ping;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPongResponsePacket(long time) implements Packet<ClientPongPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPongResponsePacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundPongResponsePacket>codec(ClientboundPongResponsePacket::write, ClientboundPongResponsePacket::new);

   private ClientboundPongResponsePacket(FriendlyByteBuf var1) {
      this(var1.readLong());
   }

   public ClientboundPongResponsePacket(long var1) {
      super();
      this.time = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeLong(this.time);
   }

   public PacketType<ClientboundPongResponsePacket> type() {
      return PingPacketTypes.CLIENTBOUND_PONG_RESPONSE;
   }

   public void handle(ClientPongPacketListener var1) {
      var1.handlePongResponse(this);
   }
}
