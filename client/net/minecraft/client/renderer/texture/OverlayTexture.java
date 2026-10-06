package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.function.Supplier;
import net.minecraft.util.ARGB;

public class OverlayTexture implements AutoCloseable {
   private static final int SIZE = 16;
   public static final int NO_WHITE_U = 0;
   public static final int RED_OVERLAY_V = 3;
   public static final int WHITE_OVERLAY_V = 10;
   public static final int NO_OVERLAY = pack(0, 10);
   private final GpuTextureView textureView;

   public OverlayTexture() {
      super();
      GpuDevice device = RenderSystem.getDevice();
      GpuTexture texture = device.createTexture((Supplier)(() -> "Entity Color Overlay"), 5, GpuFormat.RGBA8_UNORM, 16, 16, 1, 1);
      this.textureView = device.createTextureView(texture);

      try (NativeImage pixels = new NativeImage(16, 16, false)) {
         for(int y = 0; y < 16; ++y) {
            for(int x = 0; x < 16; ++x) {
               if (y < 8) {
                  pixels.setPixel(x, y, -1291911168);
               } else {
                  int a = (int)((1.0F - (float)x / 15.0F * 0.75F) * 255.0F);
                  pixels.setPixel(x, y, ARGB.white(a));
               }
            }
         }

         pixels.writeToGpuTexture(device.createCommandEncoder(), texture);
      }

      texture.close();
   }

   public void close() {
      this.textureView.close();
   }

   public static int u(final float whiteOverlayProgress) {
      return (int)(whiteOverlayProgress * 15.0F);
   }

   public static int v(final boolean hurtOverlay) {
      return hurtOverlay ? 3 : 10;
   }

   public static int pack(final int u, final int v) {
      return u | v << 16;
   }

   public static int pack(final float whiteOverlayProgress, final boolean redOverlay) {
      return pack(u(whiteOverlayProgress), v(redOverlay));
   }

   public GpuTextureView getTextureView() {
      return this.textureView;
   }
}
