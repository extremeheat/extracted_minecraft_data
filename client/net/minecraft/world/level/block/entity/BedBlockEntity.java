package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BedBlockEntity extends BlockEntity {
   private final DyeColor color;

   public BedBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      this(worldPosition, blockState, ((BedBlock)blockState.getBlock()).getColor());
   }

   public BedBlockEntity(final BlockPos worldPosition, final BlockState blockState, final DyeColor color) {
      super(BlockEntityType.BED, worldPosition, blockState);
      this.color = color;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public DyeColor getColor() {
      return this.color;
   }
}
