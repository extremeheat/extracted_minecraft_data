package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookTabButton extends StateSwitchingButton {
   private final RecipeBookCategories category;
   private float animationTime;

   public RecipeBookTabButton(RecipeBookCategories var1) {
      super(0, 0, 35, 27, false);
      this.category = var1;
      this.initTextureValues(153, 2, 35, 0, RecipeBookComponent.RECIPE_BOOK_LOCATION);
   }

   public void startAnimation(Minecraft var1) {
      ClientRecipeBook var2 = var1.player.getRecipeBook();
      List var3 = var2.getCollection(this.category);
      if (var1.player.containerMenu instanceof RecipeBookMenu) {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            RecipeCollection var5 = (RecipeCollection)var4.next();
            Iterator var6 = var5.getRecipes(var2.isFilteringCraftable((RecipeBookMenu)var1.player.containerMenu)).iterator();

            while(var6.hasNext()) {
               Recipe var7 = (Recipe)var6.next();
               if (var2.willHighlight(var7)) {
                  this.animationTime = 15.0F;
                  return;
               }
            }
         }

      }
   }

   public void renderButton(int var1, int var2, float var3) {
      if (this.animationTime > 0.0F) {
         float var4 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
         GlStateManager.scalef(1.0F, var4, 1.0F);
         GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
      }

      Minecraft var8 = Minecraft.getInstance();
      var8.getTextureManager().bind(this.resourceLocation);
      GlStateManager.disableDepthTest();
      int var5 = this.xTexStart;
      int var6 = this.yTexStart;
      if (this.isStateTriggered) {
         var5 += this.xDiffTex;
      }

      if (this.isHovered()) {
         var6 += this.yDiffTex;
      }

      int var7 = this.x;
      if (this.isStateTriggered) {
         var7 -= 2;
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.blit(var7, this.y, var5, var6, this.width, this.height);
      GlStateManager.enableDepthTest();
      Lighting.turnOnGui();
      GlStateManager.disableLighting();
      this.renderIcon(var8.getItemRenderer());
      GlStateManager.enableLighting();
      Lighting.turnOff();
      if (this.animationTime > 0.0F) {
         GlStateManager.popMatrix();
         this.animationTime -= var3;
      }

   }

   private void renderIcon(ItemRenderer var1) {
      List var2 = this.category.getIconItems();
      int var3 = this.isStateTriggered ? -2 : 0;
      if (var2.size() == 1) {
         var1.renderAndDecorateItem((ItemStack)var2.get(0), this.x + 9 + var3, this.y + 5);
      } else if (var2.size() == 2) {
         var1.renderAndDecorateItem((ItemStack)var2.get(0), this.x + 3 + var3, this.y + 5);
         var1.renderAndDecorateItem((ItemStack)var2.get(1), this.x + 14 + var3, this.y + 5);
      }

   }

   public RecipeBookCategories getCategory() {
      return this.category;
   }

   public boolean updateVisibility(ClientRecipeBook var1) {
      List var2 = var1.getCollection(this.category);
      this.visible = false;
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            RecipeCollection var4 = (RecipeCollection)var3.next();
            if (var4.hasKnownRecipes() && var4.hasFitting()) {
               this.visible = true;
               break;
            }
         }
      }

      return this.visible;
   }
}
