package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPickItemPacket implements Packet {
   private int slot;

   public ServerboundPickItemPacket() {
   }

   public ServerboundPickItemPacket(int var1) {
      this.slot = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.slot = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.slot);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePickItem(this);
   }

   public int getSlot() {
      return this.slot;
   }
}
