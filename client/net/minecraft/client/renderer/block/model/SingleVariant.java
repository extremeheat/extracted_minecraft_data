package net.minecraft.client.renderer.block.model;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RandomSource;

public class SingleVariant implements BlockStateModel {
   private final BlockModelPart model;

   public SingleVariant(BlockModelPart var1) {
      super();
      this.model = var1;
   }

   public void collectParts(RandomSource var1, List<BlockModelPart> var2) {
      var2.add(this.model);
   }

   public TextureAtlasSprite particleIcon() {
      return this.model.particleIcon();
   }

   public static record Unbaked(Variant variant) implements BlockStateModel.Unbaked {
      public static final Codec<Unbaked> CODEC;

      public Unbaked(Variant var1) {
         super();
         this.variant = var1;
      }

      public BlockStateModel bake(ModelBaker var1) {
         return new SingleVariant(this.variant.bake(var1));
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         this.variant.resolveDependencies(var1);
      }

      static {
         CODEC = Variant.CODEC.xmap(Unbaked::new, Unbaked::variant);
      }
   }
}
