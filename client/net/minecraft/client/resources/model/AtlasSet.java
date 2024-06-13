package net.minecraft.client.resources.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AtlasSet implements AutoCloseable {
   private final Map<ResourceLocation, AtlasSet.AtlasEntry> atlases;

   public AtlasSet(Map<ResourceLocation, ResourceLocation> var1, TextureManager var2) {
      super();
      this.atlases = var1.entrySet().stream().collect(Collectors.toMap(Entry::getKey, var1x -> {
         TextureAtlas var2x = new TextureAtlas((ResourceLocation)var1x.getKey());
         var2.register((ResourceLocation)var1x.getKey(), var2x);
         return new AtlasSet.AtlasEntry(var2x, (ResourceLocation)var1x.getValue());
      }));
   }

   public TextureAtlas getAtlas(ResourceLocation var1) {
      return this.atlases.get(var1).atlas();
   }

   @Override
   public void close() {
      this.atlases.values().forEach(AtlasSet.AtlasEntry::close);
      this.atlases.clear();
   }

   public Map<ResourceLocation, CompletableFuture<AtlasSet.StitchResult>> scheduleLoad(ResourceManager var1, int var2, Executor var3) {
      return this.atlases
         .entrySet()
         .stream()
         .collect(
            Collectors.toMap(
               Entry::getKey,
               var3x -> {
                  AtlasSet.AtlasEntry var4 = var3x.getValue();
                  return SpriteLoader.create(var4.atlas)
                     .loadAndStitch(var1, var4.atlasInfoLocation, var2, var3)
                     .thenApply(var1xx -> new AtlasSet.StitchResult(var4.atlas, var1xx));
               }
            )
         );
   }

   static record AtlasEntry(TextureAtlas atlas, ResourceLocation atlasInfoLocation) implements AutoCloseable {

      AtlasEntry(TextureAtlas atlas, ResourceLocation atlasInfoLocation) {
         super();
         this.atlas = atlas;
         this.atlasInfoLocation = atlasInfoLocation;
      }

      @Override
      public void close() {
         this.atlas.clearTextureData();
      }
   }

   public static class StitchResult {
      private final TextureAtlas atlas;
      private final SpriteLoader.Preparations preparations;

      public StitchResult(TextureAtlas var1, SpriteLoader.Preparations var2) {
         super();
         this.atlas = var1;
         this.preparations = var2;
      }

      @Nullable
      public TextureAtlasSprite getSprite(ResourceLocation var1) {
         return this.preparations.regions().get(var1);
      }

      public TextureAtlasSprite missing() {
         return this.preparations.missing();
      }

      public CompletableFuture<Void> readyForUpload() {
         return this.preparations.readyForUpload();
      }

      public void upload() {
         this.atlas.upload(this.preparations);
      }
   }
}
