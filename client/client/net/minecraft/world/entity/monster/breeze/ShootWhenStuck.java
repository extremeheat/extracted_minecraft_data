package net.minecraft.world.entity.monster.breeze;

import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class ShootWhenStuck extends Behavior<Breeze> {
   public ShootWhenStuck() {
      super(
         Map.of(
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.BREEZE_JUMP_INHALING,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_JUMP_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.WALK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_SHOOT,
            MemoryStatus.VALUE_ABSENT
         )
      );
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Breeze var2) {
      return var2.isPassenger() || var2.isInWater() || var2.getEffect(MobEffects.LEVITATION) != null;
   }

   protected boolean canStillUse(ServerLevel var1, Breeze var2, long var3) {
      return false;
   }

   protected void start(ServerLevel var1, Breeze var2, long var3) {
      var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 60L);
   }
}
