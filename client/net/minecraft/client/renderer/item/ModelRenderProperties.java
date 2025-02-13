package net.minecraft.client.renderer.item;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.world.item.ItemDisplayContext;

public record ModelRenderProperties(boolean usesBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms) {
   public ModelRenderProperties(boolean var1, TextureAtlasSprite var2, ItemTransforms var3) {
      super();
      this.usesBlockLight = var1;
      this.particleIcon = var2;
      this.transforms = var3;
   }

   public static ModelRenderProperties fromResolvedModel(ModelBaker var0, ResolvedModel var1, TextureSlots var2) {
      TextureAtlasSprite var3 = var1.resolveParticleSprite(var2, var0);
      return new ModelRenderProperties(var1.getTopGuiLight().lightLikeBlock(), var3, var1.getTopTransforms());
   }

   public void applyToLayer(ItemStackRenderState.LayerRenderState var1, ItemDisplayContext var2) {
      var1.setUsesBlockLight(this.usesBlockLight);
      var1.setParticleIcon(this.particleIcon);
      var1.setTransform(this.transforms.getTransform(var2));
   }
}
