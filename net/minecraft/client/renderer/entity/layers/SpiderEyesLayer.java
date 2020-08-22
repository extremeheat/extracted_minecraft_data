package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class SpiderEyesLayer extends EyesLayer {
   private static final RenderType SPIDER_EYES = RenderType.eyes(new ResourceLocation("textures/entity/spider_eyes.png"));

   public SpiderEyesLayer(RenderLayerParent var1) {
      super(var1);
   }

   public RenderType renderType() {
      return SPIDER_EYES;
   }
}
