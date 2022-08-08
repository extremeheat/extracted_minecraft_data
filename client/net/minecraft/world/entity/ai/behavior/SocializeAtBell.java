package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SocializeAtBell extends Behavior<LivingEntity> {
   private static final float SPEED_MODIFIER = 0.3F;

   public SocializeAtBell() {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      Optional var4 = var3.getMemory(MemoryModuleType.MEETING_POINT);
      return var1.getRandom().nextInt(100) == 0 && var4.isPresent() && var1.dimension() == ((GlobalPos)var4.get()).dimension() && ((GlobalPos)var4.get()).pos().closerToCenterThan(var2.position(), 4.0) && ((NearestVisibleLivingEntities)var3.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains((var0) -> {
         return EntityType.VILLAGER.equals(var0.getType());
      });
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((var1x) -> {
         return var1x.findClosest((var1) -> {
            return EntityType.VILLAGER.equals(var1.getType()) && var1.distanceToSqr(var2) <= 32.0;
         });
      }).ifPresent((var1x) -> {
         var5.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)var1x);
         var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(var1x, true)));
         var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityTracker(var1x, false), 0.3F, 1)));
      });
   }
}
