package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BlockPosTracker implements PositionTracker {
   private final BlockPos blockPos;
   private final Vec3 centerPosition;

   public BlockPosTracker(final BlockPos blockPos) {
      super();
      this.blockPos = blockPos.immutable();
      this.centerPosition = Vec3.atCenterOf(blockPos);
   }

   public BlockPosTracker(final Vec3 vec) {
      super();
      this.blockPos = BlockPos.containing(vec);
      this.centerPosition = vec;
   }

   public Vec3 currentPosition() {
      return this.centerPosition;
   }

   public BlockPos currentBlockPosition() {
      return this.blockPos;
   }

   public boolean isVisibleBy(final LivingEntity body) {
      return true;
   }

   public String toString() {
      String var10000 = String.valueOf(this.blockPos);
      return "BlockPosTracker{blockPos=" + var10000 + ", centerPosition=" + String.valueOf(this.centerPosition) + "}";
   }
}
