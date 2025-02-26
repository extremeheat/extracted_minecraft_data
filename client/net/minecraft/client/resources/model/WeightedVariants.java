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

   public WeightedVariants(WeightedList<BlockStateModel> var1) {
      super();
      this.list = var1;
      BlockStateModel var2 = (BlockStateModel)((Weighted)var1.unwrap().getFirst()).value();
      this.particleIcon = var2.particleIcon();
   }

   public TextureAtlasSprite particleIcon() {
      return this.particleIcon;
   }

   public void collectParts(RandomSource var1, List<BlockModelPart> var2) {
      (this.list.getRandomOrThrow(var1)).collectParts(var1, var2);
   }

   public static record Unbaked(WeightedList<BlockStateModel.Unbaked> entries) implements BlockStateModel.Unbaked {
      public Unbaked(WeightedList<BlockStateModel.Unbaked> var1) {
         super();
         this.entries = var1;
      }

      public BlockStateModel bake(ModelBaker var1) {
         return new WeightedVariants(this.entries.map((var1x) -> var1x.bake(var1)));
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         this.entries.unwrap().forEach((var1x) -> ((BlockStateModel.Unbaked)var1x.value()).resolveDependencies(var1));
      }
   }
}
