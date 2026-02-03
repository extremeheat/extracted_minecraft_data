package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundPingPacket implements Packet<ClientCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPingPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundPingPacket>codec(ClientboundPingPacket::write, ClientboundPingPacket::new);
   private final int id;

   public ClientboundPingPacket(final int id) {
      super();
      this.id = id;
   }

   private ClientboundPingPacket(final FriendlyByteBuf input) {
      super();
      this.id = input.readInt();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeInt(this.id);
   }

   public PacketType<ClientboundPingPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_PING;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handlePing(this);
   }

   public int getId() {
      return this.id;
   }
}
