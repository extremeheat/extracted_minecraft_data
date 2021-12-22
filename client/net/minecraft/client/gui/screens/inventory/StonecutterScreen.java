package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class StonecutterScreen extends AbstractContainerScreen<StonecutterMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/stonecutter.png");
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private static final int RECIPES_COLUMNS = 4;
   private static final int RECIPES_ROWS = 3;
   private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
   private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
   private static final int SCROLLER_FULL_HEIGHT = 54;
   private static final int RECIPES_X = 52;
   private static final int RECIPES_Y = 14;
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex;
   private boolean displayRecipes;

   public StonecutterScreen(StonecutterMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      var1.registerUpdateListener(this::containerChanged);
      --this.titleLabelY;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      this.renderBackground(var1);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BG_LOCATION);
      int var5 = this.leftPos;
      int var6 = this.topPos;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      int var7 = (int)(41.0F * this.scrollOffs);
      this.blit(var1, var5 + 119, var6 + 15 + var7, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
      int var8 = this.leftPos + 52;
      int var9 = this.topPos + 14;
      int var10 = this.startIndex + 12;
      this.renderButtons(var1, var3, var4, var8, var9, var10);
      this.renderRecipes(var8, var9, var10);
   }

   protected void renderTooltip(PoseStack var1, int var2, int var3) {
      super.renderTooltip(var1, var2, var3);
      if (this.displayRecipes) {
         int var4 = this.leftPos + 52;
         int var5 = this.topPos + 14;
         int var6 = this.startIndex + 12;
         List var7 = ((StonecutterMenu)this.menu).getRecipes();

         for(int var8 = this.startIndex; var8 < var6 && var8 < ((StonecutterMenu)this.menu).getNumRecipes(); ++var8) {
            int var9 = var8 - this.startIndex;
            int var10 = var4 + var9 % 4 * 16;
            int var11 = var5 + var9 / 4 * 18 + 2;
            if (var2 >= var10 && var2 < var10 + 16 && var3 >= var11 && var3 < var11 + 18) {
               this.renderTooltip(var1, ((StonecutterRecipe)var7.get(var8)).getResultItem(), var2, var3);
            }
         }
      }

   }

   private void renderButtons(PoseStack var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = this.startIndex; var7 < var6 && var7 < ((StonecutterMenu)this.menu).getNumRecipes(); ++var7) {
         int var8 = var7 - this.startIndex;
         int var9 = var4 + var8 % 4 * 16;
         int var10 = var8 / 4;
         int var11 = var5 + var10 * 18 + 2;
         int var12 = this.imageHeight;
         if (var7 == ((StonecutterMenu)this.menu).getSelectedRecipeIndex()) {
            var12 += 18;
         } else if (var2 >= var9 && var3 >= var11 && var2 < var9 + 16 && var3 < var11 + 18) {
            var12 += 36;
         }

         this.blit(var1, var9, var11 - 1, 0, var12, 16, 18);
      }

   }

   private void renderRecipes(int var1, int var2, int var3) {
      List var4 = ((StonecutterMenu)this.menu).getRecipes();

      for(int var5 = this.startIndex; var5 < var3 && var5 < ((StonecutterMenu)this.menu).getNumRecipes(); ++var5) {
         int var6 = var5 - this.startIndex;
         int var7 = var1 + var6 % 4 * 16;
         int var8 = var6 / 4;
         int var9 = var2 + var8 * 18 + 2;
         this.minecraft.getItemRenderer().renderAndDecorateItem(((StonecutterRecipe)var4.get(var5)).getResultItem(), var7, var9);
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.scrolling = false;
      if (this.displayRecipes) {
         int var6 = this.leftPos + 52;
         int var7 = this.topPos + 14;
         int var8 = this.startIndex + 12;

         for(int var9 = this.startIndex; var9 < var8; ++var9) {
            int var10 = var9 - this.startIndex;
            double var11 = var1 - (double)(var6 + var10 % 4 * 16);
            double var13 = var3 - (double)(var7 + var10 / 4 * 18);
            if (var11 >= 0.0D && var13 >= 0.0D && var11 < 16.0D && var13 < 18.0D && ((StonecutterMenu)this.menu).clickMenuButton(this.minecraft.player, var9)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick(((StonecutterMenu)this.menu).containerId, var9);
               return true;
            }
         }

         var6 = this.leftPos + 119;
         var7 = this.topPos + 9;
         if (var1 >= (double)var6 && var1 < (double)(var6 + 12) && var3 >= (double)var7 && var3 < (double)(var7 + 54)) {
            this.scrolling = true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.scrolling && this.isScrollBarActive()) {
         int var10 = this.topPos + 14;
         int var11 = var10 + 54;
         this.scrollOffs = ((float)var3 - (float)var10 - 7.5F) / ((float)(var11 - var10) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5D) * 4;
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (this.isScrollBarActive()) {
         int var7 = this.getOffscreenRows();
         this.scrollOffs = (float)((double)this.scrollOffs - var5 / (double)var7);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = (int)((double)(this.scrollOffs * (float)var7) + 0.5D) * 4;
      }

      return true;
   }

   private boolean isScrollBarActive() {
      return this.displayRecipes && ((StonecutterMenu)this.menu).getNumRecipes() > 12;
   }

   protected int getOffscreenRows() {
      return (((StonecutterMenu)this.menu).getNumRecipes() + 4 - 1) / 4 - 3;
   }

   private void containerChanged() {
      this.displayRecipes = ((StonecutterMenu)this.menu).hasInputItem();
      if (!this.displayRecipes) {
         this.scrollOffs = 0.0F;
         this.startIndex = 0;
      }

   }
}
