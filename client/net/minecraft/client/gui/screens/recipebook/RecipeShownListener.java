package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.world.item.crafting.Recipe;

public interface RecipeShownListener {
   void recipesShown(List<Recipe<?>> var1);
}
