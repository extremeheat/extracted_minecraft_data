package net.minecraft.world.entity.ai.behavior;

import java.util.function.BiPredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameRules;

public class StartCelebratingIfTargetDead {
   public StartCelebratingIfTargetDead() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(int var0, BiPredicate<LivingEntity, LivingEntity> var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.present(MemoryModuleType.ATTACK_TARGET), var2.registered(MemoryModuleType.ANGRY_AT), var2.absent(MemoryModuleType.CELEBRATE_LOCATION), var2.registered(MemoryModuleType.DANCING)).apply(var2, (var3, var4, var5, var6) -> {
            return (var7, var8, var9) -> {
               LivingEntity var11 = (LivingEntity)var2.get(var3);
               if (!var11.isDeadOrDying()) {
                  return false;
               } else {
                  if (var1.test(var8, var11)) {
                     var6.setWithExpiry(true, (long)var0);
                  }

                  var5.setWithExpiry(var11.blockPosition(), (long)var0);
                  if (var11.getType() != EntityType.PLAYER || var7.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
                     var3.erase();
                     var4.erase();
                  }

                  return true;
               }
            };
         });
      });
   }
}
