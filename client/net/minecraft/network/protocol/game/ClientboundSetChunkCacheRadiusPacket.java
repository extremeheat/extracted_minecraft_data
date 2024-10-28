package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetChunkCacheRadiusPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetChunkCacheRadiusPacket> STREAM_CODEC = Packet.codec(ClientboundSetChunkCacheRadiusPacket::write, ClientboundSetChunkCacheRadiusPacket::new);
   private final int radius;

   public ClientboundSetChunkCacheRadiusPacket(int var1) {
      super();
      this.radius = var1;
   }

   private ClientboundSetChunkCacheRadiusPacket(FriendlyByteBuf var1) {
      super();
      this.radius = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.radius);
   }

   public PacketType<ClientboundSetChunkCacheRadiusPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_CHUNK_CACHE_RADIUS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetChunkCacheRadius(this);
   }

   public int getRadius() {
      return this.radius;
   }
}
