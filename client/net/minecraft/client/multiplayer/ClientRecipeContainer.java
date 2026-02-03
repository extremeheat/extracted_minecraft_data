package net.minecraft.client.multiplayer;

import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class ClientRecipeContainer implements RecipeAccess {
   private final Map<ResourceKey<RecipePropertySet>, RecipePropertySet> itemSets;
   private final SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes;

   public ClientRecipeContainer(final Map<ResourceKey<RecipePropertySet>, RecipePropertySet> itemSets, final SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes) {
      super();
      this.itemSets = itemSets;
      this.stonecutterRecipes = stonecutterRecipes;
   }

   public RecipePropertySet propertySet(final ResourceKey<RecipePropertySet> id) {
      return (RecipePropertySet)this.itemSets.getOrDefault(id, RecipePropertySet.EMPTY);
   }

   public SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes() {
      return this.stonecutterRecipes;
   }
}
