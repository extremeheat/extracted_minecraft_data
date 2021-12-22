package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;

public class ClientboundBlockEventPacket implements Packet<ClientGamePacketListener> {
   private final BlockPos pos;
   // $FF: renamed from: b0 int
   private final int field_521;
   // $FF: renamed from: b1 int
   private final int field_522;
   private final Block block;

   public ClientboundBlockEventPacket(BlockPos var1, Block var2, int var3, int var4) {
      super();
      this.pos = var1;
      this.block = var2;
      this.field_521 = var3;
      this.field_522 = var4;
   }

   public ClientboundBlockEventPacket(FriendlyByteBuf var1) {
      super();
      this.pos = var1.readBlockPos();
      this.field_521 = var1.readUnsignedByte();
      this.field_522 = var1.readUnsignedByte();
      this.block = (Block)Registry.BLOCK.byId(var1.readVarInt());
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeByte(this.field_521);
      var1.writeByte(this.field_522);
      var1.writeVarInt(Registry.BLOCK.getId(this.block));
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockEvent(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getB0() {
      return this.field_521;
   }

   public int getB1() {
      return this.field_522;
   }

   public Block getBlock() {
      return this.block;
   }
}
