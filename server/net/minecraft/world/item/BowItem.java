package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class BowItem extends ProjectileWeaponItem implements Vanishable {
   public BowItem(Item.Properties var1) {
      super(var1);
   }

   public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      if (var3 instanceof Player) {
         Player var5 = (Player)var3;
         boolean var6 = var5.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, var1) > 0;
         ItemStack var7 = var5.getProjectile(var1);
         if (!var7.isEmpty() || var6) {
            if (var7.isEmpty()) {
               var7 = new ItemStack(Items.ARROW);
            }

            int var8 = this.getUseDuration(var1) - var4;
            float var9 = getPowerForTime(var8);
            if ((double)var9 >= 0.1D) {
               boolean var10 = var6 && var7.getItem() == Items.ARROW;
               if (!var2.isClientSide) {
                  ArrowItem var11 = (ArrowItem)((ArrowItem)(var7.getItem() instanceof ArrowItem ? var7.getItem() : Items.ARROW));
                  AbstractArrow var12 = var11.createArrow(var2, var7, var5);
                  var12.shootFromRotation(var5, var5.xRot, var5.yRot, 0.0F, var9 * 3.0F, 1.0F);
                  if (var9 == 1.0F) {
                     var12.setCritArrow(true);
                  }

                  int var13 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, var1);
                  if (var13 > 0) {
                     var12.setBaseDamage(var12.getBaseDamage() + (double)var13 * 0.5D + 0.5D);
                  }

                  int var14 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, var1);
                  if (var14 > 0) {
                     var12.setKnockback(var14);
                  }

                  if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, var1) > 0) {
                     var12.setSecondsOnFire(100);
                  }

                  var1.hurtAndBreak(1, var5, (var1x) -> {
                     var1x.broadcastBreakEvent(var5.getUsedItemHand());
                  });
                  if (var10 || var5.abilities.instabuild && (var7.getItem() == Items.SPECTRAL_ARROW || var7.getItem() == Items.TIPPED_ARROW)) {
                     var12.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                  }

                  var2.addFreshEntity(var12);
               }

               var2.playSound((Player)null, var5.getX(), var5.getY(), var5.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + var9 * 0.5F);
               if (!var10 && !var5.abilities.instabuild) {
                  var7.shrink(1);
                  if (var7.isEmpty()) {
                     var5.inventory.removeItem(var7);
                  }
               }

               var5.awardStat(Stats.ITEM_USED.get(this));
            }
         }
      }
   }

   public static float getPowerForTime(int var0) {
      float var1 = (float)var0 / 20.0F;
      var1 = (var1 * var1 + var1 * 2.0F) / 3.0F;
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return var1;
   }

   public int getUseDuration(ItemStack var1) {
      return 72000;
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.BOW;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      boolean var5 = !var2.getProjectile(var4).isEmpty();
      if (!var2.abilities.instabuild && !var5) {
         return InteractionResultHolder.fail(var4);
      } else {
         var2.startUsingItem(var3);
         return InteractionResultHolder.consume(var4);
      }
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }

   public int getDefaultProjectileRange() {
      return 15;
   }
}
