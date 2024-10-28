package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeBookTabButton extends StateSwitchingButton {
   private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/tab"), ResourceLocation.withDefaultNamespace("recipe_book/tab_selected"));
   private final RecipeBookCategories category;
   private static final float ANIMATION_TIME = 15.0F;
   private float animationTime;

   public RecipeBookTabButton(RecipeBookCategories var1) {
      super(0, 0, 35, 27, false);
      this.category = var1;
      this.initTextureValues(SPRITES);
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
               RecipeHolder var7 = (RecipeHolder)var6.next();
               if (var2.willHighlight(var7)) {
                  this.animationTime = 15.0F;
                  return;
               }
            }
         }

      }
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.sprites != null) {
         if (this.animationTime > 0.0F) {
            float var5 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
            var1.pose().pushPose();
            var1.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
            var1.pose().scale(1.0F, var5, 1.0F);
            var1.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
         }

         Minecraft var8 = Minecraft.getInstance();
         RenderSystem.disableDepthTest();
         ResourceLocation var6 = this.sprites.get(true, this.isStateTriggered);
         int var7 = this.getX();
         if (this.isStateTriggered) {
            var7 -= 2;
         }

         var1.blitSprite(var6, var7, this.getY(), this.width, this.height);
         RenderSystem.enableDepthTest();
         this.renderIcon(var1, var8.getItemRenderer());
         if (this.animationTime > 0.0F) {
            var1.pose().popPose();
            this.animationTime -= var4;
         }

      }
   }

   private void renderIcon(GuiGraphics var1, ItemRenderer var2) {
      List var3 = this.category.getIconItems();
      int var4 = this.isStateTriggered ? -2 : 0;
      if (var3.size() == 1) {
         var1.renderFakeItem((ItemStack)var3.get(0), this.getX() + 9 + var4, this.getY() + 5);
      } else if (var3.size() == 2) {
         var1.renderFakeItem((ItemStack)var3.get(0), this.getX() + 3 + var4, this.getY() + 5);
         var1.renderFakeItem((ItemStack)var3.get(1), this.getX() + 14 + var4, this.getY() + 5);
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
