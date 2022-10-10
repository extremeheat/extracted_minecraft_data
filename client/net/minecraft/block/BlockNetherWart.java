package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockNetherWart extends BlockBush {
   public static final IntegerProperty field_176486_a;
   private static final VoxelShape[] field_196399_b;

   protected BlockNetherWart(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176486_a, 0));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196399_b[(Integer)var1.func_177229_b(field_176486_a)];
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1.func_177230_c() == Blocks.field_150425_aM;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      int var5 = (Integer)var1.func_177229_b(field_176486_a);
      if (var5 < 3 && var4.nextInt(10) == 0) {
         var1 = (IBlockState)var1.func_206870_a(field_176486_a, var5 + 1);
         var2.func_180501_a(var3, var1, 2);
      }

      super.func_196267_b(var1, var2, var3, var4);
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      if (!var2.field_72995_K) {
         int var6 = 1;
         if ((Integer)var1.func_177229_b(field_176486_a) >= 3) {
            var6 = 2 + var2.field_73012_v.nextInt(3);
            if (var5 > 0) {
               var6 += var2.field_73012_v.nextInt(var5 + 1);
            }
         }

         for(int var7 = 0; var7 < var6; ++var7) {
            func_180635_a(var2, var3, new ItemStack(Items.field_151075_bm));
         }

      }
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(Items.field_151075_bm);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176486_a);
   }

   static {
      field_176486_a = BlockStateProperties.field_208168_U;
      field_196399_b = new VoxelShape[]{Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)};
   }
}
