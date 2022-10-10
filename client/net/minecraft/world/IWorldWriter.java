package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter {
   boolean func_180501_a(BlockPos var1, IBlockState var2, int var3);

   boolean func_72838_d(Entity var1);

   boolean func_175698_g(BlockPos var1);

   void func_175653_a(EnumLightType var1, BlockPos var2, int var3);

   boolean func_175655_b(BlockPos var1, boolean var2);
}
