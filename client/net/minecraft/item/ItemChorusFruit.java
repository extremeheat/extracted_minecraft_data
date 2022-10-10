package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemChorusFruit extends ItemFood {
   public ItemChorusFruit(int var1, float var2, Item.Properties var3) {
      super(var1, var2, false, var3);
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityLivingBase var3) {
      ItemStack var4 = super.func_77654_b(var1, var2, var3);
      if (!var2.field_72995_K) {
         double var5 = var3.field_70165_t;
         double var7 = var3.field_70163_u;
         double var9 = var3.field_70161_v;

         for(int var11 = 0; var11 < 16; ++var11) {
            double var12 = var3.field_70165_t + (var3.func_70681_au().nextDouble() - 0.5D) * 16.0D;
            double var14 = MathHelper.func_151237_a(var3.field_70163_u + (double)(var3.func_70681_au().nextInt(16) - 8), 0.0D, (double)(var2.func_72940_L() - 1));
            double var16 = var3.field_70161_v + (var3.func_70681_au().nextDouble() - 0.5D) * 16.0D;
            if (var3.func_184218_aH()) {
               var3.func_184210_p();
            }

            if (var3.func_184595_k(var12, var14, var16)) {
               var2.func_184148_a((EntityPlayer)null, var5, var7, var9, SoundEvents.field_187544_ad, SoundCategory.PLAYERS, 1.0F, 1.0F);
               var3.func_184185_a(SoundEvents.field_187544_ad, 1.0F, 1.0F);
               break;
            }
         }

         if (var3 instanceof EntityPlayer) {
            ((EntityPlayer)var3).func_184811_cZ().func_185145_a(this, 20);
         }
      }

      return var4;
   }
}
