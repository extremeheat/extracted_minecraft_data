package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class LoadingOverlay extends Overlay {
   public static final ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/title/mojangstudios.png");
   private static final int LOGO_BACKGROUND_COLOR = ARGB.color(255, 239, 50, 61);
   private static final int LOGO_BACKGROUND_COLOR_DARK = ARGB.color(255, 0, 0, 0);
   private static final IntSupplier BRAND_BACKGROUND = () -> (Boolean)Minecraft.getInstance().options.darkMojangStudiosBackground().get() ? LOGO_BACKGROUND_COLOR_DARK : LOGO_BACKGROUND_COLOR;
   private static final int LOGO_SCALE = 240;
   private static final float LOGO_QUARTER_FLOAT = 60.0F;
   private static final int LOGO_QUARTER = 60;
   private static final int LOGO_HALF = 120;
   private static final float LOGO_OVERLAP = 0.0625F;
   private static final float SMOOTHING = 0.95F;
   public static final long FADE_OUT_TIME = 1000L;
   public static final long FADE_IN_TIME = 500L;
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

   public static void registerTextures(TextureManager var0) {
      var0.registerAndLoad(MOJANG_STUDIOS_LOGO_LOCATION, new LogoTexture());
   }

   private static int replaceAlpha(int var0, int var1) {
      return var0 & 16777215 | var1 << 24;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = var1.guiWidth();
      int var6 = var1.guiHeight();
      long var7 = Util.getMillis();
      if (this.fadeIn && this.fadeInStart == -1L) {
         this.fadeInStart = var7;
      }

      float var9 = this.fadeOutStart > -1L ? (float)(var7 - this.fadeOutStart) / 1000.0F : -1.0F;
      float var10 = this.fadeInStart > -1L ? (float)(var7 - this.fadeInStart) / 500.0F : -1.0F;
      float var11;
      if (var9 >= 1.0F) {
         if (this.minecraft.screen != null) {
            this.minecraft.screen.render(var1, 0, 0, var4);
         }

         int var12 = Mth.ceil((1.0F - Mth.clamp(var9 - 1.0F, 0.0F, 1.0F)) * 255.0F);
         var1.fill(RenderType.guiOverlay(), 0, 0, var5, var6, replaceAlpha(BRAND_BACKGROUND.getAsInt(), var12));
         var11 = 1.0F - Mth.clamp(var9 - 1.0F, 0.0F, 1.0F);
      } else if (this.fadeIn) {
         if (this.minecraft.screen != null && var10 < 1.0F) {
            this.minecraft.screen.render(var1, var2, var3, var4);
         }

         int var25 = Mth.ceil(Mth.clamp((double)var10, 0.15, 1.0) * 255.0);
         var1.fill(RenderType.guiOverlay(), 0, 0, var5, var6, replaceAlpha(BRAND_BACKGROUND.getAsInt(), var25));
         var11 = Mth.clamp(var10, 0.0F, 1.0F);
      } else {
         int var26 = BRAND_BACKGROUND.getAsInt();
         float var13 = (float)(var26 >> 16 & 255) / 255.0F;
         float var14 = (float)(var26 >> 8 & 255) / 255.0F;
         float var15 = (float)(var26 & 255) / 255.0F;
         GlStateManager._clearColor(var13, var14, var15, 1.0F);
         GlStateManager._clear(16384);
         var11 = 1.0F;
      }

      int var27 = (int)((double)var1.guiWidth() * 0.5);
      int var28 = (int)((double)var1.guiHeight() * 0.5);
      double var29 = Math.min((double)var1.guiWidth() * 0.75, (double)var1.guiHeight()) * 0.25;
      int var16 = (int)(var29 * 0.5);
      double var17 = var29 * 4.0;
      int var19 = (int)(var17 * 0.5);
      int var20 = ARGB.white(var11);
      var1.blit((var0) -> RenderType.mojangLogo(), MOJANG_STUDIOS_LOGO_LOCATION, var27 - var19, var28 - var16, -0.0625F, 0.0F, var19, (int)var29, 120, 60, 120, 120, var20);
      var1.blit((var0) -> RenderType.mojangLogo(), MOJANG_STUDIOS_LOGO_LOCATION, var27, var28 - var16, 0.0625F, 60.0F, var19, (int)var29, 120, 60, 120, 120, var20);
      int var21 = (int)((double)var1.guiHeight() * 0.8325);
      float var22 = this.reload.getActualProgress();
      this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + var22 * 0.050000012F, 0.0F, 1.0F);
      if (var9 < 1.0F) {
         this.drawProgressBar(var1, var5 / 2 - var19, var21 - 5, var5 / 2 + var19, var21 + 5, 1.0F - Mth.clamp(var9, 0.0F, 1.0F));
      }

      if (var9 >= 2.0F) {
         this.minecraft.setOverlay((Overlay)null);
      }

      if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || var10 >= 2.0F)) {
         try {
            this.reload.checkExceptions();
            this.onFinish.accept(Optional.empty());
         } catch (Throwable var24) {
            this.onFinish.accept(Optional.of(var24));
         }

         this.fadeOutStart = Util.getMillis();
         if (this.minecraft.screen != null) {
            this.minecraft.screen.init(this.minecraft, var1.guiWidth(), var1.guiHeight());
         }
      }

   }

   private void drawProgressBar(GuiGraphics var1, int var2, int var3, int var4, int var5, float var6) {
      int var7 = Mth.ceil((float)(var4 - var2 - 2) * this.currentProgress);
      int var8 = Math.round(var6 * 255.0F);
      int var9 = ARGB.color(var8, 255, 255, 255);
      var1.fill(var2 + 2, var3 + 2, var2 + var7, var5 - 2, var9);
      var1.fill(var2 + 1, var3, var4 - 1, var3 + 1, var9);
      var1.fill(var2 + 1, var5, var4 - 1, var5 - 1, var9);
      var1.fill(var2, var3, var2 + 1, var5, var9);
      var1.fill(var4, var3, var4 - 1, var5, var9);
   }

   public boolean isPauseScreen() {
      return true;
   }

   static class LogoTexture extends ReloadableTexture {
      public LogoTexture() {
         super(LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION);
      }

      public TextureContents loadContents(ResourceManager var1) throws IOException {
         ResourceProvider var2 = Minecraft.getInstance().getVanillaPackResources().asProvider();
         InputStream var3 = var2.open(LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION);

         TextureContents var4;
         try {
            var4 = new TextureContents(NativeImage.read(var3), new TextureMetadataSection(true, true));
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }

         return var4;
      }
   }
}
