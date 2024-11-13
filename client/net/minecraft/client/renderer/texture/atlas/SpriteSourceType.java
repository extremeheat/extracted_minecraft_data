package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.MapCodec;

public record SpriteSourceType(MapCodec<? extends SpriteSource> codec) {
   public SpriteSourceType(MapCodec<? extends SpriteSource> var1) {
      super();
      this.codec = var1;
   }
}
