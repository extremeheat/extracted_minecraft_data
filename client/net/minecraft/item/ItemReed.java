package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemReed extends Item {
   private Block field_150935_a;

   public ItemReed(Block var1) {
      super();
      this.field_150935_a = var1;
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      IBlockState var9 = var3.func_180495_p(var4);
      Block var10 = var9.func_177230_c();
      if (var10 == Blocks.field_150431_aC && (Integer)var9.func_177229_b(BlockSnow.field_176315_a) < 1) {
         var5 = EnumFacing.UP;
      } else if (!var10.func_176200_f(var3, var4)) {
         var4 = var4.func_177972_a(var5);
      }

      if (!var2.func_175151_a(var4, var5, var1)) {
         return false;
      } else if (var1.field_77994_a == 0) {
         return false;
      } else {
         if (var3.func_175716_a(this.field_150935_a, var4, false, var5, (Entity)null, var1)) {
            IBlockState var11 = this.field_150935_a.func_180642_a(var3, var4, var5, var6, var7, var8, 0, var2);
            if (var3.func_180501_a(var4, var11, 3)) {
               var11 = var3.func_180495_p(var4);
               if (var11.func_177230_c() == this.field_150935_a) {
                  ItemBlock.func_179224_a(var3, var2, var4, var1);
                  var11.func_177230_c().func_180633_a(var3, var4, var11, var2, var1);
               }

               var3.func_72908_a((double)((float)var4.func_177958_n() + 0.5F), (double)((float)var4.func_177956_o() + 0.5F), (double)((float)var4.func_177952_p() + 0.5F), this.field_150935_a.field_149762_H.func_150496_b(), (this.field_150935_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150935_a.field_149762_H.func_150494_d() * 0.8F);
               --var1.field_77994_a;
               return true;
            }
         }

         return false;
      }
   }
}
