package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetSpawnPositionPacket implements Packet<ClientGamePacketListener> {
   private BlockPos pos;

   public ClientboundSetSpawnPositionPacket() {
      super();
   }

   public ClientboundSetSpawnPositionPacket(BlockPos var1) {
      super();
      this.pos = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetSpawn(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
