package net.minecraft.world.level.levelgen.material;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

public record MaterialRuleList(NoiseChunk.BlockStateFiller[] materialRuleList) implements NoiseChunk.BlockStateFiller {
   public MaterialRuleList(NoiseChunk.BlockStateFiller[] var1) {
      super();
      this.materialRuleList = var1;
   }

   @Nullable
   public BlockState calculate(DensityFunction.FunctionContext var1) {
      NoiseChunk.BlockStateFiller[] var2 = this.materialRuleList;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         NoiseChunk.BlockStateFiller var5 = var2[var4];
         BlockState var6 = var5.calculate(var1);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }

   public NoiseChunk.BlockStateFiller[] materialRuleList() {
      return this.materialRuleList;
   }
}
