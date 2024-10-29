package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public interface RecipeOutput {
   void accept(ResourceKey<Recipe<?>> var1, Recipe<?> var2, @Nullable AdvancementHolder var3);

   Advancement.Builder advancement();

   void includeRootAdvancement();
}
