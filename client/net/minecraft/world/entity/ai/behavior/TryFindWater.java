package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TryFindWater extends Behavior<PathfinderMob> {
   private final int range;
   private final float speedModifier;
   private long nextOkStartTime;

   public TryFindWater(int var1, float var2) {
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
      this.nextOkStartTime = var3 + 20L + 2L;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return !var2.level.getFluidState(var2.blockPosition()).is(FluidTags.WATER);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      if (var3 >= this.nextOkStartTime) {
         BlockPos var5 = null;
         BlockPos var6 = null;
         BlockPos var7 = var2.blockPosition();

         for(BlockPos var10 : BlockPos.withinManhattan(var7, this.range, this.range, this.range)) {
            if (var10.getX() != var7.getX() || var10.getZ() != var7.getZ()) {
               BlockState var11 = var2.level.getBlockState(var10.above());
               BlockState var12 = var2.level.getBlockState(var10);
               if (var12.is(Blocks.WATER)) {
                  if (var11.isAir()) {
                     var5 = var10.immutable();
                     break;
                  }

                  if (var6 == null && !var10.closerToCenterThan(var2.position(), 1.5)) {
                     var6 = var10.immutable();
                  }
               }
            }
         }

         if (var5 == null) {
            var5 = var6;
         }

         if (var5 != null) {
            this.nextOkStartTime = var3 + 40L;
            BehaviorUtils.setWalkAndLookTargetMemories(var2, var5, this.speedModifier, 0);
         }
      }
   }
}
