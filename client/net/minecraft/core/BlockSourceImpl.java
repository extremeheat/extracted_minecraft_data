package net.minecraft.core;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSourceImpl implements BlockSource {
   private final ServerLevel level;
   private final BlockPos pos;

   public BlockSourceImpl(ServerLevel var1, BlockPos var2) {
      super();
      this.level = var1;
      this.pos = var2;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   // $FF: renamed from: x () double
   public double method_2() {
      return (double)this.pos.getX() + 0.5D;
   }

   // $FF: renamed from: y () double
   public double method_3() {
      return (double)this.pos.getY() + 0.5D;
   }

   // $FF: renamed from: z () double
   public double method_4() {
      return (double)this.pos.getZ() + 0.5D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getBlockState() {
      return this.level.getBlockState(this.pos);
   }

   public <T extends BlockEntity> T getEntity() {
      return this.level.getBlockEntity(this.pos);
   }
}
