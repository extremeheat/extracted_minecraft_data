package net.minecraft.client.resources.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedBakedModel extends DelegateBakedModel {
   private final SimpleWeightedRandomList<BakedModel> list;

   public WeightedBakedModel(SimpleWeightedRandomList<BakedModel> var1) {
      super((BakedModel)((WeightedEntry.Wrapper)var1.unwrap().getFirst()).data());
      this.list = var1;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      return (List)this.list.getRandomValue(var3).map((var3x) -> {
         return var3x.getQuads(var1, var2, var3);
      }).orElse(Collections.emptyList());
   }
}
