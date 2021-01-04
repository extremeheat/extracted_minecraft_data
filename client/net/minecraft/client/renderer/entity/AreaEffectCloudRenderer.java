package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AreaEffectCloud;

public class AreaEffectCloudRenderer extends EntityRenderer<AreaEffectCloud> {
   public AreaEffectCloudRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(AreaEffectCloud var1) {
      return null;
   }
}
