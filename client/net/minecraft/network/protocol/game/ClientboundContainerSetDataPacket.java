package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerSetDataPacket implements Packet<ClientGamePacketListener> {
   private final int containerId;
   // $FF: renamed from: id int
   private final int field_503;
   private final int value;

   public ClientboundContainerSetDataPacket(int var1, int var2, int var3) {
      super();
      this.containerId = var1;
      this.field_503 = var2;
      this.value = var3;
   }

   public ClientboundContainerSetDataPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readUnsignedByte();
      this.field_503 = var1.readShort();
      this.value = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeShort(this.field_503);
      var1.writeShort(this.value);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerSetData(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getId() {
      return this.field_503;
   }

   public int getValue() {
      return this.value;
   }
}
