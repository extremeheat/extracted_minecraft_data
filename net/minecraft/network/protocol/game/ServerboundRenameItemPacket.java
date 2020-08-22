package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundRenameItemPacket implements Packet {
   private String name;

   public ServerboundRenameItemPacket() {
   }

   public ServerboundRenameItemPacket(String var1) {
      this.name = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.name = var1.readUtf(32767);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(this.name);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}
