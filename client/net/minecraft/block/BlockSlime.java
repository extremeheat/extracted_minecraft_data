package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockSlime extends BlockBreakable {
   public BlockSlime() {
      super(Material.field_151571_B, false, MapColor.field_151661_c);
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.field_149765_K = 0.8F;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      if (var3.func_70093_af()) {
         super.func_180658_a(var1, var2, var3, var4);
      } else {
         var3.func_180430_e(var4, 0.0F);
      }

   }

   public void func_176216_a(World var1, Entity var2) {
      if (var2.func_70093_af()) {
         super.func_176216_a(var1, var2);
      } else if (var2.field_70181_x < 0.0D) {
         var2.field_70181_x = -var2.field_70181_x;
      }

   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
      if (Math.abs(var3.field_70181_x) < 0.1D && !var3.func_70093_af()) {
         double var4 = 0.4D + Math.abs(var3.field_70181_x) * 0.2D;
         var3.field_70159_w *= var4;
         var3.field_70179_y *= var4;
      }

      super.func_176199_a(var1, var2, var3);
   }
}
