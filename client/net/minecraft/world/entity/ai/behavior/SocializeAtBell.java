package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SocializeAtBell {
   private static final float SPEED_MODIFIER = 0.3F;

   public SocializeAtBell() {
      super();
   }

   public static OneShot<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((var0) -> var0.group(var0.registered(MemoryModuleType.WALK_TARGET), var0.registered(MemoryModuleType.LOOK_TARGET), var0.present(MemoryModuleType.MEETING_POINT), var0.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES), var0.absent(MemoryModuleType.INTERACTION_TARGET)).apply(var0, (var1, var2, var3, var4, var5) -> (var6, var7, var8) -> {
               GlobalPos var10 = (GlobalPos)var0.get(var3);
               NearestVisibleLivingEntities var11 = (NearestVisibleLivingEntities)var0.get(var4);
               if (var6.getRandom().nextInt(100) == 0 && var6.dimension() == var10.dimension() && var10.pos().closerToCenterThan(var7.position(), 4.0) && var11.contains((Predicate)((var0x) -> EntityType.VILLAGER.equals(var0x.getType())))) {
                  var11.findClosest((var1x) -> EntityType.VILLAGER.equals(var1x.getType()) && var1x.distanceToSqr(var7) <= 32.0).ifPresent((var3x) -> {
                     var5.set(var3x);
                     var2.set(new EntityTracker(var3x, true));
                     var1.set(new WalkTarget(new EntityTracker(var3x, false), 0.3F, 1));
                  });
                  return true;
               } else {
                  return false;
               }
            })));
   }
}
