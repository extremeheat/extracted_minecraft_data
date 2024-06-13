package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
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
   public int getUseDuration(ItemStack var1) {
      return 72000;
   }

   @Override
   public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      if (var3 instanceof Player var5) {
         int var6 = this.getUseDuration(var1) - var4;
         if (var6 >= 10) {
            int var7 = EnchantmentHelper.getRiptide(var1);
            if (var7 <= 0 || var5.isInWaterOrRain()) {
               if (!var2.isClientSide) {
                  var1.hurtAndBreak(1, var5, LivingEntity.getSlotForHand(var3.getUsedItemHand()));
                  if (var7 == 0) {
                     ThrownTrident var8 = new ThrownTrident(var2, var5, var1);
                     var8.shootFromRotation(var5, var5.getXRot(), var5.getYRot(), 0.0F, 2.5F + (float)var7 * 0.5F, 1.0F);
                     if (var5.hasInfiniteMaterials()) {
                        var8.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                     }

                     var2.addFreshEntity(var8);
                     var2.playSound(null, var8, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                     if (!var5.hasInfiniteMaterials()) {
                        var5.getInventory().removeItem(var1);
                     }
                  }
               }

               var5.awardStat(Stats.ITEM_USED.get(this));
               if (var7 > 0) {
                  float var16 = var5.getYRot();
                  float var9 = var5.getXRot();
                  float var10 = -Mth.sin(var16 * 0.017453292F) * Mth.cos(var9 * 0.017453292F);
                  float var11 = -Mth.sin(var9 * 0.017453292F);
                  float var12 = Mth.cos(var16 * 0.017453292F) * Mth.cos(var9 * 0.017453292F);
                  float var13 = Mth.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
                  float var14 = 3.0F * ((1.0F + (float)var7) / 4.0F);
                  var10 *= var14 / var13;
                  var11 *= var14 / var13;
                  var12 *= var14 / var13;
                  var5.push((double)var10, (double)var11, (double)var12);
                  var5.startAutoSpinAttack(20);
                  if (var5.onGround()) {
                     float var15 = 1.1999999F;
                     var5.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
                  }

                  SoundEvent var20;
                  if (var7 >= 3) {
                     var20 = SoundEvents.TRIDENT_RIPTIDE_3;
                  } else if (var7 == 2) {
                     var20 = SoundEvents.TRIDENT_RIPTIDE_2;
                  } else {
                     var20 = SoundEvents.TRIDENT_RIPTIDE_1;
                  }

                  var2.playSound(null, var5, var20, SoundSource.PLAYERS, 1.0F, 1.0F);
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
      } else if (EnchantmentHelper.getRiptide(var4) > 0 && !var2.isInWaterOrRain()) {
         return InteractionResultHolder.fail(var4);
      } else {
         var2.startUsingItem(var3);
         return InteractionResultHolder.consume(var4);
      }
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
      return true;
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
