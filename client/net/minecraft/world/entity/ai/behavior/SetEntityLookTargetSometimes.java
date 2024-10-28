package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

/** @deprecated */
@Deprecated
public class SetEntityLookTargetSometimes {
   public SetEntityLookTargetSometimes() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(float var0, UniformInt var1) {
      return create(var0, var1, (var0x) -> {
         return true;
      });
   }

   public static BehaviorControl<LivingEntity> create(EntityType<?> var0, float var1, UniformInt var2) {
      return create(var1, var2, (var1x) -> {
         return var0.equals(var1x.getType());
      });
   }

   private static BehaviorControl<LivingEntity> create(float var0, UniformInt var1, Predicate<LivingEntity> var2) {
      float var3 = var0 * var0;
      Ticker var4 = new Ticker(var1);
      return BehaviorBuilder.create((var3x) -> {
         return var3x.group(var3x.absent(MemoryModuleType.LOOK_TARGET), var3x.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(var3x, (var4x, var5) -> {
            return (var6, var7, var8) -> {
               Optional var10 = ((NearestVisibleLivingEntities)var3x.get(var5)).findClosest(var2.and((var2x) -> {
                  return var2x.distanceToSqr(var7) <= (double)var3;
               }));
               if (var10.isEmpty()) {
                  return false;
               } else if (!var4.tickDownAndCheck(var6.random)) {
                  return false;
               } else {
                  var4x.set(new EntityTracker((Entity)var10.get(), true));
                  return true;
               }
            };
         });
      });
   }

   public static final class Ticker {
      private final UniformInt interval;
      private int ticksUntilNextStart;

      public Ticker(UniformInt var1) {
         super();
         if (var1.getMinValue() <= 1) {
            throw new IllegalArgumentException();
         } else {
            this.interval = var1;
         }
      }

      public boolean tickDownAndCheck(RandomSource var1) {
         if (this.ticksUntilNextStart == 0) {
            this.ticksUntilNextStart = this.interval.sample(var1) - 1;
            return false;
         } else {
            return --this.ticksUntilNextStart == 0;
         }
      }
   }
}
