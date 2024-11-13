package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
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
      this.defaultBlur = var3;
      NativeImage var4 = var1.image();
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> this.doLoad(var4, var3, var2));
      } else {
         this.doLoad(var4, var3, var2);
      }

   }

   private void doLoad(NativeImage var1, boolean var2, boolean var3) {
      TextureUtil.prepareImage(this.getId(), 0, var1.getWidth(), var1.getHeight());
      var1.upload(0, 0, 0, 0, 0, var1.getWidth(), var1.getHeight(), var2, var3, false, true);
   }

   public abstract TextureContents loadContents(ResourceManager var1) throws IOException;
}
