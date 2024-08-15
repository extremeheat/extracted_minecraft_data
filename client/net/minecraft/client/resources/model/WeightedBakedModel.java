package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedBakedModel implements BakedModel {
   private final int totalWeight;
   private final List<WeightedEntry.Wrapper<BakedModel>> list;
   private final BakedModel wrapped;

   public WeightedBakedModel(List<WeightedEntry.Wrapper<BakedModel>> var1) {
      super();
      this.list = var1;
      this.totalWeight = WeightedRandom.getTotalWeight(var1);
      this.wrapped = (BakedModel)((WeightedEntry.Wrapper)var1.get(0)).data();
   }

   @Override
   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      return WeightedRandom.getWeightedItem(this.list, Math.abs((int)var3.nextLong()) % this.totalWeight)
         .map(var3x -> var3x.data().getQuads(var1, var2, var3))
         .orElse(Collections.emptyList());
   }

   @Override
   public boolean useAmbientOcclusion() {
      return this.wrapped.useAmbientOcclusion();
   }

   @Override
   public boolean isGui3d() {
      return this.wrapped.isGui3d();
   }

   @Override
   public boolean usesBlockLight() {
      return this.wrapped.usesBlockLight();
   }

   @Override
   public boolean isCustomRenderer() {
      return this.wrapped.isCustomRenderer();
   }

   @Override
   public TextureAtlasSprite getParticleIcon() {
      return this.wrapped.getParticleIcon();
   }

   @Override
   public ItemTransforms getTransforms() {
      return this.wrapped.getTransforms();
   }

   @Override
   public ItemOverrides getOverrides() {
      return this.wrapped.getOverrides();
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableList.Builder<WeightedEntry.Wrapper<BakedModel>> list = ImmutableList.builder();

      public Builder() {
         super();
      }

      public WeightedBakedModel.Builder add(@Nullable BakedModel var1, int var2) {
         if (var1 != null) {
            this.list.add(WeightedEntry.wrap(var1, var2));
         }

         return this;
      }

      @Nullable
      public BakedModel build() {
         ImmutableList var1 = this.list.build();
         if (var1.isEmpty()) {
            return null;
         } else {
            return (BakedModel)(var1.size() == 1 ? (BakedModel)((WeightedEntry.Wrapper)var1.getFirst()).data() : new WeightedBakedModel(var1));
         }
      }
   }
}
