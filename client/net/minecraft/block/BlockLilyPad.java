package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
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

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      if (var6 == null || !(var6 instanceof EntityBoat)) {
         super.func_180638_a(var1, var2, var3, var4, var5, var6);
      }

   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return new AxisAlignedBB((double)var2.func_177958_n() + this.field_149759_B, (double)var2.func_177956_o() + this.field_149760_C, (double)var2.func_177952_p() + this.field_149754_D, (double)var2.func_177958_n() + this.field_149755_E, (double)var2.func_177956_o() + this.field_149756_F, (double)var2.func_177952_p() + this.field_149757_G);
   }

   public int func_149635_D() {
      return 7455580;
   }

   public int func_180644_h(IBlockState var1) {
      return 7455580;
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return 2129968;
   }

   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150355_j;
   }

   public boolean func_180671_f(World var1, BlockPos var2, IBlockState var3) {
      if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256) {
         IBlockState var4 = var1.func_180495_p(var2.func_177977_b());
         return var4.func_177230_c().func_149688_o() == Material.field_151586_h && (Integer)var4.func_177229_b(BlockLiquid.field_176367_b) == 0;
      } else {
         return false;
      }
   }

   public int func_176201_c(IBlockState var1) {
      return 0;
   }
}
