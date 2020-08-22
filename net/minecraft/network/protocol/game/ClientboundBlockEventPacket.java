package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;

public class ClientboundBlockEventPacket implements Packet {
   private BlockPos pos;
   private int b0;
   private int b1;
   private Block block;

   public ClientboundBlockEventPacket() {
   }

   public ClientboundBlockEventPacket(BlockPos var1, Block var2, int var3, int var4) {
      this.pos = var1;
      this.block = var2;
      this.b0 = var3;
      this.b1 = var4;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
      this.b0 = var1.readUnsignedByte();
      this.b1 = var1.readUnsignedByte();
      this.block = (Block)Registry.BLOCK.byId(var1.readVarInt());
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);
      var1.writeByte(this.b0);
      var1.writeByte(this.b1);
      var1.writeVarInt(Registry.BLOCK.getId(this.block));
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockEvent(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getB0() {
      return this.b0;
   }

   public int getB1() {
      return this.b1;
   }

   public Block getBlock() {
      return this.block;
   }
}
