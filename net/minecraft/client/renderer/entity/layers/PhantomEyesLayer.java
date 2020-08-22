package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class PhantomEyesLayer extends EyesLayer {
   private static final RenderType PHANTOM_EYES = RenderType.eyes(new ResourceLocation("textures/entity/phantom_eyes.png"));

   public PhantomEyesLayer(RenderLayerParent var1) {
      super(var1);
   }

   public RenderType renderType() {
      return PHANTOM_EYES;
   }
}
