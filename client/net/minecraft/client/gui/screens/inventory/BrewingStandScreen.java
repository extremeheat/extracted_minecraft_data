package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;

public class BrewingStandScreen extends AbstractContainerScreen<BrewingStandMenu> {
   private static final ResourceLocation FUEL_LENGTH_SPRITE = ResourceLocation.withDefaultNamespace("container/brewing_stand/fuel_length");
   private static final ResourceLocation BREW_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/brewing_stand/brew_progress");
   private static final ResourceLocation BUBBLES_SPRITE = ResourceLocation.withDefaultNamespace("container/brewing_stand/bubbles");
   private static final ResourceLocation BREWING_STAND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/brewing_stand.png");
   private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

   public BrewingStandScreen(BrewingStandMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width((FormattedText)this.title)) / 2;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(BREWING_STAND_LOCATION, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      int var7 = ((BrewingStandMenu)this.menu).getFuel();
      int var8 = Mth.clamp((18 * var7 + 20 - 1) / 20, 0, 18);
      if (var8 > 0) {
         var1.blitSprite(FUEL_LENGTH_SPRITE, 18, 4, 0, 0, var5 + 60, var6 + 44, var8, 4);
      }

      int var9 = ((BrewingStandMenu)this.menu).getBrewingTicks();
      if (var9 > 0) {
         int var10 = (int)(28.0F * (1.0F - (float)var9 / 400.0F));
         if (var10 > 0) {
            var1.blitSprite(BREW_PROGRESS_SPRITE, 9, 28, 0, 0, var5 + 97, var6 + 16, 9, var10);
         }

         var10 = BUBBLELENGTHS[var9 / 2 % 7];
         if (var10 > 0) {
            var1.blitSprite(BUBBLES_SPRITE, 12, 29, 0, 29 - var10, var5 + 63, var6 + 14 + 29 - var10, 12, var10);
         }
      }

   }
}
