package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;

public class PhantomEyesLayer<T extends Phantom> extends EyesLayer<T, PhantomModel<T>> {
   private static final RenderType PHANTOM_EYES = RenderType.eyes(ResourceLocation.withDefaultNamespace("textures/entity/phantom_eyes.png"));

   public PhantomEyesLayer(RenderLayerParent<T, PhantomModel<T>> var1) {
      super(var1);
   }

   public RenderType renderType() {
      return PHANTOM_EYES;
   }
}
