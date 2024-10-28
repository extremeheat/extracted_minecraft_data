package net.minecraft.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BecomePassiveIfMemoryPresent {
   public BecomePassiveIfMemoryPresent() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(MemoryModuleType<?> var0, int var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.registered(MemoryModuleType.ATTACK_TARGET), var2.absent(MemoryModuleType.PACIFIED), var2.present(var0)).apply(var2, var2.point(() -> {
            return "[BecomePassive if " + String.valueOf(var0) + " present]";
         }, (var1x, var2x, var3) -> {
            return (var3x, var4, var5) -> {
               var2x.setWithExpiry(true, (long)var1);
               var1x.erase();
               return true;
            };
         }));
      });
   }
}
