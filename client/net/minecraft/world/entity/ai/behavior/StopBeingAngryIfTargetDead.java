package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameRules;

public class StopBeingAngryIfTargetDead {
   public StopBeingAngryIfTargetDead() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((var0) -> var0.group(var0.present(MemoryModuleType.ANGRY_AT)).apply(var0, (var1) -> (var2, var3, var4) -> {
               Optional.ofNullable(var2.getEntity((UUID)var0.get(var1))).map((var0x) -> {
                  LivingEntity var10000;
                  if (var0x instanceof LivingEntity var1) {
                     var10000 = var1;
                  } else {
                     var10000 = null;
                  }

                  return var10000;
               }).filter(LivingEntity::isDeadOrDying).filter((var1x) -> var1x.getType() != EntityType.PLAYER || var2.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)).ifPresent((var1x) -> var1.erase());
               return true;
            })));
   }
}
