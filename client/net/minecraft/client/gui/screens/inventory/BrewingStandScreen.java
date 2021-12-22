package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
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

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width((FormattedText)this.title)) / 2;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BREWING_STAND_LOCATION);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      int var7 = ((BrewingStandMenu)this.menu).getFuel();
      int var8 = Mth.clamp((int)((18 * var7 + 20 - 1) / 20), (int)0, (int)18);
      if (var8 > 0) {
         this.blit(var1, var5 + 60, var6 + 44, 176, 29, var8, 4);
      }

      int var9 = ((BrewingStandMenu)this.menu).getBrewingTicks();
      if (var9 > 0) {
         int var10 = (int)(28.0F * (1.0F - (float)var9 / 400.0F));
         if (var10 > 0) {
            this.blit(var1, var5 + 97, var6 + 16, 176, 0, 9, var10);
         }

         var10 = BUBBLELENGTHS[var9 / 2 % 7];
         if (var10 > 0) {
            this.blit(var1, var5 + 63, var6 + 14 + 29 - var10, 185, 29 - var10, 12, var10);
         }
      }

   }
}
