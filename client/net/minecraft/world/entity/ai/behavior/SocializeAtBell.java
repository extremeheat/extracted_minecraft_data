package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SocializeAtBell {
   private static final float SPEED_MODIFIER = 0.3F;

   public SocializeAtBell() {
      super();
   }

   public static OneShot<LivingEntity> create() {
      return BehaviorBuilder.create(
         var0 -> var0.group(
                  var0.registered(MemoryModuleType.WALK_TARGET),
                  var0.registered(MemoryModuleType.LOOK_TARGET),
                  var0.present(MemoryModuleType.MEETING_POINT),
                  var0.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES),
                  var0.absent(MemoryModuleType.INTERACTION_TARGET)
               )
               .apply(
                  var0,
                  (var1, var2, var3, var4, var5) -> (var6, var7, var8) -> {
                        GlobalPos var10 = var0.get(var3);
                        NearestVisibleLivingEntities var11 = var0.get(var4);
                        if (var6.getRandom().nextInt(100) == 0
                           && var6.dimension() == var10.dimension()
                           && var10.pos().closerToCenterThan(var7.position(), 4.0)
                           && var11.contains(var0xxx -> EntityType.VILLAGER.equals(var0xxx.getType()))) {
                           var11.findClosest(var1xx -> EntityType.VILLAGER.equals(var1xx.getType()) && var1xx.distanceToSqr(var7) <= 32.0)
                              .ifPresent(var3xx -> {
                                 var5.set(var3xx);
                                 var2.set(new EntityTracker(var3xx, true));
                                 var1.set(new WalkTarget(new EntityTracker(var3xx, false), 0.3F, 1));
                              });
                           return true;
                        } else {
                           return false;
                        }
                     }
               )
      );
   }
}
