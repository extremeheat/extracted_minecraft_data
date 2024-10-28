package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class VillagerCalmDown {
   private static final int SAFE_DISTANCE_FROM_DANGER = 36;

   public VillagerCalmDown() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.registered(MemoryModuleType.HURT_BY), var0.registered(MemoryModuleType.HURT_BY_ENTITY), var0.registered(MemoryModuleType.NEAREST_HOSTILE)).apply(var0, (var1, var2, var3) -> {
            return (var4, var5, var6) -> {
               boolean var8 = var0.tryGet(var1).isPresent() || var0.tryGet(var3).isPresent() || var0.tryGet(var2).filter((var1x) -> {
                  return var1x.distanceToSqr(var5) <= 36.0;
               }).isPresent();
               if (!var8) {
                  var1.erase();
                  var2.erase();
                  var5.getBrain().updateActivityFromSchedule(var4.getDayTime(), var4.getGameTime());
               }

               return true;
            };
         });
      });
   }
}
