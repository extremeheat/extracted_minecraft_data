package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
            Iterator var6 = var5.getRecipes(var2.isFiltering((RecipeBookMenu)var1.player.containerMenu)).iterator();

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

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      if (this.animationTime > 0.0F) {
         float var5 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
         RenderSystem.scalef(1.0F, var5, 1.0F);
         RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
      }

      Minecraft var9 = Minecraft.getInstance();
      var9.getTextureManager().bind(this.resourceLocation);
      RenderSystem.disableDepthTest();
      int var6 = this.xTexStart;
      int var7 = this.yTexStart;
      if (this.isStateTriggered) {
         var6 += this.xDiffTex;
      }

      if (this.isHovered()) {
         var7 += this.yDiffTex;
      }

      int var8 = this.x;
      if (this.isStateTriggered) {
         var8 -= 2;
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.blit(var1, var8, this.y, var6, var7, this.width, this.height);
      RenderSystem.enableDepthTest();
      this.renderIcon(var9.getItemRenderer());
      if (this.animationTime > 0.0F) {
         RenderSystem.popMatrix();
         this.animationTime -= var4;
      }

   }

   private void renderIcon(ItemRenderer var1) {
      List var2 = this.category.getIconItems();
      int var3 = this.isStateTriggered ? -2 : 0;
      if (var2.size() == 1) {
         var1.renderAndDecorateFakeItem((ItemStack)var2.get(0), this.x + 9 + var3, this.y + 5);
      } else if (var2.size() == 2) {
         var1.renderAndDecorateFakeItem((ItemStack)var2.get(0), this.x + 3 + var3, this.y + 5);
         var1.renderAndDecorateFakeItem((ItemStack)var2.get(1), this.x + 14 + var3, this.y + 5);
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
