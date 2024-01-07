package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameRules;

public class StopBeingAngryIfTargetDead {
   public StopBeingAngryIfTargetDead() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create(
         var0 -> var0.group(var0.present(MemoryModuleType.ANGRY_AT))
               .apply(
                  var0,
                  var1 -> (var2, var3, var4) -> {
                        Optional.ofNullable(var2.getEntity(var0.get(var1)))
                           .map(var0xxx -> var0xxx instanceof LivingEntity var1xxx ? var1xxx : null)
                           .filter(LivingEntity::isDeadOrDying)
                           .filter(var1xx -> var1xx.getType() != EntityType.PLAYER || var2.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS))
                           .ifPresent(var1xx -> var1.erase());
                        return true;
                     }
               )
      );
   }
}
