package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerClosePacket implements Packet<ClientGamePacketListener> {
   private int containerId;

   public ClientboundContainerClosePacket() {
      super();
   }

   public ClientboundContainerClosePacket(int var1) {
      super();
      this.containerId = var1;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerClose(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readUnsignedByte();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
   }
}
