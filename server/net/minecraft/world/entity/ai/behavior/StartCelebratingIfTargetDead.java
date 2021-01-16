package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;

public class StartCelebratingIfTargetDead extends Behavior<LivingEntity> {
   private final int celebrateDuration;
   private final BiPredicate<LivingEntity, LivingEntity> dancePredicate;

   public StartCelebratingIfTargetDead(int var1, BiPredicate<LivingEntity, LivingEntity> var2) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryStatus.REGISTERED, MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_ABSENT, MemoryModuleType.DANCING, MemoryStatus.REGISTERED));
      this.celebrateDuration = var1;
      this.dancePredicate = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return this.getAttackTarget(var2).isDeadOrDying();
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      LivingEntity var5 = this.getAttackTarget(var2);
      if (this.dancePredicate.test(var2, var5)) {
         var2.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, (long)this.celebrateDuration);
      }

      var2.getBrain().setMemoryWithExpiry(MemoryModuleType.CELEBRATE_LOCATION, var5.blockPosition(), (long)this.celebrateDuration);
      if (var5.getType() != EntityType.PLAYER || var1.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         var2.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         var2.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
      }

   }

   private LivingEntity getAttackTarget(LivingEntity var1) {
      return (LivingEntity)var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}
