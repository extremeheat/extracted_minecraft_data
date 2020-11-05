package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundHorseScreenOpenPacket implements Packet<ClientGamePacketListener> {
   private int containerId;
   private int size;
   private int entityId;

   public ClientboundHorseScreenOpenPacket() {
      super();
   }

   public ClientboundHorseScreenOpenPacket(int var1, int var2, int var3) {
      super();
      this.containerId = var1;
      this.size = var2;
      this.entityId = var3;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleHorseScreenOpen(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readUnsignedByte();
      this.size = var1.readVarInt();
      this.entityId = var1.readInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
      var1.writeVarInt(this.size);
      var1.writeInt(this.entityId);
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
