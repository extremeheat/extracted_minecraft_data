package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSlime extends BlockBreakable {
   public BlockSlime(Block.Properties var1) {
      super(var1);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      if (var3.func_70093_af()) {
         super.func_180658_a(var1, var2, var3, var4);
      } else {
         var3.func_180430_e(var4, 0.0F);
      }

   }

   public void func_176216_a(IBlockReader var1, Entity var2) {
      if (var2.func_70093_af()) {
         super.func_176216_a(var1, var2);
      } else if (var2.field_70181_x < 0.0D) {
         var2.field_70181_x = -var2.field_70181_x;
         if (!(var2 instanceof EntityLivingBase)) {
            var2.field_70181_x *= 0.8D;
         }
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

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return 0;
   }
}
