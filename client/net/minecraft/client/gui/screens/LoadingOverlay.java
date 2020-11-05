package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class LoadingOverlay extends Overlay {
   private static final ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION = new ResourceLocation("textures/gui/title/mojangstudios.png");
   private static final int BRAND_BACKGROUND = FastColor.ARGB32.color(255, 239, 50, 61);
   private static final int BRAND_BACKGROUND_NO_ALPHA;
   private final Minecraft minecraft;
   private final ReloadInstance reload;
   private final Consumer<Optional<Throwable>> onFinish;
   private final boolean fadeIn;
   private float currentProgress;
   private long fadeOutStart = -1L;
   private long fadeInStart = -1L;

   public LoadingOverlay(Minecraft var1, ReloadInstance var2, Consumer<Optional<Throwable>> var3, boolean var4) {
      super();
      this.minecraft = var1;
      this.reload = var2;
      this.onFinish = var3;
      this.fadeIn = var4;
   }

   public static void registerTextures(Minecraft var0) {
      var0.getTextureManager().register((ResourceLocation)MOJANG_STUDIOS_LOGO_LOCATION, (AbstractTexture)(new LoadingOverlay.LogoTexture()));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      int var5 = this.minecraft.getWindow().getGuiScaledWidth();
      int var6 = this.minecraft.getWindow().getGuiScaledHeight();
      long var7 = Util.getMillis();
      if (this.fadeIn && (this.reload.isApplying() || this.minecraft.screen != null) && this.fadeInStart == -1L) {
         this.fadeInStart = var7;
      }

      float var9 = this.fadeOutStart > -1L ? (float)(var7 - this.fadeOutStart) / 1000.0F : -1.0F;
      float var10 = this.fadeInStart > -1L ? (float)(var7 - this.fadeInStart) / 500.0F : -1.0F;
      float var11;
      int var12;
      if (var9 >= 1.0F) {
         if (this.minecraft.screen != null) {
            this.minecraft.screen.render(var1, 0, 0, var4);
         }

         var12 = Mth.ceil((1.0F - Mth.clamp(var9 - 1.0F, 0.0F, 1.0F)) * 255.0F);
         fill(var1, 0, 0, var5, var6, BRAND_BACKGROUND_NO_ALPHA | var12 << 24);
         var11 = 1.0F - Mth.clamp(var9 - 1.0F, 0.0F, 1.0F);
      } else if (this.fadeIn) {
         if (this.minecraft.screen != null && var10 < 1.0F) {
            this.minecraft.screen.render(var1, var2, var3, var4);
         }

         var12 = Mth.ceil(Mth.clamp((double)var10, 0.15D, 1.0D) * 255.0D);
         fill(var1, 0, 0, var5, var6, BRAND_BACKGROUND_NO_ALPHA | var12 << 24);
         var11 = Mth.clamp(var10, 0.0F, 1.0F);
      } else {
         fill(var1, 0, 0, var5, var6, BRAND_BACKGROUND);
         var11 = 1.0F;
      }

      var12 = (int)((double)this.minecraft.getWindow().getGuiScaledWidth() * 0.5D);
      int var13 = (int)((double)this.minecraft.getWindow().getGuiScaledHeight() * 0.5D);
      double var14 = Math.min((double)this.minecraft.getWindow().getGuiScaledWidth() * 0.75D, (double)this.minecraft.getWindow().getGuiScaledHeight()) * 0.25D;
      int var16 = (int)(var14 * 0.5D);
      double var17 = var14 * 4.0D;
      int var19 = (int)(var17 * 0.5D);
      this.minecraft.getTextureManager().bind(MOJANG_STUDIOS_LOGO_LOCATION);
      RenderSystem.enableBlend();
      RenderSystem.blendEquation(32774);
      RenderSystem.blendFunc(770, 1);
      RenderSystem.alphaFunc(516, 0.0F);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, var11);
      blit(var1, var12 - var19, var13 - var16, var19, (int)var14, -0.0625F, 0.0F, 120, 60, 120, 120);
      blit(var1, var12, var13 - var16, var19, (int)var14, 0.0625F, 60.0F, 120, 60, 120, 120);
      RenderSystem.defaultBlendFunc();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.disableBlend();
      int var20 = (int)((double)this.minecraft.getWindow().getGuiScaledHeight() * 0.8325D);
      float var21 = this.reload.getActualProgress();
      this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + var21 * 0.050000012F, 0.0F, 1.0F);
      if (var9 < 1.0F) {
         this.drawProgressBar(var1, var5 / 2 - var19, var20 - 5, var5 / 2 + var19, var20 + 5, 1.0F - Mth.clamp(var9, 0.0F, 1.0F));
      }

      if (var9 >= 2.0F) {
         this.minecraft.setOverlay((Overlay)null);
      }

      if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || var10 >= 2.0F)) {
         try {
            this.reload.checkExceptions();
            this.onFinish.accept(Optional.empty());
         } catch (Throwable var23) {
            this.onFinish.accept(Optional.of(var23));
         }

         this.fadeOutStart = Util.getMillis();
         if (this.minecraft.screen != null) {
            this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
         }
      }

   }

   private void drawProgressBar(PoseStack var1, int var2, int var3, int var4, int var5, float var6) {
      int var7 = Mth.ceil((float)(var4 - var2 - 2) * this.currentProgress);
      int var8 = Math.round(var6 * 255.0F);
      int var9 = FastColor.ARGB32.color(var8, 255, 255, 255);
      fill(var1, var2 + 1, var3, var4 - 1, var3 + 1, var9);
      fill(var1, var2 + 1, var5, var4 - 1, var5 - 1, var9);
      fill(var1, var2, var3, var2 + 1, var5, var9);
      fill(var1, var4, var3, var4 - 1, var5, var9);
      fill(var1, var2 + 2, var3 + 2, var2 + var7, var5 - 2, var9);
   }

   public boolean isPauseScreen() {
      return true;
   }

   static {
      BRAND_BACKGROUND_NO_ALPHA = BRAND_BACKGROUND & 16777215;
   }

   static class LogoTexture extends SimpleTexture {
      public LogoTexture() {
         super(LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION);
      }

      protected SimpleTexture.TextureImage getTextureImage(ResourceManager var1) {
         Minecraft var2 = Minecraft.getInstance();
         VanillaPackResources var3 = var2.getClientPackSource().getVanillaPack();

         try {
            InputStream var4 = var3.getResource(PackType.CLIENT_RESOURCES, LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION);
            Throwable var5 = null;

            SimpleTexture.TextureImage var6;
            try {
               var6 = new SimpleTexture.TextureImage(new TextureMetadataSection(true, true), NativeImage.read(var4));
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
