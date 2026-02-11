package net.minecraft.client.renderer.item;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.Material;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.world.item.ItemDisplayContext;

public record ModelRenderProperties(boolean usesBlockLight, Material.Baked particleMaterial, ItemTransforms transforms) {
   public ModelRenderProperties {
      super();
   }

   public static ModelRenderProperties fromResolvedModel(final ModelBaker baker, final ResolvedModel resolvedModel, final TextureSlots textureSlots) {
      Material.Baked particleSprite = resolvedModel.resolveParticleMaterial(textureSlots, baker);
      return new ModelRenderProperties(resolvedModel.getTopGuiLight().lightLikeBlock(), particleSprite, resolvedModel.getTopTransforms());
   }

   public void applyToLayer(final ItemStackRenderState.LayerRenderState layer, final ItemDisplayContext displayContext) {
      layer.setUsesBlockLight(this.usesBlockLight);
      layer.setParticleMaterial(this.particleMaterial);
      layer.setTransform(this.transforms.getTransform(displayContext));
   }
}
