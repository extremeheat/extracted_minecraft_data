package net.minecraft.world.level.levelgen.material;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import org.jspecify.annotations.Nullable;

public record MaterialRuleList(NoiseChunk.BlockStateFiller[] materialRuleList) implements NoiseChunk.BlockStateFiller {
   public MaterialRuleList(NoiseChunk.BlockStateFiller[] var1) {
      super();
      this.materialRuleList = var1;
   }

   public @Nullable BlockState calculate(DensityFunction.FunctionContext var1) {
      for(NoiseChunk.BlockStateFiller var5 : this.materialRuleList) {
         BlockState var6 = var5.calculate(var1);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }
}
