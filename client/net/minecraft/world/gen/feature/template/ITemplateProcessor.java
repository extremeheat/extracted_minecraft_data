package net.minecraft.world.gen.feature.template;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface ITemplateProcessor {
   @Nullable
   Template.BlockInfo func_189943_a(IBlockReader var1, BlockPos var2, Template.BlockInfo var3);
}
