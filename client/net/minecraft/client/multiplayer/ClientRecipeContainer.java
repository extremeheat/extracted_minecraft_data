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

   public ClientRecipeContainer(Map<ResourceKey<RecipePropertySet>, RecipePropertySet> var1, SelectableRecipe.SingleInputSet<StonecutterRecipe> var2) {
      super();
      this.itemSets = var1;
      this.stonecutterRecipes = var2;
   }

   public RecipePropertySet propertySet(ResourceKey<RecipePropertySet> var1) {
      return (RecipePropertySet)this.itemSets.getOrDefault(var1, RecipePropertySet.EMPTY);
   }

   public SelectableRecipe.SingleInputSet<StonecutterRecipe> stonecutterRecipes() {
      return this.stonecutterRecipes;
   }
}
