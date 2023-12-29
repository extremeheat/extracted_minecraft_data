package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VideoSettingsScreen extends OptionsSubScreen {
   private static final Component FABULOUS = Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC);
   private static final Component WARNING_MESSAGE = Component.translatable("options.graphics.warning.message", FABULOUS, FABULOUS);
   private static final Component WARNING_TITLE = Component.translatable("options.graphics.warning.title").withStyle(ChatFormatting.RED);
   private static final Component BUTTON_ACCEPT = Component.translatable("options.graphics.warning.accept");
   private static final Component BUTTON_CANCEL = Component.translatable("options.graphics.warning.cancel");
   private OptionsList list;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final int oldMipmaps;

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{
         var0.graphicsMode(),
         var0.renderDistance(),
         var0.prioritizeChunkUpdates(),
         var0.simulationDistance(),
         var0.ambientOcclusion(),
         var0.framerateLimit(),
         var0.enableVsync(),
         var0.bobView(),
         var0.guiScale(),
         var0.attackIndicator(),
         var0.gamma(),
         var0.cloudStatus(),
         var0.fullscreen(),
         var0.particles(),
         var0.mipmapLevels(),
         var0.entityShadows(),
         var0.screenEffectScale(),
         var0.entityDistanceScaling(),
         var0.fovEffectScale(),
         var0.showAutosaveIndicator(),
         var0.glintSpeed(),
         var0.glintStrength()
      };
   }

   public VideoSettingsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("options.videoTitle"));
      this.gpuWarnlistManager = var1.minecraft.getGpuWarnlistManager();
      this.gpuWarnlistManager.resetWarnings();
      if (var2.graphicsMode().get() == GraphicsStatus.FABULOUS) {
         this.gpuWarnlistManager.dismissWarning();
      }

      this.oldMipmaps = var2.mipmapLevels().get();
   }

   @Override
   protected void init() {
      this.list = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height - 64, 32, 25));
      boolean var1 = true;
      Window var2 = this.minecraft.getWindow();
      Monitor var3 = var2.findBestMonitor();
      int var4;
      if (var3 == null) {
         var4 = -1;
      } else {
         Optional var5 = var2.getPreferredFullscreenVideoMode();
         var4 = var5.map(var3::getVideoModeIndex).orElse(-1);
      }

      OptionInstance var6 = new OptionInstance<>(
         "options.fullscreen.resolution",
         OptionInstance.noTooltip(),
         (var1x, var2x) -> {
            if (var3 == null) {
               return Component.translatable("options.fullscreen.unavailable");
            } else if (var2x == -1) {
               return Options.genericValueLabel(var1x, Component.translatable("options.fullscreen.current"));
            } else {
               VideoMode var3xx = var3.getMode(var2x);
               return Options.genericValueLabel(
                  var1x,
                  Component.translatable(
                     "options.fullscreen.entry",
                     var3xx.getWidth(),
                     var3xx.getHeight(),
                     var3xx.getRefreshRate(),
                     var3xx.getRedBits() + var3xx.getGreenBits() + var3xx.getBlueBits()
                  )
               );
            }
         },
         new OptionInstance.IntRange(-1, var3 != null ? var3.getModeCount() - 1 : -1),
         var4,
         var2x -> {
            if (var3 != null) {
               var2.setPreferredFullscreenVideoMode(var2x == -1 ? Optional.empty() : Optional.of(var3.getMode(var2x)));
            }
         }
      );
      this.list.addBig(var6);
      this.list.addBig(this.options.biomeBlendRadius());
      this.list.addSmall(options(this.options));
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, var2x -> {
         this.minecraft.options.save();
         var2.changeFullscreenVideoMode();
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
   }

   @Override
   public void removed() {
      if (this.options.mipmapLevels().get() != this.oldMipmaps) {
         this.minecraft.updateMaxMipLevel(this.options.mipmapLevels().get());
         this.minecraft.delayTextureReload();
      }

      super.removed();
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = this.options.guiScale().get();
      if (super.mouseClicked(var1, var3, var5)) {
         if (this.options.guiScale().get() != var6) {
            this.minecraft.resizeDisplay();
         }

         if (this.gpuWarnlistManager.isShowingWarning()) {
            ArrayList var7 = Lists.newArrayList(new Component[]{WARNING_MESSAGE, CommonComponents.NEW_LINE});
            String var8 = this.gpuWarnlistManager.getRendererWarnings();
            if (var8 != null) {
               var7.add(CommonComponents.NEW_LINE);
               var7.add(Component.translatable("options.graphics.warning.renderer", var8).withStyle(ChatFormatting.GRAY));
            }

            String var9 = this.gpuWarnlistManager.getVendorWarnings();
            if (var9 != null) {
               var7.add(CommonComponents.NEW_LINE);
               var7.add(Component.translatable("options.graphics.warning.vendor", var9).withStyle(ChatFormatting.GRAY));
            }

            String var10 = this.gpuWarnlistManager.getVersionWarnings();
            if (var10 != null) {
               var7.add(CommonComponents.NEW_LINE);
               var7.add(Component.translatable("options.graphics.warning.version", var10).withStyle(ChatFormatting.GRAY));
            }

            this.minecraft
               .setScreen(
                  new UnsupportedGraphicsWarningScreen(
                     WARNING_TITLE, var7, ImmutableList.of(new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_ACCEPT, var1x -> {
                        this.options.graphicsMode().set(GraphicsStatus.FABULOUS);
                        Minecraft.getInstance().levelRenderer.allChanged();
                        this.gpuWarnlistManager.dismissWarning();
                        this.minecraft.setScreen(this);
                     }), new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_CANCEL, var1x -> {
                        this.gpuWarnlistManager.dismissWarningAndSkipFabulous();
                        this.minecraft.setScreen(this);
                     }))
                  )
               );
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (Screen.hasControlDown()) {
         OptionInstance var9 = this.options.guiScale();
         int var10 = var9.get() + (int)Math.signum(var7);
         if (var10 != 0) {
            var9.set(var10);
            if (var9.get() == var10) {
               this.minecraft.resizeDisplay();
               return true;
            }
         }

         return false;
      } else {
         return super.mouseScrolled(var1, var3, var5, var7);
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderDirtBackground(var1);
   }
}
