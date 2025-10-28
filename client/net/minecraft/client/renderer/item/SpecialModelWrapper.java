package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class SpecialModelWrapper<T> implements ItemModel {
   private final SpecialModelRenderer<T> specialRenderer;
   private final ModelRenderProperties properties;

   public SpecialModelWrapper(SpecialModelRenderer<T> var1, ModelRenderProperties var2) {
      super();
      this.specialRenderer = var1;
      this.properties = var2;
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable ItemOwner var6, int var7) {
      var1.appendModelIdentityElement(this);
      ItemStackRenderState.LayerRenderState var8 = var1.newLayer();
      if (var2.hasFoil()) {
         ItemStackRenderState.FoilType var9 = ItemStackRenderState.FoilType.STANDARD;
         var8.setFoilType(var9);
         var1.setAnimated();
         var1.appendModelIdentityElement(var9);
      }

      Object var10 = this.specialRenderer.extractArgument(var2);
      var8.setExtents(() -> {
         HashSet var1 = new HashSet();
         this.specialRenderer.getExtents(var1);
         return (Vector3f[])var1.toArray(new Vector3f[0]);
      });
      var8.setupSpecialModel(this.specialRenderer, var10);
      if (var10 != null) {
         var1.appendModelIdentityElement(var10);
      }

      this.properties.applyToLayer(var8, var4);
   }

   public static record Unbaked(ResourceLocation base, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("base").forGetter(Unbaked::base), SpecialModelRenderers.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)).apply(var0, Unbaked::new));

      public Unbaked(ResourceLocation var1, SpecialModelRenderer.Unbaked var2) {
         super();
         this.base = var1;
         this.specialModel = var2;
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         var1.markDependency(this.base);
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         SpecialModelRenderer var2 = this.specialModel.bake(var1);
         if (var2 == null) {
            return var1.missingItemModel();
         } else {
            ModelRenderProperties var3 = this.getProperties(var1);
            return new SpecialModelWrapper(var2, var3);
         }
      }

      private ModelRenderProperties getProperties(ItemModel.BakingContext var1) {
         ModelBaker var2 = var1.blockModelBaker();
         ResolvedModel var3 = var2.getModel(this.base);
         TextureSlots var4 = var3.getTopTextureSlots();
         return ModelRenderProperties.fromResolvedModel(var2, var3, var4);
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }
   }
}
