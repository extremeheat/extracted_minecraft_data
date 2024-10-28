package net.minecraft.world.entity.animal.axolotl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class ValidatePlayDead {
   public ValidatePlayDead() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.present(MemoryModuleType.PLAY_DEAD_TICKS), var0.registered(MemoryModuleType.HURT_BY_ENTITY)).apply(var0, (var1, var2) -> {
            return (var3, var4, var5) -> {
               int var7 = (Integer)var0.get(var1);
               if (var7 <= 0) {
                  var1.erase();
                  var2.erase();
                  var4.getBrain().useDefaultActivity();
               } else {
                  var1.set(var7 - 1);
               }

               return true;
            };
         });
      });
   }
}
