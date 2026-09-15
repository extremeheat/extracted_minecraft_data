package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public RecipeUnlockedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return RecipeUnlockedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final RecipeHolder<?> recipe) {
      this.trigger(player, (t) -> t.matches(recipe));
   }

   public static Criterion<TriggerInstance> unlocked(final Holder<Recipe<?>> recipe) {
      return unlocked(HolderSet.direct(recipe));
   }

   public static Criterion<TriggerInstance> unlocked(final HolderSet<Recipe<?>> recipe) {
      return CriteriaTriggers.RECIPE_UNLOCKED.createCriterion(new TriggerInstance(Optional.empty(), recipe));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, HolderSet<Recipe<?>> recipes) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), Recipe.LIST_CODEC.fieldOf("recipes").forGetter(TriggerInstance::recipes)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public boolean matches(final RecipeHolder<?> recipe) {
         return this.recipes.stream().anyMatch((holder) -> holder.is(recipe.id()));
      }
   }
}
