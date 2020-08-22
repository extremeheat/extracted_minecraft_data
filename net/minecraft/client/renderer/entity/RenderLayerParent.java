package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public interface RenderLayerParent {
   EntityModel getModel();

   ResourceLocation getTextureLocation(Entity var1);
}
