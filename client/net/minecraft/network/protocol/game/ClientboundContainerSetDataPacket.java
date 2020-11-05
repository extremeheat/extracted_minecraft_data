package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerSetDataPacket implements Packet<ClientGamePacketListener> {
   private int containerId;
   private int id;
   private int value;

   public ClientboundContainerSetDataPacket() {
      super();
   }

   public ClientboundContainerSetDataPacket(int var1, int var2, int var3) {
      super();
      this.containerId = var1;
      this.id = var2;
      this.value = var3;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerSetData(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readUnsignedByte();
      this.id = var1.readShort();
      this.value = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
      var1.writeShort(this.id);
      var1.writeShort(this.value);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getId() {
      return this.id;
   }

   public int getValue() {
      return this.value;
   }
}
