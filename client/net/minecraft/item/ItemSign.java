package net.minecraft.item;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSign extends Item {
   public ItemSign() {
      super();
      this.field_77777_bU = 16;
      this.func_77637_a(CreativeTabs.field_78031_c);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      } else if (!var3.func_180495_p(var4).func_177230_c().func_149688_o().func_76220_a()) {
         return false;
      } else {
         var4 = var4.func_177972_a(var5);
         if (!var2.func_175151_a(var4, var5, var1)) {
            return false;
         } else if (!Blocks.field_150472_an.func_176196_c(var3, var4)) {
            return false;
         } else if (var3.field_72995_K) {
            return true;
         } else {
            if (var5 == EnumFacing.UP) {
               int var9 = MathHelper.func_76128_c((double)((var2.field_70177_z + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
               var3.func_180501_a(var4, Blocks.field_150472_an.func_176223_P().func_177226_a(BlockStandingSign.field_176413_a, var9), 3);
            } else {
               var3.func_180501_a(var4, Blocks.field_150444_as.func_176223_P().func_177226_a(BlockWallSign.field_176412_a, var5), 3);
            }

            --var1.field_77994_a;
            TileEntity var10 = var3.func_175625_s(var4);
            if (var10 instanceof TileEntitySign && !ItemBlock.func_179224_a(var3, var2, var4, var1)) {
               var2.func_175141_a((TileEntitySign)var10);
            }

            return true;
         }
      }
   }
}
