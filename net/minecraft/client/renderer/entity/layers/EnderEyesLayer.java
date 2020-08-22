package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class EnderEyesLayer extends EyesLayer {
   private static final RenderType ENDERMAN_EYES = RenderType.eyes(new ResourceLocation("textures/entity/enderman/enderman_eyes.png"));

   public EnderEyesLayer(RenderLayerParent var1) {
      super(var1);
   }

   public RenderType renderType() {
      return ENDERMAN_EYES;
   }
}
