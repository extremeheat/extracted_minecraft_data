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
      this.blockPos = BlockPos.containing(var1);
      this.centerPosition = var1;
   }

   public Vec3 currentPosition() {
      return this.centerPosition;
   }

   public BlockPos currentBlockPosition() {
      return this.blockPos;
   }

   public boolean isVisibleBy(LivingEntity var1) {
      return true;
   }

   public String toString() {
      String var10000 = String.valueOf(this.blockPos);
      return "BlockPosTracker{blockPos=" + var10000 + ", centerPosition=" + String.valueOf(this.centerPosition) + "}";
   }
}
