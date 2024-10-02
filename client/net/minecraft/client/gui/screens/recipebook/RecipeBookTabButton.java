package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public class RecipeBookTabButton extends StateSwitchingButton {
   private static final WidgetSprites SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("recipe_book/tab"), ResourceLocation.withDefaultNamespace("recipe_book/tab_selected")
   );
   private final RecipeBookComponent.TabInfo tabInfo;
   private static final float ANIMATION_TIME = 15.0F;
   private float animationTime;

   public RecipeBookTabButton(RecipeBookComponent.TabInfo var1) {
      super(0, 0, 35, 27, false);
      this.tabInfo = var1;
      this.initTextureValues(SPRITES);
   }

   public void startAnimation(ClientRecipeBook var1, boolean var2) {
      RecipeCollection.CraftableStatus var3 = var2 ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY;

      for (RecipeCollection var6 : var1.getCollection(this.tabInfo.category())) {
         for (RecipeDisplayEntry var8 : var6.getSelectedRecipes(var3)) {
            if (var1.willHighlight(var8.id())) {
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
      int var2 = this.isStateTriggered ? -2 : 0;
      if (this.tabInfo.secondaryIcon().isPresent()) {
         var1.renderFakeItem(this.tabInfo.primaryIcon(), this.getX() + 3 + var2, this.getY() + 5);
         var1.renderFakeItem(this.tabInfo.secondaryIcon().get(), this.getX() + 14 + var2, this.getY() + 5);
      } else {
         var1.renderFakeItem(this.tabInfo.primaryIcon(), this.getX() + 9 + var2, this.getY() + 5);
      }
   }

   public RecipeBookCategory getCategory() {
      return this.tabInfo.category();
   }

   public boolean updateVisibility(ClientRecipeBook var1) {
      List var2 = var1.getCollection(this.tabInfo.category());
      this.visible = false;

      for (RecipeCollection var4 : var2) {
         if (var4.hasAnySelected()) {
            this.visible = true;
            break;
         }
      }

      return this.visible;
   }
}
