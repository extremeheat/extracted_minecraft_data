package net.minecraft.client.resources.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedBakedModel extends DelegateBakedModel {
   private final WeightedList<BakedModel> list;

   public WeightedBakedModel(WeightedList<BakedModel> var1) {
      super((BakedModel)((Weighted)var1.unwrap().getFirst()).value());
      this.list = var1;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      return ((BakedModel)this.list.getRandomOrThrow(var3)).getQuads(var1, var2, var3);
   }
}
