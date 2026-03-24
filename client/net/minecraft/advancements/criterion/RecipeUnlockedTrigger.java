package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

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

   public static Criterion<TriggerInstance> unlocked(final ResourceKey<Recipe<?>> recipe) {
      return CriteriaTriggers.RECIPE_UNLOCKED.createCriterion(new TriggerInstance(Optional.empty(), recipe));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, ResourceKey<Recipe<?>> recipe) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), Recipe.KEY_CODEC.fieldOf("recipe").forGetter(TriggerInstance::recipe)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public boolean matches(final RecipeHolder<?> recipe) {
         return this.recipe == recipe.id();
      }
   }
}
