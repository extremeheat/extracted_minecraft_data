package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetChunkCacheRadiusPacket implements Packet<ClientGamePacketListener> {
   private final int radius;

   public ClientboundSetChunkCacheRadiusPacket(int var1) {
      super();
      this.radius = var1;
   }

   public ClientboundSetChunkCacheRadiusPacket(FriendlyByteBuf var1) {
      super();
      this.radius = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.radius);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetChunkCacheRadius(this);
   }

   public int getRadius() {
      return this.radius;
   }
}
