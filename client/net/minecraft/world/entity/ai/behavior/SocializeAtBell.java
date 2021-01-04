package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SocializeAtBell extends Behavior<LivingEntity> {
   public SocializeAtBell() {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      Optional var4 = var3.getMemory(MemoryModuleType.MEETING_POINT);
      return var1.getRandom().nextInt(100) == 0 && var4.isPresent() && Objects.equals(var1.getDimension().getType(), ((GlobalPos)var4.get()).dimension()) && ((GlobalPos)var4.get()).pos().closerThan(var2.position(), 4.0D) && ((List)var3.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get()).stream().anyMatch((var0) -> {
         return EntityType.VILLAGER.equals(var0.getType());
      });
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((var2x) -> {
         var2x.stream().filter((var0) -> {
            return EntityType.VILLAGER.equals(var0.getType());
         }).filter((var1) -> {
            return var1.distanceToSqr(var2) <= 32.0D;
         }).findFirst().ifPresent((var1) -> {
            var5.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)var1);
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(var1)));
            var5.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityPosWrapper(var1), 0.3F, 1)));
         });
      });
   }
}
