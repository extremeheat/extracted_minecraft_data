package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlock {
   public ItemSnow(Block var1) {
      super(var1);
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_77994_a == 0) {
         return false;
      } else if (!var2.func_175151_a(var4, var5, var1)) {
         return false;
      } else {
         IBlockState var9 = var3.func_180495_p(var4);
         Block var10 = var9.func_177230_c();
         BlockPos var11 = var4;
         if ((var5 != EnumFacing.UP || var10 != this.field_150939_a) && !var10.func_176200_f(var3, var4)) {
            var11 = var4.func_177972_a(var5);
            var9 = var3.func_180495_p(var11);
            var10 = var9.func_177230_c();
         }

         if (var10 == this.field_150939_a) {
            int var12 = (Integer)var9.func_177229_b(BlockSnow.field_176315_a);
            if (var12 <= 7) {
               IBlockState var13 = var9.func_177226_a(BlockSnow.field_176315_a, var12 + 1);
               AxisAlignedBB var14 = this.field_150939_a.func_180640_a(var3, var11, var13);
               if (var14 != null && var3.func_72855_b(var14) && var3.func_180501_a(var11, var13, 2)) {
                  var3.func_72908_a((double)((float)var11.func_177958_n() + 0.5F), (double)((float)var11.func_177956_o() + 0.5F), (double)((float)var11.func_177952_p() + 0.5F), this.field_150939_a.field_149762_H.func_150496_b(), (this.field_150939_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150939_a.field_149762_H.func_150494_d() * 0.8F);
                  --var1.field_77994_a;
                  return true;
               }
            }
         }

         return super.func_180614_a(var1, var2, var3, var11, var5, var6, var7, var8);
      }
   }

   public int func_77647_b(int var1) {
      return var1;
   }
}
