package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class MaceItem extends Item {
   private static final int DEFAULT_ATTACK_DAMAGE = 5;
   private static final float DEFAULT_ATTACK_SPEED = -3.4F;
   public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
   private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
   public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
   private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;

   public MaceItem(Item.Properties var1) {
      super(var1);
   }

   public static ItemAttributeModifiers createAttributes() {
      return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.4000000953674316, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
   }

   public static Tool createToolProperties() {
      return new Tool(List.of(), 1.0F, 2, false);
   }

   public void hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      if (canSmashAttack(var3)) {
         ServerLevel var4 = (ServerLevel)var3.level();
         var3.setDeltaMovement(var3.getDeltaMovement().with(Direction.Axis.Y, 0.009999999776482582));
         if (var3 instanceof ServerPlayer) {
            ServerPlayer var5 = (ServerPlayer)var3;
            var5.currentImpulseImpactPos = this.calculateImpactPosition(var5);
            var5.setIgnoreFallDamageFromCurrentImpulse(true);
            var5.connection.send(new ClientboundSetEntityMotionPacket(var5));
         }

         if (var2.onGround()) {
            if (var3 instanceof ServerPlayer) {
               ServerPlayer var6 = (ServerPlayer)var3;
               var6.setSpawnExtraParticlesOnFall(true);
            }

            SoundEvent var7 = var3.fallDistance > 5.0 ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
            var4.playSound((Entity)null, var3.getX(), var3.getY(), var3.getZ(), var7, var3.getSoundSource(), 1.0F, 1.0F);
         } else {
            var4.playSound((Entity)null, var3.getX(), var3.getY(), var3.getZ(), SoundEvents.MACE_SMASH_AIR, var3.getSoundSource(), 1.0F, 1.0F);
         }

         knockback(var4, var3, var2);
      }

   }

   private Vec3 calculateImpactPosition(ServerPlayer var1) {
      return var1.isIgnoringFallDamageFromCurrentImpulse() && var1.currentImpulseImpactPos != null && var1.currentImpulseImpactPos.y <= var1.position().y ? var1.currentImpulseImpactPos : var1.position();
   }

   public void postHurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      if (canSmashAttack(var3)) {
         var3.resetFallDistance();
      }

   }

   public float getAttackDamageBonus(Entity var1, float var2, DamageSource var3) {
      Entity var5 = var3.getDirectEntity();
      if (var5 instanceof LivingEntity var4) {
         if (!canSmashAttack(var4)) {
            return 0.0F;
         } else {
            double var15 = 3.0;
            double var7 = 8.0;
            double var9 = var4.fallDistance;
            double var11;
            if (var9 <= 3.0) {
               var11 = 4.0 * var9;
            } else if (var9 <= 8.0) {
               var11 = 12.0 + 2.0 * (var9 - 3.0);
            } else {
               var11 = 22.0 + var9 - 8.0;
            }

            Level var14 = var4.level();
            if (var14 instanceof ServerLevel) {
               ServerLevel var13 = (ServerLevel)var14;
               return (float)(var11 + (double)EnchantmentHelper.modifyFallBasedDamage(var13, var4.getWeaponItem(), var1, var3, 0.0F) * var9);
            } else {
               return (float)var11;
            }
         }
      } else {
         return 0.0F;
      }
   }

   private static void knockback(Level var0, Entity var1, Entity var2) {
      var0.levelEvent(2013, var2.getOnPos(), 750);
      var0.getEntitiesOfClass(LivingEntity.class, var2.getBoundingBox().inflate(3.5), knockbackPredicate(var1, var2)).forEach((var2x) -> {
         Vec3 var3 = var2x.position().subtract(var2.position());
         double var4 = getKnockbackPower(var1, var2x, var3);
         Vec3 var6 = var3.normalize().scale(var4);
         if (var4 > 0.0) {
            var2x.push(var6.x, 0.699999988079071, var6.z);
            if (var2x instanceof ServerPlayer) {
               ServerPlayer var7 = (ServerPlayer)var2x;
               var7.connection.send(new ClientboundSetEntityMotionPacket(var7));
            }
         }

      });
   }

   private static Predicate<LivingEntity> knockbackPredicate(Entity var0, Entity var1) {
      return (var2) -> {
         boolean var3;
         boolean var4;
         boolean var5;
         boolean var10000;
         label82: {
            var3 = !var2.isSpectator();
            var4 = var2 != var0 && var2 != var1;
            var5 = !var0.isAlliedTo((Entity)var2);
            if (var2 instanceof TamableAnimal var8) {
               if (var1 instanceof LivingEntity var7) {
                  if (var8.isTame() && var8.isOwnedBy(var7)) {
                     var10000 = true;
                     break label82;
                  }
               }
            }

            var10000 = false;
         }

         boolean var6;
         label74: {
            var6 = !var10000;
            if (var2 instanceof ArmorStand var12) {
               if (var12.isMarker()) {
                  var10000 = false;
                  break label74;
               }
            }

            var10000 = true;
         }

         boolean var11;
         boolean var13;
         label68: {
            var11 = var10000;
            var13 = var1.distanceToSqr((Entity)var2) <= Math.pow(3.5, 2.0);
            if (var2 instanceof Player var10) {
               if (var10.isCreative() && var10.getAbilities().flying) {
                  var10000 = true;
                  break label68;
               }
            }

            var10000 = false;
         }

         boolean var9 = !var10000;
         return var3 && var4 && var5 && var6 && var11 && var13 && var9;
      };
   }

   private static double getKnockbackPower(Entity var0, LivingEntity var1, Vec3 var2) {
      return (3.5 - var2.length()) * 0.699999988079071 * (double)(var0.fallDistance > 5.0 ? 2 : 1) * (1.0 - var1.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
   }

   public static boolean canSmashAttack(LivingEntity var0) {
      return var0.fallDistance > 1.5 && !var0.isFallFlying();
   }

   public @Nullable DamageSource getItemDamageSource(LivingEntity var1) {
      return canSmashAttack(var1) ? var1.damageSources().mace(var1) : super.getItemDamageSource(var1);
   }
}
