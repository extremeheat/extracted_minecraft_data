package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SpecialModelWrapper<T> implements ItemModel {
   private final SpecialModelRenderer<T> specialRenderer;
   private final BakedModel baseModel;

   public SpecialModelWrapper(SpecialModelRenderer<T> var1, BakedModel var2) {
      super();
      this.specialRenderer = var1;
      this.baseModel = var2;
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      ItemStackRenderState.LayerRenderState var8 = var1.newLayer();
      if (var2.hasFoil()) {
         var8.setFoilType(ItemStackRenderState.FoilType.STANDARD);
      }

      var8.setupSpecialModel(this.specialRenderer, this.specialRenderer.extractArgument(var2), this.baseModel);
   }

   public static record Unbaked(ResourceLocation base, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("base").forGetter(Unbaked::base), SpecialModelRenderers.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)).apply(var0, Unbaked::new));

      public Unbaked(ResourceLocation var1, SpecialModelRenderer.Unbaked var2) {
         super();
         this.base = var1;
         this.specialModel = var2;
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         var1.resolve(this.base);
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         BakedModel var2 = var1.bake(this.base);
         SpecialModelRenderer var3 = this.specialModel.bake(var1.entityModelSet());
         return (ItemModel)(var3 == null ? var1.missingItemModel() : new SpecialModelWrapper(var3, var2));
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }
   }
}
