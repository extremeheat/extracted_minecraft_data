package net.minecraft.data.recipes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUnlockAdvancementBuilder {
   private final Map<String, Criterion<?>> criteria = new LinkedHashMap();

   public RecipeUnlockAdvancementBuilder() {
      super();
   }

   public void unlockedBy(final String name, final Criterion<?> criterion) {
      this.criteria.put(name, criterion);
   }

   public AdvancementHolder build(final RecipeOutput output, final ResourceKey<Recipe<?>> id, final RecipeCategory category) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(id.identifier()));
      } else {
         Advancement.Builder advancement = output.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
         Map var10000 = this.criteria;
         Objects.requireNonNull(advancement);
         var10000.forEach(advancement::addCriterion);
         return advancement.build(id.identifier().withPrefix("recipes/" + category.getFolderName() + "/"));
      }
   }
}
