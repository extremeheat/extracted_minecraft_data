package net.minecraft.client.resources.model;

import java.util.List;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public class WeightedVariants implements BlockStateModel {
   private final WeightedList<BlockStateModel> list;
   private final TextureAtlasSprite particleIcon;

   public WeightedVariants(final WeightedList<BlockStateModel> list) {
      super();
      this.list = list;
      BlockStateModel firstModel = (BlockStateModel)((Weighted)list.unwrap().getFirst()).value();
      this.particleIcon = firstModel.particleIcon();
   }

   public TextureAtlasSprite particleIcon() {
      return this.particleIcon;
   }

   public void collectParts(final RandomSource random, final List<BlockModelPart> output) {
      (this.list.getRandomOrThrow(random)).collectParts(random, output);
   }

   public static record Unbaked(WeightedList<BlockStateModel.Unbaked> entries) implements BlockStateModel.Unbaked {
      public Unbaked {
         super();
      }

      public BlockStateModel bake(final ModelBaker modelBakery) {
         return new WeightedVariants(this.entries.map((m) -> m.bake(modelBakery)));
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         this.entries.unwrap().forEach((v) -> ((BlockStateModel.Unbaked)v.value()).resolveDependencies(resolver));
      }
   }
}
