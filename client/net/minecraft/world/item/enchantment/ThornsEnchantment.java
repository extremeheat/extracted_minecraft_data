package net.minecraft.world.item.enchantment;

import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ThornsEnchantment extends Enchantment {
   private static final float CHANCE_PER_LEVEL = 0.15F;

   public ThornsEnchantment(Enchantment.Rarity var1, EquipmentSlot... var2) {
      super(var1, EnchantmentCategory.ARMOR_CHEST, var2);
   }

   public int getMinCost(int var1) {
      return 10 + 20 * (var1 - 1);
   }

   public int getMaxCost(int var1) {
      return super.getMinCost(var1) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canEnchant(ItemStack var1) {
      return var1.getItem() instanceof ArmorItem ? true : super.canEnchant(var1);
   }

   public void doPostHurt(LivingEntity var1, Entity var2, int var3) {
      Random var4 = var1.getRandom();
      Entry var5 = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, var1);
      if (shouldHit(var3, var4)) {
         if (var2 != null) {
            var2.hurt(DamageSource.thorns(var1), (float)getDamage(var3, var4));
         }

         if (var5 != null) {
            ((ItemStack)var5.getValue()).hurtAndBreak(2, var1, (var1x) -> {
               var1x.broadcastBreakEvent((EquipmentSlot)var5.getKey());
            });
         }
      }

   }

   public static boolean shouldHit(int var0, Random var1) {
      if (var0 <= 0) {
         return false;
      } else {
         return var1.nextFloat() < 0.15F * (float)var0;
      }
   }

   public static int getDamage(int var0, Random var1) {
      return var0 > 10 ? var0 - 10 : 1 + var1.nextInt(4);
   }
}
