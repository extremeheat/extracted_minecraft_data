package net.minecraft.world.entity.ai.behavior.warden;

import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class TryToSniff {
   private static final IntProvider SNIFF_COOLDOWN = UniformInt.of(100, 200);

   public TryToSniff() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((var0) -> {
         return var0.group(var0.registered(MemoryModuleType.IS_SNIFFING), var0.registered(MemoryModuleType.WALK_TARGET), var0.absent(MemoryModuleType.SNIFF_COOLDOWN), var0.present(MemoryModuleType.NEAREST_ATTACKABLE), var0.absent(MemoryModuleType.DISTURBANCE_LOCATION)).apply(var0, (var0x, var1, var2, var3, var4) -> {
            return (var3x, var4x, var5) -> {
               var0x.set(Unit.INSTANCE);
               var2.setWithExpiry(Unit.INSTANCE, (long)SNIFF_COOLDOWN.sample(var3x.getRandom()));
               var1.erase();
               var4x.setPose(Pose.SNIFFING);
               return true;
            };
         });
      });
   }
}
