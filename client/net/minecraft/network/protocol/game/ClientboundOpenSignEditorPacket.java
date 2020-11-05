package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundOpenSignEditorPacket implements Packet<ClientGamePacketListener> {
   private BlockPos pos;

   public ClientboundOpenSignEditorPacket() {
      super();
   }

   public ClientboundOpenSignEditorPacket(BlockPos var1) {
      super();
      this.pos = var1;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleOpenSignEditor(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
