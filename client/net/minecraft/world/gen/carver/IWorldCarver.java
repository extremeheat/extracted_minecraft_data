package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.IFeatureConfig;

public interface IWorldCarver<C extends IFeatureConfig> {
   boolean func_212246_a(IBlockReader var1, Random var2, int var3, int var4, C var5);

   boolean func_202522_a(IWorld var1, Random var2, int var3, int var4, int var5, int var6, BitSet var7, C var8);
}
