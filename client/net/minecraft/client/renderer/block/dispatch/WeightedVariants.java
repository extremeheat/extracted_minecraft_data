package net.minecraft.client.renderer.block.dispatch;

import java.util.List;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public class WeightedVariants implements BlockStateModel {
   private final WeightedList<BlockStateModel> list;
   private final Material.Baked particleMaterial;
   private final @BakedQuad.MaterialFlags int materialFlags;

   public WeightedVariants(final WeightedList<BlockStateModel> list) {
      super();
      this.list = list;
      BlockStateModel firstModel = (BlockStateModel)((Weighted)list.unwrap().getFirst()).value();
      this.particleMaterial = firstModel.particleMaterial();
      this.materialFlags = computeMaterialFlags(list);
   }

   private static @BakedQuad.MaterialFlags int computeMaterialFlags(final WeightedList<BlockStateModel> list) {
      int flags = 0;

      for(Weighted<BlockStateModel> entry : list.unwrap()) {
         flags |= ((BlockStateModel)entry.value()).materialFlags();
      }

      return flags;
   }

   public Material.Baked particleMaterial() {
      return this.particleMaterial;
   }

   public @BakedQuad.MaterialFlags int materialFlags() {
      return this.materialFlags;
   }

   public void collectParts(final RandomSource random, final List<BlockStateModelPart> output) {
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
