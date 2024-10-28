package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VideoSettingsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.videoTitle");
   private static final Component FABULOUS;
   private static final Component WARNING_MESSAGE;
   private static final Component WARNING_TITLE;
   private static final Component BUTTON_ACCEPT;
   private static final Component BUTTON_CANCEL;
   private OptionsList list;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final int oldMipmaps;

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{var0.graphicsMode(), var0.renderDistance(), var0.prioritizeChunkUpdates(), var0.simulationDistance(), var0.ambientOcclusion(), var0.framerateLimit(), var0.enableVsync(), var0.bobView(), var0.guiScale(), var0.attackIndicator(), var0.gamma(), var0.cloudStatus(), var0.fullscreen(), var0.particles(), var0.mipmapLevels(), var0.entityShadows(), var0.screenEffectScale(), var0.entityDistanceScaling(), var0.fovEffectScale(), var0.showAutosaveIndicator(), var0.glintSpeed(), var0.glintStrength(), var0.menuBackgroundBlurriness()};
   }

   public VideoSettingsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
      this.gpuWarnlistManager = var1.minecraft.getGpuWarnlistManager();
      this.gpuWarnlistManager.resetWarnings();
      if (var2.graphicsMode().get() == GraphicsStatus.FABULOUS) {
         this.gpuWarnlistManager.dismissWarning();
      }

      this.oldMipmaps = (Integer)var2.mipmapLevels().get();
   }

   protected void init() {
      this.list = (OptionsList)this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height, this));
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
      this.list.addBig(var6);
      this.list.addBig(this.options.biomeBlendRadius());
      this.list.addSmall(options(this.options));
      super.init();
   }

   public void onClose() {
      this.minecraft.getWindow().changeFullscreenVideoMode();
      super.onClose();
   }

   protected void repositionElements() {
      super.repositionElements();
      this.list.updateSize(this.width, this.layout);
   }

   public void removed() {
      if ((Integer)this.options.mipmapLevels().get() != this.oldMipmaps) {
         this.minecraft.updateMaxMipLevel((Integer)this.options.mipmapLevels().get());
         this.minecraft.delayTextureReload();
      }

      super.removed();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         if (this.gpuWarnlistManager.isShowingWarning()) {
            ArrayList var6 = Lists.newArrayList(new Component[]{WARNING_MESSAGE, CommonComponents.NEW_LINE});
            String var7 = this.gpuWarnlistManager.getRendererWarnings();
            if (var7 != null) {
               var6.add(CommonComponents.NEW_LINE);
               var6.add(Component.translatable("options.graphics.warning.renderer", var7).withStyle(ChatFormatting.GRAY));
            }

            String var8 = this.gpuWarnlistManager.getVendorWarnings();
            if (var8 != null) {
               var6.add(CommonComponents.NEW_LINE);
               var6.add(Component.translatable("options.graphics.warning.vendor", var8).withStyle(ChatFormatting.GRAY));
            }

            String var9 = this.gpuWarnlistManager.getVersionWarnings();
            if (var9 != null) {
               var6.add(CommonComponents.NEW_LINE);
               var6.add(Component.translatable("options.graphics.warning.version", var9).withStyle(ChatFormatting.GRAY));
            }

            this.minecraft.setScreen(new UnsupportedGraphicsWarningScreen(WARNING_TITLE, var6, ImmutableList.of(new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_ACCEPT, (var1x) -> {
               this.options.graphicsMode().set(GraphicsStatus.FABULOUS);
               Minecraft.getInstance().levelRenderer.allChanged();
               this.gpuWarnlistManager.dismissWarning();
               this.minecraft.setScreen(this);
            }), new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_CANCEL, (var1x) -> {
               this.gpuWarnlistManager.dismissWarningAndSkipFabulous();
               this.minecraft.setScreen(this);
            }))));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (Screen.hasControlDown()) {
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

   static {
      FABULOUS = Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC);
      WARNING_MESSAGE = Component.translatable("options.graphics.warning.message", FABULOUS, FABULOUS);
      WARNING_TITLE = Component.translatable("options.graphics.warning.title").withStyle(ChatFormatting.RED);
      BUTTON_ACCEPT = Component.translatable("options.graphics.warning.accept");
      BUTTON_CANCEL = Component.translatable("options.graphics.warning.cancel");
   }
}