package net.minecraft.client.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class TextureAtlasHolder implements PreparableReloadListener, AutoCloseable {
   private final TextureAtlas textureAtlas;
   private final ResourceLocation atlasInfoLocation;

   public TextureAtlasHolder(TextureManager var1, ResourceLocation var2, ResourceLocation var3) {
      super();
      this.atlasInfoLocation = var3;
      this.textureAtlas = new TextureAtlas(var2);
      var1.register(this.textureAtlas.location(), this.textureAtlas);
   }

   protected TextureAtlasSprite getSprite(ResourceLocation var1) {
      return this.textureAtlas.getSprite(var1);
   }

   @Override
   public final CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      return SpriteLoader.create(this.textureAtlas)
         .loadAndStitch(var2, this.atlasInfoLocation, 0, var5)
         .thenCompose(SpriteLoader.Preparations::waitForUpload)
         .thenCompose(var1::wait)
         .thenAcceptAsync(var2x -> this.apply(var2x, var4), var6);
   }

   private void apply(SpriteLoader.Preparations var1, ProfilerFiller var2) {
      var2.startTick();
      var2.push("upload");
      this.textureAtlas.upload(var1);
      var2.pop();
      var2.endTick();
   }

   @Override
   public void close() {
      this.textureAtlas.clearTextureData();
   }
}
