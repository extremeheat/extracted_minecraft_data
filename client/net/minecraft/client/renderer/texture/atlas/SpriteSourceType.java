package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.Codec;

public record SpriteSourceType(Codec<? extends SpriteSource> a) {
   private final Codec<? extends SpriteSource> codec;

   public SpriteSourceType(Codec<? extends SpriteSource> var1) {
      super();
      this.codec = var1;
   }
}
