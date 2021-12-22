package net.minecraft.data.recipes;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface RecipeBuilder {
   RecipeBuilder unlockedBy(String var1, CriterionTriggerInstance var2);

   RecipeBuilder group(@Nullable String var1);

   Item getResult();

   void save(Consumer<FinishedRecipe> var1, ResourceLocation var2);

   default void save(Consumer<FinishedRecipe> var1) {
      this.save(var1, getDefaultRecipeId(this.getResult()));
   }

   default void save(Consumer<FinishedRecipe> var1, String var2) {
      ResourceLocation var3 = getDefaultRecipeId(this.getResult());
      ResourceLocation var4 = new ResourceLocation(var2);
      if (var4.equals(var3)) {
         throw new IllegalStateException("Recipe " + var2 + " should remove its 'save' argument as it is equal to default one");
      } else {
         this.save(var1, var4);
      }
   }

   static ResourceLocation getDefaultRecipeId(ItemLike var0) {
      return Registry.ITEM.getKey(var0.asItem());
   }
}
