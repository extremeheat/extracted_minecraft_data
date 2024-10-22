package net.minecraft.client.resources;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;

public abstract class TextureAtlasHolder implements PreparableReloadListener, AutoCloseable {
   private final TextureAtlas textureAtlas;
   private final ResourceLocation atlasInfoLocation;
   private final Set<MetadataSectionSerializer<?>> metadataSections;

   public TextureAtlasHolder(TextureManager var1, ResourceLocation var2, ResourceLocation var3) {
      this(var1, var2, var3, SpriteLoader.DEFAULT_METADATA_SECTIONS);
   }

   public TextureAtlasHolder(TextureManager var1, ResourceLocation var2, ResourceLocation var3, Set<MetadataSectionSerializer<?>> var4) {
      super();
      this.atlasInfoLocation = var3;
      this.textureAtlas = new TextureAtlas(var2);
      var1.register(this.textureAtlas.location(), this.textureAtlas);
      this.metadataSections = var4;
   }

   protected TextureAtlasSprite getSprite(ResourceLocation var1) {
      return this.textureAtlas.getSprite(var1);
   }

   @Override
   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      return SpriteLoader.create(this.textureAtlas)
         .loadAndStitch(var2, this.atlasInfoLocation, 0, var3, this.metadataSections)
         .thenCompose(SpriteLoader.Preparations::waitForUpload)
         .thenCompose(var1::wait)
         .thenAcceptAsync(this::apply, var4);
   }

   private void apply(SpriteLoader.Preparations var1) {
      try (Zone var2 = Profiler.get().zone("upload")) {
         this.textureAtlas.upload(var1);
      }
   }

   @Override
   public void close() {
      this.textureAtlas.clearTextureData();
   }
}
