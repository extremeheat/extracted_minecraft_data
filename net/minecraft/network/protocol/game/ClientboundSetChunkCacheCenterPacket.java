package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetChunkCacheCenterPacket implements Packet {
   private int x;
   private int z;

   public ClientboundSetChunkCacheCenterPacket() {
   }

   public ClientboundSetChunkCacheCenterPacket(int var1, int var2) {
      this.x = var1;
      this.z = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.x = var1.readVarInt();
      this.z = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
