package net.minecraft.world.entity.ai.behavior;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class BackUpIfTooClose {
   public BackUpIfTooClose() {
      super();
   }

   public static OneShot<Mob> create(int var0, float var1) {
      return BehaviorBuilder.create((var2) -> {
         return var2.group(var2.absent(MemoryModuleType.WALK_TARGET), var2.registered(MemoryModuleType.LOOK_TARGET), var2.present(MemoryModuleType.ATTACK_TARGET), var2.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(var2, (var3, var4, var5, var6) -> {
            return (var6x, var7, var8) -> {
               LivingEntity var10 = (LivingEntity)var2.get(var5);
               if (var10.closerThan(var7, (double)var0) && ((NearestVisibleLivingEntities)var2.get(var6)).contains(var10)) {
                  var4.set(new EntityTracker(var10, true));
                  var7.getMoveControl().strafe(-var1, 0.0F);
                  var7.setYRot(Mth.rotateIfNecessary(var7.getYRot(), var7.yHeadRot, 0.0F));
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
