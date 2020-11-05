package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetCarriedItemPacket implements Packet<ServerGamePacketListener> {
   private int slot;

   public ServerboundSetCarriedItemPacket() {
      super();
   }

   public ServerboundSetCarriedItemPacket(int var1) {
      super();
      this.slot = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.slot = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeShort(this.slot);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCarriedItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
