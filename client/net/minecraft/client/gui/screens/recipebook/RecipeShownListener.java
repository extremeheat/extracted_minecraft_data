package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface RecipeShownListener {
   void recipesShown(List<RecipeHolder<?>> var1);
}
