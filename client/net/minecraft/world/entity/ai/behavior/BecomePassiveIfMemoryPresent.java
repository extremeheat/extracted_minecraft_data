package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Function3;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BecomePassiveIfMemoryPresent {
   public BecomePassiveIfMemoryPresent() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(MemoryModuleType<?> var0, int var1) {
      return BehaviorBuilder.create((Function)((var2) -> var2.group(var2.registered(MemoryModuleType.ATTACK_TARGET), var2.absent(MemoryModuleType.PACIFIED), var2.present(var0)).apply(var2, var2.point(() -> "[BecomePassive if " + String.valueOf(var0) + " present]", (Function3)(var1x, var2x, var3) -> (var3x, var4, var5) -> {
               var2x.setWithExpiry(true, (long)var1);
               var1x.erase();
               return true;
            }))));
   }
}
