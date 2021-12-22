package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetChunkCacheCenterPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: x int
   private final int field_224;
   // $FF: renamed from: z int
   private final int field_225;

   public ClientboundSetChunkCacheCenterPacket(int var1, int var2) {
      super();
      this.field_224 = var1;
      this.field_225 = var2;
   }

   public ClientboundSetChunkCacheCenterPacket(FriendlyByteBuf var1) {
      super();
      this.field_224 = var1.readVarInt();
      this.field_225 = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_224);
      var1.writeVarInt(this.field_225);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetChunkCacheCenter(this);
   }

   public int getX() {
      return this.field_224;
   }

   public int getZ() {
      return this.field_225;
   }
}
