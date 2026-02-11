package net.minecraft.client.renderer.block.model;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RandomSource;

public class SingleVariant implements BlockStateModel {
   private final BlockModelPart model;

   public SingleVariant(final BlockModelPart model) {
      super();
      this.model = model;
   }

   public void collectParts(final RandomSource random, final List<BlockModelPart> output) {
      output.add(this.model);
   }

   public Material.Baked particleMaterial() {
      return this.model.particleMaterial();
   }

   public boolean hasTranslucency() {
      return this.model.hasTranslucency();
   }

   public static record Unbaked(Variant variant) implements BlockStateModel.Unbaked {
      public static final Codec<Unbaked> CODEC;

      public Unbaked {
         super();
      }

      public BlockStateModel bake(final ModelBaker modelBakery) {
         return new SingleVariant(this.variant.bake(modelBakery));
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         this.variant.resolveDependencies(resolver);
      }

      static {
         CODEC = Variant.CODEC.xmap(Unbaked::new, Unbaked::variant);
      }
   }
}
