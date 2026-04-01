package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.gamerules.GameRules;

public class StopBeingAngryIfTargetDead {
   public StopBeingAngryIfTargetDead() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((i) -> i.group(i.present(MemoryModuleType.ANGRY_AT)).apply(i, (angryAt) -> (level, body, timestamp) -> {
               Optional.ofNullable(level.getEntity((UUID)i.get(angryAt))).map((entity) -> entity instanceof Targetable ? (Entity)((Targetable)entity) : null).filter((rec$) -> ((Targetable)rec$).isDeadOrDying()).filter((angerTarget) -> !angerTarget.is(EntityType.PLAYER) || (Boolean)level.getGameRules().get(GameRules.FORGIVE_DEAD_PLAYERS)).ifPresent((angerTarget) -> angryAt.erase());
               return true;
            })));
   }
}
