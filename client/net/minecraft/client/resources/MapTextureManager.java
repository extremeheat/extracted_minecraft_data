package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapTextureManager implements AutoCloseable {
   private final Int2ObjectMap<MapInstance> maps = new Int2ObjectOpenHashMap();
   final TextureManager textureManager;

   public MapTextureManager(TextureManager var1) {
      super();
      this.textureManager = var1;
   }

   public void update(MapId var1, MapItemSavedData var2) {
      this.getOrCreateMapInstance(var1, var2).forceUpload();
   }

   public ResourceLocation prepareMapTexture(MapId var1, MapItemSavedData var2) {
      MapInstance var3 = this.getOrCreateMapInstance(var1, var2);
      var3.updateTextureIfNeeded();
      return var3.location;
   }

   public void resetData() {
      ObjectIterator var1 = this.maps.values().iterator();

      while(var1.hasNext()) {
         MapInstance var2 = (MapInstance)var1.next();
         var2.close();
      }

      this.maps.clear();
   }

   private MapInstance getOrCreateMapInstance(MapId var1, MapItemSavedData var2) {
      return (MapInstance)this.maps.compute(var1.id(), (var2x, var3) -> {
         if (var3 == null) {
            return new MapInstance(this, var2x, var2);
         } else {
            var3.replaceMapData(var2);
            return var3;
         }
      });
   }

   public void close() {
      this.resetData();
   }

   class MapInstance implements AutoCloseable {
      private MapItemSavedData data;
      private final DynamicTexture texture;
      private boolean requiresUpload = true;
      final ResourceLocation location;

      MapInstance(final MapTextureManager var1, final int var2, final MapItemSavedData var3) {
         super();
         this.data = var3;
         this.texture = new DynamicTexture(128, 128, true);
         this.location = var1.textureManager.register("map/" + var2, this.texture);
      }

      void replaceMapData(MapItemSavedData var1) {
         boolean var2 = this.data != var1;
         this.data = var1;
         this.requiresUpload |= var2;
      }

      public void forceUpload() {
         this.requiresUpload = true;
      }

      void updateTextureIfNeeded() {
         if (this.requiresUpload) {
            NativeImage var1 = this.texture.getPixels();
            if (var1 != null) {
               for(int var2 = 0; var2 < 128; ++var2) {
                  for(int var3 = 0; var3 < 128; ++var3) {
                     int var4 = var3 + var2 * 128;
                     var1.setPixel(var3, var2, MapColor.getColorFromPackedId(this.data.colors[var4]));
                  }
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
