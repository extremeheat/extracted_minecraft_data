package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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
      return ItemAttributeModifiers.builder()
         .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.4000000953674316, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .build();
   }

   public static Tool createToolProperties() {
      return new Tool(List.of(), 1.0F, 2);
   }

   @Override
   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return !var4.isCreative();
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      if (canSmashAttack(var3)) {
         ServerLevel var4 = (ServerLevel)var3.level();
         var3.setDeltaMovement(var3.getDeltaMovement().with(Direction.Axis.Y, 0.009999999776482582));
         if (var3 instanceof ServerPlayer var5) {
            var5.currentImpulseImpactPos = this.calculateImpactPosition(var5);
            var5.setIgnoreFallDamageFromCurrentImpulse(true);
            var5.connection.send(new ClientboundSetEntityMotionPacket(var5));
         }

         if (var2.onGround()) {
            if (var3 instanceof ServerPlayer var6) {
               var6.setSpawnExtraParticlesOnFall(true);
            }

            SoundEvent var7 = var3.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
            var4.playSound(null, var3.getX(), var3.getY(), var3.getZ(), var7, var3.getSoundSource(), 1.0F, 1.0F);
         } else {
            var4.playSound(null, var3.getX(), var3.getY(), var3.getZ(), SoundEvents.MACE_SMASH_AIR, var3.getSoundSource(), 1.0F, 1.0F);
         }

         knockback(var4, var3, var2);
      }

      return true;
   }

   private Vec3 calculateImpactPosition(ServerPlayer var1) {
      return var1.isIgnoringFallDamageFromCurrentImpulse() && var1.currentImpulseImpactPos != null && var1.currentImpulseImpactPos.y <= var1.position().y
         ? var1.currentImpulseImpactPos
         : var1.position();
   }

   @Override
   public void postHurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
      if (canSmashAttack(var3)) {
         var3.resetFallDistance();
      }
   }

   @Override
   public float getAttackDamageBonus(Entity var1, float var2, DamageSource var3) {
      if (var3.getDirectEntity() instanceof LivingEntity var4) {
         if (!canSmashAttack(var4)) {
            return 0.0F;
         } else {
            float var11 = 3.0F;
            float var6 = 8.0F;
            float var7 = var4.fallDistance;
            float var8;
            if (var7 <= 3.0F) {
               var8 = 4.0F * var7;
            } else if (var7 <= 8.0F) {
               var8 = 12.0F + 2.0F * (var7 - 3.0F);
            } else {
               var8 = 22.0F + var7 - 8.0F;
            }

            return var4.level() instanceof ServerLevel var9
               ? var8 + EnchantmentHelper.modifyFallBasedDamage(var9, var4.getWeaponItem(), var1, var3, 0.0F) * var7
               : var8;
         }
      } else {
         return 0.0F;
      }
   }

   private static void knockback(Level var0, Entity var1, Entity var2) {
      var0.levelEvent(2013, var2.getOnPos(), 750);
      var0.getEntitiesOfClass(LivingEntity.class, var2.getBoundingBox().inflate(3.5), knockbackPredicate(var1, var2)).forEach(var2x -> {
         Vec3 var3 = var2x.position().subtract(var2.position());
         double var4 = getKnockbackPower(var1, var2x, var3);
         Vec3 var6 = var3.normalize().scale(var4);
         if (var4 > 0.0) {
            var2x.push(var6.x, 0.699999988079071, var6.z);
            if (var2x instanceof ServerPlayer var7) {
               var7.connection.send(new ClientboundSetEntityMotionPacket(var7));
            }
         }
      });
   }

   private static Predicate<LivingEntity> knockbackPredicate(Entity var0, Entity var1) {
      return var2 -> {
         boolean var3;
         boolean var4;
         boolean var5;
         boolean var10000;
         label62: {
            var3 = !var2.isSpectator();
            var4 = var2 != var0 && var2 != var1;
            var5 = !var0.isAlliedTo(var2);
            if (var2 instanceof TamableAnimal var7 && var7.isTame() && var0.getUUID().equals(var7.getOwnerUUID())) {
               var10000 = true;
               break label62;
            }

            var10000 = false;
         }

         boolean var6;
         label55: {
            var6 = !var10000;
            if (var2 instanceof ArmorStand var8 && var8.isMarker()) {
               var10000 = false;
               break label55;
            }

            var10000 = true;
         }

         boolean var9 = var10000;
         boolean var10 = var1.distanceToSqr(var2) <= Math.pow(3.5, 2.0);
         return var3 && var4 && var5 && var6 && var9 && var10;
      };
   }

   private static double getKnockbackPower(Entity var0, LivingEntity var1, Vec3 var2) {
      return (3.5 - var2.length())
         * 0.699999988079071
         * (double)(var0.fallDistance > 5.0F ? 2 : 1)
         * (1.0 - var1.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
   }

   public static boolean canSmashAttack(LivingEntity var0) {
      return var0.fallDistance > 1.5F && !var0.isFallFlying();
   }
}
