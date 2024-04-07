package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class LazyLoadedImage {
   private final ResourceLocation id;
   private final Resource resource;
   private final AtomicReference<NativeImage> image = new AtomicReference<>();
   private final AtomicInteger referenceCount;

   public LazyLoadedImage(ResourceLocation var1, Resource var2, int var3) {
      super();
      this.id = var1;
      this.resource = var2;
      this.referenceCount = new AtomicInteger(var3);
   }

   public NativeImage get() throws IOException {
      NativeImage var1 = this.image.get();
      if (var1 == null) {
         synchronized (this) {
            var1 = this.image.get();
            if (var1 == null) {
               try (InputStream var3 = this.resource.open()) {
                  var1 = NativeImage.read(var3);
                  this.image.set(var1);
               } catch (IOException var9) {
                  throw new IOException("Failed to load image " + this.id, var9);
               }
            }
         }
      }

      return var1;
   }

   public void release() {
      int var1 = this.referenceCount.decrementAndGet();
      if (var1 <= 0) {
         NativeImage var2 = this.image.getAndSet(null);
         if (var2 != null) {
            var2.close();
         }
      }
   }
}
