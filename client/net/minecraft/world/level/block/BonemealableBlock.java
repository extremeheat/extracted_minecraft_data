package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BonemealableBlock {
   boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4);

   boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4);

   void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4);
}
