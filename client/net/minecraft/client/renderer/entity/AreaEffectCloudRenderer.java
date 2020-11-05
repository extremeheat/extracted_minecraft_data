package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AreaEffectCloud;

public class AreaEffectCloudRenderer extends EntityRenderer<AreaEffectCloud> {
   public AreaEffectCloudRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public ResourceLocation getTextureLocation(AreaEffectCloud var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
