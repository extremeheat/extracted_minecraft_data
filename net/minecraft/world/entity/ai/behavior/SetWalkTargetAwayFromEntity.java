package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetAwayFromEntity extends Behavior {
   private final MemoryModuleType memory;
   private final float speed;

   public SetWalkTargetAwayFromEntity(MemoryModuleType var1, float var2) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, var1, MemoryStatus.VALUE_PRESENT));
      this.memory = var1;
      this.speed = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      Entity var3 = (Entity)var2.getBrain().getMemory(this.memory).get();
      return var2.distanceToSqr(var3) < 36.0D;
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      Entity var5 = (Entity)var2.getBrain().getMemory(this.memory).get();
      moveAwayFromMob(var2, var5, this.speed);
   }

   public static void moveAwayFromMob(PathfinderMob var0, Entity var1, float var2) {
      for(int var3 = 0; var3 < 10; ++var3) {
         Vec3 var4 = RandomPos.getLandPosAvoid(var0, 16, 7, var1.position());
         if (var4 != null) {
            var0.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var4, var2, 0)));
            return;
         }
      }

   }
}
