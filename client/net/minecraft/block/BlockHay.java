package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockHay extends BlockRotatedPillar {
   public BlockHay() {
      super(Material.field_151577_b, MapColor.field_151673_t);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176298_M, EnumFacing.Axis.Y));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public IBlockState func_176203_a(int var1) {
      EnumFacing.Axis var2 = EnumFacing.Axis.Y;
      int var3 = var1 & 12;
      if (var3 == 4) {
         var2 = EnumFacing.Axis.X;
      } else if (var3 == 8) {
         var2 = EnumFacing.Axis.Z;
      }

      return this.func_176223_P().func_177226_a(field_176298_M, var2);
   }

   public int func_176201_c(IBlockState var1) {
      int var2 = 0;
      EnumFacing.Axis var3 = (EnumFacing.Axis)var1.func_177229_b(field_176298_M);
      if (var3 == EnumFacing.Axis.X) {
         var2 |= 4;
      } else if (var3 == EnumFacing.Axis.Z) {
         var2 |= 8;
      }

      return var2;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176298_M});
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      return new ItemStack(Item.func_150898_a(this), 1, 0);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return super.func_180642_a(var1, var2, var3, var4, var5, var6, var7, var8).func_177226_a(field_176298_M, var3.func_176740_k());
   }
}
