package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPickItemPacket implements Packet<ServerGamePacketListener> {
   private final int slot;

   public ServerboundPickItemPacket(int var1) {
      super();
      this.slot = var1;
   }

   public ServerboundPickItemPacket(FriendlyByteBuf var1) {
      super();
      this.slot = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.slot);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePickItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
