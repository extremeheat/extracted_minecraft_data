package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
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
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
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
         Iterator var8 = BlockPos.withinManhattan(var6, this.range, this.range, this.range).iterator();

         while(true) {
            BlockPos var9;
            do {
               do {
                  do {
                     if (!var8.hasNext()) {
                        return;
                     }

                     var9 = (BlockPos)var8.next();
                  } while(var9.getX() == var6.getX() && var9.getZ() == var6.getZ());
               } while(!var1.getBlockState(var9).getCollisionShape(var1, var9, var5).isEmpty());
            } while(var1.getBlockState(var7.setWithOffset(var9, (Direction)Direction.DOWN)).getCollisionShape(var1, var9, var5).isEmpty());

            Iterator var10 = Direction.Plane.HORIZONTAL.iterator();

            while(var10.hasNext()) {
               Direction var11 = (Direction)var10.next();
               var7.setWithOffset(var9, (Direction)var11);
               if (var1.getBlockState(var7).isAir() && var1.getBlockState(var7.move(Direction.DOWN)).is(Blocks.WATER)) {
                  this.nextOkStartTime = var3 + 40L;
                  BehaviorUtils.setWalkAndLookTargetMemories(var2, (BlockPos)var9, this.speedModifier, 0);
                  return;
               }
            }
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
