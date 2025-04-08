package net.minecraft.client.gui.render;

import com.mojang.blaze3d.textures.GpuTexture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public record TextureSetup(@Nullable GpuTexture texure0, @Nullable GpuTexture texure1, @Nullable GpuTexture texure2) {
   private static final TextureSetup NO_TEXTURE_SETUP = new TextureSetup((GpuTexture)null, (GpuTexture)null, (GpuTexture)null);

   public TextureSetup(@Nullable GpuTexture var1, @Nullable GpuTexture var2, @Nullable GpuTexture var3) {
      super();
      this.texure0 = var1;
      this.texure1 = var2;
      this.texure2 = var3;
   }

   public static TextureSetup singleTexture(GpuTexture var0) {
      return new TextureSetup(var0, (GpuTexture)null, (GpuTexture)null);
   }

   public static TextureSetup singleTextureWithLightmap(GpuTexture var0) {
      return new TextureSetup(var0, (GpuTexture)null, Minecraft.getInstance().gameRenderer.lightTexture().getTexture());
   }

   public static TextureSetup doubleTexture(GpuTexture var0, GpuTexture var1) {
      return new TextureSetup(var0, var1, (GpuTexture)null);
   }

   public static TextureSetup noTexture() {
      return NO_TEXTURE_SETUP;
   }
}
