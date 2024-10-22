package net.minecraft.client.resources.model;

import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BakedOverrides;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class ItemModel implements UnbakedModel {
   private final ResourceLocation id;
   private List<ItemOverride> overrides = List.of();

   public ItemModel(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   @Override
   public void resolveDependencies(UnbakedModel.Resolver var1) {
      if (var1.resolve(this.id) instanceof BlockModel var3) {
         this.overrides = var3.getOverrides();
         this.overrides.forEach(var1x -> var1.resolve(var1x.model()));
      }
   }

   @Override
   public BakedModel bake(ModelBaker var1, Function<Material, TextureAtlasSprite> var2, ModelState var3) {
      BakedModel var4 = var1.bake(this.id, var3);
      if (this.overrides.isEmpty()) {
         return var4;
      } else {
         BakedOverrides var5 = new BakedOverrides(var1, this.overrides);
         return new ItemModel.BakedModelWithOverrides(var4, var5);
      }
   }

   static class BakedModelWithOverrides extends DelegateBakedModel {
      private final BakedOverrides overrides;

      public BakedModelWithOverrides(BakedModel var1, BakedOverrides var2) {
         super(var1);
         this.overrides = var2;
      }

      @Override
      public BakedOverrides overrides() {
         return this.overrides;
      }
   }
}
