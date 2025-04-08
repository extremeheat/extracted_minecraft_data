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
      return create(var0, (var1x) -> var1, MemoryModuleType.NEAREST_VISIBLE_ADULT);
   }

   public static OneShot<LivingEntity> create(UniformInt var0, Function<LivingEntity, Float> var1, MemoryModuleType<? extends LivingEntity> var2) {
      return BehaviorBuilder.create((Function)((var3) -> var3.group(var3.present(var2), var3.registered(MemoryModuleType.LOOK_TARGET), var3.absent(MemoryModuleType.WALK_TARGET)).apply(var3, (var3x, var4, var5) -> (var6, var7, var8) -> {
               if (!var7.isBaby()) {
                  return false;
               } else {
                  LivingEntity var10 = (LivingEntity)var3.get(var3x);
                  if (var7.closerThan(var10, (double)(var0.getMaxValue() + 1)) && !var7.closerThan(var10, (double)var0.getMinValue())) {
                     WalkTarget var11 = new WalkTarget(new EntityTracker(var10, false), (Float)var1.apply(var7), var0.getMinValue() - 1);
                     var4.set(new EntityTracker(var10, true));
                     var5.set(var11);
                     return true;
                  } else {
                     return false;
                  }
               }
            })));
   }
}
