package net.minecraft.client.resources.model.sprite;

import net.minecraft.client.resources.model.ModelDebugName;

public interface MaterialBaker {
   Material.Baked get(Material material, ModelDebugName name);

   Material.Baked reportMissingReference(String reference, ModelDebugName name);

   default Material.Baked resolveSlot(final TextureSlots slots, final String id, final ModelDebugName name) {
      Material resolvedMaterial = slots.getMaterial(id);
      return resolvedMaterial != null ? this.get(resolvedMaterial, name) : this.reportMissingReference(id, name);
   }
}
