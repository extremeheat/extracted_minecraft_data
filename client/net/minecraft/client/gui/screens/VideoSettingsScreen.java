package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.FullscreenResolutionProgressOption;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class VideoSettingsScreen extends OptionsSubScreen {
   private static final Component FABULOUS;
   private static final Component WARNING_MESSAGE;
   private static final Component WARNING_TITLE;
   private static final Component BUTTON_ACCEPT;
   private static final Component BUTTON_CANCEL;
   private static final Component NEW_LINE;
   private static final Option[] OPTIONS;
   private OptionsList list;
   private final GpuWarnlistManager gpuWarnlistManager;
   private final int oldMipmaps;

   public VideoSettingsScreen(Screen var1, Options var2) {
      super(var1, var2, new TranslatableComponent("options.videoTitle"));
      this.gpuWarnlistManager = var1.minecraft.getGpuWarnlistManager();
      this.gpuWarnlistManager.resetWarnings();
      if (var2.graphicsMode == GraphicsStatus.FABULOUS) {
         this.gpuWarnlistManager.dismissWarning();
      }

      this.oldMipmaps = var2.mipmapLevels;
   }

   protected void init() {
      this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addBig(new FullscreenResolutionProgressOption(this.minecraft.getWindow()));
      this.list.addBig(Option.BIOME_BLEND_RADIUS);
      this.list.addSmall(OPTIONS);
      this.children.add(this.list);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, (var1) -> {
         this.minecraft.options.save();
         this.minecraft.getWindow().changeFullscreenVideoMode();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      if (this.options.mipmapLevels != this.oldMipmaps) {
         this.minecraft.updateMaxMipLevel(this.options.mipmapLevels);
         this.minecraft.delayTextureReload();
      }

      super.removed();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = this.options.guiScale;
      if (super.mouseClicked(var1, var3, var5)) {
         if (this.options.guiScale != var6) {
            this.minecraft.resizeDisplay();
         }

         if (this.gpuWarnlistManager.isShowingWarning()) {
            ArrayList var7 = Lists.newArrayList(new FormattedText[]{WARNING_MESSAGE, NEW_LINE});
            String var8 = this.gpuWarnlistManager.getRendererWarnings();
            if (var8 != null) {
               var7.add(NEW_LINE);
               var7.add((new TranslatableComponent("options.graphics.warning.renderer", new Object[]{var8})).withStyle(ChatFormatting.GRAY));
            }

            String var9 = this.gpuWarnlistManager.getVendorWarnings();
            if (var9 != null) {
               var7.add(NEW_LINE);
               var7.add((new TranslatableComponent("options.graphics.warning.vendor", new Object[]{var9})).withStyle(ChatFormatting.GRAY));
            }

            String var10 = this.gpuWarnlistManager.getVersionWarnings();
            if (var10 != null) {
               var7.add(NEW_LINE);
               var7.add((new TranslatableComponent("options.graphics.warning.version", new Object[]{var10})).withStyle(ChatFormatting.GRAY));
            }

            this.minecraft.setScreen(new PopupScreen(WARNING_TITLE, var7, ImmutableList.of(new PopupScreen.ButtonOption(BUTTON_ACCEPT, (var1x) -> {
               this.options.graphicsMode = GraphicsStatus.FABULOUS;
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
      int var6 = this.options.guiScale;
      if (super.mouseReleased(var1, var3, var5)) {
         return true;
      } else if (this.list.mouseReleased(var1, var3, var5)) {
         if (this.options.guiScale != var6) {
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
      if (var5 != null) {
         this.renderTooltip(var1, var5, var2, var3);
      }

   }

   static {
      FABULOUS = (new TranslatableComponent("options.graphics.fabulous")).withStyle(ChatFormatting.ITALIC);
      WARNING_MESSAGE = new TranslatableComponent("options.graphics.warning.message", new Object[]{FABULOUS, FABULOUS});
      WARNING_TITLE = (new TranslatableComponent("options.graphics.warning.title")).withStyle(ChatFormatting.RED);
      BUTTON_ACCEPT = new TranslatableComponent("options.graphics.warning.accept");
      BUTTON_CANCEL = new TranslatableComponent("options.graphics.warning.cancel");
      NEW_LINE = new TextComponent("\n");
      OPTIONS = new Option[]{Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AMBIENT_OCCLUSION, Option.FRAMERATE_LIMIT, Option.ENABLE_VSYNC, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ATTACK_INDICATOR, Option.GAMMA, Option.RENDER_CLOUDS, Option.USE_FULLSCREEN, Option.PARTICLES, Option.MIPMAP_LEVELS, Option.ENTITY_SHADOWS, Option.SCREEN_EFFECTS_SCALE, Option.ENTITY_DISTANCE_SCALING, Option.FOV_EFFECTS_SCALE};
   }
}
