package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class ReloadableTexture extends AbstractTexture {
   private final Identifier resourceId;

   public ReloadableTexture(Identifier var1) {
      super();
      this.resourceId = var1;
   }

   public Identifier resourceId() {
      return this.resourceId;
   }

   public void apply(TextureContents var1) {
      boolean var2 = var1.clamp();
      boolean var3 = var1.blur();
      AddressMode var4 = var2 ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT;
      FilterMode var5 = var3 ? FilterMode.LINEAR : FilterMode.NEAREST;
      this.sampler = RenderSystem.getSamplerCache().getSampler(var4, var4, var5, var5, false);

      try (NativeImage var6 = var1.image()) {
         this.doLoad(var6);
      }

   }

   protected void doLoad(NativeImage var1) {
      GpuDevice var2 = RenderSystem.getDevice();
      this.close();
      Identifier var10002 = this.resourceId;
      Objects.requireNonNull(var10002);
      this.texture = var2.createTexture(var10002::toString, 5, TextureFormat.RGBA8, var1.getWidth(), var1.getHeight(), 1, 1);
      this.textureView = var2.createTextureView(this.texture);
      var2.createCommandEncoder().writeToTexture(this.texture, var1);
   }

   public abstract TextureContents loadContents(ResourceManager var1) throws IOException;
}
