package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.TextureSlots;

@FunctionalInterface
public interface UnbakedGeometry {
   UnbakedGeometry EMPTY = (textureSlots, modelBaker, modelState, name) -> QuadCollection.EMPTY;

   QuadCollection bake(TextureSlots textureSlots, ModelBaker modelBaker, ModelState modelState, ModelDebugName name);
}
