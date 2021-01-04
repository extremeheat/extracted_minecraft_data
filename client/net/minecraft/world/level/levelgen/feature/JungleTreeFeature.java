package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;

public class JungleTreeFeature extends TreeFeature {
   public JungleTreeFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1, boolean var2, int var3, BlockState var4, BlockState var5, boolean var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected int getTreeHeight(Random var1) {
      return this.baseHeight + var1.nextInt(7);
   }
}
