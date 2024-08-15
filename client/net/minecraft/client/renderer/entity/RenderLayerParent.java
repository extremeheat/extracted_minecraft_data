package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

public interface RenderLayerParent<S extends EntityRenderState, M extends EntityModel<? super S>> {
   M getModel();

   ResourceLocation getTextureLocation(S var1);
}
