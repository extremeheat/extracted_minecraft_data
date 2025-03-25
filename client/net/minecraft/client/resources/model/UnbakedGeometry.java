package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.TextureSlots;

@FunctionalInterface
public interface UnbakedGeometry {
   UnbakedGeometry EMPTY = (var0, var1, var2, var3) -> QuadCollection.EMPTY;

   QuadCollection bake(TextureSlots var1, ModelBaker var2, ModelState var3, ModelDebugName var4);
}
