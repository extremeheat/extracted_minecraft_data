package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class BabyFollowAdult {
   public BabyFollowAdult() {
      super();
   }

   public static OneShot<LivingEntity> create(UniformInt var0, float var1) {
      return create(var0, (var1x) -> var1, MemoryModuleType.NEAREST_VISIBLE_ADULT, false);
   }

   public static OneShot<LivingEntity> create(UniformInt var0, Function<LivingEntity, Float> var1, MemoryModuleType<? extends LivingEntity> var2, boolean var3) {
      return BehaviorBuilder.create((Function)((var4) -> var4.group(var4.present(var2), var4.registered(MemoryModuleType.LOOK_TARGET), var4.absent(MemoryModuleType.WALK_TARGET)).apply(var4, (var4x, var5, var6) -> (var7, var8, var9) -> {
               if (!var8.isBaby()) {
                  return false;
               } else {
                  LivingEntity var11 = (LivingEntity)var4.get(var4x);
                  if (var8.closerThan(var11, (double)(var0.getMaxValue() + 1)) && !var8.closerThan(var11, (double)var0.getMinValue())) {
                     WalkTarget var12 = new WalkTarget(new EntityTracker(var11, var3, var3), (Float)var1.apply(var8), var0.getMinValue() - 1);
                     var5.set(new EntityTracker(var11, true, var3));
                     var6.set(var12);
                     return true;
                  } else {
                     return false;
                  }
               }
            })));
   }
}
