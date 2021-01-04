package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;

public class BrewingStandScreen extends AbstractContainerScreen<BrewingStandMenu> {
   private static final ResourceLocation BREWING_STAND_LOCATION = new ResourceLocation("textures/gui/container/brewing_stand.png");
   private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

   public BrewingStandScreen(BrewingStandMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), (float)(this.imageWidth / 2 - this.font.width(this.title.getColoredString()) / 2), 6.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BREWING_STAND_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      int var6 = ((BrewingStandMenu)this.menu).getFuel();
      int var7 = Mth.clamp((18 * var6 + 20 - 1) / 20, 0, 18);
      if (var7 > 0) {
         this.blit(var4 + 60, var5 + 44, 176, 29, var7, 4);
      }

      int var8 = ((BrewingStandMenu)this.menu).getBrewingTicks();
      if (var8 > 0) {
         int var9 = (int)(28.0F * (1.0F - (float)var8 / 400.0F));
         if (var9 > 0) {
            this.blit(var4 + 97, var5 + 16, 176, 0, 9, var9);
         }

         var9 = BUBBLELENGTHS[var8 / 2 % 7];
         if (var9 > 0) {
            this.blit(var4 + 63, var5 + 14 + 29 - var9, 185, 29 - var9, 12, var9);
         }
      }

   }
}
