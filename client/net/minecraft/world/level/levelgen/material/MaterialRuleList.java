package net.minecraft.world.level.levelgen.material;

import java.util.Iterator;
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
   public BlockState calculate(DensityFunction.FunctionContext var1) {
      Iterator var2 = this.materialRuleList.iterator();

      BlockState var4;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         NoiseChunk.BlockStateFiller var3 = (NoiseChunk.BlockStateFiller)var2.next();
         var4 = var3.calculate(var1);
      } while(var4 == null);

      return var4;
   }

   public List<NoiseChunk.BlockStateFiller> materialRuleList() {
      return this.materialRuleList;
   }
}
