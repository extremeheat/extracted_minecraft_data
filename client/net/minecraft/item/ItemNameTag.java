package net.minecraft.item;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class ItemNameTag extends Item {
   public ItemNameTag(Item.Properties var1) {
      super(var1);
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3, EnumHand var4) {
      if (var1.func_82837_s() && !(var3 instanceof EntityPlayer)) {
         var3.func_200203_b(var1.func_200301_q());
         if (var3 instanceof EntityLiving) {
            ((EntityLiving)var3).func_110163_bv();
         }

         var1.func_190918_g(1);
         return true;
      } else {
         return false;
      }
   }
}
