package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSaddle extends Item {
   public ItemSaddle() {
      super();
      this.field_77777_bU = 1;
      this.func_77637_a(CreativeTabs.field_78029_e);
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      if (var3 instanceof EntityPig) {
         EntityPig var4 = (EntityPig)var3;
         if (!var4.func_70901_n() && !var4.func_70631_g_()) {
            var4.func_70900_e(true);
            var4.field_70170_p.func_72956_a(var4, "mob.horse.leather", 0.5F, 1.0F);
            --var1.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      this.func_111207_a(var1, (EntityPlayer)null, var2);
      return true;
   }
}
