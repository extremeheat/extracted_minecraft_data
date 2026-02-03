package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.crafting.RecipeHolder;

public abstract class RecipeBookMenu extends AbstractContainerMenu {
   public RecipeBookMenu(final MenuType<?> menuType, final int containerId) {
      super(menuType, containerId);
   }

   public abstract PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder<?> recipe, ServerLevel level, Inventory inventory);

   public abstract void fillCraftSlotsStackedContents(StackedItemContents stackedContents);

   public abstract RecipeBookType getRecipeBookType();

   public static enum PostPlaceAction {
      NOTHING,
      PLACE_GHOST_RECIPE;

      private PostPlaceAction() {
      }

      // $FF: synthetic method
      private static PostPlaceAction[] $values() {
         return new PostPlaceAction[]{NOTHING, PLACE_GHOST_RECIPE};
      }
   }
}
