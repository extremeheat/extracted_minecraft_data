package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundKeepAlivePacket implements Packet<ClientCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundKeepAlivePacket> STREAM_CODEC = Packet.codec(ClientboundKeepAlivePacket::write, ClientboundKeepAlivePacket::new);
   private final long id;

   public ClientboundKeepAlivePacket(long var1) {
      super();
      this.id = var1;
   }

   private ClientboundKeepAlivePacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readLong();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeLong(this.id);
   }

   public PacketType<ClientboundKeepAlivePacket> type() {
      return CommonPacketTypes.CLIENTBOUND_KEEP_ALIVE;
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public long getId() {
      return this.id;
   }
}
