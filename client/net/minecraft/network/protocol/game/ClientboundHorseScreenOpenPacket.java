package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundHorseScreenOpenPacket implements Packet<ClientGamePacketListener> {
   private final int containerId;
   private final int size;
   private final int entityId;

   public ClientboundHorseScreenOpenPacket(int var1, int var2, int var3) {
      super();
      this.containerId = var1;
      this.size = var2;
      this.entityId = var3;
   }

   public ClientboundHorseScreenOpenPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readUnsignedByte();
      this.size = var1.readVarInt();
      this.entityId = var1.readInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeVarInt(this.size);
      var1.writeInt(this.entityId);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleHorseScreenOpen(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSize() {
      return this.size;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
