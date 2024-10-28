package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class BabyFollowAdult {
   public BabyFollowAdult() {
      super();
   }

   public static OneShot<AgeableMob> create(UniformInt var0, float var1) {
      return create(var0, (var1x) -> {
         return var1;
      });
   }

   public static OneShot<AgeableMob> create(UniformInt var0, Function<LivingEntity, Float> var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.present(MemoryModuleType.NEAREST_VISIBLE_ADULT), var2.registered(MemoryModuleType.LOOK_TARGET), var2.absent(MemoryModuleType.WALK_TARGET)).apply(var2, (var3, var4, var5) -> {
            return (var6, var7, var8) -> {
               if (!var7.isBaby()) {
                  return false;
               } else {
                  AgeableMob var10 = (AgeableMob)var2.get(var3);
                  if (var7.closerThan(var10, (double)(var0.getMaxValue() + 1)) && !var7.closerThan(var10, (double)var0.getMinValue())) {
                     WalkTarget var11 = new WalkTarget(new EntityTracker(var10, false), (Float)var1.apply(var7), var0.getMinValue() - 1);
                     var4.set(new EntityTracker(var10, true));
                     var5.set(var11);
                     return true;
                  } else {
                     return false;
                  }
               }
            };
         });
      });
   }
}
