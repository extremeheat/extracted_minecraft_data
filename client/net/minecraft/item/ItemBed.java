package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item {
   public ItemBed() {
      super();
      this.func_77637_a(CreativeTabs.field_78031_c);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var3.field_72995_K) {
         return true;
      } else if (var5 != EnumFacing.UP) {
         return false;
      } else {
         IBlockState var9 = var3.func_180495_p(var4);
         Block var10 = var9.func_177230_c();
         boolean var11 = var10.func_176200_f(var3, var4);
         if (!var11) {
            var4 = var4.func_177984_a();
         }

         int var12 = MathHelper.func_76128_c((double)(var2.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;
         EnumFacing var13 = EnumFacing.func_176731_b(var12);
         BlockPos var14 = var4.func_177972_a(var13);
         if (var2.func_175151_a(var4, var5, var1) && var2.func_175151_a(var14, var5, var1)) {
            boolean var15 = var3.func_180495_p(var14).func_177230_c().func_176200_f(var3, var14);
            boolean var16 = var11 || var3.func_175623_d(var4);
            boolean var17 = var15 || var3.func_175623_d(var14);
            if (var16 && var17 && World.func_175683_a(var3, var4.func_177977_b()) && World.func_175683_a(var3, var14.func_177977_b())) {
               IBlockState var18 = Blocks.field_150324_C.func_176223_P().func_177226_a(BlockBed.field_176471_b, false).func_177226_a(BlockBed.field_176387_N, var13).func_177226_a(BlockBed.field_176472_a, BlockBed.EnumPartType.FOOT);
               if (var3.func_180501_a(var4, var18, 3)) {
                  IBlockState var19 = var18.func_177226_a(BlockBed.field_176472_a, BlockBed.EnumPartType.HEAD);
                  var3.func_180501_a(var14, var19, 3);
               }

               --var1.field_77994_a;
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
}
