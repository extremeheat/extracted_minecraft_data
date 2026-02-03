package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface SpriteGetter {
   TextureAtlasSprite get(Material material, ModelDebugName name);

   TextureAtlasSprite reportMissingReference(String reference, ModelDebugName name);

   default TextureAtlasSprite resolveSlot(final TextureSlots slots, final String id, final ModelDebugName name) {
      Material resolvedMaterial = slots.getMaterial(id);
      return resolvedMaterial != null ? this.get(resolvedMaterial, name) : this.reportMissingReference(id, name);
   }
}
