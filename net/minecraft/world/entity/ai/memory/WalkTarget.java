package net.minecraft.world.entity.ai.memory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.BlockPosWrapper;
import net.minecraft.world.entity.ai.behavior.PositionWrapper;
import net.minecraft.world.phys.Vec3;

public class WalkTarget {
   private final PositionWrapper target;
   private final float speed;
   private final int closeEnoughDist;

   public WalkTarget(BlockPos var1, float var2, int var3) {
      this((PositionWrapper)(new BlockPosWrapper(var1)), var2, var3);
   }

   public WalkTarget(Vec3 var1, float var2, int var3) {
      this((PositionWrapper)(new BlockPosWrapper(new BlockPos(var1))), var2, var3);
   }

   public WalkTarget(PositionWrapper var1, float var2, int var3) {
      this.target = var1;
      this.speed = var2;
      this.closeEnoughDist = var3;
   }

   public PositionWrapper getTarget() {
      return this.target;
   }

   public float getSpeed() {
      return this.speed;
   }

   public int getCloseEnoughDist() {
      return this.closeEnoughDist;
   }
}
