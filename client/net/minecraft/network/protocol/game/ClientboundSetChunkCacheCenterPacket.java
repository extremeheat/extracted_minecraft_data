package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetChunkCacheCenterPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetChunkCacheCenterPacket> STREAM_CODEC = Packet.codec(ClientboundSetChunkCacheCenterPacket::write, ClientboundSetChunkCacheCenterPacket::new);
   private final int x;
   private final int z;

   public ClientboundSetChunkCacheCenterPacket(int var1, int var2) {
      super();
      this.x = var1;
      this.z = var2;
   }

   private ClientboundSetChunkCacheCenterPacket(FriendlyByteBuf var1) {
      super();
      this.x = var1.readVarInt();
      this.z = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.x);
      var1.writeVarInt(this.z);
   }

   public PacketType<ClientboundSetChunkCacheCenterPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_CHUNK_CACHE_CENTER;
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
