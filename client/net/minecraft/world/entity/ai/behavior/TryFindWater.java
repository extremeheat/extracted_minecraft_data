package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
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
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
      this.range = var1;
      this.speedModifier = var2;
   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      this.nextOkStartTime = var3 + 20L + 2L;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return !var2.level.getFluidState(var2.blockPosition()).method_56(FluidTags.WATER);
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      if (var3 >= this.nextOkStartTime) {
         BlockPos var5 = null;
         BlockPos var6 = null;
         BlockPos var7 = var2.blockPosition();
         Iterable var8 = BlockPos.withinManhattan(var7, this.range, this.range, this.range);
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            BlockPos var10 = (BlockPos)var9.next();
            if (var10.getX() != var7.getX() || var10.getZ() != var7.getZ()) {
               BlockState var11 = var2.level.getBlockState(var10.above());
               BlockState var12 = var2.level.getBlockState(var10);
               if (var12.is(Blocks.WATER)) {
                  if (var11.isAir()) {
                     var5 = var10.immutable();
                     break;
                  }

                  if (var6 == null && !var10.closerThan(var2.position(), 1.5D)) {
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
            BehaviorUtils.setWalkAndLookTargetMemories(var2, (BlockPos)var5, this.speedModifier, 0);
         }

      }
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (PathfinderMob)var2, var3);
   }
}
