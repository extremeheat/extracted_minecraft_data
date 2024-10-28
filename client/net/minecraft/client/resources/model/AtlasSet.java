package net.minecraft.client.resources.model;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class AtlasSet implements AutoCloseable {
   private final Map<ResourceLocation, AtlasEntry> atlases;

   public AtlasSet(Map<ResourceLocation, ResourceLocation> var1, TextureManager var2) {
      super();
      this.atlases = (Map)var1.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (var1x) -> {
         TextureAtlas var2x = new TextureAtlas((ResourceLocation)var1x.getKey());
         var2.register((ResourceLocation)((ResourceLocation)var1x.getKey()), (AbstractTexture)var2x);
         return new AtlasEntry(var2x, (ResourceLocation)var1x.getValue());
      }));
   }

   public TextureAtlas getAtlas(ResourceLocation var1) {
      return ((AtlasEntry)this.atlases.get(var1)).atlas();
   }

   public void close() {
      this.atlases.values().forEach(AtlasEntry::close);
      this.atlases.clear();
   }

   public Map<ResourceLocation, CompletableFuture<StitchResult>> scheduleLoad(ResourceManager var1, int var2, Executor var3) {
      return (Map)this.atlases.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (var3x) -> {
         AtlasEntry var4 = (AtlasEntry)var3x.getValue();
         return SpriteLoader.create(var4.atlas).loadAndStitch(var1, var4.atlasInfoLocation, var2, var3).thenApply((var1x) -> {
            return new StitchResult(var4.atlas, var1x);
         });
      }));
   }

   static record AtlasEntry(TextureAtlas atlas, ResourceLocation atlasInfoLocation) implements AutoCloseable {
      final TextureAtlas atlas;
      final ResourceLocation atlasInfoLocation;

      AtlasEntry(TextureAtlas var1, ResourceLocation var2) {
         super();
         this.atlas = var1;
         this.atlasInfoLocation = var2;
      }

      public void close() {
         this.atlas.clearTextureData();
      }

      public TextureAtlas atlas() {
         return this.atlas;
      }

      public ResourceLocation atlasInfoLocation() {
         return this.atlasInfoLocation;
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
         return (TextureAtlasSprite)this.preparations.regions().get(var1);
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
