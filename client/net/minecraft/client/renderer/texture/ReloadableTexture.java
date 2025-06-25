package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class ReloadableTexture extends AbstractTexture {
   private final ResourceLocation resourceId;

   public ReloadableTexture(ResourceLocation var1) {
      super();
      this.resourceId = var1;
   }

   public ResourceLocation resourceId() {
      return this.resourceId;
   }

   public void apply(TextureContents var1) {
      boolean var2 = var1.clamp();
      boolean var3 = var1.blur();

      try (NativeImage var4 = var1.image()) {
         this.doLoad(var4, var3, var2);
      }

   }

   protected void doLoad(NativeImage var1, boolean var2, boolean var3) {
      GpuDevice var4 = RenderSystem.getDevice();
      this.close();
      ResourceLocation var10002 = this.resourceId;
      Objects.requireNonNull(var10002);
      this.texture = var4.createTexture(var10002::toString, 5, TextureFormat.RGBA8, var1.getWidth(), var1.getHeight(), 1, 1);
      this.textureView = var4.createTextureView(this.texture);
      this.setFilter(var2, false);
      this.setClamp(var3);
      var4.createCommandEncoder().writeToTexture(this.texture, var1);
   }

   public abstract TextureContents loadContents(ResourceManager var1) throws IOException;
}
