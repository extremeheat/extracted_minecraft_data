package net.minecraft.client.resources;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.AbstractTexture;
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
      var1.register((ResourceLocation)this.textureAtlas.location(), (AbstractTexture)this.textureAtlas);
      this.metadataSections = var4;
   }

   protected TextureAtlasSprite getSprite(ResourceLocation var1) {
      return this.textureAtlas.getSprite(var1);
   }

   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      CompletableFuture var10000 = SpriteLoader.create(this.textureAtlas).loadAndStitch(var2, this.atlasInfoLocation, 0, var3, this.metadataSections).thenCompose(SpriteLoader.Preparations::waitForUpload);
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync(this::apply, var4);
   }

   private void apply(SpriteLoader.Preparations var1) {
      Zone var2 = Profiler.get().zone("upload");

      try {
         this.textureAtlas.upload(var1);
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         var2.close();
      }

   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }
}
