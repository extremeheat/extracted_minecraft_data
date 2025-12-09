package net.minecraft.client.gui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

public record TextureSetup(@Nullable GpuTextureView texure0, @Nullable GpuTextureView texure1, @Nullable GpuTextureView texure2, @Nullable GpuSampler sampler0, @Nullable GpuSampler sampler1, @Nullable GpuSampler sampler2) {
   private static final TextureSetup NO_TEXTURE_SETUP = new TextureSetup((GpuTextureView)null, (GpuTextureView)null, (GpuTextureView)null, (GpuSampler)null, (GpuSampler)null, (GpuSampler)null);
   private static int sortKeySeed;

   public TextureSetup(@Nullable GpuTextureView var1, @Nullable GpuTextureView var2, @Nullable GpuTextureView var3, @Nullable GpuSampler var4, @Nullable GpuSampler var5, @Nullable GpuSampler var6) {
      super();
      this.texure0 = var1;
      this.texure1 = var2;
      this.texure2 = var3;
      this.sampler0 = var4;
      this.sampler1 = var5;
      this.sampler2 = var6;
   }

   public static TextureSetup singleTexture(GpuTextureView var0, GpuSampler var1) {
      return new TextureSetup(var0, (GpuTextureView)null, (GpuTextureView)null, var1, (GpuSampler)null, (GpuSampler)null);
   }

   public static TextureSetup singleTextureWithLightmap(GpuTextureView var0, GpuSampler var1) {
      return new TextureSetup(var0, (GpuTextureView)null, Minecraft.getInstance().gameRenderer.lightTexture().getTextureView(), var1, (GpuSampler)null, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
   }

   public static TextureSetup doubleTexture(GpuTextureView var0, GpuSampler var1, GpuTextureView var2, GpuSampler var3) {
      return new TextureSetup(var0, var2, (GpuTextureView)null, var1, var3, (GpuSampler)null);
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
