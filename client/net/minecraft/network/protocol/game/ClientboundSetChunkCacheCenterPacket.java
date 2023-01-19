package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetChunkCacheCenterPacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;

   public ClientboundSetChunkCacheCenterPacket(int var1, int var2) {
      super();
      this.x = var1;
      this.z = var2;
   }

   public ClientboundSetChunkCacheCenterPacket(FriendlyByteBuf var1) {
      super();
      this.x = var1.readVarInt();
      this.z = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.x);
      var1.writeVarInt(this.z);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetChunkCacheCenter(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }
}
