package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;

public record ClientboundForgetLevelChunkPacket(ChunkPos pos) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundForgetLevelChunkPacket> STREAM_CODEC = Packet.codec(
      ClientboundForgetLevelChunkPacket::write, ClientboundForgetLevelChunkPacket::new
   );

   private ClientboundForgetLevelChunkPacket(FriendlyByteBuf var1) {
      this(var1.readChunkPos());
   }

   public ClientboundForgetLevelChunkPacket(ChunkPos pos) {
      super();
      this.pos = pos;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeChunkPos(this.pos);
   }

   @Override
   public PacketType<ClientboundForgetLevelChunkPacket> type() {
      return GamePacketTypes.CLIENTBOUND_FORGET_LEVEL_CHUNK;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleForgetLevelChunk(this);
   }
}
