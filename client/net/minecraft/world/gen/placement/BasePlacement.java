package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class BasePlacement<T extends IPlacementConfig> {
   public BasePlacement() {
      super();
   }

   public abstract <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, T var5, Feature<C> var6, C var7);

   public String toString() {
      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
   }
}
