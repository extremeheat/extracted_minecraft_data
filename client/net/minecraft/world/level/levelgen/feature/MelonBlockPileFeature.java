package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MelonBlockPileFeature extends BlockPileFeature {
   public MelonBlockPileFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected BlockState getBlockState(LevelAccessor var1) {
      return Blocks.MELON.defaultBlockState();
   }
}
