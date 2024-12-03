package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;

public class CrossbowAttack<E extends Mob & CrossbowAttackMob, T extends LivingEntity> extends Behavior<E> {
   private static final int TIMEOUT = 1200;
   private int attackDelay;
   private CrossbowState crossbowState;

   public CrossbowAttack() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
      this.crossbowState = CrossbowAttack.CrossbowState.UNCHARGED;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      LivingEntity var3 = getAttackTarget(var2);
      return var2.isHolding(Items.CROSSBOW) && BehaviorUtils.canSee(var2, var3) && BehaviorUtils.isWithinAttackRange(var2, var3, 0);
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(var1, var2);
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      LivingEntity var5 = getAttackTarget(var2);
      this.lookAtTarget(var2, var5);
      this.crossbowAttack(var2, var5);
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      if (var2.isUsingItem()) {
         var2.stopUsingItem();
      }

      if (var2.isHolding(Items.CROSSBOW)) {
         ((CrossbowAttackMob)var2).setChargingCrossbow(false);
         var2.getUseItem().set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
      }

   }

   private void crossbowAttack(E var1, LivingEntity var2) {
      if (this.crossbowState == CrossbowAttack.CrossbowState.UNCHARGED) {
         var1.startUsingItem(ProjectileUtil.getWeaponHoldingHand(var1, Items.CROSSBOW));
         this.crossbowState = CrossbowAttack.CrossbowState.CHARGING;
         ((CrossbowAttackMob)var1).setChargingCrossbow(true);
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.CHARGING) {
         if (!var1.isUsingItem()) {
            this.crossbowState = CrossbowAttack.CrossbowState.UNCHARGED;
         }

         int var3 = var1.getTicksUsingItem();
         ItemStack var4 = var1.getUseItem();
         if (var3 >= CrossbowItem.getChargeDuration(var4, var1)) {
            var1.releaseUsingItem();
            this.crossbowState = CrossbowAttack.CrossbowState.CHARGED;
            this.attackDelay = 20 + var1.getRandom().nextInt(20);
            ((CrossbowAttackMob)var1).setChargingCrossbow(false);
         }
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.CHARGED) {
         --this.attackDelay;
         if (this.attackDelay == 0) {
            this.crossbowState = CrossbowAttack.CrossbowState.READY_TO_ATTACK;
         }
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.READY_TO_ATTACK) {
         ((RangedAttackMob)var1).performRangedAttack(var2, 1.0F);
         this.crossbowState = CrossbowAttack.CrossbowState.UNCHARGED;
      }

   }

   private void lookAtTarget(Mob var1, LivingEntity var2) {
      var1.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(var2, true));
   }

   private static LivingEntity getAttackTarget(LivingEntity var0) {
      return (LivingEntity)var0.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.tick(var1, (Mob)var2, var3);
   }

   static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;

      private CrossbowState() {
      }

      // $FF: synthetic method
      private static CrossbowState[] $values() {
         return new CrossbowState[]{UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK};
      }
   }
}
