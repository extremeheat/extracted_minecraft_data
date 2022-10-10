package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockStainedGlass extends BlockBreakable {
   private final EnumDyeColor field_196458_a;

   public BlockStainedGlass(EnumDyeColor var1, Block.Properties var2) {
      super(var2);
      this.field_196458_a = var1;
   }

   public boolean func_200123_i(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return true;
   }

   public EnumDyeColor func_196457_d() {
      return this.field_196458_a;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   protected boolean func_149700_E() {
      return true;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         if (!var2.field_72995_K) {
            BlockBeacon.func_176450_d(var2, var3);
         }

      }
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         if (!var2.field_72995_K) {
            BlockBeacon.func_176450_d(var2, var3);
         }

      }
   }
}
