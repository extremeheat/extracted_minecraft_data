package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetLookAndInteract extends Behavior {
   private final EntityType type;
   private final int interactionRangeSqr;
   private final Predicate targetFilter;
   private final Predicate selfFilter;

   public SetLookAndInteract(EntityType var1, int var2, Predicate var3, Predicate var4) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.type = var1;
      this.interactionRangeSqr = var2 * var2;
      this.targetFilter = var4;
      this.selfFilter = var3;
   }

   public SetLookAndInteract(EntityType var1, int var2) {
      this(var1, var2, (var0) -> {
         return true;
      }, (var0) -> {
         return true;
      });
   }

   public boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return this.selfFilter.test(var2) && this.getVisibleEntities(var2).stream().anyMatch(this::isMatchingTarget);
   }

   public void start(ServerLevel var1, LivingEntity var2, long var3) {
      super.start(var1, var2, var3);
      Brain var5 = var2.getBrain();
      var5.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).ifPresent((var3x) -> {
         var3x.stream().filter((var2x) -> {
            return var2x.distanceToSqr(var2) <= (double)this.interactionRangeSqr;
         }).filter(this::isMatchingTarget).findFirst().ifPresent((var1) -> {
            var5.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)var1);
            var5.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(var1)));
         });
      });
   }

   private boolean isMatchingTarget(LivingEntity var1) {
      return this.type.equals(var1.getType()) && this.targetFilter.test(var1);
   }

   private List getVisibleEntities(LivingEntity var1) {
      return (List)var1.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get();
   }
}
