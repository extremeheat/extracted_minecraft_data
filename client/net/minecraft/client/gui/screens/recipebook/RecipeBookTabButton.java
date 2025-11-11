package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public class RecipeBookTabButton extends ImageButton {
   private static final WidgetSprites SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/tab"), Identifier.withDefaultNamespace("recipe_book/tab_selected"));
   public static final int WIDTH = 35;
   public static final int HEIGHT = 27;
   private final RecipeBookComponent.TabInfo tabInfo;
   private static final float ANIMATION_TIME = 15.0F;
   private float animationTime;
   private boolean selected = false;

   public RecipeBookTabButton(int var1, int var2, RecipeBookComponent.TabInfo var3, Button.OnPress var4) {
      super(var1, var2, 35, 27, SPRITES, var4);
      this.tabInfo = var3;
   }

   public void startAnimation(ClientRecipeBook var1, boolean var2) {
      RecipeCollection.CraftableStatus var3 = var2 ? RecipeCollection.CraftableStatus.CRAFTABLE : RecipeCollection.CraftableStatus.ANY;

      for(RecipeCollection var6 : var1.getCollection(this.tabInfo.category())) {
         for(RecipeDisplayEntry var8 : var6.getSelectedRecipes(var3)) {
            if (var1.willHighlight(var8.id())) {
               this.animationTime = 15.0F;
               return;
            }
         }
      }

   }

   public void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.animationTime > 0.0F) {
         float var5 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         var1.pose().pushMatrix();
         var1.pose().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
         var1.pose().scale(1.0F, var5);
         var1.pose().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
      }

      Identifier var7 = this.sprites.get(true, this.selected);
      int var6 = this.getX();
      if (this.selected) {
         var6 -= 2;
      }

      var1.blitSprite(RenderPipelines.GUI_TEXTURED, var7, var6, this.getY(), this.width, this.height);
      this.renderIcon(var1);
      if (this.animationTime > 0.0F) {
         var1.pose().popMatrix();
         this.animationTime -= var4;
      }

   }

   protected void handleCursor(GuiGraphics var1) {
      if (!this.selected) {
         super.handleCursor(var1);
      }

   }

   private void renderIcon(GuiGraphics var1) {
      int var2 = this.selected ? -2 : 0;
      if (this.tabInfo.secondaryIcon().isPresent()) {
         var1.renderFakeItem(this.tabInfo.primaryIcon(), this.getX() + 3 + var2, this.getY() + 5);
         var1.renderFakeItem((ItemStack)this.tabInfo.secondaryIcon().get(), this.getX() + 14 + var2, this.getY() + 5);
      } else {
         var1.renderFakeItem(this.tabInfo.primaryIcon(), this.getX() + 9 + var2, this.getY() + 5);
      }

   }

   public ExtendedRecipeBookCategory getCategory() {
      return this.tabInfo.category();
   }

   public boolean updateVisibility(ClientRecipeBook var1) {
      List var2 = var1.getCollection(this.tabInfo.category());
      this.visible = false;

      for(RecipeCollection var4 : var2) {
         if (var4.hasAnySelected()) {
            this.visible = true;
            break;
         }
      }

      return this.visible;
   }

   public void select() {
      this.selected = true;
   }

   public void unselect() {
      this.selected = false;
   }
}
