package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundContainerClosePacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundContainerClosePacket> STREAM_CODEC = Packet.codec(ServerboundContainerClosePacket::write, ServerboundContainerClosePacket::new);
   private final int containerId;

   public ServerboundContainerClosePacket(int var1) {
      super();
      this.containerId = var1;
   }

   private ServerboundContainerClosePacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
   }

   public PacketType<ServerboundContainerClosePacket> type() {
      return GamePacketTypes.SERVERBOUND_CONTAINER_CLOSE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerClose(this);
   }

   public int getContainerId() {
      return this.containerId;
   }
}
