package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundRenameItemPacket implements Packet<ServerGamePacketListener> {
   private final String name;

   public ServerboundRenameItemPacket(String var1) {
      super();
      this.name = var1;
   }

   public ServerboundRenameItemPacket(FriendlyByteBuf var1) {
      super();
      this.name = var1.readUtf();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.name);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRenameItem(this);
   }

   public String getName() {
      return this.name;
   }
}
