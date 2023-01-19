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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class TryFindLand extends Behavior<PathfinderMob> {
   private static final int COOLDOWN_TICKS = 60;
   private final int range;
   private final float speedModifier;
   private long nextOkStartTime;

   public TryFindLand(int var1, float var2) {
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
      this.nextOkStartTime = var3 + 60L;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return var2.level.getFluidState(var2.blockPosition()).is(FluidTags.WATER);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      if (var3 >= this.nextOkStartTime) {
         BlockPos var5 = var2.blockPosition();
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
         CollisionContext var7 = CollisionContext.of(var2);

         for(BlockPos var9 : BlockPos.withinManhattan(var5, this.range, this.range, this.range)) {
            if (var9.getX() != var5.getX() || var9.getZ() != var5.getZ()) {
               BlockState var10 = var1.getBlockState(var9);
               BlockState var11 = var1.getBlockState(var6.setWithOffset(var9, Direction.DOWN));
               if (!var10.is(Blocks.WATER)
                  && var1.getFluidState(var9).isEmpty()
                  && var10.getCollisionShape(var1, var9, var7).isEmpty()
                  && var11.isFaceSturdy(var1, var6, Direction.UP)) {
                  this.nextOkStartTime = var3 + 60L;
                  BehaviorUtils.setWalkAndLookTargetMemories(var2, var9.immutable(), this.speedModifier, 1);
                  return;
               }
            }
         }
      }
   }
}
