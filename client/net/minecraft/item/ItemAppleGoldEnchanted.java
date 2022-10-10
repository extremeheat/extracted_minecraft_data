package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemAppleGoldEnchanted extends ItemFood {
   public ItemAppleGoldEnchanted(int var1, float var2, boolean var3, Item.Properties var4) {
      super(var1, var2, var3, var4);
   }

   public boolean func_77636_d(ItemStack var1) {
      return true;
   }

   protected void func_77849_c(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var2.field_72995_K) {
         var3.func_195064_c(new PotionEffect(MobEffects.field_76428_l, 400, 1));
         var3.func_195064_c(new PotionEffect(MobEffects.field_76429_m, 6000, 0));
         var3.func_195064_c(new PotionEffect(MobEffects.field_76426_n, 6000, 0));
         var3.func_195064_c(new PotionEffect(MobEffects.field_76444_x, 2400, 3));
      }

   }
}
