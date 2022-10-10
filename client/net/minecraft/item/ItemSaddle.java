package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

public class ItemSaddle extends Item {
   public ItemSaddle(Item.Properties var1) {
      super(var1);
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3, EnumHand var4) {
      if (var3 instanceof EntityPig) {
         EntityPig var5 = (EntityPig)var3;
         if (!var5.func_70901_n() && !var5.func_70631_g_()) {
            var5.func_70900_e(true);
            var5.field_70170_p.func_184148_a(var2, var5.field_70165_t, var5.field_70163_u, var5.field_70161_v, SoundEvents.field_187706_dO, SoundCategory.NEUTRAL, 0.5F, 1.0F);
            var1.func_190918_g(1);
         }

         return true;
      } else {
         return false;
      }
   }
}
