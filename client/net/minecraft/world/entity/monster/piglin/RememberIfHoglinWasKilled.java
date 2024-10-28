package net.minecraft.world.entity.monster.piglin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class RememberIfHoglinWasKilled {
   public RememberIfHoglinWasKilled() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.present(MemoryModuleType.ATTACK_TARGET), var0.registered(MemoryModuleType.HUNTED_RECENTLY)).apply(var0, (var1, var2) -> {
            return (var3, var4, var5) -> {
               LivingEntity var7 = (LivingEntity)var0.get(var1);
               if (var7.getType() == EntityType.HOGLIN && var7.isDeadOrDying()) {
                  var2.setWithExpiry(true, (long)PiglinAi.TIME_BETWEEN_HUNTS.sample(var4.level().random));
               }

               return true;
            };
         });
      });
   }
}
