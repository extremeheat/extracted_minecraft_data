package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetChunkCacheRadiusPacket implements Packet<ClientGamePacketListener> {
   private int radius;

   public ClientboundSetChunkCacheRadiusPacket() {
      super();
   }

   public ClientboundSetChunkCacheRadiusPacket(int var1) {
      super();
      this.radius = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.radius = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.radius);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetChunkCacheRadius(this);
   }

   public int getRadius() {
      return this.radius;
   }
}
