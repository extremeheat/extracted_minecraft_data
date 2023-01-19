package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundBlockDestructionPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   private final BlockPos pos;
   private final int progress;

   public ClientboundBlockDestructionPacket(int var1, BlockPos var2, int var3) {
      super();
      this.id = var1;
      this.pos = var2;
      this.progress = var3;
   }

   public ClientboundBlockDestructionPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.pos = var1.readBlockPos();
      this.progress = var1.readUnsignedByte();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeBlockPos(this.pos);
      var1.writeByte(this.progress);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockDestruction(this);
   }

   public int getId() {
      return this.id;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getProgress() {
      return this.progress;
   }
}
