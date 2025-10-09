package net.minecraft.client.gui.screens.options;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VideoSettingsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.videoTitle");
   private static final Component IMPROVED_TRANSPARENCY;
   private static final Component WARNING_MESSAGE;
   private static final Component WARNING_TITLE;
   private static final Component BUTTON_ACCEPT;
   private static final Component BUTTON_CANCEL;
   private static final Component DISPLAY_HEADER;
   private static final Component QUALITY_HEADER;
   private static final Component INTERFACE_HEADER;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final int oldMipmaps;

   private static OptionInstance<?>[] qualityOptions(Options var0) {
      return new OptionInstance[]{var0.biomeBlendRadius(), var0.renderDistance(), var0.prioritizeChunkUpdates(), var0.simulationDistance(), var0.ambientOcclusion(), var0.cloudStatus(), var0.particles(), var0.mipmapLevels(), var0.entityShadows(), var0.entityDistanceScaling(), var0.menuBackgroundBlurriness(), var0.cloudRange(), var0.cutoutLeaves(), var0.improvedTransparency(), var0.weatherRadius()};
   }

   private static OptionInstance<?>[] displayOptions(Options var0) {
      return new OptionInstance[]{var0.framerateLimit(), var0.enableVsync(), var0.inactivityFpsLimit(), var0.guiScale(), var0.fullscreen(), var0.gamma()};
   }

   private static OptionInstance<?>[] interfaceOptions(Options var0) {
      return new OptionInstance[]{var0.showAutosaveIndicator(), var0.vignette(), var0.attackIndicator()};
   }

   public VideoSettingsScreen(Screen var1, Minecraft var2, Options var3) {
      super(var1, var3, TITLE);
      this.gpuWarnlistManager = var2.getGpuWarnlistManager();
      this.gpuWarnlistManager.resetWarnings();
      if ((Boolean)var3.improvedTransparency().get()) {
         this.gpuWarnlistManager.dismissWarning();
      }

      this.oldMipmaps = (Integer)var3.mipmapLevels().get();
   }

   protected void addOptions() {
      boolean var1 = true;
      Window var2 = this.minecraft.getWindow();
      Monitor var3 = var2.findBestMonitor();
      int var4;
      if (var3 == null) {
         var4 = -1;
      } else {
         Optional var5 = var2.getPreferredFullscreenVideoMode();
         Objects.requireNonNull(var3);
         var4 = (Integer)var5.map(var3::getVideoModeIndex).orElse(-1);
      }

      OptionInstance var6 = new OptionInstance("options.fullscreen.resolution", OptionInstance.noTooltip(), (var1x, var2x) -> {
         if (var3 == null) {
            return Component.translatable("options.fullscreen.unavailable");
         } else if (var2x == -1) {
            return Options.genericValueLabel(var1x, Component.translatable("options.fullscreen.current"));
         } else {
            VideoMode var3x = var3.getMode(var2x);
            return Options.genericValueLabel(var1x, Component.translatable("options.fullscreen.entry", var3x.getWidth(), var3x.getHeight(), var3x.getRefreshRate(), var3x.getRedBits() + var3x.getGreenBits() + var3x.getBlueBits()));
         }
      }, new OptionInstance.IntRange(-1, var3 != null ? var3.getModeCount() - 1 : -1), var4, (var2x) -> {
         if (var3 != null) {
            var2.setPreferredFullscreenVideoMode(var2x == -1 ? Optional.empty() : Optional.of(var3.getMode(var2x)));
         }
      });
      this.list.addHeader(DISPLAY_HEADER);
      this.list.addBig(var6);
      this.list.addSmall(displayOptions(this.options));
      this.list.addHeader(QUALITY_HEADER);
      this.list.addBig(this.options.graphicsPreset());
      this.list.addSmall(qualityOptions(this.options));
      this.list.addHeader(INTERFACE_HEADER);
      this.list.addSmall(interfaceOptions(this.options));
   }

   public void onClose() {
      this.minecraft.getWindow().changeFullscreenVideoMode();
      super.onClose();
   }

   public void removed() {
      if ((Integer)this.options.mipmapLevels().get() != this.oldMipmaps) {
         this.minecraft.updateMaxMipLevel((Integer)this.options.mipmapLevels().get());
         this.minecraft.delayTextureReload();
      }

      super.removed();
   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      if (super.mouseClicked(var1, var2)) {
         if (this.gpuWarnlistManager.isShowingWarning()) {
            ArrayList var3 = Lists.newArrayList(new Component[]{WARNING_MESSAGE, CommonComponents.NEW_LINE});
            String var4 = this.gpuWarnlistManager.getRendererWarnings();
            if (var4 != null) {
               var3.add(CommonComponents.NEW_LINE);
               var3.add(Component.translatable("options.graphics.warning.renderer", var4).withStyle(ChatFormatting.GRAY));
            }

            String var5 = this.gpuWarnlistManager.getVendorWarnings();
            if (var5 != null) {
               var3.add(CommonComponents.NEW_LINE);
               var3.add(Component.translatable("options.graphics.warning.vendor", var5).withStyle(ChatFormatting.GRAY));
            }

            String var6 = this.gpuWarnlistManager.getVersionWarnings();
            if (var6 != null) {
               var3.add(CommonComponents.NEW_LINE);
               var3.add(Component.translatable("options.graphics.warning.version", var6).withStyle(ChatFormatting.GRAY));
            }

            this.minecraft.setScreen(new UnsupportedGraphicsWarningScreen(WARNING_TITLE, var3, ImmutableList.of(new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_ACCEPT, (var1x) -> {
               this.options.improvedTransparency().set(true);
               Minecraft.getInstance().levelRenderer.allChanged();
               this.gpuWarnlistManager.dismissWarning();
               this.minecraft.setScreen(this);
            }), new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_CANCEL, (var1x) -> {
               this.gpuWarnlistManager.dismissWarning();
               this.options.improvedTransparency().set(false);
               this.updateTransparencyButton();
               this.minecraft.setScreen(this);
            }))));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.minecraft.hasControlDown()) {
         OptionInstance var9 = this.options.guiScale();
         OptionInstance.ValueSet var11 = var9.values();
         if (var11 instanceof OptionInstance.ClampingLazyMaxIntRange) {
            OptionInstance.ClampingLazyMaxIntRange var10 = (OptionInstance.ClampingLazyMaxIntRange)var11;
            int var15 = (Integer)var9.get();
            int var12 = var15 == 0 ? var10.maxInclusive() + 1 : var15;
            int var13 = var12 + (int)Math.signum(var7);
            if (var13 != 0 && var13 <= var10.maxInclusive() && var13 >= var10.minInclusive()) {
               CycleButton var14 = (CycleButton)this.list.findOption(var9);
               if (var14 != null) {
                  var9.set(var13);
                  var14.setValue(var13);
                  this.list.setScrollAmount(0.0);
                  return true;
               }
            }
         }

         return false;
      } else {
         return super.mouseScrolled(var1, var3, var5, var7);
      }
   }

   public void updateFullscreenButton(boolean var1) {
      if (this.list != null) {
         AbstractWidget var2 = this.list.findOption(this.options.fullscreen());
         if (var2 != null) {
            CycleButton var3 = (CycleButton)var2;
            var3.setValue(var1);
         }
      }

   }

   public void updateTransparencyButton() {
      if (this.list != null) {
         OptionInstance var1 = this.options.improvedTransparency();
         AbstractWidget var2 = this.list.findOption(var1);
         if (var2 != null) {
            CycleButton var3 = (CycleButton)var2;
            var3.setValue((Boolean)var1.get());
         }
      }

   }

   static {
      IMPROVED_TRANSPARENCY = Component.translatable("options.improvedTransparency").withStyle(ChatFormatting.ITALIC);
      WARNING_MESSAGE = Component.translatable("options.graphics.warning.message", IMPROVED_TRANSPARENCY, IMPROVED_TRANSPARENCY);
      WARNING_TITLE = Component.translatable("options.graphics.warning.title").withStyle(ChatFormatting.RED);
      BUTTON_ACCEPT = Component.translatable("options.graphics.warning.accept");
      BUTTON_CANCEL = Component.translatable("options.graphics.warning.cancel");
      DISPLAY_HEADER = Component.translatable("options.video.display.header");
      QUALITY_HEADER = Component.translatable("options.video.quality.header");
      INTERFACE_HEADER = Component.translatable("options.video.interface.header");
   }
}
