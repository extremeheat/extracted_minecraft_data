package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Objects;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapTextureManager implements AutoCloseable {
   private final Int2ObjectMap<MapInstance> maps = new Int2ObjectOpenHashMap();
   private final TextureManager textureManager;

   public MapTextureManager(final TextureManager textureManager) {
      super();
      this.textureManager = textureManager;
   }

   public void update(final MapId id, final MapItemSavedData data) {
      this.getOrCreateMapInstance(id, data).forceUpload();
   }

   public Identifier prepareMapTexture(final MapId id, final MapItemSavedData data) {
      MapInstance mapInstance = this.getOrCreateMapInstance(id, data);
      mapInstance.updateTextureIfNeeded();
      return mapInstance.location;
   }

   public void resetData() {
      ObjectIterator var1 = this.maps.values().iterator();

      while(var1.hasNext()) {
         MapInstance mapInstance = (MapInstance)var1.next();
         mapInstance.close();
      }

      this.maps.clear();
   }

   private MapInstance getOrCreateMapInstance(final MapId id, final MapItemSavedData data) {
      return (MapInstance)this.maps.compute(id.id(), (k, instance) -> {
         if (instance == null) {
            return new MapInstance(k, data);
         } else {
            instance.replaceMapData(data);
            return instance;
         }
      });
   }

   public void close() {
      this.resetData();
   }

   private class MapInstance implements AutoCloseable {
      private MapItemSavedData data;
      private final DynamicTexture texture;
      private boolean requiresUpload;
      private final Identifier location;

      private MapInstance(final int id, final MapItemSavedData data) {
         Objects.requireNonNull(MapTextureManager.this);
         super();
         this.requiresUpload = true;
         this.data = data;
         this.texture = new DynamicTexture(() -> "Map " + id, 128, 128, true);
         this.location = Identifier.withDefaultNamespace("map/" + id);
         MapTextureManager.this.textureManager.register(this.location, this.texture);
      }

      private void replaceMapData(final MapItemSavedData data) {
         boolean dataChanged = this.data != data;
         this.data = data;
         this.requiresUpload |= dataChanged;
      }

      public void forceUpload() {
         this.requiresUpload = true;
      }

      private void updateTextureIfNeeded() {
         if (this.requiresUpload) {
            NativeImage pixels = this.texture.getPixels();

            for(int y = 0; y < 128; ++y) {
               for(int x = 0; x < 128; ++x) {
                  int i = x + y * 128;
                  pixels.setPixel(x, y, MapColor.getColorFromPackedId(this.data.colors[i]));
               }
            }

            this.texture.upload();
            this.requiresUpload = false;
         }

      }

      public void close() {
         this.texture.close();
      }
   }
}
