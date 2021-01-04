package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetEntityLookTarget extends Behavior<LivingEntity> {
   private final Predicate<LivingEntity> predicate;
   private final float maxDistSqr;

   public SetEntityLookTarget(MobCategory var1, float var2) {
      this((var1x) -> {
         return var1.equals(var1x.getType().getCategory());
      }, var2);
   }

   public SetEntityLookTarget(EntityType<?> var1, float var2) {
      this((var1x) -> {
         return var1.equals(var1x.getType());
      }, var2);
   }

   public SetEntityLookTarget(Predicate<LivingEntity> var1, float var2) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.predicate = var1;
      this.maxDistSqr = var2 * var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return ((List)var2.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get()).stream().anyMatch(this.predicate);
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((var3x) -> {
         var3x.stream().filter(this.predicate).filter((var2x) -> {
            return var2x.distanceToSqr(var2) <= (double)this.maxDistSqr;
         }).findFirst().ifPresent((var1) -> {
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(var1)));
         });
      });
   }
}
