package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundForgetLevelChunkPacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;

   public ClientboundForgetLevelChunkPacket(int var1, int var2) {
      super();
      this.x = var1;
      this.z = var2;
   }

   public ClientboundForgetLevelChunkPacket(FriendlyByteBuf var1) {
      super();
      this.x = var1.readInt();
      this.z = var1.readInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.x);
      var1.writeInt(this.z);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleForgetLevelChunk(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }
}
