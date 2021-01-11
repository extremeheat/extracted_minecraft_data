package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item {
   public ItemNameTag() {
      super();
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      if (!var1.func_82837_s()) {
         return false;
      } else if (var3 instanceof EntityLiving) {
         EntityLiving var4 = (EntityLiving)var3;
         var4.func_96094_a(var1.func_82833_r());
         var4.func_110163_bv();
         --var1.field_77994_a;
         return true;
      } else {
         return super.func_111207_a(var1, var2, var3);
      }
   }
}
