package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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

   protected boolean checkExtraStartConditions(final ServerLevel level, final E body) {
      Entity attackTarget = getAttackTarget(body);
      return body.isHolding(Items.CROSSBOW) && BehaviorUtils.canSee(body, attackTarget) && BehaviorUtils.isWithinAttackRange(body, attackTarget, 0);
   }

   protected boolean canStillUse(final ServerLevel level, final E body, final long timestamp) {
      return body.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(level, body);
   }

   protected void tick(final ServerLevel level, final E body, final long timestamp) {
      Entity target = getAttackTarget(body);
      this.lookAtTarget(body, target);
      this.crossbowAttack(body, target);
   }

   protected void stop(final ServerLevel level, final E body, final long timestamp) {
      if (body.isUsingItem()) {
         body.stopUsingItem();
      }

      if (body.isHolding(Items.CROSSBOW)) {
         ((CrossbowAttackMob)body).setChargingCrossbow(false);
         body.getUseItem().set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
      }

   }

   private void crossbowAttack(final E body, final Entity target) {
      if (this.crossbowState == CrossbowAttack.CrossbowState.UNCHARGED) {
         body.startUsingItem(ProjectileUtil.getWeaponHoldingHand(body, Items.CROSSBOW));
         this.crossbowState = CrossbowAttack.CrossbowState.CHARGING;
         ((CrossbowAttackMob)body).setChargingCrossbow(true);
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.CHARGING) {
         if (!body.isUsingItem()) {
            this.crossbowState = CrossbowAttack.CrossbowState.UNCHARGED;
         }

         int pullTime = body.getTicksUsingItem();
         ItemStack useItem = body.getUseItem();
         if (pullTime >= CrossbowItem.getChargeDuration(useItem, body)) {
            body.releaseUsingItem();
            this.crossbowState = CrossbowAttack.CrossbowState.CHARGED;
            this.attackDelay = 20 + body.getRandom().nextInt(20);
            ((CrossbowAttackMob)body).setChargingCrossbow(false);
         }
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.CHARGED) {
         --this.attackDelay;
         if (this.attackDelay == 0) {
            this.crossbowState = CrossbowAttack.CrossbowState.READY_TO_ATTACK;
         }
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.READY_TO_ATTACK) {
         ((RangedAttackMob)body).performRangedAttack(target, 1.0F);
         this.crossbowState = CrossbowAttack.CrossbowState.UNCHARGED;
      }

   }

   private void lookAtTarget(final Mob body, final Entity target) {
      body.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
   }

   private static Entity getAttackTarget(final LivingEntity body) {
      return (Entity)body.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }

   private static enum CrossbowState {
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
