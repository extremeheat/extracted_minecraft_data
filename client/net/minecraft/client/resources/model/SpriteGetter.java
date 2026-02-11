package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface SpriteGetter {
   TextureAtlasSprite get(SpriteId id);
}
