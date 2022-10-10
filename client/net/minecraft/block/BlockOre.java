package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockOre extends Block {
   public BlockOre(Block.Properties var1) {
      super(var1);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      if (this == Blocks.field_150365_q) {
         return Items.field_151044_h;
      } else if (this == Blocks.field_150482_ag) {
         return Items.field_151045_i;
      } else if (this == Blocks.field_150369_x) {
         return Items.field_196128_bn;
      } else if (this == Blocks.field_150412_bA) {
         return Items.field_151166_bC;
      } else {
         return (IItemProvider)(this == Blocks.field_196766_fg ? Items.field_151128_bU : this);
      }
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return this == Blocks.field_150369_x ? 4 + var2.nextInt(5) : 1;
   }

   public int func_196251_a(IBlockState var1, int var2, World var3, BlockPos var4, Random var5) {
      if (var2 > 0 && this != this.func_199769_a((IBlockState)this.func_176194_O().func_177619_a().iterator().next(), var3, var4, var2)) {
         int var6 = var5.nextInt(var2 + 2) - 1;
         if (var6 < 0) {
            var6 = 0;
         }

         return this.func_196264_a(var1, var5) * (var6 + 1);
      } else {
         return this.func_196264_a(var1, var5);
      }
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      super.func_196255_a(var1, var2, var3, var4, var5);
      if (this.func_199769_a(var1, var2, var3, var5) != this) {
         int var6 = 0;
         if (this == Blocks.field_150365_q) {
            var6 = MathHelper.func_76136_a(var2.field_73012_v, 0, 2);
         } else if (this == Blocks.field_150482_ag) {
            var6 = MathHelper.func_76136_a(var2.field_73012_v, 3, 7);
         } else if (this == Blocks.field_150412_bA) {
            var6 = MathHelper.func_76136_a(var2.field_73012_v, 3, 7);
         } else if (this == Blocks.field_150369_x) {
            var6 = MathHelper.func_76136_a(var2.field_73012_v, 2, 5);
         } else if (this == Blocks.field_196766_fg) {
            var6 = MathHelper.func_76136_a(var2.field_73012_v, 2, 5);
         }

         this.func_180637_b(var2, var3, var6);
      }

   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(this);
   }
}
