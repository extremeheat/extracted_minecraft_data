package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SetWalkTargetFromAttackTargetIfTargetOutOfReach {
   private static final int PROJECTILE_ATTACK_RANGE_BUFFER = 1;

   public SetWalkTargetFromAttackTargetIfTargetOutOfReach() {
      super();
   }

   public static BehaviorControl<Mob> create(float var0) {
      return create((var1) -> {
         return var0;
      });
   }

   public static BehaviorControl<Mob> create(Function<LivingEntity, Float> var0) {
      return BehaviorBuilder.create((var1) -> {
         return var1.group(var1.registered(MemoryModuleType.WALK_TARGET), var1.registered(MemoryModuleType.LOOK_TARGET), var1.present(MemoryModuleType.ATTACK_TARGET), var1.registered(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(var1, (var2, var3, var4, var5) -> {
            return (var6, var7, var8) -> {
               LivingEntity var10 = (LivingEntity)var1.get(var4);
               Optional var11 = var1.tryGet(var5);
               if (var11.isPresent() && ((NearestVisibleLivingEntities)var11.get()).contains(var10) && BehaviorUtils.isWithinAttackRange(var7, var10, 1)) {
                  var2.erase();
               } else {
                  var3.set(new EntityTracker(var10, true));
                  var2.set(new WalkTarget(new EntityTracker(var10, false), (Float)var0.apply(var7), 0));
               }

               return true;
            };
         });
      });
   }
}
