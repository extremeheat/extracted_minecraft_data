package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface SpriteSet {
   TextureAtlasSprite get(int var1, int var2);

   TextureAtlasSprite get(Random var1);
}
