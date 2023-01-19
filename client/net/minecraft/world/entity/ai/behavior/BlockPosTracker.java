package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BlockPosTracker implements PositionTracker {
   private final BlockPos blockPos;
   private final Vec3 centerPosition;

   public BlockPosTracker(BlockPos var1) {
      super();
      this.blockPos = var1.immutable();
      this.centerPosition = Vec3.atCenterOf(var1);
   }

   public BlockPosTracker(Vec3 var1) {
      super();
      this.blockPos = new BlockPos(var1);
      this.centerPosition = var1;
   }

   @Override
   public Vec3 currentPosition() {
      return this.centerPosition;
   }

   @Override
   public BlockPos currentBlockPosition() {
      return this.blockPos;
   }

   @Override
   public boolean isVisibleBy(LivingEntity var1) {
      return true;
   }

   @Override
   public String toString() {
      return "BlockPosTracker{blockPos=" + this.blockPos + ", centerPosition=" + this.centerPosition + "}";
   }
}
