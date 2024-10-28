package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundPingPacket implements Packet<ClientCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPingPacket> STREAM_CODEC = Packet.codec(ClientboundPingPacket::write, ClientboundPingPacket::new);
   private final int id;

   public ClientboundPingPacket(int var1) {
      super();
      this.id = var1;
   }

   private ClientboundPingPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeInt(this.id);
   }

   public PacketType<ClientboundPingPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_PING;
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handlePing(this);
   }

   public int getId() {
      return this.id;
   }
}
