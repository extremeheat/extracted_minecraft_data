package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasIds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class MapDecorationTextureManager extends TextureAtlasHolder {
   public MapDecorationTextureManager(TextureManager var1) {
      super(var1, ResourceLocation.withDefaultNamespace("textures/atlas/map_decorations.png"), AtlasIds.MAP_DECORATIONS);
   }

   public TextureAtlasSprite get(MapDecoration var1) {
      return this.getSprite(var1.getSpriteLocation());
   }
}
