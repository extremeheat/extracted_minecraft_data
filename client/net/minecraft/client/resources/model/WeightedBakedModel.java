package net.minecraft.client.resources.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedBakedModel implements BlockStateModel {
   private final WeightedList<BlockStateModel> list;
   private final boolean useAmbientOcclusion;
   private final TextureAtlasSprite particleIcon;

   public WeightedBakedModel(WeightedList<BlockStateModel> var1) {
      super();
      this.list = var1;
      BlockStateModel var2 = (BlockStateModel)((Weighted)var1.unwrap().getFirst()).value();
      this.useAmbientOcclusion = var2.useAmbientOcclusion();
      this.particleIcon = var2.particleIcon();
   }

   public boolean useAmbientOcclusion() {
      return this.useAmbientOcclusion;
   }

   public TextureAtlasSprite particleIcon() {
      return this.particleIcon;
   }

   public List<BakedQuad> getQuads(BlockState var1, @Nullable Direction var2, RandomSource var3) {
      return ((BlockStateModel)this.list.getRandomOrThrow(var3)).getQuads(var1, var2, var3);
   }
}
