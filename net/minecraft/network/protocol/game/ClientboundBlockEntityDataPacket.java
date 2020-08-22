package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundBlockEntityDataPacket implements Packet {
   private BlockPos pos;
   private int type;
   private CompoundTag tag;

   public ClientboundBlockEntityDataPacket() {
   }

   public ClientboundBlockEntityDataPacket(BlockPos var1, int var2, CompoundTag var3) {
      this.pos = var1;
      this.type = var2;
      this.tag = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
      this.type = var1.readUnsignedByte();
      this.tag = var1.readNbt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);
      var1.writeByte((byte)this.type);
      var1.writeNbt(this.tag);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockEntityData(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getType() {
      return this.type;
   }

   public CompoundTag getTag() {
      return this.tag;
   }
}
