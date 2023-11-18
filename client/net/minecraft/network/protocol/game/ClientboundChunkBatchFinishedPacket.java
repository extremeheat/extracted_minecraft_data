package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundChunkBatchFinishedPacket(int a) implements Packet<ClientGamePacketListener> {
   private final int batchSize;

   public ClientboundChunkBatchFinishedPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ClientboundChunkBatchFinishedPacket(int var1) {
      super();
      this.batchSize = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.batchSize);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChunkBatchFinished(this);
   }
}
