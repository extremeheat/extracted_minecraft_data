package net.minecraft.client.renderer.item;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.world.item.ItemDisplayContext;

public record ModelRenderProperties(boolean usesBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms) {
   public ModelRenderProperties {
      super();
   }

   public static ModelRenderProperties fromResolvedModel(final ModelBaker baker, final ResolvedModel resolvedModel, final TextureSlots textureSlots) {
      TextureAtlasSprite particleSprite = resolvedModel.resolveParticleSprite(textureSlots, baker);
      return new ModelRenderProperties(resolvedModel.getTopGuiLight().lightLikeBlock(), particleSprite, resolvedModel.getTopTransforms());
   }

   public void applyToLayer(final ItemStackRenderState.LayerRenderState layer, final ItemDisplayContext displayContext) {
      layer.setUsesBlockLight(this.usesBlockLight);
      layer.setParticleIcon(this.particleIcon);
      layer.setTransform(this.transforms.getTransform(displayContext));
   }
}
