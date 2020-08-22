package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.level.block.state.BlockState;

public class WeightedBakedModel implements BakedModel {
   private final int totalWeight;
   private final List list;
   private final BakedModel wrapped;

   public WeightedBakedModel(List var1) {
      this.list = var1;
      this.totalWeight = WeighedRandom.getTotalWeight(var1);
      this.wrapped = ((WeightedBakedModel.WeightedModel)var1.get(0)).model;
   }

   public List getQuads(@Nullable BlockState var1, @Nullable Direction var2, Random var3) {
      return ((WeightedBakedModel.WeightedModel)WeighedRandom.getWeightedItem(this.list, Math.abs((int)var3.nextLong()) % this.totalWeight)).model.getQuads(var1, var2, var3);
   }

   public boolean useAmbientOcclusion() {
      return this.wrapped.useAmbientOcclusion();
   }

   public boolean isGui3d() {
      return this.wrapped.isGui3d();
   }

   public boolean isCustomRenderer() {
      return this.wrapped.isCustomRenderer();
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.wrapped.getParticleIcon();
   }

   public ItemTransforms getTransforms() {
      return this.wrapped.getTransforms();
   }

   public ItemOverrides getOverrides() {
      return this.wrapped.getOverrides();
   }

   static class WeightedModel extends WeighedRandom.WeighedRandomItem {
      protected final BakedModel model;

      public WeightedModel(BakedModel var1, int var2) {
         super(var2);
         this.model = var1;
      }
   }

   public static class Builder {
      private final List list = Lists.newArrayList();

      public WeightedBakedModel.Builder add(@Nullable BakedModel var1, int var2) {
         if (var1 != null) {
            this.list.add(new WeightedBakedModel.WeightedModel(var1, var2));
         }

         return this;
      }

      @Nullable
      public BakedModel build() {
         if (this.list.isEmpty()) {
            return null;
         } else {
            return (BakedModel)(this.list.size() == 1 ? ((WeightedBakedModel.WeightedModel)this.list.get(0)).model : new WeightedBakedModel(this.list));
         }
      }
   }
}
