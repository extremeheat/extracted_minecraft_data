package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TridentItem extends Item implements ProjectileItem {
   public static final int THROW_THRESHOLD_TIME = 10;
   public static final float BASE_DAMAGE = 8.0F;
   public static final float SHOOT_POWER = 2.5F;

   public TridentItem(Item.Properties var1) {
      super(var1);
   }

   public static ItemAttributeModifiers createAttributes() {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9000000953674316, AttributeModifier.Operation.ADD_VALUE),
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
   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.SPEAR;
   }

   @Override
   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 72000;
   }

   @Override
   public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      if (var3 instanceof Player var5) {
         int var6 = this.getUseDuration(var1, var3) - var4;
         if (var6 >= 10) {
            float var7 = var2 instanceof ServerLevel var8 ? EnchantmentHelper.getTridentSpinAttackStrength(var8, var1, var5) : 0.0F;
            if (!(var7 > 0.0F) || var5.isInWaterOrRain()) {
               Holder var16 = EnchantmentHelper.pickHighestLevel(var1, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
               if (!var2.isClientSide) {
                  var1.hurtAndBreak(1, var5, LivingEntity.getSlotForHand(var3.getUsedItemHand()));
                  if (var7 == 0.0F) {
                     ThrownTrident var9 = new ThrownTrident(var2, var5, var1);
                     var9.shootFromRotation(var5, var5.getXRot(), var5.getYRot(), 0.0F, 2.5F, 1.0F);
                     if (var5.hasInfiniteMaterials()) {
                        var9.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                     }

                     var2.addFreshEntity(var9);
                     var2.playSound(null, var9, (SoundEvent)var16.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                     if (!var5.hasInfiniteMaterials()) {
                        var5.getInventory().removeItem(var1);
                     }
                  }
               }

               var5.awardStat(Stats.ITEM_USED.get(this));
               if (var7 > 0.0F) {
                  float var17 = var5.getYRot();
                  float var10 = var5.getXRot();
                  float var11 = -Mth.sin(var17 * 0.017453292F) * Mth.cos(var10 * 0.017453292F);
                  float var12 = -Mth.sin(var10 * 0.017453292F);
                  float var13 = Mth.cos(var17 * 0.017453292F) * Mth.cos(var10 * 0.017453292F);
                  float var14 = Mth.sqrt(var11 * var11 + var12 * var12 + var13 * var13);
                  var11 *= var7 / var14;
                  var12 *= var7 / var14;
                  var13 *= var7 / var14;
                  var5.push((double)var11, (double)var12, (double)var13);
                  var5.startAutoSpinAttack(20, 8.0F, var1);
                  if (var5.onGround()) {
                     float var15 = 1.1999999F;
                     var5.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
                  }

                  var2.playSound(null, var5, (SoundEvent)var16.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
               }
            }
         }
      }
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (var4.getDamageValue() >= var4.getMaxDamage() - 1) {
         return InteractionResultHolder.fail(var4);
      } else {
         if (var1 instanceof ServerLevel var5 && EnchantmentHelper.getTridentSpinAttackStrength(var5, var4, var2) > 0.0F && !var2.isInWaterOrRain()) {
            return InteractionResultHolder.fail(var4);
         }

         var2.startUsingItem(var3);
         return InteractionResultHolder.consume(var4);
      }
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      return true;
   }

   @Override
   public void postHurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
   }

   @Override
   public int getEnchantmentValue() {
      return 1;
   }

   @Override
   public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4) {
      ThrownTrident var5 = new ThrownTrident(var1, var2.x(), var2.y(), var2.z(), var3.copyWithCount(1));
      var5.pickup = AbstractArrow.Pickup.ALLOWED;
      return var5;
   }
}
