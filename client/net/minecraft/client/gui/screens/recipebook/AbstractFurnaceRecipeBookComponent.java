package net.minecraft.client.gui.screens.recipebook;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public abstract class AbstractFurnaceRecipeBookComponent extends RecipeBookComponent {
   private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled"), ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled"), ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"), ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted"));
   @Nullable
   private Ingredient fuels;

   public AbstractFurnaceRecipeBookComponent() {
      super();
   }

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_SPRITES);
   }

   public void slotClicked(@Nullable Slot var1) {
      super.slotClicked(var1);
      if (var1 != null && var1.index < this.menu.getSize()) {
         this.ghostRecipe.clear();
      }

   }

   public void setupGhostRecipe(RecipeHolder<?> var1, List<Slot> var2) {
      ItemStack var3 = var1.value().getResultItem(this.minecraft.level.registryAccess());
      this.ghostRecipe.setRecipe(var1);
      this.ghostRecipe.addIngredient(Ingredient.of(var3), ((Slot)var2.get(2)).x, ((Slot)var2.get(2)).y);
      NonNullList var4 = var1.value().getIngredients();
      Slot var5 = (Slot)var2.get(1);
      if (var5.getItem().isEmpty()) {
         if (this.fuels == null) {
            this.fuels = Ingredient.of(this.getFuelItems().stream().filter((var1x) -> {
               return var1x.isEnabled(this.minecraft.level.enabledFeatures());
            }).map(ItemStack::new));
         }

         this.ghostRecipe.addIngredient(this.fuels, var5.x, var5.y);
      }

      Iterator var6 = var4.iterator();

      for(int var7 = 0; var7 < 2; ++var7) {
         if (!var6.hasNext()) {
            return;
         }

         Ingredient var8 = (Ingredient)var6.next();
         if (!var8.isEmpty()) {
            Slot var9 = (Slot)var2.get(var7);
            this.ghostRecipe.addIngredient(var8, var9.x, var9.y);
         }
      }

   }

   protected abstract Set<Item> getFuelItems();
}
