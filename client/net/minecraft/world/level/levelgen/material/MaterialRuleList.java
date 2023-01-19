package net.minecraft.world.level.levelgen.material;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

public record MaterialRuleList(List<NoiseChunk.BlockStateFiller> a) implements NoiseChunk.BlockStateFiller {
   private final List<NoiseChunk.BlockStateFiller> materialRuleList;

   public MaterialRuleList(List<NoiseChunk.BlockStateFiller> var1) {
      super();
      this.materialRuleList = var1;
   }

   @Nullable
   @Override
   public BlockState calculate(DensityFunction.FunctionContext var1) {
      for(NoiseChunk.BlockStateFiller var3 : this.materialRuleList) {
         BlockState var4 = var3.calculate(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }
}
