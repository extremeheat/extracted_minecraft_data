package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetCarriedItemPacket implements Packet<ServerGamePacketListener> {
   private final int slot;

   public ServerboundSetCarriedItemPacket(int var1) {
      super();
      this.slot = var1;
   }

   public ServerboundSetCarriedItemPacket(FriendlyByteBuf var1) {
      super();
      this.slot = var1.readShort();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeShort(this.slot);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
