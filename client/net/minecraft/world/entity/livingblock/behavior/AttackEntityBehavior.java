package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class AttackEntityBehavior implements LivingBlockBehavior {
   private static final double ATTACK_DISTANCE = 0.5;
   private static final double RETREAT_DISTANCE = 1.5;
   private static final int ATTACK_COOLDOWN_TICKS = 25;
   private static final float BASE_DAMAGE = 1.0F;
   public static final LivingBlockBehaviorType BEHAVIOR = LivingBlockBehaviorType.behaviorType((Function)((a) -> new AttackEntityBehavior(a.withIntentTo(LivingBlock.MOVE_TOWARDS))));
   private final Intent<Target> moveTowards;
   private int lastAttackTick = 0;

   private AttackEntityBehavior(final Intent<Target> moveTowards) {
      super();
      this.moveTowards = moveTowards;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.isAttacking();
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      Targetable attackTarget = entity.getAttackTarget();
      if (attackTarget == null) {
         return false;
      } else if (attackTarget.equals(entity)) {
         return false;
      } else {
         if (attackTarget instanceof Entity) {
            Entity targetEntity = (Entity)attackTarget;
            Vec3 entityPos = entity.position();
            Vec3 targetCenter = targetEntity.position().add(0.0, (double)targetEntity.getBbHeight() * 0.5, 0.0);
            double distanceToTarget = entityPos.distanceTo(targetCenter);
            boolean isReadyToAttack = tickCount - this.lastAttackTick >= 25;
            if (isReadyToAttack) {
               if (distanceToTarget - (double)((targetEntity.getBbWidth() + entity.getBbWidth()) / 2.0F) <= 0.5) {
                  this.performAttack(entity, level, targetEntity);
                  this.lastAttackTick = tickCount;
               }

               this.moveTowards.update(Target.exactlyAt(targetCenter));
            } else {
               Vec3 awayDirection = entityPos.subtract(targetCenter).normalize();
               Vec3 retreatPos = targetCenter.add(awayDirection.scale(1.5));
               this.moveTowards.update(Target.near(retreatPos, 0.1));
            }
         }

         return attackTarget.isAlive();
      }
   }

   private void performAttack(final LivingBlock entity, final ServerLevel level, final Entity target) {
      DamageSource damageSource = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.MOB_ATTACK), entity, entity.getCommander());
      if (entity.takeDamageOnAttack()) {
         entity.hurtServer(level, entity.damageSources().generic(), 0.1F);
      }

      ItemStack item = entity.getItemStack();
      float damage = this.calculateDamage(entity);
      float postEnchantmentDamage = EnchantmentHelper.modifyDamage(level, item, target, damageSource, damage);
      boolean dealtDamage = target.hurtServer(level, damageSource, postEnchantmentDamage);
      if (dealtDamage) {
         float knockback = EnchantmentHelper.modifyKnockback(level, item, target, damageSource, 0.0F);
         Vec3 dir = entity.position().subtract(target.position());
         target.knockback((double)knockback, dir.x, dir.z);
         EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(level, target, damageSource, entity.getItemStack(), (Consumer)null);
      }

   }

   private float calculateDamage(final LivingBlock entity) {
      ItemStack itemStack = entity.getItemStack();
      ItemAttributeModifiers attributeModifiers = (ItemAttributeModifiers)itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
      return (float)attributeModifiers.compute(Attributes.ATTACK_DAMAGE, 1.0, EquipmentSlot.MAINHAND);
   }
}
