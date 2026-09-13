package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.command.IEntitySelector$ArmoredMob;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;

final class ItemArmor$1 extends BehaviorDefaultDispenseItem {
   ItemArmor$1() {
      super();
   }

   @Override
   protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
      EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
      int var4 = var1.func_82623_d() + var3.func_82601_c();
      int var5 = var1.func_82622_e() + var3.func_96559_d();
      int var6 = var1.func_82621_f() + var3.func_82599_e();
      AxisAlignedBB var7 = AxisAlignedBB.func_72330_a((double)var4, (double)var5, (double)var6, (double)(var4 + 1), (double)(var5 + 1), (double)(var6 + 1));
      List var8 = var1.func_82618_k().func_82733_a(EntityLivingBase.class, var7, new IEntitySelector$ArmoredMob(var2));
      if (var8.size() > 0) {
         EntityLivingBase var9 = (EntityLivingBase)var8.get(0);
         int var10 = var9 instanceof EntityPlayer ? 1 : 0;
         int var11 = EntityLiving.func_82159_b(var2);
         ItemStack var12 = var2.func_77946_l();
         var12.field_77994_a = 1;
         var9.func_70062_b(var11 - var10, var12);
         if (var9 instanceof EntityLiving) {
            ((EntityLiving)var9).func_96120_a(var11, 2.0F);
         }

         --var2.field_77994_a;
         return var2;
      } else {
         return super.func_82487_b(var1, var2);
      }
   }
}
