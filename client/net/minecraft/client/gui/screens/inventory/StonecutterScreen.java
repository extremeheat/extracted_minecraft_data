package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import net.minecraft.client.Minecraft;
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
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex;
   private boolean displayRecipes;

   public StonecutterScreen(StonecutterMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      var1.registerUpdateListener(this::containerChanged);
   }

   public void render(int var1, int var2, float var3) {
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 8.0F, 4.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 94), 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      this.renderBackground();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int var4 = this.leftPos;
      int var5 = this.topPos;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      int var6 = (int)(41.0F * this.scrollOffs);
      this.blit(var4 + 119, var5 + 15 + var6, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
      int var7 = this.leftPos + 52;
      int var8 = this.topPos + 14;
      int var9 = this.startIndex + 12;
      this.renderButtons(var2, var3, var7, var8, var9);
      this.renderRecipes(var7, var8, var9);
   }

   private void renderButtons(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = this.startIndex; var6 < var5 && var6 < ((StonecutterMenu)this.menu).getNumRecipes(); ++var6) {
         int var7 = var6 - this.startIndex;
         int var8 = var3 + var7 % 4 * 16;
         int var9 = var7 / 4;
         int var10 = var4 + var9 * 18 + 2;
         int var11 = this.imageHeight;
         if (var6 == ((StonecutterMenu)this.menu).getSelectedRecipeIndex()) {
            var11 += 18;
         } else if (var1 >= var8 && var2 >= var10 && var1 < var8 + 16 && var2 < var10 + 18) {
            var11 += 36;
         }

         this.blit(var8, var10 - 1, 0, var11, 16, 18);
      }

   }

   private void renderRecipes(int var1, int var2, int var3) {
      Lighting.turnOnGui();
      List var4 = ((StonecutterMenu)this.menu).getRecipes();

      for(int var5 = this.startIndex; var5 < var3 && var5 < ((StonecutterMenu)this.menu).getNumRecipes(); ++var5) {
         int var6 = var5 - this.startIndex;
         int var7 = var1 + var6 % 4 * 16;
         int var8 = var6 / 4;
         int var9 = var2 + var8 * 18 + 2;
         this.minecraft.getItemRenderer().renderAndDecorateItem(((StonecutterRecipe)var4.get(var5)).getResultItem(), var7, var9);
      }

      Lighting.turnOff();
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
