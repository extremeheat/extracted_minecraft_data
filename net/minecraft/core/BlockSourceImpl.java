package net.minecraft.core;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSourceImpl implements BlockSource {
   private final Level level;
   private final BlockPos pos;

   public BlockSourceImpl(Level var1, BlockPos var2) {
      this.level = var1;
      this.pos = var2;
   }

   public Level getLevel() {
      return this.level;
   }

   public double x() {
      return (double)this.pos.getX() + 0.5D;
   }

   public double y() {
      return (double)this.pos.getY() + 0.5D;
   }

   public double z() {
      return (double)this.pos.getZ() + 0.5D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getBlockState() {
      return this.level.getBlockState(this.pos);
   }

   public BlockEntity getEntity() {
      return this.level.getBlockEntity(this.pos);
   }
}
