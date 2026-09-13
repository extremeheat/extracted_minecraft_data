package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLilyPad extends BlockBush {
   protected BlockLilyPad() {
      super();
      float var1 = 0.5F;
      float var2 = 0.015625F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var2, 0.5F + var1);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public int func_149645_b() {
      return 23;
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      if (var7 == null || !(var7 instanceof EntityBoat)) {
         super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return AxisAlignedBB.func_72330_a(
         (double)var2 + this.field_149759_B,
         (double)var3 + this.field_149760_C,
         (double)var4 + this.field_149754_D,
         (double)var2 + this.field_149755_E,
         (double)var3 + this.field_149756_F,
         (double)var4 + this.field_149757_G
      );
   }

   @Override
   public int func_149635_D() {
      return 2129968;
   }

   @Override
   public int func_149741_i(int var1) {
      return 2129968;
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      return 2129968;
   }

   @Override
   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150355_j;
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var3 < 256) {
         return var1.func_147439_a(var2, var3 - 1, var4).func_149688_o() == Material.field_151586_h && var1.func_72805_g(var2, var3 - 1, var4) == 0;
      } else {
         return false;
      }
   }
}
