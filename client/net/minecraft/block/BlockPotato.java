package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockPotato extends BlockCrops {
   private static final VoxelShape[] field_196396_a = new VoxelShape[]{Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)};

   public BlockPotato(Block.Properties var1) {
      super(var1);
   }

   protected IItemProvider func_199772_f() {
      return Items.field_151174_bG;
   }

   protected IItemProvider func_199773_g() {
      return Items.field_151174_bG;
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      super.func_196255_a(var1, var2, var3, var4, var5);
      if (!var2.field_72995_K) {
         if (this.func_185525_y(var1) && var2.field_73012_v.nextInt(50) == 0) {
            func_180635_a(var2, var3, new ItemStack(Items.field_151170_bI));
         }

      }
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196396_a[(Integer)var1.func_177229_b(this.func_185524_e())];
   }
}
