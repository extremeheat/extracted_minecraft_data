package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerButtonClickPacket implements Packet {
   private int containerId;
   private int buttonId;

   public ServerboundContainerButtonClickPacket() {
   }

   public ServerboundContainerButtonClickPacket(int var1, int var2) {
      this.containerId = var1;
      this.buttonId = var2;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerButtonClick(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readByte();
      this.buttonId = var1.readByte();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
