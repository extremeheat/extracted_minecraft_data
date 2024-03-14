package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundContainerSlotStateChangedPacket(int b, int c, boolean d) implements Packet<ServerGamePacketListener> {
   private final int slotId;
   private final int containerId;
   private final boolean newState;
   public static final StreamCodec<FriendlyByteBuf, ServerboundContainerSlotStateChangedPacket> STREAM_CODEC = Packet.codec(
      ServerboundContainerSlotStateChangedPacket::write, ServerboundContainerSlotStateChangedPacket::new
   );

   private ServerboundContainerSlotStateChangedPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), var1.readVarInt(), var1.readBoolean());
   }

   public ServerboundContainerSlotStateChangedPacket(int var1, int var2, boolean var3) {
      super();
      this.slotId = var1;
      this.containerId = var2;
      this.newState = var3;
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
