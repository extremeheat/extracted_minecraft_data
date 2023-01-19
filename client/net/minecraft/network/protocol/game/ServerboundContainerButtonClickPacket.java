package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerButtonClickPacket implements Packet<ServerGamePacketListener> {
   private final int containerId;
   private final int buttonId;

   public ServerboundContainerButtonClickPacket(int var1, int var2) {
      super();
      this.containerId = var1;
      this.buttonId = var2;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerButtonClick(this);
   }

   public ServerboundContainerButtonClickPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
      this.buttonId = var1.readByte();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeByte(this.buttonId);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getButtonId() {
      return this.buttonId;
   }
}
