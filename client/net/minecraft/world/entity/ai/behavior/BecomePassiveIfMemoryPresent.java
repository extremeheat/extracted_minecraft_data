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

   public static BehaviorControl<LivingEntity> create(final MemoryModuleType<?> pacifyingMemory, final int pacifyDuration) {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.registered(MemoryModuleType.ATTACK_TARGET), i.absent(MemoryModuleType.PACIFIED), i.present(pacifyingMemory)).apply(i, i.point(() -> "[BecomePassive if " + String.valueOf(pacifyingMemory) + " present]", (Function3)(attackTarget, pacified, pacifying) -> (level, body, timestamp) -> {
               pacified.setWithExpiry(true, (long)pacifyDuration);
               attackTarget.erase();
               return true;
            }))));
   }
}
