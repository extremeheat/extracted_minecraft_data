package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemAppleGold extends ItemFood {
   public ItemAppleGold(int var1, float var2, boolean var3) {
      super(var1, var2, var3);
      this.func_77627_a(true);
   }

   public boolean func_77636_d(ItemStack var1) {
      return var1.func_77960_j() > 0;
   }

   public EnumRarity func_77613_e(ItemStack var1) {
      return var1.func_77960_j() == 0 ? EnumRarity.RARE : EnumRarity.EPIC;
   }

   protected void func_77849_c(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var2.field_72995_K) {
         var3.func_70690_d(new PotionEffect(Potion.field_76444_x.field_76415_H, 2400, 0));
      }

      if (var1.func_77960_j() > 0) {
         if (!var2.field_72995_K) {
            var3.func_70690_d(new PotionEffect(Potion.field_76428_l.field_76415_H, 600, 4));
            var3.func_70690_d(new PotionEffect(Potion.field_76429_m.field_76415_H, 6000, 0));
            var3.func_70690_d(new PotionEffect(Potion.field_76426_n.field_76415_H, 6000, 0));
         }
      } else {
         super.func_77849_c(var1, var2, var3);
      }

   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }
}
