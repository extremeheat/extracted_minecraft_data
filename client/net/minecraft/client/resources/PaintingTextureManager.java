package net.minecraft.client.resources;

import com.google.common.collect.Iterables;
import java.util.Collections;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Motive;

public class PaintingTextureManager extends TextureAtlasHolder {
   private static final ResourceLocation BACK_SPRITE_LOCATION = new ResourceLocation("back");

   public PaintingTextureManager(TextureManager var1) {
      super(var1, TextureAtlas.LOCATION_PAINTINGS, "textures/painting");
   }

   protected Iterable<ResourceLocation> getResourcesToLoad() {
      return Iterables.concat(Registry.MOTIVE.keySet(), Collections.singleton(BACK_SPRITE_LOCATION));
   }

   public TextureAtlasSprite get(Motive var1) {
      return this.getSprite(Registry.MOTIVE.getKey(var1));
   }

   public TextureAtlasSprite getBackSprite() {
      return this.getSprite(BACK_SPRITE_LOCATION);
   }
}
