package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockTallFlower extends BlockDoublePlant implements IGrowable {
   public BlockTallFlower(Block.Properties var1) {
      super(var1);
   }

   public boolean func_196253_a(IBlockState var1, BlockItemUseContext var2) {
      return false;
   }

   public boolean func_176473_a(IBlockReader var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      func_180635_a(var1, var3, new ItemStack(this));
   }
}
