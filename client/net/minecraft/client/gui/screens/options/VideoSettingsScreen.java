package net.minecraft.client.gui.screens.options;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.TextureFilteringMethod;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class VideoSettingsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.videoTitle");
   private static final Component IMPROVED_TRANSPARENCY;
   private static final Component WARNING_MESSAGE;
   private static final Component WARNING_TITLE;
   private static final Component BUTTON_ACCEPT;
   private static final Component BUTTON_CANCEL;
   private static final Component DISPLAY_HEADER;
   private static final Component QUALITY_HEADER;
   private static final Component PREFERENCES_HEADER;
   private static final Component GRAPHICS_API_REQUIRES_RESTART;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final int oldMipmaps;
   private final int oldAnisotropyBit;
   private final TextureFilteringMethod oldTextureFiltering;
   private final LinearLayout header = LinearLayout.vertical().spacing(2);
   private @Nullable StringWidget restartWarning;

   private static OptionInstance<?>[] qualityOptions(final Options options) {
      return new OptionInstance[]{options.biomeBlendRadius(), options.renderDistance(), options.prioritizeChunkUpdates(), options.simulationDistance(), options.ambientOcclusion(), options.cloudStatus(), options.particles(), options.mipmapLevels(), options.entityShadows(), options.entityDistanceScaling(), options.menuBackgroundBlurriness(), options.cloudRange(), options.cutoutLeaves(), options.improvedTransparency(), options.textureFiltering(), options.maxAnisotropyBit(), options.weatherRadius()};
   }

   private static OptionInstance<?>[] displayOptions(final Options options) {
      return new OptionInstance[]{options.framerateLimit(), options.enableVsync(), options.inactivityFpsLimit(), options.guiScale(), options.fullscreen(), options.exclusiveFullscreen(), options.gamma(), options.preferredGraphicsBackend()};
   }

   private static OptionInstance<?>[] preferenceOptions(final Options options) {
      return new OptionInstance[]{options.showAutosaveIndicator(), options.vignette(), options.attackIndicator(), options.chunkSectionFadeInTime()};
   }

   public VideoSettingsScreen(final Screen lastScreen, final Minecraft minecraft, final Options options) {
      super(lastScreen, options, TITLE);
      this.gpuWarnlistManager = minecraft.getGpuWarnlistManager();
      this.gpuWarnlistManager.resetWarnings();
      if ((Boolean)options.improvedTransparency().get()) {
         this.gpuWarnlistManager.dismissWarning();
      }

      this.oldMipmaps = (Integer)options.mipmapLevels().get();
      this.oldAnisotropyBit = (Integer)options.maxAnisotropyBit().get();
      this.oldTextureFiltering = (TextureFilteringMethod)options.textureFiltering().get();
   }

   protected void addOptions() {
      int CURRENT_MODE = -1;
      Window window = this.minecraft.getWindow();
      Monitor monitor = window.findBestMonitor();
      int initialValue;
      if (monitor == null) {
         initialValue = -1;
      } else {
         Optional<VideoMode> preferredFullscreenVideoMode = window.getPreferredFullscreenVideoMode();
         Objects.requireNonNull(monitor);
         initialValue = (Integer)preferredFullscreenVideoMode.map(monitor::getVideoModeIndex).orElse(-1);
      }

      OptionInstance<Integer> fullscreenOption = new OptionInstance<Integer>("options.fullscreen.resolution", OptionInstance.noTooltip(), (caption, value) -> {
         if (monitor == null) {
            return Component.translatable("options.fullscreen.unavailable");
         } else if (value == -1) {
            return Options.genericValueLabel(caption, Component.translatable("options.fullscreen.current"));
         } else {
            VideoMode mode = monitor.getMode(value);
            return Options.genericValueLabel(caption, Component.translatable("options.fullscreen.entry", mode.getWidth(), mode.getHeight(), mode.getRefreshRate(), mode.getRedBits() + mode.getGreenBits() + mode.getBlueBits()));
         }
      }, new OptionInstance.IntRange(-1, monitor != null ? monitor.getModeCount() - 1 : -1), initialValue, (value) -> {
         if (monitor != null) {
            window.setPreferredFullscreenVideoMode(value == -1 ? Optional.empty() : Optional.of(monitor.getMode(value)));
         }
      });
      this.list.addHeader(DISPLAY_HEADER);
      this.list.addBig(fullscreenOption);
      this.list.addSmall(displayOptions(this.options));
      this.list.addHeader(QUALITY_HEADER);
      this.list.addBig(this.options.graphicsPreset());
      this.list.addSmall(qualityOptions(this.options));
      this.list.addHeader(PREFERENCES_HEADER);
      this.list.addSmall(preferenceOptions(this.options));
   }

   protected void addTitle() {
      this.header.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
      this.header.addChild(new StringWidget(this.title, this.font));
      if (this.options.hasPreferredGraphicsBackendChanged()) {
         this.restartWarning = new StringWidget(GRAPHICS_API_REQUIRES_RESTART, this.font);
         this.header.addChild(this.restartWarning);
      }

      this.layout.addToHeader(this.header);
   }

   public void tick() {
      if (this.list != null) {
         AbstractWidget var2 = this.list.findOption(this.options.maxAnisotropyBit());
         if (var2 instanceof AbstractSliderButton) {
            AbstractSliderButton maxAnisotropy = (AbstractSliderButton)var2;
            maxAnisotropy.active = this.options.textureFiltering().get() == TextureFilteringMethod.ANISOTROPIC;
         }
      }

      boolean needsRestart = this.options.hasPreferredGraphicsBackendChanged();
      if (needsRestart && (this.restartWarning == null || !this.restartWarning.visible)) {
         if (this.restartWarning == null) {
            this.restartWarning = new StringWidget(GRAPHICS_API_REQUIRES_RESTART, this.font);
            this.header.addChild(this.restartWarning);
            this.addRenderableWidget(this.restartWarning);
         }

         this.restartWarning.visible = true;
         this.repositionElements();
      } else if (!needsRestart && this.restartWarning != null && this.restartWarning.visible) {
         this.restartWarning.visible = false;
         this.repositionElements();
      }

      super.tick();
   }

   public void onClose() {
      this.minecraft.getWindow().changeFullscreenVideoMode();
      super.onClose();
   }

   public void removed() {
      if ((Integer)this.options.mipmapLevels().get() != this.oldMipmaps || (Integer)this.options.maxAnisotropyBit().get() != this.oldAnisotropyBit || this.options.textureFiltering().get() != this.oldTextureFiltering) {
         this.minecraft.updateMaxMipLevel((Integer)this.options.mipmapLevels().get());
         this.minecraft.delayTextureReload();
      }

      super.removed();
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (super.mouseClicked(event, doubleClick)) {
         if (this.gpuWarnlistManager.isShowingWarning()) {
            List<Component> warningMessage = Lists.newArrayList(new Component[]{WARNING_MESSAGE, CommonComponents.NEW_LINE});
            String rendererWarnings = this.gpuWarnlistManager.getRendererWarnings();
            if (rendererWarnings != null) {
               warningMessage.add(CommonComponents.NEW_LINE);
               warningMessage.add(Component.translatable("options.graphics.warning.renderer", rendererWarnings).withStyle(ChatFormatting.GRAY));
            }

            String vendorWarnings = this.gpuWarnlistManager.getVendorWarnings();
            if (vendorWarnings != null) {
               warningMessage.add(CommonComponents.NEW_LINE);
               warningMessage.add(Component.translatable("options.graphics.warning.vendor", vendorWarnings).withStyle(ChatFormatting.GRAY));
            }

            String versionWarnings = this.gpuWarnlistManager.getVersionWarnings();
            if (versionWarnings != null) {
               warningMessage.add(CommonComponents.NEW_LINE);
               warningMessage.add(Component.translatable("options.graphics.warning.version", versionWarnings).withStyle(ChatFormatting.GRAY));
            }

            this.minecraft.gui.setScreen(new UnsupportedGraphicsWarningScreen(WARNING_TITLE, warningMessage, ImmutableList.of(new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_ACCEPT, (btn) -> {
               this.options.improvedTransparency().set(true);
               Minecraft.getInstance().levelRenderer.allChanged();
               this.gpuWarnlistManager.dismissWarning();
               this.minecraft.gui.setScreen(this);
            }), new UnsupportedGraphicsWarningScreen.ButtonOption(BUTTON_CANCEL, (btn) -> {
               this.gpuWarnlistManager.dismissWarning();
               this.options.improvedTransparency().set(false);
               this.updateTransparencyButton();
               this.minecraft.gui.setScreen(this);
            }))));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      if (this.minecraft.hasControlDown()) {
         OptionInstance<Integer> guiScale = this.options.guiScale();
         OptionInstance.ValueSet var11 = guiScale.values();
         if (var11 instanceof OptionInstance.ClampingLazyMaxIntRange) {
            OptionInstance.ClampingLazyMaxIntRange clampingLazyMaxIntRange = (OptionInstance.ClampingLazyMaxIntRange)var11;
            int oldValue = (Integer)guiScale.get();
            int adjustedOldValue = oldValue == 0 ? clampingLazyMaxIntRange.maxInclusive() + 1 : oldValue;
            int newValue = adjustedOldValue + (int)Math.signum(scrollY);
            if (newValue != 0 && newValue <= clampingLazyMaxIntRange.maxInclusive() && newValue >= clampingLazyMaxIntRange.minInclusive()) {
               CycleButton<Integer> cycleButton = (CycleButton)this.list.findOption(guiScale);
               if (cycleButton != null) {
                  guiScale.set(newValue);
                  cycleButton.setValue(newValue);
                  this.list.setScrollAmount(0.0);
                  return true;
               }
            }
         }

         return false;
      } else {
         return super.mouseScrolled(x, y, scrollX, scrollY);
      }
   }

   public void updateFullscreenButton(final boolean fullscreen) {
      if (this.list != null) {
         AbstractWidget fullscreenWidget = this.list.findOption(this.options.fullscreen());
         if (fullscreenWidget != null) {
            CycleButton<Boolean> fullscreenButton = (CycleButton)fullscreenWidget;
            fullscreenButton.setValue(fullscreen);
         }
      }

   }

   public void updateTransparencyButton() {
      if (this.list != null) {
         OptionInstance<Boolean> option = this.options.improvedTransparency();
         AbstractWidget widget = this.list.findOption(option);
         if (widget != null) {
            CycleButton<Boolean> button = (CycleButton)widget;
            button.setValue(option.get());
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
      PREFERENCES_HEADER = Component.translatable("options.video.preferences.header");
      GRAPHICS_API_REQUIRES_RESTART = Component.translatable("options.graphicsApi.restart").withColor(-2142128);
   }
}
