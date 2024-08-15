package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import net.minecraft.resources.ResourceLocation;

public class PhantomEyesLayer extends EyesLayer<PhantomRenderState, PhantomModel> {
   private static final RenderType PHANTOM_EYES = RenderType.eyes(ResourceLocation.withDefaultNamespace("textures/entity/phantom_eyes.png"));

   public PhantomEyesLayer(RenderLayerParent<PhantomRenderState, PhantomModel> var1) {
      super(var1);
   }

   @Override
   public RenderType renderType() {
      return PHANTOM_EYES;
   }
}
