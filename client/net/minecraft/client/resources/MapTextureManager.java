package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureResources;
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

   private class MapInstance {
      private MapItemSavedData data;
      private boolean requiresUpload;
      private final Identifier location;
      private final GpuTexture texture;

      private MapInstance(final int id, final MapItemSavedData data) {
         Objects.requireNonNull(MapTextureManager.this);
         super();
         this.requiresUpload = true;
         this.data = data;
         GpuDevice device = RenderSystem.getDevice();
         this.texture = device.createTexture((Supplier)(() -> "Map " + id), 5, GpuFormat.RGBA8_UNORM, 128, 128, 1, 1);
         GpuSampler sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
         GpuTextureView textureView = device.createTextureView(this.texture);
         this.location = Identifier.withDefaultNamespace("map/" + id);
         MapTextureManager.this.textureManager.register(this.location, new TextureResources(this.texture, textureView, sampler));
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
            try (NativeImage pixels = new NativeImage(128, 128, false)) {
               for(int y = 0; y < 128; ++y) {
                  for(int x = 0; x < 128; ++x) {
                     int i = x + y * 128;
                     pixels.setPixel(x, y, MapColor.getColorFromPackedId(this.data.colors[i]));
                  }
               }

               pixels.writeToGpuTexture(RenderSystem.getDevice().createCommandEncoder(), this.texture);
            }

            this.requiresUpload = false;
         }

      }
   }
}
