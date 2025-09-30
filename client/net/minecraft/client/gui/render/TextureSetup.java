package net.minecraft.client.gui.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;

public record TextureSetup(@Nullable GpuTextureView texure0, @Nullable GpuTextureView texure1, @Nullable GpuTextureView texure2) {
   private static final TextureSetup NO_TEXTURE_SETUP = new TextureSetup((GpuTextureView)null, (GpuTextureView)null, (GpuTextureView)null);
   private static int sortKeySeed;

   public TextureSetup(@Nullable GpuTextureView var1, @Nullable GpuTextureView var2, @Nullable GpuTextureView var3) {
      super();
      this.texure0 = var1;
      this.texure1 = var2;
      this.texure2 = var3;
   }

   public static TextureSetup singleTexture(GpuTextureView var0) {
      return new TextureSetup(var0, (GpuTextureView)null, (GpuTextureView)null);
   }

   public static TextureSetup singleTextureWithLightmap(GpuTextureView var0) {
      return new TextureSetup(var0, (GpuTextureView)null, Minecraft.getInstance().gameRenderer.lightTexture().getTextureView());
   }

   public static TextureSetup doubleTexture(GpuTextureView var0, GpuTextureView var1) {
      return new TextureSetup(var0, var1, (GpuTextureView)null);
   }

   public static TextureSetup noTexture() {
      return NO_TEXTURE_SETUP;
   }

   public int getSortKey() {
      return SharedConstants.DEBUG_SHUFFLE_UI_RENDERING_ORDER ? this.hashCode() * (sortKeySeed + 1) : this.hashCode();
   }

   public static void updateSortKeySeed() {
      sortKeySeed = Math.round(100000.0F * (float)Math.random());
   }
}
