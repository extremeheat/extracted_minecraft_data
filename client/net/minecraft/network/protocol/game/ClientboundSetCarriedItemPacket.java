package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetCarriedItemPacket implements Packet<ClientGamePacketListener> {
   private int slot;

   public ClientboundSetCarriedItemPacket() {
      super();
   }

   public ClientboundSetCarriedItemPacket(int var1) {
      super();
      this.slot = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.slot = var1.readByte();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.slot);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
