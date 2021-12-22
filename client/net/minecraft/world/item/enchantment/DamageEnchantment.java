package net.minecraft.world.item.enchantment;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

public class DamageEnchantment extends Enchantment {
   public static final int ALL = 0;
   public static final int UNDEAD = 1;
   public static final int ARTHROPODS = 2;
   private static final String[] NAMES = new String[]{"all", "undead", "arthropods"};
   private static final int[] MIN_COST = new int[]{1, 5, 5};
   private static final int[] LEVEL_COST = new int[]{11, 8, 8};
   private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20};
   public final int type;

   public DamageEnchantment(Enchantment.Rarity var1, int var2, EquipmentSlot... var3) {
      super(var1, EnchantmentCategory.WEAPON, var3);
      this.type = var2;
   }

   public int getMinCost(int var1) {
      return MIN_COST[this.type] + (var1 - 1) * LEVEL_COST[this.type];
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + LEVEL_COST_SPAN[this.type];
   }

   public int getMaxLevel() {
      return 5;
   }

   public float getDamageBonus(int var1, MobType var2) {
      if (this.type == 0) {
         return 1.0F + (float)Math.max(0, var1 - 1) * 0.5F;
      } else if (this.type == 1 && var2 == MobType.UNDEAD) {
         return (float)var1 * 2.5F;
      } else {
         return this.type == 2 && var2 == MobType.ARTHROPOD ? (float)var1 * 2.5F : 0.0F;
      }
   }

   public boolean checkCompatibility(Enchantment var1) {
      return !(var1 instanceof DamageEnchantment);
   }

   public boolean canEnchant(ItemStack var1) {
      return var1.getItem() instanceof AxeItem ? true : super.canEnchant(var1);
   }

   public void doPostAttack(LivingEntity var1, Entity var2, int var3) {
      if (var2 instanceof LivingEntity) {
         LivingEntity var4 = (LivingEntity)var2;
         if (this.type == 2 && var3 > 0 && var4.getMobType() == MobType.ARTHROPOD) {
            int var5 = 20 + var1.getRandom().nextInt(10 * var3);
            var4.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, var5, 3));
         }
      }

   }
}
