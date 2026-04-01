package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.phys.Vec3;

public class FireAtEntityBehavior implements LivingBlockBehavior {
   private static final float ATTACK_DISTANCE = 15.0F;
   private static final double RETREAT_DISTANCE = 10.0;
   private static final int ATTACK_COOLDOWN_TICKS = 40;
   private static final float BASE_DAMAGE = 1.0F;
   public static final LivingBlockBehaviorType BEHAVIOR = LivingBlockBehaviorType.behaviorType((Function)((a) -> new FireAtEntityBehavior(a.withIntentTo(LivingBlock.MOVE_TOWARDS))));
   private final Intent<Target> moveTowards;
   private int lastAttackTick = 0;

   private FireAtEntityBehavior(final Intent<Target> moveTowards) {
      super();
      this.moveTowards = moveTowards;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.isAttacking() && !entity.getItemStack().isEmpty();
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      Targetable attackTarget = entity.getAttackTarget();
      if (attackTarget == null) {
         return false;
      } else {
         if (attackTarget instanceof Entity) {
            Entity targetEntity = (Entity)attackTarget;
            Vec3 entityPos = entity.position();
            Vec3 targetCenter = targetEntity.position().add(0.0, (double)targetEntity.getBbHeight() * 0.5, 0.0);
            double distanceToTarget = entityPos.distanceTo(targetCenter);
            boolean isReadyToAttack = tickCount - this.lastAttackTick >= 40;
            if (isReadyToAttack) {
               if (distanceToTarget <= 15.0) {
                  this.fire(entity, level, targetEntity);
                  this.lastAttackTick = tickCount;
               }
            } else {
               Vec3 awayDirection = entityPos.subtract(targetCenter).normalize();
               Vec3 retreatPos = targetCenter.add(awayDirection.scale(11.0));
               this.moveTowards.update(Target.near(retreatPos, 1.0));
            }
         }

         return attackTarget.isAlive();
      }
   }

   private void fire(final LivingBlock entity, final ServerLevel level, final Entity target) {
      ItemStack bowItem = entity.getItemStack();
      ChargedProjectiles chargedProjectiles = (ChargedProjectiles)bowItem.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
      if (!chargedProjectiles.isEmpty()) {
         List<ItemStack> itemStacks = chargedProjectiles.itemCopies();
         ItemStack projectile = (ItemStack)itemStacks.getFirst();
         AbstractArrow arrow = new Arrow(level, entity.getX(), entity.getEyeY(), entity.getZ(), projectile, bowItem);
         double xd = target.getX() - entity.getX();
         double yd = target.getY(0.3333333333333333) - arrow.getY();
         double zd = target.getZ() - entity.getZ();
         double distanceToTarget = Math.sqrt(xd * xd + zd * zd);
         Projectile.spawnProjectileUsingShoot(arrow, level, projectile, xd, yd + distanceToTarget * 0.20000000298023224, zd, 1.6F, (float)(14 - level.getDifficulty().getId() * 4));
         arrow.setLife(1100);
         entity.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
         bowItem.remove(DataComponents.CHARGED_PROJECTILES);
         if (entity.takeDamageOnAttack()) {
            entity.hurtServer(level, entity.damageSources().generic(), 0.1F);
         }

      }
   }
}
