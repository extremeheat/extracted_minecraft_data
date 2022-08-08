package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BedBlockEntity extends BlockEntity {
   private DyeColor color;

   public BedBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BED, var1, var2);
      this.color = ((BedBlock)var2.getBlock()).getColor();
   }

   public BedBlockEntity(BlockPos var1, BlockState var2, DyeColor var3) {
      super(BlockEntityType.BED, var1, var2);
      this.color = var3;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public void setColor(DyeColor var1) {
      this.color = var1;
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
