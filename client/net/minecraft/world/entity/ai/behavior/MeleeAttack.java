package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;

public class MeleeAttack extends Behavior<Mob> {
   private final int cooldownBetweenAttacks;

   public MeleeAttack(int var1) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
      this.cooldownBetweenAttacks = var1;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      LivingEntity var3 = this.getAttackTarget(var2);
      return !this.isHoldingUsableProjectileWeapon(var2) && BehaviorUtils.canSee(var2, var3) && BehaviorUtils.isWithinMeleeAttackRange(var2, var3);
   }

   private boolean isHoldingUsableProjectileWeapon(Mob var1) {
      return var1.isHolding((var1x) -> {
         Item var2 = var1x.getItem();
         return var2 instanceof ProjectileWeaponItem && var1.canFireProjectileWeapon((ProjectileWeaponItem)var2);
      });
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      LivingEntity var5 = this.getAttackTarget(var2);
      BehaviorUtils.lookAtEntity(var2, var5);
      var2.swing(InteractionHand.MAIN_HAND);
      var2.doHurtTarget(var5);
      var2.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.cooldownBetweenAttacks);
   }

   private LivingEntity getAttackTarget(Mob var1) {
      return (LivingEntity)var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}
