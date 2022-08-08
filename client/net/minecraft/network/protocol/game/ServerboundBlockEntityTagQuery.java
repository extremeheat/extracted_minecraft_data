package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundBlockEntityTagQuery implements Packet<ServerGamePacketListener> {
   private final int transactionId;
   private final BlockPos pos;

   public ServerboundBlockEntityTagQuery(int var1, BlockPos var2) {
      super();
      this.transactionId = var1;
      this.pos = var2;
   }

   public ServerboundBlockEntityTagQuery(FriendlyByteBuf var1) {
      super();
      this.transactionId = var1.readVarInt();
      this.pos = var1.readBlockPos();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeBlockPos(this.pos);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleBlockEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
