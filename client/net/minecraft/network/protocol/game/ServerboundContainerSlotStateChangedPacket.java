package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundContainerSlotStateChangedPacket(int slotId, int containerId, boolean newState) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundContainerSlotStateChangedPacket> STREAM_CODEC = Packet.codec(
      ServerboundContainerSlotStateChangedPacket::write, ServerboundContainerSlotStateChangedPacket::new
   );

   private ServerboundContainerSlotStateChangedPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), var1.readVarInt(), var1.readBoolean());
   }

   public ServerboundContainerSlotStateChangedPacket(int slotId, int containerId, boolean newState) {
      super();
      this.slotId = slotId;
      this.containerId = containerId;
      this.newState = newState;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.slotId);
      var1.writeVarInt(this.containerId);
      var1.writeBoolean(this.newState);
   }

   @Override
   public PacketType<ServerboundContainerSlotStateChangedPacket> type() {
      return GamePacketTypes.SERVERBOUND_CONTAINER_SLOT_STATE_CHANGED;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerSlotStateChanged(this);
   }
}
