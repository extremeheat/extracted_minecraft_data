package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerClosePacket implements Packet {
   private int containerId;

   public ServerboundContainerClosePacket() {
   }

   public ServerboundContainerClosePacket(int var1) {
      this.containerId = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerClose(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readByte();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
   }
}
