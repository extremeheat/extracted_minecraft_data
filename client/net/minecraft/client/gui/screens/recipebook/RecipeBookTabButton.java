package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeBookTabButton extends StateSwitchingButton {
   private static final WidgetSprites SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("recipe_book/tab"), ResourceLocation.withDefaultNamespace("recipe_book/tab_selected")
   );
   private final RecipeBookCategories category;
   private static final float ANIMATION_TIME = 15.0F;
   private float animationTime;

   public RecipeBookTabButton(RecipeBookCategories var1) {
      super(0, 0, 35, 27, false);
      this.category = var1;
      this.initTextureValues(SPRITES);
   }

   public void startAnimation(ClientRecipeBook var1, boolean var2) {
      RecipeCollection.CraftableStatus var3 = var2 ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY;

      for (RecipeCollection var6 : var1.getCollection(this.category)) {
         for (RecipeHolder var8 : var6.getFittingRecipes(var3)) {
            if (var1.willHighlight(var8)) {
               this.animationTime = 15.0F;
               return;
            }
         }
      }
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.sprites != null) {
         if (this.animationTime > 0.0F) {
            float var5 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
            var1.pose().pushPose();
            var1.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12), 0.0F);
            var1.pose().scale(1.0F, var5, 1.0F);
            var1.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)), 0.0F);
         }

         ResourceLocation var7 = this.sprites.get(true, this.isStateTriggered);
         int var6 = this.getX();
         if (this.isStateTriggered) {
            var6 -= 2;
         }

         var1.blitSprite(RenderType::guiTextured, var7, var6, this.getY(), this.width, this.height);
         this.renderIcon(var1);
         if (this.animationTime > 0.0F) {
            var1.pose().popPose();
            this.animationTime -= var4;
         }
      }
   }

   private void renderIcon(GuiGraphics var1) {
      List var2 = this.category.getIconItems();
      int var3 = this.isStateTriggered ? -2 : 0;
      if (var2.size() == 1) {
         var1.renderFakeItem((ItemStack)var2.get(0), this.getX() + 9 + var3, this.getY() + 5);
      } else if (var2.size() == 2) {
         var1.renderFakeItem((ItemStack)var2.get(0), this.getX() + 3 + var3, this.getY() + 5);
         var1.renderFakeItem((ItemStack)var2.get(1), this.getX() + 14 + var3, this.getY() + 5);
      }
   }

   public RecipeBookCategories getCategory() {
      return this.category;
   }

   public boolean updateVisibility(ClientRecipeBook var1) {
      List var2 = var1.getCollection(this.category);
      this.visible = false;

      for (RecipeCollection var4 : var2) {
         if (var4.hasKnownRecipes() && var4.hasFitting()) {
            this.visible = true;
            break;
         }
      }

      return this.visible;
   }
}
