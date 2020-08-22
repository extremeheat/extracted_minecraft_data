package net.minecraft.client.renderer.texture;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public class AtlasSet implements AutoCloseable {
   private final Map atlases;

   public AtlasSet(Collection var1) {
      this.atlases = (Map)var1.stream().collect(Collectors.toMap(TextureAtlas::location, Function.identity()));
   }

   public TextureAtlas getAtlas(ResourceLocation var1) {
      return (TextureAtlas)this.atlases.get(var1);
   }

   public TextureAtlasSprite getSprite(Material var1) {
      return ((TextureAtlas)this.atlases.get(var1.atlasLocation())).getSprite(var1.texture());
   }

   public void close() {
      this.atlases.values().forEach(TextureAtlas::clearTextureData);
      this.atlases.clear();
   }
}
