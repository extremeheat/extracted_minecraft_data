package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundContainerClosePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundContainerClosePacket> STREAM_CODEC = Packet.codec(ClientboundContainerClosePacket::write, ClientboundContainerClosePacket::new);
   private final int containerId;

   public ClientboundContainerClosePacket(int var1) {
      super();
      this.containerId = var1;
   }

   private ClientboundContainerClosePacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readUnsignedByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
   }

   public PacketType<ClientboundContainerClosePacket> type() {
      return GamePacketTypes.CLIENTBOUND_CONTAINER_CLOSE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerClose(this);
   }

   public int getContainerId() {
      return this.containerId;
   }
}
