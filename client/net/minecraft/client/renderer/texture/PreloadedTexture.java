package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class PreloadedTexture extends SimpleTexture {
   @Nullable
   private CompletableFuture<SimpleTexture.TextureImage> future;

   public PreloadedTexture(ResourceManager var1, ResourceLocation var2, Executor var3) {
      super(var2);
      this.future = CompletableFuture.supplyAsync(() -> {
         return SimpleTexture.TextureImage.load(var1, var2);
      }, var3);
   }

   protected SimpleTexture.TextureImage getTextureImage(ResourceManager var1) {
      if (this.future != null) {
         SimpleTexture.TextureImage var2 = (SimpleTexture.TextureImage)this.future.join();
         this.future = null;
         return var2;
      } else {
         return SimpleTexture.TextureImage.load(var1, this.location);
      }
   }

   public CompletableFuture<Void> getFuture() {
      return this.future == null ? CompletableFuture.completedFuture((Object)null) : this.future.thenApply((var0) -> {
         return null;
      });
   }

   public void reset(TextureManager var1, ResourceManager var2, ResourceLocation var3, Executor var4) {
      this.future = CompletableFuture.supplyAsync(() -> {
         return SimpleTexture.TextureImage.load(var2, this.location);
      }, Util.backgroundExecutor());
      this.future.thenRunAsync(() -> {
         var1.register((ResourceLocation)this.location, (AbstractTexture)this);
      }, executor(var4));
   }

   private static Executor executor(Executor var0) {
      return (var1) -> {
         var0.execute(() -> {
            Objects.requireNonNull(var1);
            RenderSystem.recordRenderCall(var1::run);
         });
      };
   }
}
