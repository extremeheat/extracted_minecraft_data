package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public interface RenderLayerParent<T extends Entity, M extends EntityModel<T>> {
   M getModel();

   ResourceLocation getTextureLocation(T var1);
}
