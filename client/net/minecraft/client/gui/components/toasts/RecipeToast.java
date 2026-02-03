package net.minecraft.client.gui.components.toasts;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class RecipeToast implements Toast {
   private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/recipe");
   private static final long DISPLAY_TIME = 5000L;
   private static final Component TITLE_TEXT = Component.translatable("recipe.toast.title");
   private static final Component DESCRIPTION_TEXT = Component.translatable("recipe.toast.description");
   private final List<Entry> recipeItems = new ArrayList();
   private long lastChanged;
   private boolean changed;
   private Toast.Visibility wantedVisibility;
   private int displayedRecipeIndex;

   private RecipeToast() {
      super();
      this.wantedVisibility = Toast.Visibility.HIDE;
   }

   public Toast.Visibility getWantedVisibility() {
      return this.wantedVisibility;
   }

   public void update(final ToastManager manager, final long fullyVisibleForMs) {
      if (this.changed) {
         this.lastChanged = fullyVisibleForMs;
         this.changed = false;
      }

      if (this.recipeItems.isEmpty()) {
         this.wantedVisibility = Toast.Visibility.HIDE;
      } else {
         this.wantedVisibility = (double)(fullyVisibleForMs - this.lastChanged) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      }

      this.displayedRecipeIndex = (int)((double)fullyVisibleForMs / Math.max(1.0, 5000.0 * manager.getNotificationDisplayTimeMultiplier() / (double)this.recipeItems.size()) % (double)this.recipeItems.size());
   }

   public void render(final GuiGraphics graphics, final Font font, final long fullyVisibleForMs) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      graphics.drawString(font, (Component)TITLE_TEXT, 30, 7, -11534256, false);
      graphics.drawString(font, (Component)DESCRIPTION_TEXT, 30, 18, -16777216, false);
      Entry items = (Entry)this.recipeItems.get(this.displayedRecipeIndex);
      graphics.pose().pushMatrix();
      graphics.pose().scale(0.6F, 0.6F);
      graphics.renderFakeItem(items.categoryItem(), 3, 3);
      graphics.pose().popMatrix();
      graphics.renderFakeItem(items.unlockedItem(), 8, 8);
   }

   private void addItem(final ItemStack craftingStation, final ItemStack unlockedItem) {
      this.recipeItems.add(new Entry(craftingStation, unlockedItem));
      this.changed = true;
   }

   public static void addOrUpdate(final ToastManager toastManager, final RecipeDisplay recipe) {
      RecipeToast toast = (RecipeToast)toastManager.getToast(RecipeToast.class, NO_TOKEN);
      if (toast == null) {
         toast = new RecipeToast();
         toastManager.addToast(toast);
      }

      ContextMap context = SlotDisplayContext.fromLevel(toastManager.getMinecraft().level);
      ItemStack categoryItem = recipe.craftingStation().resolveForFirstStack(context);
      ItemStack unlockedItem = recipe.result().resolveForFirstStack(context);
      toast.addItem(categoryItem, unlockedItem);
   }

   private static record Entry(ItemStack categoryItem, ItemStack unlockedItem) {
      private Entry {
         super();
      }
   }
}
