package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SetEntityLookTarget {
   public SetEntityLookTarget() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(MobCategory var0, float var1) {
      return create((Predicate)((var1x) -> var0.equals(var1x.getType().getCategory())), var1);
   }

   public static OneShot<LivingEntity> create(EntityType<?> var0, float var1) {
      return create((Predicate)((var1x) -> var0.equals(var1x.getType())), var1);
   }

   public static OneShot<LivingEntity> create(float var0) {
      return create((Predicate)((var0x) -> true), var0);
   }

   public static OneShot<LivingEntity> create(Predicate<LivingEntity> var0, float var1) {
      float var2 = var1 * var1;
      return BehaviorBuilder.create((Function)((var2x) -> var2x.group(var2x.absent(MemoryModuleType.LOOK_TARGET), var2x.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(var2x, (var3, var4) -> (var5, var6, var7) -> {
               Optional var9 = ((NearestVisibleLivingEntities)var2x.get(var4)).findClosest(var0.and((var2xx) -> var2xx.distanceToSqr(var6) <= (double)var2 && !var6.hasPassenger(var2xx)));
               if (var9.isEmpty()) {
                  return false;
               } else {
                  var3.set(new EntityTracker((Entity)var9.get(), true));
                  return true;
               }
            })));
   }
}
