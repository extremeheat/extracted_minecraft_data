package net.minecraft.world.entity.ai.memory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.phys.Vec3;

public class WalkTarget {
   private final PositionTracker target;
   private final float speedModifier;
   private final int closeEnoughDist;

   public WalkTarget(BlockPos var1, float var2, int var3) {
      this((PositionTracker)(new BlockPosTracker(var1)), var2, var3);
   }

   public WalkTarget(Vec3 var1, float var2, int var3) {
      this((PositionTracker)(new BlockPosTracker(BlockPos.containing(var1))), var2, var3);
   }

   public WalkTarget(Entity var1, float var2, int var3) {
      this((PositionTracker)(new EntityTracker(var1, false)), var2, var3);
   }

   public WalkTarget(PositionTracker var1, float var2, int var3) {
      super();
      this.target = var1;
      this.speedModifier = var2;
      this.closeEnoughDist = var3;
   }

   public PositionTracker getTarget() {
      return this.target;
   }

   public float getSpeedModifier() {
      return this.speedModifier;
   }

   public int getCloseEnoughDist() {
      return this.closeEnoughDist;
   }
}
