package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;

public record ClientboundForgetLevelChunkPacket(ChunkPos a) implements Packet<ClientGamePacketListener> {
   private final ChunkPos pos;

   public ClientboundForgetLevelChunkPacket(FriendlyByteBuf var1) {
      this(var1.readChunkPos());
   }

   public ClientboundForgetLevelChunkPacket(ChunkPos var1) {
      super();
      this.pos = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeChunkPos(this.pos);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleForgetLevelChunk(this);
   }
}
