package net.minecraft.client.gui.screens;

import net.minecraft.client.FullscreenResolutionProgressOption;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

public class VideoSettingsScreen extends Screen {
   private final Screen lastScreen;
   private final Options options;
   private OptionsList list;
   private static final Option[] OPTIONS;
   private int oldMipmaps;

   public VideoSettingsScreen(Screen var1, Options var2) {
      super(new TranslatableComponent("options.videoTitle", new Object[0]));
      this.lastScreen = var1;
      this.options = var2;
   }

   protected void init() {
      this.oldMipmaps = this.options.mipmapLevels;
      this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addBig(new FullscreenResolutionProgressOption(this.minecraft.window));
      this.list.addSmall(OPTIONS);
      this.children.add(this.list);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.get("gui.done"), (var1) -> {
         this.minecraft.options.save();
         this.minecraft.window.changeFullscreenVideoMode();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      if (this.options.mipmapLevels != this.oldMipmaps) {
         this.minecraft.getTextureAtlas().setMaxMipLevel(this.options.mipmapLevels);
         this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
         this.minecraft.getTextureAtlas().setFilter(false, this.options.mipmapLevels > 0);
         this.minecraft.delayTextureReload();
      }

      this.minecraft.options.save();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = this.options.guiScale;
      if (super.mouseClicked(var1, var3, var5)) {
         if (this.options.guiScale != var6) {
            this.minecraft.resizeDisplay();
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

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.list.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 5, 16777215);
      super.render(var1, var2, var3);
   }

   static {
      OPTIONS = new Option[]{Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AMBIENT_OCCLUSION, Option.FRAMERATE_LIMIT, Option.ENABLE_VSYNC, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ATTACK_INDICATOR, Option.GAMMA, Option.RENDER_CLOUDS, Option.USE_FULLSCREEN, Option.PARTICLES, Option.MIPMAP_LEVELS, Option.ENTITY_SHADOWS, Option.BIOME_BLEND_RADIUS};
   }
}
