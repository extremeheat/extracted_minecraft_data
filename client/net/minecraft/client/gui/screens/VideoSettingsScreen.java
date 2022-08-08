package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class VideoSettingsScreen extends OptionsSubScreen {
   private static final Component FABULOUS;
   private static final Component WARNING_MESSAGE;
   private static final Component WARNING_TITLE;
   private static final Component BUTTON_ACCEPT;
   private static final Component BUTTON_CANCEL;
   private OptionsList list;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final int oldMipmaps;

   private static OptionInstance<?>[] options(Options var0) {
      return new OptionInstance[]{var0.graphicsMode(), var0.renderDistance(), var0.prioritizeChunkUpdates(), var0.simulationDistance(), var0.ambientOcclusion(), var0.framerateLimit(), var0.enableVsync(), var0.bobView(), var0.guiScale(), var0.attackIndicator(), var0.gamma(), var0.cloudStatus(), var0.fullscreen(), var0.particles(), var0.mipmapLevels(), var0.entityShadows(), var0.screenEffectScale(), var0.entityDistanceScaling(), var0.fovEffectScale(), var0.showAutosaveIndicator()};
   }

   public VideoSettingsScreen(Screen var1, Options var2) {
      super(var1, var2, Component.translatable("options.videoTitle"));
      this.gpuWarnlistManager = var1.minecraft.getGpuWarnlistManager();
      this.gpuWarnlistManager.resetWarnings();
      if (var2.graphicsMode().get() == GraphicsStatus.FABULOUS) {
         this.gpuWarnlistManager.dismissWarning();
      }

      this.oldMipmaps = (Integer)var2.mipmapLevels().get();
   }

   protected void init() {
      this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
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
         } else {
            return var2x == -1 ? Options.genericValueLabel(var1x, Component.translatable("options.fullscreen.current")) : Options.genericValueLabel(var1x, Component.literal(var3.getMode(var2x).toString()));
         }
      }, new OptionInstance.IntRange(-1, var3 != null ? var3.getModeCount() - 1 : -1), var4, (var2x) -> {
         if (var3 != null) {
            var2.setPreferredFullscreenVideoMode(var2x == -1 ? Optional.empty() : Optional.of(var3.getMode(var2x)));
         }
      });
      this.list.addBig(var6);
      this.list.addBig(this.options.biomeBlendRadius());
      this.list.addSmall(options(this.options));
      this.addWidget(this.list);
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, (var2x) -> {
         this.minecraft.options.save();
         var2.changeFullscreenVideoMode();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      if ((Integer)this.options.mipmapLevels().get() != this.oldMipmaps) {
         this.minecraft.updateMaxMipLevel((Integer)this.options.mipmapLevels().get());
         this.minecraft.delayTextureReload();
      }

      super.removed();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = (Integer)this.options.guiScale().get();
      if (super.mouseClicked(var1, var3, var5)) {
         if ((Integer)this.options.guiScale().get() != var6) {
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

            this.minecraft.setScreen(new PopupScreen(WARNING_TITLE, var7, ImmutableList.of(new PopupScreen.ButtonOption(BUTTON_ACCEPT, (var1x) -> {
               this.options.graphicsMode().set(GraphicsStatus.FABULOUS);
               Minecraft.getInstance().levelRenderer.allChanged();
               this.gpuWarnlistManager.dismissWarning();
               this.minecraft.setScreen(this);
            }), new PopupScreen.ButtonOption(BUTTON_CANCEL, (var1x) -> {
               this.gpuWarnlistManager.dismissWarningAndSkipFabulous();
               this.minecraft.setScreen(this);
            }))));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      int var6 = (Integer)this.options.guiScale().get();
      if (super.mouseReleased(var1, var3, var5)) {
         return true;
      } else if (this.list.mouseReleased(var1, var3, var5)) {
         if ((Integer)this.options.guiScale().get() != var6) {
            this.minecraft.resizeDisplay();
         }

         return true;
      } else {
         return false;
      }
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.list.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 5, 16777215);
      super.render(var1, var2, var3, var4);
      List var5 = tooltipAt(this.list, var2, var3);
      this.renderTooltip(var1, var5, var2, var3);
   }

   static {
      FABULOUS = Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC);
      WARNING_MESSAGE = Component.translatable("options.graphics.warning.message", FABULOUS, FABULOUS);
      WARNING_TITLE = Component.translatable("options.graphics.warning.title").withStyle(ChatFormatting.RED);
      BUTTON_ACCEPT = Component.translatable("options.graphics.warning.accept");
      BUTTON_CANCEL = Component.translatable("options.graphics.warning.cancel");
   }
}
