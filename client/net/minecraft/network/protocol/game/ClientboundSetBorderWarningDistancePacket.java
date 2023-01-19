package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderWarningDistancePacket implements Packet<ClientGamePacketListener> {
   private final int warningBlocks;

   public ClientboundSetBorderWarningDistancePacket(WorldBorder var1) {
      super();
      this.warningBlocks = var1.getWarningBlocks();
   }

   public ClientboundSetBorderWarningDistancePacket(FriendlyByteBuf var1) {
      super();
      this.warningBlocks = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.warningBlocks);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorderWarningDistance(this);
   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }
}
