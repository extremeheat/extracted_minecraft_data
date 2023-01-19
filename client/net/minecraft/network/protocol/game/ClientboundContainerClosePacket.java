package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerClosePacket implements Packet<ClientGamePacketListener> {
   private final int containerId;

   public ClientboundContainerClosePacket(int var1) {
      super();
      this.containerId = var1;
   }

   public ClientboundContainerClosePacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readUnsignedByte();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerClose(this);
   }

   public int getContainerId() {
      return this.containerId;
   }
}
