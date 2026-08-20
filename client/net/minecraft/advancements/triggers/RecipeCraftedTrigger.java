package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RecipeCraftedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public RecipeCraftedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return RecipeCraftedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final ResourceKey<Recipe<?>> id, final List<ItemStack> usedIngredients) {
      this.trigger(player, (t) -> t.matches(id, usedIngredients));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, HolderSet<Recipe<?>> recipes, List<ItemPredicate> ingredients) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), Recipe.LIST_CODEC.fieldOf("recipes").forGetter(TriggerInstance::recipes), ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(TriggerInstance::ingredients)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> craftedItem(final HolderSet<Recipe<?>> recipeId, final List<ItemPredicate.Builder> predicates) {
         return CriteriaTriggers.RECIPE_CRAFTED.createCriterion(new TriggerInstance(Optional.empty(), recipeId, predicates.stream().map(ItemPredicate.Builder::build).toList()));
      }

      public static Criterion<TriggerInstance> craftedItem(final HolderSet<Recipe<?>> recipeId) {
         return CriteriaTriggers.RECIPE_CRAFTED.createCriterion(new TriggerInstance(Optional.empty(), recipeId, List.of()));
      }

      public static Criterion<TriggerInstance> crafterCraftedItem(final HolderSet<Recipe<?>> recipeId) {
         return CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.createCriterion(new TriggerInstance(Optional.empty(), recipeId, List.of()));
      }

      private boolean matches(final ResourceKey<Recipe<?>> id, final List<ItemStack> usedIngredients) {
         if (this.recipes.stream().noneMatch((holder) -> holder.is(id))) {
            return false;
         } else {
            List<ItemStack> remaining = new ArrayList(usedIngredients);

            for(ItemPredicate predicate : this.ingredients) {
               boolean found = false;
               Iterator<ItemStack> iterator = remaining.iterator();

               while(iterator.hasNext()) {
                  if (predicate.test((ItemInstance)iterator.next())) {
                     iterator.remove();
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
