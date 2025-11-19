package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.monster.phantom.PhantomModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class PhantomEyesLayer extends EyesLayer<PhantomRenderState, PhantomModel> {
   private static final RenderType PHANTOM_EYES = RenderTypes.eyes(Identifier.withDefaultNamespace("textures/entity/phantom_eyes.png"));

   public PhantomEyesLayer(RenderLayerParent<PhantomRenderState, PhantomModel> var1) {
      super(var1);
   }

   public RenderType renderType() {
      return PHANTOM_EYES;
   }
}
