package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ServerboundChunkBatchReceivedPacket(float a) implements Packet<ServerGamePacketListener> {
   private final float desiredChunksPerTick;

   public ServerboundChunkBatchReceivedPacket(FriendlyByteBuf var1) {
      this(var1.readFloat());
   }

   public ServerboundChunkBatchReceivedPacket(float var1) {
      super();
      this.desiredChunksPerTick = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeFloat(this.desiredChunksPerTick);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChunkBatchReceived(this);
   }
}
