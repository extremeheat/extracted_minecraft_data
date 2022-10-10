package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHay extends BlockRotatedPillar {
   public BlockHay(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176298_M, EnumFacing.Axis.Y));
   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      var3.func_180430_e(var4, 0.2F);
   }
}
