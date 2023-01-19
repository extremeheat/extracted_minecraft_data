package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
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
      this.future = CompletableFuture.supplyAsync(() -> SimpleTexture.TextureImage.load(var1, var2), var3);
   }

   @Override
   protected SimpleTexture.TextureImage getTextureImage(ResourceManager var1) {
      if (this.future != null) {
         SimpleTexture.TextureImage var2 = this.future.join();
         this.future = null;
         return var2;
      } else {
         return SimpleTexture.TextureImage.load(var1, this.location);
      }
   }

   public CompletableFuture<Void> getFuture() {
      return this.future == null ? CompletableFuture.completedFuture(null) : this.future.thenApply(var0 -> null);
   }

   @Override
   public void reset(TextureManager var1, ResourceManager var2, ResourceLocation var3, Executor var4) {
      this.future = CompletableFuture.supplyAsync(() -> SimpleTexture.TextureImage.load(var2, this.location), Util.backgroundExecutor());
      this.future.thenRunAsync(() -> var1.register(this.location, this), executor(var4));
   }

   private static Executor executor(Executor var0) {
      return var1 -> var0.execute(() -> RenderSystem.recordRenderCall(var1::run));
   }
}
