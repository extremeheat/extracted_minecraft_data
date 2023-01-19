package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetCarriedItemPacket implements Packet<ClientGamePacketListener> {
   private final int slot;

   public ClientboundSetCarriedItemPacket(int var1) {
      super();
      this.slot = var1;
   }

   public ClientboundSetCarriedItemPacket(FriendlyByteBuf var1) {
      super();
      this.slot = var1.readByte();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.slot);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
