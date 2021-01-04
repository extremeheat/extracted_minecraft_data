package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class VillageBoundRandomStroll extends Behavior<PathfinderMob> {
   private final float speed;
   private final int maxXyDist;
   private final int maxYDist;

   public VillageBoundRandomStroll(float var1) {
      this(var1, 10, 7);
   }

   public VillageBoundRandomStroll(float var1, int var2, int var3) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speed = var1;
      this.maxXyDist = var2;
      this.maxYDist = var3;
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      BlockPos var5 = new BlockPos(var2);
      if (var1.isVillage(var5)) {
         this.setRandomPos(var2);
      } else {
         SectionPos var6 = SectionPos.of(var5);
         SectionPos var7 = BehaviorUtils.findSectionClosestToVillage(var1, var6, 2);
         if (var7 != var6) {
            this.setTargetedPos(var2, var7);
         } else {
            this.setRandomPos(var2);
         }
      }

   }

   private void setTargetedPos(PathfinderMob var1, SectionPos var2) {
      BlockPos var3 = var2.center();
      Optional var4 = Optional.ofNullable(RandomPos.getPosTowards(var1, this.maxXyDist, this.maxYDist, new Vec3((double)var3.getX(), (double)var3.getY(), (double)var3.getZ())));
      var1.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var4.map((var1x) -> {
         return new WalkTarget(var1x, this.speed, 0);
      }));
   }

   private void setRandomPos(PathfinderMob var1) {
      Optional var2 = Optional.ofNullable(RandomPos.getLandPos(var1, this.maxXyDist, this.maxYDist));
      var1.getBrain().setMemory(MemoryModuleType.WALK_TARGET, var2.map((var1x) -> {
         return new WalkTarget(var1x, this.speed, 0);
      }));
   }
}
