package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface SpriteGetter {
   TextureAtlasSprite get(Material var1, ModelDebugName var2);

   TextureAtlasSprite reportMissingReference(String var1, ModelDebugName var2);

   default TextureAtlasSprite resolveSlot(TextureSlots var1, String var2, ModelDebugName var3) {
      Material var4 = var1.getMaterial(var2);
      return var4 != null ? this.get(var4, var3) : this.reportMissingReference(var2, var3);
   }
}
