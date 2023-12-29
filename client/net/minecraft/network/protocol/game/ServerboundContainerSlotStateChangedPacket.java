package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundContainerSlotStateChangedPacket(int a, int b, boolean c) implements Packet<ServerGamePacketListener> {
   private final int slotId;
   private final int containerId;
   private final boolean newState;

   public ServerboundContainerSlotStateChangedPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), var1.readVarInt(), var1.readBoolean());
   }

   public ServerboundContainerSlotStateChangedPacket(int var1, int var2, boolean var3) {
      super();
      this.slotId = var1;
      this.containerId = var2;
      this.newState = var3;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.slotId);
      var1.writeVarInt(this.containerId);
      var1.writeBoolean(this.newState);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerSlotStateChanged(this);
   }
}
