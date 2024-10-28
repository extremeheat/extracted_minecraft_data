package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ReactToBell {
   public ReactToBell() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.present(MemoryModuleType.HEARD_BELL_TIME)).apply(var0, (var0x) -> {
            return (var0, var1, var2) -> {
               Raid var4 = var0.getRaidAt(var1.blockPosition());
               if (var4 == null) {
                  var1.getBrain().setActiveActivityIfPossible(Activity.HIDE);
               }

               return true;
            };
         });
      });
   }
}
