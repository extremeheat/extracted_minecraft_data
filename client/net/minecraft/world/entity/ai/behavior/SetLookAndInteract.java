package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

public class SetLookAndInteract {
   public SetLookAndInteract() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(EntityType<?> var0, int var1) {
      int var2 = var1 * var1;
      return BehaviorBuilder.create(
         var2x -> var2x.group(
                  var2x.registered(MemoryModuleType.LOOK_TARGET),
                  var2x.absent(MemoryModuleType.INTERACTION_TARGET),
                  var2x.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
               )
               .apply(
                  var2x,
                  (var3, var4, var5) -> (var6, var7, var8) -> {
                        Optional var10 = var2x.<NearestVisibleLivingEntities>get(var5)
                           .findClosest(var3xx -> var3xx.distanceToSqr(var7) <= (double)var2 && var0.equals(var3xx.getType()));
                        if (var10.isEmpty()) {
                           return false;
                        } else {
                           LivingEntity var11 = (LivingEntity)var10.get();
                           var4.set(var11);
                           var3.set(new EntityTracker(var11, true));
                           return true;
                        }
                     }
               )
      );
   }
}