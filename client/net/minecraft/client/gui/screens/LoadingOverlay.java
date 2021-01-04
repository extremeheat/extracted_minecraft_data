package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPack;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;

public class LoadingOverlay extends Overlay {
   private static final ResourceLocation MOJANG_LOGO_LOCATION = new ResourceLocation("textures/gui/title/mojang.png");
   private final Minecraft minecraft;
   private final ReloadInstance reload;
   private final Runnable onFinish;
   private final boolean fadeIn;
   private float currentProgress;
   private long fadeOutStart = -1L;
   private long fadeInStart = -1L;

   public LoadingOverlay(Minecraft var1, ReloadInstance var2, Runnable var3, boolean var4) {
      super();
      this.minecraft = var1;
      this.reload = var2;
      this.onFinish = var3;
      this.fadeIn = var4;
   }

   public static void registerTextures(Minecraft var0) {
      var0.getTextureManager().register((ResourceLocation)MOJANG_LOGO_LOCATION, (TextureObject)(new LoadingOverlay.LogoTexture()));
   }

   public void render(int var1, int var2, float var3) {
      int var4 = this.minecraft.window.getGuiScaledWidth();
      int var5 = this.minecraft.window.getGuiScaledHeight();
      long var6 = Util.getMillis();
      if (this.fadeIn && (this.reload.isApplying() || this.minecraft.screen != null) && this.fadeInStart == -1L) {
         this.fadeInStart = var6;
      }

      float var8 = this.fadeOutStart > -1L ? (float)(var6 - this.fadeOutStart) / 1000.0F : -1.0F;
      float var9 = this.fadeInStart > -1L ? (float)(var6 - this.fadeInStart) / 500.0F : -1.0F;
      float var10;
      int var11;
      if (var8 >= 1.0F) {
         if (this.minecraft.screen != null) {
            this.minecraft.screen.render(0, 0, var3);
         }

         var11 = Mth.ceil((1.0F - Mth.clamp(var8 - 1.0F, 0.0F, 1.0F)) * 255.0F);
         fill(0, 0, var4, var5, 16777215 | var11 << 24);
         var10 = 1.0F - Mth.clamp(var8 - 1.0F, 0.0F, 1.0F);
      } else if (this.fadeIn) {
         if (this.minecraft.screen != null && var9 < 1.0F) {
            this.minecraft.screen.render(var1, var2, var3);
         }

         var11 = Mth.ceil(Mth.clamp((double)var9, 0.15D, 1.0D) * 255.0D);
         fill(0, 0, var4, var5, 16777215 | var11 << 24);
         var10 = Mth.clamp(var9, 0.0F, 1.0F);
      } else {
         fill(0, 0, var4, var5, -1);
         var10 = 1.0F;
      }

      var11 = (this.minecraft.window.getGuiScaledWidth() - 256) / 2;
      int var12 = (this.minecraft.window.getGuiScaledHeight() - 256) / 2;
      this.minecraft.getTextureManager().bind(MOJANG_LOGO_LOCATION);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, var10);
      this.blit(var11, var12, 0, 0, 256, 256);
      float var13 = this.reload.getActualProgress();
      this.currentProgress = this.currentProgress * 0.95F + var13 * 0.050000012F;
      if (var8 < 1.0F) {
         this.drawProgressBar(var4 / 2 - 150, var5 / 4 * 3, var4 / 2 + 150, var5 / 4 * 3 + 10, this.currentProgress, 1.0F - Mth.clamp(var8, 0.0F, 1.0F));
      }

      if (var8 >= 2.0F) {
         this.minecraft.setOverlay((Overlay)null);
      }

      if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || var9 >= 2.0F)) {
         this.reload.checkExceptions();
         this.fadeOutStart = Util.getMillis();
         this.onFinish.run();
         if (this.minecraft.screen != null) {
            this.minecraft.screen.init(this.minecraft, this.minecraft.window.getGuiScaledWidth(), this.minecraft.window.getGuiScaledHeight());
         }
      }

   }

   private void drawProgressBar(int var1, int var2, int var3, int var4, float var5, float var6) {
      int var7 = Mth.ceil((float)(var3 - var1 - 2) * var5);
      fill(var1 - 1, var2 - 1, var3 + 1, var4 + 1, -16777216 | Math.round((1.0F - var6) * 255.0F) << 16 | Math.round((1.0F - var6) * 255.0F) << 8 | Math.round((1.0F - var6) * 255.0F));
      fill(var1, var2, var3, var4, -1);
      fill(var1 + 1, var2 + 1, var1 + var7, var4 - 1, -16777216 | (int)Mth.lerp(1.0F - var6, 226.0F, 255.0F) << 16 | (int)Mth.lerp(1.0F - var6, 40.0F, 255.0F) << 8 | (int)Mth.lerp(1.0F - var6, 55.0F, 255.0F));
   }

   public boolean isPauseScreen() {
      return true;
   }

   static class LogoTexture extends SimpleTexture {
      public LogoTexture() {
         super(LoadingOverlay.MOJANG_LOGO_LOCATION);
      }

      protected SimpleTexture.TextureImage getTextureImage(ResourceManager var1) {
         Minecraft var2 = Minecraft.getInstance();
         VanillaPack var3 = var2.getClientPackSource().getVanillaPack();

         try {
            InputStream var4 = var3.getResource(PackType.CLIENT_RESOURCES, LoadingOverlay.MOJANG_LOGO_LOCATION);
            Throwable var5 = null;

            SimpleTexture.TextureImage var6;
            try {
               var6 = new SimpleTexture.TextureImage((TextureMetadataSection)null, NativeImage.read(var4));
            } catch (Throwable var16) {
               var5 = var16;
               throw var16;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var15) {
                        var5.addSuppressed(var15);
                     }
                  } else {
                     var4.close();
                  }
               }

            }

            return var6;
         } catch (IOException var18) {
            return new SimpleTexture.TextureImage(var18);
         }
      }
   }
}
