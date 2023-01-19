package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.CollisionContext;

public class TryFindLandNearWater extends Behavior<PathfinderMob> {
   private final int range;
   private final float speedModifier;
   private long nextOkStartTime;

   public TryFindLandNearWater(int var1, float var2) {
      super(
         ImmutableMap.of(
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.WALK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.LOOK_TARGET,
            MemoryStatus.REGISTERED
         )
      );
      this.range = var1;
      this.speedModifier = var2;
   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      this.nextOkStartTime = var3 + 40L;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return !var2.level.getFluidState(var2.blockPosition()).is(FluidTags.WATER);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      if (var3 >= this.nextOkStartTime) {
         CollisionContext var5 = CollisionContext.of(var2);
         BlockPos var6 = var2.blockPosition();
         BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

         for(BlockPos var9 : BlockPos.withinManhattan(var6, this.range, this.range, this.range)) {
            if ((var9.getX() != var6.getX() || var9.getZ() != var6.getZ())
               && var1.getBlockState(var9).getCollisionShape(var1, var9, var5).isEmpty()
               && !var1.getBlockState(var7.setWithOffset(var9, Direction.DOWN)).getCollisionShape(var1, var9, var5).isEmpty()) {
               for(Direction var11 : Direction.Plane.HORIZONTAL) {
                  var7.setWithOffset(var9, var11);
                  if (var1.getBlockState(var7).isAir() && var1.getBlockState(var7.move(Direction.DOWN)).is(Blocks.WATER)) {
                     this.nextOkStartTime = var3 + 40L;
                     BehaviorUtils.setWalkAndLookTargetMemories(var2, var9, this.speedModifier, 0);
                     return;
                  }
               }
            }
         }
      }
   }
}
