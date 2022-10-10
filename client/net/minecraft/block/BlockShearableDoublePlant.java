package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockShearableDoublePlant extends BlockDoublePlant {
   public static final EnumProperty<DoubleBlockHalf> field_208063_b;
   private final Block field_196392_b;

   public BlockShearableDoublePlant(Block var1, Block.Properties var2) {
      super(var2);
      this.field_196392_b = var1;
   }

   public boolean func_196253_a(IBlockState var1, BlockItemUseContext var2) {
      boolean var3 = super.func_196253_a(var1, var2);
      return var3 && var2.func_195996_i().func_77973_b() == this.func_199767_j() ? false : var3;
   }

   protected void func_196391_a(IBlockState var1, World var2, BlockPos var3, ItemStack var4) {
      if (var4.func_77973_b() == Items.field_151097_aZ) {
         func_180635_a(var2, var3, new ItemStack(this.field_196392_b, 2));
      } else {
         super.func_196391_a(var1, var2, var3, var4);
      }

   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return var1.func_177229_b(field_208063_b) == DoubleBlockHalf.LOWER && this == Blocks.field_196804_gh && var2.field_73012_v.nextInt(8) == 0 ? Items.field_151014_N : Items.field_190931_a;
   }

   static {
      field_208063_b = BlockDoublePlant.field_176492_b;
   }
}
