package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DecoratedFeature extends Feature<DecoratedFeatureConfiguration> {
   public DecoratedFeature(Codec<DecoratedFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, DecoratedFeatureConfiguration var5) {
      MutableBoolean var6 = new MutableBoolean();
      var5.decorator.getPositions(new DecorationContext(var1, var2), var3, var4).forEach((var5x) -> {
         if (((ConfiguredFeature)var5.feature.get()).place(var1, var2, var3, var5x)) {
            var6.setTrue();
         }

      });
      return var6.isTrue();
   }

   public String toString() {
      return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this));
   }
}
