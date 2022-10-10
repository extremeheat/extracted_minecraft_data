package net.minecraft.client.renderer.color;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;

public interface IBlockColor {
   int getColor(IBlockState var1, @Nullable IWorldReaderBase var2, @Nullable BlockPos var3, int var4);
}
