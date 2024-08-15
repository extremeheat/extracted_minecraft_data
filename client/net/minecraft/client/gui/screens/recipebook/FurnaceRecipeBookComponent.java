package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import java.util.Optional;
import java.util.SequencedSet;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.FuelValues;

public class FurnaceRecipeBookComponent extends RecipeBookComponent<AbstractFurnaceMenu> {
   private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
   );
   private final Component recipeFilterName;
   @Nullable
   private List<ItemStack> fuels;

   public FurnaceRecipeBookComponent(AbstractFurnaceMenu var1, Component var2) {
      super(var1);
      this.recipeFilterName = var2;
   }

   @Override
   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_SPRITES);
   }

   @Override
   protected boolean isCraftingSlot(Slot var1) {
      return switch (var1.index) {
         case 0, 1, 2 -> true;
         default -> false;
      };
   }

   @Override
   protected void setupGhostRecipeSlots(GhostSlots var1, RecipeHolder<?> var2) {
      ClientLevel var3 = this.minecraft.level;
      ItemStack var4 = var2.value().getResultItem(var3.registryAccess());
      Slot var5 = this.menu.getResultSlot();
      var1.addResult(var4, var5);
      List var6 = var2.value().placementInfo().slotInfo();
      if (!var6.isEmpty()) {
         ((Optional)var6.getFirst()).ifPresent(var2x -> {
            Slot var3x = this.menu.slots.get(0);
            var1.addIngredient(var2x.possibleItems(), var3x);
         });
      }

      Slot var7 = this.menu.slots.get(1);
      if (var7.getItem().isEmpty()) {
         if (var6.size() > 1) {
            ((Optional)var6.get(1)).ifPresent(var2x -> var1.addIngredient(var2x.possibleItems(), var7));
         } else {
            if (this.fuels == null) {
               this.fuels = this.getFuelItems(var3.fuelValues()).stream().map(ItemStack::new).toList();
            }

            var1.addIngredient(this.fuels, var7);
         }
      }
   }

   private SequencedSet<Item> getFuelItems(FuelValues var1) {
      return var1.fuelItems();
   }

   @Override
   protected Component getRecipeFilterName() {
      return this.recipeFilterName;
   }

   @Override
   protected void selectMatchingRecipes(RecipeCollection var1, StackedItemContents var2, RecipeBook var3) {
      var1.selectMatchingRecipes(var2, 1, 1, var3);
   }
}
