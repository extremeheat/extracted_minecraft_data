package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TridentItem extends Item implements ProjectileItem {
   public static final int THROW_THRESHOLD_TIME = 10;
   public static final float BASE_DAMAGE = 8.0F;
   public static final float PROJECTILE_SHOOT_POWER = 2.5F;

   public TridentItem(final Item.Properties properties) {
      super(properties);
   }

   public static ItemAttributeModifiers createAttributes() {
      return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.9000000953674316, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
   }

   public static Tool createToolProperties() {
      return new Tool(List.of(), 1.0F, 2, false);
   }

   public ItemUseAnimation getUseAnimation(final ItemStack itemStack) {
      return ItemUseAnimation.TRIDENT;
   }

   public int getUseDuration(final ItemStack itemStack, final LivingEntity user) {
      return 72000;
   }

   public boolean releaseUsing(final ItemStack itemStack, final Level level, final LivingEntity entity, final int remainingTime) {
      if (entity instanceof Player player) {
         int timeHeld = this.getUseDuration(itemStack, entity) - remainingTime;
         if (timeHeld < 10) {
            return false;
         } else {
            float riptideStrength = EnchantmentHelper.getTridentSpinAttackStrength(itemStack, player);
            if (riptideStrength > 0.0F && !player.isInWaterOrRain()) {
               return false;
            } else if (itemStack.nextDamageWillBreak()) {
               return false;
            } else {
               Holder<SoundEvent> sound = (Holder)EnchantmentHelper.pickHighestLevel(itemStack, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
               player.awardStat(Stats.ITEM_USED.get(this));
               if (level instanceof ServerLevel) {
                  ServerLevel serverLevel = (ServerLevel)level;
                  itemStack.hurtWithoutBreaking(1, player);
                  if (riptideStrength == 0.0F) {
                     ItemStack thrownItemStack = itemStack.consumeAndReturn(1, player);
                     ThrownTrident trident = (ThrownTrident)Projectile.spawnProjectileFromRotation(ThrownTrident::new, serverLevel, thrownItemStack, player, 0.0F, 2.5F, 1.0F);
                     if (player.hasInfiniteMaterials()) {
                        trident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                     }

                     level.playSound((Entity)null, (Entity)trident, sound.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                     return true;
                  }
               }

               if (riptideStrength > 0.0F) {
                  float yRot = player.getYRot();
                  float xRot = player.getXRot();
                  float xd = -Mth.sin((double)(yRot * 0.017453292F)) * Mth.cos((double)(xRot * 0.017453292F));
                  float yd = -Mth.sin((double)(xRot * 0.017453292F));
                  float zd = Mth.cos((double)(yRot * 0.017453292F)) * Mth.cos((double)(xRot * 0.017453292F));
                  float dist = Mth.sqrt(xd * xd + yd * yd + zd * zd);
                  xd *= riptideStrength / dist;
                  yd *= riptideStrength / dist;
                  zd *= riptideStrength / dist;
                  player.push((double)xd, (double)yd, (double)zd);
                  player.startAutoSpinAttack(20, 8.0F, itemStack);
                  if (player.onGround()) {
                     float heightDifference = 1.1999999F;
                     player.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
                  }

                  level.playSound((Entity)null, (Entity)player, sound.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                  return true;
               } else {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
      ItemStack itemInHand = player.getItemInHand(hand);
      if (itemInHand.nextDamageWillBreak()) {
         return InteractionResult.FAIL;
      } else if (EnchantmentHelper.getTridentSpinAttackStrength(itemInHand, player) > 0.0F && !player.isInWaterOrRain()) {
         return InteractionResult.FAIL;
      } else {
         player.startUsingItem(hand);
         return InteractionResult.CONSUME;
      }
   }

   public Projectile asProjectile(final Level level, final Position position, final ItemStack itemStack, final Direction direction) {
      ThrownTrident trident = new ThrownTrident(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1));
      trident.pickup = AbstractArrow.Pickup.ALLOWED;
      return trident;
   }
}
