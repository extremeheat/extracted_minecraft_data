package net.minecraft.client.resources.model;

import java.util.List;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public class WeightedVariants implements BlockStateModel {
   private final WeightedList<BlockStateModel> list;
   private final Material.Baked particleMaterial;
   private final boolean hasTranslucency;

   public WeightedVariants(final WeightedList<BlockStateModel> list) {
      super();
      this.list = list;
      BlockStateModel firstModel = (BlockStateModel)((Weighted)list.unwrap().getFirst()).value();
      this.particleMaterial = firstModel.particleMaterial();
      this.hasTranslucency = hasTranslucency(list);
   }

   private static boolean hasTranslucency(final WeightedList<BlockStateModel> list) {
      for(Weighted<BlockStateModel> entry : list.unwrap()) {
         if (((BlockStateModel)entry.value()).hasTranslucency()) {
            return true;
         }
      }

      return false;
   }

   public Material.Baked particleMaterial() {
      return this.particleMaterial;
   }

   public boolean hasTranslucency() {
      return this.hasTranslucency;
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
